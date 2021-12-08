package processors;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import config.PropertiesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Polynomial;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractJobProcessor implements JobProcessor {
    protected final Polynomial p1;
    protected final Polynomial p2;
    protected final Integer nrThreads = PropertiesLoader.getNrThreads();

    @Override
    public Long getResultIterative() {
        return calculateDuration(this::calculateIterative);
    }

    @Override
    public Long getResultMultiThreaded() {
        return calculateDuration(this::calculateMultiThreaded);
    }

    private Long calculateDuration(Supplier<Long> supplier) {
        Instant start = Instant.now();
        Long result = supplier.get();
        Instant end = Instant.now();
        log.info("Duration in milis: {}", Duration.between(start, end).toMillis());
        return result;
    }

    protected abstract Long calculateIterative();

    protected abstract Long calculateMultiThreaded();

    public void close() {}
}
