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
package io.github.libfp.impl.tlsh;

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.cha.step.GenericMethodStep;
import io.github.libfp.hash.TrendMicroLSH;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.Nullable;


public final class TLSHFactory
{
    public static final String HASH_KEY = "tlsh_hash";


    // Note the generic here, so we can apply this strategy to all subclasses.
    public static class MethodStrategy<M extends MethodProfile> implements
            ISimilarityStrategy<M>
    {

        @Override
        public double similarityOf(M app, M lib, IThresholdConfig config)
        {
            if (!app.getDescriptor().equals(lib.getDescriptor())) {
                return 0.0;
            }

            TrendMicroLSH appHash = (TrendMicroLSH) app.get(HASH_KEY);
            TrendMicroLSH libHash = (TrendMicroLSH) lib.get(HASH_KEY);
            if (appHash == null || libHash == null) {
                return 0.0;
            }

            if (appHash.tlsh == null || libHash.tlsh == null) {
                // same descriptor and no hash: we have to flag them as equal
                return appHash.tlsh == null && libHash.tlsh == null ? 1 : 0;
            }

            final double diff = libHash.tlsh.totalDiff(appHash.tlsh, false);
            if (diff == 0) {
                // zero means absolute similarity
                return 1.0;
            }

            double upperBound = ITLSHThresholdConfig.defaultUpperBound;
            if (config instanceof ITLSHThresholdConfig) {
                upperBound = ((ITLSHThresholdConfig) config).getUpperDifferenceBound();
            }

            if (diff > upperBound) {
                return 0.0; // too far away
            }
            return (upperBound - diff) / upperBound;
        }
    }

    public static class MethodStep<M extends MethodProfile>
            extends GenericMethodStep<M>
    {

        public MethodStep()
        {
            super();
        }

        public MethodStep(@Nullable Class<M> profileClass)
        {
            super(profileClass);
        }

        @Override
        public void process(IMethod ref, M target)
        {
            TrendMicroLSH hash = new TrendMicroLSH();
            target.put(HASH_KEY, hash);

            BytecodeNormalizer normalizer = target
                    .getManager()
                    .getNormalizer();
            if (normalizer == null) {
                throw new IllegalStateException(
                        "TrendMicroLSH MethodStep needs a bytecode normalizer " +
                                "to be present. "
                );
            }

            //noinspection DataFlowIssue
            normalizer.normalize(ref)
                    .map(String::getBytes)
                    .forEach(hash.creator::update); // creator is not null here

            // REVISIT: maybe flag methods with no hash
            hash.tlsh = hash.creator.getHashNoThrow();
            hash.creator = null;
        }
    }
}
