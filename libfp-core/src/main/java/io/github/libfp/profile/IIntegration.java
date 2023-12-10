package io.github.libfp.profile;//@date 13.11.2023

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
     * @param blueprint The blueprint to use for the update.
     * @param <E>       The type of the extensible profile.
     */
    default <E extends ExtensibleProfile> void updateBlueprint(
            @NotNull Blueprint<E> blueprint)
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
        updateBlueprint(blueprint);
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
