import java.util.*;
import java.io.*;

/**
 * Graph class implementing campus map with Dijkstra's shortest path algorithm
 * UPGRADE 4: Path caching with HashMap optimization
 * UPGRADE 2: File persistence for locations and routes
 * UPGRADE 5: Statistics tracking for Dijkstra calls
 */
public class Graph {
    private final Map<String, List<Edge>> adjacencyList;
    private final Map<String, Location> locationInfoMap;

    // UPGRADE 4: Path caching
    private final Map<String, PathResult> pathCache;
    private int cacheHits;
    private int cacheMisses;

    // UPGRADE 5: Statistics tracking
    private int dijkstraCallCount;

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.locationInfoMap = new HashMap<>();
        this.pathCache = new HashMap<>();
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.dijkstraCallCount = 0;
    }

    public void addLocation(String name, Location loc) {
        adjacencyList.putIfAbsent(name, new ArrayList<>());
        locationInfoMap.put(name, loc);
        // Invalidate cache when graph structure changes
        clearCache();
    }

    public void addEdge(String from, String to, int distance) {
        if (distance <= 0) {
            System.out.println("Distance must be a positive integer.");
            return;
        }
        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            throw new IllegalArgumentException("Both locations must exist in the graph.");
        }
        adjacencyList.get(from).add(new Edge(to, distance));
        adjacencyList.get(to).add(new Edge(from, distance));
        // Invalidate cache when graph structure changes
        clearCache();
    }

    public void displayGraph() {
        for (String name : adjacencyList.keySet()) {
            Location loc = locationInfoMap.get(name);
            String details = (loc != null) ? " (" + loc.getFacultyOrDorm() + " - " + loc.getBlock() + ")" : "";
            System.out.print(name + details + " -> ");

            List<Edge> edges = adjacencyList.get(name);
            for (Edge edge : edges) {
                System.out.print("[" + edge.getToNode() + ", " + edge.getWeight() + "] ");
            }
            System.out.println();
        }
    }

    /**
     * Get shortest path with caching optimization
     * UPGRADE 4: Uses HashMap cache to avoid repeated Dijkstra calculations
     */
    public List<String> getShortestPath(String start, String end) {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            return Collections.emptyList();
        }

        if (start.equals(end)) {
            return Collections.singletonList(start);
        }

        // UPGRADE 4: Check cache first
        String cacheKey = getCacheKey(start, end);
        if (pathCache.containsKey(cacheKey)) {
            cacheHits++;
            return pathCache.get(cacheKey).getPath();
        }

        // Cache miss - run Dijkstra
        cacheMisses++;
        dijkstraCallCount++;

        PathResult result = runDijkstra(start, end);
        pathCache.put(cacheKey, result);

        // Also cache the reverse path (for undirected graph)
        String reverseCacheKey = getCacheKey(end, start);
        if (!pathCache.containsKey(reverseCacheKey)) {
            List<String> reversePath = new ArrayList<>(result.getPath());
            Collections.reverse(reversePath);
            pathCache.put(reverseCacheKey, new PathResult(result.getDistance(), reversePath));
        }

        return result.getPath();
    }

    /**
     * Get distance between two locations (with caching)
     */
    public int getPathDistance(String start, String end) {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            return -1;
        }

        if (start.equals(end)) {
            return 0;
        }

        // getShortestPath handles caching - just call it and retrieve from cache
        getShortestPath(start, end);

        // Get from cache (guaranteed to exist after getShortestPath call)
        String cacheKey = getCacheKey(start, end);
        PathResult result = pathCache.get(cacheKey);
        return result != null ? result.getDistance() : -1;
    }

    /**
     * Internal Dijkstra implementation
     */
    private PathResult runDijkstra(String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();

        for (String location : adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(new NodeDistance(start, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentNode = current.node;

            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            if (currentNode.equals(end)) {
                break;
            }

            for (Edge edge : adjacencyList.get(currentNode)) {
                String neighbor = edge.getToNode();
                if (visited.contains(neighbor))
                    continue;

                int newDist = distances.get(currentNode) + edge.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newDist));
                }
            }
        }

        List<String> path = reconstructPath(previousNodes, start, end);
        int distance = distances.get(end);
        if (distance == Integer.MAX_VALUE) {
            distance = -1;
        }

        return new PathResult(distance, path);
    }

    private List<String> reconstructPath(Map<String, String> previousNodes, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        String step = end;

        if (previousNodes.get(step) == null && !step.equals(start)) {
            return path;
        }

        while (step != null) {
            path.addFirst(step);
            step = previousNodes.get(step);
        }
        return path;
    }

    public List<String> getDeliveryRoute(String riderLoc, String restaurant, String customerLoc) {
        List<String> firstLeg = getShortestPath(riderLoc, restaurant);
        List<String> secondLeg = getShortestPath(restaurant, customerLoc);

        if (firstLeg.isEmpty() || secondLeg.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> fullPath = new ArrayList<>(firstLeg);
        fullPath.addAll(secondLeg.subList(1, secondLeg.size()));
        return fullPath;
    }

    // ==========================================
    // UPGRADE 2: File Persistence Methods
    // ==========================================

    /**
     * Load locations from file
     * File format: one location name per line
     */
    public void loadLocations(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                // Check if line contains additional info (name,faculty,block)
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String faculty = parts.length > 1 ? parts[1].trim() : "Campus";
                String block = parts.length > 2 ? parts[2].trim() : name;

                addLocation(name, new Location(faculty, block));
                count++;
            }
            System.out.println("Loaded " + count + " locations from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Locations file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading locations file: " + e.getMessage());
        }
    }

    /**
     * Load routes from file
     * File format: from,to,distance
     */
    public void loadRoutes(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    int distance = Integer.parseInt(parts[2].trim());

                    // Ensure locations exist before adding edge
                    if (!adjacencyList.containsKey(from)) {
                        addLocation(from, new Location("Campus", from));
                    }
                    if (!adjacencyList.containsKey(to)) {
                        addLocation(to, new Location("Campus", to));
                    }

                    addEdge(from, to, distance);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " routes from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Routes file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading routes file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing distance in routes file: " + e.getMessage());
        }
    }

    // ==========================================
    // UPGRADE 4: Cache Statistics Methods
    // ==========================================

    /**
     * Helper method to create consistent cache keys
     */
    private String getCacheKey(String from, String to) {
        return from + "|" + to;
    }

    public void clearCache() {
        pathCache.clear();
        cacheHits = 0;
        cacheMisses = 0;
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public int getCacheMisses() {
        return cacheMisses;
    }

    public int getCacheSize() {
        return pathCache.size();
    }

    public double getCacheHitRate() {
        int total = cacheHits + cacheMisses;
        if (total == 0)
            return 0.0;
        return (double) cacheHits / total * 100;
    }

    // ==========================================
    // UPGRADE 5: Statistics Methods
    // ==========================================

    public int getDijkstraCallCount() {
        return dijkstraCallCount;
    }

    public void resetStatistics() {
        dijkstraCallCount = 0;
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * Get all location names
     */
    public Set<String> getAllLocations() {
        return adjacencyList.keySet();
    }

    /**
     * Check if location exists
     */
    public boolean hasLocation(String name) {
        return adjacencyList.containsKey(name);
    }
}
