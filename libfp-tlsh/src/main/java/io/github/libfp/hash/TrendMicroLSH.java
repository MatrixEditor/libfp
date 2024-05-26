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

import com.trendmicro.tlsh.Tlsh;
import com.trendmicro.tlsh.TlshCreator;
import io.github.libfp.ISerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Wrapper around the Trend Micro Locality-Sensitive Hashing (TLSH) algorithm.
 *
 * @author MatrixEditor
 */
public class TrendMicroLSH implements ISerializable
{

    /**
     * Hash object
     */
    public @Nullable Tlsh tlsh;

    /**
     * Creator object
     */
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
