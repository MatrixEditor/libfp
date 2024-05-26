/*
 * MIT License
 *
 * Copyright (c) 2024 MatrixEditor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.libfp.benchmark;

import io.github.libfp.profile.Profile;
import io.github.libfp.profile.ProfileFactory;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@ApiStatus.Experimental
public abstract class TestSuite<C>
        implements ITestSuite
{
    protected final @NotNull IProfileManagerFactory managerFactory;
    protected final @NotNull ProfileFactory<C> profileFactory;
    protected final @NotNull DataSet dataSet;
    protected final @NotNull IStrategy<?> strategy;

    protected final ProgressBarBuilder pbb;
    protected final Map<String, Profile> cachedProfiles;

    protected TestSuite(
            @NotNull IProfileManagerFactory managerFactory,
            @NotNull ProfileFactory<C> profileFactory,
            @NotNull DataSet dataSet,
            @NotNull IStrategy<?> strategy)
    {
        this.managerFactory = managerFactory;
        this.profileFactory = profileFactory;
        this.dataSet = dataSet;
        this.strategy = strategy;
        this.pbb = new ProgressBarBuilder().setStyle(ProgressBarStyle.ASCII);
        this.cachedProfiles = new Hashtable<>();

        verifyDataSet();
    }

    /**
     * Performs benchmark tests for a given application domain.
     *
     * @param appDomainName The name of the application domain.
     * @param config        The threshold configuration for testing.
     * @return A BenchmarkResult object with the test results.
     */
    public BenchmarkResult benchmark(
            final @NotNull String appDomainName,
            final @NotNull IThresholdConfig config)
    {
        return benchmark(appDomainName, config, false);
    }

    /**
     * Performs benchmark tests for a given application domain.
     *
     * @param appDomainName  The name of the application domain.
     * @param config         The threshold configuration for testing.
     * @param forceOverwrite Flag indicating whether to overwrite existing
     *                       profiles.
     * @return A BenchmarkResult object with the test results.
     */
    public BenchmarkResult benchmark(
            final @NotNull String appDomainName,
            final @NotNull IThresholdConfig config,
            boolean forceOverwrite)
    {
        final String appDir = dataSet.getApplicationDirectory(appDomainName);
        final File apps = new File(appDir);

        final File[] apkFiles = requireNotEmpty(apps);
        return benchmark(appDomainName, config, forceOverwrite,
                apkFiles);
    }

    @NotNull
    public BenchmarkResult benchmark(
            @NotNull String appDomainName,
            @NotNull IThresholdConfig config,
            boolean forceOverwrite,
            File[] apkFiles)
    {
        Map<String, List<TestResult>> results = new HashMap<>();
        final File libsDir = new File(dataSet.getLibraryDirectory());
        final File[] libFiles = requireNotEmpty(libsDir);

        ProgressBar.wrap(libFiles, pbb.setTaskName("Loading Lib-Profiles"))
                .parallel()
                .forEach(
                        f -> cachedProfiles.put(f.getAbsolutePath(),
                                createOrLoadLibProfile(f, forceOverwrite,
                                        false, true)));

        ProgressBar
                .wrap(apkFiles,
                        pbb.setTaskName("Performing Tests"))
                .parallel()
                .forEach(f -> benchmarkApp(f, appDomainName, libFiles,
                        config, forceOverwrite, results));

        return new BenchmarkResult(results);
    }

    /**
     * Prepares application profiles for a given app domain.
     *
     * @param appDomainName The name of the application domain.
     */
    public void prepareApp(
            final @NotNull String appDomainName
    )
    {
        prepareApp(appDomainName, false);
    }

    /**
     * Prepares application profiles for a given app domain with an option to
     * force overwriting.
     *
     * @param appDomainName  The name of the application domain.
     * @param forceOverwrite Flag indicating whether to overwrite existing
     *                       profiles.
     */
    public void prepareApp(
            final @NotNull String appDomainName,
            boolean forceOverwrite
    )
    {
        final String appDir = dataSet.getApplicationDirectory(appDomainName);
        final File apps = new File(appDir);

        final File[] apkFiles = requireNotEmpty(apps);
        ProgressBar
                .wrap(apkFiles, pbb.setTaskName("Creating App-Profiles"))
                .forEach(f -> createOrLoadAppProfile(f, forceOverwrite, true,
                        false));
    }

    /**
     * Prepares library profiles.
     */
    public void prepareLibraries()
    {
        prepareLibraries(false);
    }

    /**
     * Prepares library profiles with an option to force overwriting.
     *
     * @param forceOverwrite Flag indicating whether to overwrite existing
     *                       profiles.
     */
    public void prepareLibraries(boolean forceOverwrite)
    {
        final File libsDir = new File(dataSet.getLibraryDirectory());
        final File[] libFiles = requireNotEmpty(libsDir);

        ProgressBar
                .wrap(libFiles, pbb.setTaskName("Creating Lib-Profiles"))
                .forEach(f -> createOrLoadLibProfile(f, forceOverwrite, true,
                        false));
    }

    /**
     * Runs a test for a given application and library profiles.
     *
     * @param app    The application profile to test.
     * @param lib    The library profile to test against.
     * @param config The threshold configuration for testing.
     * @return A TestResult object containing the test results.
     */
    public TestResult run(
            final @NotNull Profile app,
            final @NotNull Profile lib,
            final @NotNull IThresholdConfig config)
    {
        long nanoStart = System.nanoTime();
        long milliStart = System.currentTimeMillis();

        TestResult.Status status = new TestResult.Success();
        double result = 0.0;
        try {
            result = app.similarityTo(lib, config);
        } catch (Exception exception) {
            status = new TestResult.Failure(exception);
        }

        long nanoTime = System.nanoTime() - nanoStart;
        long milliTime = System.currentTimeMillis() - milliStart;
        System.gc();
        return new TestResult(
                this, app, lib, nanoTime,
                milliTime, result, status
        );
    }

    /**
     * Get a Callable that runs a test for a given application and library
     * profiles.
     *
     * @param app    The application profile to test.
     * @param lib    The library profile to test against.
     * @param config The threshold configuration for testing.
     * @return A Callable for running the test.
     */
    public Callable<TestResult> getRunner(
            final @NotNull Profile app,
            final @NotNull Profile lib,
            final @NotNull IThresholdConfig config)
    {
        return () -> run(app, lib, config);
    }

    public Profile createOrLoadAppProfile(
            final @NotNull File file,
            final boolean forceOverwrite,
            final boolean ignoreResult,
            final boolean load)
    {

        Name name = Name.parse(file.getName());
        final String target = dataSet.getFullApplicationProfilePath(name);
        final File out = new File(target);

        Profile profile = createOrLoadProfile(
                file, forceOverwrite, ignoreResult, target, true);
        if (profile == null) return null;

        if (!load) {
            try {
                // set as app profile
                profile.getProfileInfo().flags |= 1;
                profile.saveTo(out);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return ignoreResult ? null : profile;
    }

    public @Nullable Profile createOrLoadLibProfile(
            final @NotNull File file,
            final boolean forceOverwrite,
            final boolean ignoreResult,
            final boolean load)
    {
        String name = file.getName().substring(0, file.getName().length() - 4);
        final String target = dataSet.getFullLibraryProfilePath(name);
        final File out = new File(target);

        Profile profile =
                createOrLoadProfile(file, forceOverwrite, ignoreResult, target,
                        false);
        if (profile == null) return null;

        if (!load) {
            Constants constants =
                    profile.getManager().getExtension(Constants.class);
            constants.put("name", name);
            try {
                profile.saveTo(out);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return ignoreResult ? null : profile;
    }

    protected abstract C createContext(final @NotNull File source)
            throws IOException;

    /**
     * Get the factory for creating profile managers.
     *
     * @return The IProfileManagerFactory.
     */
    public @NotNull IProfileManagerFactory managerFactory()
    {
        return managerFactory;
    }

    /**
     * Get the factory for creating profiles.
     *
     * @return The IProfileFactory.
     */
    public @NotNull ProfileFactory<C> profileFactory()
    {
        return profileFactory;
    }

    /**
     * Get the data set containing profiles.
     *
     * @return The DataSet.
     */
    public @NotNull DataSet dataSet()
    {
        return dataSet;
    }

    /**
     * Get the profiling strategy.
     *
     * @return The Strategy.
     */
    public @NotNull IStrategy<?> strategy()
    {
        return strategy;
    }


    /**
     * Get a string representation of the TestSuite.
     *
     * @return A string representing the TestSuite.
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString()
    {
        return "TestSuite[" +
                "dataSet=" + dataSet + ", " +
                "strategy=" + strategy + ']';
    }

    ///////////////////////////////////////////////////////////////////////////
    // private API
    ///////////////////////////////////////////////////////////////////////////
    protected void verifyDataSet() throws IllegalStateException
    {
        final File framework = new File(dataSet.frameworkPath());
        if (!framework.exists()) {
            throw new IllegalStateException(
                    "Could not find framework at: " + framework);
        }

        final File root = new File(dataSet.datasetBaseDirectory());
        createIfNotExists(root);

        final String[] dirs = {
                dataSet.getLibraryDirectory(),
                dataSet.getApplicationDirectory(),
        };
        for (final String dir : dirs) {
            final File directory = new File(dir);
            createIfNotExists(directory);

            final String[] files = directory.list();
            if (files == null || files.length == 0) {
                throw new IllegalArgumentException(dir + " is empty!");
            }
        }
    }

    protected final void createIfNotExists(@NotNull File outDirectory)
    {
        if (!outDirectory.exists() && !outDirectory.mkdirs()) {
            throw new IllegalStateException(
                    "Could not create: " + outDirectory);
        }
    }

    protected final File @NotNull [] requireNotEmpty(final @NotNull File dir)
    {
        final File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No files found at: " + dir);
        }
        return files;
    }

    protected void benchmarkApp(
            File file,
            String appDomainName,
            File[] libFiles,
            IThresholdConfig config,
            boolean forceOverwrite,
            Map<String, List<TestResult>> results)
    {
        /// Filename structure:
        //     name := [<appType>]-<appNum>-<appDomainName>.apk
        String[] parts = DataSet.parseAppFileName(file.getName());
        String appType = parts[0];
        Profile app = createOrLoadAppProfile(file, forceOverwrite, false, true);

        List<TestResult> testResults =
                Collections.synchronizedList(new LinkedList<>());

        String taskName =
                "Test[" + (appType.isBlank() ? "default" : appType) + "]";

        ProgressBarBuilder pbb = this.pbb.setTaskName(taskName);
        ProgressBar.wrap(libFiles, pbb)
                .map(f -> cachedProfiles.getOrDefault(f.getAbsolutePath(),
                        null))
                .filter(Objects::nonNull)
                .parallel()
                .map(f -> run(app, f, config))
                .forEach(testResults::add);

        results.put(appType, testResults);
        System.gc();
    }

    protected final @Nullable Profile createOrLoadProfile(
            final @NotNull File file,
            final boolean forceOverwrite,
            final boolean ignoreResult,
            final @NotNull String target,
            boolean appProfile)
    {
        final File out = new File(target);
        final File outDirectory = out.getParentFile();
        createIfNotExists(outDirectory);

        ProfileManager manager = managerFactory()
                .newInstance()
                .with(new Constants());
        try {
            if (out.exists() && !forceOverwrite) {
                if (ignoreResult) return null;
                if (appProfile) {
                    manager.getExtension(ProfileInfo.class).flags = 1;
                }

                return profileFactory()
                        .load(out, manager, strategy);
            }

            final C context = createContext(file);
            if (!strategy.isValid(context)) {
                return null;
            }
            return profileFactory
                    .build(context, manager, strategy);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load: " + target, e);
        }
    }
}
