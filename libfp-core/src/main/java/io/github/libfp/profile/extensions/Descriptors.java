package io.github.libfp.profile.extensions; //@date 24.10.2023

import io.github.libfp.Descriptor;
import io.github.libfp.profile.manager.IExtension;
import io.github.libfp.util.IO;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Descriptors class is responsible for managing a list of Descriptor
 * instances, which are used to store and count unique descriptor values.
 */
public final class Descriptors extends IExtension
{
    // we use an array list here to save time when accessing the data
    private @NotNull List<Descriptor> descriptors = new ArrayList<>();

    /**
     * Adds a descriptor with the given value to the list of descriptors. If a
     * descriptor with the same value exists, it increments its count;
     * otherwise, it adds a new Descriptor instance.
     *
     * @param value The descriptor value to add.
     *
     * @return The index of the added or existing descriptor in the list.
     */
    public synchronized int addDescriptor(final @NotNull String value)
    {
        int index = descriptors.indexOf(new Descriptor(value));

        if (index != -1) {
            descriptors.get(index).count++;
        } else {
            Descriptor descriptor = new Descriptor(value);
            descriptor.count++;
            index = descriptors.size();
            descriptors.add(descriptor);
        }
        return index;
    }

    /**
     * Retrieves the Descriptor instance at the specified index in the list.
     *
     * @param index The index of the descriptor to retrieve.
     *
     * @return The Descriptor instance at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Descriptor getDescriptor(final int index)
            throws IndexOutOfBoundsException
    {
        return descriptors.get(index);
    }

    /**
     * Gets the total count of descriptors stored in the list.
     *
     * @return The count of descriptors in the list.
     */
    public int getDescriptorCount()
    {
        return descriptors.size();
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeInt(getDescriptorCount());
        IO.writeList(descriptors, out);
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int count = in.readInt();
        descriptors = new ArrayList<>(count);

        // Read and populate the list with Descriptor instances.
        for (int i = 0; i < count; i++) {
            Descriptor profile = new Descriptor();
            profile.readExternal(in);
            descriptors.add(profile);
        }
    }

    @Override
    public void reset()
    {
        descriptors.clear();
    }

}
