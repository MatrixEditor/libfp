package io.github.libfp.profile.bytecode;//@date 27.10.2023

import io.github.libfp.profile.il.ILFactory;

import java.util.function.Function;

public interface IBytecodeNormalizerFactory
        extends Function<ILFactory, IBytecodeNormalizer>
{
    @Override
    IBytecodeNormalizer apply(ILFactory factory);
}
