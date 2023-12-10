package io.github.libfp.profile;//@date 13.11.2023

import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code IProfileContext<C>} interface represents a profile context
 * parameterized by type {@code C}. It defines methods to create a new
 * {@code ProfileManager}, access a {@code ProfileBuilder}, a
 * {@code ProfileFactory}, retrieve the current strategy, and get the
 * integration associated with the profile context.
 *
 * @see ProfileManager
 * @see IProfileManagerFactory
 * @see IProfileBuilder
 * @see IProfileFactory
 * @see IStrategy
 * @see IIntegration
 */
public interface IProfileContext<C>
{
    /**
     * Gets the {@code IProfileManagerFactory} associated with the profile
     * context.
     *
     * @return The {@code IProfileManagerFactory} instance.
     */
    default @NotNull IProfileManagerFactory getManagerFactory()
    {
        return this::newProfileManager;
    }

    /**
     * Creates a new {@code ProfileManager} instance.
     *
     * @return A new {@code ProfileManager}.
     */
    @NotNull ProfileManager newProfileManager();

    /**
     * Gets the {@code IProfileBuilder} associated with the profile context.
     *
     * @return The {@code IProfileBuilder} instance.
     */
    @NotNull IProfileBuilder<C> getProfileBuilder();

    /**
     * Gets the {@code IProfileFactory} associated with the profile context.
     *
     * @return The {@code IProfileFactory} instance.
     */
    @NotNull IProfileFactory<C> getProfileFactory();

    /**
     * Gets the current strategy associated with the profile context.
     *
     * @return The current {@code IStrategy} instance.
     */
    @NotNull IStrategy<?> getStrategy();

    /**
     * Gets the integration associated with the profile context.
     *
     * @return The {@code IIntegration} instance, or {@code null} if not
     *         available.
     */
    @Nullable IIntegration getIntegration();
}
