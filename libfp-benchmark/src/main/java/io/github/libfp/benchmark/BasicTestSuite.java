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

import io.github.libfp.profile.IContextFactory;
import io.github.libfp.profile.IProfileProvider;
import io.github.libfp.profile.Profile;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApiStatus.Experimental
public class BasicTestSuite<C> implements ITestSuite
{


    /**
     * Controls whether to process all profiles in parallel.
     */
    public boolean parallelProcessing = false;

    /**
     * This variable controls whether to wrap the processing in a progress bar
     * and make the progress visible. Note that this feature is disabled by
     * default.
     */
    public boolean wrapProcesssing = false;

    /**
     * Controls whether to store profiles in the cache instead of loading them
     * before each test.
     */
    public boolean cacheProfiles = false;

    /**
     * Enables or disables garbage collection at the end of each test.
     */
    public boolean forceGC = false;

    /**
     * Controls whether to create directories in the output directory if they
     * don't exist. If set to {@code true}, the output directory won't be
     * created and an error will be thrown.
     */
    public boolean strict = false;

    /**
     * Controls whether to verify that the given {@link DataSet} is valid.
     */
    public boolean verify = true;

    /**
     * Controls whether to display all tasks as a progress-bar or not.
     */
    public boolean displayTasks = false;

    /**
     * Internal provider that will be responsible for creating the profile
     * managers, strategies, and profiles.
     */
    private final IProfileProvider<C> provider;


    /**
     * The data set used for testing.
     */
    private final DataSet dataSet;


    /**
     * Internal field that holds the progress bar builder. It can't be used if
     * {@link #wrapProcesssing} is set to {@code false}.
     *
     * @see #wrapProcesssing
     */
    private ProgressBarBuilder pbb;

    /**
     * Internal fields that stores the cached profiles for each source file.
     * That means, the mapping is based on the absolute path of the source file,
     * not the profile file. Furthermore, the mapping is not available if
     * {@link #cacheProfiles} is set to {@code false}.
     *
     * @see #cacheProfiles
     */
    private final Map<String, Profile> cachedProfiles;

    /**
     * Internal field to temporarily store the results of the tests. It will be
     * accessible only during the tests.
     *
     * @implNote The key in this map will be the tested application
     *         type.
     */
    private volatile Map<String, List<TestResult>> benchmarkResults = null;


    /**
     * Creates a new BasicTestSuite based on the {@link IProfileProvider} and
     * the given {@link DataSet}.
     *
     * @param provider the profile provider
     * @param dataSet  the data set to use
     * @param verify   whether to verify the data set
     * @param strict   whether to throw an exception if the data set is invalid
     */
    public BasicTestSuite(
            final @NotNull IProfileProvider<C> provider,
            final @NotNull DataSet dataSet,
            boolean verify,
            boolean strict)
    {
        this.provider = provider;
        this.dataSet = dataSet;
        this.verify = verify;
        this.strict = strict;
        this.cachedProfiles = new Hashtable<>();

        if (verify) {
            verifyDataSet(dataSet);
        }
        if (pbb == null) {
            this.pbb = new ProgressBarBuilder()
                    .setStyle(ProgressBarStyle.ASCII);
        } else {
            this.wrapProcesssing = true;
        }
    }


    /**
     * Validates that the given {@link DataSet} is valid.
     *
     * @param dataSet the data set to validate
     * @throws IllegalStateException if the data set is invalid
     */
    public void verifyDataSet(DataSet dataSet) throws IllegalStateException
    {
        if (dataSet == null) {
            throw new IllegalStateException("dataSet cannot be null");
        }

        final File framework = new File(dataSet.frameworkPath());
        if (!framework.exists()) {
            throw new IllegalStateException(
                    "Could not find framework at: " + framework);
        }

        final File root = new File(dataSet.datasetBaseDirectory());
        if (!root.exists()) {
            if (strict) {
                throw new IllegalStateException(
                        "Could not find dataset root-dir at: " + root);
            } else if (!root.mkdirs()) {
                throw new IllegalStateException(
                        "Could not create: " + root);
            }
        }

        final String[] dirs = {
                dataSet.getLibraryDirectory(),
                dataSet.getApplicationDirectory(),
                dataSet.getLibraryTargetDirectory(),
                dataSet.getApplicationTargetBaseDirectory()
        };
        for (final String dir : dirs) {
            final File directory = new File(dir);
            if (!directory.exists()) {
                if (strict) {
                    throw new IllegalStateException(
                            "Could not find directory at: " + directory);
                } else if (!directory.mkdirs()) {
                    throw new IllegalStateException(
                            "Could not create: " + directory);
                }
            }
        }
    }

