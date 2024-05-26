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
import io.github.libfp.Descriptor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * The <code>ILContext</code> class represents a context for generating
 * Intermediate Language (IL) descriptors and maintaining a list of descriptors
 * for a specific declaring class.
 *
 * <p>
 * An <code>ILContext</code> is associated with a specific class, and it allows
 * the generation and management of IL descriptors for various class members,
 * such as fields and methods. This class maintains a list of descriptors unique
 * to the declaring class.
 * </p>
 */
public class ILContext
{

    /**
     * The class for which the IL descriptors are being generated.
     */
    public final IClass declaringClass;

    /**
     * A list of IL descriptors associated with the declaring class.
     */
    public final @NotNull List<Descriptor> descriptors;

    /**
     * Constructs a new <code>ILContext</code> for a specific declaring class.
     *
     * @param declaringClass The class for which IL descriptors are generated.
     */
    public ILContext(final IClass declaringClass)
    {
        this.declaringClass = declaringClass;
        this.descriptors = new LinkedList<>();
    }

    /**
     * Adds an IL descriptor to the context and ensures its uniqueness.
     *
     * @param value The IL descriptor to add.
     * @return The IL descriptor, either the newly added one or an existing one
     *         from the list.
     */
    public String addDescriptor(@NotNull String value)
    {
        final int index = descriptors.indexOf(new Descriptor(value));
        if (index != -1) {
            value = getDescriptor(index);
        } else descriptors.add(new Descriptor(value));

        return value;
    }

    /**
     * Retrieves the IL descriptor at the specified index.
     *
     * @param index The index of the IL descriptor to retrieve.
     * @return The IL descriptor at the specified index.
     */
    public String getDescriptor(final int index)
    {
        return descriptors.get(index).toString();
    }
}
