
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class WebServer {
    private HttpServer server;
    private CampusMap map;
    private DispatchSystem dispatchSystem;
    private OrderSystem orderSystem;

    public WebServer(CampusMap map, DispatchSystem dispatchSystem, OrderSystem orderSystem) {
        this.map = map;
        this.dispatchSystem = dispatchSystem;
        this.orderSystem = orderSystem;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve HTML files - use specific handlers for each path
        server.createContext("/dashboard.html", new DashboardHandler());
        server.createContext("/admin.html", new AdminHandler());
        server.createContext("/", new DashboardHandler()); // Default to dashboard

        // API endpoints
        server.createContext("/api/state", new ApiHandler());
        server.createContext("/api/orders/add", new OrderAddHandler());
        server.createContext("/api/orders/cancel", new OrderCancelHandler());
        server.createContext("/api/orders/search", new OrderSearchHandler());
        server.createContext("/api/riders/add", new RiderAddHandler());
        server.createContext("/api/riders/search", new RiderSearchHandler());
        server.createContext("/api/dispatch/assign", new DispatchAssignHandler());
        server.createContext("/api/dispatch/complete", new DispatchCompleteHandler());
        server.createContext("/api/dispatch/undo", new DispatchUndoHandler());
        server.createContext("/api/dispatch/redo", new DispatchRedoHandler());
        server.createContext("/api/dispatch/status", new DispatchStatusHandler());
        server.createContext("/api/map/location/add", new MapLocationAddHandler());
        server.createContext("/api/map/route/add", new MapRouteAddHandler());
        server.createContext("/api/map/path", new MapPathHandler());
        server.createContext("/api/map/cache", new MapCacheHandler());
        server.createContext("/api/map/cache/clear", new MapCacheClearHandler());
        server.createContext("/api/data/save", new DataSaveHandler());
        server.createContext("/api/data/load", new DataLoadHandler());
        server.createContext("/api/data/save/riders", new DataSaveRidersHandler());
        server.createContext("/api/data/save/orders", new DataSaveOrdersHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            serveHtmlFile(t, "dashboard.html");
        }
    }

    private class AdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            serveHtmlFile(t, "admin.html");
        }
    }

    private void serveHtmlFile(HttpExchange t, String filename) throws IOException {
        String response = "";
        
        try {
            java.nio.file.Path htmlPath = Paths.get("src", filename);
            if (!Files.exists(htmlPath)) {
                htmlPath = Paths.get(filename);
            }
            if (!Files.exists(htmlPath)) {
                htmlPath = Paths.get(System.getProperty("user.dir"), "src", filename);
            }
            
            if (!Files.exists(htmlPath)) {
                response = "<h1>404 Not Found</h1><p>File " + filename + " not found.</p>";
                t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(404, responseBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(responseBytes);
                os.close();
                return;
            }
            
            byte[] bytes = Files.readAllBytes(htmlPath);
            response = new String(bytes, StandardCharsets.UTF_8);
            
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        } catch (Exception e) {
            response = "<h1>Error: could not load " + filename + "</h1><p>" + e.getMessage() + "</p>";
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        }

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = t.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
            // Set JSON Content Type
                t.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            String jsonResponse = buildJsonState();

            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
            } catch (Exception e) {
                String errorResponse = "{\"error\": \"" + escape(e.getMessage()) + "\"}";
                byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                t.sendResponseHeaders(500, errorBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(errorBytes);
                os.close();
            }
        }
    }

    /**
     * Manually build JSON string to avoid external libraries
     */
    private String buildJsonState() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Locations (Nodes)
        json.append("\"locations\": [");
        Graph graph = map.getGraph();
        int[] count = new int[1];
        String[] locations = graph.getAllLocations(count);

        // We need X,Y coordinates for visualization.
        // Since the original data doesn't have coordinates, we will synthesize them or
        // just send raw nodes.
        // For a cooler visualization, the frontend can auto-layout, or we can hash the
        // name to get a consistent position.

        for (int i = 0; i < count[0]; i++) {
            json.append(String.format("{\"name\": \"%s\"}", escape(locations[i])));
            if (i < count[0] - 1)
                json.append(",");
        }
        json.append("],");

        // Routes (Edges)
        json.append("\"routes\": [");
        boolean firstEdge = true;
        for (int i = 0; i < count[0]; i++) {
            String from = locations[i];
            Edge[] edges = graph.getEdges(from);
            if (edges != null) {
                for (Edge e : edges) {
                    if (e == null)
                        continue;
                    if (!firstEdge)
                        json.append(",");
                    json.append(String.format("{\"from\": \"%s\", \"to\": \"%s\", \"weight\": %d}",
                            escape(from), escape(e.getToNode()), e.getWeight()));
                    firstEdge = false;
                }
            }
        }
        json.append("],");

        // Riders
        json.append("\"riders\": [");
        Rider[] riders = dispatchSystem.getAllRiders();
        for (int i = 0; i < riders.length; i++) {
            if (riders[i] == null)
                continue;
            Rider r = riders[i];
            json.append(String.format(
                    "{\"id\": \"%s\", \"name\": \"%s\", \"location\": \"%s\", \"status\": \"%s\"}",
                    escape(r.getId()), escape(r.getName()), escape(r.getLocation()), escape(r.getStatus().toString())));
            if (i < riders.length - 1 && riders[i + 1] != null)
                json.append(",");
        }
        json.append("],");

        // Orders
        json.append("\"orders\": [");
        Order[] orders = orderSystem.getAllOrders();
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == null)
                continue;
            Order o = orders[i];
            json.append(String.format(
                    "{\"id\": \"%s\", \"studentName\": \"%s\", \"pickup\": \"%s\", \"delivery\": \"%s\", \"priority\": %d, \"status\": \"%s\"}",
                    escape(o.getId()),
                    escape(o.getStudentName()),
                    escape(o.getPickupLocation()),
                    escape(o.getDeliveryLocation()),
                    o.getPriority(),
                    escape(o.getStatus())));
            if (i < orders.length - 1 && orders[i + 1] != null)
                json.append(",");
        }
        json.append("]");

        // Stats
        json.append(",\"stats\": {");
        SystemStatistics stats = SystemStatistics.getInstance();

        json.append(String.format("\"uptime\": %d,", stats.getUptimeSeconds()));
        json.append(String.format("\"totalOrders\": %d,", stats.getTotalOrdersProcessed()));
        json.append(String.format("\"pendingOrders\": %d,", orderSystem.getPendingCount()));
        json.append(String.format("\"totalDistance\": %.1f,", stats.getTotalDistanceTraveled()));
        json.append(String.format("\"avgDistance\": %.1f,", stats.getAverageDistance()));

        json.append(String.format("\"activeRiders\": %d,", dispatchSystem.getAvailableRiderCount()));
        json.append(String.format("\"dijkstraCalls\": %d,", graph.getDijkstraCallCount()));
        json.append(String.format("\"cacheHits\": %d,", graph.getCacheHits()));
        json.append(String.format("\"cacheMisses\": %d,", graph.getCacheMisses()));
        json.append(String.format("\"cacheHitRate\": %.1f", graph.getCacheHitRate()));

        json.append("}");

        json.append("}");
        return json.toString();
    }

    private String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String readRequestBody(HttpExchange t) throws IOException {
        InputStream is = t.getRequestBody();
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private void sendJsonResponse(HttpExchange t, String json) throws IOException {
        t.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(200, bytes.length);
        OutputStream os = t.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void sendErrorResponse(HttpExchange t, String error, int code) throws IOException {
        String json = "{\"success\": false, \"error\": \"" + escape(error) + "\"}";
        t.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(code, bytes.length);
        OutputStream os = t.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // Order Handlers
    private class OrderAddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                // Simple JSON parsing (assuming format: {"id":"...","studentName":"...","pickup":"...","delivery":"...","priority":1})
                String id = extractJsonValue(body, "id");
                String studentName = extractJsonValue(body, "studentName");
                String pickup = extractJsonValue(body, "pickup");
                String delivery = extractJsonValue(body, "delivery");
                int priority = Integer.parseInt(extractJsonValue(body, "priority"));

                Order order = new Order(id, studentName, pickup, delivery, priority);
                orderSystem.addOrder(order);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class OrderCancelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                String orderId = extractJsonValue(body, "orderId");
                
                Order order = orderSystem.searchOrder(orderId);
                if (order == null) {
                    sendErrorResponse(t, "Order not found", 404);
                    return;
                }

                if (order.getStatus().equals("Delivering")) {
                    Rider[] riders = dispatchSystem.getAllRiders();
                    for (Rider rider : riders) {
                        if (rider != null && rider.getCurrentOrderId() != null && rider.getCurrentOrderId().equals(orderId)) {
                            rider.setStatus(Rider.RiderStatus.AVAILABLE);
                            rider.setCurrentOrderId(null);
                            break;
                        }
                    }
                }

                Order cancelled = orderSystem.cancelOrder(orderId);
                if (cancelled != null) {
                    sendJsonResponse(t, "{\"success\": true}");
                } else {
                    sendErrorResponse(t, "Failed to cancel order", 500);
                }
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class OrderSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                URI uri = t.getRequestURI();
                String query = uri.getQuery();
                String q = "";
                if (query != null && query.startsWith("q=")) {
                    q = java.net.URLDecoder.decode(query.substring(2), StandardCharsets.UTF_8).toLowerCase();
                }

                StringBuilder json = new StringBuilder();
                json.append("{\"orders\": [");
                
                Order[] allOrders = orderSystem.getAllOrders();
                boolean first = true;
                for (Order o : allOrders) {
                    if (o == null) continue;
                    if (q.isEmpty() || o.getId().toLowerCase().contains(q) || o.getStudentName().toLowerCase().contains(q)) {
                        if (!first) json.append(",");
                        json.append(String.format("{\"id\": \"%s\", \"studentName\": \"%s\", \"pickup\": \"%s\", \"delivery\": \"%s\", \"priority\": %d, \"status\": \"%s\"}",
                                escape(o.getId()), escape(o.getStudentName()), escape(o.getPickupLocation()),
                                escape(o.getDeliveryLocation()), o.getPriority(), escape(o.getStatus())));
                        first = false;
                    }
                }
                json.append("]}");
                sendJsonResponse(t, json.toString());
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    // Rider Handlers
    private class RiderAddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                String id = extractJsonValue(body, "id");
                String name = extractJsonValue(body, "name");
                String location = extractJsonValue(body, "location");

                Rider rider = new Rider(id, name, location);
                dispatchSystem.addRider(rider);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class RiderSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                URI uri = t.getRequestURI();
                String query = uri.getQuery();
                String q = "";
                if (query != null && query.startsWith("q=")) {
                    q = java.net.URLDecoder.decode(query.substring(2), StandardCharsets.UTF_8).toLowerCase();
                }

                StringBuilder json = new StringBuilder();
                json.append("{\"riders\": [");
                
                Rider[] allRiders = dispatchSystem.getAllRiders();
                boolean first = true;
                for (Rider r : allRiders) {
                    if (r == null) continue;
                    if (q.isEmpty() || r.getId().toLowerCase().contains(q) || r.getName().toLowerCase().contains(q)) {
                        if (!first) json.append(",");
                        json.append(String.format("{\"id\": \"%s\", \"name\": \"%s\", \"location\": \"%s\", \"status\": \"%s\"}",
                                escape(r.getId()), escape(r.getName()), escape(r.getLocation()), escape(r.getStatus().toString())));
                        first = false;
                    }
                }
                json.append("]}");
                sendJsonResponse(t, json.toString());
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    // Dispatch Handlers
    private class DispatchAssignHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                dispatchSystem.assignOrder(orderSystem, map);
                sendJsonResponse(t, "{\"success\": true, \"message\": \"Order assigned successfully\"}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DispatchCompleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                String orderId = extractJsonValue(body, "orderId");
                dispatchSystem.completeOrder(orderId, orderSystem, map);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DispatchUndoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                dispatchSystem.undoLastDispatch(orderSystem);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DispatchRedoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                dispatchSystem.redoLastDispatch(orderSystem, map);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DispatchStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append(String.format("\"totalDispatched\": %d,", dispatchSystem.getTotalOrdersDispatched()));
                json.append(String.format("\"totalDistance\": %.1f,", SystemStatistics.getInstance().getTotalDistanceTraveled()));
                json.append(String.format("\"availableRiders\": %d", dispatchSystem.getAvailableRiderCount()));
                json.append("}");
                sendJsonResponse(t, json.toString());
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    // Map Handlers
    private class MapLocationAddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                String name = extractJsonValue(body, "name");
                String faculty = extractJsonValue(body, "faculty");
                String block = extractJsonValue(body, "block");

                if (map.getGraph().hasLocation(name)) {
                    sendErrorResponse(t, "Location already exists", 400);
                    return;
                }

                map.addLocation(name, faculty, block);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class MapRouteAddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                String body = readRequestBody(t);
                String from = extractJsonValue(body, "from");
                String to = extractJsonValue(body, "to");
                int distance = Integer.parseInt(extractJsonValue(body, "distance"));

                if (!map.getGraph().hasLocation(from)) {
                    sendErrorResponse(t, "From location does not exist", 400);
                    return;
                }
                if (!map.getGraph().hasLocation(to)) {
                    sendErrorResponse(t, "To location does not exist", 400);
                    return;
                }

                map.addRoute(from, to, distance);
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class MapPathHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                URI uri = t.getRequestURI();
                String query = uri.getQuery();
                String from = "";
                String to = "";
                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("from=")) {
                            from = java.net.URLDecoder.decode(param.substring(5), StandardCharsets.UTF_8);
                        } else if (param.startsWith("to=")) {
                            to = java.net.URLDecoder.decode(param.substring(3), StandardCharsets.UTF_8);
                        }
                    }
                }

                int[] length = new int[1];
                String[] path = map.getShortestPath(from, to, length);
                double distance = map.getDistance(from, to);

                if (length[0] == 0 || distance < 0) {
                    sendErrorResponse(t, "No path found", 404);
                    return;
                }

                StringBuilder json = new StringBuilder();
                json.append("{\"success\": true, \"path\": [");
                for (int i = 0; i < length[0]; i++) {
                    if (i > 0) json.append(",");
                    json.append("\"").append(escape(path[i])).append("\"");
                }
                json.append("], \"distance\": ").append(distance).append("}");
                sendJsonResponse(t, json.toString());
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class MapCacheHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                Graph graph = map.getGraph();
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append(String.format("\"cacheSize\": %d,", graph.getCacheSize()));
                json.append(String.format("\"cacheHits\": %d,", graph.getCacheHits()));
                json.append(String.format("\"cacheMisses\": %d,", graph.getCacheMisses()));
                json.append(String.format("\"hitRate\": %.1f,", graph.getCacheHitRate()));
                json.append(String.format("\"dijkstraCalls\": %d", graph.getDijkstraCallCount()));
                json.append("}");
                sendJsonResponse(t, json.toString());
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class MapCacheClearHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                map.getGraph().clearCache();
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    // Data Handlers
    private class DataSaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                dispatchSystem.saveRiders("data/riders_backup.txt");
                orderSystem.saveOrders("data/orders_backup.txt");
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DataLoadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                map.loadLocations("data/locations.txt");
                map.loadRoutes("data/routes.txt");
                dispatchSystem.loadRiders("data/riders.txt");
                orderSystem.loadOrders("data/orders.txt");
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DataSaveRidersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                dispatchSystem.saveRiders("data/riders_backup.txt");
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    private class DataSaveOrdersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST")) {
                sendErrorResponse(t, "Method not allowed", 405);
                return;
            }
            try {
                orderSystem.saveOrders("data/orders_backup.txt");
                sendJsonResponse(t, "{\"success\": true}");
            } catch (Exception e) {
                sendErrorResponse(t, e.getMessage(), 500);
            }
        }
    }

    // Helper method to extract JSON values (simple parser)
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return "";
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return "";
        
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\t')) {
            startIndex++;
        }
        
        if (startIndex >= json.length()) return "";
        
        if (json.charAt(startIndex) == '"') {
            // String value
            startIndex++;
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex == -1) return "";
            return json.substring(startIndex, endIndex);
        } else {
            // Number value
            int endIndex = startIndex;
            while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.' || json.charAt(endIndex) == '-')) {
                endIndex++;
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
}
