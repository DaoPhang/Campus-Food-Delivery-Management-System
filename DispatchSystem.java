import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DispatchSystem {
    private ArrayList<Rider> riderList;
    private HashMap<String, Rider> riderMap;

    public DispatchSystem() {
        this.riderList = new ArrayList<>();
        this.riderMap = new HashMap<>();
    }

    public void addRider(Rider rider) {
        riderList.add(rider);
        riderMap.put(rider.getId(), rider);
    }

    public Rider getRider(String id) {
        return riderMap.get(id);
    }

    public void displayAvailableRiders() {
        System.out.println("--- Available Riders ---");
        boolean found = false;
        for (Rider r : riderList) {
            if (r.getStatus() == Rider.RiderStatus.AVAILABLE) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available riders at the moment.");
        }
    }

    // Alternative method that returns list (for integration purposes)
    public List<Rider> listAvailableRiders() {
        List<Rider> available = new ArrayList<>();
        for (Rider r : riderList) {
            if (r.getStatus() == Rider.RiderStatus.AVAILABLE) {
                available.add(r);
            }
        }
        return available;
    }

    // Display all riders (useful for menu option "View riders")
    public void displayAllRiders() {
        System.out.println("\n=== All Riders ===");
        if (riderList.isEmpty()) {
            System.out.println("No riders registered.");
        } else {
            for (Rider r : riderList) {
                System.out.println(r);
            }
        }
        System.out.println("=================\n");
    }

    // Get count of available riders (useful for statistics)
    public int getAvailableRiderCount() {
        int count = 0;
        for (Rider r : riderList) {
            if (r.getStatus() == Rider.RiderStatus.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    public void assignOrder(OrderSystem orderSystem, CampusMap map) {
        // 1. Check for pending orders
        Order nextOrder = orderSystem.getNextPendingOrder(); 
        if (nextOrder == null) {
            System.out.println("No pending orders to assign.");
            return;
        }

        // 2. Find an Available Rider
        Rider selectedRider = null;
        for (Rider r : riderList) {
            if (r.getStatus() == Rider.RiderStatus.AVAILABLE) {
                selectedRider = r; 
                break; // Just pick the first available one
            }
        }

        if (selectedRider == null) {
            System.out.println("No riders available! Order remains Pending.");
            return; 
        }

        // 3. Validate locations exist in the graph
        String riderLoc = selectedRider.getLocation();
        String pickupLoc = nextOrder.getPickupLocation();
        String deliveryLoc = nextOrder.getDeliveryLocation();
        
        // Check if locations are valid (you may need to coordinate with Member 1 for this method)
        // For now, we'll assume getDistance returns -1 or throws exception for invalid locations
        
        // 4. Calculate Paths and get actual path sequences
        // NOTE: Coordinate with Member 1 - they should provide:
        // - getShortestPath(from, to) that returns List<String> of locations
        // - getShortestDistance(from, to) that returns double distance
        
        // Try to get paths (if Member 1 implements getShortestPath method)
        List<String> pathToPickup = null;
        List<String> pathToDelivery = null;
        double distToPickup = -1;
        double distToDelivery = -1;
        
        try {
            // Option 1: If Member 1 provides getShortestPath method
            // pathToPickup = map.getShortestPath(riderLoc, pickupLoc);
            // pathToDelivery = map.getShortestPath(pickupLoc, deliveryLoc);
            // distToPickup = map.getShortestDistance(riderLoc, pickupLoc);
            // distToDelivery = map.getShortestDistance(pickupLoc, deliveryLoc);
            
            // Option 2: If Member 1 only provides getDistance (current assumption)
            distToPickup = map.getDistance(riderLoc, pickupLoc);
            distToDelivery = map.getDistance(pickupLoc, deliveryLoc);
            
            // Validate paths exist
            if (distToPickup < 0 || distToDelivery < 0) {
                System.out.println("Error: Invalid path detected. Locations may not exist or be unreachable.");
                System.out.println("Rider Location: " + riderLoc);
                System.out.println("Pickup Location: " + pickupLoc);
                System.out.println("Delivery Location: " + deliveryLoc);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error calculating path: " + e.getMessage());
            return;
        }

        // 5. Remove order from Queue (only after validation passes)
        orderSystem.pollPriorityQueue(); 

        // 6. Update Statuses & LINK RIDER TO ORDER
        selectedRider.setStatus(Rider.RiderStatus.DELIVERING);
        selectedRider.setCurrentOrderId(nextOrder.getId()); 
        nextOrder.setStatus("Delivering");

        // 7. Display dispatch information with path
        System.out.println("\n=== Dispatch Success! ===");
        System.out.println("Order ID: " + nextOrder.getId());
        System.out.println("Student: " + nextOrder.getStudentName());
        System.out.println("Assigned Rider: " + selectedRider.getName() + " (ID: " + selectedRider.getId() + ")");
        System.out.println("\n--- Delivery Route ---");
        
        // Display path if available, otherwise show locations
        if (pathToPickup != null && pathToDelivery != null) {
            // Display full path sequence
            System.out.print("Path: ");
            for (int i = 0; i < pathToPickup.size(); i++) {
                System.out.print(pathToPickup.get(i));
                if (i < pathToPickup.size() - 1) System.out.print(" → ");
            }
            // Skip first element of pathToDelivery since it's the same as last of pathToPickup
            for (int i = 1; i < pathToDelivery.size(); i++) {
                System.out.print(" → " + pathToDelivery.get(i));
            }
            System.out.println();
        } else {
            // Fallback: Show locations if path reconstruction not available
            System.out.println("Route: " + riderLoc + " → " + pickupLoc + " → " + deliveryLoc);
        }
        
        System.out.println("Distance to Pickup: " + distToPickup + " km");
        System.out.println("Distance to Delivery: " + distToDelivery + " km");
        System.out.println("Total Distance: " + (distToPickup + distToDelivery) + " km");
        System.out.println("========================\n");
    }

    // ==========================================
    // COMPLETE ORDER METHOD
    // ==========================================
    public void completeOrder(String orderID, OrderSystem orderSystem) {
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
        for (Rider r : riderList) {
            // Check if rider is Delivering AND matches the Order ID
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
        
        // 3. Update rider location and status
        String oldLocation = assignedRider.getLocation();
        assignedRider.setLocation(order.getDeliveryLocation()); 
        assignedRider.setStatus(Rider.RiderStatus.AVAILABLE);   
        assignedRider.setCurrentOrderId(null); // Clear the link
        
        // 4. Update Order status
        order.setStatus("Delivered");               
        
        // 5. Display completion message
        System.out.println("\n=== Order Completed ===");
        System.out.println("Order ID: " + orderID);
        System.out.println("Student: " + order.getStudentName());
        System.out.println("Delivered by: " + assignedRider.getName() + " (ID: " + assignedRider.getId() + ")");
        System.out.println("Rider moved from: " + oldLocation + " → " + order.getDeliveryLocation());
        System.out.println("Rider status: Available");
        System.out.println("=======================\n");
    }
}