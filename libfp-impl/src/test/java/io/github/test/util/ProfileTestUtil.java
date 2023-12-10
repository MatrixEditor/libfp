package io.github.test.util; //@date 22.10.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.IProfileFactory;
import io.github.libfp.profile.Profile;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import io.github.libfp.util.AnalysisScopeBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

// TODO: use IProfileContext
public class ProfileTestUtil
{

    public static final String[] AppTypes = {
            "",
            "allatori-strong-repackage-",
            "allatori-weak-repackage-",
            "dasho-flatten-",
            "dasho-repackage-",
            "obfuscapk-",
            "proguard-",
            "proguard-flatten-",
            "proguard-repackage-"
    };

    public static void test(
            final @NotNull Profile<AnalysisScope> app,
            final @NotNull String profiles,
            @NotNull IProfileFactory<AnalysisScope> factory,
            IThresholdConfig threshold,
            final IStrategy<?> strategy,
            ProfileManager manager)
            throws IOException
    {
        test(app, new File(profiles), factory, threshold, strategy, manager);
    }

    public static void testApps(
            final String domain, final String directory,
            final @NotNull String profiles,
            @NotNull IProfileFactory<AnalysisScope> factory,
            final String extension, IThresholdConfig threshold,
            final IStrategy<?> strategy, ProfileManager manager)
            throws IOException
    {
        for (final String prefix : AppTypes) {
            final String appPath =
                    directory + File.separator + prefix + domain + extension;
            System.out.println(appPath + "\n" + "=".repeat(100));
            Profile<AnalysisScope> app =
                    factory.newInstance(new File(appPath), manager, strategy);
            test(app, profiles, factory, threshold, strategy, manager);
            System.out.println("-".repeat(100) + "\n");
        }
    }

    public static void test(
            final @NotNull Profile<AnalysisScope> app,
            final @NotNull File profiles,
            @NotNull IProfileFactory<AnalysisScope> factory,
            IThresholdConfig threshold,
            final IStrategy<?> strategy,
            ProfileManager manager)
            throws IOException
    {
        for (final File file : Objects.requireNonNull(profiles.listFiles())) {
            Profile<AnalysisScope> lib = factory.newInstance(file, manager,
                    strategy);
            final long start = System.nanoTime();
            final double result = app.similarityTo(lib, threshold);
            final long end = System.nanoTime();
            final long time = end - start;
            System.out.println(file.getName() + ":" + result + " in "
                    + (time) + "ns (" + ((double) time / 1000000000) + "s)");
        }
    }

    public static @NotNull List<Profile<?>> loadProfiles(
            final @NotNull String directory,
            final @NotNull IProfileFactory<AnalysisScope> factory,
            IStrategy<?> strategy,
            ProfileManager manager) throws IOException
    {
        return loadProfiles(new File(directory), factory, strategy, manager);
    }

    public static @NotNull List<Profile<?>> loadProfiles(
            final @NotNull File directory,
            final @NotNull IProfileFactory<AnalysisScope> factory,
            IStrategy<?> strategy,
            ProfileManager manager) throws IOException
    {
        List<Profile<?>> profiles = new LinkedList<>();
        for (final File file : Objects.requireNonNull(directory.listFiles())) {
            Profile<AnalysisScope> lib = factory.newInstance(file, manager,
                    strategy);
            System.out.println(file.getAbsolutePath());
            profiles.add(lib);
        }
        return profiles;
    }

    public static void convertApps(
            final @NotNull String directory,
            final @NotNull String outDirectory,
            final @NotNull IProfileBuilder<AnalysisScope> builder,
            final @NotNull String extension,
            IStrategy<?> strategy,
            @NotNull ProfileManager manager,
            final @NotNull String frameworkPath)
            throws Exception
    {
        for (final File file :
                Objects.requireNonNull(new File(directory).listFiles())) {
            if (!file.isDirectory()) {
                convertApp(file, outDirectory, builder, extension, strategy,
                        manager, frameworkPath);
            }
        }
    }

    public static void convertApp(
            final @NotNull File file,
            final @NotNull String outDirectory,
            final @NotNull IProfileBuilder<AnalysisScope> builder,
            final @NotNull String extension,
            IStrategy<?> strategy,
            @NotNull ProfileManager manager,
            final @NotNull String frameworkPath)
            throws Exception
    {
        final String destName = file.getName().replace(".apk", extension);

        File out = new File(outDirectory);
        if (!out.exists() && !out.mkdirs()) {
            throw new IllegalStateException("Could not create: " + out);
        }

        final String outPath = outDirectory + File.separator + destName;
        if (new File(outPath).exists()) {
            return;
        }

        final AnalysisScope scope = new AnalysisScopeBuilder()
                .enableDEX()
                .withAndroidFramework(frameworkPath)
                .withAPK(file.getAbsolutePath())
                .build();

        manager.reset();
        Profile<?> profile = builder.build(scope, manager, strategy);
        // set as app profile
        profile.getProfileInfo().flags |= 1;
        profile.saveTo(outPath);
        System.out.println(outPath);

        System.gc();
    }

    public static void createProfiles(
            final @NotNull String directory,
            final @NotNull String outDirectory,
            final @NotNull IProfileBuilder<AnalysisScope> builder,
            final String extension,
            IStrategy<?> strategy,
            @NotNull ProfileManager manager,
            @NotNull String frameworkPath)
            throws Exception
    {
        for (final File file :
                Objects.requireNonNull(new File(directory).listFiles())) {
            if (!file.isDirectory()) {
                createProfile(file, outDirectory, builder, extension,
                        strategy, manager, frameworkPath);
            }
        }
    }

    public static void createProfile(
            final @NotNull File file,
            final @NotNull String outDirectory,
            final @NotNull IProfileBuilder<AnalysisScope> builder,
            final String extension,
            IStrategy<?> strategy,
            @NotNull ProfileManager manager,
            @NotNull String frameworkPath)
            throws Exception
    {

        final String destination = outDirectory
                + File.separator
                + file.getName().substring(0, file.getName().length() - 4)
                + extension;

        File out = new File(outDirectory);
        if (!out.exists() && !out.mkdirs()) {
            throw new IllegalStateException("Could not create: " + out);
        }

        if (new File(destination).exists()) {
            return;
        }

        final AnalysisScope scope = new AnalysisScopeBuilder()
                .withAndroidFramework(frameworkPath)
                .with(file.getAbsolutePath())
                .build();

        manager.reset();
        Profile<?> profile = builder.build(scope, manager, strategy);
        profile.saveTo(destination);
    }
}
