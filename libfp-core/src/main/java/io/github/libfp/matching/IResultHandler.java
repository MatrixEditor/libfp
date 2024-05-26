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

