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

import io.github.libfp.profile.ProfileFactory;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines a factory for creating instances of the TestSuite
 * class, which represents a suite of tests to be executed using various
 * components like profile managers, builders, factories, data sets, and
 * strategies.
 */
public interface ITestSuiteFactory<C, T extends ITestSuite>
{
    /**
     * Creates a new instance of a TestSuite with the provided components.
     *
     * @param managerFactory The factory for creating profile managers.
     * @param profileFactory The factory for creating profiles.
     * @param dataSet        The data set used for testing.
     * @param strategy       The testing strategy to be applied.
     * @return A new TestSuite instance configured with the provided
     *         components.
     */
    @NotNull T newInstance(
            final @NotNull IProfileManagerFactory managerFactory,
            final @NotNull ProfileFactory<C> profileFactory,
            final @NotNull DataSet dataSet,
            final @NotNull IStrategy<?> strategy
    );
}
