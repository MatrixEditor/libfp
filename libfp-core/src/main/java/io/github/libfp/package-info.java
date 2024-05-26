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
 * <h2>LibFP - Library Finger-Printing Framework</h2>
 * <hr/> The {@code io.github.libfp} package and its subpackages form the core
 * of the Library Finger-Printing Framework (LibFP). LibFP is a comprehensive
 * framework designed for the analysis and management of software profiles, with
 * a primary focus on libraries and applications. This extensive framework
 * provides essential tools, data structures, and strategies to effectively
 * understand, compare, and manage software profiles. It is instrumental in
 * classifying, profiling, and calculating similarities between various software
 * components.
 * <p>
 * <h3>Core (Sub-)Packages:</h3>
 * 1. <b>io.github.libfp:</b> This package contains the core classes and
 * interfaces used for software profile management and analysis. It serves as
 * the foundation for the framework's operations.
 * <p>
 * 2. <b>io.github.libfp.profile.extensions:</b> Extensions are provided within
 * this subpackage, including specialized profiles for libraries and
 * applications. It encapsulates classes and methods that help manage the
 * different types of software profiles.
 * <p>
 * 3. <b>io.github.libfp.similarity:</b> The "similarity" subpackage handles
 * similarity calculations and the strategies used within the framework.
 * <p>
 * <h3>Key Classes within the {@code io.github.libfp} Module:</h3>
 * <p>
 * - <b>ManagedProfile:</b> This abstract class represents a managed profile
 * associated with a ProfileManager. It serves as the foundation for other
 * profile-related classes, offering features for calculating profile
 * similarities and customizing string representations.
 * <p>
 * - <b>ProfileManager:</b> The "ProfileManager" class, is a fundamental
 * component for managing software profiles within the framework. It
 * orchestrates the operations related to software profiling and provides access
 * to various extensions and strategies.
 * <p>
 * - <b>Profile:</b> This class serves as the base class for both library and
 * application profiles. It provides essential methods for creating and
 * processing profiles, including methods for handling classes, methods, and
 * packages.
 * <p>
 * - <b>PackageProfile:</b> "PackageProfile", represents a profile associated
 * with a package. It maintains information about classes within a package and
 * is responsible for calculating similarities between different packages.
 * <p>
 * - <b>MethodProfile:</b> "MethodProfile", is used to represent and analyze
 * individual methods. It provides information about method descriptors and
 * plays a role in similarity calculations.
 * <p>
 * - <b>VarInt:</b> The "VarInt" class provides utility methods for reading and
 * writing variable-length integers. These methods are essential for efficient
 * data storage and serialization within the framework.
 * <p>
 * - <b>AnalysisScopeBuilder:</b> "AnalysisScopeBuilder",assists in building
 * analysis scopes for the LibFP framework. It facilitates the inclusion of
 * different types of files and formats, such as Android APKs, JARs, DEX files,
 * and AAR files, into the analysis scope.
 * <p>
 * - <b>Correspondences:</b> "Correspondences" is a class used to manage and
 * calculate correspondences between profiles. It plays a role in determining
 * the similarity between different profiles within the LibFP framework.
 * <p>
 * - <b>TypeNames:</b> The "TypeNames" class provides utility methods for
 * handling and formatting class, field, and method names. It assists in
 * generating fuzzy names for these elements and determining their scope.
 * <p>
 * <p>
 * The classes and subpackages within {@code io.github.libfp} collectively
 * contribute to the LibFP framework's ability to manage, analyze, and compare
 * profiles effectively.
 */
package io.github.libfp;
