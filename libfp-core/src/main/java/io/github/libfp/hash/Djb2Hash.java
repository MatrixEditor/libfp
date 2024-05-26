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
package io.github.libfp.hash;  

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The <code>Djb2Hash</code> class represents a hash value calculated using the
 * DJB2 algorithm. It implements the <code>ISerializable</code> interface to
 * support serialization and deserialization of its state.
 *
 * <p>
 * The DJB2 algorithm is used to calculate a hash value from a given string.
 * This class allows you to set the hash value based on a text input and
 * provides methods for reading and writing the hash value to binary data.
 * </p>
 *
 * <p>
 * The class also supports comparison with other objects, allowing equality
 * checks with other <code>Djb2Hash</code> objects or long values.
 * </p>
 *
 * @see ISerializable
 */
public class Djb2Hash implements ISerializable
{
    /**
     * A long value representing the DJB2 hash value.
     */
    public long value;

    /**
     * Constructs a new <code>Djb2Hash</code> object with an initial value of
     * 0.
     */
    public Djb2Hash()
    {
    }

    /**
     * Constructs a <code>Djb2Hash</code> object with the hash value calculated
     * from the provided text input.
     *
     * @param text The text input for which the hash value is calculated.
     */
    public Djb2Hash(final @NotNull String text)
    {
        setValue(text);
    }

    /**
     * Constructs a <code>Djb2Hash</code> object by reading the hash value from
     * a <code>DataInput</code> object during deserialization.
     *
     * @param input The <code>DataInput</code> containing the hash value to be
     *              read.
     * @throws IOException If an I/O error occurs while reading.
     */
    public Djb2Hash(@NotNull DataInput input) throws IOException
    {
        readExternal(input);
    }

    /**
     * Sets the hash value based on the provided text input using the DJB2
     * algorithm.
     *
     * @param text The text input for which the hash value is calculated.
     */
    public final void setValue(final @NotNull String text)
    {
        long hash = 5381;
        for (final char c : text.toCharArray()) {
            // hash * 33: due to faster runtime in calculation
            hash = ((hash << 5) + hash) + c;
        }
        this.value = hash;
    }

    /**
     * Reads the hash value from a <code>DataInput</code> object during
     * deserialization.
     *
     * @param in The <code>DataInput</code> to read the hash value from.
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        value = VarInt.read(in);
    }

    /**
     * Writes the hash value to a <code>DataOutput</code> object during
     * serialization.
     *
     * @param out The <code>DataOutput</code> to write the hash value to.
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        VarInt.write(value, out);
    }

    /**
     * Compares the <code>Djb2Hash</code> object with another object for
     * equality.
     *
     * @param object The object to compare with.
     * @return <code>true</code> if the objects are equal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (!(object instanceof Djb2Hash djb2Hash)) {
            // Support comparison with long values
            if (object instanceof Long other) {
                return other == value;
            }
            return false;
        }
        return value == djb2Hash.value;
    }
}
