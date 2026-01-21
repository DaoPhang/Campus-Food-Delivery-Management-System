<<<<<<< HEAD
import java.io.*;
import java.util.*;

/**
 * OrderSystem manages orders using a PriorityQueue for efficient dispatching
 * and a HashMap for O(1) order lookup by ID
 */
public class OrderSystem {
    private PriorityQueue<Order> orderQueue; // Pending orders sorted by priority
    private HashMap<String, Order> orderMap; // All orders for quick lookup
    private List<Order> allOrders; // All orders including completed ones

    public OrderSystem() {
        this.orderQueue = new PriorityQueue<>();
        this.orderMap = new HashMap<>();
        this.allOrders = new ArrayList<>();
    }

    /**
     * Add a new order to the system
     */
    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
        allOrders.add(order);
        if (order.getStatus().equals("Pending")) {
            orderQueue.offer(order);
        }
    }

    /**
     * Get the next pending order without removing it
     */
    public Order getNextPendingOrder() {
        return orderQueue.peek();
    }

    /**
     * Remove and return the next pending order
     */
    public Order pollPriorityQueue() {
        return orderQueue.poll();
    }

    /**
     * Search for an order by ID - O(1) lookup
     */
    public Order searchOrder(String orderID) {
        return orderMap.get(orderID);
    }

    /**
     * Get number of pending orders
     */
    public int getPendingCount() {
        return orderQueue.size();
    }

    /**
     * Get total number of orders
     */
    public int getTotalCount() {
        return allOrders.size();
    }

    /**
     * Display all orders
     */
    public void displayAllOrders() {
        System.out.println("\n=== All Orders ===");
        if (allOrders.isEmpty()) {
            System.out.println("No orders in the system.");
        } else {
            for (Order order : allOrders) {
                System.out.println(order);
            }
        }
        System.out.println("==================\n");
    }

    /**
     * Display pending orders only
     */
    public void displayPendingOrders() {
        System.out.println("\n=== Pending Orders ===");
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders.");
        } else {
            // Create a copy to iterate without modifying original
            PriorityQueue<Order> copy = new PriorityQueue<>(orderQueue);
            while (!copy.isEmpty()) {
                System.out.println(copy.poll());
            }
        }
        System.out.println("======================\n");
    }

    /**
     * Re-add an order to the pending queue (for undo operations)
     */
    public void readdToPendingQueue(Order order) {
        if (order != null && order.getStatus().equals("Pending")) {
            orderQueue.offer(order);
        }
    }

    /**
     * Load orders from file
     * Format: orderID,studentName,pickup,delivery,priority,status
     */
    public void loadOrders(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String orderId = parts[0].trim();
                    String studentName = parts[1].trim();
                    String pickup = parts[2].trim();
                    String delivery = parts[3].trim();
                    int priority = Integer.parseInt(parts[4].trim());
                    String status = parts.length > 5 ? parts[5].trim() : "Pending";

                    Order order = new Order(orderId, studentName, pickup, delivery, priority, status);
                    addOrder(order);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " orders from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Orders file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading orders file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing priority in orders file: " + e.getMessage());
        }
    }

    /**
     * Save orders to file
     */
    public void saveOrders(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Order order : allOrders) {
                writer.println(order.toFileFormat());
            }
            System.out.println("Saved " + allOrders.size() + " orders to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    /**
     * Get all orders list
     */
    public List<Order> getAllOrders() {
        return allOrders;
    }
}
=======
import java.util.*;
import java.io.*;

public class OrderSystem {

    // REQUIREMENT 1: PriorityQueue for "Smart Dispatch" (Urgent first)
    private PriorityQueue<Order> pendingQueue;

    // REQUIREMENT 2: LinkedList for "History" (All orders)
    private LinkedList<Order> allOrders;

    // REQUIREMENT 3: HashMap for "Fast Search" by ID
    private HashMap<String, Order> orderMap;

    public OrderSystem() {
        this.pendingQueue = new PriorityQueue<>();
        this.allOrders = new LinkedList<>();
        this.orderMap = new HashMap<>();
    }

    // METHOD: Add a NEW order (from the menu)
    public void addOrder(Order order) {
        if (orderMap.containsKey(order.getOrderId())) {
            System.out.println("Error: Order ID " + order.getOrderId() + " already exists.");
            return;
        }

        // Add to ALL three data structures
        pendingQueue.add(order);  
        allOrders.add(order);     
        orderMap.put(order.getOrderId(), order); 
    }

    // METHOD: View all orders
    public void displayAllOrders() {
        System.out.println("\n--- All Orders History ---");
        if (allOrders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            for (Order o : allOrders) {
                System.out.println(o);
            }
        }
    }

    // METHOD: View pending orders (Priority View)
    public void displayPendingOrders() {
        System.out.println("\n--- Pending Orders (Priority View) ---");
        if (pendingQueue.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }

        // Use a copy to display so we don't remove items from the real queue
        PriorityQueue<Order> copy = new PriorityQueue<>(pendingQueue);
        while (!copy.isEmpty()) {
            System.out.println(copy.poll()); // .poll() pulls items in correct priority order
        }
    }

    // METHOD: Search by ID
    public Order searchOrder(String orderId) {
        return orderMap.get(orderId); // Instant O(1) lookup
    }

    public PriorityQueue<Order> getPendingQueue() {
        return pendingQueue;
    }

    // --- FILE OPERATIONS ---
    public void saveOrders(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Order o : allOrders) {
                writer.write(o.toFileFormat()); // Uses the method from Order.java
                writer.newLine();
            }
            System.out.println("Orders saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    public void loadOrders(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    // Reconstruct order from file data
                    String id = parts[0];
                    String name = parts[1];
                    String pickup = parts[2];
                    String delivery = parts[3];
                    int priority = Integer.parseInt(parts[4]);
                    String status = parts[5];

                    Order o = new Order(id, name, pickup, delivery, priority, status);

                    allOrders.add(o);
                    orderMap.put(o.getOrderId(), o);

                    // 2. ONLY add to PriorityQueue if it is actually Pending
                    if (o.getStatus().equalsIgnoreCase("Pending")) {
                        pendingQueue.add(o);
                    }
                }
            }
            System.out.println("Orders loaded from " + filename);
        } catch (Exception e) {
            System.out.println("Error loading orders.");
        }
    }
}


>>>>>>> 631647f513795f854269fb90c20cc9a9de7b4af5
