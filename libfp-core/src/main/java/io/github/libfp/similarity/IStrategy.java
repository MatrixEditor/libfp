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

import java.util.Collection;

/**
 * The <code>IStrategy</code> interface defines a contract for a strategy that
 * manages similarity strategies and processing steps for different types of
 * profiles.
 *
 * @param <S> The type of the strategy, allowing for method chaining and fluent
 *            API design.
 */
public sealed interface IStrategy<S extends IStrategy<S>>
        permits AbstractStrategy
{

    /**
     * Retrieves a similarity strategy for a specified class type.
     *
     * @param type The class type for which a similarity strategy is needed.
     * @param <T>  The type of objects processed by the strategy.
     * @return The similarity strategy for the specified class type, or
     *         <code>null</code> if not found.
     */
    <T> ISimilarityStrategy<T> getSimilarityStrategy(
            final @NotNull Class<? extends T> type);

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
    <T, M extends ManagedProfile>
    @NotNull Collection<IStep<T, M>> getFeatureExtractors(
            @NotNull Class<M> type);


    <C> @NotNull Collection<ProfilePolicy<C>> getPolicies(
            Class<? extends C> type);

    <C> @NotNull S with(ProfilePolicy<C> policy);

    default <C> boolean isValid(C context)
    {
        //noinspection unchecked
        Collection<ProfilePolicy<C>> policies =
                getPolicies((Class<C>) context.getClass());

        if (policies.isEmpty()) return true;
        else return policies
                .stream()
                .allMatch(p -> p.isValid(context));
    }

    /**
     * Registers a similarity strategy for a specified class type.
     *
     * @param type     The class type for which to register the similarity
     *                 strategy.
     * @param strategy The similarity strategy to register.
     * @param <T>      The type of objects processed by the strategy.
     * @return The current instance of the strategy.
     */
    <T> @NotNull S with(
            @NotNull Class<T> type,
            final ISimilarityStrategy<T> strategy);

    /**
     * Registers a feature extraction step for a specified profile type.
     *
     * @param type      The profile type for which to register the feature
     *                  extraction step.
     * @param extractor The feature extraction step to register.
     * @param <T>       The type of objects processed by the steps.
     * @param <M>       The type of the managed profile.
     * @return The current instance of the strategy.
     */
    <T, M extends ManagedProfile> @NotNull S with(
            @NotNull Class<M> type,
            final IStep<T, M> extractor);

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
     * @return The current instance of the strategy.
     */
    default <T, R, M extends ManagedProfile,
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
     * @return The current instance of the strategy.
     */
    <M extends ManagedProfile> @NotNull S remove(@NotNull Class<M> type);

    /**
     * Combines the strategies and steps from another strategy instance into the
     * current instance.
     *
     * @param other The strategy instance to combine.
     * @param <T>   The type of the strategy.
     * @return The current instance of the strategy.
     */
    <T extends IStrategy<T>> @NotNull S with(final @NotNull IStrategy<T> other);

    /**
     * Returns a string representation of the strategy, including information
     * about the registered strategies and steps.
     *
     * @return A string representation of the strategy.
     */
    @Override
    String toString();
}
