import java.util.Scanner;

/**
 * Main class with integrated menu system demonstrating all 5 upgrades:
 * 1. Smart Dispatch v2 (Weighted Optimization)
 * 2. File Persistence (Save & Load)
 * 3. Undo/Redo System (Stack-based)
 * 4. Path Caching (HashMap Optimization)
 * 5. System Performance Statistics
 */
public class Main {
    private static CampusMap campusMap;
    private static DispatchSystem dispatchSystem;
    private static OrderSystem orderSystem;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║    CAMPUS FOOD DELIVERY MANAGEMENT SYSTEM               ║");
        System.out.println("║    WIA1002 Data Structures - Degree Level Project       ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Initialize systems
        initializeSystems();

        // Main menu loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    orderMenu();
                    break;
                case 2:
                    riderMenu();
                    break;
                case 3:
                    dispatchMenu();
                    break;
                case 4:
                    mapMenu();
                    break;
                case 5:
                    persistenceMenu();
                    break;
                case 6:
                    displayStatistics();
                    break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using Campus Food Delivery System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void initializeSystems() {
        System.out.println("Initializing systems...\n");

        // Initialize Graph and CampusMap
        Graph graph = new Graph();
        campusMap = new CampusMap(graph);

        // Initialize Dispatch and Order systems
        dispatchSystem = new DispatchSystem();
        orderSystem = new OrderSystem();

        // Load data from files (UPGRADE 2)
        System.out.println("\n--- Loading Data from Files (UPGRADE 2) ---");
        campusMap.loadLocations("data/locations.txt");
        campusMap.loadRoutes("data/routes.txt");
        dispatchSystem.loadRiders("data/riders.txt");
        orderSystem.loadOrders("data/orders.txt");

        System.out.println("\nSystem initialized successfully!\n");
    }

    private static void displayMainMenu() {
        System.out.println("\n╔═══════════════════════════════╗");
        System.out.println("║        MAIN MENU               ║");
        System.out.println("╠═══════════════════════════════╣");
        System.out.println("║  1. Order Management           ║");
        System.out.println("║  2. Rider Management           ║");
        System.out.println("║  3. Dispatch Operations        ║");
        System.out.println("║  4. Campus Map                 ║");
        System.out.println("║  5. Save/Load Data             ║");
        System.out.println("║  6. System Statistics          ║");
        System.out.println("║  0. Exit                       ║");
        System.out.println("╚═══════════════════════════════╝");
    }

    // ==========================================
    // ORDER MENU
    // ==========================================
    private static void orderMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Order Management ---");
            System.out.println("1. Add New Order");
            System.out.println("2. View All Orders");
            System.out.println("3. View Pending Orders");
            System.out.println("4. Search Order by ID");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    addNewOrder();
                    break;
                case 2:
                    orderSystem.displayAllOrders();
                    break;
                case 3:
                    orderSystem.displayPendingOrders();
                    break;
                case 4:
                    searchOrder();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addNewOrder() {
        System.out.println("\n--- Add New Order ---");
        System.out.print("Order ID: ");
        String orderId = scanner.nextLine().trim();
        System.out.print("Student Name: ");
        String studentName = scanner.nextLine().trim();
        System.out.print("Pickup Location: ");
        String pickup = scanner.nextLine().trim();
        System.out.print("Delivery Location: ");
        String delivery = scanner.nextLine().trim();
        int priority = getIntInput("Priority (1=highest): ");

        Order order = new Order(orderId, studentName, pickup, delivery, priority);
        orderSystem.addOrder(order);
        System.out.println("Order added successfully!");
    }

    private static void searchOrder() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine().trim();
        Order order = orderSystem.searchOrder(orderId);
        if (order != null) {
            System.out.println("Found: " + order);
        } else {
            System.out.println("Order not found.");
        }
    }

    // ==========================================
    // RIDER MENU
    // ==========================================
    private static void riderMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Rider Management ---");
            System.out.println("1. Add New Rider");
            System.out.println("2. View All Riders");
            System.out.println("3. View Available Riders");
            System.out.println("4. Search Rider by ID");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    addNewRider();
                    break;
                case 2:
                    dispatchSystem.displayAllRiders();
                    break;
                case 3:
                    dispatchSystem.displayAvailableRiders();
                    break;
                case 4:
                    searchRider();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addNewRider() {
        System.out.println("\n--- Add New Rider ---");
        System.out.print("Rider ID: ");
        String riderId = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Starting Location: ");
        String location = scanner.nextLine().trim();

        Rider rider = new Rider(riderId, name, location);
        dispatchSystem.addRider(rider);
        System.out.println("Rider added successfully!");
    }

    private static void searchRider() {
        System.out.print("Enter Rider ID: ");
        String riderId = scanner.nextLine().trim();
        Rider rider = dispatchSystem.getRider(riderId);
        if (rider != null) {
            System.out.println("Found: " + rider.toDetailedString());
        } else {
            System.out.println("Rider not found.");
        }
    }