    /**
     * Verifies that the given directory is not empty.
     *
     * @param dir the directory to verify
     * @return the files in the directory
     * @throws IllegalStateException if the directory is empty
     */
    protected final File @NotNull [] requireNotEmpty(final @NotNull File dir)
    {
        final File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No files found at: " + dir);
        }
        return files;
    }


    /**
     * Loads the profile from the given file.
     *
     * @param file the profile file
     * @return the profile
     */
    public Profile loadProfile(final File file)
    {
        Profile profile = null;
        if (cacheProfiles) {
            if (cachedProfiles == null) {
                throw new IllegalStateException(
                        "Cached profiles are not available");
            }
            profile = cachedProfiles.getOrDefault(
                    file.getAbsolutePath(), null);
        }

        if (profile == null) {
            try {
                profile = provider.getProfileFactory()
                        .load(file, provider.newProfileManager(),
                                provider.getStrategy());
            } catch (IOException e) {
                throw new RuntimeException("Failed to load profile at " + file,
                        e);
            }

            if (cacheProfiles) {
                cachedProfiles.put(file.getAbsolutePath(), profile);
            }
        }
        return profile;
    }


    /**
     * Creates a new application profile from the given context and writes it to
     * the given target file. The newly created profile will be stored under
     * {@code
     * <datasetDir>/appProfiles/<targetProfileDir>/<app-short>/<app>.<profileExtension>}
     *
     * @param source         the source file
     * @param forceOverwrite the flag indicating whether to overwrite existing
     *                       profiles
     * @param factory        the profile context factory
     */
    public void createApplicationProfile(final @NotNull File source,
                                         final boolean forceOverwrite,
                                         final IContextFactory<C> factory)
    {
        // First, let us create the current file context and parse
        // the application filename
        C context = factory.newInstance(source);
        Name name = Name.parse(source.getName());

        // As described by the standard dataset specification, the application
        // profile will be stored in a separate subdirectory within the target
        // profile directory under the application profiles.
        String target = dataSet.getFullApplicationProfilePath(name);
        try {
            createProfile(context, forceOverwrite, target, p -> {
                p.getManager().getExtension(ProfileInfo.class).flags |= 1;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new library profile from the given source file and writes it to
     * the given target file. The profile won't be created if the target file
     * already exists and {@code forceOverwrite} is set to {@code false}.
     *
     * @param source         the source file
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the profile context factory
     * @throws RuntimeException if the profile could not be created
     */
    public void createLibraryProfile(final @NotNull File source,
                                     final boolean forceOverwrite,
                                     final IContextFactory<C> factory)
            throws RuntimeException
    {
        // First, let us create the current file context and transform
        // the filename
        C context = factory.newInstance(source);
        String name = source.getName().replaceFirst("[.][^.]+$", "");

        final String target = dataSet.getFullLibraryProfilePath(name);
        try {
            createProfile(context, forceOverwrite, target,
                    p -> p.getManager()
                            .getExtension(Constants.class)
                            .put("name", name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new profile from the given context and writes it to the given
     * target file. The profile won't be created if the target file already
     * exists and {@code forceOverwrite} is set to {@code false}.
     * <p>
     * First, the profile context is validated - if the context is invalid, an
     * exception is thrown. Then, the profile is created using the given context
     * and (optionally) flagged as an app profile. Additionally, if
     * {@link #cacheProfiles} is active, the profile is cached.
     *
     * @param context        the profile context
     * @param forceOverwrite the flag indicating whether to overwrite existing
     *                       profiles
     * @param target         the target file
     * @throws Exception if the profile could not be created
     */
    public void createProfile(final @NotNull C context,
                              final boolean forceOverwrite,
                              final @NotNull String target,
                              @Nullable Consumer<Profile> consumer)
            throws Exception
    {
        IStrategy<?> strategy = provider.getStrategy();
        if (!strategy.isValid(context)) {
            throw new IllegalStateException(
                    "Invalid context: " + context);
        }

        final File file = new File(target);
        if (!file.exists() || forceOverwrite) {
            Profile profile = provider
                    .getProfileFactory()
                    .build(context, provider.newProfileManager(), strategy);

            if (cacheProfiles) {
                cachedProfiles.put(file.getAbsolutePath(), profile);
            }

            if (consumer != null) {
                consumer.accept(profile);
            }
            profile.saveTo(file);
        }
        if (forceGC) {
            System.gc();
        }
    }

    /**
     * Performs a single test on two profiles, where the first one <b>must</b>
     * be an application profile and the second one <b>must</b> be a lib
     * profile. This method will measure both milli and nano time and stores the
     * exception if the test fails. Finally, it will trigger the GC if
     * {@link #forceGC} is set to {@code true}.
     *
     * @param app    the application profile
     * @param lib    the library profile
     * @param config the threshold configuration
     * @return the test result
     */
    public TestResult test(final @NotNull Profile app,
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
        if (forceGC) {
            System.gc();
        }
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
    public Callable<TestResult> getTestRunner(
            final @NotNull Profile app,
            final @NotNull Profile lib,
            final @NotNull IThresholdConfig config)
    {
        return () -> test(app, lib, config);
    }


    /**
     * Prepares all library profiles in the library directory of the current
     * dataset. By default, exists profiles won't be overwritten.
     *
     * @param factory the context factory
     */
    public void prepareLibraries(IContextFactory<C> factory)
    {
        prepareLibraries(false, factory);
    }

    /**
     * Prepares all library profiles in the library directory of the current
     * dataset.
     *
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareLibraries(boolean forceOverwrite,
                                 IContextFactory<C> factory)
    {
        final String libDir = dataSet.getLibraryDirectory();
        final File[] libs = requireNotEmpty(new File(libDir));

        prepareLibraries(libs, forceOverwrite, factory);
    }

    /**
     * Prepares all library profiles in the given array by creating a profile
     * for each file in the array. If {@link #parallelProcessing} is set to
     * {@code true}, the stream will be parallelized and If
     * {@link #wrapProcesssing} is set to {@code true}, the stream will be
     * wrapped in a {@link ProgressBar}.
     *
     * @param libs           the array of files to prepare
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareLibraries(final @NotNull File[] libs,
                                 boolean forceOverwrite,
                                 IContextFactory<C> factory)
    {
        pbb.setTaskName("Creating Lib-Profiles");
        Stream<File> stream = wrapProcesssing
                ? ProgressBar.wrap(libs, pbb)
                : Arrays.stream(libs);

        prepareLibraries(parallelProcessing ? stream.parallel() : stream,
                forceOverwrite, factory);
    }

    /**
     * Prepares all library profiles in the given stream by creating a profile
     * for each file in the stream.
     *
     * @param stream         the stream of files to prepare
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareLibraries(final @NotNull Stream<File> stream,
                                 boolean forceOverwrite,
                                 IContextFactory<C> factory)
    {
        stream.forEach(
                f -> createLibraryProfile(f, forceOverwrite, factory));
    }

    /**
     * Prepares all application profiles from the given app.
     *
     * @param appDomainName the name of the application
     * @param factory       the context factory
     */
    public void prepareApplications(final @NotNull String appDomainName,
                                    IContextFactory<C> factory)
    {
        prepareApplications(appDomainName, false, factory);
    }

    /**
     * Prepares all application profiles from the given app.
     *
     * @param appDomainName  the name of the application
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareApplications(final @NotNull String appDomainName,
                                    boolean forceOverwrite,
                                    IContextFactory<C> factory)
    {
        final String appDir = dataSet.getApplicationDirectory(appDomainName);
        final File[] apps = requireNotEmpty(new File(appDir));
        prepareApplications(apps, forceOverwrite, factory);
    }

    /**
     * Prepares all application profiles in the given array by creating a
     * profile for each file in the array. If {@link #parallelProcessing} is set
     * to {@code true}, the stream will be parallelized and If
     * {@link #wrapProcesssing} is set to {@code true}, the stream will be
     * wrapped in a {@link ProgressBar}.
     *
     * @param apps           the array of files to prepare
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareApplications(final @NotNull File[] apps,
                                    boolean forceOverwrite,
                                    IContextFactory<C> factory)
    {
        pbb.setTaskName("Creating App-Profiles");
        Stream<File> stream = wrapProcesssing
                ? ProgressBar.wrap(apps, pbb)
                : Arrays.stream(apps);

        prepareApplications(parallelProcessing ? stream.parallel() : stream,
                forceOverwrite, factory);
    }

    /**
     * Prepares all application profiles in the given stream.
     *
     * @param stream         the stream of files to prepare
     * @param forceOverwrite the flag indicating whether to overwrite existing
     * @param factory        the context factory
     */
    public void prepareApplications(final @NotNull Stream<File> stream,
                                    boolean forceOverwrite,
                                    IContextFactory<C> factory)
    {
        stream.forEach(
                f -> createApplicationProfile(f, forceOverwrite, factory));
    }

    /**
     * Executes a single benchmark on the given application profile using only
     * its domain name (without identifier and type).
     *
     * @param appDomainName the name of the application
     * @param config        the threshold config
     * @return the benchmark result storing all test results
     */
    @Override
    public BenchmarkResult benchmark(@NotNull String appDomainName,
                                     @NotNull IThresholdConfig config)
    {

        if (benchmarkResults != null) {
            throw new IllegalStateException("Benchmarking already in progress");
        }

        BenchmarkResult result = new BenchmarkResult(
                benchmarkResults = new Hashtable<>());

        assert cacheProfiles : "Cannot benchmark without caching profiles";
        // 1. load all library profiles
        loadLibraries();
        Stream<File> stream;

        // 2. load application profiles
        File[] appFiles = requireNotEmpty(
                new File(dataSet.getApplicationDirectory(appDomainName)));

        stream = (wrapProcesssing ?
                ProgressBar.wrap(appFiles, pbb.setTaskName("Benchmarking: ")) :
                Arrays.stream(appFiles));

        // 3. benchmark
        (parallelProcessing ? stream.parallel() : stream)
                .forEach(f -> benchmark(f, config));

        benchmarkResults = null;
        return result;
    }

    public void loadLibraries()
    {
        File[] libFiles = requireNotEmpty(
                new File(dataSet.getLibraryTargetDirectory()));

        pbb.setTaskName("Loading Lib-Profiles");
        Stream<File> stream = (wrapProcesssing
                ? ProgressBar.wrap(libFiles, pbb)
                : Arrays.stream(libFiles));

        (parallelProcessing ? stream.parallel() : stream)
                .forEach(this::loadProfile);
    }

    /**
     * Performs a benchmark on the given application and all cached library
     * profiles.
     * <p>
     * This is a <i>middle level</i> benchmark function.
     *
     * @param app    the application profile use for the benchmark
     * @param config the threshold config
     */
    public void benchmark(final @NotNull File app,
                          final @NotNull IThresholdConfig config)
    {
        assert cacheProfiles : "Cannot benchmark without caching profiles";
        if (benchmarkResults == null) {
            benchmarkResults = new Hashtable<>();
        }

        // load the application profile
        Name name = Name.parse(app.getName());
        String appProfilePath = dataSet.getFullApplicationProfilePath(name);

        if (!new File(appProfilePath).exists()) {
            throw new IllegalStateException(
                    "Application profile not found: " + appProfilePath);
        }

        Profile appProfile = loadProfile(new File(appProfilePath));
        benchmark(name, appProfile, null, config);
    }


    /**
     * Performs a benchmark on the given application and library profiles,
     * whereby the library profiles are optional. {@link #cacheProfiles}
     * <b>must</b> be enabled to use this method.
     *
     * @param name   the application's name
     * @param app    the application profile
     * @param libs   the library profiles (optional)
     * @param config the threshold config
     */
    public void benchmark(final @NotNull Name name,
                          final @NotNull Profile app,
                          @Nullable File[] libs,
                          final @NotNull IThresholdConfig config)
    {
        assert cacheProfiles : "Cannot benchmark without caching profiles";

        // make sure the files are not null
        if (libs == null) {
            String libsDir = dataSet.getLibraryTargetDirectory();
            libs = requireNotEmpty(new File(libsDir));
        }

        Stream<File> stream = Arrays.stream(libs);

        List<Profile> profiles = (parallelProcessing
                ? stream.parallel()
                : stream)
                .map(this::loadProfile)
                .toList();

        Stream<Profile> profileStream = displayTasks
                ? ProgressBar.wrap(profiles.stream(),
                pbb.setTaskName(getTaskName(name)))
                : profiles.stream();

        benchmarkResults.put(name.type,
                process(app, profileStream, config));
    }

    /**
     * Processes the given application and library profiles by running a test on
     * each library profile. The results will be stored in a linked list.
     *
     * @param app    the application profile
     * @param libs   the library profiles as {@link Stream}
     * @param config the threshold configuration
     * @return the list of test results
     */
    public List<TestResult> process(final @NotNull Profile app,
                                    final @NotNull Stream<Profile> libs,
                                    final @NotNull IThresholdConfig config)
    {
        // The results will be stored in a linked list to reduce the
        // amount of insertion time.
        List<TestResult> results = Collections.synchronizedList(
                new LinkedList<>());

        // Run the test for each library and make sure the stream
        // is parallel.
        (parallelProcessing ? libs.parallel() : libs)
                .forEach(p -> results.add(test(app, p, config)));

        return results;
    }

    /**
     * Creates a task name for the given application profile name. It will be in
     * the format {@code "Test[<app-type>]"}.
     *
     * @param name the application name
     * @return the task name
     */
    @NotNull
    protected String getTaskName(final @NotNull Name name)
    {
        return " Test[" + (name.isDefaultType() ? "default" : name.type) + "]";
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public BenchmarkResult getBenchmarkResult()
    {
        return new BenchmarkResult(benchmarkResults);
    }

    public IProfileProvider<C> getProvider()
    {
        return provider;
    }

}