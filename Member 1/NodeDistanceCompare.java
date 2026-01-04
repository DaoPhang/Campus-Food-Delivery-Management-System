class NodeDistance implements Comparable<NodeDistance> {
    String node;
    int dist;

    public NodeDistance(String node, int dist) {
        this.node = node;
        this.dist = dist;
    }

    @Override
    public int compareTo(NodeDistance other) {
        return Integer.compare(this.dist, other.dist);
    }
}