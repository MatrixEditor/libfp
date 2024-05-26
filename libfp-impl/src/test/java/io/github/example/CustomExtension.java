package io.github.example;  

import io.github.libfp.profile.manager.IExtension;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CustomExtension extends IExtension
{
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeInt(123);
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        assert in.readInt() == 123;
    }

    @Override
    public void reset()
    {
        // remove all resources linked to this extension
    }

}
