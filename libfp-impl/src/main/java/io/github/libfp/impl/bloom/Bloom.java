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
package io.github.libfp.impl.bloom;

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.*;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.features.IStep;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code Bloom} class provides utility methods for working with Bloom
 * filters in the context of profiles that extend {@link ExtensibleProfile}. It
 * includes methods to apply Bloom filters to profiles, retrieve Bloom filters
 * from profiles, and set Bloom filters for profiles. Additionally, it provides
 * methods to add Bloom filter steps and strategies to a {@link CHAStrategy}.
 */
public final class Bloom
        implements ICHAIntegration
{

    private static final Bloom instance = new Bloom();

    private Bloom()
    {
    }

    public static Bloom getInstance()
    {
        return instance;
    }

    /**
     * Applies the Bloom filter blueprint to the provided profile manager using
     * the specified managed profile factory.
     *
     * @param base    The profile manager to which the blueprint is applied.
     * @param factory The managed profile factory used to create profiles.
     * @param <E>     The type of profiles created by the factory.
     */
    public static <E extends ExtensibleProfile> void applyBlueprint(
            @NotNull ProfileManager base,
            @NotNull IManagedProfileFactory<E> factory)
    {
        getInstance().update(base, factory);
    }

    public static <E extends ExtensibleProfile> Blueprint<E> addToBlueprint(
            @NotNull Blueprint<E> blueprint
    )
    {
        return blueprint.add("bloom", BloomFilter::new);
    }

    /**
     * Creates a blueprint for the specified managed profile factory, adding a
     * Bloom filter field with the key "bloom" to the blueprint.
     *
     * @param factory The managed profile factory.
     * @param <E>     The type of profiles created by the factory.
     * @return The blueprint with the Bloom filter field.
     */
    public static <E extends ExtensibleProfile> Blueprint<E> getBlueprint(
            @NotNull IManagedProfileFactory<E> factory)
    {
        return Blueprint.make(factory).add("bloom", BloomFilter::new);
    }

    /**
     * Retrieves the Bloom filter associated with the "bloom" key from the given
     * {@link ExtensibleProfile}.
     *
     * @param profile The profile from which the Bloom filter is retrieved.
     * @return The Bloom filter associated with the "bloom" key.
     * @throws IllegalArgumentException If the Bloom filter is not found.
     */
    public static @NotNull BloomFilter getFilter(
            @NotNull ExtensibleProfile profile)
            throws IllegalArgumentException
    {
        return Blueprint.getOrThrow(profile, "bloom");
    }

    /**
     * Sets the provided Bloom filter for the "bloom" key in the given
     * {@link ExtensibleProfile}.
     *
     * @param profile The profile to which the Bloom filter is associated.
     * @param filter  The Bloom filter to be set.
     */
    public static void setFilter(
            @NotNull ExtensibleProfile profile,
            BloomFilter filter)
    {
        profile.put("bloom", filter);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Steps
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Adds a Bloom filter class step to the provided {@link CHAStrategy} for
     * {@link ClassProfile}.
     *
     * @param strategy The CHA strategy to which the Bloom filter class step is
     *                 added.
     */
    public static void classStep(@NotNull CHAStrategy strategy)
    {
        strategy.with(ClassProfile.class,
                (IBloomClassStep) new ClassStrategy());
    }

    /**
     * Adds a Bloom filter method step to the provided {@link CHAStrategy} for
     * {@link MethodProfile}.
     *
     * @param strategy The CHA strategy to which the Bloom filter method step is
     *                 added.
     */
    public static void methodStep(@NotNull CHAStrategy strategy)
    {
        strategy.with(MethodProfile.class,
                (IBloomMethodStep) new MethodStrategy());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Strategies
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Adds a Bloom filter class layer to the provided {@link CHAStrategy}.
     *
     * @param strategy The CHA strategy to which the Bloom filter class layer is
     *                 added.
     */
    public static void classLayer(@NotNull CHAStrategy strategy)
    {
        getInstance().setClassLayer(strategy);
    }

    /**
     * Adds a Bloom filter method layer to the provided {@link CHAStrategy}.
     *
     * @param strategy The CHA strategy to which the Bloom filter method layer
     *                 is added.
     */
    public static void methodLayer(@NotNull CHAStrategy strategy)
    {
        strategy.eachMethod(new MethodStrategy());
    }

    ///////////////////////////////////////////////////////////////////////////
    // integration
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void addClassStep(@NotNull CHAStrategy strategy)
    {
        classStep(strategy);
        strategy.with(ExtendedClassProfile.class,
                new IStep<IClass, ExtendedClassProfile>()
                {
                    @Override
                    public Class<? extends ManagedProfile> targetProfileClass()
                    {
                        return ExtendedClassProfile.class;
                    }

                    @Override
                    public void process(IClass ref,
                                        ExtendedClassProfile target)
                    {
                        new ExtendedClassStrategy().process(ref, target);
                    }
                });
    }

    @Override
    public void addMethodStep(@NotNull CHAStrategy strategy)
    {
        methodStep(strategy);
    }

    @Override
    public void setClassStrategy(@NotNull IStrategy<?> strategy)
    {
        strategy.with(ClassProfile.class,
                (ISimilarityStrategy<ClassProfile>) new ClassStrategy());
        strategy.with(ExtendedClassProfile.class, new ExtendedClassStrategy());
    }

    @Override
    public void setMethodStrategy(@NotNull IStrategy<?> strategy)
    {
        strategy.with(MethodProfile.class,
                (ISimilarityStrategy<MethodProfile>) new MethodStrategy());
    }

    @Override
    public void addProfileStep(IStrategy<?> strategy)
    {
        strategy.with(CHAProfile.class, new BloomProfileStep());
    }

    @Override
    public <E extends ExtensibleProfile> void updateBlueprint(
            @NotNull Blueprint<E> blueprint, @NotNull Class<E> type)
    {
        blueprint.add("bloom", BloomFilter::new);
    }

    /**
     * The {@code MethodStrategy} class is an implementation of
     * {@link BloomFilterStrategy} for profiles of type {@link MethodProfile}.
     */
    public static final class MethodStrategy
            extends BloomFilterStrategy<MethodProfile>
            implements IBloomMethodStep
    {
    }

    /**
     * The {@code ClassStrategy} class is an implementation of
     * {@link BloomFilterStrategy} for profiles of type {@link ClassProfile}.
     */
    public static final class ClassStrategy
            extends BloomFilterStrategy<ClassProfile>
            implements IBloomClassStep
    {
    }

    public static final class ExtendedClassStrategy
            extends BloomFilterStrategy<ExtendedClassProfile>
            implements IBloomClassStep
    {

        @Override
        public @NotNull Class<? extends ManagedProfile> targetProfileClass()
        {
            return ExtendedClassProfile.class;
        }
    }

    public static final class BloomProfileStep
            extends CHAProfileStep
    {
        @Override
        protected void processPackage(@NotNull IClass iClass,
                                      @NotNull PackageProfile packageProfile)
        {
            BloomFilter filter = new BloomFilter();
            setFilter(packageProfile, filter);

            packageProfile.getManager()
                    .getILFactory()
                    .getDocument(iClass)
                    .forEach(filter::add);
        }
    }

}
