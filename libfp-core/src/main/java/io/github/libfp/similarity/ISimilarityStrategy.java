package io.github.libfp.similarity;//@date 24.10.2023

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
     *
     * @return A double value representing the similarity between the two
     *         objects.
     */
    double similarityOf(T app, T lib, final IThresholdConfig config);

}
