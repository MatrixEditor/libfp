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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.BitSet;
import java.util.StringJoiner;

/**
 * The <code>BloomFilter</code> class represents a Bloom filter data structure
 * for efficient membership queries. It implements the
 * <code>ISerializable</code> interface for serialization and deserialization
 * support.
 *
 * <p>
 * A Bloom filter is a probabilistic data structure used to test whether an
 * element is a member of a set or not. It is particularly useful for
 * applications where false positives are acceptable but false negatives are
 * not. The class provides methods to add elements, check for membership, and
 * determine if it is a superset of another Bloom filter.
 * </p>
 *
 * @see ISerializable
 */
public class BloomFilter implements ISerializable
{
    public static int defaultK = 4; // num hash functions
    public static int defaultM = 256; // bloom filter size
    private int m;
    private int k;
    private BitSet data;
    private int entries;

    /**
     * Constructs a <code>BloomFilter</code> object by reading its state from a
     * <code>DataInput</code> object during deserialization.
     *
     * @param in The <code>DataInput</code> containing the serialized state.
     * @throws IOException If an I/O error occurs while reading.
     */
    public BloomFilter(@NotNull DataInput in) throws IOException
    {
        this();
        readExternal(in);
    }

    /**
     * Constructs a <code>BloomFilter</code> object with default parameters.
     */
    @ApiStatus.Obsolete
    public BloomFilter()
    {
        this(256, 4);
    }

    /**
     * Constructs a <code>BloomFilter</code> object with custom parameters,
     * including the number of bits (m) and the number of hash functions (k).
     *
     * @param m The number of bits in the Bloom filter.
     * @param k The number of hash functions to use.
     */
    public BloomFilter(int m, int k)
    {
        this.m = m;
        this.k = k;
        this.data = new BitSet(m);
        this.entries = 0;
    }

    /**
     * Constructs a <code>BloomFilter</code> object by initializing it with an
     * array of long values.
     *
     * @param words An array of long values used to initialize the Bloom
     *              filter.
     */
    public BloomFilter(final long @NotNull [] words)
    {
        this.data = BitSet.valueOf(words);
        this.m = data.length();
    }

    /**
     * Returns the number of bits in the Bloom filter.
     *
     * @return The number of bits (m).
     */
    public int length()
    {
        return m;
    }

    /**
     * Returns the number of entries (elements) added to the Bloom filter.
     *
     * @return The number of entries.
     */
    public int entries()
    {
        return entries;
    }

    /**
     * Converts the Bloom filter data to a binary vector.
     *
     * @return A boolean array representing the binary vector.
     */
    public boolean[] toBinaryVector()
    {
        boolean[] vector = new boolean[m];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = data.get(i);
        }
        return vector;
    }

    /**
     * Adds a string value to the Bloom filter, updating its internal state.
     *
     * @param value The string value to add to the Bloom filter.
     */
    public void add(final @NotNull String value)
    {
        for (final int index : hash(value)) {
            data.set(index);
        }
        entries++;
    }

    /**
     * Returns the BitSet representing the Bloom filter data.
     *
     * @return The BitSet containing the Bloom filter data.
     */
    public BitSet data()
    {
        return data;
    }

    /**
     * Computes hash values for a string using the Bloom filter's parameters.
     *
     * @param value The string for which hash values are calculated.
     * @return An array of hash positions for the string.
     */
    public int[] hash(final @NotNull String value)
    {
        int[] positions = new int[this.k];

        final int hash = value.hashCode();
        long b = hash >> 8;

        long running = hash & 0xFF;
        for (int i = 0; i < k; i++) {
            positions[i] = (int) (Math.abs(running) % m);
            running += b;
        }
        return positions;
    }

    /**
     * Checks if a specific bit at the given index is set in the Bloom filter.
     *
     * @param index The index of the bit to check.
     * @return <code>true</code> if the bit is set, <code>false</code>
     *         otherwise.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public boolean at(final int index) throws IndexOutOfBoundsException
    {
        return data.get(index);
    }

    /**
     * Checks if the Bloom filter is empty (contains no elements).
     *
     * @return <code>true</code> if the Bloom filter is empty,
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    /**
     * Writes the state of the Bloom filter to a <code>DataOutput</code> object
     * during serialization.
     *
     * @param out The <code>DataOutput</code> to write the serialized state.
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeShort(entries);
        final boolean empty = isEmpty();

        out.writeBoolean(empty);
        if (!empty) {
            final long[] bloomData = data.toLongArray();
            VarInt.write(bloomData.length, out);
            for (long value : bloomData) {
                out.writeLong(value);
            }
        }
    }

    /**
     * Reads the state of the Bloom filter from a <code>DataInput</code> object
     * during deserialization.
     *
     * @param in The <code>DataInput</code> containing the serialized state.
     * @throws IOException If an I/O error occurs while reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        entries = in.readUnsignedShort();
        boolean empty = in.readBoolean();
        m = defaultM;
        k = defaultK;

        if (!empty) {
            final int bloomLength = (int) VarInt.read(in);
            long[] words = new long[bloomLength];
            for (int i = 0; i < bloomLength; i++) {
                words[i] = in.readLong();
            }
            data = BitSet.valueOf(words);
        } else data = new BitSet(m);
    }

    /**
     * Returns a string representation of the Bloom filter as a matrix of binary
     * bits.
     *
     * @return A string representing the binary matrix of the Bloom filter.
     */
    @Override
    public String toString()
    {
        StringJoiner builder = new StringJoiner("\n");

        int index = 0;
        for (int i = 0; i < 8; i++) {
            StringJoiner sj = new StringJoiner(" ", "| ", " |");
            for (int j = 0; j < 32; j++) {
                sj.add(at(index) ? "1" : "0");
                index++;
            }
            builder.add(sj.toString());
        }
        return builder.toString();
    }

    /**
     * Checks if this Bloom filter is a superset of another Bloom filter.
     *
     * @param bloom The Bloom filter to compare against.
     * @return <code>true</code> if this Bloom filter is a superset of the
     *         other, <code>false</code> otherwise.
     */
    public boolean isSuperSetOf(@NotNull BloomFilter bloom)
    {
        BitSet s = (BitSet) data.clone();
        s.and(bloom.data);
        return s.equals(bloom.data);
    }

    public double getOverlapRatio(@NotNull BloomFilter bloom)
    {
        BitSet s = (BitSet) data.clone();
        s.and(bloom.data);
        return data.cardinality() > bloom.data.cardinality() ?
                (double) s.cardinality() / bloom.data.cardinality() :
                (double) s.cardinality() / data.cardinality();
    }
}
