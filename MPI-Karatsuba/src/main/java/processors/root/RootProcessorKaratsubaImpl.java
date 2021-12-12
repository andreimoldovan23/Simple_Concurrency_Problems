package processors.root;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import mpi.MPI;
import utils.Utils;

@Slf4j
public class RootProcessorKaratsubaImpl extends AbstractRootProcessor {
    private Boolean isFirstCall = true;

    public RootProcessorKaratsubaImpl(Polynomial p1, Polynomial p2) {
        super(p1, p2);
    }

    @Override
    protected Long doProcess() {
        return multiply(p1, p2);
    }

    private Long multiply(Polynomial p1, Polynomial p2) {
        Integer maxDegree = Utils.getMaxDegree(p1, p2);
        if (maxDegree == 1) return Utils.multiplyArrays(p1.getListOfTerms(), p2.getListOfTerms());

        Polynomial p1Upper = p1.getUpperHalf(), p1Lower = p1.getLowerHalf(), p2Upper = p2.getUpperHalf(), p2Lower = p2.getLowerHalf();

        return isFirstCall ?
                getResultWithWorkers(p1Upper, p1Lower, p2Upper, p2Lower, maxDegree) :
                getResult(p1Upper, p1Lower, p2Upper, p2Lower, maxDegree);
    }

    private Long getResult(Polynomial upperHalf1, Polynomial lowerHalf1, Polynomial upperHalf2, Polynomial lowerHalf2, Integer maxDegree) {
        Long d0 = multiply(lowerHalf1, lowerHalf2);
        Long d1 = multiply(upperHalf1, upperHalf2);
        Long d01 = multiply(Polynomial.add(lowerHalf1, upperHalf1), Polynomial.add(lowerHalf2, upperHalf2));
        return d1 * Utils.pow(upperHalf1.getValue(), maxDegree) +
                (d01 - d0 - d1) * Utils.pow(upperHalf1.getValue(), maxDegree / 2) + d0;
    }

    private Long getResultWithWorkers(Polynomial upperHalf1, Polynomial lowerHalf1, Polynomial upperHalf2, Polynomial lowerHalf2, Integer maxDegree) {
        isFirstCall = false;

        log.info("Sending polynomial halves to workers");
        sendToWorker(lowerHalf1, lowerHalf2, 1);
        sendToWorker(upperHalf1, upperHalf2, 2);

        log.info("Calculating own workload");
        Long d01 = multiply(Polynomial.add(lowerHalf1, upperHalf1), Polynomial.add(lowerHalf2, upperHalf2));

        log.info("Receiving data from workers");
        Long d0 = receiveFromWorker(1);
        Long d1 = receiveFromWorker(2);

        return d1 * Utils.pow(upperHalf1.getValue(), maxDegree) +
                (d01 - d0 - d1) * Utils.pow(upperHalf1.getValue(), maxDegree / 2) + d0;
    }

    private void sendToWorker(Polynomial p1, Polynomial p2, Integer workerRank) {
        List<Integer> serialized = p1.serializeAsArray();
        serialized.addAll(p2.serializeAsArray());
        int[] array = serialized.stream().mapToInt(x -> x).toArray();
        CompletableFuture.runAsync(() -> MPI.COMM_WORLD.Send(array, 0, serialized.size(), MPI.INT, workerRank, workerRank + 10));
    }

    private Long receiveFromWorker(Integer workerRank) {
        long[] array = new long[1];
        MPI.COMM_WORLD.Recv(array, 0, 1, MPI.LONG, workerRank, workerRank);
        return array[0];
    }
}
