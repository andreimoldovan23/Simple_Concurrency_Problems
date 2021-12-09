package processors;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import Utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.DirectedGraph;
import model.Task;

@RequiredArgsConstructor
@Slf4j
public class Runner {
    private final DirectedGraph graph;

    public void run() {
        Instant start = Instant.now();
        String result = invokeOnAllVertices();
        Instant end = Instant.now();
        log.info("Duration in milis: {}", Duration.between(start, end).toMillis());

        if (result != null) log.info("{}", result);
        else log.info("No Hamiltonian cycle present");
    }

    private String invokeOnAllVertices() {
        List<ForkJoinTask<String>> results = graph.getVertices().stream()
                .map(v -> new Task(graph, List.of(v)).fork())
                .collect(Collectors.toList());

        return Utils.getFirstOrNull(results);
    }
}
