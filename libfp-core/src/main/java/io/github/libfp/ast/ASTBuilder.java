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
package io.github.libfp.ast; 

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
public interface ASTBuilder<N extends Node, T, A extends AbstractSyntaxTree<N>>
{
    /**
     * Builds an Abstract Syntax Tree (AST) node of type <code>N</code from the
     * provided input of type <code>T</code.
     *
     * @param t The input data used to construct the AST node.
     * @return An AST node of type <code>N</code.
     * @throws NullPointerException if the input is null and the implementation
     *                              does not support null input.
     */
    @NotNull A build(
            @NotNull T t,
            @NotNull ILFactory ilFactory);
}

