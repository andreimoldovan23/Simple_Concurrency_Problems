package utils;

import config.PropertiesLoader;

public class NumberGenerator {
    public static Integer get() {
        int min = PropertiesLoader.getMinMatrixValue();
        int max = PropertiesLoader.getMaxMatrixValue();
        return (int) ((Math.random() * (max - min)) + min);
    }
}
