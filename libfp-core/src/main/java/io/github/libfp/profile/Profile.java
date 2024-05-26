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
package io.github.libfp.profile;

import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Profile is the base class for all library and application profiles. It
 * extends ManagedProfile and provides methods for managing class and method
 * profiles, package profiles, and handling profile similarity calculations.
 *
 * @implNote We won't store the context variable within one profile
 *         instance, because it would unnecessarily cost a lot of memory space
 *         to manage the context. Therefore, the context will be used only in
 *         profile generation.
 */
public abstract class Profile extends ManagedProfile
{

    /**
     * Constructor for creating a Profile.
     *
     * @param manager The ProfileManager associated with this profile.
     */
    public Profile(final ProfileManager manager)
    {
        super(manager);
    }

    /**
     * Calculate the similarity between two profiles using a specified
     * similarity strategy.
     *
     * @param other  The other Profile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     * @return The similarity score between the two profiles.
     */
    public double similarityTo(
            final Profile other,
            final IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }


    /**
     * Get the ProfileInfo associated with this profile.
     *
     * @return The ProfileInfo object.
     */
    public @NotNull ProfileInfo getProfileInfo()
            throws IllegalStateException
    {
        return manager.getExtension(ProfileInfo.class);
    }

    /**
     * Save the profile data to a file.
     *
     * @param fileName The name of the file to save the profile data to.
     * @throws IOException if an I/O error occurs.
     */
    public void saveTo(final @NotNull String fileName) throws IOException
    {
        saveTo(new File(fileName));
    }

    /**
     * Save the profile data to a file.
     *
     * @param fileName The file to save the profile data to.
     * @throws IOException if an I/O error occurs.
     */
    public void saveTo(final @NotNull File fileName) throws IOException
    {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            writeExternal(new DataOutputStream(outputStream));
        }
    }

    /**
     * Read the profile data from an external data input.
     *
     * @param in The DataInput stream to read the data from.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        manager.readExternal(in);
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        manager.writeExternal(out);
    }

}
