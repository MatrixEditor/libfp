package io.github.libfp.hash; //@date 24.10.2023

import com.trendmicro.tlsh.Tlsh;
import com.trendmicro.tlsh.TlshCreator;
import io.github.libfp.ISerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TrendMicroLSH implements ISerializable
{

    public Tlsh tlsh;
    public @Nullable TlshCreator creator;

    public TrendMicroLSH()
    {
        creator = new TlshCreator();
    }

    public TrendMicroLSH(@NotNull DataInput input) throws IOException
    {
        this();
        readExternal(input);
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int length = in.readUnsignedShort();
        if (length > 0) {
            byte[] bytes = new byte[length];

            in.readFully(bytes);
            tlsh = Tlsh.fromTlshStr(new String(bytes));
        }
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        if (tlsh == null) {
            out.writeShort(0);
        } else {
            final String encoded = tlsh.getEncoded();
            out.writeShort(encoded.length());
            out.writeBytes(encoded);
        }
    }
}
