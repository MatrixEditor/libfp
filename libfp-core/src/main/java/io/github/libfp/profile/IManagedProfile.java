package io.github.libfp.profile;//@date 27.10.2023

import io.github.libfp.ISerializable;
import io.github.libfp.profile.manager.ProfileManager;

/**
 * The {@code IManagedProfile} interface constructs the base class for all
 * managed profile classes. It is mainly used to serve the
 * {@link IManagedProfileFactory}.
 *
 * @see IManagedProfileFactory
 */
public interface IManagedProfile extends ISerializable
{
    /**
     * Gets the {@code ProfileManager} associated with the managed profile.
     *
     * @return The associated {@code ProfileManager}.
     */
    ProfileManager getManager();
}

