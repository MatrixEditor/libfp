package io.github.libfp.benchmark;//@date 01.11.2023

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A record class representing a data set, which includes various directory
 * paths and extensions.
 * <pre>
 *     root/
 *        apps/
 *           001-app.apk
 *           ...
 *        libs/
 *          lib-abc-0.0.1.[aar|jar|dex]
 *          ...
 * </pre>
 *
 * @see GroundTruth
 * @see CHATestSuite
 */
@ApiStatus.Experimental
public record DataSet(
        @NotNull String datasetBaseDirectory,
        @NotNull String targetDirectory,
        @NotNull String frameworkPath,
        @NotNull String extension,
        @NotNull GroundTruth groundTruth
)
{
    // Constants for directory names
    public static final String APP_DIR = "apps";
    public static final String APP_PROFILE_DIR = "appProfiles";
    public static final String LIB_PROFILE_DIR = "libProfiles";
    public static final String LIB_DIR = "libs";

    /**
     * Get the directory path for a specific application within the data set.
     *
     * @param appName The name of the application.
     *
     * @return The directory path for the application.
     */
    public @NotNull String getApplicationDirectory(final @NotNull String appName)
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                APP_DIR,
                appName
        );
    }

    /**
     * Get the directory path for all applications within the data set.
     *
     * @return The directory path for all applications.
     */
    public @NotNull String getApplicationDirectory()
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                APP_DIR
        );
    }

    /**
     * Get the target directory path for all applications within the data set.
     *
     * @return The target directory path for all applications.
     */
    public @NotNull String getApplicationTargetDirectory()
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                APP_PROFILE_DIR,
                targetDirectory()
        );
    }

    public @NotNull String getApplicationFilePath(
            String appName,
            String appType)
    {
        return String.join(File.separator,
                getApplicationDirectory(appName),
                (appType.equals(BenchmarkResult.defaultAppType)
                        ? appName : appType + "-" + appName) + ".apk"
        );
    }

    /**
     * Get the directory path for all libraries within the data set.
     *
     * @return The directory path for libraries.
     */
    @Contract(" -> new")
    public @NotNull String getLibraryDirectory()
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                LIB_DIR
        );
    }

    /**
     * Get the target directory path for all libraries within the data set.
     *
     * @return The target directory path for libraries.
     */
    @Contract(" -> new")
    public @NotNull String getLibraryTargetDirectory()
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                LIB_PROFILE_DIR,
                targetDirectory()
        );
    }

    /**
     * Get the full profile path for a specific application within the data
     * set.
     *
     * @param name The name of the application.
     *
     * @return The full profile path for the application.
     */
    @Contract("_ -> new")
    public @NotNull String getFullApplicationProfilePath(final @NotNull String name)
    {
        return String.join(File.separator,
                getApplicationTargetDirectory(),
                name + extension()
        );
    }

    /**
     * Get the full profile path for a specific library within the data set.
     *
     * @param name The name of the library.
     *
     * @return The full profile path for the library.
     */
    @Contract("_ -> new")
    public @NotNull String getFullLibraryProfilePath(final @NotNull String name)
    {
        return String.join(File.separator,
                getLibraryTargetDirectory(),
                name + extension()
        );
    }
}
