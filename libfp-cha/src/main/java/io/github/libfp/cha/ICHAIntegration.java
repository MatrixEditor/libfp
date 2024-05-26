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
package io.github.libfp.cha;

import io.github.libfp.profile.IIntegration;
import io.github.libfp.similarity.IStrategy;

/**
 * The {@code ICHAIntegration} interface extends the {@code IIntegration}
 * interface and provides additional methods specifically for Class Hierarchy
 * Analysis (CHA) integration.
 * <p>
 * It includes methods for adding class, method, package, and field steps,
 * setting similarity strategies for classes, methods, packages, and fields, and
 * defining layers for fields, methods, classes, and packages in the CHA
 * strategy.
 *
 * @see IIntegration
 */
public interface ICHAIntegration extends IIntegration
{
    /**
     * Adds a class step to the CHA strategy.
     *
     * @param strategy The CHA strategy to which the class step is added.
     */
    default void addClassStep(CHAStrategy strategy)
    {
    }

    /**
     * Adds a method step to the CHA strategy.
     *
     * @param strategy The CHA strategy to which the method step is added.
     */
    default void addMethodStep(CHAStrategy strategy)
    {
    }

    /**
     * Adds a package step to the CHA strategy.
     *
     * @param strategy The CHA strategy to which the package step is added.
     */
    default void addPackageStep(CHAStrategy strategy)
    {
    }

    /**
     * Adds a field step to the CHA strategy.
     *
     * @param strategy The CHA strategy to which the field step is added.
     */
    default void addFieldStep(CHAStrategy strategy)
    {
    }

    /**
     * Sets the similarity strategy for classes in the CHA integration.
     *
     * @param strategy The strategy to set for classes.
     */
    default void setClassStrategy(IStrategy<?> strategy)
    {
    }

    /**
     * Sets the similarity strategy for methods in the CHA integration.
     *
     * @param strategy The strategy to set for methods.
     */
    default void setMethodStrategy(IStrategy<?> strategy)
    {
    }

    /**
     * Sets the similarity strategy for packages in the CHA integration.
     *
     * @param strategy The strategy to set for packages.
     */
    default void setPackageStrategy(IStrategy<?> strategy)
    {
    }

    /**
     * Sets the similarity strategy for fields in the CHA integration.
     *
     * @param strategy The strategy to set for fields.
     */
    default void setFieldStrategy(IStrategy<?> strategy)
    {
    }

    /**
     * Sets the field layer in the CHA strategy.
     *
     * @param strategy The CHA strategy for which the field layer is set.
     */
    default void setFieldLayer(CHAStrategy strategy)
    {
        setFieldStrategy(strategy);
        addFieldStep(strategy);
    }

    /**
     * Sets the method layer in the CHA strategy.
     *
     * @param strategy The CHA strategy for which the method layer is set.
     */
    default void setMethodLayer(CHAStrategy strategy)
    {
        setMethodStrategy(strategy);
        addMethodStep(strategy);
    }

    /**
     * Sets the class layer in the CHA strategy.
     *
     * @param strategy The CHA strategy for which the class layer is set.
     */
    default void setClassLayer(CHAStrategy strategy)
    {
        setClassStrategy(strategy);
        addClassStep(strategy);
    }

    /**
     * Sets the package layer in the CHA strategy.
     *
     * @param strategy The CHA strategy for which the package layer is set.
     */
    default void setPackageLayer(CHAStrategy strategy)
    {
        setPackageStrategy(strategy);
        addPackageStep(strategy);
    }

    @Override
    default void addProfileStep(IStrategy<?> strategy)
    {
        strategy.with(CHAProfile.class, new CHAProfileStep());
    }
}
