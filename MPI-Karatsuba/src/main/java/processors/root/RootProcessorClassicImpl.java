package processors.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import mpi.MPI;
import utils.Utils;

@Slf4j
public class RootProcessorClassicImpl extends AbstractRootProcessor {
    private final List<Long> polynomialTerms1 = new ArrayList<>();
    private final List<Long> polynomialTerms2 = new ArrayList<>();

    public RootProcessorClassicImpl(Polynomial p1, Polynomial p2) {
        super(p1, p2);
    }

    protected Long doProcess() {
        sendDataToWorkers();
        long[] results = new long[PropertiesLoader.getNumberWorkers() + 1];

        log.info("Processing own workload");
        results[0] = Utils.multiplyArrays(polynomialTerms1, polynomialTerms2);

        log.info("Receiving results from workers");
        IntStream.range(1, PropertiesLoader.getNumberWorkers() + 1)
                .forEach(nr -> MPI.COMM_WORLD.Recv(results, nr, 1, MPI.LONG, nr, nr));

        return Arrays.stream(results).reduce(0L, Long::sum);
    }

    private void sendDataToWorkers() {
        log.info("Sending terms to workers");

        List<Long> terms1 = p1.getListOfTerms();
        List<Long> terms2 = p2.getListOfTerms();

        IntStream.range(1, PropertiesLoader.getNumberWorkers() + 1)
                .forEach(nr -> CompletableFuture.runAsync(() -> {
                    List<Long> allTerms = new ArrayList<>();
                    allTerms.addAll(Utils.getSliceFrom(terms1, nr / 2));
                    allTerms.addAll(Utils.getSliceFrom(terms2, nr % 2));
                    MPI.COMM_WORLD.Send(allTerms.stream().mapToLong(x -> x).toArray(), 0, allTerms.size(), MPI.LONG, nr, nr + 10);
                }));

        polynomialTerms1.addAll(Utils.getSliceFrom(terms1, 0));
        polynomialTerms2.addAll(Utils.getSliceFrom(terms2, 0));
    }

}
