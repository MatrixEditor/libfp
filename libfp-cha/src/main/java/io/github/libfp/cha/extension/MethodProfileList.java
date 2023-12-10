package io.github.libfp.cha.extension; //@date 24.10.2023

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.NotNull;

public final class MethodProfileList extends ProfileList<MethodProfile>
{

    public MethodProfileList()
    {
        this(MethodProfile::new);
    }

    public MethodProfileList(IManagedProfileFactory<MethodProfile> factory)
    {
        super(factory);
    }

    public int addMethod(final @NotNull IMethod iMethod)
    {
        final int index = add();
        final MethodProfile profile = get(index);
        if (getManager().hasExtension(Descriptors.class)) {
            final String descriptor = getManager()
                    .getILFactory()
                    .getDescriptor(iMethod);

            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(descriptor);
        }
        return index;
    }
}
