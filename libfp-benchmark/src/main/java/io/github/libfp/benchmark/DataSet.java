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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

/**
 * A so-called dataset defines a set of application and library files that can
 * be used within a benchmark used within a {@link ITestSuite}.
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

    public static @NotNull String[] parseAppFileName(
            final @NotNull String filename)
    {
        String n = filename;
        if (filename.endsWith(".apk")) {
            n = filename.substring(0, filename.length() - 4);
        }

        String[] parts = n.split("-");
        if (parts.length < 3) {
            return new String[]{"", parts[0], parts[1]};
        }

        if (parts.length > 3) {
            // last two parts are name and number
            String name = parts[parts.length - 1];
            String number = parts[parts.length - 2];
            return new String[]{
                    String.join("-", Arrays.copyOf(parts, parts.length - 2)),
                    name, number};

        }
        return parts;
    }

    /**
     * Get the directory path for a specific application within the data set.
     *
     * @param appName The name of the application.
     * @return The directory path for the application.
     */
    public @NotNull String getApplicationDirectory(
            final @NotNull String appName)
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
    public @NotNull String getApplicationTargetDirectory(String appName)
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                APP_PROFILE_DIR,
                appName
        );
    }

    public @NotNull String getApplicationTargetBaseDirectory()
    {
        return String.join(File.separator,
                datasetBaseDirectory(),
                APP_PROFILE_DIR
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
     * @param name The name of the application (all three parts)
     * @return The full profile path for the application.
     */
    @Contract("_ -> new")
    public @NotNull String getFullApplicationProfilePath(
            final @NotNull Name name)
    {
        return String.join(File.separator,
                getApplicationTargetDirectory(name.name),
                String.join(".", name.toString(), extension)
        );
    }

    /**
     * Get the full profile path for a specific library within the data set.
     *
     * @param name The name of the library.
     * @return The full profile path for the library.
     */
    @Contract("_ -> new")
    public @NotNull String getFullLibraryProfilePath(final @NotNull String name)
    {
        return String.join(File.separator,
                getLibraryTargetDirectory(),
                String.join(".", name, extension)
        );
    }
}
