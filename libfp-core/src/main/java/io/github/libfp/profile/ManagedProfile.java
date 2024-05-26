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
package io.github.libfp.profile;

import io.github.libfp.profile.extensions.ProfileList;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.similarity.Strategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

/**
 * This abstract class represents a managed profile, which is associated with a
 * ProfileManager. It provides a reference to the managing ProfileManager, a
 * method for calculating the similarity between profiles, and a customized
 * implementation of the toString method.
 */
public abstract class ManagedProfile implements IManagedProfile
{
    protected final ProfileManager manager;

    /**
     * Constructs a ManagedProfile associated with the given ProfileManager.
     *
     * @param manager The ProfileManager that manages this profile.
     */
    protected ManagedProfile(ProfileManager manager)
    {
        this.manager = manager;
    }

    /**
     * Gets the ProfileManager associated with this profile.
     *
     * @return The managing ProfileManager.
     */
    @Override
    public ProfileManager getManager()
    {
        return manager;
    }

    /**
     * Calculate the similarity between two profiles of the same type using a
     * specified similarity strategy.
     *
     * @param type   The class type of the profiles.
     * @param first  The first profile to compare.
     * @param second The second profile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     * @param <T>    The generic type of the profiles.
     * @return The similarity score between the two profiles.
     * @throws IllegalStateException If the {@link Strategy} is not set in the
     *                               ProfileManager.
     */
    protected final <T extends ManagedProfile> double similarityTo(
            @NotNull Class<? extends T> type,
            T first,
            T second,
            IThresholdConfig config)
    {
        final IStrategy<?> strategy = manager.getStrategy();
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set!");
        }

        final ISimilarityStrategy<T> similarityStrategy =
                strategy.getSimilarityStrategy(type);

        if (similarityStrategy == null) {
            return 0;
        }
        return similarityStrategy.similarityOf(first, second, config);
    }

    protected final <M extends ManagedProfile> @NotNull Collection<M> getCollection(
            final @NotNull Class<? extends ProfileList<M>> listType,
            @Range(from = 0, to = Integer.MAX_VALUE) int start,
            @Range(from = 0, to = Integer.MAX_VALUE) int count)
    {
        if (!manager.hasExtension(listType)) {
            return Collections.emptyList();
        }

        return manager.getExtension(listType).get(start, count);
    }

    /**
     * Provides a customized string representation of the profile.
     *
     * @return A string representing the profile with its class name and an
     *         identifier indicating whether it is an "Application" or "Library"
     *         profile.
     */
    @Override
    public String toString()
    {
        StringJoiner builder = new StringJoiner(",", "<", ">");
        builder.add(getClass().getSimpleName());

        String id = getManager().isAppProfile() ? "Application" : "Library";
        return builder.add(id).add(debugInfo()).toString();
    }

    protected String debugInfo()
    {
        return "";
    }
}
