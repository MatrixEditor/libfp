package io.github.libfp.ast;//@date 31.10.2023

import io.github.libfp.profile.il.ILFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The <code>ASTBuilder</code> functional interface represents a contract for
 * building an Abstract Syntax Tree (AST) node of type <code>N</code> from a
 * given input of type <code>T</code.
 *
 * @param <N> The type of AST node that will be constructed.
 * @param <T> The type of input used to build the AST node.
 */
@ApiStatus.Experimental
@FunctionalInterface
public interface ASTBuilder<N extends Node, T>
{
    /**
     * Builds an Abstract Syntax Tree (AST) node of type <code>N</code from the
     * provided input of type <code>T</code.
     *
     * @param t The input data used to construct the AST node.
     *
     * @return An AST node of type <code>N</code.
     * @throws NullPointerException if the input is null and the implementation
     *                              does not support null input.
     */
    @NotNull AbstractSyntaxTree<N> build(
            @NotNull T t,
            @NotNull ILFactory ilFactory);
}

