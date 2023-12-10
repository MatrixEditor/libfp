package io.github.libfp.impl.cra; //@date 11.11.2023

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
