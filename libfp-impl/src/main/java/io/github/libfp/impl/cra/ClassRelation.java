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

import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@ApiStatus.Experimental
public enum ClassRelation
{
    CALL,
    REFERENCE,
    INHERITANCE;

    public static <V extends CRClassProfile> @NotNull Set<V> resolveRefs(
            @NotNull V profile,
            @NotNull Set<Integer> refs)
    {
        ClassProfileList profiles =
                profile.getManager().getExtension(ClassProfileList.class);

        //noinspection unchecked
        return refs.stream()
                .map(profiles::get)
                .map(x -> (V) x)
                .collect(Collectors.toSet());
    }

    public static <V extends CRClassProfile>
    Graph<V, ClassRelationEdge> computeGraph(
            @NotNull Iterable<@NotNull V> profiles)
    {
        return computeGraph(profiles, ClassRelationEdge.class);
    }

    public static <V extends CRClassProfile, E extends ClassRelationEdge>
    @NotNull Graph<V, E> computeGraph(
            @NotNull Iterable<@NotNull V> profiles, Class<E> edgeType)
    {
        Graph<V, E> graph = new WeightedMultigraph<>(edgeType);
        return fillGraph(profiles.iterator(), graph);
    }

    public static <U extends ClassProfile, V extends CRClassProfile>
    Graph<V, ClassRelationEdge> computeGraph(
            @NotNull ProfileList<U> profileList,
            @NotNull Class<V> profileType)
    {
        return computeGraph(profileList, profileType, ClassRelationEdge.class);
    }

    public static <U extends ClassProfile, V extends CRClassProfile,
            E extends ClassRelationEdge>
    @NotNull Graph<V, E> computeGraph(
            @NotNull ProfileList<U> profileList,
            @NotNull Class<V> profileType,
            @NotNull Class<E> edgeType)
    {

        Graph<V, E> graph = new WeightedMultigraph<>(edgeType);
        Iterator<V> iterator = profileList.asIterator(profileType);

        return fillGraph(iterator, graph);
    }

    private static <V extends CRClassProfile, E extends ClassRelationEdge> Graph<V, E> fillGraph(
            @NotNull Iterator<V> iterator,
            @NotNull Graph<V, E> graph)
    {
        while (iterator.hasNext()) {
            V profile = iterator.next();
            graph.addVertex(profile);

            for (ClassRelation relation : ClassRelation.values()) {
                Set<Integer> indices = profile
                        .getRelationships()
                        .getOrDefault(relation, new HashSet<>());

                relation.addRelation(profile, indices, graph);
            }
        }
        return graph;
    }

    private <V extends CRClassProfile, E extends ClassRelationEdge>
    void addRelation(
            @NotNull V profile,
            @NotNull Set<Integer> profile1,
            @NotNull Graph<V, E> graph)
    {
        resolveRefs(profile, profile1).forEach(v -> {
            graph.addVertex(v);
            graph.addEdge(profile, v).relation = this;
        });
    }
}
