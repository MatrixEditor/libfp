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

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.cha.step.GenericMethodStep;
import io.github.libfp.hash.RollingHash;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;

/**
 * <i>copied from RollingHashMethodProfile</i>
 * <p>
 * The RollingHashMethodProfile class extends {@link MethodProfile} and provides
 * additional behavior related to rolling hash calculations and similarity
 * calculations specific to method profiles.
 * <p>
 * The {@code #similarityTo} method calculates the similarity between two
 * {@code RollingHashMethodProfile} objects using a rolling hash technique.
 * Here's how the similarity is calculated:
 * <ul>
 *     <li>
 *     If the initial similarity score is 0.0 (indicating no similarity),
 *     the method returns 0.0 immediately.
 *     </li>
 *     <li>
 *     If there is some initial similarity, the method proceeds with the
 *     rolling hash similarity calculation.
 *     </li>
 *     <li>
 *     It calculates the rolling hash similarity by iterating over the
 *     hash values of the current {@code RollingHashMethodProfile} (this
 *     .hash) and checking how many of these values are also contained in the
 *     hash of the other {@code RollingHashMethodProfile} (appProfile.hash).
 *     </li>
 *     <li>
 *     The rolling hash similarity is calculated as {@code (count * 1.0) /
 *     size},
 *     where count is the number of common hash values, and size is the total
 *     number of hash values in the current profile's rolling hash.
 *     </li>
 * </ul>
 *
 * @see MethodProfile
 */
public final class RollingHashFactory
{
    public static final String HASH_KEY = "rolling_hash";

    private RollingHashFactory()
    {
    }

    public static class MethodStrategy<M extends MethodProfile>
            implements ISimilarityStrategy<M>
    {

        /**
         * Calculates the similarity between two
         * {@code RollingHashMethodProfile} instances based on their rolling
         * hash values.
         *
         * @param app    The application {@code RollingHashMethodProfile} for
         *               comparison.
         * @param lib    The library {@code RollingHashMethodProfile} for
         *               comparison.
         * @param config The similarity threshold configuration.
         * @return The rolling hash-based similarity score between the
         *         profiles.
         */
        @Override
        public double similarityOf(M app, M lib, IThresholdConfig config)
        {
            if (!app.getDescriptor().equals(lib.getDescriptor())) {
                return 0.0;
            }

            RollingHash appHash = (RollingHash) app.get(HASH_KEY);
            RollingHash libHash = (RollingHash) lib.get(HASH_KEY);

            if (appHash == null || libHash == null) {
                return 0.0;
            }

            int size = libHash.size();
            if (size == appHash.size() && size == 0) {
                // no code but same descriptor -> we have to flag them as equal
                return 1.0;
            }
            int count = 0;
            for (int value : libHash) {
                if (appHash.contains(value)) {
                    count++;
                }
            }
            return count == 0 ? 0 : (double) count / size;
        }
    }

    public static class MethodStep<M extends MethodProfile>
            extends GenericMethodStep<M>
    {

        public MethodStep()
        {
            super();
        }

        public MethodStep(Class<M> profileClass)
        {
            super(profileClass);
        }

        /**
         * Processes an {@code IMethod} reference and updates the corresponding
         * {@code RollingHashMethodProfile}. This involves computing and storing
         * the rolling hash of the method's normalized content.
         *
         * @param ref    The method reference to process.
         * @param target The method profile to update with rolling hash
         *               information.
         */
        @Override
        public void process(IMethod ref, M target)
        {
            RollingHash hash = new RollingHash();
            target.put(HASH_KEY, hash);

            BytecodeNormalizer normalizer = target.getManager()
                    .getNormalizer();
            if (normalizer == null) {
                throw new IllegalStateException(
                        "No normalizer available for method " + ref
                );
            }

            normalizer.normalize(ref).forEach(hash::add);
        }
    }
}
