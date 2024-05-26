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

import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Correspondences class is a final implementation of the
 * AbstractCorrespondences class that represents a collection of correspondences
 * between a key element and a list of comparable possibilities.
 *
 * @param <T> The type of elements that are comparable.
 * @apiNote runtime is at best {@code O(1)} and at worst {@code O(N)},
 *         where {@code N} is the number of possibilities. Therefore, if we
 *         compute the correspondences of two sets, the worst runtime is
 *         {@code O(NxM)} and at best {@code O(N)}. Note that, {@code M} and
 *         {@code N} are the number of elements in each set.
 */
public final class Correspondences<T extends IComparable<T>>
        extends AbstractCorrespondences<T>
{

    /**
     * Constructs a Correspondences instance based on the specified key,
     * possibilities, and a similarity threshold configuration.
     *
     * @param key           The key element for which correspondences are being
     *                      determined.
     * @param possibilities The list of possible matches.
     * @param config        The similarity threshold configuration to be used
     *                      for comparison.
     */
    public Correspondences(
            final T key,
            final @NotNull Iterable<T> possibilities,
            @NotNull IThresholdConfig config)
    {
        super(key);
        Map<T, Double> map = new HashMap<>();

        final double threshold = config.getThreshold(key.getClass());
        // Calculate similarity and filter based on the threshold
        for (T profile : possibilities) {
            final double similarity = profile.similarityTo(key, config);
            if (similarity >= threshold) {
                map.put(profile, similarity);
                // REVISIT: add a configuration value that allows to break the
                // loop here.
//                if (similarity == 1.0) {
//                    break;
//                }
            }
        }

        // Initialize the matches and similarities based on the filtered results
        this.matches = new ArrayList<>(map.keySet());
        this.similarities = new ArrayList<>(map.values());
    }
}
