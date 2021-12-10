package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import config.PropertiesLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.Utils;

@Getter
@Slf4j
@AllArgsConstructor
public class Polynomial {
    private final String name;
    private final Integer value;
    private final Integer rank;
    private final List<Integer> coefficients;

    public Polynomial(String name, Integer value) {
        this.name = name;
        this.value = value;
        rank = PropertiesLoader.getRank();
        coefficients = Utils.getRandomCoefficients(PropertiesLoader.getMaxCof(), rank);
        log.info("Polynomial created: {}, rank: {}", name, rank);
    }

    public List<Long> getListOfTerms() {
        AtomicReference<Integer> size = new AtomicReference<>(rank);
        return coefficients.stream()
                .map(cof -> cof * Utils.pow(value, size.getAndUpdate(x -> x - 1)))
                .collect(Collectors.toList());
    }

    public Polynomial getUpperHalf() {
        return getHalf(0, (getRank() + 1) / 2);
    }

    public Polynomial getLowerHalf() {
        return getHalf((getRank() + 1) / 2, coefficients.size());
    }

    private Polynomial getHalf(Integer startIndex, Integer endIndex) {
        List<Integer> half = coefficients.subList(startIndex, endIndex);
        return new Polynomial(this.name, this.value, this.rank / 2, half);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name. %s. Value of X: %s. ", name, value));
        sb.append("Polynomial form: ");

        for (int i = 0; i < rank + 1; i++) {
            sb.append(coefficients.get(i)).append("x^").append(rank - i).append(" + ");
        }

        sb.delete(sb.length() - 3, sb.length());
        return sb.toString();
    }

    public static Polynomial add(Polynomial p1, Polynomial p2) {
        if (!p1.getRank().equals(p2.getRank()) || !p1.getValue().equals(p2.getValue()))
            throw new RuntimeException("Incompatible addition between polynomials");

        List<Integer> result = new ArrayList<>(), coef1 = p1.getCoefficients(), coef2 = p2.getCoefficients();
        for (int i = 0; i < p1.getRank() + 1; i++) {
            result.add(coef1.get(i) + coef2.get(i));
        }

        return new Polynomial("SUM", p1.getValue(), p1.getRank(), result);
    }

}
