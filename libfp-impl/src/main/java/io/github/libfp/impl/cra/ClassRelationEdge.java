package io.github.libfp.impl.cra; //@date 11.11.2023

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.graph.DefaultWeightedEdge;

@ApiStatus.Experimental
public class ClassRelationEdge
        extends DefaultWeightedEdge
{

    public @Nullable ClassRelation relation;
}
