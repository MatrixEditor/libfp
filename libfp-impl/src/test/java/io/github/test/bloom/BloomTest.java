package io.github.test.bloom; //@date 23.10.2023

import io.github.libfp.cha.*;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.impl.CallSideNormalizer;
import io.github.libfp.impl.Strategies;
import io.github.libfp.impl.UniqueFuzzyILFactory;
import io.github.libfp.impl.bloom.Bloom;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.SimpleThresholdConfig;
import io.github.test.ProfileTest;
import io.github.test.util.ProfileTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BloomTest extends ProfileTest
{
    public BloomTest()
    {
        super(
                "demo",
                "003-nodomain.freeyourgadget.gadgetbridge",
                "bloom",
                CHAProfile::new,
                ".b.fp",
                "../android.jar",
                Bloom.getInstance()
        );
    }

    @Override
    public IStrategy<CHAStrategy> getStrategy()
    {
        CHAStrategy strategy = CHAStrategy.getDefaultInstance();
        ICHAIntegration integration = getIntegration();

        integration.setClassLayer(strategy);
        integration.setMethodLayer(strategy);

        return strategy.with(
                CHAProfile.class,
                Strategies.profileMaximumWeightBipartiteMatching());
    }

    @Override
    public @NotNull ProfileManager getManager()
    {
        ProfileManager manager = ProfileManager
                .getInstance(new UniqueFuzzyILFactory())
                .with(new ClassProfileList())
                .with(new MethodProfileList())
                .with(CallSideNormalizer::new);

        ICHAIntegration integration = getIntegration();
        integration.update(manager, ClassProfile::new);
        integration.update(manager, MethodProfile::new);
        return manager;
    }

    @Test
    void testImport() throws IOException
    {
        ProfileTestUtil.loadProfiles(
                getLibsTargetDir(),
                CHAProfile::new,
                getStrategy(),
                getManager()
        );
    }

    @Test
    void testDefault() throws IOException
    {
        ProfileTestUtil.testApps(
                getAppName(),
                getAppsTargetDir(),
                getLibsTargetDir(),
                CHAProfile::new,
                getExtension(),
                new SimpleThresholdConfig(),
                getStrategy(),
                getManager()
        );
    }
}
