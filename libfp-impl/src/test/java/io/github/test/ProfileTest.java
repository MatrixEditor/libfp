package io.github.test; //@date 24.10.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.cha.ICHAIntegration;
import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.test.util.ProfileTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Objects;

// TODO: use IProfileContext
public abstract class ProfileTest
{

    private final String prefix;
    private final String appName;
    private final String outDirectory;

    private final IProfileBuilder<AnalysisScope> builder;
    private final @NotNull String libsDirectory;
    private final String extension;
    private final String frameworkPath;

    private final ICHAIntegration integration;

    public ProfileTest(
            String prefix,
            String appName,
            String outDirectory,
            IProfileBuilder<AnalysisScope> builder,
            String extension,
            String frameworkPath)
    {
        this(prefix, appName, outDirectory, builder, extension, frameworkPath, null);
    }

    public ProfileTest(
            String prefix,
            String appName,
            String outDirectory,
            IProfileBuilder<AnalysisScope> builder,
            String extension,
            String frameworkPath, ICHAIntegration integration)
    {
        this.prefix = prefix;
        this.appName = appName;
        this.builder = builder;
        this.libsDirectory = prefix + File.separator + "libs";
        this.outDirectory = outDirectory;
        this.extension = extension;
        this.frameworkPath = frameworkPath;
        this.integration = integration;
    }

    public ICHAIntegration getIntegration()
    {
        return Objects.requireNonNull(integration);
    }

    public @NotNull String getAppsDir()
    {
        return "../" + prefix + "/apps/" + appName;
    }

    public @NotNull String getAppsTargetDir()
    {
        return "../" + prefix + "/appProfiles/" + outDirectory;
    }

    public @NotNull String getLibsTargetDir()
    {
        return "../" + prefix + "/profiles/" + outDirectory;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getAppName()
    {
        return appName;
    }

    public IProfileBuilder<AnalysisScope> getBuilder()
    {
        return builder;
    }

    public @NotNull String getLibsDirectory()
    {
        return "../" + libsDirectory;
    }

    public abstract IStrategy<?> getStrategy();

    public abstract ProfileManager getManager();

    @BeforeEach
    public void setUp() throws Exception
    {
        System.out.println("Converting app profiles...");
        ProfileTestUtil.convertApps(
                getAppsDir(),
                getAppsTargetDir(),
                getBuilder(),
                getExtension(),
                getStrategy(),
                getManager(),
                frameworkPath
        );

        System.out.println("Converting lib profiles...");
        ProfileTestUtil.createProfiles(
                getLibsDirectory(),
                getLibsTargetDir(),
                getBuilder(),
                getExtension(),
                getStrategy(),
                getManager(),
                frameworkPath
        );
    }
}
