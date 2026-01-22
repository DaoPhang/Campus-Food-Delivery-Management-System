import java.util.Scanner;

/**
 * Main class with integrated menu system demonstrating all 5 upgrades:
 * 1. Smart Dispatch (Weighted Optimization)
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

        ConsoleUI.printHeader("CAMPUS FOOD DELIVERY MANAGEMENT SYSTEM");

        // Initialize YOUR systems (Keep this line)
        initializeSystems();

        //Start Teammate's Web Server
        // We pass YOUR campusMap, dispatchSystem, and orderSystem into THEIR server
        try {
            WebServer server = new WebServer(campusMap, dispatchSystem, orderSystem);
            server.start();
            ConsoleUI.printSuccess("Web Dashboard running at: http://localhost:8080");
            ConsoleUI.printInfo("Admin Panel: http://localhost:8080/admin.html");
        } catch (java.io.IOException e) {
            ConsoleUI.printError("Web Server failed to start: " + e.getMessage());
        }

        // 3. Continue with YOUR Console Menu Loop (Keep the rest the same)
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
                    ConsoleUI.printSuccess("Thank you for using Campus Food Delivery System!");
                    break;
                default:
                    ConsoleUI.printError("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void initializeSystems() {
        ConsoleUI.showLoading("Initializing Systems");

        // Initialize Graph and CampusMap
        Graph graph = new Graph();
        campusMap = new CampusMap(graph);

        // Initialize Dispatch and Order systems
        dispatchSystem = new DispatchSystem();
        orderSystem = new OrderSystem();

        // Load data from files (UPGRADE 2)
        loadSystemData();

        ConsoleUI.printSuccess("System initialized successfully!");
    }

    /**
     * Load all system data from files
     * Used by both initializeSystems() and loadAllData()
     */
    private static void loadSystemData() {
        campusMap.loadLocations("data/locations.txt");
        campusMap.loadRoutes("data/routes.txt");
        dispatchSystem.loadRiders("data/riders.txt");
        orderSystem.loadOrders("data/orders.txt");
    }

    private static void displayMainMenu() {
        System.out.println("\n╔═══════════════════════════════╗");
        System.out.println("║        MAIN MENU              ║");
        System.out.println("╠═══════════════════════════════╣");
        System.out.println("║  1. Order Management          ║");
        System.out.println("║  2. Rider Management          ║");
        System.out.println("║  3. Dispatch Operations       ║");
        System.out.println("║  4. Campus Map                ║");
        System.out.println("║  5. Save/Load Data            ║");
        System.out.println("║  6. System Statistics         ║");
        System.out.println("║  0. Exit                      ║");
        System.out.println("╚═══════════════════════════════╝");
    }

    // ==========================================
    // ORDER MENU
    // ==========================================
    private static void orderMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.printHeader("ORDER MANAGEMENT");
            System.out.println("1. Add New Order");
            System.out.println("2. View All Orders");
            System.out.println("3. View Pending Orders");
            System.out.println("4. View Orders by Status");
            System.out.println("5. Search Order by ID");
            System.out.println("6. Search Orders by Student Name");
            System.out.println("7. Cancel Order");
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
                    viewOrdersByStatus();
                    break;
                case 5:
                    searchOrder();
                    break;
                case 6:
                    searchOrdersByStudentName();
                    break;
                case 7:
                    cancelOrder();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private static void addNewOrder() {
        ConsoleUI.printHeader("ADD NEW ORDER");
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
        ConsoleUI.printSuccess("Order added successfully!");
    }

    private static void searchOrder() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine().trim();
        Order order = orderSystem.searchOrder(orderId);
        if (order != null) {
            ConsoleUI.printSuccess("Found: " + order);
        } else {
            ConsoleUI.printError("Order not found.");
        }
    }

    private static void searchOrdersByStudentName() {
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();
        int[] count = new int[1];
        Order[] results = orderSystem.searchByStudentName(name, count);
        
        ConsoleUI.printHeader("Search Results for: " + name);
        
        if (count[0] == 0) {
            ConsoleUI.printWarning("No orders found for student: " + name);
        } else {
            // Define column widths {ID, Student Name, Priority, Status}
            int[] widths = {10, 20, 10, 15};

            // Print Header
            ConsoleUI.printTableSeparator(widths);
            ConsoleUI.printRow(widths, "ORDER ID", "STUDENT", "PRIORITY", "STATUS");
            ConsoleUI.printTableSeparator(widths);

            // Print Data
            for (int i = 0; i < count[0]; i++) {
                Order o = results[i];
                
                // Color code the status
                String statusStr = o.getStatus();
                if (statusStr.equals("Pending")) statusStr = ConsoleUI.YELLOW + statusStr + ConsoleUI.RESET;
                if (statusStr.equals("Delivered")) statusStr = ConsoleUI.GREEN + statusStr + ConsoleUI.RESET;
                if (statusStr.equals("Delivering")) statusStr = ConsoleUI.BLUE + statusStr + ConsoleUI.RESET;
                if (statusStr.equals("Cancelled")) statusStr = ConsoleUI.RED + statusStr + ConsoleUI.RESET;

                ConsoleUI.printRow(widths, 
                    o.getId(), 
                    o.getStudentName(), 
                    String.valueOf(o.getPriority()), 
                    statusStr
                );
            }
            ConsoleUI.printTableSeparator(widths);
        }
    }

    private static void viewOrdersByStatus() {
        System.out.println("\nSelect Status:");
        System.out.println("1. Pending");
        System.out.println("2. Delivering");
        System.out.println("3. Delivered");
        System.out.println("4. Cancelled");
        int choice = getIntInput("Enter choice: ");
        
        String status;
        switch (choice) {
            case 1: status = "Pending"; break;
            case 2: status = "Delivering"; break;
            case 3: status = "Delivered"; break;
            case 4: status = "Cancelled"; break;
            default:
                ConsoleUI.printError("Invalid choice.");
                return;
        }
        orderSystem.displayOrdersByStatus(status);
    }

    private static void cancelOrder() {
        System.out.print("Enter Order ID to cancel: ");
        String orderId = scanner.nextLine().trim();
        
        Order order = orderSystem.searchOrder(orderId);
        if (order == null) {
            ConsoleUI.printError("Order " + orderId + " not found.");
            return;
        }
        
        // Check if order is being delivered - need to reset rider
        if (order.getStatus().equals("Delivering")) {
            // Find the rider carrying this order and reset them
            for (Rider rider : dispatchSystem.getAllRiders()) {
                if (rider.getCurrentOrderId() != null && rider.getCurrentOrderId().equals(orderId)) {
                    rider.setStatus(Rider.RiderStatus.AVAILABLE);
                    rider.setCurrentOrderId(null);
                    ConsoleUI.printInfo("Rider " + rider.getName() + " has been freed.");
                    break;
                }
            }
        }
        
        Order cancelled = orderSystem.cancelOrder(orderId);
        if (cancelled != null) {
            ConsoleUI.printSuccess("Order Cancelled Successfully!");
            ConsoleUI.printDivider();
            System.out.println(cancelled);
            ConsoleUI.printDivider();
        }
    }

    // ==========================================
    // RIDER MENU
    // ==========================================
    private static void riderMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.printHeader("RIDER MANAGEMENT");
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
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private static void addNewRider() {
        ConsoleUI.printHeader("ADD NEW RIDER");
        System.out.print("Rider ID: ");
        String riderId = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Starting Location: ");
        String location = scanner.nextLine().trim();

        Rider rider = new Rider(riderId, name, location);
        dispatchSystem.addRider(rider);
        ConsoleUI.printSuccess("Rider added successfully!");
    }

    private static void searchRider() {
        System.out.print("Enter Rider ID: ");
        String riderId = scanner.nextLine().trim();
        Rider rider = dispatchSystem.getRider(riderId);
        if (rider != null) {
            ConsoleUI.printSuccess("Found: " + rider.toDetailedString());
        } else {
            ConsoleUI.printError("Rider not found.");
        }
    }

    // ==========================================
    // DISPATCH MENU (UPGRADE 1 & 3)
    // ==========================================
    private static void dispatchMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.printHeader("DISPATCH OPERATIONS");
            ConsoleUI.printInfo("Weighted Optimization Active | Undo/Redo System Available");
            System.out.println("1. Assign Next Order (Weighted Dispatch)");
            System.out.println("2. Complete Order");
            System.out.println("3. Undo Last Dispatch");
            System.out.println("4. Redo Last Undo");
            System.out.println("5. View Dispatch Status");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    ConsoleUI.printInfo("Using Weighted Scoring Algorithm (Distance + Workload + IdleTime)");
                    ConsoleUI.showLoading("Optimizing Rider Selection");
                    dispatchSystem.assignOrder(orderSystem, campusMap);
                    break;
                case 2:
                    completeOrder();
                    break;
                case 3:
                    ConsoleUI.showLoading("Undoing last dispatch");
                    dispatchSystem.undoLastDispatch(orderSystem);
                    break;
                case 4:
                    ConsoleUI.showLoading("Redoing last undo");
                    dispatchSystem.redoLastDispatch(orderSystem, campusMap);
                    break;
                case 5:
                    dispatchSystem.displayDispatchStatistics();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private static void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String orderId = scanner.nextLine().trim();
        dispatchSystem.completeOrder(orderId, orderSystem, campusMap);
        // Statistics are now recorded inside DispatchSystem.completeOrder() with actual distance
    }

    // ==========================================
    // MAP MENU (UPGRADE 4)
    // ==========================================
    private static void mapMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.printHeader("CAMPUS MAP");
            ConsoleUI.printInfo("Graph-based Location System with Dijkstra's Algorithm");
            System.out.println("1. Add New Location");
            System.out.println("2. Add Route Between Locations");
            System.out.println("3. Display Full Map (Adjacency List)");
            System.out.println("4. Find Shortest Path (Dijkstra)");
            System.out.println("5. Get Distance Between Locations");
            System.out.println("6. View Cache Statistics");
            System.out.println("7. Clear Path Cache");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    addLocation();
                    break;
                case 2:
                    addRoute();
                    break;
                case 3:
                    campusMap.displayMap();
                    break;
                case 4:
                    findShortestPath();
                    break;
                case 5:
                    getDistance();
                    break;
                case 6:
                    displayCacheStats();
                    break;
                case 7:
                    campusMap.getGraph().clearCache();
                    ConsoleUI.printSuccess("Path cache cleared.");
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private static void addLocation() {
        ConsoleUI.printHeader("ADD NEW LOCATION");
        System.out.print("Location Name: ");
        String name = scanner.nextLine().trim();
        
        if (campusMap.getGraph().hasLocation(name)) {
            ConsoleUI.printError("Location '" + name + "' already exists.");
            return;
        }
        
        System.out.print("Faculty/Dorm (e.g., Faculty, Hostel, Dining): ");
        String facultyOrDorm = scanner.nextLine().trim();
        System.out.print("Block/Zone: ");
        String block = scanner.nextLine().trim();
        
        campusMap.addLocation(name, facultyOrDorm, block);
        ConsoleUI.printSuccess("Location '" + name + "' added successfully!");
    }

    private static void addRoute() {
        ConsoleUI.printHeader("ADD ROUTE");
        System.out.print("From Location: ");
        String from = scanner.nextLine().trim();
        System.out.print("To Location: ");
        String to = scanner.nextLine().trim();
        
        // Validate locations exist
        if (!campusMap.getGraph().hasLocation(from)) {
            ConsoleUI.printError("Location '" + from + "' does not exist.");
            return;
        }
        if (!campusMap.getGraph().hasLocation(to)) {
            ConsoleUI.printError("Location '" + to + "' does not exist.");
            return;
        }
        
        int distance = getIntInput("Distance (in meters): ");
        if (distance <= 0) {
            ConsoleUI.printError("Distance must be positive.");
            return;
        }
        
        campusMap.addRoute(from, to, distance);
        ConsoleUI.printSuccess("Route added: " + from + " <-> " + to + " (" + distance + "m)");
    }

    private static void findShortestPath() {
        System.out.print("From: ");
        String from = scanner.nextLine().trim();
        System.out.print("To: ");
        String to = scanner.nextLine().trim();

        ConsoleUI.showLoading("Calculating Optimal Route");
        int[] length = new int[1];
        String[] path = campusMap.getShortestPath(from, to, length);

        if (length[0] == 0) {
            ConsoleUI.printError("No path found between " + from + " and " + to);
        } else {
            ConsoleUI.printSuccess("Path Found!");
            System.out.print(ConsoleUI.GREEN + "Shortest Path: " + ConsoleUI.RESET);
            for (int i = 0; i < length[0]; i++) {
                System.out.print(ConsoleUI.CYAN + path[i] + ConsoleUI.RESET);
                if (i < length[0] - 1)
                    System.out.print(" → ");
            }
            System.out.println();
            System.out.println(ConsoleUI.YELLOW + "Distance: " + campusMap.getDistance(from, to) + " meters" + ConsoleUI.RESET);
        }
    }

    private static void getDistance() {
        System.out.print("From: ");
        String from = scanner.nextLine().trim();
        System.out.print("To: ");
        String to = scanner.nextLine().trim();

        double distance = campusMap.getDistance(from, to);
        if (distance >= 0) {
            ConsoleUI.printSuccess("Distance: " + distance + " meters");
        } else {
            ConsoleUI.printError("No path found.");
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

    private static void persistenceMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.printHeader("FILE PERSISTENCE");
            ConsoleUI.printInfo("Save/Load System Data to Files");
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
                    ConsoleUI.printSuccess("Riders saved!");
                    break;
                case 4:
                    orderSystem.saveOrders("data/orders_backup.txt");
                    ConsoleUI.printSuccess("Orders saved!");
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    ConsoleUI.printError("Invalid choice.");
            }
        }
    }

    private static void saveAllData() {
        ConsoleUI.showLoading("Saving all data");
        dispatchSystem.saveRiders("data/riders_backup.txt");
        orderSystem.saveOrders("data/orders_backup.txt");
        ConsoleUI.printSuccess("All data saved successfully!");
    }

    private static void loadAllData() {
        ConsoleUI.showLoading("Reloading data from files");
        loadSystemData();
        ConsoleUI.printSuccess("Data reloaded successfully!");
    }

    private static void displayStatistics() {
        ConsoleUI.printHeader("SYSTEM STATISTICS");
        ConsoleUI.showLoading("Gathering performance metrics");
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
