package processors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import config.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import model.Body;
import model.Vector;

@Slf4j
public class FileProcessor {
    private final List<String> lines;
    private final double gravitationalConstant;
    private final int numberBodies, timeStep;

    public FileProcessor() {
        try {

            Path path = Paths.get(PropertiesLoader.getInputFileName());
            lines = Files.readAllLines(path);

            List<String> tokens = Arrays.asList(lines.get(0).split(" "));

            gravitationalConstant = Double.parseDouble(tokens.get(0));
            numberBodies = Integer.parseInt(tokens.get(1));
            timeStep = Integer.parseInt(tokens.get(2));

            log.info("GC - {}, Number Bodies - {}, Time Steps - {}", gravitationalConstant, numberBodies, timeStep);

        } catch (IOException ioe) {

            log.error("Error reading system values from file {}", PropertiesLoader.getInputFileName());
            throw new RuntimeException();

        } catch (NullPointerException | NumberFormatException e) {

            log.error("Invalid system values in input file {}", PropertiesLoader.getInputFileName());
            throw new RuntimeException();

        }
    }

    public double getGC() { return gravitationalConstant; }

    public int getNumberBodies() { return numberBodies; }

    public int getTimeStep() { return timeStep; }

    private List<Vector> getPositions(Integer start, Integer end) {
        int startIndex = lines.indexOf("#positions") + 1 + start;
        List<String> posLines = lines.subList(startIndex, startIndex + end - start);
        return posLines.stream()
                .map(this::decompose).collect(Collectors.toList());
    }

    private List<Vector> getVelocities(Integer start, Integer end) {
        int startIndex = lines.indexOf("#velocities") + 1 + start;
        List<String> velLines = lines.subList(startIndex, startIndex + end - start);
        return velLines.stream()
                .map(this::decompose).collect(Collectors.toList());
    }

    private List<Double> getMasses(Integer start, Integer end) {
        int startIndex = lines.indexOf("#masses") + 1 + start;
        List<String> massLines = lines.subList(startIndex, startIndex + end - start);
        return massLines.stream()
                .map(Double::parseDouble).collect(Collectors.toList());
    }

    public List<Body> getBodies(Integer startIndex, Integer endIndex) {
        try {
            List<Vector> poz = getPositions(startIndex, endIndex);
            List<Vector> vel = getVelocities(startIndex, endIndex);
            List<Double> mas = getMasses(startIndex, endIndex);

            return IntStream.range(0, endIndex - startIndex)
                    .mapToObj(x -> Body.builder()
                            .mass(mas.get(x))
                            .position(poz.get(x))
                            .velocity(vel.get(x))
                            .acceleration(new Vector(0.00, 0.00, 0.00))
                            .build())
                    .collect(Collectors.toList());
        } catch (NullPointerException | NumberFormatException e) {
            log.error("Invalid values for bodies in input file {}", PropertiesLoader.getInputFileName());
            throw new RuntimeException();
        }
    }

    private Vector decompose(String line) {
        String[] xyz = line.split(" ");
        double x = Double.parseDouble(xyz[0]);
        double y = Double.parseDouble(xyz[1]);
        double z = Double.parseDouble(xyz[2]);
        return new Vector(x, y, z);
    }

}
