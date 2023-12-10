package io.github.libfp.profile; //@date 25.10.2023

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * The AbstractCorrespondences class is an abstract implementation of the
 * ICorrespondences interface. It represents a collection of correspondences
 * between a key element and a list of comparable possibilities.
 *
 * @param <T> The type of elements that are comparable.
 */
public abstract non-sealed class AbstractCorrespondences<T extends IComparable<T>>
        implements ICorrespondences<T>
{

    private final T key;
    protected List<T> matches;
    protected List<Double> similarities;

    /**
     * Constructs an AbstractCorrespondences instance with the specified key
     * element.
     *
     * @param key The key element for which correspondences are being
     *            determined.
     */
    public AbstractCorrespondences(T key)
    {
        this.key = key;
    }

    /**
     * Get the key element for which correspondences are being determined.
     *
     * @return The key element.
     */
    @Override
    public T key()
    {
        return key;
    }

    /**
     * Get the count of correspondences in the collection.
     *
     * @return The count of correspondences.
     */
    @Override
    public int count()
    {
        return matches.size();
    }

    /**
     * Get the similarity score at the specified index in the collection of
     * correspondences.
     *
     * @param index The index at which to retrieve the similarity score.
     *
     * @return The similarity score at the specified index.
     */
    @Override
    public double getSimilarityAt(int index)
    {
        return similarities.get(index);
    }

    /**
     * Get the matching element at the specified index in the collection of
     * correspondences.
     *
     * @param index The index at which to retrieve the matching element.
     *
     * @return The matching element at the specified index.
     */
    @Override
    public T getMatchAt(int index)
    {
        return matches.get(index);
    }

    /**
     * Get an iterator to iterate through the matching elements in the
     * collection.
     *
     * @return An iterator over the matching elements.
     */
    @Override
    public @NotNull Iterator<T> iterator()
    {
        return matches.iterator();
    }
}
