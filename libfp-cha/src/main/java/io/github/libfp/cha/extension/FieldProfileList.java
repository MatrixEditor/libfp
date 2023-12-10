package io.github.libfp.cha.extension; //@date 27.10.2023

import com.ibm.wala.classLoader.IField;
import io.github.libfp.cha.FieldProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.NotNull;

/**
 * The FieldProfileList class is a specific implementation of the ProfileList
 * class for managing a list of FieldProfile instances.
 */
public final class FieldProfileList extends ProfileList<FieldProfile>
{

    /**
     * Constructs a FieldProfileList with the given ManagedProfileFactory.
     */
    public FieldProfileList()
    {
        this(FieldProfile::new);
    }

    /**
     * Constructs a FieldProfileList with the given ManagedProfileFactory.
     *
     * @param factory The factory used to create new instances of FieldProfile.
     */
    public FieldProfileList(IManagedProfileFactory<FieldProfile> factory)
    {
        super(factory);
    }

    /**
     * Adds a new field profile to the list associated with the given IField.
     *
     * @param field The IField for which to create a field profile.
     *
     * @return The index of the newly added field profile in the list.
     */
    public int addField(final @NotNull IField field)
    {
        final int index = add();
        FieldProfile profile = factory.newInstance(getManager());

        if (getManager().hasExtension(Descriptors.class)) {
            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(getManager()
                            .getILFactory()
                            .getDescriptor(field));
        }

        profiles.add(profile);
        return index;
    }
}
