package io.github.libfp.profile;//@date 21.10.2023

import io.github.libfp.threshold.IThresholdConfig;

/**
 * The <code>IComparable</code> interface defines a simple way for objects that
 * to compare itself to another object of type {@code <T>} to calculate their
 * similarity.
 *
 * <p>
 * Implementations of this interface must provide a method to calculate the
 * similarity of the object to another object of type {@code <T>} based on a
 * given configuration defined by an {@link IThresholdConfig}.
 * </p>
 *
 * <p>
 * The similarity calculation may vary depending on the implementation and the
 * nature of the objects being compared. The resulting similarity score is
 * typically a double value.
 * </p>
 *
 * @param <T> the type objects of this class can be compared to
 */
public interface IComparable<T>
{

    /**
     * Calculates the similarity of the object to another object of type <T>.
     *
     * @param other  The object of type <T> to compare to.
     * @param config The configuration defining the similarity threshold.
     *
     * @return A double value representing the similarity of the object to the
     *         other object.
     */
    double similarityTo(final T other, IThresholdConfig config);
}

