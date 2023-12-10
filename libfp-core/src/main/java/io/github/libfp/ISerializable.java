package io.github.libfp;//@date 21.10.2023

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
     *
     * @throws IOException If an I/O error occurs while writing.
     */
    void writeExternal(@NotNull DataOutput out) throws IOException;

    /**
     * Reads the object's state from a {@link DataInput}.
     *
     * @param in The {@link DataInput} to read the object's state from.
     *
     * @throws IOException If an I/O error occurs while reading.
     */
    void readExternal(@NotNull DataInput in) throws IOException;
}
