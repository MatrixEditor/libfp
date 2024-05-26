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

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.step.IClassStep;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.profile.ManagedProfile;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code IBloomClassStep} interface extends {@link IClassStep} and defines
 * additional methods for processing class steps related to Bloom filters.
 *
 * <p>
 * This interface provides an implementation for the
 * {@link IClassStep#targetProfileClass()} method, specifying that the target
 * profile class is {@link ClassProfile}. It also implements the
 * {@code IClassStep#process(IClass, ManagedProfile)} method to create a
 * {@link BloomFilter} and populate it with information obtained from the
 * bytecode document associated with the provided {@link IClass} instance.
 * </p>
 */
public interface IBloomClassStep extends IClassStep
{
    /**
     * Gets the target profile class for this class step, which is
     * {@link ClassProfile}.
     *
     * @return The target profile class.
     */
    @Override
    default @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        return ClassProfile.class;
    }

    /**
     * Processes the specified class reference and updates the target
     * {@link ClassProfile} by creating a new {@link BloomFilter} and populating
     * it with information obtained from the bytecode document associated with
     * the provided {@link IClass} instance.
     *
     * @param ref    The class reference to process.
     * @param target The target class profile to update.
     */
    @Override
    default void process(@NotNull IClass ref, @NotNull ClassProfile target)
    {
        BloomFilter filter = new BloomFilter();
        // Set the BloomFilter in the target ClassProfile
        Bloom.setFilter(target, filter);

        target.getManager()
                .getILFactory()
                .getDocument(ref)
                .forEach(filter::add);
    }
}
