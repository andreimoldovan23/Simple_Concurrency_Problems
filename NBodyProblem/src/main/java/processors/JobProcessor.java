package processors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import config.PropertiesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Body;

@Slf4j
@RequiredArgsConstructor
public class JobProcessor {
    private final FileProcessor fileProcessor;
    private final ExecutorService executor = Executors.newFixedThreadPool(PropertiesLoader.getNrThreads());

    public void run() {
        int numberBodies = fileProcessor.getNumberBodies();

        List<Body> bodies = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(PropertiesLoader.getNrTasks());
        CountDownLatch exitLatch = new CountDownLatch(PropertiesLoader.getNrTasks());

        IntStream.range(0, PropertiesLoader.getNrTasks())
                .forEach(y -> {
                    int stepSize = numberBodies / PropertiesLoader.getNrTasks();
                    int startIndex = y * stepSize;
                    int endIndex = startIndex + stepSize;
                    WorkerThread thread = new WorkerThread(bodies, startIndex, endIndex, fileProcessor, latch, exitLatch);
                    executor.execute(thread);
                });

        close(exitLatch);
    }

    private void close(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                    throw new RuntimeException();
                }
            }
        } catch (InterruptedException ie) {
            log.error("Error while shutting down executor service: {}", ie.getMessage());
            throw new RuntimeException();
        }
    }

}
