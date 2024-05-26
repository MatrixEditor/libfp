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

/**
 * This package contains classes that are used for benchmarking. Before tests
 * can be performed, the general structure of a {@code DataSet} must and the
 * used filenames must be clarified. The structure (directory-based) of a
 * {@code DataSet} used here is standardized in the following way:
 * <pre>
 *     datasetBaseDirectory/
 *            libs/
 *                [lib].jar|aar|har|dex
 *                ...
 *            apps/
 *                [app].apk
 *                ...
 *            appProfiles/
 *                profileTargetDir/
 *                    [app-short]/
 *                        [app].[profileExtension]
 *                        ...
 *                     ...
 *                ...
 *            libProfiles/
 *                profileTargetDir/
 *                    [lib].[profileExtension]
 *                    ...
 *                ...
 * </pre>
 * Each directory serves a different role:
 * <ul>
 *     <li>libs: all library files (either {@code .jar}, {@code .aar} or {@code .dex}).</li>
 *     <li>apps: all application related files must be stored here ({@code .apk}).</li>
 *     <li>appProfiles: this directory contains <b>all</b> application profile files
 *     sorted by their profile type and application name.</li>
 *     <li>libProfiles: the last directory contains <b>all</b> library profile files
 *     only sorted by their profile type.</li>
 * </ul>
 *
 * @see io.github.libfp.benchmark.DataSet
 */
package io.github.libfp.benchmark;