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
package io.github.libfp.util;

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

/**
 * The {@code IO} class provides utility methods for reading and writing data
 * from/to {@link DataInput} and {@link DataOutput} streams. It includes methods
 * for reading and writing strings, enumerations, and collections of
 * serializable objects.
 */
public final class IO
{
    private IO()
    {
    }

    /**
     * Reads a string from the specified {@link DataInput} stream.
     *
     * @param input The {@link DataInput} stream from which to read the string.
     * @return The string read from the input stream.
     * @throws IOException If an I/O error occurs during data reading.
     */
    public static String readString(DataInput input) throws IOException
    {
        final int length = (int) VarInt.read(input);
        byte[] raw = new byte[length];

        input.readFully(raw);
        return new String(raw);
    }

    /**
     * Writes a string to the specified {@link DataOutput} stream.
     *
     * @param s      The string to write to the output stream.
     * @param output The {@link DataOutput} stream to which the string is
     *               written.
     * @throws IOException If an I/O error occurs during data writing.
     */
    public static void writeString(final String s, DataOutput output)
            throws IOException
    {
        VarInt.write(s.length(), output);
        output.writeBytes(s);
    }

    /**
     * Reads an enumeration value from the specified {@link DataInput} stream
     * based on the provided array of enum values.
     *
     * @param <E>    The type of the enumeration.
     * @param values An array of enum values for the enumeration.
     * @param in     The {@link DataInput} stream from which to read the
     *               enumeration value.
     * @return The enumeration value read from the input stream.
     * @throws IOException If an I/O error occurs during data reading.
     */
    public static <E extends Enum<E>> E readEnum(E[] values, DataInput in)
            throws IOException
    {
        return values[(int) VarInt.read(in)];
    }

    /**
     * Writes an enumeration value to the specified {@link DataOutput} stream.
     *
     * @param <E> The type of the enumeration.
     * @param e   The enumeration value to write to the output stream.
     * @param out The {@link DataOutput} stream to which the enumeration value
     *            is written.
     * @throws IOException If an I/O error occurs during data writing.
     */
    public static <E extends Enum<E>> void writeEnum(E e, DataOutput out)
            throws IOException
    {
        VarInt.write(e.ordinal(), out);
    }

    /**
     * Writes a collection of serializable objects to the specified
     * {@link DataOutput} stream.
     *
     * @param <T>      The type of the serializable objects.
     * @param profiles The collection of serializable objects to write to the
     *                 output stream.
     * @param out      The {@link DataOutput} stream to which the serializable
     *                 objects are written.
     * @throws IOException If an I/O error occurs during data writing.
     */
    public static <T extends ISerializable> void writeList(
            @NotNull Collection<T> profiles,
            @NotNull DataOutput out) throws IOException
    {
        for (final T profile : profiles) {
            profile.writeExternal(out);
        }
    }

    public static void writeArray(
            byte @Nullable [] bytes,
            @NotNull DataOutput output)
            throws IOException
    {
        VarInt.write(bytes != null ? bytes.length : 0, output);
        if (bytes != null) {
            output.write(bytes);
        }
    }

    public static @NotNull byte[] readArray(@NotNull DataInput input)
            throws IOException
    {
        final int length = (int) VarInt.read(input);
        byte[] values = new byte[length];
        input.readFully(values);
        return values;
    }
}
