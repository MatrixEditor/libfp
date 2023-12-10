package io.github.test.bloom; //@date 24.10.2023

import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.CHAStrategy;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.impl.*;
import io.github.libfp.impl.bloom.Bloom;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import io.github.test.ProfileTest;
import io.github.test.util.ProfileTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BloomAndRollingHTest extends ProfileTest
        implements IThresholdConfig
{

    public BloomAndRollingHTest()
    {
        super(
                "demo",
                "003-nodomain.freeyourgadget.gadgetbridge",
                "bloom+rolling-hash",
                CHAProfile::new,
                ".rh+b.fp",
                "../android.jar",
                Bloom.getInstance()
        );
    }

    public static void main(String[] args)
            throws Exception
    {
        new BloomAndRollingHTest().setUp();
    }

    @Override
    public @NotNull ProfileManager getManager()
    {
        ProfileManager manager = ProfileManager
                .getInstance(new UniqueFuzzyILFactory())
                .with(new ClassProfileList())
                .with(new MethodProfileList(RollingHashMethodProfile::new))
                .with(CallSideNormalizer::new);

        getIntegration().update(manager, ClassProfile::new);
        return manager;
    }

    @Override
    public IStrategy<CHAStrategy> getStrategy()
    {
        CHAStrategy strategy = CHAStrategy
                .getDefaultInstance()
                // REVISIT: similarityOf in RollingHashMethodLayer is never
                // going to be called
                .with(RollingHashMethodProfile.class,
                        new RollingHashMethodLayer());

        getIntegration().setClassLayer(strategy);

        return strategy.with(
                CHAProfile.class,
                Strategies.profileMaximumWeightBipartiteMatching());
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
                this,
                getStrategy(),
                getManager()
        );
    }

}
