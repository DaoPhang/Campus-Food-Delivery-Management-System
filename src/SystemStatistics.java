/**
 * SystemStatistics provides comprehensive performance metrics
 * UPGRADE 5: System Performance Statistics
 */
public class SystemStatistics {
    // Singleton instance
    private static SystemStatistics instance;

    private int totalOrdersProcessed;
    private double totalDistanceTraveled;
    private long systemStartTime;

    private SystemStatistics() {
        this.totalOrdersProcessed = 0;
        this.totalDistanceTraveled = 0.0;
        this.systemStartTime = System.currentTimeMillis();
    }

    /**
     * Get singleton instance
     */
    public static SystemStatistics getInstance() {
        if (instance == null) {
            instance = new SystemStatistics();
        }
        return instance;
    }

    /**
     * Reset the instance (for testing)
     */
    public static void reset() {
        instance = new SystemStatistics();
    }

    /**
     * Record an order being processed
     */
    public void recordOrderProcessed(double distance) {
        totalOrdersProcessed++;
        totalDistanceTraveled += distance;
    }

    /**
     * Get total orders processed
     */
    public int getTotalOrdersProcessed() {
        return totalOrdersProcessed;
    }

    /**
     * Get total distance traveled
     */
    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    /**
     * Get average distance per order
     */
    public double getAverageDistance() {
        if (totalOrdersProcessed == 0)
            return 0.0;
        return totalDistanceTraveled / totalOrdersProcessed;
    }

    /**
     * Get system uptime in seconds
     */
    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - systemStartTime) / 1000;
    }

    /**
     * Display comprehensive system statistics
     */
    public void displayStatistics(Graph graph, DispatchSystem dispatch, OrderSystem orders) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      SYSTEM PERFORMANCE STATISTICS      ║");
        System.out.println("╠════════════════════════════════════════╣");

        // Order Statistics
        System.out.println("║  📦 ORDER STATISTICS                    ║");
        System.out.printf("║    Total Orders Processed: %-13d║%n", totalOrdersProcessed);
        System.out.printf("║    Pending Orders: %-20d║%n", orders.getPendingCount());
        System.out.printf("║    Total Distance: %-16.1f km ║%n", totalDistanceTraveled);
        System.out.printf("║    Avg Distance/Order: %-13.1f km ║%n", getAverageDistance());

        System.out.println("╠════════════════════════════════════════╣");

        // Rider Statistics
        System.out.println("║  🚴 RIDER STATISTICS                    ║");
        System.out.printf("║    Total Riders: %-22d║%n", dispatch.getTotalRiders());
        System.out.printf("║    Available Riders: %-18d║%n", dispatch.getAvailableRiderCount());
        System.out.printf("║    Orders Dispatched: %-17d║%n", dispatch.getTotalOrdersDispatched());

        System.out.println("╠════════════════════════════════════════╣");

        // Cache Statistics (UPGRADE 4)
        System.out.println("║  🗃️  CACHE STATISTICS (UPGRADE 4)        ║");
        System.out.printf("║    Cache Size: %-24d║%n", graph.getCacheSize());
        System.out.printf("║    Cache Hits: %-24d║%n", graph.getCacheHits());
        System.out.printf("║    Cache Misses: %-22d║%n", graph.getCacheMisses());
        System.out.printf("║    Cache Hit Rate: %-17.1f%% ║%n", graph.getCacheHitRate());

        System.out.println("╠════════════════════════════════════════╣");

        // Algorithm Statistics (UPGRADE 5)
        System.out.println("║  🧮 ALGORITHM STATISTICS (UPGRADE 5)    ║");
        System.out.printf("║    Dijkstra Calls: %-20d║%n", graph.getDijkstraCallCount());

        System.out.println("╠════════════════════════════════════════╣");

        // Undo/Redo Statistics (UPGRADE 3)
        System.out.println("║  ↩️  UNDO/REDO STATUS (UPGRADE 3)        ║");
        System.out.printf("║    Undo Stack: %-24d║%n", dispatch.getUndoStackSize());
        System.out.printf("║    Redo Stack: %-24d║%n", dispatch.getRedoStackSize());

        System.out.println("╠════════════════════════════════════════╣");

        // System Info
        System.out.println("║  ⏱️  SYSTEM INFO                         ║");
        System.out.printf("║    Uptime: %-25d sec ║%n", getUptimeSeconds());

        System.out.println("╚════════════════════════════════════════╝\n");
    }

    /**
     * Display simple statistics (without dependencies)
     */
    public void displaySimpleStatistics() {
        System.out.println("\n=== System Statistics ===");
        System.out.println("Orders: " + totalOrdersProcessed);
        System.out.printf("Avg Distance: %.1f km%n", getAverageDistance());
        System.out.printf("Total Distance: %.1f km%n", totalDistanceTraveled);
        System.out.println("=========================\n");
    }

    /**
     * Get statistics summary as string
     */
    public String getSummary() {
        return String.format("Orders: %d, Avg Dist: %.1f km, Total: %.1f km",
                totalOrdersProcessed, getAverageDistance(), totalDistanceTraveled);
    }
}