    // ==========================================
    // DISPATCH MENU (UPGRADE 1 & 3)
    // ==========================================
    private static void dispatchMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Dispatch Operations ---");
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║  UPGRADE 1: Weighted Optimization Active  ║");
            System.out.println("║  UPGRADE 3: Undo/Redo System Available    ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.println("1. Assign Next Order (Weighted Dispatch)");
            System.out.println("2. Complete Order");
            System.out.println("3. Undo Last Dispatch");
            System.out.println("4. Redo Last Undo");
            System.out.println("5. View Dispatch Status");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    System.out.println(
                            "\n[UPGRADE 1] Using weighted scoring: score = (1.0×distance) + (2.0×jobs) - (0.5×idleTime/1000)");
                    dispatchSystem.assignOrder(orderSystem, campusMap);
                    break;
                case 2:
                    completeOrder();
                    break;
                case 3:
                    System.out.println("\n[UPGRADE 3] Undoing last dispatch...");
                    dispatchSystem.undoLastDispatch(orderSystem);
                    break;
                case 4:
                    System.out.println("\n[UPGRADE 3] Redoing last undo...");
                    dispatchSystem.redoLastDispatch(orderSystem, campusMap);
                    break;
                case 5:
                    dispatchSystem.displayDispatchStatistics();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String orderId = scanner.nextLine().trim();
        dispatchSystem.completeOrder(orderId, orderSystem, campusMap);

        // Update global statistics
        Order order = orderSystem.searchOrder(orderId);
        if (order != null && order.getStatus().equals("Delivered")) {
            SystemStatistics.getInstance().recordOrderProcessed(
                    dispatchSystem.getAverageDistancePerOrder());
        }
    }

    // ==========================================
    // MAP MENU (UPGRADE 4)
    // ==========================================
    private static void mapMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Campus Map ---");
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║  UPGRADE 4: Path Caching Active           ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.println("1. Display Full Map");
            System.out.println("2. Find Shortest Path");
            System.out.println("3. Get Distance Between Locations");
            System.out.println("4. View Cache Statistics");
            System.out.println("5. Clear Path Cache");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    campusMap.displayMap();
                    break;
                case 2:
                    findShortestPath();
                    break;
                case 3:
                    getDistance();
                    break;
                case 4:
                    displayCacheStats();
                    break;
                case 5:
                    campusMap.getGraph().clearCache();
                    System.out.println("Path cache cleared.");
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void findShortestPath() {
        System.out.print("From: ");
        String from = scanner.nextLine().trim();
        System.out.print("To: ");
        String to = scanner.nextLine().trim();

        System.out.println("\n[UPGRADE 4] Checking cache...");
        java.util.List<String> path = campusMap.getShortestPath(from, to);

        if (path.isEmpty()) {
            System.out.println("No path found between " + from + " and " + to);
        } else {
            System.out.print("Shortest Path: ");
            for (int i = 0; i < path.size(); i++) {
                System.out.print(path.get(i));
                if (i < path.size() - 1)
                    System.out.print(" → ");
            }
            System.out.println();
            System.out.println("Distance: " + campusMap.getDistance(from, to) + " km");
        }
    }

    private static void getDistance() {
        System.out.print("From: ");
        String from = scanner.nextLine().trim();
        System.out.print("To: ");
        String to = scanner.nextLine().trim();

        double distance = campusMap.getDistance(from, to);
        if (distance >= 0) {
            System.out.println("Distance: " + distance + " km");
        } else {
            System.out.println("No path found.");
        }
    }

    private static void displayCacheStats() {
        Graph graph = campusMap.getGraph();
        System.out.println("\n[UPGRADE 4] Path Cache Statistics:");
        System.out.println("  Cache Size: " + graph.getCacheSize() + " entries");
        System.out.println("  Cache Hits: " + graph.getCacheHits());
        System.out.println("  Cache Misses: " + graph.getCacheMisses());
        System.out.printf("  Hit Rate: %.1f%%%n", graph.getCacheHitRate());
        System.out.println("  Dijkstra Calls: " + graph.getDijkstraCallCount());
    }

    // ==========================================
    // PERSISTENCE MENU (UPGRADE 2)
    // ==========================================
    private static void persistenceMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- File Persistence ---");
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║  UPGRADE 2: File Save/Load System         ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.println("1. Save All Data");
            System.out.println("2. Load All Data");
            System.out.println("3. Save Riders Only");
            System.out.println("4. Save Orders Only");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    saveAllData();
                    break;
                case 2:
                    loadAllData();
                    break;
                case 3:
                    dispatchSystem.saveRiders("data/riders_backup.txt");
                    break;
                case 4:
                    orderSystem.saveOrders("data/orders_backup.txt");
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void saveAllData() {
        System.out.println("\n[UPGRADE 2] Saving all data...");
        dispatchSystem.saveRiders("data/riders_backup.txt");
        orderSystem.saveOrders("data/orders_backup.txt");
        System.out.println("All data saved successfully!");
    }

    private static void loadAllData() {
        System.out.println("\n[UPGRADE 2] Reloading data from files...");
        campusMap.loadLocations("data/locations.txt");
        campusMap.loadRoutes("data/routes.txt");
        dispatchSystem.loadRiders("data/riders.txt");
        orderSystem.loadOrders("data/orders.txt");
        System.out.println("Data reloaded successfully!");
    }

    // ==========================================
    // STATISTICS (UPGRADE 5)
    // ==========================================
    private static void displayStatistics() {
        System.out.println("\n[UPGRADE 5] Displaying comprehensive system statistics...");
        SystemStatistics.getInstance().displayStatistics(
                campusMap.getGraph(),
                dispatchSystem,
                orderSystem);
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
