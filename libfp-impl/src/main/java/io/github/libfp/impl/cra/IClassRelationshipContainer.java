package io.github.libfp.impl.cra;//@date 12.11.2023

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
