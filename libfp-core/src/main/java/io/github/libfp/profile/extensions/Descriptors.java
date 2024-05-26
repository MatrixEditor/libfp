/*
 * MIT License
 *
 * Copyright (c) 2024 MatrixEditor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.libfp.profile.extensions;

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
 * <pre>
 * +----------------------+
 * |     Descriptors      |
 * +----------------------+
 * | count                | --> [int value from in.readInt()]
 * | descriptors          | --> [reference to ArrayList]
 * |    +-----------------+
 * |    | ArrayList       |
 * |    +-----------------+
 * |    | size: count     | --> [number of Descriptor objects]
 * |    | [0]             | --> [reference to Descriptor]
 * |    | [1]             | --> [reference to Descriptor]
 * |    | ...             |
 * |    | [count-1]       | --> [reference to Descriptor]
 * +----------------------+
 * </pre>
 *
 * @implNote This class is not thread-safe and does not allow any duplicates.
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
