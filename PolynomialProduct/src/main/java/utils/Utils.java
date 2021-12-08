package utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.Polynomial;

public class Utils {

    private static Integer getRandomNumber(Integer min, Integer max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static List<Integer> getRandomCoefficients(Integer maxVal, Integer rank) {
        return IntStream.range(0, rank + 1)
                .map(nr -> getRandomNumber(1, maxVal))
                .boxed().collect(Collectors.toList());
    }

    public static Long pow(Integer number, Integer power) {
        return Double.valueOf(Math.pow(number, power)).longValue();
    }

    public static Long pow(Long number, Integer power) {
        return Double.valueOf(Math.pow(number, power)).longValue();
    }

    public static Boolean isPowerOf(Integer number, Integer of) {
        double value = Math.log(number) / Math.log(of);
        return (int)(Math.ceil(value)) == (int)(Math.floor(value));
    }

    public static Long multiplyArrays(List<Long> l1, List<Long> l2) {
        return l1.stream()
                .map(term -> l2.stream().map(term2 -> term * term2).reduce(0L, Long::sum))
                .reduce(0L, Long::sum);
    }

    public static Integer getMaxDegree(Polynomial p1, Polynomial p2) {
        return Math.max(p1.getRank(), p2.getRank()) + 1;
    }

}
