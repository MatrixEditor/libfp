package io.github.libfp.profile.extensions; //@date 23.10.2023

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
     *
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
     *
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
