package io.github.libfp.profile;//@date 23.10.2023

import io.github.libfp.profile.manager.ProfileManager;

/**
 * The ManagedProfileFactory is a functional interface that defines a factory
 * method for creating instances of {@link IManagedProfile} or its subtypes.
 *
 * @param <T> The type of ManagedProfile or its subtypes to be created.
 */
@FunctionalInterface
public interface IManagedProfileFactory<T extends IManagedProfile>
{

    /**
     * Create a new instance of a ManagedProfile or its subtype, associated with
     * a ProfileManager.
     *
     * @param manager The ProfileManager to associate with the created profile.
     *
     * @return A new instance of a ManagedProfile or its subtype.
     */
    T newInstance(final ProfileManager manager);
}
