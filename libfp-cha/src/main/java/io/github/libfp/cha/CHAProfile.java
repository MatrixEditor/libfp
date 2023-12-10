package io.github.libfp.cha; //@date 23.10.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.FieldProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.cha.extension.PackageProfileList;
import io.github.libfp.profile.Profile;
import io.github.libfp.profile.features.IStep;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class CHAProfile extends Profile<AnalysisScope>
{
    public CHAProfile(
            @NotNull File file,
            ProfileManager manager,
            IStrategy<?> strategy)
            throws IOException
    {
        super(file, manager, strategy);
    }

    public CHAProfile(
            @NotNull AnalysisScope context,
            ProfileManager manager,
            IStrategy<?> strategy) throws Exception
    {
        super(context, manager, strategy);
    }

    @Override
    public void process(final AnalysisScope scope) throws Exception
    {
        final IClassHierarchy hierarchy =
                ClassHierarchyFactory.makeWithRoot(scope);

        final Collection<IStep<IClassHierarchy, CHAProfile>> extractors =
                getManager().getStrategy()
                            .getFeatureExtractors(CHAProfile.class);

        for (final IStep<IClassHierarchy, CHAProfile> step : extractors) {
            if (step.test(getClass())) {
                step.process(hierarchy, this);
            }
        }
    }

    /**
     * Get the list of ClassProfile objects associated with this profile.
     *
     * @return The list of ClassProfile objects.
     */
    public @NotNull ClassProfileList getClasses() throws IllegalStateException
    {
        return manager.getExtension(ClassProfileList.class);
    }

    public @NotNull FieldProfileList getFields() throws IllegalStateException
    {
        return manager.getExtension(FieldProfileList.class);
    }

    /**
     * Get the list of MethodProfile objects associated with this profile.
     *
     * @return The list of MethodProfile objects.
     */
    public @NotNull MethodProfileList getMethods() throws IllegalStateException
    {
        return manager.getExtension(MethodProfileList.class);
    }

    /**
     * Get the list of PackageProfile objects associated with this profile.
     *
     * @return The list of PackageProfile objects.
     */
    public @NotNull PackageProfileList getPackages()
            throws IllegalStateException
    {
        return manager.getExtension(PackageProfileList.class);
    }

}
