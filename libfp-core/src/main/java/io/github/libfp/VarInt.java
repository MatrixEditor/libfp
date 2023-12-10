package io.github.libfp; //@date 21.10.2023

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

/**
 * The VarInt class provides methods for writing and reading integers using a
 * variable-length encoding to make the encoding compact and efficient.
 */
public final class VarInt /* big endian */
{

    /**
     * Writes a long value to a DataOutput stream using variable-length
     * encoding.
     *
     * @param value  The long value to be written.
     * @param output The DataOutput stream to write the value to.
     *
     * @throws IOException If an I/O error occurs while writing to the output
     *                     stream.
     */
    public static void write(long value, @NotNull DataOutput output)
            throws IOException
    {
        while (Math.abs(value) > 0b01111111L) {
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
     *
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
