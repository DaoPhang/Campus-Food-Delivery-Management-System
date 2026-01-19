public class Edge {
    private final String destination;
    private final int distance;

    public Edge(String destination, int distance) {
        this.destination = destination;
        this.distance = distance;
    }

    public String getToNode() {
        return destination;
    }

    public int getWeight() {
        return distance;
    }
}
