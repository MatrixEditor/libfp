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
package io.github.libfp.benchmark;

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
        @NotNull ITestSuite suite,

        // The application profile used in the test.
        @NotNull Profile app,

        // The library profile used in the test.
        @NotNull Profile lib,

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
}
