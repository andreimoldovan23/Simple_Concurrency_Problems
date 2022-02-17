import java.time.Duration;
import java.time.Instant;


import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import processors.FileProcessor;
import processors.JobProcessor;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Program started");

        log.info("Properties are: number of threads - {}, number of tasks - {}, input file - {}",
                PropertiesLoader.getNrThreads(), PropertiesLoader.getNrTasks(), PropertiesLoader.getInputFileName());

        try {
            FileProcessor fileProcessor = new FileProcessor();
            JobProcessor jobProcessor = new JobProcessor(fileProcessor);

            Instant start = Instant.now();
            jobProcessor.run();
            Instant end = Instant.now();
            log.info("Duration in milis: {}", Duration.between(start, end).toMillis());
        } catch (RuntimeException re) {
            log.error("System exit due beacasue of \n{}", re.getMessage());
            System.exit(-1);
        }

        log.info("Program ended");
    }
}
