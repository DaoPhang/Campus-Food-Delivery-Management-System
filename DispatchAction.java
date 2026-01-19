/**
 * DispatchAction stores the state of a dispatch operation for undo/redo
 * UPGRADE 3: Undo/Redo System
 */
public class DispatchAction {
    private final String orderId;
    private final String riderId;
    private final String previousRiderLocation;
    private final String previousOrderStatus;
    private final double totalDistance;

    public DispatchAction(String orderId, String riderId, String previousRiderLocation,
            String previousOrderStatus, double totalDistance) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.previousRiderLocation = previousRiderLocation;
        this.previousOrderStatus = previousOrderStatus;
        this.totalDistance = totalDistance;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getPreviousRiderLocation() {
        return previousRiderLocation;
    }

    public String getPreviousOrderStatus() {
        return previousOrderStatus;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        return String.format("DispatchAction{orderId='%s', riderId='%s', prevLoc='%s', distance=%.1f}",
                orderId, riderId, previousRiderLocation, totalDistance);
    }
}
