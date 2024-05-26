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
package io.github.libfp.cha;

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This abstract class represents a class profile that extends ManagedProfile.
 * It implements the {@link IComparable} and {@link IDescriptorContainer}
 * interfaces, providing methods for calculating similarity between class
 * profiles and obtaining descriptor information. It also includes serialization
 * methods for reading and writing data.
 */
public class ClassProfile extends ExtensibleProfile
        implements ISerializable, IComparable<ClassProfile>,
        IDescriptorContainer
{
    public int descriptor;

    /**
     * Constructs a ClassProfile associated with the given ProfileManager.
     *
     * @param manager The ProfileManager that manages this class profile.
     */
    public ClassProfile(ProfileManager manager)
    {
        super(manager);
    }

    @Override
    public int getDescriptorIndex()
    {
        return descriptor;
    }

    /**
     * Calculates the similarity between two class profiles using a specified
     * threshold configuration.
     *
     * @param other  The other class profile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     * @return The similarity score between the two class profiles.
     */
    @Override
    public double similarityTo(ClassProfile other, IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }

    /**
     * Reads the class profile's descriptor from the provided DataInput stream.
     *
     * @param in The DataInput stream to read from.
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        descriptor = (int) VarInt.read(in);
    }

    /**
     * Writes the class profile's descriptor to the provided DataOutput stream.
     *
     * @param out The DataOutput stream to write to.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        if (descriptor < 0) {
            throw new IllegalArgumentException(
                    "Invalid class descriptor: " + descriptor);
        }
        VarInt.write(descriptor, out);
    }

    @Override
    protected String debugInfo()
    {
        return getDescriptor();
    }

    public static final class Factory implements IClassProfileFactory
    {

        @Override
        public ClassProfile newInstance(ProfileManager manager)
        {
            return new ClassProfile(manager);
        }
    }
}
