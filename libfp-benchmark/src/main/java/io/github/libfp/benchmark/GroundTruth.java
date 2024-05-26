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

import io.github.libfp.profile.extensions.Constants;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.*;

/**
 * A class representing ground truth information for applications and their
 * associated libraries. It allows reading and writing ground truth data from/to
 * files, managing the data in-memory, and provides a {@link Whitelist} for
 * filtering test results based on the ground truth.
 * <p>
 * File structure:
 * <pre>
 *     line    := &lt;appName&gt; ':' &lt;libDef&gt; [ ',' &lt;libDef&gt; ... ]
 *     appName := &lt;name&gt;
 *     libDef  := &lt;name&gt; '-' &lt;version&gt;
 * </pre>
 *
 * @see Whitelist
 * @see DataSet
 */
@ApiStatus.Experimental
public class GroundTruth
{
    private final Map<String, List<String>> appLabels;

    /**
     * Constructs an empty GroundTruth instance.
     */
    public GroundTruth()
    {
        appLabels = new HashMap<>();
    }

    /**
     * Constructs a GroundTruth instance by reading data from a file.
     *
     * @param path The path to the file containing ground truth data.
     * @throws IOException If there are issues reading the file.
     */
    public GroundTruth(final String path) throws IOException
    {
        this(new File(path));
    }

    /**
     * Constructs a GroundTruth instance by reading data from a file.
     *
     * @param file The File object representing the file containing ground truth
     *             data.
     * @throws IOException If there are issues reading the file.
     */
    public GroundTruth(final File file) throws IOException
    {
        this();
        read(file);
    }

    /**
     * Reads ground truth data from a file and populates the internal data
     * structure.
     *
     * @param file The File object representing the file containing ground truth
     *             data.
     * @throws IOException If there are issues reading the file.
     */
    public void read(final File file) throws IOException
    {
        // Line format: <app-name> ':' <lib-name> { ',' <lib-name> }
        try (FileReader reader = new FileReader(file)) {
            BufferedReader lineReader = new BufferedReader(reader);
            lineReader.lines()
                    .map(line -> line.split(":"))
                    .forEach(values -> add(
                            DataSet.parseAppFileName(values[0])[2],
                            values[1].split(",")));
        }
    }

    /**
     * Writes the ground truth data to an output stream.
     *
     * @param outputStream The OutputStream to which the ground truth data is
     *                     written.
     * @throws IOException If there are issues writing to the output stream.
     */
    public void write(OutputStream outputStream) throws IOException
    {
        try (BufferedOutputStream stream =
                     new BufferedOutputStream(outputStream))
        {
            for (final String appName : getApps()) {
                StringJoiner sj = new StringJoiner(",", appName + ":", "\n");
                getLibraries(appName).forEach(sj::add);
                stream.write(sj.toString().getBytes());
            }
        }
    }

    /**
     * Adds ground truth data for an application and its associated libraries.
     *
     * @param appName The name of the application.
     * @param libs    An array of library names associated with the
     *                application.
     */
    public void add(final String appName, final String... libs)
    {
        appLabels.putIfAbsent(appName, Arrays.asList(libs));
    }

    /**
     * Adds a single library to the ground truth data for an application.
     *
     * @param appName The name of the application.
     * @param lib     The name of the library to be added.
     */
    public void addLibrary(final String appName, final String lib)
    {
        appLabels.computeIfAbsent(appName, key -> new LinkedList<>())
                .add(lib);
    }

    /**
     * Retrieves a list of libraries associated with a specific application.
     *
     * @param app The name of the application.
     * @return A List of library names associated with the application.
     */
    public List<String> getLibraries(final String app)
    {
        return appLabels.get(app);
    }

    /**
     * Retrieves a set of application names for which ground truth data is
     * available.
     *
     * @return A Set of application names.
     */
    public Set<String> getApps()
    {
        return appLabels.keySet();
    }

    /**
     * Creates a {@link Whitelist} based on the ground truth for a specific
     * application name. The {@link Whitelist} filters test results based on
     * whether the library name and version in the result match the libraries
     * associated with the specified application.
     *
     * @param appName The name of the application for which the
     *                {@link Whitelist} is created.
     * @return A {@link Whitelist} for filtering test results based on ground
     *         truth data.
     */
    public Whitelist getVersionWhitelist(final String appName)
    {
        return new VersionLevelWhitelist(appName);
    }

    /**
     * Creates a {@link Whitelist} based on the ground truth for a specific
     * application name. The {@link Whitelist} filters test results based on
     * whether the library name in the result matches the libraries associated
     * with the specified application.
     *
     * @param appName The name of the application for which the
     *                {@link Whitelist} is created.
     * @return A {@link Whitelist} for filtering test results based on ground
     *         truth data.
     */
    public Whitelist getLibraryWhitelist(final String appName)
    {
        return new LibraryLevelWhitelist(appName);
    }

    private class LibraryLevelWhitelist implements Whitelist
    {

        private final List<String> libraries;

        private LibraryLevelWhitelist(String name)
        {
            libraries = getLibraries(name);
        }

        @Override
        public boolean test(TestResult testResult)
        {
            Constants constants = testResult
                    .lib()
                    .getManager()
                    .getExtension(Constants.class);

            Constants.Literal name = constants.get("name");
            String[] parts = name.value.split("-");
            for (final String lib : libraries) {
                String[] libParts = lib.split("-");
                int i = 0;
                while (i < parts.length && i < libParts.length) {
                    if (!parts[i].equals(libParts[i])) {
                        break;
                    }
                    i++;
                }

                if (i == parts.length) {
                    // full match
                    return true;
                }

                if (i < libParts.length &&
                        Character.isDigit(libParts[i].charAt(0)))
                {
                    // partial match
                    return true;
                }
            }
            return false;
        }
    }

    private class VersionLevelWhitelist implements Whitelist
    {
        private final List<String> libraries;

        private VersionLevelWhitelist(String name)
        {
            libraries = getLibraries(name);
        }

        @Override
        public boolean test(TestResult testResult)
        {
            Constants constants = testResult
                    .lib()
                    .getManager()
                    .getExtension(Constants.class);

            Constants.Literal name = constants.get("name");
            return libraries.contains(name.value);
        }
    }
}
