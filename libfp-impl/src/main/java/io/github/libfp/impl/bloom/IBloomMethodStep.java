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
package io.github.libfp.impl.bloom;

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.profile.ManagedProfile;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code IBloomMethodStep} interface extends {@link IMethodStep} and
 * provides additional methods for processing method steps related to Bloom
 * filters.
 * <p>
 * This interface includes an implementation for the
 * {@link IMethodStep#targetProfileClass()} method, specifying that the target
 * profile class is {@link MethodProfile}. It also implements the
 * {@link IMethodStep#process} method to create a {@link BloomFilter} and
 * populate it with information obtained from the normalized representation of
 * the provided {@link IMethod} instance.
 * </p>
 */
public interface IBloomMethodStep extends IMethodStep
{
    /**
     * Gets the target profile class for this method step, which is
     * {@link MethodProfile}.
     *
     * @return The target profile class.
     */
    @Override
    default @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        return MethodProfile.class;
    }

    /**
     * Processes the specified method reference and updates the target
     * {@link MethodProfile} by creating a new {@link BloomFilter} and
     * populating it with information obtained from the normalized
     * representation of the provided {@link IMethod} instance.
     *
     * @param ref    The method reference to process.
     * @param target The target method profile to update.
     */
    @Override
    default void process(@NotNull IMethod ref, @NotNull MethodProfile target)
    {
        BloomFilter filter = new BloomFilter();
        // Set the BloomFilter in the target MethodProfile
        Bloom.setFilter(target, filter);

        target.getManager()
                .getNormalizer()
                .normalize(ref)
                .forEach(filter::add);
    }
}
