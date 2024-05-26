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
package io.github.libfp.similarity;

import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.ProfilePolicy;
import io.github.libfp.profile.features.IStep;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The abstract base class for implementing the {@link IStrategy} interface. It
 * provides common functionality for managing similarity strategies and
 * processing steps for different types of profiles.
 *
 * @param <S> The type of the concrete strategy class, allowing for method
 *            chaining and fluent API design.
 */
public abstract non-sealed class AbstractStrategy<S extends IStrategy<S>>
        implements IStrategy<S>
{
    // REVISIT: find a way to remove the dangerous casting operation
    private final Map<Class<?>, ISimilarityStrategy<?>> strategies =
            new HashMap<>();

    private final Map<Class<?>, List<IStep<?, ?>>> steps =
            new HashMap<>();

    private final Set<ProfilePolicy<?>> policies = new HashSet<>();


    @Override
    public @NotNull <C> Collection<ProfilePolicy<C>> getPolicies(
            Class<? extends C> type)
    {
        //noinspection unchecked
        return policies.stream()
                .filter(p -> p.isContextClass(type))
                .map(p -> (ProfilePolicy<C>) p)
                .toList();
    }

    @Override
    public <C> @NotNull S with(ProfilePolicy<C> policy)
    {
        policies.add(policy);
        return self();
    }

    /**
     * Retrieves a similarity strategy for a specified class type.
     *
     * @param type The class type for which a similarity strategy is needed.
     * @param <T>  The type of objects processed by the strategy.
     * @return The similarity strategy for the specified class type, or
     *         <code>null</code> if not found.
     */
    public <T> ISimilarityStrategy<T> getSimilarityStrategy(
            final @NotNull Class<? extends T> type)
    {
        //noinspection unchecked
        return (ISimilarityStrategy<T>) this.strategies.get(type);
    }

    /**
     * Retrieves a collection of feature extraction steps for a specified
     * profile type.
     *
     * @param type The profile type for which feature extraction steps are
     *             needed.
     * @param <T>  The type of objects processed by the steps.
     * @param <M>  The type of the managed profile.
     * @return A collection of feature extraction steps for the specified
     *         profile type, or an empty collection if not found.
     */
    public <T, M extends ManagedProfile>
    @NotNull Collection<IStep<T, M>> getFeatureExtractors(
            @NotNull Class<M> type)
    {
        if (!steps.containsKey(type)) {
            return Collections.emptyList();
        }

        //noinspection unchecked
        return this.steps
                .get(type)
                .stream()
                .sorted(Comparator.comparingInt(IStep::priority))
                .map(x -> (IStep<T, M>) x)
                .toList();
    }

    /**
     * Registers a similarity strategy for a specified class type.
     *
     * @param type     The class type for which to register the similarity
     *                 strategy.
     * @param strategy The similarity strategy to register.
     * @param <T>      The type of objects processed by the strategy.
     * @return The current instance of the concrete strategy class.
     */
    public <T> @NotNull S with(
            @NotNull Class<T> type,
            final ISimilarityStrategy<T> strategy)
    {
        strategies.put(type, strategy);
        return self();
    }

    /**
     * Registers a feature extraction step for a specified profile type.
     *
     * @param type      The profile type for which to register the feature
     *                  extraction step.
     * @param extractor The feature extraction step to register.
     * @param <T>       The type of objects processed by the steps.
     * @param <M>       The type of the managed profile.
     * @return The current instance of the concrete strategy class.
     */
    public <T, M extends ManagedProfile> @NotNull S with(
            @NotNull Class<M> type,
            final IStep<T, M> extractor)
    {
        steps.computeIfAbsent(type, k -> new LinkedList<>())
                .add(extractor);
        return self();
    }

    /**
     * Registers a similarity strategy and a feature extraction step for a
     * specified profile type and its base type.
     *
     * @param profileType     The specific profile type for which to register
     *                        the strategy and step.
     * @param profileBaseType The base type of the profile for which to register
     *                        the strategy and step.
     * @param processor       The similarity strategy and feature extraction
     *                        step to register.
     * @param <T>             The type of objects processed by the strategy.
     * @param <R>             The result type of the feature extraction step.
     * @param <M>             The type of the managed profile.
     * @param <U>             The type of the strategy and step.
     * @return The current instance of the concrete strategy class.
     */
    public <T, R, M extends ManagedProfile,
            U extends ISimilarityStrategy<T> & IStep<R, M>>
    @NotNull S with(
            @NotNull Class<T> profileType,
            @NotNull Class<M> profileBaseType,
            U processor)
    {
        return with(profileType, processor)
                .with(profileBaseType, processor);
    }

    /**
     * Removes feature extraction steps for a specified profile type.
     *
     * @param type The profile type for which to remove feature extraction
     *             steps.
     * @param <M>  The type of the managed profile.
     * @return The current instance of the concrete strategy class.
     */
    public <M extends ManagedProfile> @NotNull S remove(
            @NotNull Class<M> type)
    {
        steps.remove(type);
        return self();
    }

    /**
     * Combines the strategies and steps from another strategy instance into the
     * current instance.
     *
     * @param other The strategy instance to combine.
     * @param <T>   The type of the strategy.
     * @return The current instance of the concrete strategy class.
     */
    @Override
    public <T extends IStrategy<T>> @NotNull S with(
            @NotNull IStrategy<T> other)
    {
        this.steps.putAll(((AbstractStrategy<T>) other).steps);
        this.strategies.putAll(((AbstractStrategy<T>) other).strategies);
        return self();
    }

    /**
     * Returns a string representation of the strategy, including information
     * about the registered strategies and steps.
     *
     * @return A string representation of the strategy.
     */
    @Override
    public String toString()
    {
        return "Strategy{" +
                "strategy-types=" + strategies.size() +
                ", step-types=" + steps.size() +
                '}';
    }

    /**
     * Abstract method to return the concrete instance of the strategy class.
     *
     * @return The concrete instance of the strategy class.
     */
    protected abstract S self();
}
