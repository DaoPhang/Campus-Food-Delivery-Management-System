/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author yanpi
 */
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

                    // --- YOUR FIX APPLIED HERE ---
                    // 1. Always add to History and Map
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
