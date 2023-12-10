package io.github.test.hierarchy; //@date 23.10.2023


import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.CHAStrategy;
import io.github.libfp.impl.hierarchy.HierarchyStrategies;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.SimpleThresholdConfig;
import io.github.test.ProfileTest;
import io.github.test.util.ProfileTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HierarchyAndRollingHashTest extends ProfileTest
{
    public HierarchyAndRollingHashTest()
    {
        super(
                "demo",
                "003-nodomain.freeyourgadget.gadgetbridge",
                "hierarchy+rolling-hash",
                CHAProfile::new,
                ".rh+h.fp",
                "../android.jar",
                HierarchyStrategies.getInstance()
        );
    }

    @Override
    public @NotNull ProfileManager getManager()
    {
        return HierarchyStrategies.getInstance().newProfileManager();
    }

    @Override
    public @NotNull IStrategy<CHAStrategy> getStrategy()
    {
        return HierarchyStrategies.defaultStrategy();
    }

    @Test
    public void testDefault() throws IOException
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
