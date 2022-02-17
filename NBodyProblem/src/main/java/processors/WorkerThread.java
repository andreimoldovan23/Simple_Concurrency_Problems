package processors;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Body;
import model.Vector;

@Slf4j
@RequiredArgsConstructor
public class WorkerThread implements Runnable {
    private Double gravitationalConstant;

    private final List<Body> bodies;
    private final Integer startIndex, endIndex;
    private final FileProcessor fileProcessor;

    private final CountDownLatch latch;
    private final CountDownLatch exitLatch;

    private final Vector origin = new Vector(0.00, 0.00, 0.00);

    private Vector getNormalizedValue(Vector initial, Vector v1, Vector v2, Vector v3, Double m1, Double m2) {
        if (!v1.equals(origin)) return v1;
        Vector result = initial.plus(v2).plus(v3.times(m1 - m2));
        if ((v2.equals(origin) && v3.equals(origin)) || result.equals(origin))
            return new Vector(1.00, 1.00, 0.00);
        return result;
    }

    private void computeAccelerations() {
        log.info("Computing acceleration for bodies from {} to {}", startIndex, endIndex);

        for (int i = startIndex; i < endIndex; i++) {
            Body body = bodies.get(i);
            body.setAcceleration(origin);

            bodies.forEach(aux -> {
                if (!aux.equals(body)) {
                    Vector pozDiff = body.getPosition().minus(aux.getPosition());
                    Vector velocityDiff = body.getVelocity().minus(aux.getVelocity());
                    Vector accDiff = body.getAcceleration().minus(aux.getAcceleration());

                    Vector diff = getNormalizedValue(body.getPosition(), pozDiff, velocityDiff, accDiff, body.getMass(), aux.getMass());

                    double temp = gravitationalConstant * aux.getMass() / Math.pow(diff.mod(), 3);

                    Vector newAcceleration = diff.inverse().times(temp);
                    body.setAcceleration(body.getAcceleration().plus(newAcceleration));
                }
            });
        }
    }

    private void computeVelocities() {
        log.info("Computing velocities for bodies from {} to {}", startIndex, endIndex);

        for (int i = startIndex; i < endIndex; i++) {
            Body body = bodies.get(i);
            body.setVelocity(body.getVelocity().plus(body.getAcceleration()));
        }
    }

    private void computePositions() {
        log.info("Computing positions for bodies from {} to {}", startIndex, endIndex);

        for (int i = startIndex; i < endIndex; i++) {
            Body body = bodies.get(i);
            Vector acc = body.getAcceleration();
            Vector vel = body.getVelocity();
            Vector pos = body.getPosition();

            body.setPosition(pos.plus(vel).plus(acc.times(0.5)));
        }
    }

    private void resolveCollisions() {
        log.info("Resolving collisions for bodies from {} to {}", startIndex, endIndex);

        for (int i = startIndex; i < endIndex; ++i) {
            for (int j = i + 1; j < bodies.size(); ++j) {
                Vector pos1 = bodies.get(i).getPosition();
                Vector pos2 = bodies.get(j).getPosition();

                if (pos1.equals(pos2)) {
                    Vector vel1 = bodies.get(i).getVelocity();
                    Vector vel2 = bodies.get(j).getVelocity();
                    Vector temp = new Vector(vel1);
                    bodies.get(i).setVelocity(vel2);
                    bodies.get(j).setVelocity(temp);
                }
            }
        }
    }

    private void readInput() {
        gravitationalConstant = fileProcessor.getGC();
        List<Body> threadLocalBodies = fileProcessor.getBodies(startIndex, endIndex);

        synchronized (bodies) {
            bodies.addAll(threadLocalBodies);
        }

        latch.countDown();
    }

    private void printResults(Integer cycle) {
        bodies.subList(startIndex, endIndex).forEach(body -> log.info("Cycle {}: {}", cycle, body.toString()));
    }

    @Override
    public void run() {
        log.info("Reading input");
        readInput();

        try {
            log.info("Waiting for latch");
            latch.await();
        } catch (InterruptedException e) {
            log.error("Error encountered while awaiting countdown: {}", e.getMessage());
            throw new RuntimeException();
        }

        IntStream.range(0, fileProcessor.getTimeStep())
                .forEach(x -> {
                    computeAccelerations();
                    computePositions();
                    computeVelocities();
                    resolveCollisions();
                    printResults(x);
                });

        exitLatch.countDown();
    }
}
