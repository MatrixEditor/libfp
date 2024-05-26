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

import io.github.libfp.cha.IDescriptorContainer;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.features.FeatureVector;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;

public class ManhattanDiff<E extends ExtensibleProfile & IDescriptorContainer>
        implements ISimilarityStrategy<E>
{

    private final boolean sameDescriptors;

    public ManhattanDiff(boolean sameDescriptors)
    {
        this.sameDescriptors = sameDescriptors;
    }

    @Override
    public double similarityOf(E app, E lib, IThresholdConfig config)
    {
        if (app == null || lib == null) {
            return 0.0;
        }
        if (sameDescriptors &&
                !app.getDescriptor().equals(lib.getDescriptor()))
        {
            return 0.0;
        }

        FeatureVector appVector = (FeatureVector) app.get("vector");
        FeatureVector libVector = (FeatureVector) lib.get("vector");
        if (appVector == null || libVector == null) {
            return 0.0;
        }

        if (appVector.isEmpty() && libVector.isEmpty()) {
            return 1.0;
        }

        double diff = 0.0;
        for (int i = 0; i < libVector.size(); i++) {
            diff += Math.abs(libVector.get(i) - appVector.get(i));
        }

        if (diff == 0.0) {
            return 1.0;
        }
        // we assume here that the difference does not get above 100
        return 1 / (1 + diff);
    }
}
