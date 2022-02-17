import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import processors.ClassicMultiplicationImpl;
import processors.JobProcessor;
import processors.KaratsubaMultiplicationImpl;

@Slf4j
public class Main {
    private static final String CLASSIC_APPROACH = "classic";
    private static final String KARATSUBA_APPROACH = "karatsuba";
    private static final String ITERATIVE_APPROACH = "iterative";
    private static final String MULTI_THREADED_APPROACH = "multithreaded";

    private static JobProcessor getProcessor(Polynomial p1, Polynomial p2) {
        String approach = PropertiesLoader.getApproach();
        log.info("Algorithm is: {}", approach);
        if (approach.equals(CLASSIC_APPROACH)) return new ClassicMultiplicationImpl(p1, p2);
        else if (approach.equals(KARATSUBA_APPROACH)) return new KaratsubaMultiplicationImpl(p1, p2);
        else throw new RuntimeException("Invalid algorithm");
    }

    private static Long runAlgorithm(JobProcessor jb) {
        String approach = PropertiesLoader.getThreadsApproach();
        log.info("Threads option is: {}", approach);
        if (approach.equals(ITERATIVE_APPROACH)) return jb.getResultIterative();
        else if (approach.equals(MULTI_THREADED_APPROACH)) return jb.getResultMultiThreaded();
        else throw new RuntimeException("Invalid threads option");
    }

    public static void main(String[] args) {
        Polynomial p1 = new Polynomial("P(X)", PropertiesLoader.getXValue());
        Polynomial p2 = new Polynomial("F(X)", PropertiesLoader.getXValue());
        log.info("P1 {}", p1.toString());
        log.info("P2 {}", p2.toString());

        try {
            JobProcessor jb = getProcessor(p1, p2);
            Long result = runAlgorithm(jb);
            jb.close();
            log.info("Final result is: {}", result);
        } catch (RuntimeException re) {
            log.error(re.getMessage());
            System.exit(1);
        }
    }
}
