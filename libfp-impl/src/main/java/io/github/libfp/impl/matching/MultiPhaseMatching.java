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
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Collection;

/**
 * The <code>MultiPhaseMatching</code> class implements a result handler for
 * multiphase matching by extending the <code>IResultHandler</code> interface.
 *
 * @param <T> The type of the input objects for the current matching.
 * @param <U> The type of vertices for the current matching.
 */
public class MultiPhaseMatching<T, U extends IComparable<U>>
        implements IResultHandler<U, DefaultWeightedEdge>
{

    final AbstractGraphMatching<T, U, DefaultWeightedEdge> matching;
    final ISimilarityStrategy<U> nextMatching;

    public MultiPhaseMatching(
            final @NotNull AbstractGraphMatching<T, U, DefaultWeightedEdge> matching,
            final ISimilarityStrategy<U> nextMatching)
    {
        this.matching = matching;
        this.nextMatching = nextMatching;
        matching.setHandler(this);
    }

    /**
     * Apply the multiphase matching strategy by combining the results of the
     * current matching with the results of the next matching phase.
     *
     * @param matching The matching result of the current phase.
     * @param app      The collection of vertices from the application.
     * @param lib      The collection of vertices from the library.
     * @param config   The threshold configuration for matching.
     * @return The combined similarity score.
     */
    @Override
    public double apply(
            MatchingAlgorithm.@NotNull Matching<U, DefaultWeightedEdge> matching,
            @NotNull Collection<U> app,
            @NotNull Collection<U> lib,
            @NotNull IThresholdConfig config)
    {

        final double threshold =
                config.getThreshold(this.matching.getInputType());

        if ((1.0 * matching
                .getEdges()
                .size() / lib.size()) < threshold)
        {
            // Filter out any negative candidates
            return 0;
        }

        final Graph<U, DefaultWeightedEdge> graph =
                matching.getGraph();

        double result = 0.0;
        double weight = 0.0;
        for (final DefaultWeightedEdge edge : matching.getEdges()) {
            // Coarse-grained matching
            U libProfile = graph.getEdgeSource(edge);
            U appProfile = graph.getEdgeTarget(edge);

            double similarity = nextMatching
                    .similarityOf(appProfile, libProfile, config);

            if (libProfile instanceof ExtensibleProfile profile) {
                Constants.Numeric profileWeight =
                        (Constants.Numeric) profile.get("weight");

                int pWeight = (profileWeight != null
                        ? profileWeight.value.intValue()
                        : 1);
                result = similarity * pWeight;
                weight += pWeight;
            } else {
                result += similarity;
            }
        }

        if (weight != 0) {
            // Sum(s_i * w_i) / Sum(w_i)
            return Math.min(1.0, result / weight);
        }

        // Sum(s_i) / |lib|
        return result / lib.size();
    }
}
