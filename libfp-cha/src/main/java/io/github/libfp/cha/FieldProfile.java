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

import io.github.libfp.VarInt;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FieldProfile extends ExtensibleProfile
        implements IDescriptorContainer, IComparable<FieldProfile>
{

    /**
     * The descriptor associated with the field profile.
     */
    public int descriptor;

    /**
     * Constructs a FieldProfile associated with the given ProfileManager.
     *
     * @param manager The ProfileManager that manages this field profile.
     */
    public FieldProfile(ProfileManager manager)
    {
        super(manager);
    }

    /**
     * Calculates the similarity between this FieldProfile and another
     * FieldProfile based on the provided configuration.
     *
     * @param other  The other FieldProfile to compare to.
     * @param config The configuration for calculating similarity.
     * @return The similarity score between the two FieldProfiles.
     */
    @Override
    public double similarityTo(FieldProfile other, IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }

    /**
     * Gets the descriptor index associated with this FieldProfile.
     *
     * @return The descriptor index.
     */
    @Override
    public int getDescriptorIndex()
    {
        return descriptor;
    }

    /**
     * Reads the FieldProfile data from an input stream (serialization) and
     * populates the descriptor.
     *
     * @param in The DataInput stream from which to read the data.
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        descriptor = (int) VarInt.read(in);
    }

    /**
     * Writes the FieldProfile data to an output stream (serialization),
     * including the descriptor.
     *
     * @param out The DataOutput stream to which the data is written.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        VarInt.write(descriptor, out);
    }

    @Override
    protected String debugInfo()
    {
        return getDescriptor();
    }
}
