package io.github.libfp.profile.manager;//@date 24.10.2023

import io.github.libfp.ISerializable;
import io.github.libfp.profile.IManagedProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;

/**
 * IExtension (Profile Extension)
 * <p>
 * This abstract class represents a profile extension, providing basic
 * functionality for managing extensions within a ProfileManager. It implements
 * ISerializable to support serialization and deserialization.
 */
public abstract class IExtension implements ISerializable
{
    private ProfileManager manager;
    private RetentionPolicy retentionPolicy;

    /**
     * Gets the ProfileManager associated with this extension.
     *
     * @return The ProfileManager instance.
     */
    public ProfileManager getManager()
    {
        return manager;
    }

    /**
     * Sets the ProfileManager for this extension.
     *
     * @param manager The ProfileManager to set.
     */
    public void setManager(ProfileManager manager)
    {
        this.manager = manager;
    }

    public abstract void reset();

    public void setRetentionPolicy(RetentionPolicy retentionPolicy)
    {
        this.retentionPolicy = retentionPolicy;
    }

    public RetentionPolicy getRetention()
    {
        return retentionPolicy;
    }

    /**
     * Reads a collection of profiles from a DataInput stream and populates the
     * provided collection.
     *
     * @param in       The DataInput stream to read from.
     * @param factory  A factory for creating profile instances.
     * @param count    The number of profiles to read.
     * @param elements The collection to store the read profiles.
     * @param <T>      The generic type of the profiles to read.
     *
     * @throws IOException If an I/O error occurs during reading.
     */
    protected final <T extends IManagedProfile> void read(
            @NotNull DataInput in,
            @NotNull IManagedProfileFactory<T> factory,
            final int count,
            @NotNull Collection<T> elements) throws IOException
    {
        for (int i = 0; i < count; i++) {
            T profile = factory.newInstance(manager);
            profile.readExternal(in);
            elements.add(profile);
        }
    }
}
