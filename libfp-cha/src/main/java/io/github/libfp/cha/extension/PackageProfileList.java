package io.github.libfp.cha.extension; //@date 24.10.2023

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
     *
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
