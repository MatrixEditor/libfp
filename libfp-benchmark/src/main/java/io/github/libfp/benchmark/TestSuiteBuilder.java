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

import io.github.libfp.profile.IProfileProvider;
import io.github.libfp.profile.ProfileFactory;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.similarity.IStrategy;

/**
 * A builder class for creating instances of the TestSuite class with
 * customizable components. Allows users to set various components required for
 * creating a {@link CHATestSuite} instance.
 */
public class TestSuiteBuilder<C, T extends ITestSuite>
{
    private final ITestSuiteFactory<C, T> factory;

    // Components for creating a TestSuite
    private IProfileManagerFactory managerFactory;
    private ProfileFactory<C> profileFactory;
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
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setManagerFactory(
            IProfileManagerFactory managerFactory)
    {
        this.managerFactory = managerFactory;
        return this;
    }

    /**
     * Sets the profile factory for the TestSuite.
     *
     * @param profileFactory The factory for creating profiles.
     * @return The TestSuiteBuilder instance for method chaining.
     */
    public TestSuiteBuilder<C, T> setProfileFactory(
            ProfileFactory<C> profileFactory)
    {
        this.profileFactory = profileFactory;
        return this;
    }

    public TestSuiteBuilder<C, T> setProvider(IProfileProvider<C> provider)
    {
        return setStrategy(provider.getStrategy())
                .setManagerFactory(provider.getManagerFactory())
                .setProfileFactory(provider.getProfileFactory());
    }

    /**
     * Sets the data set to be used in the TestSuite.
     *
     * @param dataSet The data set for testing.
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
                profileFactory,
                dataSet,
                strategy
        );
    }
}
