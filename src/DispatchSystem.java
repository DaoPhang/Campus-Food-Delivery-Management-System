import java.io.*;

/**
 * DispatchSystem manages rider dispatch operations
 */
public class DispatchSystem {
    // Manual array-based rider list
    private Rider[] riderList;
    private int riderCount;
    private static final int MAX_RIDERS = 100;

    // UPGRADE: CustomHashMap for O(1) Fast Lookup (Requirement #3)
    private CustomHashMap<String, Rider> riderMap;

    // Manual array-based undo/redo stacks
    private DispatchAction[] undoStack;
    private int undoTop;
    private DispatchAction[] redoStack;
    private int redoTop;
    private static final int MAX_STACK_SIZE = 50;
    private int totalOrdersDispatched;
    private double totalDistanceAssigned;
    private static final double ALPHA = 1.0; // Distance importance
    private static final double BETA = 2.0; // Workload balancing
    private static final double GAMMA = 0.5; // Fairness (idle riders preferred)

    public DispatchSystem() {
        // Initialize manual arrays
        this.riderList = new Rider[MAX_RIDERS];
        this.riderCount = 0;
        
        // Initialize Custom Map
        this.riderMap = new CustomHashMap<>();
        
        // Initialize undo/redo stacks
        this.undoStack = new DispatchAction[MAX_STACK_SIZE];
        this.undoTop = 0;
        this.redoStack = new DispatchAction[MAX_STACK_SIZE];
        this.redoTop = 0;
        
        this.totalOrdersDispatched = 0;
        this.totalDistanceAssigned = 0.0;
    }

    public void addRider(Rider rider) {
        if (riderCount < MAX_RIDERS) {
            riderList[riderCount++] = rider;
            
            // Add to HashMap for fast lookup
            riderMap.put(rider.getId(), rider);
        }
    }

    /**
     * Get rider by ID - Uses CustomHashMap for O(1) access
     */
    public Rider getRider(String id) {
        // Requirement 3: Fast lookup using Custom Hash Map
        return riderMap.get(id);
    }

