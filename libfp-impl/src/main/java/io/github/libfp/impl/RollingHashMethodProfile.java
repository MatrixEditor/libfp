package io.github.libfp.impl; //@date 24.10.2023

import io.github.libfp.cha.MethodProfile;
import io.github.libfp.hash.RollingHash;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The RollingHashMethodProfile class extends {@link MethodProfile} and provides
 * additional behavior related to rolling hash calculations and similarity
 * calculations specific to method profiles.
 * <p>
 * The {@link #similarityTo(MethodProfile, IThresholdConfig)} method calculates
 * the similarity between two {@link RollingHashMethodProfile} objects using a
 * rolling hash technique. Here's how the similarity is calculated:
 * <ul>
 *     <li>
 *     If the initial similarity score is 0.0 (indicating no similarity),
 *     the method returns 0.0 immediately.
 *     </li>
 *     <li>
 *     If there is some initial similarity, the method proceeds with the
 *     rolling hash similarity calculation.
 *     </li>
 *     <li>
 *     It calculates the rolling hash similarity by iterating over the
 *     hash values of the current {@link RollingHashMethodProfile} (this
 *     .hash) and checking how many of these values are also contained in the
 *     hash of the other {@link RollingHashMethodProfile} (appProfile.hash).
 *     </li>
 *     <li>
 *     The rolling hash similarity is calculated as {@code (count * 1.0) /
 *     size},
 *     where count is the number of common hash values, and size is the total
 *     number of hash values in the current profile's rolling hash.
 *     </li>
 * </ul>
 *
 * @see MethodProfile
 * @see RollingHashMethodLayer
 */
public class RollingHashMethodProfile extends MethodProfile
{

    /**
     * The rolling hash for this method profile.
     */
    public RollingHash hash;

    /**
     * Constructs a RollingHashMethodProfile with the given ProfileManager.
     *
     * @param manager The ProfileManager to associate with this method profile.
     */
    public RollingHashMethodProfile(ProfileManager manager)
    {
        super(manager);
    }

    /**
     * Writes this method profile's data to a DataOutput.
     *
     * @param out The {@link DataOutput} to write the data to.
     *
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        hash.writeExternal(out);
    }

    /**
     * Reads this method profile's data from a DataInput.
     *
     * @param in The DataInput to read the data from.
     *
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        hash = new RollingHash(in);
    }
}
