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

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The <code>Node</code> class serves as a base class for representing nodes in
 * an {@link AbstractSyntaxTree}. It implements the <code>ISerializable</code>
 * interface to support serialization and deserialization of its state.
 */
@ApiStatus.Experimental
public abstract class Node implements ISerializable
{
    /**
     * An integer representing the parent node of this one.
     */
    public int parent;

    /**
     * Constructs a new <code>Node</code> object.
     */
    public Node()
    {
    }

    /**
     * Reads the state of the node from a <code>DataInput</code> object.
     *
     * @param in The <code>DataInput</code> to read the node's state from.
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        parent = (int) VarInt.read(in);
    }

    /**
     * Writes the state of the node to a <code>DataOutput</code> object.
     *
     * @param out The <code>DataOutput</code> to write the node's state to.
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        VarInt.write(parent, out);
    }
}
