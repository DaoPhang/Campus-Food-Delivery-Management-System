import java.util.List;

/**
 * CampusMap is a wrapper class for Graph that provides
 * simplified interface for the dispatch system
 */
public class CampusMap {
    private Graph graph;

    public CampusMap() {
        this.graph = new Graph();
    }

    public CampusMap(Graph graph) {
        this.graph = graph;
    }

    /**
     * Get the underlying graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Add a location to the map (delegates to 3-parameter version)
     */
    public void addLocation(String name) {
        addLocation(name, "Campus", name);
    }

    /**
     * Add a location with details
     */
    public void addLocation(String name, String facultyOrDorm, String block) {
        graph.addLocation(name, new Location(facultyOrDorm, block));
    }

    /**
     * Add a route between two locations
     */
    public void addRoute(String from, String to, int distance) {
        graph.addEdge(from, to, distance);
    }

    /**
     * Get distance between two locations
     * Returns -1 if no path exists
     */
    public double getDistance(String from, String to) {
        if (from == null || to == null)
            return -1;
        if (from.equals(to))
            return 0;

        // getPathDistance handles caching and returns -1 if no path exists
        return graph.getPathDistance(from, to);
    }

    /**
     * Get the shortest path between two locations
     */
    public List<String> getShortestPath(String from, String to) {
        return graph.getShortestPath(from, to);
    }

    /**
     * Get delivery route: rider → pickup → delivery
     */
    public List<String> getDeliveryRoute(String riderLoc, String pickup, String delivery) {
        return graph.getDeliveryRoute(riderLoc, pickup, delivery);
    }

    /**
     * Display the campus map
     */
    public void displayMap() {
        System.out.println("\n=== Campus Map ===");
        graph.displayGraph();
        System.out.println("==================\n");
    }

    /**
     * Load locations from file
     */
    public void loadLocations(String filename) {
        graph.loadLocations(filename);
    }

    /**
     * Load routes from file
     */
    public void loadRoutes(String filename) {
        graph.loadRoutes(filename);
    }
}
