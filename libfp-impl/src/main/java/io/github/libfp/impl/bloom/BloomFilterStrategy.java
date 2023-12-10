package io.github.libfp.impl.bloom; //@date 12.11.2023

import io.github.libfp.cha.IDescriptorContainer;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.similarity.BinarySimilarityParams;
import io.github.libfp.similarity.ISimilarityParams;
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
     *
     * @return The similarity between the profiles, ranging from 0 to 1.
     */
    @Override
    public double similarityOf(
            @NotNull T app,
            @NotNull T lib,
            IThresholdConfig config)
    {
        if (!app.getDescriptor().equals(lib.getDescriptor())) {
            return 0;
        }

        BloomFilter appFilter = Bloom.getFilter(app);
        BloomFilter libFilter = Bloom.getFilter(lib);

        // Check if the Bloom filter of the first profile is a superset of
        // the second profile's Bloom filter
        if (appFilter.isSuperSetOf(libFilter)) {
            ISimilarityParams p = BinarySimilarityParams.of(
                    appFilter.toBinaryVector(),
                    libFilter.toBinaryVector()
            );
            // Calculate and return the Jaccard similarity
            return p.jaccard();
        }
        return 0;
    }
}
