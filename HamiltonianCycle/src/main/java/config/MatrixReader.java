package config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import model.DirectedGraph;

@Slf4j
public class MatrixReader {
    private static final String fileName = PropertiesLoader.getMatrixFile();

    public static DirectedGraph readGraph() {
        try {
            return new DirectedGraph(
                    Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8)
                        .stream()
                        .map(line -> {
                            List<String> values = Arrays.asList(line.split(" "));
                            return values.stream().map(Integer::parseInt).collect(Collectors.toList());
                        })
                        .collect(Collectors.toList())
            );
        } catch (IOException ioe) {
            log.error("Error while reading from graph file");
            throw new RuntimeException();
        }
    }
}
