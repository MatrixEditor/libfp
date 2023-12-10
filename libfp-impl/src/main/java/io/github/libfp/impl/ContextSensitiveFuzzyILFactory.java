package io.github.libfp.impl; //@date 30.10.2023

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
                    "[".repeat(reference.getDerivedMask()) + fuzzyDeclaringTypeIdentifier
                    : fuzzyDeclaringTypeIdentifier;
        }
        return originalDescriptor;
    }
}
