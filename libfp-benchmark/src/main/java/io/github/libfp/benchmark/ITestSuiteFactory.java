package io.github.libfp.benchmark;//@date 01.11.2023

import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.IProfileFactory;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines a factory for creating instances of the TestSuite
 * class, which represents a suite of tests to be executed using various
 * components like profile managers, builders, factories, data sets, and
 * strategies.
 */
public interface ITestSuiteFactory<C, T extends TestSuite<C>>
{
    /**
     * Creates a new instance of a TestSuite with the provided components.
     *
     * @param managerFactory The factory for creating profile managers.
     * @param profileBuilder The builder for creating profiles.
     * @param profileFactory The factory for creating profiles.
     * @param dataSet        The data set used for testing.
     * @param strategy       The testing strategy to be applied.
     *
     * @return A new TestSuite instance configured with the provided
     *         components.
     */
    @NotNull T newInstance(
            final @NotNull IProfileManagerFactory managerFactory,
            final @NotNull IProfileBuilder<C> profileBuilder,
            final @NotNull IProfileFactory<C> profileFactory,
            final @NotNull DataSet dataSet,
            final @NotNull IStrategy<?> strategy
    );
}
