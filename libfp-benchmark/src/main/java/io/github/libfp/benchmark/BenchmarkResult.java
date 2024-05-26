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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * A class representing benchmark results for various application types. It
 * provides methods to calculate test accuracy metrics, such as true positives,
 * false positives, true negatives, and false negatives, based on user-defined
 * thresholds and a {@link Whitelist}.
 */
@ApiStatus.Experimental
public class BenchmarkResult
{
    public static final String defaultAppType = "";

    private final Map<String, List<TestResult>> results;

    /**
     * Constructs a BenchmarkResult with a map of test results for different
     * application types.
     *
     * @param results A map of test results, where keys represent application
     *                types and values are arrays of TestResult.
     */
    public BenchmarkResult(Map<String, List<TestResult>> results)
    {
        this.results = results;
    }

    public Map<String, List<TestResult>> getTests()
    {
        return results;
    }

    /**
     * Calculates the test accuracy for a specific application type using a
     * threshold and a {@link Whitelist}.
     *
     * @param appType   The application type for which test accuracy is
     *                  calculated.
     * @param threshold The similarity threshold used to classify test results.
     * @param whitelist A {@link Whitelist} for filtering test results.
     * @return A TestAccuracy object containing true positives, false positives,
     *         true negatives, and false negatives.
     */
    @Contract("_, _, _ -> new")
    public final @NotNull TestAccuracy getTestAccuracy(
            final @NotNull String appType,
            final double threshold,
            final @NotNull Whitelist whitelist
    )
    {
        return new TestAccuracy(
                getTruePositives(appType, threshold, whitelist).size(),
                getFalsePositives(appType, threshold, whitelist).size(),
                getTrueNegatives(appType, threshold, whitelist).size(),
                getFalseNegatives(appType, threshold, whitelist).size()
        );
    }

    public Collection<TestResult> getTruePositives(
            final @NotNull String appType,
            final double threshold,
            final @NotNull Whitelist whitelist
    )
    {
        // lib reported and in app
        return getTruePositives(appType, whitelist,
                x -> x.similarity() >= threshold ? 1 : 0);
    }

    public Collection<TestResult> getTruePositives(
            final @NotNull String appType,
            final @NotNull Whitelist whitelist,
            final @NotNull ToIntFunction<TestResult> function
    )
    {
        // lib reported and in app
        // x >= threshold && x in W
        return getTestResults(appType)
                .stream()
                .filter(x -> function.applyAsInt(x) == 1)
                .filter(whitelist)
                .toList();
    }

    public Collection<TestResult> getFalsePositives(
            final @NotNull String appType,
            final double threshold,
            final @NotNull Whitelist whitelist
    )
    {
        // lib reported but not in app
        return getFalsePositives(appType, whitelist,
                x -> x.similarity() >= threshold ? 1 : 0);
    }

    public Collection<TestResult> getFalsePositives(
            final @NotNull String appType,
            final @NotNull Whitelist whitelist,
            final @NotNull ToIntFunction<TestResult> function
    )
    {
        // lib reported but not in app
        // x >= threshold && x not in W
        return getTestResults(appType)
                .stream()
                .filter(x -> function.applyAsInt(x) == 1)
                .filter(whitelist.negate())
                .toList();
    }

    public Collection<TestResult> getFalseNegatives(
            final @NotNull String appType,
            final @NotNull Whitelist whitelist,
            final @NotNull ToIntFunction<TestResult> function
    )
    {
        // lib isn't reported but in app
        // x < threshold && x in W
        return getTestResults(appType)
                .stream()
                .filter(x -> function.applyAsInt(x) == 1)
                .filter(whitelist)
                .toList();
    }

    public Collection<TestResult> getFalseNegatives(
            final @NotNull String appType,
            final double threshold,
            final @NotNull Whitelist whitelist
    )
    {
        return getFalseNegatives(appType, whitelist,
                x -> x.similarity() < threshold ? 1 : 0);
    }

    public Collection<TestResult> getTrueNegatives(
            final @NotNull String appType,
            final @NotNull Whitelist whitelist,
            final @NotNull ToIntFunction<TestResult> function
    )
    {
        // lib isn't reported and not in app
        // x < threshold && x not in W
        return getTestResults(appType)
                .stream()
                .filter(x -> function.applyAsInt(x) == 1)
                .filter(whitelist.negate())
                .toList();
    }

    public Collection<TestResult> getTrueNegatives(
            final @NotNull String appType,
            final double threshold,
            final @NotNull Whitelist whitelist
    )
    {
        // lib isn't reported but in app
        return getTrueNegatives(appType, whitelist,
                x -> x.similarity() < threshold ? 1 : 0);
    }

    /**
     * Get the set of application types that were tested in the benchmark.
     *
     * @return A set of strings representing the tested application types.
     */
    public @NotNull Set<String> getTestedAppTypes()
    {
        return results.keySet();
    }

    /**
     * Get the test results for a specific application type.
     *
     * @param appType The application type for which test results are
     *                requested.
     * @return An array of TestResult objects for the specified application
     *         type, or null if not found.
     */
    public @NotNull Collection<TestResult> getTestResults(
            final @NotNull String appType)
    {
        return results.get(appType);
    }
}
