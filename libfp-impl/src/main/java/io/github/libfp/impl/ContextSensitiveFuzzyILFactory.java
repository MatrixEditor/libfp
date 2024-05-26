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

import com.ibm.wala.types.TypeReference;
import io.github.libfp.profile.il.ILContext;
import org.jetbrains.annotations.NotNull;

import static io.github.libfp.util.TypeNames.fuzzyDeclaringTypeIdentifier;

public class ContextSensitiveFuzzyILFactory
        extends BasicFuzzyILFactory
{
    @Override
    public String getDescriptor(
            @NotNull TypeReference reference,
            @NotNull ILContext context)
    {
        final String originalDescriptor =
                super.getDescriptor(reference, context);

        final String declaringTypeName =
                context.declaringClass.getName().toString();

        if (reference.getName().toString().equals(declaringTypeName)) {
            return reference.isArrayType() ?
                    "[".repeat(reference.getDerivedMask()) +
                            fuzzyDeclaringTypeIdentifier
                    : fuzzyDeclaringTypeIdentifier;
        }
        return originalDescriptor;
    }
}