    public void displayAvailableRiders() {
        System.out.println("--- Available Riders ---");
        boolean found = false;
        for (int i = 0; i < riderCount; i++) {
            if (riderList[i].getStatus() == Rider.RiderStatus.AVAILABLE) {
                System.out.println(riderList[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available riders at the moment.");
        }
    }

    /**
     * Get available riders as an array
     * Returns array and sets count in the provided int array (index 0)
     */
    public Rider[] listAvailableRiders(int[] countOut) {
        Rider[] available = new Rider[riderCount];
        int availableCount = 0;
        for (int i = 0; i < riderCount; i++) {
            if (riderList[i].getStatus() == Rider.RiderStatus.AVAILABLE) {
                available[availableCount++] = riderList[i];
            }
        }
        if (countOut != null && countOut.length > 0) {
            countOut[0] = availableCount;
        }
        return available;
    }

    public void displayAllRiders() {
        System.out.println("\n=== All Riders ===");
        if (riderCount == 0) {
            System.out.println("No riders registered.");
        } else {
            for (int i = 0; i < riderCount; i++) {
                System.out.println(riderList[i].toDetailedString());
            }
        }
        System.out.println("=================\n");
    }

    public int getAvailableRiderCount() {
        int count = 0;
        for (int i = 0; i < riderCount; i++) {
            if (riderList[i].getStatus() == Rider.RiderStatus.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    // ==========================================
    // UPGRADE 1: Weighted Dispatch Algorithm
    // ==========================================

    /**
     * Assign order using weighted optimization strategy
     * Formula: score = (α × distance) + (β × jobsCompleted) − (γ × idleTime/1000)
     * Lower score = better rider selection
     */
    public void assignOrder(OrderSystem orderSystem, CampusMap map) {
        // 1. Check for pending orders
        Order nextOrder = orderSystem.getNextPendingOrder();
        if (nextOrder == null) {
            System.out.println("No pending orders to assign.");
            return;
        }

        // 2. UPGRADE 1: Find BEST rider using weighted scoring
        Rider bestRider = null;
        double bestScore = Double.MAX_VALUE;
        double bestDistToPickup = -1;
        double bestDistToDelivery = -1;

        String pickupLoc = nextOrder.getPickupLocation();
        String deliveryLoc = nextOrder.getDeliveryLocation();

        System.out.println("\n--- Calculating Weighted Scores ---");

        for (int i = 0; i < riderCount; i++) {
            Rider rider = riderList[i];
            if (rider.getStatus() != Rider.RiderStatus.AVAILABLE)
                continue;

            String riderLoc = rider.getLocation();

            // Calculate distance to pickup
            double distToPickup = map.getDistance(riderLoc, pickupLoc);
            if (distToPickup < 0)
                continue; // Invalid path

            double distToDelivery = map.getDistance(pickupLoc, deliveryLoc);
            if (distToDelivery < 0)
                continue; // Invalid path

            // UPGRADE 1: Calculate weighted score
            double score = (ALPHA * distToPickup)
                    + (BETA * rider.getJobsCompleted())
                    - (GAMMA * rider.getIdleTime() / 1000.0);

            System.out.printf("  %s: dist=%.1f, jobs=%d, idle=%.1fs, score=%.2f%n",
                    rider.getName(), distToPickup, rider.getJobsCompleted(),
                    rider.getIdleTimeSeconds(), score);

            if (score < bestScore) {
                bestScore = score;
                bestRider = rider;
                bestDistToPickup = distToPickup;
                bestDistToDelivery = distToDelivery;
            }
        }

        if (bestRider == null) {
            System.out.println("No riders available or no valid paths! Order remains Pending.");
            return;
        }

        // 3. Calculate total distance (validation already done in loop - only valid paths reach here)
        double totalDistance = bestDistToPickup + bestDistToDelivery;

        // 5. Store state for undo (UPGRADE 3)
        String previousRiderLocation = bestRider.getLocation();
        String previousOrderStatus = nextOrder.getStatus();

        // 6. Remove order from Queue (only after validation passes)
        orderSystem.pollPriorityQueue();

        // 7. Update Statuses & LINK RIDER TO ORDER
        bestRider.setStatus(Rider.RiderStatus.DELIVERING);
        bestRider.setCurrentOrderId(nextOrder.getId());
        nextOrder.setStatus("Delivering");

        // 8. UPGRADE 3: Push to undo stack (manual array-based)
        DispatchAction action = new DispatchAction(
                nextOrder.getId(), bestRider.getId(),
                previousRiderLocation, previousOrderStatus, totalDistance);
        if (undoTop < MAX_STACK_SIZE) {
            undoStack[undoTop++] = action;
        }
        redoTop = 0; // Clear redo history on new action

        // 9. UPGRADE 5: Update statistics
        totalOrdersDispatched++;
        totalDistanceAssigned += totalDistance;

        // 10. Display dispatch information
        System.out.println("\n=== Dispatch Success! ===");
        System.out.println("Order ID: " + nextOrder.getId());
        System.out.println("Student: " + nextOrder.getStudentName());
        System.out.println("Assigned Rider: " + bestRider.getName() + " (ID: " + bestRider.getId() + ")");
        System.out.println("Selection Score: " + String.format("%.2f", bestScore));
        System.out.println("\n--- Delivery Route ---");

        // Display path (simplified - shows route summary)
        System.out.println("Route: " + previousRiderLocation + " → " + pickupLoc + " → " + deliveryLoc);

        System.out.println("Distance to Pickup: " + bestDistToPickup + " m");
        System.out.println("Distance to Delivery: " + bestDistToDelivery + " m");
        System.out.println("Total Distance: " + totalDistance + " m");
        System.out.println("========================\n");
    }

    // ==========================================
    // COMPLETE ORDER METHOD
    // ==========================================
    public void completeOrder(String orderID, OrderSystem orderSystem, CampusMap map) {
        // 1. Find the order details
        Order order = orderSystem.searchOrder(orderID);

        if (order == null) {
            System.out.println("Error: Order " + orderID + " not found.");
            return;
        }

        if (!order.getStatus().equals("Delivering")) {
            System.out.println("Error: Order " + orderID + " is not in 'Delivering' status.");
            System.out.println("Current status: " + order.getStatus());
            return;
        }

        // 2. Find which rider has THIS SPECIFIC order
        Rider assignedRider = null;
        for (int i = 0; i < riderCount; i++) {
            Rider r = riderList[i];
            if (r.getStatus() == Rider.RiderStatus.DELIVERING &&
                    r.getCurrentOrderId() != null &&
                    r.getCurrentOrderId().equals(orderID)) {
                assignedRider = r;
                break;
            }
        }

        if (assignedRider == null) {
            System.out.println("Error: No rider found carrying order " + orderID);
            return;
        }

        // 3. Calculate distance for this order (for statistics)
        double distance = map.getDistance(assignedRider.getLocation(), order.getDeliveryLocation());
        if (distance < 0)
            distance = 0;

        // 4. Update rider location and status
        String oldLocation = assignedRider.getLocation();
        assignedRider.setLocation(order.getDeliveryLocation());
        assignedRider.setStatus(Rider.RiderStatus.AVAILABLE);
        assignedRider.setCurrentOrderId(null);

        // UPGRADE 1: Update rider's job completion stats
        assignedRider.completeJob(distance);

        // 5. Update Order status
        order.setStatus("Delivered");

        // 6. Update global statistics with ACTUAL distance
        SystemStatistics.getInstance().recordOrderProcessed(distance);

        // 7. Display completion message
        System.out.println("\n=== Order Completed ===");
        System.out.println("Order ID: " + orderID);
        System.out.println("Student: " + order.getStudentName());
        System.out.println("Delivered by: " + assignedRider.getName() + " (ID: " + assignedRider.getId() + ")");
        System.out.println("Rider moved from: " + oldLocation + " → " + order.getDeliveryLocation());
        System.out.println("Rider status: Available");
        System.out.println("Rider's total jobs: " + assignedRider.getJobsCompleted());
        System.out.println("=======================\n");
    }

    // ==========================================
    // UPGRADE 3: Undo/Redo System
    // ==========================================

    /**
     * Undo the last dispatch action
     */
    public void undoLastDispatch(OrderSystem orderSystem) {
        if (undoTop == 0) {
            System.out.println("Nothing to undo. Undo stack is empty.");
            return;
        }

        DispatchAction action = undoStack[--undoTop];

        // Find the rider
        Rider rider = getRider(action.getRiderId());
        if (rider == null) {
            System.out.println("Error: Rider not found for undo.");
            if (redoTop < MAX_STACK_SIZE) {
                redoStack[redoTop++] = action; // Put it back
            }
            return;
        }

        // Find the order
        Order order = orderSystem.searchOrder(action.getOrderId());
        if (order == null) {
            System.out.println("Error: Order not found for undo.");
            if (redoTop < MAX_STACK_SIZE) {
                redoStack[redoTop++] = action;
            }
            return;
        }

        // Restore rider state
        rider.setLocation(action.getPreviousRiderLocation());
        rider.setStatus(Rider.RiderStatus.AVAILABLE);
        rider.setCurrentOrderId(null);

        // Restore order state
        order.setStatus("Pending");
        orderSystem.readdToPendingQueue(order);

        // Push to redo stack
        if (redoTop < MAX_STACK_SIZE) {
            redoStack[redoTop++] = action;
        }

        // Update statistics
        totalOrdersDispatched--;
        totalDistanceAssigned -= action.getTotalDistance();

        System.out.println("\n=== Undo Successful ===");
        System.out.println("Order " + action.getOrderId() + " has been unassigned.");
        System.out.println("Rider " + rider.getName() + " returned to " + action.getPreviousRiderLocation());
        System.out.println("Order returned to pending queue.");
        System.out.println("=======================\n");
    }

    /**
     * Redo the last undone dispatch action
     */
    public void redoLastDispatch(OrderSystem orderSystem, CampusMap map) {
        if (redoTop == 0) {
            System.out.println("Nothing to redo. Redo stack is empty.");
            return;
        }

        DispatchAction action = redoStack[--redoTop];

        // Find the order in pending queue
        Order order = orderSystem.searchOrder(action.getOrderId());
        if (order == null || !order.getStatus().equals("Pending")) {
            System.out.println("Error: Cannot redo - order not in pending state.");
            return;
        }

        // Find the specific rider
        Rider rider = getRider(action.getRiderId());
        if (rider == null || rider.getStatus() != Rider.RiderStatus.AVAILABLE) {
            System.out.println("Error: Cannot redo - rider not available.");
            return;
        }

        // Remove from pending queue (search and remove)
        orderSystem.pollPriorityQueue();

        // Re-assign
        rider.setStatus(Rider.RiderStatus.DELIVERING);
        rider.setCurrentOrderId(order.getId());
        order.setStatus("Delivering");

        // Push back to undo stack
        if (undoTop < MAX_STACK_SIZE) {
            undoStack[undoTop++] = action;
        }

        // Update statistics
        totalOrdersDispatched++;
        totalDistanceAssigned += action.getTotalDistance();

        System.out.println("\n=== Redo Successful ===");
        System.out.println("Order " + order.getId() + " reassigned to " + rider.getName());
        System.out.println("=======================\n");
    }

    /**
     * Check if undo is possible
     */
    public boolean canUndo() {
        return undoTop > 0;
    }

    /**
     * Check if redo is possible
     */
    public boolean canRedo() {
        return redoTop > 0;
    }

    /**
     * Get undo stack size
     */
    public int getUndoStackSize() {
        return undoTop;
    }

    /**
     * Get redo stack size
     */
    public int getRedoStackSize() {
        return redoTop;
    }

    // ==========================================
    // UPGRADE 2: File Persistence
    // ==========================================

    /**
     * Load riders from file
     * Format: riderID,name,currentLocation
     */
    public void loadRiders(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String riderId = parts[0].trim();
                    String name = parts[1].trim();
                    String location = parts[2].trim();

                    Rider rider = new Rider(riderId, name, location);
                    addRider(rider);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " riders from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Riders file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading riders file: " + e.getMessage());
        }
    }

    /**
     * Save riders to file
     */
    public void saveRiders(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < riderCount; i++) {
                writer.println(riderList[i].toFileFormat());
            }
            System.out.println("Saved " + riderCount + " riders to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving riders: " + e.getMessage());
        }
    }

    // ==========================================
    // UPGRADE 5: Statistics Methods
    // ==========================================

    public int getTotalOrdersDispatched() {
        return totalOrdersDispatched;
    }

    public double getTotalDistanceAssigned() {
        return totalDistanceAssigned;
    }

    public double getAverageDistancePerOrder() {
        if (totalOrdersDispatched == 0)
            return 0.0;
        return totalDistanceAssigned / totalOrdersDispatched;
    }

    public int getTotalRiders() {
        return riderCount;
    }

    /**
     * Display dispatch system statistics
     */
    public void displayDispatchStatistics() {
        System.out.println("\n=== Dispatch System Statistics ===");
        System.out.println("Total Riders: " + riderCount);
        System.out.println("Available Riders: " + getAvailableRiderCount());
        System.out.println("Orders Dispatched: " + totalOrdersDispatched);
        System.out.printf("Total Distance Assigned: %.1f m%n", totalDistanceAssigned);
        System.out.printf("Average Distance per Order: %.1f m%n", getAverageDistancePerOrder());
        System.out.println("Undo Stack Size: " + undoTop);
        System.out.println("Redo Stack Size: " + redoTop);
        System.out.println("==================================\n");
    }

    /**
     * Get all riders as array
     */
    public Rider[] getAllRiders() {
        Rider[] result = new Rider[riderCount];
        for (int i = 0; i < riderCount; i++) {
            result[i] = riderList[i];
        }
        return result;
    }

    /**
     * Get rider count
     */
    public int getRiderCount() {
        return riderCount;
    }
}
