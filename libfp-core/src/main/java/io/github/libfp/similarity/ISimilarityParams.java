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
package io.github.libfp.similarity;//
/**
 * This class provides a collection of static methods for calculating binary
 * similarity measures based on given {@link ISimilarityParams}.
 *
 * <p>
 * These similarity measures are commonly used to assess the similarity between
 * two sets of binary data, where binary values represent the presence or
 * absence of certain attributes or features. The class includes methods to
 * calculate various similarity measures, and each method takes a set of
 * parameters in the form of {@link BinarySimilarityParams} for the
 * calculation.
 * </p>
 *
 * <p>
 * The class includes methods for the following similarity measures:
 * </p>
 *
 * <ul>
 * <li><a href="https://www.ibm.com/docs/en/spss-statistics/27.0.0?topic=measures-distances-similarity-binary-data">
 * Jaccard Similarity</a>: An index where joint absences are excluded from
 * consideration, and equal
 * weight is given to matches and non-matches.</li>
 *
 * <li>Dice Similarity: An index similar to Jaccard Similarity but with
 * double weight
 * for matches.</li>
 *
 * <li>Sokal-Sneath 1 Similarity: An index with double weight for matches.</li>
 *
 * <li>Euclidean Distance: A measure based on the Euclidean distance between
 * binary vectors.</li>
 *
 * <li>Ample Similarity: A measure that takes into account both presence and
 * absence of attributes.</li>
 *
 * <li>Hamming Distance: A measure based on the Hamming distance, which is
 * the count of differing
 * positions between binary vectors.</li>
 * </ul>
 *
 * <p>
 * For more details and usage, refer to the provided links and references.
 * </p>
 */
@Deprecated
public interface ISimilarityParams
{

    /**
     * Calculates the Jaccard Similarity based on the given similarity
     * parameters.
     *
     * @return The Jaccard Similarity.
     */
    default double jaccard()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the Dice Similarity based on the given similarity parameters.
     *
     * @return The Dice Similarity.
     */
    default double dice()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the Sokal-Sneath 1 Similarity based on the given binary
     * similarity parameters.
     *
     * @return The Sokal-Sneath 1 Similarity.
     */
    default double sokalSneath1()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the Euclidean Distance based on the given similarity
     * parameters.
     *
     * @return The Euclidean Distance.
     */
    default double euclid()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the Ample Similarity based on the given similarity
     * parameters.
     *
     * @return The Ample Similarity.
     */
    default double ample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the Hamming Distance based on the given similarity
     * parameters.
     *
     * @return The Hamming Distance.
     */
    default double hamming()
    {
        throw new UnsupportedOperationException();
    }

}
