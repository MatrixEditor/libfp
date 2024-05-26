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
package io.github.libfp.impl.cra;

import io.github.libfp.cha.ExtendedClassProfile;
import io.github.libfp.profile.manager.ProfileManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiStatus.Experimental
public class CRClassProfile
        extends ExtendedClassProfile
        implements IClassRelationshipContainer
{

    private @NotNull Map<ClassRelation, Set<Integer>> relationships =
            new HashMap<>();


    public CRClassProfile(@NotNull ProfileManager manager)
    {
        super(manager);
    }

    @NotNull
    @Override
    public Map<ClassRelation, Set<Integer>> getRelationships()
    {
        return relationships;
    }

    ///////////////////////////////////////////////////////////////////////////
    // I/O
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        writeRelationships(out);
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        relationships = readRelationships(in);
    }
}
