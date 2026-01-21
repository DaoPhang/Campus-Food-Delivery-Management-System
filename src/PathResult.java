public class PathResult {
    private int[] pathNodes;
    private int pathSize;
    private int totalDistance;

    public PathResult(int maxNodes) {
        this.pathNodes = new int[maxNodes];
        this.pathSize = 0;
        this.totalDistance = -1;
    }

    public void addNode(int nodeIndex) {
        if (pathSize < pathNodes.length) {
            pathNodes[pathSize++] = nodeIndex;
        }
    }

    public int getNode(int index) {
        return pathNodes[index];
    }

    public int getPathSize() {
        return pathSize;
    }

    public void setTotalDistance(int dist) {
        this.totalDistance = dist;
    }

    public int getTotalDistance() {
        return totalDistance;
    }
}
