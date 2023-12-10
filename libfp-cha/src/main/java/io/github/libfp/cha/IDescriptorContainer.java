package io.github.libfp.cha;//@date 25.10.2023

import io.github.libfp.profile.IManagedProfile;
import io.github.libfp.profile.extensions.Descriptors;

/**
 * The IDescriptorContainer interface represents an entity that can provide a
 * descriptor. Classes implementing this interface should provide a method to
 * retrieve a descriptor string.
 *
 * @see ClassProfile
 * @see MethodProfile
 */
public interface IDescriptorContainer extends IManagedProfile
{

    /**
     * Get the descriptor associated with the implementing entity.
     *
     * @return The descriptor string.
     */
    default String getDescriptor()
    {
        return getManager()
                .getExtension(Descriptors.class)
                .getDescriptor(getDescriptorIndex())
                .toString();
    }

    int getDescriptorIndex();

}
