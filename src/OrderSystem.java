import java.io.*;

/**
 * OrderSystem manages orders using manual array-based implementations
 * Uses FIFO queue for pending orders and array for order lookup
 */
public class OrderSystem {
    private static final int MAX_ORDERS = 200;

    // Manual FIFO queue for pending orders
    private Order[] orderQueue;
    private int front;
    private int rear;

    // All orders array for lookup
    private Order[] allOrders;
    private int orderCount;

    public OrderSystem() {
        this.orderQueue = new Order[MAX_ORDERS];
        this.front = 0;
        this.rear = 0;

        this.allOrders = new Order[MAX_ORDERS];
        this.orderCount = 0;
    }

    /**
     * Search for order by ID - linear search
     */
    public Order searchOrder(String orderID) {
        for (int i = 0; i < orderCount; i++) {
            if (allOrders[i].getId().equals(orderID)) {
                return allOrders[i];
            }
        }
        return null;
    }

    /**
     * Check if order ID already exists
     */
    private boolean orderExists(String orderID) {
        return searchOrder(orderID) != null;
    }

    /**
     * Add a new order to the system
     */
    public void addOrder(Order order) {
        if (orderExists(order.getId())) {
            System.out.println("Error: Order ID " + order.getId() + " already exists.");
            return;
        }

        if (orderCount >= MAX_ORDERS) {
            System.out.println("Error: Maximum order capacity reached.");
            return;
        }

        // Add to all orders array
        allOrders[orderCount++] = order;

        // If pending, add to queue
        if (order.getStatus().equals("Pending")) {
            enqueue(order);
        }
    }

    /**
     * Enqueue order to FIFO queue (sorted by priority - lower number = higher priority)
     */
    private void enqueue(Order newOrder) {
        if (rear >= MAX_ORDERS) {
            System.out.println("Error: Queue is full.");
            return;
        }

        // Insert sorted by priority (insertion sort style)
        int insertPos = rear;
        
        // Find correct position based on priority
        for (int i = front; i < rear; i++) {
            if (newOrder.getPriority() < orderQueue[i].getPriority()) {
                insertPos = i;
                break;
            }
        }

        // Shift elements to make room
        for (int i = rear; i > insertPos; i--) {
            orderQueue[i] = orderQueue[i - 1];
        }

        orderQueue[insertPos] = newOrder;
        rear++;
    }

    /**
     * Dequeue - remove and return front order
     */
    private Order dequeue() {
        if (front >= rear) {
            return null;
        }

        Order order = orderQueue[front];

        // Shift remaining elements
        for (int i = front; i < rear - 1; i++) {
            orderQueue[i] = orderQueue[i + 1];
        }
        rear--;

        return order;
    }

    /**
     * Get the next pending order without removing it (peek)
     */
    public Order getNextPendingOrder() {
        if (front >= rear) {
            return null;
        }
        return orderQueue[front];
    }

    /**
     * Remove and return the next pending order
     */
    public Order pollPriorityQueue() {
        return dequeue();
    }

    /**
     * Get number of pending orders
     */
    public int getPendingCount() {
        return rear - front;
    }

    /**
     * Get total number of orders
     */
    public int getTotalCount() {
        return orderCount;
    }

    /**
     * Display all orders
     */
    public void displayAllOrders() {
        System.out.println("\n=== All Orders ===");
        if (orderCount == 0) {
            System.out.println("No orders in the system.");
        } else {
            for (int i = 0; i < orderCount; i++) {
                System.out.println(allOrders[i]);
            }
        }
        System.out.println("==================\n");
    }

    /**
     * Display pending orders only
     */
    public void displayPendingOrders() {
        System.out.println("\n=== Pending Orders ===");
        if (front >= rear) {
            System.out.println("No pending orders.");
        } else {
            for (int i = front; i < rear; i++) {
                System.out.println(orderQueue[i]);
            }
        }
        System.out.println("======================\n");
    }

    /**
     * Re-add an order to the pending queue (for undo operations)
     */
    public void readdToPendingQueue(Order order) {
        if (order != null && order.getStatus().equals("Pending")) {
            enqueue(order);
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
                if (line.isEmpty()) continue;

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
            for (int i = 0; i < orderCount; i++) {
                writer.println(allOrders[i].toFileFormat());
            }
            System.out.println("Saved " + orderCount + " orders to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    /**
     * Get all orders as array
     */
    public Order[] getAllOrders() {
        Order[] result = new Order[orderCount];
        for (int i = 0; i < orderCount; i++) {
            result[i] = allOrders[i];
        }
        return result;
    }

    /**
     * Get order count
     */
    public int getOrderCount() {
        return orderCount;
    }

    /**
     * Cancel an order by ID
     */
    public Order cancelOrder(String orderId) {
        Order order = searchOrder(orderId);
        if (order == null) {
            return null;
        }

        // If order is pending, remove from queue
        if (order.getStatus().equals("Pending")) {
            removeFromQueue(order);
        }

        // Update status to Cancelled
        order.setStatus("Cancelled");
        return order;
    }

    /**
     * Remove specific order from queue
     */
    private void removeFromQueue(Order order) {
        int removeIdx = -1;
        for (int i = front; i < rear; i++) {
            if (orderQueue[i].getId().equals(order.getId())) {
                removeIdx = i;
                break;
            }
        }

        if (removeIdx != -1) {
            // Shift elements
            for (int i = removeIdx; i < rear - 1; i++) {
                orderQueue[i] = orderQueue[i + 1];
            }
            rear--;
        }
    }

    /**
     * Search orders by student name (returns matching orders)
     */
    public Order[] searchByStudentName(String name, int[] countOut) {
        Order[] results = new Order[orderCount];
        int resultCount = 0;
        String searchName = name.toLowerCase();

        for (int i = 0; i < orderCount; i++) {
            if (allOrders[i].getStudentName().toLowerCase().contains(searchName)) {
                results[resultCount++] = allOrders[i];
            }
        }

        if (countOut != null && countOut.length > 0) {
            countOut[0] = resultCount;
        }
        return results;
    }

    /**
     * Search orders by status
     */
    public Order[] searchByStatus(String status, int[] countOut) {
        Order[] results = new Order[orderCount];
        int resultCount = 0;

        for (int i = 0; i < orderCount; i++) {
            if (allOrders[i].getStatus().equalsIgnoreCase(status)) {
                results[resultCount++] = allOrders[i];
            }
        }

        if (countOut != null && countOut.length > 0) {
            countOut[0] = resultCount;
        }
        return results;
    }

    /**
     * Display orders filtered by status
     */
    public void displayOrdersByStatus(String status) {
        System.out.println("\n=== Orders with Status: " + status + " ===");
        int[] count = new int[1];
        Order[] filtered = searchByStatus(status, count);

        if (count[0] == 0) {
            System.out.println("No orders found with status: " + status);
        } else {
            for (int i = 0; i < count[0]; i++) {
                System.out.println(filtered[i]);
            }
        }
        System.out.println("=====================================\n");
    }

    /**
     * Get count of orders by status
     */
    public int getCountByStatus(String status) {
        int count = 0;
        for (int i = 0; i < orderCount; i++) {
            if (allOrders[i].getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
}
