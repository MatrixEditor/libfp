package io.github.libfp.profile; //@date 23.10.2023

import io.github.libfp.profile.extensions.*;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Profile is an abstract base class for all library and application profiles.
 * It extends ManagedProfile and provides methods for managing class and method
 * profiles, package profiles, and handling profile similarity calculations.
 *
 * @param <C> the context used to build a profile
 *
 * @apiNote We won't store the context variable within one profile
 *         instance, because it would unnecessarily cost a lot of memory
 *         space to manage the context. Therefore, the context will be used
 *         only in profile generation.
 */
public abstract class Profile<C> extends ManagedProfile
{

    /**
     * Constructor for creating a Profile from a file.
     *
     * @param file     The file containing profile data.
     * @param manager  The ProfileManager associated with this profile.
     * @param strategy The strategy for profile management.
     *
     * @throws IOException if an I/O error occurs or if the file does not
     *                     exist.
     */
    public Profile(
            final @NotNull File file,
            final ProfileManager manager,
            final IStrategy<?> strategy) throws IOException
    {
        super(manager);
        getManager().setStrategy(strategy);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            readExternal(new DataInputStream(fis));
        }
    }

    /**
     * Constructor for creating a Profile from an AnalysisScope.
     *
     * @param context  The context for profile data extraction.
     * @param manager  The ProfileManager associated with this profile.
     * @param strategy The strategy for profile management.
     */
    public Profile(
            final @NotNull C context,
            final ProfileManager manager,
            final IStrategy<?> strategy)
            throws Exception
    {
        super(manager);
        getManager().setStrategy(strategy);
        process(context);
    }

    public abstract void process(final @NotNull C context)
            throws Exception;

    /**
     * Calculate the similarity between two profiles using a specified
     * similarity strategy.
     *
     * @param other  The other Profile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     *
     * @return The similarity score between the two profiles.
     */
    public double similarityTo(
            final Profile<C> other,
            final IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }



    /**
     * Get the ProfileInfo associated with this profile.
     *
     * @return The ProfileInfo object.
     */
    public @NotNull ProfileInfo getProfileInfo()
            throws IllegalStateException
    {
        return manager.getExtension(ProfileInfo.class);
    }

    /**
     * Save the profile data to a file.
     *
     * @param fileName The name of the file to save the profile data to.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void saveTo(final @NotNull String fileName) throws IOException
    {
        saveTo(new File(fileName));
    }

    /**
     * Save the profile data to a file.
     *
     * @param fileName The file to save the profile data to.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void saveTo(final @NotNull File fileName) throws IOException
    {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            writeExternal(new DataOutputStream(outputStream));
        }
    }

    /**
     * Read the profile data from an external data input.
     *
     * @param in The DataInput stream to read the data from.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public final void readExternal(@NotNull DataInput in) throws IOException
    {
        manager.readExternal(in);
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        manager.writeExternal(out);
    }

}
