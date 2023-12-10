package io.github.libfp.cha;//@date 24.10.2023

import io.github.libfp.profile.IManagedProfileFactory;

/**
 * The IClassProfileFactory is a functional interface that extends the
 * ManagedProfileFactory for creating instances of ClassProfile.
 */
@FunctionalInterface
public interface IClassProfileFactory
        extends IManagedProfileFactory<ClassProfile>
{

    // This interface inherits the newInstance method from ManagedProfileFactory
    // for creating instances of ClassProfile with the associated
    // ProfileManager.
}

