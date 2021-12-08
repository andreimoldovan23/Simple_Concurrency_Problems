package processors;

import java.util.concurrent.ForkJoinPool;

import lombok.extern.slf4j.Slf4j;
import model.KAlgoAsync;
import model.Polynomial;
import utils.Utils;

@Slf4j
public class KaratsubaMultiplicationImpl extends AbstractJobProcessor {
    private final ForkJoinPool threadPool = ForkJoinPool.commonPool();

    public KaratsubaMultiplicationImpl(Polynomial p1, Polynomial p2) {
        super(p1, p2);
        if (!p1.getRank().equals(p2.getRank())) throw new RuntimeException("Polynomials must have the same size");
        if (!Utils.isPowerOf(p1.getRank() + 1, 2)) throw new RuntimeException("Polynomial size must be a power of 2");
    }

    @Override
    protected Long calculateIterative() {
        return KAlgo.multiply(p1, p2);
    }

    @Override
    protected Long calculateMultiThreaded() {
        return threadPool.invoke(new KAlgoAsync(p1, p2));
    }

    private static class KAlgo {
        private static Long getResultIterative(Polynomial upperHalf1, Polynomial lowerHalf1, Polynomial upperHalf2, Polynomial lowerHalf2,
                                               Integer maxDegree) {
            Long d0 = KAlgo.multiply(lowerHalf1, lowerHalf2);
            Long d1 = KAlgo.multiply(upperHalf1, upperHalf2);
            Long d01 = KAlgo.multiply(Polynomial.add(lowerHalf1, upperHalf1), Polynomial.add(lowerHalf2, upperHalf2));
            return d1 * Utils.pow(upperHalf1.getValue(), maxDegree) +
                    (d01 - d0 - d1) * Utils.pow(upperHalf1.getValue(), maxDegree / 2) +
                    d0;
        }

        public static Long multiply(Polynomial p1, Polynomial p2) {
            Integer maxDegree = Utils.getMaxDegree(p1, p2);
            if (maxDegree == 1) return Utils.multiplyArrays(p1.getListOfTerms(), p2.getListOfTerms());

            Polynomial p1Upper = p1.getUpperHalf(), p1Lower = p1.getLowerHalf(), p2Upper = p2.getUpperHalf(), p2Lower = p2.getLowerHalf();

            return getResultIterative(p1Upper, p1Lower, p2Upper, p2Lower, maxDegree);
        }
    }

}
