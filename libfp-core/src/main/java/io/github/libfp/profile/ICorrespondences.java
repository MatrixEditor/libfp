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
package io.github.libfp.profile;

import io.github.libfp.matching.AbstractGraphMatching;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

/**
 * The ICorrespondences interface represents a collection of correspondences or
 * matches between a key element and a list of possibilities, each of which is
 * comparable.
 * <p>
 * It is recommended to use this class together with
 * {@link AbstractGraphMatching}, because the result of this class
 *
 * @param <T> The type of elements that are comparable.
 */
public sealed interface ICorrespondences<T extends IComparable<T>>
        extends Iterable<T>
        permits AbstractCorrespondences
{

    /**
     * Create an instance of ICorrespondences using the specified key,
     * possibilities, and a similarity threshold configuration.
     *
     * @param <E>           The type of elements that are comparable.
     * @param key           The key element for which correspondences are being
     *                      determined.
     * @param possibilities The list of possible matches.
     * @param config        The similarity threshold configuration to be used
     *                      for comparison.
     * @return An instance of ICorrespondences.
     */
    static <E extends IComparable<E>> @NotNull ICorrespondences<E> of(
            @NotNull E key,
            @NotNull Iterable<E> possibilities,
            @NotNull IThresholdConfig config)
    {
        return new Correspondences<>(key, possibilities, config);
    }

    /**
     * Get the key element for which correspondences are being determined.
     *
     * @return The key element.
     */
    T key();

    /**
     * Get the count of correspondences in the collection.
     *
     * @return The count of correspondences.
     */
    int count();

    /**
     * Get the similarity score at the specified index in the collection of
     * correspondences.
     *
     * @param index The index at which to retrieve the similarity score.
     * @return The similarity score at the specified index.
     */

    double getSimilarityAt(final int index);

    /**
     * Get the matching element at the specified index in the collection of
     * correspondences.
     *
     * @param index The index at which to retrieve the matching element.
     * @return The matching element at the specified index.
     */

    T getMatchAt(final int index);

    /**
     * Get the highest similarity score among all correspondences.
     *
     * @return The highest similarity score, or 0 if the collection is empty.
     */
    default double getHighestScore()
    {
        if (isEmpty()) {
            return 0;
        }

        return IntStream
                .range(0, count())
                .mapToDouble(this::getSimilarityAt)
                .max()
                .orElse(0);
    }

    /**
     * Check if the collection of correspondences is empty.
     *
     * @return true if the collection is empty, false otherwise.
     */
    default boolean isEmpty()
    {
        return count() == 0;
    }

    /**
     * Get the best matching element with the highest similarity score.
     *
     * @return The best matching element, or null if the collection is empty.
     */
    default @Nullable T getBestMatch()
    {
        if (isEmpty()) {
            return null;
        }

        double highestScore = 0.0d;
        int index = 0;
        for (int i = 0; i < count(); i++) {
            if (getSimilarityAt(i) > highestScore) {
                highestScore = getSimilarityAt(i);
                index = i;
            }
        }
        return getMatchAt(index);
    }

    /**
     * Factory interface for creating instances of ICorrespondences.
     *
     * @param <T> The type of elements that are comparable.
     */
    interface Factory<T extends IComparable<T>>
    {
        ICorrespondences<T> newInstance(
                T key, Iterable<T> possibilities, IThresholdConfig config);
    }
}
