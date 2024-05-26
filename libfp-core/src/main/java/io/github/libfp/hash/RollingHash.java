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
package io.github.libfp.hash; //

import io.github.libfp.ISerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The <code>RollingHash</code> class represents a rolling hash data structure
 * for efficiently storing and querying hash values. It implements the
 * <code>ISerializable</code> interface for serialization and deserialization
 * support.
 *
 * <p>
 * A rolling hash is used to efficiently store and query hash values for a set
 * of strings. It provides methods to add new hash values and check for
 * containment. The class maintains a set of unique hash values and allows
 * iteration over these values.
 * </p>
 *
 * @see ISerializable
 */
public class RollingHash implements ISerializable, Iterable<Integer>
{
    // REVISIT: these values should be variable
    public static int rollingHashN = 1000007;
    public static int rollingHashBase = 256;

    /**
     * The base used for hash calculations.
     */
    private final int base;

    /**
     * The modulus value for hash calculations.
     */
    private final int n;

    /**
     * A set to store unique hash values.
     */
    private Set<Integer> values;

    /**
     * Constructs a <code>RollingHash</code> object by reading its state from a
     * <code>DataInput</code> object during deserialization.
     *
     * @param in The <code>DataInput</code> containing the serialized state.
     * @throws IOException If an I/O error occurs while reading.
     */
    public RollingHash(@NotNull DataInput in) throws IOException
    {
        this();
        readExternal(in);
    }

    /**
     * Constructs a <code>RollingHash</code> object with default settings,
     * including the rolling hash base, the hash modulus, and an initial size
     * for the hash value set.
     */
    @ApiStatus.Obsolete
    public RollingHash()
    {
        this(256, 10007, 10);
    }

    /**
     * Constructs a <code>RollingHash</code> object with custom settings,
     * including the rolling hash base, the hash modulus, and an initial size
     * for the hash value set.
     *
     * @param base The base used for hash calculations.
     * @param n    The modulus value for hash calculations.
     * @param size The initial size for the hash value set.
     */
    public RollingHash(int base, int n, int size)
    {
        this.base = base;
        this.n = n;
        this.values = new HashSet<>(Math.min(size, 10));
    }

    /**
     * Gets the number of unique hash values stored in the
     * <code>RollingHash</code> object.
     *
     * @return The number of unique hash values.
     */
    public int size()
    {
        return values.size();
    }

    /**
     * Adds a new hash value to the <code>RollingHash</code> object.
     *
     * @param value The string for which the hash value is calculated and
     *              added.
     */
    public void add(final @NotNull String value)
    {
        int hashValue = 0;
        for (int i = 0; i < value.length(); i++) {
            hashValue = (hashValue * base + value.charAt(i)) % n;
        }

        if (hashValue != 0) values.add(hashValue);
    }

    /**
     * Checks if the <code>RollingHash</code> object contains a specific hash
     * value.
     *
     * @param hash The hash value to check for containment.
     * @return <code>true</code> if the hash value is contained in the set,
     *         <code>false</code> otherwise.
     */
    public boolean contains(final int hash)
    {
        return values.contains(hash);
    }

    /**
     * Reads the state of the <code>RollingHash</code> object from a
     * <code>DataInput</code> object during deserialization.
     *
     * @param in The <code>DataInput</code> containing the serialized state.
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int valueCount = in.readUnsignedShort();
        values = new HashSet<>(valueCount);
        for (int i = 0; i < valueCount; i++) {
            values.add(in.readInt());
        }
    }

    /**
     * Writes the state of the <code>RollingHash</code> object to a
     * <code>DataOutput</code> object during serialization.
     *
     * @param out The <code>DataOutput</code> to write the serialized state.
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeShort(values.size());
        for (int value : values) {
            out.writeInt(value);
        }
    }

    /**
     * Returns an iterator to iterate over the unique hash values in the
     * <code>RollingHash</code> object.
     *
     * @return An iterator over the unique hash values.
     */
    @Override
    public @NotNull Iterator<Integer> iterator()
    {
        return values.iterator();
    }
}
