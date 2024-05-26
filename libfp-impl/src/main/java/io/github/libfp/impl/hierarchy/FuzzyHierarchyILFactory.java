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
package io.github.libfp.impl.hierarchy;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.impl.BasicFuzzyILFactory;
import io.github.libfp.impl.UniqueFuzzyILFactory;
import io.github.libfp.profile.il.ILContext;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;

/**
 * The HierarchyFuzzyDescriptorFactory class extends ILFactory and is
 * responsible for generating hierarchical fuzzy descriptors for methods and
 * classes.
 *
 * @see ILFactory
 * @see UniqueFuzzyILFactory
 */
public class FuzzyHierarchyILFactory
        extends BasicFuzzyILFactory
{

    /**
     * Generates a hierarchical fuzzy descriptor for an IMethod (method) based
     * on the provided IMethod instance and ILContext. The descriptor indicates
     * if the method is abstract.
     *
     * @param iMethod The IMethod for which a fuzzy descriptor is generated.
     * @param context The ILContext associated with the operation.
     * @return A hierarchical fuzzy descriptor representing the IMethod.
     */
    @Override
    public @NotNull String getDescriptor(
            @NotNull IMethod iMethod,
            @NotNull ILContext context)
    {
        StringBuilder builder = new StringBuilder();
        if (iMethod.isAbstract()) {
            builder.append("A");
        }
        return builder + super.getDescriptor(iMethod, context);
    }

    /**
     * Generates a hierarchical fuzzy descriptor for an IClass (class) based on
     * the provided IClass instance and ILContext. The descriptor includes
     * information about the class, such as whether it's abstract, an interface,
     * an enum, or an application class. It also includes information about
     * implemented interfaces and the class's superclasses.
     *
     * @param iClass  The IClass for which a fuzzy descriptor is generated.
     * @param context The ILContext associated with the operation.
     * @return A hierarchical fuzzy descriptor representing the IClass.
     */
    @Override
    public @NotNull String getDescriptor(
            @NotNull IClass iClass,
            @NotNull ILContext context)
    {
        StringBuilder descriptor = new StringBuilder("|");
        if (iClass.isAbstract()) {
            descriptor.append("A");
        }
        if (iClass.isInterface()) {
            descriptor.append("I");
        }
        if ((iClass.getModifiers() & 0x4000) != 0) {
            descriptor.append("E");
        }
        if (TypeNames.isAppScope(iClass)) {
            descriptor.append("X");
        }

        final IClass superClass = iClass.getSuperclass();
        if (superClass != null && !superClass
                .getName()
                .toString()
                .equals("Ljava/lang/Object"))
        {
            descriptor
                    .append("{")
                    .append(getDescriptor(superClass))
                    .append("}");
        }

        descriptor.append("[");
        for (final IClass cls : iClass.getAllImplementedInterfaces()) {
            descriptor.append(super.getDescriptor(cls));
        }

        return descriptor + "]|";
    }
}
