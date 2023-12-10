package io.github.libfp.impl; //@date 24.10.2023

import com.ibm.wala.classLoader.*;
import com.ibm.wala.shrike.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.Selector;
import io.github.libfp.profile.bytecode.IBytecodeContext;
import io.github.libfp.profile.bytecode.IBytecodeNormalizer;
import io.github.libfp.profile.il.ILFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

public class CallSideNormalizer extends IBytecodeNormalizer
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
            @NotNull IBytecodeContext context)
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
            @NotNull IBytecodeContext context)
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
