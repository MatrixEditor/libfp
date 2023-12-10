package io.github.libfp.impl.hierarchy; //@date 25.10.2023

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.*;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.step.IClassStep;
import io.github.libfp.hash.RollingHash;
import io.github.libfp.impl.Strategies;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HierarchyStrategies
        implements ICHAIntegration, ICHAProfileContext
{

    private static final HierarchyStrategies instance =
            new HierarchyStrategies();

    private static final String blueprintKey = "signatures";

    private HierarchyStrategies()
    {
    }

    public static HierarchyStrategies getInstance()
    {
        return instance;
    }

    public static RollingHash getSignatures(ExtensibleProfile profile)
    {
        return Blueprint.getOrThrow(profile, blueprintKey);
    }

    public static void setSignatures(
            ExtensibleProfile profile,
            RollingHash hash)
    {
        profile.put(blueprintKey, hash);
    }

    public static @NotNull CHAStrategy defaultStrategy()
    {
        return getInstance().getStrategy();
    }

    /**
     * HierarchyDescriptorFilter + rolling-hash + simple matching
     */
    public static final class ClassStrategy
            implements ISimilarityStrategy<ClassProfile>,
                       IClassStep
    {

        @Override
        public double similarityOf(
                @NotNull ClassProfile app,
                @NotNull ClassProfile lib,
                IThresholdConfig config)
        {
            if (!app.getDescriptor().equals(lib.getDescriptor())) {
                return 0;
            }

            RollingHash appSignatures = getSignatures(app);
            RollingHash libSignatures = getSignatures(lib);

            int a = 0;
            int count = Math.max(appSignatures.size(), 1);

            for (final int hash : appSignatures) {
                a += libSignatures.contains(hash) ? 1 : 0;
            }
            return a != 0 ? (a * 1.0) / count : 0;
        }

        @Override
        public @NotNull Class<? extends ManagedProfile> targetProfileClass()
        {
            return ClassProfile.class;
        }

        @Override
        public void process(@NotNull IClass ref, @NotNull ClassProfile target)
        {
            final List<String> fields = target
                    .getManager()
                    .getILFactory()
                    .getFields(ref)
                    .toList();

            RollingHash signatures = new RollingHash(
                    RollingHash.rollingHashBase, RollingHash.rollingHashN, fields.size());
            fields.forEach(signatures::add);

            final List<String> methods = target
                    .getManager()
                    .getILFactory()
                    .getMethods(ref)
                    .toList();

            methods.forEach(signatures::add);
            setSignatures(target, signatures);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // context
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public @NotNull ProfileManager newProfileManager()
    {
        ProfileManager manager =
                ProfileManager.getInstance(new FuzzyHierarchyILFactory());

        update(manager, ClassProfile::new);
        update(manager, ExtendedClassProfile::new);
        return manager.with(new ClassProfileList());
    }

    @Override
    public ICHAIntegration getIntegration()
    {
        return this;
    }

    @Override
    public <E extends ExtensibleProfile> void updateBlueprint(@NotNull Blueprint<E> blueprint)
    {
        blueprint.add(blueprintKey, RollingHash::new);
    }

    ///////////////////////////////////////////////////////////////////////////
    // integration
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void setProfileStrategy(IStrategy<?> strategy)
    {
        strategy.with(CHAProfile.class,
                Strategies.profileMaximumWeightBipartiteMatching());
    }

    @Override
    public void setClassStrategy(IStrategy<?> strategy)
    {
        strategy.with(ClassProfile.class,
                (ISimilarityStrategy<ClassProfile>) new ClassStrategy());
    }

    @Override
    public void addClassStep(CHAStrategy strategy)
    {
        strategy.with(ClassProfile.class, (IClassStep) new ClassStrategy());
    }
}
