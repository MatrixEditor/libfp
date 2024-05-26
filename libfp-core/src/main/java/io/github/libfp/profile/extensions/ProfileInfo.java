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

import io.github.libfp.profile.manager.IExtension;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Provides information about a profile, including its version and flags.
 */
public class ProfileInfo extends IExtension
{

    /**
     * The version of the profile.
     */
    public int version;

    /**
     * Flags associated with the profile.
     */
    public int flags;

    /**
     * Creates a new ProfileInfo for an application profile by setting the
     * 'flags' to 1.
     *
     * @return A new ProfileInfo for an application profile.
     */
    public static @NotNull ProfileInfo newApplicationProfileInfo()
    {
        ProfileInfo info = new ProfileInfo();
        info.flags |= 1;
        return info;
    }

    /**
     * Writes the profile information to a data output stream.
     *
     * @param out The DataOutput stream to which the information is written.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeByte(version);
        out.writeByte(flags);
    }

    /**
     * Reads profile information from a data input stream (serialization).
     *
     * @param in The DataInput stream from which the information is read.
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        version = in.readByte();
        flags = in.readByte();
    }

    /**
     * Checks if the profile is an application profile based on the 'flags'
     * field.
     *
     * @return True if the profile is an application profile, false otherwise.
     */
    public final boolean isAppProfile()
    {
        return (flags & 1) == 1;
    }

    /**
     * Checks if the profile is a library profile based on the 'flags' field.
     *
     * @return True if the profile is a library profile, false otherwise.
     */
    public final boolean isLibProfile()
    {
        return !isAppProfile();
    }

    @Override
    public void reset()
    {
        flags = 0;
        version = 0;
    }
}
