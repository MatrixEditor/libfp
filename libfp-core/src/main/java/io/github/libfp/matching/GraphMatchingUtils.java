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
package io.github.libfp.matching;

import org.jgrapht.Graph;
import org.jgrapht.GraphIterables;
import org.jgrapht.GraphType;
import org.jheaps.AddressableHeap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The <code>GraphMatchingUtils</code> class provides utility methods for
 * working with graphs, specifically implementing a synchronized graph.
 */
public final class GraphMatchingUtils
{

    private GraphMatchingUtils()
    {
    }

    /**
     * Returns a synchronized version of the given graph.
     *
     * @param g The graph to be synchronized.
     * @param <V> The type of the vertices in the graph.
     * @param <E> The type of the edges in the graph.
     * @return A synchronized version of the given graph.
     */
    public static <V, E> Graph<V, E> synchronizedGraph(Graph<V, E> g)
    {
        return new SynchronizedGraph<>(g);
    }

    /**
     * Implements a synchronized version of a given graph.
     *
     * @param <V> vertex type
     * @param <E> edge type
     */
    static class SynchronizedGraph<V, E> implements Graph<V, E>
    {

        private final Graph<V, E> delegate;

        SynchronizedGraph(Graph<V, E> delegate)
        {
            this.delegate = delegate;
        }

        @Override
        public Set<E> getAllEdges(V v, V v1)
        {
            return delegate.getAllEdges(v, v1);
        }

        @Override
        public E getEdge(V v, V v1)
        {
            return delegate.getEdge(v, v1);
        }

        @Override
        public Supplier<V> getVertexSupplier()
        {
            return delegate.getVertexSupplier();
        }

        @Override
        public Supplier<E> getEdgeSupplier()
        {
            return delegate.getEdgeSupplier();
        }

        @Override
        public E addEdge(V v, V v1)
        {
            return delegate.addEdge(v, v1);
        }

        @Override
        public boolean addEdge(V v, V v1, E e)
        {
            return delegate.addEdge(v, v1, e);
        }

        @Override
        public V addVertex()
        {
            return delegate.addVertex();
        }

        @Override
        public boolean addVertex(V v)
        {
            return delegate.addVertex(v);
        }

        @Override
        public boolean containsEdge(V v, V v1)
        {
            return delegate.containsEdge(v, v1);
        }

        @Override
        public boolean containsEdge(E e)
        {
            return delegate.containsEdge(e);
        }

        @Override
        public boolean containsVertex(V v)
        {
            return delegate.containsVertex(v);
        }

        @Override
        public Set<E> edgeSet()
        {
            return delegate.edgeSet();
        }

        @Override
        public int degreeOf(V v)
        {
            return delegate.degreeOf(v);
        }

        @Override
        public Set<E> edgesOf(V v)
        {
            return delegate.edgesOf(v);
        }

        @Override
        public int inDegreeOf(V v)
        {
            return delegate.inDegreeOf(v);
        }

        @Override
        public Set<E> incomingEdgesOf(V v)
        {
            return delegate.incomingEdgesOf(v);
        }

        @Override
        public int outDegreeOf(V v)
        {
            return delegate.outDegreeOf(v);
        }

        @Override
        public Set<E> outgoingEdgesOf(V v)
        {
            return delegate.outgoingEdgesOf(v);
        }

        @Override
        public boolean removeAllEdges(Collection<? extends E> collection)
        {
            return delegate.removeAllEdges(collection);
        }

        @Override
        public Set<E> removeAllEdges(V v, V v1)
        {
            return delegate.removeAllEdges(v, v1);
        }

        @Override
        public boolean removeAllVertices(Collection<? extends V> collection)
        {
            return delegate.removeAllVertices(collection);
        }

        @Override
        public E removeEdge(V v, V v1)
        {
            return delegate.removeEdge(v, v1);
        }

        @Override
        public boolean removeEdge(E e)
        {
            return delegate.removeEdge(e);
        }

        @Override
        public boolean removeVertex(V v)
        {
            return delegate.removeVertex(v);
        }

        @Override
        public Set<V> vertexSet()
        {
            return delegate.vertexSet();
        }

        @Override
        public V getEdgeSource(E e)
        {
            return delegate.getEdgeSource(e);
        }

        @Override
        public V getEdgeTarget(E e)
        {
            return delegate.getEdgeTarget(e);
        }

        @Override
        public GraphType getType()
        {
            return delegate.getType();
        }

        @Override
        public double getEdgeWeight(E e)
        {
            return delegate.getEdgeWeight(e);
        }

        @Override
        public void setEdgeWeight(E e, double v)
        {
            delegate.setEdgeWeight(e, v);
        }

        @Override
        public void setEdgeWeight(V sourceVertex, V targetVertex,
                                  double weight)
        {
            delegate.setEdgeWeight(sourceVertex, targetVertex, weight);
        }

        @Override
        public GraphIterables<V, E> iterables()
        {
            return delegate.iterables();
        }
    }
}
