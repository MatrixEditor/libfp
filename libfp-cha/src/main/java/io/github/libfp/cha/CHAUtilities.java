package io.github.libfp.cha; //@date 30.10.2023

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
