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

import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.threshold.IThresholdConfig;

/**
 * The <code>ISimilarityStrategy</code> interface defines the base for
 * calculating the similarity between two objects of type <T>.
 *
 * <p>
 * Implementations of this interface must provide a method to calculate the
 * similarity between two objects, 'app' and 'lib', based on a given
 * configuration defined by an {@link IThresholdConfig}.
 * </p>
 *
 * <p>
 * The similarity calculation may vary depending on the implementation and the
 * nature of the objects being compared. The resulting similarity score is
 * typically a double value.
 * </p>
 *
 * @param <T> typically a subclass of {@link ManagedProfile}
 */
public interface ISimilarityStrategy<T>
{

    /**
     * Calculates the similarity between two objects of type <T>.
     *
     * @param app    The first object for similarity comparison.
     * @param lib    The second object for similarity comparison.
     * @param config The configuration defining the similarity threshold.
     * @return A double value representing the similarity between the two
     *         objects.
     */
    double similarityOf(T app, T lib, final IThresholdConfig config);

}
