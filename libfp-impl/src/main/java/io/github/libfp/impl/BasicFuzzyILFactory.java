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
package io.github.libfp.impl;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import io.github.libfp.profile.il.ILContext;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static io.github.libfp.util.TypeNames.fuzzyApplicationIdentifier;

/**
 * The BasicFuzzyDescriptorFactory class is responsible for generating fuzzy
 * descriptors for methods, classes, and fields. These fuzzy descriptors are
 * string representations used for profiling and analysis within the LibFP
 * (Library Finger-Printing Framework).
 *
 * @see ILFactory
 */
public class BasicFuzzyILFactory extends ILFactory
{
    private static final BasicFuzzyILFactory defaultInstance =
            new BasicFuzzyILFactory();

    public static BasicFuzzyILFactory getDefaultInstance()
    {
        return defaultInstance;
    }

    /**
     * Generates a fuzzy descriptor for an IMethod (method) based on the
     * provided IMethod instance and ILContext.
     *
     * @param iMethod The IMethod for which a fuzzy descriptor is generated.
     * @param context The ILContext associated with the operation.
     * @return A fuzzy descriptor representing the IMethod.
     */
    @Override
    public String getDescriptor(
            @NotNull IMethod iMethod,
            @NotNull ILContext context)
    {
        final String returnType = getDescriptor(
                iMethod.getReturnType(),
                iMethod.getDeclaringClass()
        );

        StringBuilder descriptor = new StringBuilder();
        String name = iMethod.getName().toString();
        if (name.equals("<clinit>") || name.equals("<init>")) {
            descriptor.append(name);
        } else if (iMethod.isStatic()) {
            // add an identifier to distinguish static methods
            descriptor.append("<static>");
        }
        descriptor.append("(");
        // We MUST start from i=1 if the method is not static as we don't want
        // the current class reference in our descriptor.
        for (int i = iMethod.isStatic() ? 0 : 1;
             i < iMethod.getNumberOfParameters(); i++) {
            TypeReference ref = iMethod.getParameterType(i);
            descriptor.append(getDescriptor(ref, iMethod.getDeclaringClass()));
        }
        return context.addDescriptor(descriptor
                .append(")")
                .append(returnType)
                .toString());
    }

    /**
     * Generates a fuzzy descriptor for an IClass (class) based on the provided
     * IClass instance and ILContext.
     *
     * @param iClass  The IClass for which a fuzzy descriptor is generated.
     * @param context The ILContext associated with the operation.
     * @return A fuzzy descriptor representing the IClass.
     */
    @Override
    public String getDescriptor(
            @NotNull IClass iClass,
            @NotNull ILContext context)
    {
        if (TypeNames.isAppScope(iClass)) {
            if (iClass.isArrayClass()) {
                return "[".repeat(iClass
                        .getName()
                        .getDerivedMask()) + fuzzyApplicationIdentifier;
            }
            return fuzzyApplicationIdentifier;
        }
        return context.addDescriptor(iClass.getName().toString());
    }

    @Override
    public String getDescriptor(
            @NotNull TypeReference reference,
            @NotNull ILContext context)
    {
        boolean isApplication = reference
                .getClassLoader()
                .equals(ClassLoaderReference.Application)
                && !reference.getName().toString().startsWith("Ljava/");

        StringBuilder descriptor = new StringBuilder();
        if (isApplication && reference.isArrayType()) {
            descriptor.append("[".repeat(reference.getDerivedMask()));
        }

        descriptor.append(isApplication
                ? fuzzyApplicationIdentifier
                : reference.getName().toString()
        );
        return context.addDescriptor(descriptor.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> getDocument(@NotNull IClass iClass)
    {
        return super.getDocument(iClass).sorted(String::compareTo);
    }
}

