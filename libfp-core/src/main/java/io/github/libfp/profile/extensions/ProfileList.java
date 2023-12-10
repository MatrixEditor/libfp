package io.github.libfp.profile.extensions; //@date 24.10.2023

import io.github.libfp.profile.IManagedProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.manager.IExtension;
import io.github.libfp.util.IO;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

/**
 * The ProfileList class is an abstract class that manages a list of profiles.
 *
 * @param <T> The type of ManagedProfile to be managed in the list.
 */
public abstract class ProfileList<T extends IManagedProfile> extends IExtension
        implements Iterable<T>
{

    protected final IManagedProfileFactory<T> factory;
    protected @NotNull List<T> profiles = new ArrayList<>();

    /**
     * Constructs a ProfileList with the given {@link IManagedProfileFactory}.
     *
     * @param factory The factory used to create new instances of profiles.
     */
    protected ProfileList(IManagedProfileFactory<T> factory)
    {
        this.factory = factory;
    }

    /**
     * Adds a new profile to the list and returns its index.
     *
     * @return The index of the newly added profile.
     */
    protected synchronized final int add()
    {
        T profile = factory.newInstance(getManager());
        final int index = profiles.size();
        profiles.add(profile);
        return index;
    }

    /**
     * Gets a profile from the list by its index.
     *
     * @param index The index of the profile to retrieve.
     *
     * @return The profile at the specified index.
     */
    public T get(final int index)
    {
        return profiles.get(index);
    }

    /**
     * Gets a collection of profiles from the list starting from the specified
     * index.
     *
     * @param start The starting index of the profiles to retrieve.
     * @param count The number of profiles to retrieve.
     *
     * @return A collection of profiles from the specified range.
     */
    public final @NotNull Collection<T> get(final int start, final int count)
    {
        return Collections.unmodifiableList(profiles.subList(start,
                start + count));
    }

    /**
     * Returns the number of profiles in the list.
     *
     * @return The number of profiles in the list.
     */
    public final int size()
    {
        return profiles.size();
    }

    /**
     * Provides an iterator to iterate through the profiles in the list.
     *
     * @return An iterator for the profiles in the list.
     */
    @Override
    public @NotNull Iterator<T> iterator()
    {
        return profiles.iterator();
    }

    public <U> @NotNull Iterator<U> asIterator(Class<U> type)
    {
        return profiles.stream()
                       .map(type::cast)
                       .toList()
                       .iterator();
    }

    /**
     * Reads the profiles from a data input stream and populates the list.
     *
     * @param in The DataInput stream from which to read the profiles.
     *
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int count = in.readInt();
        profiles = new ArrayList<>(count);
        read(in, factory, count, profiles);
    }

    /**
     * Writes the profiles from the list to a data output stream.
     *
     * @param out The DataOutput stream to which the profiles are written.
     *
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeInt(size());
        IO.writeList(profiles, out);
    }

    @Override
    public void reset()
    {
        profiles.clear();
    }
}
