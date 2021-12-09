import config.MatrixReader;
import lombok.extern.slf4j.Slf4j;
import model.DirectedGraph;
import processors.Runner;

@Slf4j
public class Main {
    public static void main(String[] args) {
        DirectedGraph g = MatrixReader.readGraph();
        log.info("{}", g.toString());

        Runner runner = new Runner(g);
        runner.run();
    }
}
