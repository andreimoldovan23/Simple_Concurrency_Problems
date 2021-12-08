package model;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import lombok.RequiredArgsConstructor;
import utils.Utils;

@RequiredArgsConstructor
public class KAlgoAsync extends RecursiveTask<Long> {
    private final Polynomial p1;
    private final Polynomial p2;

    private Long calculateAsync(Polynomial lowerHalf1, Polynomial upperHalf1, Polynomial lowerHalf2, Polynomial upperHalf2, Integer maxDegree) {
        ForkJoinTask<Long> f0 = new KAlgoAsync(lowerHalf1, lowerHalf2).fork();
        ForkJoinTask<Long> f1 = new KAlgoAsync(upperHalf1, upperHalf2).fork();
        ForkJoinTask<Long> f01 = new KAlgoAsync(Polynomial.add(lowerHalf1, upperHalf1), Polynomial.add(lowerHalf2, upperHalf2)).fork();

        Long d0 = f0.join();
        Long d1 = f1.join();
        Long d01 = f01.join();

        return d1 * Utils.pow(upperHalf1.getValue(), maxDegree) +
                (d01 - d0 - d1) * Utils.pow(upperHalf1.getValue(), maxDegree / 2) +
                d0;
    }

    @Override
    protected Long compute() {
        Integer maxDegree = Utils.getMaxDegree(p1, p2);
        if (maxDegree == 1) return Utils.multiplyArrays(p1.getListOfTerms(), p2.getListOfTerms());

        Polynomial p1Upper = p1.getUpperHalf(), p1Lower = p1.getLowerHalf(), p2Upper = p2.getUpperHalf(), p2Lower = p2.getLowerHalf();

        return calculateAsync(p1Upper, p1Lower, p2Upper, p2Lower, maxDegree);
    }
}
