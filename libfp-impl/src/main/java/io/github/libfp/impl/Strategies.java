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
package io.github.libfp.impl;

import io.github.libfp.cha.*;
import io.github.libfp.impl.matching.HungarianAlgorithm;
import io.github.libfp.impl.matching.MaxWeightBipartiteSimilarity;
import io.github.libfp.impl.matching.MultiPhaseMatching;
import io.github.libfp.matching.AbstractGraphMatching;
import io.github.libfp.matching.IResultHandler;
import io.github.libfp.matching.IVertexFunction;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.function.Function;

/**
 * The <code>Strategies</code> class provides a set of static methods for
 * creating and applying various matching strategies and algorithms.
 */
public final class Strategies
{
    private Strategies()
    {
    }

    /**
     * Apply the Rolling Hash Method layer to the given base strategy.
     *
     * @param base The base strategy to apply the Rolling Hash Method layer to.
     * @return The strategy with the Rolling Hash Method layer applied.
     * @see RollingHashFactory
     */
    public static <M extends MethodProfile> CHAStrategy applyRollingHashMethod(
            @NotNull CHAStrategy base, Class<M> mType)
    {
        return base
                .with(mType, new RollingHashFactory.MethodStrategy<>())
                .with(mType, new RollingHashFactory.MethodStep<>(mType));
    }

    /**
     * Add a weighted class profile step to the given base strategy.
     *
     * @param base The base strategy to add the weighted class profile step to.
     * @param type The class type for the weighted profile.
     * @param <T>  The type of class profile.
     * @return The strategy with the added weighted class profile step.
     */
    public static <T extends ClassProfile,
            S extends IStrategy<S>>
    @NotNull S addWeightedClassProfileStep(
            @NotNull IStrategy<S> base,
            @NotNull Class<T> type)
    {
        return base.with(type, new WeightedBBClassStep<>(type));
    }

    public static <T extends ExtensibleProfile> Blueprint<T> addWeight(
            @NotNull Blueprint<T> blueprint)
    {
        return blueprint.add("weight", Constants.Numeric::new);
    }

    /**
     * Create a Hungarian algorithm for matching class profiles with method
     * profiles.
     *
     * @param <T> The type of class profile.
     * @return A Hungarian algorithm for class-method profile matching.
     */
    public static <T extends ClassProfile>
    @NotNull HungarianAlgorithm<T, MethodProfile> classHungarianAlgorithm()
    {
        return hungarianAlgorithm(x -> ((ExtendedClassProfile) x).getMethods());
    }

    /**
     * Create a Hungarian algorithm for matching profiles with class profiles.
     *
     * @param <T> The type of profile.
     * @return A Hungarian algorithm for profile-class profile matching.
     */
    public static <T extends CHAProfile>
    @NotNull HungarianAlgorithm<T, ClassProfile> profileHungarianAlgorithm()
    {
        return hungarianAlgorithm(CHAProfile::getClasses);
    }

    /**
     * Create a Hungarian algorithm with a custom vertex mapping function.
     *
     * @param <T>    The type of managed profile.
     * @param <V>    The type of vertices for matching.
     * @param mapper The vertex mapping function.
     * @return A Hungarian algorithm for matching using the given mapping
     *         function.
     */
    public static <T extends ManagedProfile, V extends IComparable<V>>
    @NotNull HungarianAlgorithm<T, V> hungarianAlgorithm(
            final Function<T, Iterable<V>> mapper)
    {
        return hungarianAlgorithm(null, mapper);
    }

    /**
     * Create a Hungarian algorithm with a custom vertex mapping function and
     * result handler.
     *
     * @param <T>     The type of managed profile.
     * @param <V>     The type of vertices for matching.
     * @param handler The result handler for matching.
     * @param mapper  The vertex mapping function.
     * @return A Hungarian algorithm for matching using the given mapping
     *         function and result handler.
     */
    public static <T extends ManagedProfile, V extends IComparable<V>>
    @NotNull HungarianAlgorithm<T, V> hungarianAlgorithm(
            final IResultHandler<V, DefaultWeightedEdge> handler,
            final Function<T, Iterable<V>> mapper)
    {
        IVertexFunction<T, V> function =
                IVertexFunction.toVertexFunction(mapper);
        return new HungarianAlgorithm<>(function, handler);
    }

    /**
     * Create a maximum weight bipartite matching algorithm for matching class
     * profiles with method profiles.
     *
     * @param <T> The type of class profile.
     * @return A maximum weight bipartite matching algorithm for class-method
     *         profile matching.
     */
    public static <T extends ClassProfile>
    @NotNull MaxWeightBipartiteSimilarity<T, MethodProfile> classMaximumWeightBipartiteMatching()
    {
        return maximumWeightBipartiteMatching(
                x -> ((ExtendedClassProfile) x).getMethods());
    }

    /**
     * Create a maximum weight bipartite matching algorithm for matching
     * profiles with class profiles.
     *
     * @param <T> The type of profile.
     * @return A maximum weight bipartite matching algorithm for profile-class
     *         profile matching.
     */
    public static <T extends CHAProfile>
    @NotNull MaxWeightBipartiteSimilarity<T, ClassProfile> profileMaximumWeightBipartiteMatching()
    {
        return maximumWeightBipartiteMatching(CHAProfile::getClasses);
    }

    /**
     * Create a maximum weight bipartite matching algorithm with a custom vertex
     * mapping function.
     *
     * @param <T>    The type of managed profile.
     * @param <V>    The type of vertices for matching.
     * @param mapper The vertex mapping function.
     * @return A maximum weight bipartite matching algorithm for matching using
     *         the given mapping function.
     */
    public static <T extends ManagedProfile, V extends IComparable<V>>
    @NotNull MaxWeightBipartiteSimilarity<T, V> maximumWeightBipartiteMatching(
            final Function<T, Iterable<V>> mapper)
    {
        return maximumWeightBipartiteMatching(null, mapper);
    }

    /**
     * Create a maximum weight bipartite matching algorithm with a custom vertex
     * mapping function and result handler.
     *
     * @param <T>     The type of managed profile.
     * @param <V>     The type of vertices for matching.
     * @param handler The result handler for matching.
     * @param mapper  The vertex mapping function.
     * @return A maximum weight bipartite matching algorithm for matching using
     *         the given mapping function and result handler.
     */
    public static <T extends ManagedProfile, V extends IComparable<V>>
    @NotNull MaxWeightBipartiteSimilarity<T, V> maximumWeightBipartiteMatching(
            final IResultHandler<V, DefaultWeightedEdge> handler,
            final Function<T, Iterable<V>> mapper)
    {
        final IVertexFunction<T, V> function =
                IVertexFunction.toVertexFunction(mapper);
        return new MaxWeightBipartiteSimilarity<>(function, handler);
    }

    /**
     * Combine two matching algorithms to create a multiphase matching
     * strategy.
     *
     * @param matching     The current matching algorithm.
     * @param nextMatching The next matching algorithm for the next phase.
     * @param <T>          The type of input objects for the current matching.
     * @param <U>          The type of vertices for the current matching.
     * @return The current matching algorithm with the multiphase handler
     *         applied.
     */
    public static <T extends ManagedProfile, U extends IComparable<U>,
            A extends AbstractGraphMatching<T, U, DefaultWeightedEdge>,
            B extends ISimilarityStrategy<U>>
    @NotNull A combine(
            @NotNull A matching,
            B nextMatching)
    {
        IResultHandler<U, DefaultWeightedEdge> handler =
                new MultiPhaseMatching<>(matching, nextMatching);
        matching.setHandler(handler);
        return matching;
    }

}
