package processors.root;

import java.time.Duration;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import model.Polynomial;
import processors.JobProcessor;
import utils.Utils;

@Slf4j
public abstract class AbstractRootProcessor implements JobProcessor {
    protected final Polynomial p1;
    protected final Polynomial p2;

    public AbstractRootProcessor(Polynomial p1, Polynomial p2) {
        Utils.validatePolynomials(p1, p2);
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Long calculate() {
        Instant start = Instant.now();
        Long result = doProcess();
        Instant end = Instant.now();
        log.info("Duration in milis: {}", Duration.between(start, end).toMillis());
        return result;
    }

    protected abstract Long doProcess();
}
