package io.github.libfp.profile;//@date 23.10.2023

import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for building profiles using an input context.
 *
 * @param <C> The type of context associated with the profile.
 */
@FunctionalInterface
public interface IProfileBuilder<C>
{

    /**
     * Builds a profile using the provided context, profile manager, and
     * strategy.
     *
     * @param context  The context associated with the profile.
     * @param manager  The profile manager for managing profiles.
     * @param strategy The strategy used for building the profile.
     *
     * @return The built profile.
     * @throws Exception If an error occurs during the profile building
     *                   process.
     */
    Profile<C> build(
            final @NotNull C context,
            final @NotNull ProfileManager manager,
            final @NotNull IStrategy<?> strategy) throws Exception;
}


