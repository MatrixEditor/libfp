package io.github.libfp.profile;//@date 23.10.2023

import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Functional interface for creating profile instances (import).
 *
 * @param <C> The type of context associated with the profile.
 */
@FunctionalInterface
public interface IProfileFactory<C>
{

    /**
     * Creates a new profile instance using the provided file, profile manager,
     * and strategy.
     *
     * @param file     The file associated with the profile.
     * @param manager  The profile manager for managing profiles.
     * @param strategy The strategy used for creating the profile.
     *
     * @return The newly created profile instance.
     * @throws IOException If an I/O error occurs during the profile creation
     *                     process.
     */
    Profile<C> newInstance(
            final @NotNull File file,
            final @NotNull ProfileManager manager,
            final @NotNull IStrategy<?> strategy) throws IOException;
}


