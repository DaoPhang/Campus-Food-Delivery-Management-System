/**
 * Order class representing a food delivery order
 * Implements Comparable for priority queue ordering
 */
public class Order implements Comparable<Order> {
    private String orderId;
    private String studentName;
    private String pickupLocation;
    private String deliveryLocation;
    private int priority; // Lower number = higher priority
    private String status; // Pending, Delivering, Delivered, Cancelled
    private long timestamp;
    
    public Order(String orderId, String studentName, String pickupLocation, 
                 String deliveryLocation, int priority) {
        this.orderId = orderId;
        this.studentName = studentName;
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.priority = priority;
        this.status = "Pending";
        this.timestamp = System.nanoTime();
    }

    // Constructor with status (for file loading)
    public Order(String orderId, String studentName, String pickupLocation, 
                 String deliveryLocation, int priority, String status) {
        this.orderId = orderId;
        this.studentName = studentName;
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.priority = priority;
        this.status = status;
        this.timestamp = System.nanoTime();
    }

    // Getters
    public String getId() { return orderId; }
    public String getStudentName() { return studentName; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDeliveryLocation() { return deliveryLocation; }
    public int getPriority() { return priority; }
    public String getStatus() { return status; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public int compareTo(Order other) {
        if (this.priority != other.priority) {
            return Integer.compare(this.priority, other.priority);
        }
        // If priorities are same, compare time (First Come First Served)
        return Long.compare(this.timestamp, other.timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("[Order: %s] %s | From: %s → To: %s | Priority: %d | Status: %s",
                orderId, studentName, pickupLocation, deliveryLocation, priority, status);
    }

    // Format for file saving
    public String toFileFormat() {
        return String.format("%s,%s,%s,%s,%d,%s",
                orderId, studentName, pickupLocation, deliveryLocation, priority, status);
    }
}
