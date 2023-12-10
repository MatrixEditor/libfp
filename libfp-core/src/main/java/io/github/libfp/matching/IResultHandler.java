package io.github.libfp.matching; //@date 31.10.2023

import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.Collection;

/**
 * The <code>IResultHandler</code> interface represents a functional interface
 * for handling the results of a matching algorithm. It defines a single method
 * <code>apply</code> that takes a matching result, a collection of application
 * vertices, and a collection of library vertices and returns a double value
 * representing a score or metric based on the provided inputs.
 *
 * @param <V> The type of vertices in the graph.
 * @param <E> The type of edges in the graph.
 */
@FunctionalInterface
public interface IResultHandler<V, E>
{

    /**
     * Handles the results of a matching algorithm and returns a double value
     * representing a score or metric.
     *
     * @param matching The matching result, typically a subgraph containing the
     *                 matched edges and vertices.
     * @param app      The collection of application vertices.
     * @param lib      The collection of library vertices.
     *
     * @return A double value representing a score or metric based on the
     *         matching results and input collections.
     */
    double apply(
            @NotNull MatchingAlgorithm.Matching<V, E> matching,
            @NotNull Collection<V> app,
            @NotNull Collection<V> lib,
            @NotNull IThresholdConfig config
    );
}

