package io.github.libfp.impl.hierarchy; //@date 23.10.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class HierarchyProfile extends CHAProfile
{


    public HierarchyProfile(
            @NotNull File file,
            ProfileManager manager,
            IStrategy<?> strategy) throws IOException
    {
        super(file, manager, strategy);
    }

    public HierarchyProfile(
            @NotNull AnalysisScope context,
            ProfileManager manager,
            IStrategy<?> strategy) throws Exception
    {
        super(context, manager, strategy);
    }

    public static @NotNull ProfileManager getManagerInstance()
    {
        return new ProfileManager(new FuzzyHierarchyILFactory(), null)
                .with(new ProfileInfo())
                .with(new ClassProfileList(HierarchyClassProfile::new))
                .with(new Descriptors());
    }

}
