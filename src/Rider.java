/**
 * Rider class representing a delivery rider
 * UPGRADE 1: Added job tracking for weighted dispatch optimization
 */
public class Rider {
    private String riderID;
    private String name;
    private String currentLocation; // Must match a Location Name from Member 1
    private RiderStatus status;
    private String currentOrderId;

    // UPGRADE 1: Job tracking for weighted dispatch
    private int jobsCompleted;
    private long lastCompletedTime;
    private double totalDistanceTraveled;

    public enum RiderStatus {
        AVAILABLE, DELIVERING, OFFLINE
    }

    public Rider(String riderID, String name, String startLocation) {
        this.riderID = riderID;
        this.name = name;
        this.currentLocation = startLocation;
        this.status = RiderStatus.AVAILABLE;
        this.currentOrderId = null;

        // UPGRADE 1: Initialize job tracking
        this.jobsCompleted = 0;
        this.lastCompletedTime = System.currentTimeMillis();
        this.totalDistanceTraveled = 0.0;
    }

    // ==========================================
    // Getters and Setters
    // ==========================================

    public void setLocation(String newLocation) {
        this.currentLocation = newLocation;
    }

    public void setStatus(RiderStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return currentLocation;
    }

    public String getName() {
        return name;
    }

    public RiderStatus getStatus() {
        return status;
    }

    public String getId() {
        return riderID;
    }

    @Override
    public String toString() {
        return String.format("[ID: %s] %s | Loc: %s | Status: %s | Jobs: %d",
                riderID, name, currentLocation, status, jobsCompleted);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentOrderId(String orderId) {
        this.currentOrderId = orderId;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    // ==========================================
    // UPGRADE 1: Weighted Dispatch Methods
    // ==========================================

    /**
     * Get number of jobs completed by this rider
     */
    public int getJobsCompleted() {
        return jobsCompleted;
    }

    /**
     * Get idle time in milliseconds since last job completion
     */
    public long getIdleTime() {
        return System.currentTimeMillis() - lastCompletedTime;
    }

    /**
     * Get idle time in seconds (for display)
     */
    public double getIdleTimeSeconds() {
        return getIdleTime() / 1000.0;
    }

    /**
     * Complete a job and update tracking statistics
     * 
     * @param distance Total distance traveled for this job
     */
    public void completeJob(double distance) {
        jobsCompleted++;
        lastCompletedTime = System.currentTimeMillis();
        totalDistanceTraveled += distance;
    }

    /**
     * Get total distance traveled by this rider
     */
    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    /**
     * Get average distance per job
     */
    public double getAverageJobDistance() {
        if (jobsCompleted == 0)
            return 0.0;
        return totalDistanceTraveled / jobsCompleted;
    }

    /**
     * Format for file saving
     */
    public String toFileFormat() {
        return String.format("%s,%s,%s", riderID, name, currentLocation);
    }

    /**
     * Detailed statistics string
     */
    public String toDetailedString() {
        return String.format("[ID: %s] %s | Loc: %s | Status: %s | Jobs: %d | Total Dist: %.1f km | Idle: %.0f sec",
                riderID, name, currentLocation, status, jobsCompleted, totalDistanceTraveled, getIdleTimeSeconds());
    }
}
