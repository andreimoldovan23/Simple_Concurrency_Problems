package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

import Utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Task extends RecursiveTask<String> {
    private final DirectedGraph graph;
    private final List<Integer> path;

    private boolean isSafe(Integer vertex) {
        Integer lastVertexInPath = path.get(path.size() - 1);
        return graph.isEdge(lastVertexInPath, vertex) && !path.contains(vertex);
    }

    @Override
    protected String compute() {
        if(graph.hasCycle()) return null;

        Integer lastVertexInPath = path.get(path.size() - 1);
        if (path.size() == graph.getNumberVertices() && graph.isEdge(lastVertexInPath, path.get(0))) {
            graph.setCycle(true);
            path.add(path.get(0));
            String pathStr = path.stream().map(Object::toString).collect(Collectors.joining(", "));
            return String.format("Found Hamiltonian cycle\n%s", pathStr);
        } else if (path.size() == graph.getNumberVertices()) {
            return null;
        }

        List<ForkJoinTask<String>> subtasks = graph.getVertices().stream()
                .filter(this::isSafe)
                .map(v -> {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(v);
                    return new Task(graph, newPath).fork();
                })
                .collect(Collectors.toList());

        return Utils.getFirstOrNull(subtasks);
    }
}
