package io.github.libfp.benchmark;

import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.IProfileContext;
import io.github.libfp.profile.IProfileFactory;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.similarity.IStrategy;

/**
 * A builder class for creating instances of the TestSuite class with
 * customizable components. Allows users to set various components required for
 * creating a {@link CHATestSuite} instance.
 */
public class TestSuiteBuilder<C, T extends TestSuite<C>>
{
    private final ITestSuiteFactory<C, T> factory;

    // Components for creating a TestSuite
    private IProfileManagerFactory managerFactory;
    private IProfileBuilder<C> profileBuilder;
    private IProfileFactory<C> profileFactory;
    private DataSet dataSet;
    private IStrategy<?> strategy;

    /**
     * Constructs a TestSuiteBuilder with a specified factory for creating
     * TestSuite instances.
     *
     * @param factory The factory used to create TestSuite instances.
     */
    public TestSuiteBuilder(ITestSuiteFactory<C, T> factory)
    {
        this.factory = factory;
    }

    /**
     * Sets the profile manager factory for the TestSuite.
     *
     * @param managerFactory The factory for creating profile managers.
     *
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setManagerFactory(IProfileManagerFactory managerFactory)
    {
        this.managerFactory = managerFactory;
        return this;
    }

    /**
     * Sets the profile builder for the TestSuite.
     *
     * @param profileBuilder The builder for creating profiles.
     *
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setProfileBuilder(IProfileBuilder<C> profileBuilder)
    {
        this.profileBuilder = profileBuilder;
        return this;
    }

    /**
     * Sets the profile factory for the TestSuite.
     *
     * @param profileFactory The factory for creating profiles.
     *
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setProfileFactory(IProfileFactory<C> profileFactory)
    {
        this.profileFactory = profileFactory;
        return this;
    }

    public TestSuiteBuilder<C, T> setContext(IProfileContext<C> context)
    {
        return setStrategy(context.getStrategy())
                .setManagerFactory(context.getManagerFactory())
                .setProfileBuilder(context.getProfileBuilder())
                .setProfileFactory(context.getProfileFactory());
    }

    /**
     * Sets the data set to be used in the TestSuite.
     *
     * @param dataSet The data set for testing.
     *
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setDataSet(DataSet dataSet)
    {
        this.dataSet = dataSet;
        return this;
    }

    /**
     * Sets the testing strategy for the TestSuite.
     *
     * @param strategy The testing strategy to be applied.
     *
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setStrategy(IStrategy<?> strategy)
    {
        this.strategy = strategy;
        return this;
    }

    /**
     * Creates and returns a new TestSuite instance based on the configured
     * components.
     *
     * @return A TestSuite instance with the specified components.
     */
    public T createTestSuite()
    {
        return factory.newInstance(
                managerFactory,
                profileBuilder,
                profileFactory,
                dataSet,
                strategy
        );
    }
}
