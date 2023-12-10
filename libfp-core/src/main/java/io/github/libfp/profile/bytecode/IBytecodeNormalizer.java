package io.github.libfp.profile.bytecode;//@date 24.10.2023

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeCTMethod;
import com.ibm.wala.dalvik.classLoader.DexIMethod;
import com.ibm.wala.dalvik.dex.instructions.Instruction;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrike.shrikeBT.IInstruction;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.profile.manager.IModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * The <code>IBytecodeNormalizer</code> abstract class provides a framework for
 * normalizing bytecode instructions in both Java and Dalvik bytecode formats.
 *
 * <p>
 * This class serves as a foundation for normalizing bytecode instructions by
 * providing a structure for bytecode visitors specific to Java and Dalvik
 * bytecode. It includes methods for obtaining and formatting bytecode
 * instructions and offers the ability to normalize an entire method by
 * streaming the normalized instructions.
 * </p>
 */
public abstract class IBytecodeNormalizer implements IModule
{

    /**
     * The factory for generating Intermediate Language (IL) descriptors.
     */
    protected final ILFactory factory;

    /**
     * Constructs a new <code>IBytecodeNormalizer</code> with the provided
     * ILFactory.
     *
     * @param factory The ILFactory to use for generating IL descriptors.
     */
    protected IBytecodeNormalizer(ILFactory factory)
    {
        this.factory = factory;
    }

    /**
     * A Java bytecode visitor for normalizing Java bytecode instructions.
     */
    public static abstract class JavaBytecodeVisitor
            extends IInstruction.Visitor
    {
        protected @Nullable String format = null;
        protected @Nullable IBytecodeContext context = null;

        /**
         * Prepares the visitor for normalization with the given bytecode
         * context.
         *
         * @param context The bytecode context for normalization.
         */
        public void prepare(IBytecodeContext context)
        {
            this.format = null;
            this.context = context;
        }

        /**
         * Gets the normalized format of the bytecode instruction.
         *
         * @return The normalized format.
         */
        public @Nullable String getFormat()
        {
            return format;
        }
    }

    /**
     * A Dalvik bytecode visitor for normalizing Dalvik bytecode instructions.
     */
    public static abstract class DalvikBytecodeVisitor
            extends Instruction.Visitor
    {
        protected @Nullable String format = null;
        protected @Nullable IBytecodeContext context = null;

        /**
         * Prepares the visitor for normalization with the given bytecode
         * context.
         *
         * @param context The bytecode context for normalization.
         */
        public void prepare(IBytecodeContext context)
        {
            this.format = null;
            this.context = context;
        }

        /**
         * Gets the normalized format of the bytecode instruction.
         *
         * @return The normalized format.
         */
        public @Nullable String getFormat()
        {
            return format;
        }
    }

    /**
     * Gets a Java bytecode visitor for normalizing Java bytecode instructions.
     *
     * @return A Java bytecode visitor.
     */
    public abstract JavaBytecodeVisitor getJavaBytecodeVisitor();

    /**
     * Gets a Dalvik bytecode visitor for normalizing Dalvik bytecode
     * instructions.
     *
     * @return A Dalvik bytecode visitor.
     */
    public abstract DalvikBytecodeVisitor getDalvikBytecodeVisitor();

    /**
     * Gets the normalized format of a Java bytecode instruction using the
     * provided context.
     *
     * @param javaInstruction The Java bytecode instruction.
     * @param hierarchy       The class hierarchy context.
     *
     * @return The normalized format of the instruction.
     */
    public final String getFormat(
            final @NotNull IInstruction javaInstruction,
            final IClassHierarchy hierarchy)
    {
        return getFormat(javaInstruction, new IBytecodeContext(hierarchy));
    }

    /**
     * Gets the normalized format of a Java bytecode instruction using the
     * provided context.
     *
     * @param javaInstruction The Java bytecode instruction.
     * @param context         The bytecode context for normalization.
     *
     * @return The normalized format of the instruction.
     */
    public @Nullable String getFormat(
            final @NotNull IInstruction javaInstruction,
            final IBytecodeContext context)
    {
        final JavaBytecodeVisitor visitor = getJavaBytecodeVisitor();
        visitor.prepare(context);
        javaInstruction.visit(visitor);
        return visitor.getFormat();
    }

    /**
     * Gets the normalized format of a Dalvik bytecode instruction using the
     * provided context.
     *
     * @param dexInstruction The Dalvik bytecode instruction.
     * @param hierarchy      The class hierarchy context.
     *
     * @return The normalized format of the instruction.
     */
    public final String getFormat(
            final @NotNull Instruction dexInstruction,
            final IClassHierarchy hierarchy)
    {
        return getFormat(dexInstruction, new IBytecodeContext(hierarchy));
    }

    /**
     * Gets the normalized format of a Dalvik bytecode instruction using the
     * provided context.
     *
     * @param dexInstruction The Dalvik bytecode instruction.
     * @param context        The bytecode context for normalization.
     *
     * @return The normalized format of the instruction.
     */
    public @Nullable String getFormat(
            final @NotNull Instruction dexInstruction,
            final IBytecodeContext context)
    {
        final DalvikBytecodeVisitor visitor = getDalvikBytecodeVisitor();
        visitor.prepare(context);
        dexInstruction.visit(visitor);
        return visitor.getFormat();
    }

    /**
     * Normalizes the bytecode instructions of a method and returns them as a
     * stream of strings.
     *
     * @param iMethod The method to normalize.
     *
     * @return A stream of normalized bytecode instructions.
     */
    public Stream<String> normalize(final @NotNull IMethod iMethod)
    {
        return normalize(iMethod,
                new IBytecodeContext(iMethod.getClassHierarchy()));
    }

    /**
     * Normalizes the bytecode instructions of a method using the provided
     * context and returns them as a stream of strings.
     *
     * @param iMethod The method to normalize.
     * @param context The bytecode context for normalization.
     *
     * @return A stream of normalized bytecode instructions.
     */
    public Stream<String> normalize(
            final @NotNull IMethod iMethod,
            final IBytecodeContext context)
    {

        if (iMethod.isAbstract()) {
            return Stream.empty();
        }

        boolean isJavaMethod = iMethod instanceof ShrikeCTMethod;
        Collection<String> code = new HashSet<>();
        if (isJavaMethod) try {
            final IInstruction[] instructions =
                    ((ShrikeCTMethod) iMethod).getInstructions();
            if (instructions == null || instructions.length == 0) {
                return Stream.empty();
            }

            for (IInstruction instruction : instructions) {
                final String format = getFormat(instruction, context);
                if (format != null) {
                    code.add(format);
                }
            }
        } catch (InvalidClassFileException e) {
            /* ignored */
        }
        else {
            Instruction[] instructions;
            try {
                instructions = ((DexIMethod) iMethod).getInstructions();
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                // method is abstract/ no code information / parsing error
                return Stream.empty();
            }

            if (instructions == null || instructions.length == 0) {
                return Stream.empty();
            }

            for (Instruction instruction : instructions) {
                final String format = getFormat(instruction, context);
                if (format != null) {
                    code.add(format);
                }
            }
        }
        if (code.isEmpty()) {
            return Stream.empty();
        }
        return code.stream();
    }

}
