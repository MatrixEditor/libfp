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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

@Deprecated
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

}
