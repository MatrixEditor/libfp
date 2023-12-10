package io.github.libfp.cha; //@date 24.10.2023

import io.github.libfp.VarInt;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
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
     *
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
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        VarInt.write(descriptor, out);
    }

    /**
     * Read the method profile's data from an external data input.
     *
     * @param in The DataInput stream to read the data from.
     *
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
}
