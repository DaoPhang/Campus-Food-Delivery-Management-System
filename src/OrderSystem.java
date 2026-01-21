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
        if (orderMap.containsKey(order.getId())) {
            System.out.println("Error: Order ID " + order.getId() + " already exists.");
            return;
        }
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

    /**
     * Cancel an order by ID
     * If the order is in the pending queue, remove it
     * @return the cancelled order, or null if not found
     */
    public Order cancelOrder(String orderId) {
        Order order = orderMap.get(orderId);
        if (order == null) {
            return null;
        }

        // If order is pending, remove from priority queue
        if (order.getStatus().equals("Pending")) {
            orderQueue.remove(order);
        }

        // Update status to Cancelled
        order.setStatus("Cancelled");
        return order;
    }

    /**
     * Search orders by student name (case-insensitive partial match)
     * @return list of matching orders
     */
    public List<Order> searchByStudentName(String name) {
        List<Order> results = new ArrayList<>();
        String searchName = name.toLowerCase();
        for (Order order : allOrders) {
            if (order.getStudentName().toLowerCase().contains(searchName)) {
                results.add(order);
            }
        }
        return results;
    }

    /**
     * Search orders by status
     * @return list of orders with matching status
     */
    public List<Order> searchByStatus(String status) {
        List<Order> results = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getStatus().equalsIgnoreCase(status)) {
                results.add(order);
            }
        }
        return results;
    }

    /**
     * Display orders filtered by status
     */
    public void displayOrdersByStatus(String status) {
        System.out.println("\n=== Orders with Status: " + status + " ===");
        List<Order> filtered = searchByStatus(status);
        if (filtered.isEmpty()) {
            System.out.println("No orders found with status: " + status);
        } else {
            for (Order order : filtered) {
                System.out.println(order);
            }
        }
        System.out.println("=====================================\n");
    }

    /**
     * Get count of orders by status
     */
    public int getCountByStatus(String status) {
        int count = 0;
        for (Order order : allOrders) {
            if (order.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
}
