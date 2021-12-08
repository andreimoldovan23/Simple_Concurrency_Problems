package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import config.PropertiesLoader;
import utils.NumberGenerator;

public class Matrix {
    private final List<List<Integer>> matrix;

    public Matrix(Boolean randomValues) {
        int size = PropertiesLoader.getMatrixSize();
        matrix = new ArrayList<>();
        IntStream.range(0, size)
                .forEach(nr -> {
                    Integer[] intArray = new Integer[size];
                    if (!randomValues) {
                        Arrays.fill(intArray, 0);
                    } else {
                        for (int i = 0; i < size; i++) {
                            intArray[i] = NumberGenerator.get();
                        }
                    }
                    matrix.add(Arrays.asList(intArray));
                });
    }

    public Integer get(Integer x, Integer y) {
        return matrix.get(x).get(y);
    }

    public void set(Integer x, Integer y, Integer val) {
        matrix.get(x).set(y, val);
    }

    @Override
    public String toString() {
        return "Matrix is:\n" + matrix.stream()
                .map(line -> line.stream().map(String::valueOf)
                    .reduce("", (a, b) -> a + " " + b))
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
