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
package io.github.libfp.cha.extension;

import io.github.libfp.cha.PackageProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.Nullable;

/**
 * The PackageProfileList class is a specific implementation of the ProfileList
 * class for managing a list of PackageProfile instances.
 * <p>
 * If this extension is added to the manager, all packages will be constructed
 * to support a package tree hierarchy.
 */
public final class PackageProfileList extends ProfileList<PackageProfile>
{

    /**
     * Constructs a PackageProfileList with the given ManagedProfileFactory.
     */
    public PackageProfileList()
    {
        this(PackageProfile::new);
    }

    /**
     * Constructs a PackageProfileList with the given ManagedProfileFactory.
     *
     * @param factory The factory used to create new instances of
     *                PackageProfile.
     */
    public PackageProfileList(IManagedProfileFactory<PackageProfile> factory)
    {
        super(factory);
    }

    public PackageProfile getRootPackage()
    {
        return get(0);
    }

    @Override
    public PackageProfile get(int index)
    {
        if (size() == 0 && index == 0) {
            addPackage(null);
        }
        return super.get(index);
    }

    /**
     * Adds a new package profile to the list associated with the given package
     * name.
     *
     * @param name The name of the package to create a package profile for.
     * @return The index of the newly added package profile in the list.
     */
    public int addPackage(final @Nullable String name)
    {
        if (size() == 0 && name != null) {
            // require root package
            addPackage(null);
        }

        PackageProfile profile = factory.newInstance(getManager());
        profile.nameHash = name == null
                ? 0
                : name.hashCode(); // REVISIT: hash creation
        if (name == null) {
            profile.depth = 0;
        } else {
            // "$"           -> 0
            // "com"         -> 1
            // "com.example" -> 2
            profile.depth = name
                    .chars()
                    .filter(x -> x == '.')
                    .sum() + 1;
        }

        int index = profiles.indexOf(profile);
        if (index == -1) {
            index = profiles.size();
            profiles.add(profile);
        }
        return index;
    }
}
