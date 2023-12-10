package io.github.libfp.cha.extension; //@date 24.10.2023

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.IClassProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The ClassProfileList class is a specific implementation of the ProfileList
 * class for managing a list of ClassProfile instances.
 *
 * @see ProfileList
 * @see ClassProfile
 * @see IClassProfileFactory
 */
public final class ClassProfileList extends ProfileList<ClassProfile>
{

    // only during the generation process
    private final Map<IClass, Integer> classProfileMap = new HashMap<>();

    /**
     * Constructs a ClassProfileList with the given
     * {@link IClassProfileFactory}.
     */
    public ClassProfileList()
    {
        this(ClassProfile::new);
    }

    /**
     * Constructs a ClassProfileList with the given
     * {@link IClassProfileFactory}.
     *
     * @param factory The factory used to create new instances of ClassProfile.
     */
    public ClassProfileList(IClassProfileFactory factory)
    {
        super(factory);
    }

    /**
     * Adds a new class profile to the list associated with the given IClass.
     *
     * @param iClass The IClass for which to create a class profile.
     *
     * @return The index of the newly added class profile in the list.
     */
    public int addClass(final IClass iClass)
    {
        final int index = add();
        final ClassProfile profile = get(index);

        if (getManager().hasExtension(Descriptors.class)) {
            final String descriptor = getManager()
                    .getILFactory()
                    .getDescriptor(iClass);

            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(descriptor);
        }

        classProfileMap.putIfAbsent(iClass, index);
        return index;
    }

    public @Nullable ClassProfile getClassProfile(final IClass iClass)
    {
        final int index = getClassProfileIndex(iClass);
        if (index == -1) {
            return null;
        }
        return get(index);
    }

    public int getClassProfileIndex(final IClass iClass)
    {
        return classProfileMap.getOrDefault(iClass, -1);
    }

}
