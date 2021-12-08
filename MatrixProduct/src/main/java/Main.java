import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import model.Matrix;
import model.Worker;

@Slf4j
public class Main {
    private static final Matrix m1 = new Matrix(true);
    private static final Matrix m2 = new Matrix(true);
    private static final Matrix result = new Matrix(false);

    private static void useNormalThreads() throws InterruptedException {
        log.trace("Using normal threads to compute sum");

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < PropertiesLoader.getNumberTasks(); i++) {
            Thread thread = new Thread(new Worker(m1, m2, result, i));
            threads.add(thread);
            thread.start();
        }

        for (int i = 0; i < PropertiesLoader.getNumberTasks(); i++) {
            threads.get(i).join();
        }
    }

    private static void safeShutdown(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.error("Pool did not terminate");
                throw new RuntimeException();
            }
        }
    }

    private static void useThreadPool() throws InterruptedException {
        log.trace("Using thread pool to compute sum");

        ExecutorService executorService = Executors.newFixedThreadPool(PropertiesLoader.getThreadPoolSize());
        for (int i = 0; i < PropertiesLoader.getNumberTasks(); i++) {
            executorService.submit(new Worker(m1, m2, result, i));
        }

        safeShutdown(executorService);
    }

    public static void main(String[] args) throws InterruptedException {
        PropertiesLoader.printProperties();

        System.out.println("#########################################\n");
        System.out.println(m1);
        System.out.println("\n#########################################\n");
        System.out.println(m2);
        System.out.println("\n#########################################\n");

        Instant start = Instant.now();

        if (PropertiesLoader.isSimpleThreadsApproach())
            useNormalThreads();
        else if (PropertiesLoader.isThreadPoolApproach())
            useThreadPool();
        else {
            log.error("No approach chosen");
            throw new RuntimeException();
        }

        Instant end = Instant.now();

        System.out.println("Result is:\n");
        System.out.println(result);
        System.out.println("\n");

        log.trace("Time taken in ms - {}", Duration.between(start, end).toMillis());
    }
}
