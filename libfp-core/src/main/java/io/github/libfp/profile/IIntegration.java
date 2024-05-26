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

import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code IIntegration} interface provides an adapter-like implementation
 * for integrating profiles. It defines methods for updating a profile manager
 * with a given blueprint, adding a profile step, and setting a profile
 * strategy.
 */
public interface IIntegration /* for profiles */
{
    /**
     * Updates the profile manager with the specified managed profile factory.
     *
     * @param manager The profile manager to update.
     * @param factory The managed profile factory.
     * @param <E>     The type of the extensible profile.
     */
    default <E extends ExtensibleProfile> void update(
            @NotNull ProfileManager manager,
            @NotNull IManagedProfileFactory<E> factory)
    {
        update(manager, Blueprint.make(factory));
    }

    /**
     * Updates the profile manager with the specified blueprint.
     *
     * @param <E>       The type of the extensible profile.
     * @param blueprint The blueprint to use for the update.
     * @param type      the type of the extensible profile
     */
    default <E extends ExtensibleProfile> void updateBlueprint(
            @NotNull Blueprint<E> blueprint, @NotNull Class<E> type)
    {
    }

    /**
     * Updates the profile manager with the specified blueprint.
     *
     * @param manager   The profile manager to update.
     * @param blueprint The blueprint to use for the update.
     * @param <E>       The type of the extensible profile.
     */
    default <E extends ExtensibleProfile> void update(
            @NotNull ProfileManager manager,
            @NotNull Blueprint<E> blueprint)
    {
        E e = blueprint.newInstance(manager);
        //noinspection unchecked
        updateBlueprint(blueprint, (Class<E>) e.getClass());
        manager.with(blueprint);
    }

    default void addProfileStep(IStrategy<?> strategy)
    {
    }

    /**
     * Sets the profile strategy for the integration.
     *
     * @param strategy The profile strategy to set.
     */
    default void setProfileStrategy(IStrategy<?> strategy)
    {
    }

    default void setPolicies(IStrategy<?> strategy)
    {
    }
}
