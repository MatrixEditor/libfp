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
package io.github.libfp.cha;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMember;
import com.ibm.wala.classLoader.IMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

public final class CHAUtilities
{

    @Contract(pure = true)
    public static @NotNull Predicate<? super IMethod> getCompilerGeneratedMethodFilter()
    {
        return m -> m.isSynthetic() || m.isWalaSynthetic() || m.isBridge();
    }

    public static @NotNull Predicate<? super IMethod> getNonCompilerGeneratedMethodFilter()
    {
        return getCompilerGeneratedMethodFilter().negate();
    }

    @Contract(pure = true)
    public static @NotNull Predicate<? super IMethod> getStaticMethodFilter()
    {
        return IMember::isStatic;
    }

    public static @NotNull Predicate<? super IMethod> getInstanceMethodFilter()
    {
        return getStaticMethodFilter().negate();
    }

    public static @NotNull Collection<? extends IMethod> getStaticMethods(
            final @NotNull IClass iClass,
            final boolean compilerGenerated)
    {
        return getAllMethods(iClass, compilerGenerated, true);
    }

    public static @NotNull Collection<? extends IMethod> getInstanceMethods(
            final @NotNull IClass iClass,
            final boolean compilerGenerated)
    {
        return getAllMethods(iClass, compilerGenerated, false);
    }

    public static @NotNull Collection<? extends IMethod> getAllMethods(
            final @NotNull IClass iClass,
            final boolean compilerGenerated,
            final boolean staticMethods)
    {
        return iClass.getAllMethods()
                .stream()
                .filter(compilerGenerated
                        ? getCompilerGeneratedMethodFilter()
                        : getNonCompilerGeneratedMethodFilter())
                .filter(staticMethods
                        ? getStaticMethodFilter()
                        : getInstanceMethodFilter())
                .toList();
    }

}
