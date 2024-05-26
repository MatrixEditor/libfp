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
package io.github.libfp.profile.manager;

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
