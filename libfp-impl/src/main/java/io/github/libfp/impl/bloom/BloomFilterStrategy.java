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
package io.github.libfp.impl.bloom;

import io.github.libfp.cha.IDescriptorContainer;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code BloomFilterStrategy} class is an abstract implementation of the
 * {@link ISimilarityStrategy} interface for profiles that extend
 * {@link ExtensibleProfile} and implement {@link IDescriptorContainer}. It
 * provides a method to calculate the similarity between two profiles based on
 * their Bloom filters.
 * <p>
 * The
 * {@link #similarityOf(ExtensibleProfile, ExtensibleProfile, IThresholdConfig)}
 * method compares the descriptors of the provided profiles. If the descriptors
 * are equal, it retrieves the Bloom filters from the profiles and checks if the
 * Bloom filter of the first profile is a superset of the second profile's Bloom
 * filter. If it is, it calculates the Jaccard similarity using binary vectors
 * obtained from the Bloom filters.
 * </p>
 *
 * @param <T> The type of profiles that extend {@link ExtensibleProfile} and
 *            implement {@link IDescriptorContainer}.
 */
public abstract class BloomFilterStrategy<T extends ExtensibleProfile & IDescriptorContainer>
        implements ISimilarityStrategy<T>
{

    /**
     * Calculates the similarity between two profiles based on their Bloom
     * filters.
     *
     * @param app    The application profile.
     * @param lib    The library profile.
     * @param config The threshold configuration.
     * @return The similarity between the profiles, ranging from 0 to 1.
     */
    @Override
    public double similarityOf(
            @NotNull T app,
            @NotNull T lib,
            IThresholdConfig config)
    {
        BloomFilter appFilter = Bloom.getFilter(app);
        BloomFilter libFilter = Bloom.getFilter(lib);

        // Check if the Bloom filter of the first profile is a superset of
        // the second profile's Bloom filter
        if (appFilter.isSuperSetOf(libFilter)) {
            // Calculate and return the Jaccard similarity
            return libFilter.getOverlapRatio(appFilter);
        }
        return 0;
    }
}
