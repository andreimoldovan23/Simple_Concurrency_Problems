package processors.workers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import config.PropertiesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import mpi.MPI;
import processors.JobProcessor;
import utils.Utils;

@RequiredArgsConstructor
@Slf4j
public class WorkerProcessorKaratsubaImpl implements JobProcessor {
    private final Integer rank;

    @Override
    public Long calculate() {
        log.info("Receiving data from root in {}", rank);
        int size = ((PropertiesLoader.getRank() + 1) / 2 + 2) * 2;
        int[] array = new int[size];

        MPI.COMM_WORLD.Recv(array, 0, size, MPI.INT, 0, rank + 10);

        log.info("Processing own workload in {}", rank);
        List<Integer> terms = Arrays.stream(array)
                .boxed().collect(Collectors.toList());

        Polynomial p1 = constructFrom(terms.subList(0, terms.size() / 2));
        Polynomial p2 = constructFrom(terms.subList(terms.size() / 2, terms.size()));

        long[] results = new long[1];
        results[0] = multiply(p1, p2);

        log.info("Sending result to root from {}", rank);
        MPI.COMM_WORLD.Send(results, 0, 1, MPI.LONG, 0, rank);
        return null;
    }

    private Polynomial constructFrom(List<Integer> elems) {
        Integer value = elems.get(0);
        Integer polynomialRank = elems.get(1);
        List<Integer> coefficients = elems.subList(2, elems.size());
        return new Polynomial("P" + rank, value, polynomialRank, coefficients);
    }

    private Long multiply(Polynomial p1, Polynomial p2) {
        Integer maxDegree = Utils.getMaxDegree(p1, p2);
        if (maxDegree == 1) return Utils.multiplyArrays(p1.getListOfTerms(), p2.getListOfTerms());

        Polynomial p1Upper = p1.getUpperHalf(), p1Lower = p1.getLowerHalf(), p2Upper = p2.getUpperHalf(), p2Lower = p2.getLowerHalf();

        return getResult(p1Upper, p1Lower, p2Upper, p2Lower, maxDegree);
    }

    private Long getResult(Polynomial upperHalf1, Polynomial lowerHalf1, Polynomial upperHalf2, Polynomial lowerHalf2, Integer maxDegree) {
        Long d0 = multiply(lowerHalf1, lowerHalf2);
        Long d1 = multiply(upperHalf1, upperHalf2);
        Long d01 = multiply(Polynomial.add(lowerHalf1, upperHalf1), Polynomial.add(lowerHalf2, upperHalf2));
        return d1 * Utils.pow(upperHalf1.getValue(), maxDegree) +
                (d01 - d0 - d1) * Utils.pow(upperHalf1.getValue(), maxDegree / 2) + d0;
    }
}
