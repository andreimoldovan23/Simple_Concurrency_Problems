import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import mpi.MPI;
import processors.JobProcessor;
import processors.root.RootProcessorClassicImpl;
import processors.root.RootProcessorKaratsubaImpl;
import processors.workers.WorkerProcessorClassicImpl;
import processors.workers.WorkerProcessorKaratsubaImpl;

@Slf4j
public class Main {
    private static final String KARATSUBA = "karatsuba";
    private static final String CLASSIC = "classic";

    private static void runRoot() {
        Polynomial p1 = new Polynomial("P0", PropertiesLoader.getValue());
        Polynomial p2 = new Polynomial("P0", PropertiesLoader.getValue());
//        log.info("{}", p1.toString());
//        log.info("{}", p2.toString());

        JobProcessor jb;
        String approach = PropertiesLoader.getApproach();
        if (approach.equals(CLASSIC)) jb = new RootProcessorClassicImpl(p1, p2);
        else if (approach.equals(KARATSUBA)) jb = new RootProcessorKaratsubaImpl(p1, p2);
        else throw new RuntimeException("Invalid approach chosen");

        log.info("Approach: {}", approach);

        Long result = jb.calculate();
        log.info("Final result {}", result);
    }

    private static void runWorker(int rank) {
        JobProcessor jb;
        String approach = PropertiesLoader.getApproach();
        if (approach.equals(CLASSIC)) jb = new WorkerProcessorClassicImpl(rank);
        else if (approach.equals(KARATSUBA)) jb = new WorkerProcessorKaratsubaImpl(rank);
        else throw new RuntimeException("Invalid approach chosen");

        jb.calculate();
    }

    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();

        try {
            if (rank == 0) runRoot();
            else runWorker(rank);
        } catch (RuntimeException re) {
            log.error("An error has occured in {}", rank);
            re.printStackTrace();
        }

        MPI.Finalize();
    }
}
