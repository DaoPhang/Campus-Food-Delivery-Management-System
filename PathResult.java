import java.util.List;
import java.util.ArrayList;

/**
 * PathResult stores cached path computation results
 * Used for HashMap caching optimization in Graph
 */
public class PathResult {
    private final int distance;
    private final List<String> path;

    public PathResult(int distance, List<String> path) {
        this.distance = distance;
        this.path = new ArrayList<>(path); // Defensive copy
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getPath() {
        return new ArrayList<>(path); // Return copy to prevent modification
    }

    public boolean hasPath() {
        return !path.isEmpty();
    }

    @Override
    public String toString() {
        return "PathResult{distance=" + distance + ", path=" + path + "}";
    }
}
