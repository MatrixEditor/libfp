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

import io.github.libfp.util.IO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The <code>Descriptor</code> class represents a serializable descriptor with
 * an integer count, a string value, and its length. This class implements the
 * {@link ISerializable} interface, allowing instances to be serialized to and
 * deserialized from binary data.
 *
 * <p>
 * The descriptor consists of a count, which represents the number of times this
 * descriptor has been encountered, a string value, and the length of the
 * value.
 * </p>
 *
 * <p>
 * Instances of this class can be serialized by writing the count, length, and
 * value to a {@link DataOutput} and deserialized by reading the same values
 * from a {@link DataInput}.
 * </p>
 *
 * <p>
 * The <code>equals</code> method is overridden to compare this descriptor's
 * string value with another object's string representation. If the string
 * representations match, it returns <code>true</code>; otherwise, it defers to
 * the superclasses <code>equals</code> method.
 * </p>
 *
 * <p>
 * The <code>toString</code> method returns the string value of this
 * descriptor.
 * </p>
 *
 * @see ISerializable
 * @see DataOutput
 * @see DataInput
 * @see IOException
 * @see VarInt
 */
public final class Descriptor implements ISerializable
{

    /**
     * The number of times this descriptor has been encountered.
     */
    public int count;

    /**
     * The string value of the descriptor.
     */
    public String value;

    /**
     * The length of the string value.
     */
    private int length;

    /**
     * Constructs an empty descriptor.
     */
    @Contract(pure = true)
    public Descriptor()
    {
    }

    /**
     * Constructs a descriptor with the given string value.
     * <p>
     * The initial count is set to 1, and the length is calculated based on the
     * string's length.
     *
     * @param value The string value for the descriptor.
     */
    @Contract(pure = true)
    public Descriptor(final @NotNull String value)
    {
        this.value = value;
        this.length = value.length();
        this.count = 1;
    }

    /**
     * Writes the descriptor's state to a {@link DataOutput}.
     *
     * @param out The {@link DataOutput} to write the descriptor's state to.
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        VarInt.write(count, out);
        IO.writeString(value, out);
    }

    /**
     * Reads the descriptor's state from a {@link DataInput}.
     *
     * @param in The {@link DataInput} to read the descriptor's state from.
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        count = (int) VarInt.read(in);
        value = IO.readString(in);
        length = value.length();
    }

    /**
     * Compares this descriptor's string value with the string representation of
     * an object. If they match, returns <code>true</code>; otherwise, defers to
     * the superclasses <code>equals</code> method.
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the string values match, otherwise
     *         <code>false</code>.
     */
    @Override
    public boolean equals(@NotNull Object obj)
    {
        if (obj.toString().equals(value)) {
            return true;
        }
        return super.equals(obj);
    }

    /**
     * Returns the string value of this descriptor.
     *
     * @return The string value.
     */
    @Contract(pure = true)
    @Override
    public String toString()
    {
        return value;
    }
}
