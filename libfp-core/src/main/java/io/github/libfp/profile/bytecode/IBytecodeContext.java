package io.github.libfp.profile.bytecode; //@date 24.10.2023

import com.ibm.wala.ipa.cha.IClassHierarchy;
import org.jetbrains.annotations.ApiStatus;

/**
 * The <code>IBytecodeContext</code> record represents a context for bytecode
 * normalization, specifically designed for a given class hierarchy.
 *
 * <p>
 * An <code>IBytecodeContext</code> is designed to encapsulate the class
 * hierarchy information required for normalizing bytecode instructions. It
 * provides a context that can be used during the normalization process to
 * access the class hierarchy.
 * </p>
 *
 * @param hierarchy The class hierarchy associated with the bytecode
 *                  normalization context.
 */
@ApiStatus.Experimental
public record IBytecodeContext(IClassHierarchy hierarchy)
{
    // REVISIT: this class should be extensible
}

