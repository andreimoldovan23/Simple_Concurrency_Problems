package model;

import config.PropertiesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Worker implements Runnable {
    private final Matrix m1;
    private final Matrix m2;
    private final Matrix result;
    private final Integer taskNumber;
    private final Integer size = PropertiesLoader.getMatrixSize();

    @Override
    public void run() {
        int step = PropertiesLoader.getNumberTasks();
        for (int i = taskNumber % size; i < size * size; i += step) {
            int x = i / size;
            int y = i % size;
            int rez = calculateElement(x, y);
            result.set(x, y, rez);
        }
    }

    private Integer calculateElement(Integer x, Integer y) {
        log.trace("Calculating element on row {} column {} in thread {}", x, y, taskNumber);
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += m1.get(x, i) * m2.get(i, y);
        }
        return sum;
    }
}
