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
import java.io.EOFException;
import java.io.IOException;

/**
 * The VarInt class provides methods for writing and reading integers using a
 * variable-length encoding to make the encoding compact and efficient.
 */
public final class VarInt
{

    /**
     * Writes a long value to a DataOutput stream using variable-length
     * encoding.
     *
     * @param value  The long value to be written.
     * @param output The DataOutput stream to write the value to.
     * @throws IOException If an I/O error occurs while writing to the output
     *                     stream.
     */
    public static void write(long value, @NotNull DataOutput output)
            throws IOException
    {
        if (value < 0) {
            throw new IllegalArgumentException("VarInt cannot be negative");
        }
        while (value > 0b01111111L) {
            output.writeByte((int) (0x80 | (value & 0x7F)));
            value >>= 7;
        }
        output.writeByte((int) value);
    }

    /**
     * Reads a long value from a DataInput stream using variable-length
     * encoding.
     *
     * @param objectInput The DataInput stream to read the value from.
     * @return The long value read from the input stream.
     * @throws IOException If an I/O error occurs while reading from the input
     *                     stream.
     */
    public static long read(@NotNull DataInput objectInput) throws IOException
    {
        long result = 0L;
        int shift = 0;
        try {
            while (true) {
                byte current = objectInput.readByte();
                result |= (current & 0b01111111L) << shift;
                shift += 7;
                if ((current & 0x80) == 0) {
                    break;
                }
            }
        } catch (EOFException e) {
            throw new RuntimeException("EOF while reading VarInt");
        }
        return result;
    }
}
