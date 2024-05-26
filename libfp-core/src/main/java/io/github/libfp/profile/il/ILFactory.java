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
package io.github.libfp.profile.il;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.TypeReference;
import io.github.libfp.profile.manager.IModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * The <code>ILFactory</code> abstract class provides a factory for generating
 * Intermediate Language (IL) descriptors and documentation for Java classes,
 * fields, and methods in a format suitable for various purposes, such as
 * analysis and code generation.
 * <pre>
 *                            +-------------------------------+
 *                            | Lde/github/example/Foo        |
 *                            +-------------------------------+
 *                            | &lt;init&gt;()V                     |
 *                            | method1()V                    |
 *                            | method2(Ljava/lang/String;I)V |
 *                            | method3(Ljava/lang/String;I)V |
 *                            +---------------+---------------+
 *                                            |
 *                                            |
 *            +-------------------------------+-----------------------------+
 *            |                               |                             |
 *            |                               |                             |
 * +----------v-------------+     +-----------v----------+     +------------v-----------+
 * |FuzzyHierarchyILFactory |     |BasicFuzzyILFactory   |     |UniqueFuzzyILFactory    |
 * +------------------------+     +----------------------+     +------------------------+
 * ||X[]|                   |     |X                     |     |Ljava/lang/Object[]     |
 * +------------------------+     +----------------------+     +------------------------+
 * |&lt;init&gt;()V               |     |&lt;init&gt;()V             |     |&lt;init&gt;()V               |
 * |()V                     |     |()V                   |     |()V                     |
 * |(Ljava/lang/StringI)V   |     |(Ljava/lang/StringI)V |     |(Ljava/lang/StringI)V   |
 * |(Ljava/lang/StringI)V   |     |(Ljava/lang/StringI)V |     |(Ljava/lang/StringI)V:1 |
 * +------------------------+     +----------------------+     +------------------------+
 * </pre>
 */
public abstract class ILFactory implements IModule
{

    /**
     * Gets the descriptor for the specified field.
     *
     * @param iField The field for which to get the descriptor.
     * @return The descriptor for the specified field.
     */
    public String getDescriptor(final @NotNull IField iField)
    {
        return getDescriptor(iField, new ILContext(iField.getDeclaringClass()));
    }

    /**
     * Gets the descriptor for the specified field and context.
     *
     * @param iField  The field for which to get the descriptor.
     * @param context The context for generating the descriptor.
     * @return The descriptor for the specified field.
     */
    public String getDescriptor(
            final @NotNull IField iField,
            final ILContext context)
    {
        return getDescriptor(iField.getFieldTypeReference(), context);
    }

    /**
     * Gets the descriptor for the specified method.
     *
     * @param iMethod The method for which to get the descriptor.
     * @return The descriptor for the specified method.
     */
    public String getDescriptor(final @NotNull IMethod iMethod)
    {
        return getDescriptor(iMethod,
                new ILContext(iMethod.getDeclaringClass()));
    }

    /**
     * Gets the descriptor for the specified method and context.
     *
     * @param iMethod The method for which to get the descriptor.
     * @param context The context for generating the descriptor.
     * @return The descriptor for the specified method.
     */
    public abstract String getDescriptor(
            final IMethod iMethod,
            final ILContext context);

    /**
     * Gets the descriptor for the specified class.
     *
     * @param iClass The class for which to get the descriptor.
     * @return The descriptor for the specified class.
     */
    public String getDescriptor(final IClass iClass)
    {
        return getDescriptor(iClass, new ILContext(iClass));
    }

    /**
     * Gets the descriptor for the specified class and context.
     *
     * @param iClass  The class for which to get the descriptor.
     * @param context The context for generating the descriptor.
     * @return The descriptor for the specified class.
     */
    public abstract String getDescriptor(
            final IClass iClass,
            final ILContext context);

    public String getDescriptor(
            final TypeReference reference,
            final IClass declaringClass
    )
    {
        return getDescriptor(reference, new ILContext(declaringClass));
    }

    public abstract String getDescriptor(
            final TypeReference reference,
            final ILContext context
    );

    /**
     * Gets a stream of IL document lines for the specified class.
     *
     * @param iClass The class for which to generate the IL document.
     * @return A stream of IL document lines for the specified class.
     */
    public Stream<String> getDocument(
            final @NotNull IClass iClass)
    {
        return getDocument(iClass, new ILContext(iClass));
    }

    /**
     * Gets a stream of IL document lines for the specified class.
     *
     * @param iClass The class for which to generate the IL document.
     * @return A stream of IL document lines for the specified class.
     */
    public Stream<String> getDocument(
            final @NotNull IClass iClass,
            final @NotNull ILContext context)
    {
        final List<? extends IMethod> methods = iClass
                .getDeclaredMethods()
                .stream()
                .filter(method -> !method.isWalaSynthetic() &&
                        !method.isSynthetic())
                .toList();

        final Collection<IField> fields = iClass.getAllFields();

        Collection<String> repr = new ArrayList<>();

        // Header: class descriptor
        repr.add(getDescriptor(iClass, context));
        // all fields
        if (!fields.isEmpty()) {
            fields.stream()
                    .map(field -> getDescriptor(field, context))
                    .forEach(repr::add);
        }

        if (!methods.isEmpty()) {
            methods.stream()
                    .map(method -> getDescriptor(method, context))
                    .forEach(repr::add);
        }

        return repr.stream();
    }

    /**
     * Gets a stream of IL document lines for the fields of the specified
     * class.
     *
     * @param iClass The class for which to generate IL document lines for
     *               fields.
     * @return A stream of IL document lines for the fields of the specified
     *         class.
     */
    public Stream<String> getFields(final @NotNull IClass iClass)
    {
        final Collection<IField> fields = iClass.getAllFields();
        ILContext context = new ILContext(iClass);
        Collection<String> repr = new ArrayList<>(fields.size());
        if (!fields.isEmpty()) {
            fields.stream()
                    .map(field -> getDescriptor(field, context))
                    .forEach(repr::add);
        }
        return repr.stream();
    }

    /**
     * Gets a stream of IL document lines for the methods of the specified
     * class.
     *
     * @param iClass The class for which to generate IL document lines for
     *               methods.
     * @return A stream of IL document lines for the methods of the specified
     *         class.
     */
    public Stream<String> getMethods(
            final @NotNull IClass iClass)
    {
        return getMethods(iClass, new ILContext(iClass));
    }

    /**
     * Gets a stream of IL document lines for the methods of the specified
     * class.
     *
     * @param iClass The class for which to generate IL document lines for
     *               methods.
     * @return A stream of IL document lines for the methods of the specified
     *         class.
     */
    public Stream<String> getMethods(
            final @NotNull IClass iClass,
            final @NotNull ILContext context)
    {
        final List<? extends IMethod> methods = iClass
                .getDeclaredMethods()
                .stream()
                .filter(method -> !method.isWalaSynthetic() &&
                        !method.isSynthetic())
                .toList();

        Collection<String> repr = new ArrayList<>(methods.size());
        if (!methods.isEmpty()) {
            methods.stream()
                    .map(m -> getDescriptor(m, context))
                    .forEach(repr::add);
        }
        return repr.stream();
    }
}
