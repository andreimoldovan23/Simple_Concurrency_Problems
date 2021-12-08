package processors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import utils.Utils;

@Slf4j
public class ClassicMultiplicationImpl extends AbstractJobProcessor {
    private final ExecutorService executorService = Executors.newFixedThreadPool(nrThreads);

    public ClassicMultiplicationImpl(Polynomial p1, Polynomial p2) {
        super(p1, p2);
    }

    @Override
    protected Long calculateIterative() {
        return Utils.multiplyArrays(p1.getListOfTerms(), p2.getListOfTerms());
    }

    @Override
    protected Long calculateMultiThreaded() {
        List<Long> termsInFirst = p1.getListOfTerms();
        List<Long> termsInSecond = p2.getListOfTerms();

        List<Future<Long>> futures = IntStream.range(0, nrThreads)
                .mapToObj(nr -> executorService.submit(new Task(termsInFirst, termsInSecond, nr)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Thread was interrupted while returning future");
                    }
                }).reduce(0L, Long::sum);
    }

    @Override
    public void close() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                    throw new RuntimeException();
                }
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException("Error while shutting down executor service");
        }
    }

    @RequiredArgsConstructor
    private static class Task implements Callable<Long> {
        private final List<Long> first;
        private final List<Long> second;
        private final Integer index;

        private static List<Long> getSliceFrom(List<Long> array, Integer var) {
            int extra = array.size() % 2;
            int start = array.size() / 2 * var;
            int end = start + array.size() / 2 + extra;
            return array.subList(start, end);
        }

        @Override
        public Long call() {
            List<Long> sliceFromFirst = getSliceFrom(first, index / 2);
            List<Long> sliceFromSecond = getSliceFrom(second, index % 2);
            return Utils.multiplyArrays(sliceFromFirst, sliceFromSecond);
        }
    }


}
