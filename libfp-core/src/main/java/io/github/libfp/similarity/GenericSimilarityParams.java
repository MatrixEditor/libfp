package io.github.libfp.similarity; //@date 30.10.2023

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

public final class GenericSimilarityParams<E>
        implements ISimilarityParams
{

    /**
     * A & B
     */
    public final int intersection;

    /**
     * number of different elements
     */
    public final int symDiff;

    /**
     * (A + B) - (A & B)
     */
    public final int union;

    /**
     * elements that belong to A
     */
    public final E[] A;

    /**
     * elements that belong to B
     */
    public final E[] B;

    /**
     * weights for all elements in A
     */
    public final double[] weightA;

    /**
     * weights for all elements in B
     */
    public final double[] weightB;

    public GenericSimilarityParams(
            final @NotNull Collection<E> a,
            final @NotNull Collection<E> b,
            final double[] weighta,
            final double[] weightb,
            IntFunction<E[]> generator
    )
    {
        this(a.toArray(generator), b.toArray(generator), weighta, weightb);
    }

    public GenericSimilarityParams(
            final E[] a,
            final E[] b,
            final double[] weighta,
            final double[] weightb
    )
    {
        this.A = a;
        this.B = b;
        this.weightA = weighta;
        this.weightB = weightb;

        final List<E> lB = Arrays.asList(B);
        final List<E> lA = Arrays.asList(A);

        int intersection = 0;
        int symDiff = 0;
        for (final E eA : A) {
            if (lB.contains(eA)) intersection++;
            if (!lB.contains(eA)) symDiff++;
        }

        for (final E eB : B) {
            if (!lA.contains(eB)) symDiff++;
        }

        this.intersection = intersection;
        this.symDiff = symDiff;
        this.union = (A.length + B.length) - intersection;
    }

    ///////////////////////////////////////////////////////////////////////////
    // implementations
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public double jaccard()
    {
        return intersection * 1.0 / union;
    }
}
