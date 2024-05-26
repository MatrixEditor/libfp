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
package io.github.libfp.impl.matching;

import io.github.libfp.matching.AbstractGraphMatching;
import io.github.libfp.matching.IResultHandler;
import io.github.libfp.matching.IVertexFunction;
import io.github.libfp.profile.IComparable;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Set;

/**
 * The <code>HungarianAlgorithm</code> class extends the abstract
 * <code>AbstractGraphMatching</code> class to implement a specific
 * similarity strategy using the Hungarian algorithm for finding the minimal
 * cost bipartite perfect matching between two sets of vertices.
 *
 * @param <T> The type of the input objects.
 * @param <V> The type of the graph vertices.
 */
public class HungarianAlgorithm<T, V extends IComparable<V>>
        extends AbstractGraphMatching<T, V, DefaultWeightedEdge>
{

    public HungarianAlgorithm(
            IVertexFunction<T, V> vertexFunction)
    {
        this(vertexFunction, null);
    }

    public HungarianAlgorithm(
            IVertexFunction<T, V> vertexFunction,
            IResultHandler<V, DefaultWeightedEdge> handler)
    {
        super(vertexFunction, handler);
    }

    /**
     * Get the matching algorithm specific to the Hungarian algorithm.
     *
     * @param graph      The graph containing the vertices and edges.
     * @param partition1 The first set of vertices.
     * @param partition2 The second set of vertices.
     * @return A matching algorithm for finding the minimal cost perfect
     *         matching.
     */
    @Override
    protected @NotNull MatchingAlgorithm<V, DefaultWeightedEdge> getAlgorithm(
            @NotNull Graph<V, DefaultWeightedEdge> graph,
            @NotNull Set<V> partition1,
            @NotNull Set<V> partition2)
    {
        return new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(
                graph, partition1, partition2
        );
    }

    /**
     * Adjust the edge weight for the Hungarian algorithm.
     *
     * @param weight The original weight to adjust.
     * @return The adjusted weight (distance).
     */
    @Override
    protected double getWeight(double weight)
    {
        // We need the distance as this algorithm uses the minimal cost
        return 1 - super.getWeight(weight);
    }

    /**
     * Get an instance of the weighted multigraph used for the Hungarian
     * algorithm.
     *
     * @return A new weighted multigraph.
     */
    @Override
    protected @NotNull Graph<V, DefaultWeightedEdge> getGraphInstance()
    {
        return new WeightedMultigraph<>(DefaultWeightedEdge.class);
    }
}
