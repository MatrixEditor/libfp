package io.github.test.relation; //@date 11.11.2023

import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.CHAProfileStep;
import io.github.libfp.cha.CHAStrategy;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.impl.BasicFuzzyILFactory;
import io.github.libfp.impl.cra.CRClassProfile;
import io.github.libfp.impl.cra.ClassRelationStep;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.SimpleThresholdConfig;
import io.github.test.ProfileTest;
import io.github.test.util.ProfileTestUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CRTest
        extends ProfileTest
{

    public CRTest()
    {
        super("demo",
                "003-nodomain.freeyourgadget.gadgetbridge",
                "cr",
                CHAProfile::new,
                ".cr.fp",
                "../android.jar");
    }

    @Override
    public IStrategy<?> getStrategy()
    {
        return new CHAStrategy()
                .with(CHAProfile.class, new CHAProfileStep())
                .with(CHAProfile.class, new ClassRelationStep());
    }

    @Override
    public ProfileManager getManager()
    {
        ProfileManager manager = new ProfileManager(new BasicFuzzyILFactory())
                .with(new Descriptors())
                .with(new ProfileInfo())
                .with(new Constants())
                .with(new ClassProfileList(CRClassProfile::new));

        getIntegration().update(manager, CRClassProfile::new);
        return manager;
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
