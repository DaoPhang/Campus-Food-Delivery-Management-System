public class Rider {
    private String riderID;
    private String name;
    private String currentLocation; // Must match a Location Name from Member 1
    private RiderStatus status;
    private String currentOrderId;


    public enum RiderStatus {
        AVAILABLE, DELIVERING, OFFLINE
    }

    public Rider(String riderID, String name, String startLocation) {
        this.riderID = riderID;
        this.name = name;
        this.currentLocation = startLocation;
        this.status = RiderStatus.AVAILABLE;
    }

    // Getters and Setters are crucial here
    public void setLocation(String newLocation) { this.currentLocation = newLocation; }
    public void setStatus(RiderStatus status) { this.status = status; }
    public String getLocation() { return currentLocation; }
    public String getName() { return name; }
    public RiderStatus getStatus() { return status; }
    public String getId() { return riderID; }

    @Override
    public String toString() {
        return String.format("[ID: %s] %s | Loc: %s | Status: %s", riderID, name, currentLocation, status);
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setCurrentOrderId(String orderId) {
        this.currentOrderId = orderId;
    }
    
    public String getCurrentOrderId() {
        return currentOrderId;
    }
    
}