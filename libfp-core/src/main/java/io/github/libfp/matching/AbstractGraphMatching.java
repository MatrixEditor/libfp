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
package io.github.libfp.matching; //

import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.ICorrespondences;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.Collection;
import java.util.Set;

/**
 * The <code>AbstractGraphMatching</code> class provides an abstract
 * implementation of the <code>ISimilarityStrategy</code> interface for
 * computing the similarity between two input objects based on their graph
 * structure. It defines various methods for setting up the graph, computing the
 * similarity, and handling the matching results.
 *
 * @param <T> The type of the input objects.
 * @param <V> The type of the graph vertices.
 * @param <E> The type of the graph edges.
 */
public abstract class AbstractGraphMatching<T, V extends IComparable<V>, E>
        implements ISimilarityStrategy<T>
{
    protected final IVertexFunction<T, V> vertexFunction;
    protected IResultHandler<V, E> handler;

    private Class<?> inputType;

    protected AbstractGraphMatching(
            IVertexFunction<T, V> vertexFunction,
            IResultHandler<V, E> handler)
    {
        this.vertexFunction = vertexFunction;
        this.handler = handler;
    }

    public void setHandler(IResultHandler<V, E> handler)
    {
        this.handler = handler;
    }

    public Class<?> getInputType()
    {
        return inputType;
    }

    @NotNull
    protected abstract MatchingAlgorithm<V, E> getAlgorithm(
            Graph<V, E> graph,
            Set<V> partition1,
            Set<V> partition2
    );

    protected abstract Graph<V, E> getGraphInstance();

    protected Set<V> getVertices(T t)
    {
        return vertexFunction.getVertices(t);
    }

    protected ICorrespondences<V> getCorrespondences(
            V libVertex,
            Set<V> partition1,
            IThresholdConfig config)
    {
        return ICorrespondences.of(libVertex, partition1, config);
    }

    protected double getWeight(final double weight)
    {
        return weight;
    }

    protected double computeResult(
            MatchingAlgorithm.Matching<V, E> matching,
            Collection<V> app, Collection<V> lib,
            IThresholdConfig config)
    {
        if (handler != null) {
            return handler.apply(matching, app, lib, config);
        }

        Set<E> edges = matching.getEdges();
        if (matching.isPerfect()) {
            return 1.0;
        }
        return (edges.size() * 1.0) / lib.size();
    }

    @Override
    public double similarityOf(T app, T lib, IThresholdConfig config)
    {
        Set<V> partition1 = getVertices(app);
        Set<V> partition2 = getVertices(lib);
        if (partition2.isEmpty() || partition1.isEmpty()) {
            return 0.0;
        }

        Graph<V, E> graph = getGraphInstance();
        inputType = app.getClass();

        partition1.forEach(graph::addVertex);
        partition2.forEach(graph::addVertex);
        partition2.forEach(libVertex -> {
            ICorrespondences<V> c = getCorrespondences(libVertex, partition1,
                    config);
            addCorrespondences(libVertex, c, graph);
        });

        MatchingAlgorithm<V, E> algorithm =
                getAlgorithm(graph, partition1, partition2);

        // Retrieve the matched vertices
        MatchingAlgorithm.Matching<V, E> matching = algorithm.getMatching();
        return computeResult(matching, partition1, partition2, config);
    }

    protected void addCorrespondences(
            V libVertex,
            ICorrespondences<V> correspondences,
            Graph<V, E> graph)
    {
        for (int i = 0; i < correspondences.count(); i++) {
            final V matchedVertex = correspondences.getMatchAt(i);
            final double weight =
                    getWeight(correspondences.getSimilarityAt(i));

            E edge = graph.addEdge(libVertex, matchedVertex);
            graph.setEdgeWeight(edge, weight);
        }
    }
}
