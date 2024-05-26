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
package io.github.libfp;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The <code>ISerializable</code> interface represents a contract for objects
 * that can be serialized to and deserialized from binary data. Objects
 * implementing this interface must provide methods to write their state to a
 * {@link DataOutput} and read their state from a {@link DataInput}.
 *
 * <p>
 * Implementing classes should ensure that the data written and read are
 * compatible, following the same format for serialization and deserialization.
 * This interface is typically used to allow objects to be stored in a
 * persistent storage or transferred over a network.
 * </p>
 *
 * <p>
 * Implementing classes should handle any exceptions that may occur during
 * serialization or deserialization and specify the format used for writing and
 * reading data.
 * </p>
 *
 * @see DataOutput
 * @see DataInput
 * @see IOException
 */
public interface ISerializable
{

    /**
     * Writes the object's state to a {@link DataOutput}.
     *
     * @param out The {@link DataOutput} to write the object's state to.
     * @throws IOException If an I/O error occurs while writing.
     */
    void writeExternal(@NotNull DataOutput out) throws IOException;

    /**
     * Reads the object's state from a {@link DataInput}.
     *
     * @param in The {@link DataInput} to read the object's state from.
     * @throws IOException If an I/O error occurs while reading.
     */
    void readExternal(@NotNull DataInput in) throws IOException;
}
