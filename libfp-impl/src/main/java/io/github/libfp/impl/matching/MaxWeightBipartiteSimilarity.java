package io.github.libfp.impl.matching; //@date 31.10.2023

import io.github.libfp.matching.AbstractGraphMatching;
import io.github.libfp.matching.IResultHandler;
import io.github.libfp.matching.IVertexFunction;
import io.github.libfp.profile.IComparable;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Set;

/**
 * The <code>MaxWeightBipartiteSimilarity</code> class extends the abstract
 * <code>AbstractGraphMatching</code> class to implement a similarity strategy
 * using the Maximum Weight Bipartite Matching algorithm for finding the maximum
 * weight matching between two sets of vertices.
 *
 * @param <T> The type of the input objects.
 * @param <V> The type of the graph vertices.
 */
public class MaxWeightBipartiteSimilarity<T, V extends IComparable<V>>
        extends AbstractGraphMatching<T, V, DefaultWeightedEdge>
{

    public MaxWeightBipartiteSimilarity(
            IVertexFunction<T, V> vertexFunction)
    {
        this(vertexFunction, null);
    }

    public MaxWeightBipartiteSimilarity(
            IVertexFunction<T, V> vertexFunction,
            IResultHandler<V, DefaultWeightedEdge> handler)
    {
        super(vertexFunction, handler);
    }

    /**
     * Get an instance of the weighted multigraph used for the Maximum Weight
     * Bipartite Matching algorithm.
     *
     * @return A new weighted multigraph.
     */
    @Override
    protected @NotNull Graph<V, DefaultWeightedEdge> getGraphInstance()
    {
        return new WeightedMultigraph<>(DefaultWeightedEdge.class);
    }

    /**
     * Get the matching algorithm specific to the Maximum Weight Bipartite
     * Matching algorithm.
     *
     * @param graph      The graph containing the vertices and edges.
     * @param partition1 The first set of vertices.
     * @param partition2 The second set of vertices.
     *
     * @return A matching algorithm for finding the maximum weight matching.
     */
    @Override
    protected @NotNull MatchingAlgorithm<V, DefaultWeightedEdge> getAlgorithm(
            @NotNull Graph<V, DefaultWeightedEdge> graph,
            @NotNull Set<V> partition1,
            @NotNull Set<V> partition2)
    {
        return new MaximumWeightBipartiteMatching<>(
                graph, partition1, partition2
        );
    }
}
