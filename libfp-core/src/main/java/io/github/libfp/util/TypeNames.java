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

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.core.util.strings.Atom;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The TypeNames class provides a set of static utility methods for working with
 * class and type names.
 */
public final class TypeNames
{

    public static final String fuzzyApplicationIdentifier = "X";
    public static final String fuzzyDeclaringTypeIdentifier = "T";

    private TypeNames()
    {
    }

    /**
     * Get a fuzzy invocation name for an IClass, suitable for similarity
     * calculations.
     *
     * @param cls The IClass for which to get the fuzzy invocation name.
     * @return A fuzzy invocation name for the IClass.
     */
    public static @NotNull String getFuzzyInvocation(@Nullable IClass cls)
    {
        String result = fuzzyApplicationIdentifier;
        if (cls != null && !isAppScope(cls)) {
            result = cls.getName().toString();
        }
        return result;
    }

    /**
     * Check if an IClass belongs to the application scope.
     *
     * @param iClass The IClass to check.
     * @return True if the IClass belongs to the application scope, false
     *         otherwise.
     */
    public static boolean isAppScope(final @NotNull IClass iClass)
    {
        final String className = getSimpleName(iClass);

        return isAppScope(iClass.getClassHierarchy(),
                iClass.getClassLoader()) && !(
                (className.equals("R")
                        || className.startsWith("R$")
                        || className.equals("BuildConfig")
                        || className.equals("module-info")
                        || className.startsWith("Ljava/")
                ) || Pattern.matches("^.+\\$\\d+$", className)
                        // anonymous inner class
                        || Pattern.matches("^.+\\$\\d+\\$\\d+$", className)
                        // anonymous inner inner class
                        // empty class
                        || (
                        (iClass.getDeclaredMethods().size() == 1 && iClass
                                .getDeclaredMethods()
                                .iterator()
                                .next()
                                .isClinit())
                                && iClass.getDeclaredInstanceFields().isEmpty()
                                && iClass.getDeclaredStaticFields().isEmpty()
                )
        );
    }

    /**
     * Get the simple name of an IClass.
     *
     * @param cls The IClass for which to get the simple name.
     * @return The simple name of the IClass.
     */
    public static @NotNull String getSimpleName(@NotNull IClass cls)
    {
        String className = cls.getName().getClassName().toString();
        if (className.endsWith(";")) {
            return className.substring(1, className.length() - 2);
        }
        return className;
    }

    /**
     * Check if an IClassLoader belongs to the application scope in an
     * IClassHierarchy.
     *
     * @param hierarchy The IClassHierarchy containing the loader.
     * @param loader    The IClassLoader to check.
     * @return True if the IClassLoader belongs to the application scope, false
     *         otherwise.
     */
    public static boolean isAppScope(
            final @NotNull IClassHierarchy hierarchy,
            IClassLoader loader)
    {
        return hierarchy.getScope().isApplicationLoader(loader);
    }

    /**
     * Look up an IClass by its name in an IClassHierarchy.
     *
     * @param cha       The IClassHierarchy in which to perform the lookup.
     * @param className The name of the class to look up.
     * @return The IClass matching the given name, or null if not found.
     */
    public static IClass lookup(
            @NotNull IClassHierarchy cha,
            @NotNull String className)
    {
        String name = className.replaceAll("\\.", "/");
        if (name.endsWith(";")) {
            name = name.substring(0, name.length() - 1);
        }
        if (!name.startsWith("L")) {
            name = String.format("L%s", name);
        }
        return cha.lookupClass(
                TypeReference.findOrCreate(ClassLoaderReference.Application,
                        name));
    }

    /**
     * Get a hash code for a Java Class object.
     *
     * @param type The Java Class object for which to calculate the hash code.
     * @return The hash code for the Java Class object.
     */
    public static int hash(@NotNull Class<?> type)
    {
        return type.getName().hashCode();
    }

    public static @NotNull List<String> getPackages(
            final @NotNull TypeName name)
    {
        Atom atom = name.getPackage();
        if (atom == null) {
            return Collections.emptyList();
        }
        return getPackages(atom.toString());
    }

    public static @NotNull List<String> getPackages(final @NotNull String name)
    {
        final String[] baseNames = name.split("/");
        /*                                                  v-- max length  */
        List<String> packages = new ArrayList<>(baseNames.length);
        for (int i = 0; i < baseNames.length; i++) {
            final String packageName = baseNames[i];
            if (i == 0) {
                packages.add(packageName);
            } else {
                packages.add(packages.get(i - 1) + "." + packageName);
            }
        }
        return Collections.unmodifiableList(packages);
    }
}
