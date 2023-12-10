package io.github.libfp.threshold;//@date 23.10.2023

/**
 * The <code>IThresholdConfig</code> interface defines the base class for
 * obtaining threshold values for class profiling.
 *
 * <p>
 * Implementations of this interface must provide a method to retrieve the class
 * profile threshold, which is used to determine how classes are included in
 * profiling.
 * </p>
 *
 * <p>
 * The class correspondence threshold typically represents a value above which a
 * correspondence is considered for profiling. The specific threshold value and
 * its meaning may vary depending on the implementation.
 * </p>
 */
public interface IThresholdConfig
{

    default double getThreshold(Class<?> context)
    {
        return 0.0;
    }
}
