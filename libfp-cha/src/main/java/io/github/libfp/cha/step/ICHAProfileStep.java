package io.github.libfp.cha.step;//@date 10.11.2023

import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.cha.CHAProfile;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.features.IProfileStep;
import org.jetbrains.annotations.NotNull;

public interface ICHAProfileStep<T extends CHAProfile>
        extends IProfileStep<IClassHierarchy, T>
{
    @Override
    default @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        return CHAProfile.class;
    }
}
