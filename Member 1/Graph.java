import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjacencyList;
    private final Map<String, Location> locationInfoMap = new HashMap<>();

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }
    
    public void addLocation(String name, Location loc) {
        adjacencyList.putIfAbsent(name, new ArrayList<>());
        locationInfoMap.put(name, loc);
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
        for (String name : adjacencyList.keySet()) {
            Location loc = locationInfoMap.get(name);
            String details = (loc != null) ? " (" + loc.getFacultyorDorm() + " - " + loc.getBlock() + ")" : "";
            System.out.print(name + details + " -> ");
        
            List<Edge> edges = adjacencyList.get(name);
            for (Edge edge : edges) {
                System.out.print("[" + edge.getToNode() + ", " + edge.getWeight() + "] ");
            }
            System.out.println();
        }
    }

    public List<String> getShortestPath(String start, String end) {   
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            return Collections.emptyList();
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();

        for (String location : adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(new NodeDistance(start, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentNode = current.node;

            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            if (currentNode.equals(end)) {
                break;
            }

            for (Edge edge : adjacencyList.get(currentNode)) {
                String neighbor = edge.getToNode();
                if (visited.contains(neighbor)) continue;

                int newDist = distances.get(currentNode) + edge.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newDist));
                }
            }
        }

        return reconstructPath(previousNodes, start, end);
    }

    private List<String> reconstructPath(Map<String, String> previousNodes, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        String step = end;

        if (previousNodes.get(step) == null && !step.equals(start)) {
            return path;
        }

        while (step != null) {
            path.addFirst(step);
            step = previousNodes.get(step);
        }
        return path;
    }

    public List<String> getDeliveryRoute(String riderLoc, String restaurant, String customerLoc) {
        List<String> firstLeg = getShortestPath(riderLoc, restaurant);
        List<String> secondLeg = getShortestPath(restaurant, customerLoc);

        if (firstLeg.isEmpty() || secondLeg.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> fullPath = new ArrayList<>(firstLeg);
        fullPath.addAll(secondLeg.subList(1, secondLeg.size()));
        return fullPath;
    }
}
