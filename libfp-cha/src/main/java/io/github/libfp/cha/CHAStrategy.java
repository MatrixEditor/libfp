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

import io.github.libfp.cha.step.IClassStep;
import io.github.libfp.cha.step.IFieldStep;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.cha.step.IPackageStep;
import io.github.libfp.similarity.AbstractStrategy;
import io.github.libfp.similarity.ISimilarityStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * The CHAStrategy class extends the AbstractStrategy class and represents a
 * configuration and management class for handling similarity strategies and
 * processing steps specific to the CHA (Class Hierarchy Analysis) algorithm. It
 * allows the registration of similarity strategies and processing steps for
 * different types of profiles in the context of CHA.
 *
 * @see ClassProfile
 * @see PackageProfile
 * @see MethodProfile
 * @see FieldProfile
 */
public class CHAStrategy
        extends AbstractStrategy<CHAStrategy>
{
    public static CHAStrategy getDefaultInstance()
    {
        return new CHAStrategy()
                .with(CHAProfile.class, new CHAProfileStep());
    }

    /**
     * Registers a similarity strategy and processing step for each class
     * profile type in the CHA algorithm.
     *
     * @param processor The similarity strategy and class processing step to
     *                  register.
     * @return The current instance of the CHAStrategy class.
     */
    public <U extends ISimilarityStrategy<ClassProfile> & IClassStep>
    CHAStrategy eachClass(U processor)
    {
        return eachClass(ClassProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each class
     * profile type in the CHA algorithm.
     *
     * @param profileType The specific class profile type for which to register
     *                    the strategy and step.
     * @param processor   The similarity strategy and class processing step to
     *                    register.
     * @param <T>         The type of objects processed by the strategy.
     * @param <U>         The type of the strategy and step.
     * @return The current instance of the CHAStrategy class.
     */
    public <T extends ClassProfile,
            U extends ISimilarityStrategy<T> & IClassStep>
    CHAStrategy eachClass(@NotNull Class<T> profileType, U processor)
    {
        return with(profileType, ClassProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each method
     * profile type in the CHA algorithm.
     *
     * @param processor The similarity strategy and method processing step to
     *                  register.
     * @return The current instance of the CHAStrategy class.
     */
    public <U extends ISimilarityStrategy<MethodProfile> & IMethodStep>
    CHAStrategy eachMethod(U processor)
    {
        return eachMethod(MethodProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each method
     * profile type in the CHA algorithm.
     *
     * @param profileType The specific method profile type for which to register
     *                    the strategy and step.
     * @param processor   The similarity strategy and method processing step to
     *                    register.
     * @param <T>         The type of objects processed by the strategy.
     * @param <U>         The type of the strategy and step.
     * @return The current instance of the CHAStrategy class.
     */
    public <T extends MethodProfile,
            U extends ISimilarityStrategy<T> & IMethodStep>
    CHAStrategy eachMethod(@NotNull Class<T> profileType, U processor)
    {
        return with(profileType, MethodProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each package
     * profile type in the CHA algorithm.
     *
     * @param processor The similarity strategy and package processing step to
     *                  register.
     * @return The current instance of the CHAStrategy class.
     */
    public <U extends ISimilarityStrategy<PackageProfile> & IPackageStep>
    CHAStrategy eachPackage(U processor)
    {
        return eachPackage(PackageProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each package
     * profile type in the CHA algorithm.
     *
     * @param profileType The specific package profile type for which to
     *                    register the strategy and step.
     * @param processor   The similarity strategy and package processing step to
     *                    register.
     * @param <T>         The type of objects processed by the strategy.
     * @param <U>         The type of the strategy and step.
     * @return The current instance of the CHAStrategy class.
     */
    public <T extends PackageProfile,
            U extends ISimilarityStrategy<T> & IPackageStep>
    CHAStrategy eachPackage(@NotNull Class<T> profileType, U processor)
    {
        return with(profileType, PackageProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each field
     * profile type in the CHA algorithm.
     *
     * @param processor The similarity strategy and field processing step to
     *                  register.
     * @return The current instance of the CHAStrategy class.
     */
    public <U extends ISimilarityStrategy<FieldProfile> & IFieldStep>
    CHAStrategy eachField(U processor)
    {
        return eachField(FieldProfile.class, processor);
    }

    /**
     * Registers a similarity strategy and processing step for each field
     * profile type in the CHA algorithm.
     *
     * @param profileType The specific field profile type for which to register
     *                    the strategy and step.
     * @param processor   The similarity strategy and field processing step to
     *                    register.
     * @param <T>         The type of objects processed by the strategy.
     * @param <U>         The type of the strategy and step.
     * @return The current instance of the CHAStrategy class.
     */
    public <T extends FieldProfile,
            U extends ISimilarityStrategy<T> & IFieldStep>
    CHAStrategy eachField(@NotNull Class<T> profileType, U processor)
    {
        return with(profileType, FieldProfile.class, processor);
    }

    /**
     * Provides the concrete instance of the CHAStrategy class for method
     * chaining and fluent API design.
     *
     * @return The current instance of the CHAStrategy class.
     */
    @Override
    protected CHAStrategy self()
    {
        return this;
    }
}
