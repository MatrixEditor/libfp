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

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.VarInt;
import io.github.libfp.util.IO;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

@ApiStatus.Experimental
public interface IClassRelationshipContainer
{

    @NotNull Map<ClassRelation, Set<Integer>> getRelationships();

    default @NotNull Set<Integer> getCallRefs()
    {
        return getRelationships().getOrDefault(ClassRelation.CALL,
                Collections.emptySet());
    }

    default @NotNull Set<Integer> getTypeRefs()
    {
        return getRelationships().getOrDefault(ClassRelation.REFERENCE,
                Collections.emptySet());
    }

    default @NotNull Set<Integer> getInheritanceRefs()
    {
        return getRelationships().getOrDefault(ClassRelation.INHERITANCE,
                Collections.emptySet());
    }

    default void addRef(@NotNull ClassRelation relation, IClass iClass)
    {
        addRef(relation, 0);
    }

    default void addRef(@NotNull ClassRelation relation, int index)
    {
        getRelationships().computeIfAbsent(relation, r -> new HashSet<>())
                .add(index);
    }

    ///////////////////////////////////////////////////////////////////////////
    // I/O
    ///////////////////////////////////////////////////////////////////////////
    default void writeRelationships(@NotNull DataOutput out) throws IOException
    {
        var relationships = getRelationships();

        VarInt.write(relationships.size(), out);
        for (final ClassRelation relation : relationships.keySet()) {
            IO.writeEnum(relation, out);

            final Set<Integer> refs = relationships.get(relation);
            VarInt.write(refs.size(), out);
            for (final int index : refs) {
                VarInt.write(index, out);
            }
        }
    }

    default @NotNull Map<ClassRelation, Set<Integer>> readRelationships(
            @NotNull DataInput in) throws IOException
    {
        final int size = (int) VarInt.read(in);
        var relationships = new HashMap<ClassRelation, Set<Integer>>(size);
        for (int i = 0; i < size; i++) {
            ClassRelation relation = IO.readEnum(ClassRelation.values(), in);

            final int length = (int) VarInt.read(in);
            Set<Integer> refs = new HashSet<>(length);
            for (int j = 0; j < length; j++) {
                refs.add((int) VarInt.read(in));
            }
            relationships.put(relation, refs);
        }
        return relationships;
    }
}
