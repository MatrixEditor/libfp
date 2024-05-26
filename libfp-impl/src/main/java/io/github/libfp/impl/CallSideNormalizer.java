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

import com.ibm.wala.classLoader.*;
import com.ibm.wala.shrike.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.Selector;
import io.github.libfp.profile.bytecode.BytecodeContext;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.profile.il.ILFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

public class CallSideNormalizer extends BytecodeNormalizer
{

    public CallSideNormalizer(ILFactory factory)
    {
        super(factory);
    }

    @Override
    public @Nullable JavaBytecodeVisitor getJavaBytecodeVisitor()
    {
        return null;
    }

    @Override
    public @Nullable DalvikBytecodeVisitor getDalvikBytecodeVisitor()
    {
        return null;
    }

    public @Nullable String getFormat(
            final @NotNull CallSiteReference reference,
            @NotNull BytecodeContext context)
    {
        IInvokeInstruction.Dispatch dispatch =
                (IInvokeInstruction.Dispatch) reference.getInvocationCode();
        // REVISIT: "invoke" only would create a more fuzzy approach
        String name = "invoke"; // + dispatch.name().toLowerCase();

        final IClass target = context.hierarchy().lookupClass(reference
                .getDeclaredTarget()
                .getDeclaringClass());
        if (target != null) {
            final Selector selector = reference
                    .getDeclaredTarget()
                    .getSelector();
            IMethod iMethod = target.getMethod(selector);
            if (iMethod != null) {
                return name + ":" + factory.getDescriptor(target) + "->"
                        + factory.getDescriptor(iMethod);
            }
        }
        return null;
    }

    @Override
    public @NotNull Stream<String> normalize(
            @NotNull IMethod iMethod,
            @NotNull BytecodeContext context)
    {
        if (iMethod.isAbstract()) {
            return Stream.empty();
        }

        Collection<String> code = new HashSet<>();
        if (iMethod instanceof IBytecodeMethod) {
            Collection<CallSiteReference> refs = null;
            try {
                refs = CodeScanner.getCallSites(iMethod);
            } catch (InvalidClassFileException |
                     ArrayIndexOutOfBoundsException e) {
                /* ignore */
            }

            if (refs != null) {
                refs.stream()
                        .map(c -> getFormat(c, context))
                        .filter(Objects::nonNull)
                        .forEach(code::add);
            }
        }
        // NOTE: we sort the result to mitigate against possible control flow
        // randomization
        return code.stream().sorted(String::compareTo);
    }
}
