package io.github.libfp.benchmark;//@date 01.11.2023

import io.github.libfp.profile.Profile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the result of a test, including information about the test suite,
 * the application profile, the library profile, execution times, similarity
 * score, and status of the test.
 */
@ApiStatus.Experimental
public record TestResult(
        // The test suite associated with this result.
        @NotNull TestSuite<?> suite,

        // The application profile used in the test.
        @NotNull Profile<?> app,

        // The library profile used in the test.
        @NotNull Profile<?> lib,

        // Execution time in nanoseconds.
        long nanoTime,

        // Execution time in milliseconds.
        long milliTime,

        // Similarity score of the test result.
        double similarity,

        // The status of the test, either Success or Failure.
        @NotNull Status status
)
{
    /**
     * Defines the possible statuses of a test result.
     */
    public static abstract class Status
    {
    }

    /**
     * Represents a successful test result status.
     */
    public static final class Success extends Status
    {
    }

    /**
     * Represents a failed test result status.
     */
    public static final class Failure extends Status
    {
        public final Throwable cause;

        /**
         * Constructs a Failure status with an associated cause (exception).
         *
         * @param cause The exception that caused the test to fail.
         */
        public Failure(Throwable cause)
        {
            this.cause = cause;
        }
    }

    @Override
    public String toString()
    {
        return "TestResult{" +
                "app=" + app +
                ", lib=" + lib +
                ", nanoTime=" + nanoTime +
                ", milliTime=" + milliTime +
                ", similarity=" + similarity +
                ", status=" + status +
                '}';
    }
}
