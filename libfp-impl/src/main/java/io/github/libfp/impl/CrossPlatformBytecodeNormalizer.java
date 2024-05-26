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
import com.ibm.wala.dalvik.dex.instructions.GetField;
import com.ibm.wala.dalvik.dex.instructions.Invoke;
import com.ibm.wala.dalvik.dex.instructions.PutField;
import com.ibm.wala.shrike.shrikeBT.IGetInstruction;
import com.ibm.wala.shrike.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrike.shrikeBT.IPutInstruction;
import com.ibm.wala.types.Selector;
import io.github.libfp.profile.bytecode.BytecodeContext;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * A class for normalizing bytecode instructions for both Java and Dalvik
 * platforms.
 */
public final class CrossPlatformBytecodeNormalizer extends BytecodeNormalizer
{

    // Bytecode visitors for Java and Dalvik platforms
    private final JavaBytecodeVisitor javaVisitor = new JavaNormalizer();
    private final DalvikBytecodeVisitor dalvikVisitor = new DalvikNormalizer();

    /**
     * Constructs a new CrossPlatformBytecodeNormalizer with the provided
     * ILFactory.
     *
     * @param factory The ILFactory to use for generating IL descriptors.
     */
    public CrossPlatformBytecodeNormalizer(ILFactory factory)
    {
        super(factory);
    }

    /**
     * Gets the normalized form of a bytecode instruction.
     *
     * @param name           The instruction name.
     * @param declaringClass The class where the instruction is declared.
     * @param ref            The reference to the field or method.
     * @param isStatic       True if the instruction is static, false
     *                       otherwise.
     * @return The normalized instruction format.
     */
    private @NotNull String getNormalizedForm(
            String name,
            final IClass declaringClass,
            final String ref,
            final boolean isStatic)
    {
        if (isStatic) {
            name = "s-" + name;
        }
        return name + ":" + factory.getDescriptor(declaringClass) + "->" + ref;
    }

    @Override
    public @NotNull DalvikBytecodeVisitor getDalvikBytecodeVisitor()
    {
        return dalvikVisitor;
    }

    @Override
    public @NotNull JavaBytecodeVisitor getJavaBytecodeVisitor()
    {
        return javaVisitor;
    }

    @Override
    public Stream<String> normalize(
            @NotNull IMethod iMethod,
            BytecodeContext context)
    {
        // NOTE: This stream is sorted for resilience against control flow
        // randomization
        return super.normalize(iMethod, context).sorted(String::compareTo);
    }

    /**
     * Inner class for normalizing Java bytecode instructions.
     */
    private final class JavaNormalizer extends JavaBytecodeVisitor
    {

        @Override
        public void visitPut(@NotNull IPutInstruction put)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    put.getClassType());
            final IClass fieldType = TypeNames.lookup(context.hierarchy(),
                    put.getFieldType());

            if (target != null && fieldType != null) {
                // Format either "put:CLASS->FIELD_TYPE" or
                // "s-put:CLASS->FIELD_TYPE"
                format = getNormalizedForm("put", target,
                        factory.getDescriptor(fieldType), put.isStatic());
            }
        }

        @Override
        public void visitGet(@NotNull IGetInstruction get)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    get.getClassType());
            final IClass fieldType = TypeNames.lookup(context.hierarchy(),
                    get.getFieldType());

            if (target != null && fieldType != null) {
                // Format either "get:CLASS->FIELD_TYPE" or
                // "s-get:CLASS->FIELD_TYPE"
                format = getNormalizedForm("get", target,
                        factory.getDescriptor(fieldType), get.isStatic());
            }
        }

        @Override
        public void visitInvoke(@NotNull IInvokeInstruction invoke)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    invoke.getClassType());

            if (target != null) {
                IInvokeInstruction.Dispatch dispatch =
                        (IInvokeInstruction.Dispatch) invoke.getInvocationCode();
                final Selector selector =
                        Selector.make(invoke.getMethodSignature());

                IMethod iMethod = target.getMethod(selector);
                if (iMethod != null) {
                    // "invoke" or "s-invoke" if static: Used for generating
                    // coarse-grained function calls
                    format = getNormalizedForm("invoke", target,
                            factory.getDescriptor(iMethod),
                            dispatch == IInvokeInstruction.Dispatch.STATIC);
                }
            }
        }
    }

    /**
     * Inner class for normalizing Dalvik bytecode instructions.
     */
    private final class DalvikNormalizer extends DalvikBytecodeVisitor
    {

        @Override
        public void visitPutField(@NotNull PutField put)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    put.clazzName);
            final IClass fieldType = TypeNames.lookup(context.hierarchy(),
                    put.fieldType);

            if (target != null && fieldType != null) {
                // Format either "put:CLASS->FIELD_TYPE" or
                // "s-put:CLASS->FIELD_TYPE"
                format = getNormalizedForm("put", target,
                        factory.getDescriptor(fieldType),
                        put instanceof PutField.PutStaticField);
            }
        }

        @Override
        public void visitGetField(@NotNull GetField get)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    get.clazzName);
            final IClass fieldType = TypeNames.lookup(context.hierarchy(),
                    get.fieldType);

            if (target != null && fieldType != null) {
                // Format either "get:CLASS->FIELD_TYPE" or
                // "s-get:CLASS->FIELD_TYPE"
                format = getNormalizedForm("get", target,
                        factory.getDescriptor(fieldType),
                        get instanceof GetField.GetStaticField);
            }
        }

        @Override
        public void visitInvoke(@NotNull Invoke invoke)
        {
            final IClass target = TypeNames.lookup(context.hierarchy(),
                    invoke.clazzName);

            if (target != null) {
                IInvokeInstruction.Dispatch dispatch =
                        (IInvokeInstruction.Dispatch) invoke.getInvocationCode();
                final Selector selector =
                        Selector.make(invoke.methodName + invoke.descriptor);

                IMethod iMethod = target.getMethod(selector);
                if (iMethod != null) {
                    // "invoke" or "s-invoke" if static: Used for generating
                    // coarse-grained function calls
                    format = getNormalizedForm("invoke", target,
                            factory.getDescriptor(iMethod),
                            dispatch == IInvokeInstruction.Dispatch.STATIC);
                }
            }
        }
    }
}
