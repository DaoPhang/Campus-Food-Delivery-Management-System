import java.io.*;

/**
 * Graph class implementing campus map with Dijkstra's shortest path algorithm
 * Uses manual array-based implementations instead of Java Collections
 * UPGRADE 4: Path caching optimization
 * UPGRADE 2: File persistence for locations and routes
 * UPGRADE 5: Statistics tracking for Dijkstra calls
 */
public class Graph {
    // Constants
    private static final int MAX_NODES = 500;  // Increased for safety
    private static final int MAX_EDGES = 20;   // Max edges per node
    private static final int MAX_CACHE = 1000; // Max cached paths

    // Manual adjacency list: Edge[nodeIndex][edgeIndex]
    private Edge[][] adjList;
    private int[] edgeCount;

    // Node name to index mapping
    private String[] nodeNames;
    private Location[] nodeLocations;
    private int nodeCount;

    // UPGRADE 4: Path caching (manual array-based)
    private String[] cacheKeys;
    private PathResult[] cacheValues;
    private int cacheCount;
    private int cacheHits;
    private int cacheMisses;

    // UPGRADE 5: Statistics tracking
    private int dijkstraCallCount;

    public Graph() {
        this.adjList = new Edge[MAX_NODES][MAX_EDGES];
        this.edgeCount = new int[MAX_NODES];
        this.nodeNames = new String[MAX_NODES];
        this.nodeLocations = new Location[MAX_NODES];
        this.nodeCount = 0;

        this.cacheKeys = new String[MAX_CACHE];
        this.cacheValues = new PathResult[MAX_CACHE];
        this.cacheCount = 0;
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.dijkstraCallCount = 0;
    }

