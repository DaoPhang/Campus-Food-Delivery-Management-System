import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }
    
    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addEdge(String from, String to, int distance) {
        if(distance <= 0) {
            System.out.println("Distance must be a positive integer.");
            return;
        }
        if(!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            throw new IllegalArgumentException("Both locations must exist in the graph.");
        }
        adjacencyList.get(from).add(new Edge(to, distance));
        adjacencyList.get(to).add(new Edge(from, distance)); // For undirected graph
    }

    public void displayGraph() {
        for (String location : adjacencyList.keySet()) {
            System.out.print(location + " -> ");
            List<Edge> edges = adjacencyList.get(location);
            for (Edge edge : edges) {
                System.out.print("[" + edge.getToNode() + ", " + edge.getWeight() + "] ");
            }
            System.out.println();
        }
    }
}
