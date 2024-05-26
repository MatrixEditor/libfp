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
package io.github.libfp.util;

import com.ibm.wala.dalvik.classLoader.DexFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.io.TemporaryFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The AnalysisScopeBuilder class is responsible for constructing an analysis
 * scope for program analysis. It allows adding various types of class loaders
 * and modules to the analysis scope, such as JAR files, DEX files, APK files,
 * and AAR files.
 */
public final class AnalysisScopeBuilder
{

    private static final String DEX_LOADER_CLASS =
            "com.ibm.wala.dalvik.classLoader.WDexClassLoaderImpl";

    private static final Pattern DEX_FILE_PATTERN =
            Pattern.compile("classes[0-9]*\\.dex");

    private final AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();

    private boolean dexEnabled = false;

    /**
     * Add the Android framework JAR file to the analysis scope.
     *
     * @param path The path to the Android framework JAR file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while reading the JAR file.
     */
    @Contract("_ -> this")
    public @NotNull AnalysisScopeBuilder withAndroidFramework(
            @NotNull String path)
            throws IOException
    {
        this.scope.addToScope(ClassLoaderReference.Primordial,
                new JarFile(path));
        return this;
    }

    /**
     * Enable DEX loading by setting the loader implementation to the DEX loader
     * class.
     *
     * @return The AnalysisScopeBuilder for method chaining.
     */
    @Contract(" -> this")
    public @NotNull AnalysisScopeBuilder enableDEX()
    {
        this.scope.setLoaderImpl(ClassLoaderReference.Application,
                DEX_LOADER_CLASS);
        dexEnabled = true;
        return this;
    }

    /**
     * Add a DEX file to the analysis scope.
     *
     * @param path The path to the DEX file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while reading the DEX file.
     */
    @Contract("_ -> this")
    public @NotNull AnalysisScopeBuilder withDEX(@NotNull String path)
            throws IOException
    {
        if (!dexEnabled) enableDEX();

        this.scope.addToScope(ClassLoaderReference.Application,
                DexFileModule.make(new File(path)));
        return this;
    }

    /**
     * Add a JAR file to the analysis scope.
     *
     * @param path The path to the JAR file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while reading the JAR file.
     */
    @Contract("_ -> this")
    public @NotNull AnalysisScopeBuilder withJAR(@NotNull String path)
            throws IOException
    {
        this.scope.addToScope(ClassLoaderReference.Application,
                new JarFile(path));
        return this;
    }

    /**
     * Add an APK file to the analysis scope.
     *
     * @param path The path to the APK file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while reading the APK file.
     * @apiNote This method will extract all DEX files from an Android
     *         App Package and import classes defined in them.
     */
    public @NotNull AnalysisScopeBuilder withAPK(@NotNull String path)
            throws IOException
    {

        try (ZipFile zipFile = new ZipFile(path)) {
            Iterator<? extends ZipEntry> entries =
                    zipFile.entries().asIterator();

            while (entries.hasNext()) {
                ZipEntry zipEntry = entries.next();

                // only include classes[1-9].dex
                if (DEX_FILE_PATTERN.matcher(zipEntry.getName())
                        .matches())
                {
                    // name := <apk-name> 'classes' [1-9] '.dex'
                    final String name = "-" + zipEntry.getName();
                    final String apkName = Path
                            .of(zipFile.getName().substring(0,
                                    zipFile.getName().length() - 4))
                            .getFileName()
                            .toString();

                    final File tempPath = new File(
                            System.getProperty("java.io.tmpdir")
                                    + File.separator + apkName);

                    TemporaryFile.streamToFile(tempPath,
                            zipFile.getInputStream(zipEntry));

                    withDEX(tempPath.toString());
                    tempPath.deleteOnExit();
                }
            }
        }
        return this;
    }

    /**
     * Add an AAR file to the analysis scope. If the AAR file contains a
     * "classes.jar," it is treated as a JAR file.
     *
     * @param path The path to the AAR file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while processing the AAR
     *                     file.
     */
    @Contract("_ -> this")
    public @NotNull AnalysisScopeBuilder withAAR(@NotNull String path)
            throws IOException
    {
        return withAAR(path, ".aar");
    }

    /**
     * Add an AAR file to the analysis scope. If the AAR file contains a
     * "classes.jar," it is treated as a JAR file.
     *
     * @param path The path to the AAR file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException If an I/O error occurs while processing the AAR
     *                     file.
     */
    @Contract("_, _ -> this")
    public @NotNull AnalysisScopeBuilder withAAR(@NotNull String path,
                                                 @NotNull String extension)
            throws IOException
    {
        try (ZipFile zipFile = new ZipFile(path)) {
            ZipEntry zipEntry = zipFile.getEntry("classes.jar");
            if (zipEntry != null) {
                final String name = Path
                        .of(zipFile.getName().replace(extension, ".jar"))
                        .getFileName()
                        .toString();

                final File tempPath = new File(
                        System.getProperty("java.io.tmpdir")
                                + File.separator + name);

                TemporaryFile.streamToFile(tempPath,
                        zipFile.getInputStream(zipEntry));
                withJAR(tempPath.toString());
                tempPath.deleteOnExit();
            }
        }
        return this;
    }

    /**
     * Add a file to the analysis scope based on its file extension.
     *
     * @param path The path to the file.
     * @return The AnalysisScopeBuilder for method chaining.
     * @throws IOException              If an I/O error occurs while reading the
     *                                  file.
     * @throws IllegalArgumentException If the file has an unexpected file
     *                                  extension.
     */
    public @NotNull AnalysisScopeBuilder with(@NotNull String path)
            throws IOException
    {
        String suffix = path.substring(path.length() - 4);
        return switch (suffix) {
            case ".apk" -> withAPK(path);
            case ".jar" -> withJAR(path);
            case ".dex" -> withDEX(path);
            case ".aar" -> withAAR(path);
            case ".har" -> withAAR(path, ".har");
            default -> throw new IllegalArgumentException(
                    "Unexpected file: " + suffix + " - " + path);
        };
    }

    /**
     * Build and return the constructed AnalysisScope.
     *
     * @return The constructed AnalysisScope for program analysis.
     */
    @Contract(pure = true)
    public @NotNull AnalysisScope build()
    {
        return scope;
    }
}