    /**
     * Get node index by name, returns -1 if not found
     */
    private int getNodeIndex(String name) {
        for (int i = 0; i < nodeCount; i++) {
            if (nodeNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Add a location to the graph
     */
    public void addLocation(String name, Location loc) {
        if (getNodeIndex(name) == -1 && nodeCount < MAX_NODES) {
            nodeNames[nodeCount] = name;
            nodeLocations[nodeCount] = loc;
            edgeCount[nodeCount] = 0;
            nodeCount++;
            clearCache();
        }
    }

    /**
     * Add an edge between two nodes (by name)
     */
    public void addEdge(String from, String to, int distance) {
        if (distance <= 0) {
            System.out.println("Distance must be a positive integer.");
            return;
        }

        int fromIdx = getNodeIndex(from);
        int toIdx = getNodeIndex(to);

        if (fromIdx == -1 || toIdx == -1) {
            throw new IllegalArgumentException("Both locations must exist in the graph.");
        }

        // Add edge from -> to
        if (edgeCount[fromIdx] < MAX_EDGES) {
            adjList[fromIdx][edgeCount[fromIdx]++] = new Edge(to, distance);
        }

        // Add edge to -> from (undirected graph)
        if (edgeCount[toIdx] < MAX_EDGES) {
            adjList[toIdx][edgeCount[toIdx]++] = new Edge(from, distance);
        }

        clearCache();
    }

    /**
     * Display the graph
     */
    public void displayGraph() {
        for (int i = 0; i < nodeCount; i++) {
            Location loc = nodeLocations[i];
            String details = (loc != null) ? " (" + loc.getFacultyOrDorm() + " - " + loc.getBlock() + ")" : "";
            System.out.print(nodeNames[i] + details + " -> ");

            for (int j = 0; j < edgeCount[i]; j++) {
                Edge edge = adjList[i][j];
                System.out.print("[" + edge.getToNode() + ", " + edge.getWeight() + "] ");
            }
            System.out.println();
        }
    }

    /**
     * Get shortest path with caching optimization
     */
    public String[] getShortestPath(String start, String end, int[] pathLengthOut) {
        int startIdx = getNodeIndex(start);
        int endIdx = getNodeIndex(end);

        if (startIdx == -1 || endIdx == -1) {
            if (pathLengthOut != null && pathLengthOut.length > 0) pathLengthOut[0] = 0;
            return new String[0];
        }

        if (start.equals(end)) {
            if (pathLengthOut != null && pathLengthOut.length > 0) pathLengthOut[0] = 1;
            String[] result = new String[1];
            result[0] = start;
            return result;
        }

        // Check cache first
        String cacheKey = getCacheKey(start, end);
        PathResult cached = getCachedPath(cacheKey);
        if (cached != null) {
            cacheHits++;
            if (pathLengthOut != null && pathLengthOut.length > 0) {
                pathLengthOut[0] = cached.getPathSize();
            }
            // Return copy of path
            String[] result = new String[cached.getPathSize()];
            for (int i = 0; i < cached.getPathSize(); i++) {
                result[i] = nodeNames[cached.getNode(i)];
            }
            return result;
        }

        // Cache miss - run Dijkstra
        cacheMisses++;
        dijkstraCallCount++;

        PathResult pathResult = runDijkstra(startIdx, endIdx);
        putCache(cacheKey, pathResult);

        if (pathLengthOut != null && pathLengthOut.length > 0) {
            pathLengthOut[0] = pathResult.getPathSize();
        }

        // Convert node indices to names
        String[] result = new String[pathResult.getPathSize()];
        for (int i = 0; i < pathResult.getPathSize(); i++) {
            result[i] = nodeNames[pathResult.getNode(i)];
        }
        return result;
    }

    /**
     * Get distance between two locations (with caching)
     */
    public int getPathDistance(String start, String end) {
        int startIdx = getNodeIndex(start);
        int endIdx = getNodeIndex(end);

        if (startIdx == -1 || endIdx == -1) {
            return -1;
        }

        if (start.equals(end)) {
            return 0;
        }

        // Check cache
        String cacheKey = getCacheKey(start, end);
        PathResult cached = getCachedPath(cacheKey);
        if (cached != null) {
            return cached.getTotalDistance();
        }

        // Run Dijkstra
        int[] pathLen = new int[1];
        getShortestPath(start, end, pathLen);

        // Get from cache now
        cached = getCachedPath(cacheKey);
        return cached != null ? cached.getTotalDistance() : -1;
    }

    /**
     * Internal Dijkstra implementation using manual arrays
     */
    private PathResult runDijkstra(int startIdx, int endIdx) {
        // Distance array
        int[] distances = new int[nodeCount];
        int[] previousNodes = new int[nodeCount];
        boolean[] visited = new boolean[nodeCount];

        // Initialize
        for (int i = 0; i < nodeCount; i++) {
            distances[i] = Integer.MAX_VALUE;
            previousNodes[i] = -1;
            visited[i] = false;
        }
        distances[startIdx] = 0;

        // Manual priority queue (simple array-based min-heap simulation)
        int[] pqNodes = new int[MAX_NODES * MAX_EDGES];
        int[] pqDist = new int[MAX_NODES * MAX_EDGES];
        int pqSize = 0;

        // Add start node
        pqNodes[pqSize] = startIdx;
        pqDist[pqSize] = 0;
        pqSize++;

        while (pqSize > 0) {
            // Find minimum distance node (linear search - simple approach)
            int minIdx = 0;
            for (int i = 1; i < pqSize; i++) {
                if (pqDist[i] < pqDist[minIdx]) {
                    minIdx = i;
                }
            }

            int currentNode = pqNodes[minIdx];
            int currentDist = pqDist[minIdx];

            // Remove from queue (swap with last element)
            pqNodes[minIdx] = pqNodes[pqSize - 1];
            pqDist[minIdx] = pqDist[pqSize - 1];
            pqSize--;

            if (visited[currentNode]) {
                continue;
            }
            visited[currentNode] = true;

            if (currentNode == endIdx) {
                break;
            }

            // Process all edges from current node
            for (int i = 0; i < edgeCount[currentNode]; i++) {
                Edge edge = adjList[currentNode][i];
                int neighborIdx = getNodeIndex(edge.getToNode());
                if (neighborIdx == -1 || visited[neighborIdx]) continue;

                int newDist = currentDist + edge.getWeight();

                if (newDist < distances[neighborIdx]) {
                    distances[neighborIdx] = newDist;
                    previousNodes[neighborIdx] = currentNode;

                    // Add to priority queue
                    if (pqSize < pqNodes.length) {
                        pqNodes[pqSize] = neighborIdx;
                        pqDist[pqSize] = newDist;
                        pqSize++;
                    }
                }
            }
        }

        // Reconstruct path using manual stack
        PathResult result = new PathResult(MAX_NODES);

        if (distances[endIdx] == Integer.MAX_VALUE) {
            result.setTotalDistance(-1);
            return result;
        }

        // Use stack to reverse the path
        int[] stack = new int[MAX_NODES];
        int top = -1;

        int step = endIdx;
        while (step != -1) {
            stack[++top] = step;
            step = previousNodes[step];
        }

        // Pop from stack to get correct order
        while (top >= 0) {
            result.addNode(stack[top--]);
        }

        result.setTotalDistance(distances[endIdx]);
        return result;
    }

    /**
     * Get delivery route: rider -> pickup -> delivery
     */
    public String[] getDeliveryRoute(String riderLoc, String restaurant, String customerLoc, int[] pathLengthOut) {
        int[] firstLegLen = new int[1];
        int[] secondLegLen = new int[1];

        String[] firstLeg = getShortestPath(riderLoc, restaurant, firstLegLen);
        String[] secondLeg = getShortestPath(restaurant, customerLoc, secondLegLen);

        if (firstLegLen[0] == 0 || secondLegLen[0] == 0) {
            if (pathLengthOut != null && pathLengthOut.length > 0) pathLengthOut[0] = 0;
            return new String[0];
        }

        // Combine paths (excluding duplicate restaurant node)
        int totalLen = firstLegLen[0] + secondLegLen[0] - 1;
        String[] fullPath = new String[totalLen];

        int idx = 0;
        for (int i = 0; i < firstLegLen[0]; i++) {
            fullPath[idx++] = firstLeg[i];
        }
        for (int i = 1; i < secondLegLen[0]; i++) {
            fullPath[idx++] = secondLeg[i];
        }

        if (pathLengthOut != null && pathLengthOut.length > 0) {
            pathLengthOut[0] = totalLen;
        }
        return fullPath;
    }

    // ==========================================
    // UPGRADE 2: File Persistence Methods
    // ==========================================

    public void loadLocations(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

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

    public void loadRoutes(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    int distance = Integer.parseInt(parts[2].trim());

                    // Ensure locations exist before adding edge
                    if (getNodeIndex(from) == -1) {
                        addLocation(from, new Location("Campus", from));
                    }
                    if (getNodeIndex(to) == -1) {
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
    // UPGRADE 4: Cache Methods (Manual Array-Based)
    // ==========================================

    private String getCacheKey(String from, String to) {
        return from + "|" + to;
    }

    private PathResult getCachedPath(String key) {
        for (int i = 0; i < cacheCount; i++) {
            if (cacheKeys[i].equals(key)) {
                return cacheValues[i];
            }
        }
        return null;
    }

    private void putCache(String key, PathResult value) {
        // Check if key exists
        for (int i = 0; i < cacheCount; i++) {
            if (cacheKeys[i].equals(key)) {
                cacheValues[i] = value;
                return;
            }
        }
        // Add new entry if space available
        if (cacheCount < MAX_CACHE) {
            cacheKeys[cacheCount] = key;
            cacheValues[cacheCount] = value;
            cacheCount++;
        }
    }

    public void clearCache() {
        cacheCount = 0;
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
        return cacheCount;
    }

    public double getCacheHitRate() {
        int total = cacheHits + cacheMisses;
        if (total == 0) return 0.0;
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
     * Get all location names as array
     */
    public String[] getAllLocations(int[] countOut) {
        if (countOut != null && countOut.length > 0) {
            countOut[0] = nodeCount;
        }
        String[] result = new String[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            result[i] = nodeNames[i];
        }
        return result;
    }

    /**
     * Get node count
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Check if location exists
     */
    public boolean hasLocation(String name) {
        return getNodeIndex(name) != -1;
    }
}
