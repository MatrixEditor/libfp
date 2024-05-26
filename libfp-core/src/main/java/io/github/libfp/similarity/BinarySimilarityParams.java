/*
 * MIT License
 *
 * Copyright (c) 2024 MatrixEditor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.libfp.similarity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * The <code>BinarySimilarityParams</code> class represents a set of parameters
 * for calculating binary similarity measures between two sets of binary
 * values.
 *
 * <p>
 * This class includes parameters such as element count, binary values, and
 * weights for positive and negative binary values. It provides methods to
 * compute binary similarity measures based on these parameters.
 * </p>
 */
@Deprecated
public final class BinarySimilarityParams
        implements ISimilarityParams
{

    /**
     * The number of elements in the binary sets.
     */
    public final int m;

    /**
     * The array of binary values for the first set.
     */
    public final int @NotNull [] i;

    /**
     * The array of binary values for the second set.
     */
    public final int @NotNull [] j;

    /**
     * The array of weights for positive binary values in the first set.
     */
    public final int @NotNull [] wP;

    /**
     * The array of weights for negative binary values in the second set.
     */
    public final int @NotNull [] wN;

    /**
     * The number of occurrences where both sets have a positive binary value.
     */
    public int a;

    /**
     * The number of occurrences where the first set is negative, and the second
     * set is positive.
     */
    public int b;

    /**
     * The number of occurrences where the first set is positive, and the second
     * set is negative.
     */
    public int c;

    /**
     * The number of occurrences where both sets have negative binary values.
     */
    public int d;

    /**
     * Constructs a new <code>BinarySimilarityParams</code> object based on two
     * arrays of binary values.
     *
     * @param i The array of binary values for the first set.
     * @param j The array of binary values for the second set.
     * @throws IndexOutOfBoundsException If the array lengths are inconsistent.
     */
    public BinarySimilarityParams(
            final int @NotNull [] i,
            final int @NotNull [] j)
            throws IndexOutOfBoundsException
    {
        this(i, j, null, null);
    }

    /**
     * Constructs a new <code>BinarySimilarityParams</code> object based on two
     * arrays of binary values and optional weight arrays.
     *
     * @param i  The array of binary values for the first set.
     * @param j  The array of binary values for the second set.
     * @param wP The array of weights for positive binary values in the first
     *           set.
     * @param wN The array of weights for negative binary values in the second
     *           set.
     * @throws IndexOutOfBoundsException If the array lengths are inconsistent.
     */
    public BinarySimilarityParams(
            final int @NotNull [] i,
            final int @NotNull [] j,
            final int @Nullable [] wP,
            final int @Nullable [] wN)
            throws IndexOutOfBoundsException
    {
        this.m = i.length;
        this.i = i;
        this.j = j;

        this.wP = wP == null ? new int[i.length] : wP;
        this.wN = wN == null ? new int[j.length] : wN;
        if (wP == null) Arrays.fill(this.wP, 1);
        if (wN == null) Arrays.fill(this.wN, 1);

        this.a = b = c = d = 0;
        for (int k = 0; k < i.length; k++) {
            final int E_i = i[k];
            final int nE_i = E_i == 1 ? 0 : 1;
            final int E_j = j[k];
            final int nE_j = E_j == 1 ? 0 : 1;

            a += E_i & E_j;
            b += nE_i & E_j;
            c += E_i & nE_j;
            d += nE_i & nE_j;
        }
    }

    /**
     * Creates a <code>BinarySimilarityParams</code> object based on two arrays
     * of boolean values.
     *
     * @param x The array of boolean values for the first set.
     * @param y The array of boolean values for the second set.
     * @return A <code>BinarySimilarityParams</code> object created from boolean
     *         arrays.
     */
    public static @NotNull BinarySimilarityParams of(
            final boolean @NotNull [] x,
            final boolean @NotNull [] y)
    {
        final int[] i = new int[x.length], j = new int[y.length];
        for (int k = 0; k < i.length; k++) {
            i[k] = x[k] ? 1 : 0;
            j[k] = y[k] ? 1 : 0;
        }
        return new BinarySimilarityParams(i, j);
    }

    public static <U, V> @NotNull BinarySimilarityParams of(
            final @NotNull Collection<U> x,
            final @NotNull Collection<V> y,
            @NotNull Function<U, Integer> xMapper,
            @NotNull Function<V, Integer> yMapper
    )
    {
        assert x.size() == y.size() : "Invalid length!";
        final int[] i = new int[x.size()], j = new int[y.size()];

        Iterator<U> xI = x.iterator();
        Iterator<V> yI = y.iterator();
        for (int k = 0; k < i.length; k++) {
            U xT = xI.next();
            V yT = yI.next();
            i[k] = xMapper.apply(xT);
            j[k] = yMapper.apply(yT);
        }
        return new BinarySimilarityParams(i, j);
    }

    /**
     * Computes the sum of values a, b, c, and d.
     *
     * @return The sum of values a, b, c, and d.
     */
    public int n()
    {
        return a + b + c + d;
    }

    ///////////////////////////////////////////////////////////////////////////
    // implementations
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the Jaccard Similarity based on the given binary similarity
     * parameters.
     *
     * @return The Jaccard Similarity.
     */
    public double jaccard()
    {
        return (a * 1.0) / (a + b + c);
    }

    /**
     * Calculates the Dice Similarity based on the given binary similarity
     * parameters.
     *
     * @return The Dice Similarity.
     */
    public double dice()
    {
        return (a * 2.0) / (2.0 * a + b + c);
    }

    /**
     * Calculates the Sokal-Sneath 1 Similarity based on the given binary
     * similarity parameters.
     *
     * @return The Sokal-Sneath 1 Similarity.
     */
    public double sokalSneath1()
    {
        return (double) (a) / (a + b * 2.0 + c * 2.0);
    }

    /**
     * Calculates the Euclidean Distance based on the given binary similarity
     * parameters.
     *
     * @return The Euclidean Distance.
     */
    public double euclid()
    {
        return Math.sqrt(b + c);
    }

    /**
     * Calculates the Ample Similarity based on the given binary similarity
     * parameters.
     *
     * @return The Ample Similarity.
     */
    public double ample()
    {
        return (a * (c + d) * 1.0) / (c * (a + b));
    }

    /**
     * Calculates the Hamming Distance based on the given binary similarity
     * parameters.
     *
     * @return The Hamming Distance.
     */
    public double hamming()
    {
        return b + c;
    }
}
