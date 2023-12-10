package io.github.libfp.matching; //@date 31.10.2023

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The <code>IVertexFunction</code> interface represents a functional interface
 * for transforming elements of type <code>T</code> into sets of elements of
 * type
 * <code>R</code>. It defines a single method <code>getVertices</code> that
 * takes an input of type <code>T</code> and returns a set of vertices of type
 * <code>R</code>.
 *
 * @param <T> The type of elements to transform.
 * @param <R> The type of vertices in the resulting set.
 */
@FunctionalInterface
public interface IVertexFunction<T, R>
{

    /**
     * Creates an <code>IVertexFunction</code> based on a provided function that
     * maps elements of type <code>A</code> to an iterable of elements of type
     * <code>B</code>.
     *
     * @param <A>      The type of input elements.
     * @param <B>      The type of vertices.
     * @param function The function that maps elements of type <code>A</code> to
     *                 an iterable of elements of type <code>B</code.
     *
     * @return An <code>IVertexFunction</code> that transforms elements of type
     *         <code>A</code> into a set of vertices of type <code>B</code>.
     */
    static <A, B> IVertexFunction<A, B> toVertexFunction(
            final Function<A, Iterable<B>> function)
    {
        return toVertexFunction(function, HashSet::new);
    }

    /**
     * Creates an <code>IVertexFunction</code> based on a provided function that
     * maps elements of type <code>A</code> to an iterable of elements of type
     * <code>B</code> and a supplier for the set type.
     *
     * @param <A>      The type of input elements.
     * @param <B>      The type of vertices.
     * @param function The function that maps elements of type <code>A</code> to
     *                 an iterable of elements of type <code>B</code>.
     * @param factory  A supplier for creating sets of elements of type
     *                 <code>B</code>.
     *
     * @return An <code>IVertexFunction</code> that transforms elements of type
     *         <code>A</code> into a set of vertices of type <code>B</code>.
     */
    static <A, B> IVertexFunction<A, B> toVertexFunction(
            final Function<A, Iterable<B>> function,
            final Supplier<? extends Set<B>> factory)
    {
        return (t) -> {
            Set<B> vertices = factory.get();
            for (final B b : function.apply(t)) {
                vertices.add(b);
            }
            return vertices;
        };
    }

    /**
     * Transforms an input element of type <code>T</code> into a set of vertices
     * of type <code>R</code>.
     *
     * @param in The input element to transform.
     *
     * @return A set of vertices of type <code>R</code>.
     */
    Set<R> getVertices(T in);
}
