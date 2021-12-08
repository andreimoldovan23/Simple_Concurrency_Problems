package processors;

public interface JobProcessor {
    Long getResultIterative();
    Long getResultMultiThreaded();
    void close();
}
