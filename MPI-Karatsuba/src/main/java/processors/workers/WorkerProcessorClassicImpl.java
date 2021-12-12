package processors.workers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import config.PropertiesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mpi.MPI;
import processors.JobProcessor;
import utils.Utils;

@RequiredArgsConstructor
@Slf4j
public class WorkerProcessorClassicImpl implements JobProcessor {
    private final Integer rank;

    @Override
    public Long calculate() {
        log.info("Receiving data from root in {}", rank);
        long[] array = new long[PropertiesLoader.getRank() + 1];
        MPI.COMM_WORLD.Recv(array, 0, PropertiesLoader.getRank() + 1, MPI.LONG, 0, rank + 10);
        List<Long> terms = Arrays.stream(array)
                .boxed().collect(Collectors.toList());

        log.info("Processing own workload in {}", rank);
        int size = terms.size();
        long[] results = { Utils.multiplyArrays(terms.subList(0, size / 2), terms.subList(size / 2, size)) };

        log.info("Sending result to root from {}", rank);
        MPI.COMM_WORLD.Send(results, 0, 1, MPI.LONG, 0, rank);
        return null;
    }
}
