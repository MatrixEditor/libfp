package io.github.libfp.ast; //@date 31.10.2023

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
     *
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
     *
     * @throws IOException If an I/O error occurs while writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        VarInt.write(parent, out);
    }
}
