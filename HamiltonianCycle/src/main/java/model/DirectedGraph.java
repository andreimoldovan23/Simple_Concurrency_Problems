package model;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DirectedGraph {
    private final List<List<Integer>> graph;

    private final AtomicBoolean hasCycle = new AtomicBoolean(false);

    public Boolean hasCycle() {
        return hasCycle.get();
    }

    public void setCycle(Boolean b) {
        hasCycle.set(b);
    }

    public boolean isEdge(Integer x, Integer y) {
        return graph.get(x).get(y).equals(1);
    }

    public List<Integer> getVertices() {
        return IntStream.range(0, graph.size()).boxed().collect(Collectors.toList());
    }

    public Integer getNumberVertices() {
        return graph.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n");

        for (int i = 0; i < graph.size(); i++) {
            sb.append(i).append(" goes into: ");
            for (int j = 0; j < graph.get(i).size(); j++) {
                if (graph.get(i).get(j).equals(1))
                    sb.append(j).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
