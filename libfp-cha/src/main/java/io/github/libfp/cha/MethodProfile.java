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
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * MethodProfile is an abstract class representing a method-level profile. It
 * extends {@link ManagedProfile} and provides methods for managing a descriptor
 * and calculating similarity with other MethodProfiles. It also implements the
 * {@link IDescriptorContainer} interface.
 */
public class MethodProfile extends ExtensibleProfile
        implements IComparable<MethodProfile>, IDescriptorContainer
{

    /**
     * The descriptor associated with the method profile.
     */
    public int descriptor;

    /**
     * Constructor for MethodProfile.
     *
     * @param manager The ProfileManager associated with this method profile.
     */
    public MethodProfile(ProfileManager manager)
    {
        super(manager);
    }

    @Override
    public int getDescriptorIndex()
    {
        return descriptor;
    }

    /**
     * Calculate the similarity between two MethodProfiles using a specified
     * similarity strategy.
     *
     * @param other  The other MethodProfile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     * @return The similarity score between the two MethodProfiles.
     */
    @Override
    public double similarityTo(MethodProfile other, IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }

    /**
     * Write the method profile's data to an external data output.
     *
     * @param out The DataOutput stream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        if (descriptor < 0) {
            throw new IllegalArgumentException(
                    "Invalid descriptor: " + descriptor);
        }
        VarInt.write(descriptor, out);
    }

    /**
     * Read the method profile's data from an external data input.
     *
     * @param in The DataInput stream to read the data from.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        descriptor = (int) VarInt.read(in);
    }

    @Override
    protected String debugInfo()
    {
        return getDescriptor();
    }

    public static final class Factory
            implements IManagedProfileFactory<MethodProfile>
    {

        @Override
        public MethodProfile newInstance(ProfileManager manager)
        {
            return new MethodProfile(manager);
        }
    }
}
