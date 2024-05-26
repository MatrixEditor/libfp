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
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.PackageProfileList;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IComparable;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * PackageProfile is an abstract class representing a package-level profile. It
 * extends ManagedProfile and provides methods for managing a set of classes and
 * calculating similarity with other PackageProfiles.
 */
public class PackageProfile extends ExtensibleProfile
        implements IComparable<PackageProfile>, Iterable<PackageProfile>
{

    /**
     * The hash code associated with the package profile.
     */
    public int nameHash;

    public int depth;

    public int parent;

    /**
     * A set of integers representing the classes associated with this package.
     */
    private Set<Integer> classes;

    /**
     * A set of integers representing the sub-packages associated with this
     * package.
     */
    private Set<Integer> packages;

    /**
     * Constructor for PackageProfile.
     *
     * @param manager The ProfileManager associated with this package profile.
     */
    public PackageProfile(ProfileManager manager)
    {
        super(manager);
        this.classes = new HashSet<>();
        this.packages = new HashSet<>();
    }

    /**
     * Calculate the similarity between two PackageProfiles using a specified
     * similarity strategy.
     *
     * @param other  The other PackageProfile to compare.
     * @param config The threshold configuration for the similarity
     *               calculation.
     * @return The similarity score between the two PackageProfiles.
     */
    @Override
    public double similarityTo(PackageProfile other, IThresholdConfig config)
    {
        return getManager().isAppProfile()
                ? similarityTo(getClass(), this, other, config)
                : similarityTo(getClass(), other, this, config);
    }

    /**
     * Get a collection of {@link ClassProfile} objects associated with this
     * package profile.
     *
     * @return A collection of {@link ClassProfile} objects representing the
     *         classes in this package.
     */
    public final @NotNull Collection<ClassProfile> getClasses()
    {
        List<ClassProfile> profiles = new ArrayList<>(getClassCount());
        for (final int index : classes) {
            profiles.add(manager
                    .getExtension(ClassProfileList.class)
                    .get(index));
        }
        return Collections.unmodifiableCollection(profiles);
    }

    public final @NotNull Collection<ClassProfile> getAllClasses()
    {
        List<ClassProfile> profiles = new LinkedList<>(getClasses());
        if (getSubPackageCount() != 0) {
            for (final PackageProfile packageProfile : getSubPackages()) {
                profiles.addAll(packageProfile.getAllClasses());
            }
        }
        return Collections.unmodifiableCollection(profiles);
    }

    public final @NotNull Collection<PackageProfile> getSubPackages()
    {
        List<PackageProfile> profiles = new ArrayList<>(getSubPackageCount());
        for (final int index : packages) {
            profiles.add(manager
                    .getExtension(PackageProfileList.class)
                    .get(index));
        }
        return Collections.unmodifiableCollection(profiles);
    }

    public final void visitAllPackages(
            @NotNull Consumer<?
                    super PackageProfile> consumer)
    {
        consumer.accept(this);
        for (final PackageProfile profile : getSubPackages()) {
            profile.visitAllPackages(consumer);
        }
    }

    public final void visitAllClasses(
            @NotNull Consumer<Collection<ClassProfile>> consumer)
    {
        consumer.accept(getAllClasses());
        for (final PackageProfile profile : getSubPackages()) {
            profile.visitAllClasses(consumer);
        }
    }

    public final PackageProfile getParent()
    {
        return getManager()
                .getExtension(PackageProfileList.class)
                .get(parent);
    }

    public final void addPackage(final int index)
    {
        packages.add(index);
    }

    public final int getSubPackageCount()
    {
        return packages.size();
    }

    /**
     * Add a class to the package profile using its index.
     *
     * @param index The index of the class to add.
     */
    public final void addClass(final int index)
    {
        classes.add(index);
    }

    /**
     * Get the count of classes associated with this package profile.
     *
     * @return The count of classes in this package.
     */
    public final int getClassCount()
    {
        return classes.size();
    }

    /**
     * Write the package profile's data to an external data output.
     *
     * @param out The DataOutput stream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        out.writeInt(nameHash);
        VarInt.write(depth, out);
        VarInt.write(getClassCount(), out);
        for (final int index : classes) VarInt.write(index, out);

        VarInt.write(getSubPackageCount(), out);
        for (final int index : packages) VarInt.write(index, out);
    }

    /**
     * Read the package profile's data from an external data input.
     *
     * @param in The DataInput stream to read the data from.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        nameHash = in.readInt();
        depth = (int) VarInt.read(in);

        final int classCount = (int) VarInt.read(in);
        classes = new HashSet<>(classCount);
        for (int i = 0; i < classCount; i++) {
            addClass((int) VarInt.read(in));
        }

        final int packageCount = (int) VarInt.read(in);
        packages = new HashSet<>(packageCount);
        for (int i = 0; i < packageCount; i++) {
            addPackage((int) VarInt.read(in));
        }
    }

    /**
     * Compare the package profile with another object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PackageProfile)) {
            return false;
        }
        return obj.hashCode() == nameHash;
    }

    /**
     * Compute the hash code for the package profile.
     *
     * @return The hash code of the package profile.
     */
    @Override
    public int hashCode()
    {
        return nameHash;
    }

    @Override
    public @NotNull Iterator<PackageProfile> iterator()
    {
        return getSubPackages().iterator();
    }

    @Override
    protected @NotNull String debugInfo()
    {
        return String.valueOf(nameHash);
    }

    public static final class Factory
            implements IManagedProfileFactory<PackageProfile>
    {
        @Override
        public @NotNull PackageProfile newInstance(ProfileManager manager)
        {
            return new PackageProfile(manager);
        }
    }
}
