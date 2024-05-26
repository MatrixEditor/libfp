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
package io.github.libfp.profile.features;

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * The {@code FeatureVector} class represents a vector of features, which can be
 * used to represent and store feature values for various inputs. It implements
 * the {@link ISerializable} interface for serialization and deserialization of
 * feature vectors.
 */
public class FeatureVector
        implements ISerializable
{

    public int[] values;

    /**
     * Constructs an empty {@code FeatureVector}.
     */
    public FeatureVector(final int length)
    {
        this.values = new int[length];
    }

    /**
     * Constructs a feature vector based on the given array of feature
     * specifications and an input object.
     *
     * @param <T>          The type of objects from which features are
     *                     extracted.
     * @param featureSpecs An array of feature specifications.
     * @param input        The input object from which features are extracted.
     * @throws IndexOutOfBoundsException If an index is out of bounds while
     *                                   setting feature values.
     */
    public <T> FeatureVector(
            final IFeatureSpec<T> @NotNull [] featureSpecs,
            final @NotNull T input) throws IndexOutOfBoundsException
    {
        this(featureSpecs, input, featureSpecs.length);
    }

    /**
     * Constructs a feature vector with a specified maximum length based on the
     * given array of feature specifications and an input object.
     *
     * @param <T>          The type of objects from which features are
     *                     extracted.
     * @param featureSpecs An array of feature specifications.
     * @param input        The input object from which features are extracted.
     * @param maxLength    The maximum length of the feature vector.
     * @throws IndexOutOfBoundsException If an index is out of bounds while
     *                                   setting feature values.
     */
    public <T> FeatureVector(
            final IFeatureSpec<T> @NotNull [] featureSpecs,
            final @NotNull T input,
            final int maxLength) throws IndexOutOfBoundsException
    {
        this(maxLength);
        for (final IFeatureSpec<T> spec : featureSpecs) {
            set(spec, input);
        }
    }

    /**
     * Constructs a feature vector from the data read from a {@link DataInput}.
     *
     * @param in The {@link DataInput} from which to read the feature vector
     *           data.
     * @throws IOException If an I/O error occurs during data reading.
     */
    public FeatureVector(@NotNull DataInput in) throws IOException
    {
        readExternal(in);
    }

    public FeatureVector()
    {
        this(0);
    }

    /**
     * Constructs a feature vector with the given array of feature values.
     *
     * @param values An array of feature values.
     */
    public FeatureVector(final int[] values)
    {
        this.values = values;
    }

    /**
     * Gets the feature value for the specified feature specification.
     *
     * @param <T>  The type of objects from which features are extracted.
     * @param spec The feature specification.
     * @return The feature value associated with the feature specification.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public <T> int get(@NotNull IFeatureSpec<T> spec)
            throws IndexOutOfBoundsException
    {
        return get(spec.index());
    }

    /**
     * Gets the feature value at the specified index.
     *
     * @param index The index of the feature value.
     * @return The feature value at the given index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public int get(final int index) throws IndexOutOfBoundsException
    {
        Objects.checkIndex(index, values.length);
        return values[index];
    }

    /**
     * Checks if the feature vector is empty, meaning all feature values are
     * zero.
     *
     * @return {@code true} if the feature vector is empty, {@code false}
     *         otherwise.
     */
    public boolean isEmpty()
    {
        return IntStream.range(0, values.length).allMatch(i -> values[i] == 0);
    }

    /**
     * Gets the size (length) of the feature vector.
     *
     * @return The size (length) of the feature vector.
     */
    public int size()
    {
        return values.length;
    }

    /**
     * Sets the feature value for the specified feature specification.
     *
     * @param <T>   The type of objects from which features are extracted.
     * @param spec  The feature specification.
     * @param input The input object from which to calculate the feature value.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public <T> void set(
            final @NotNull IFeatureSpec<T> spec,
            final @NotNull T input
    ) throws IndexOutOfBoundsException
    {
        set(spec.index(), spec.hasFeature(input));
    }

    /**
     * Sets the feature value at the specified index.
     *
     * @param index The index at which to set the feature value.
     * @param value The feature value to set.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public void set(final int index, final int value)
            throws IndexOutOfBoundsException
    {
        Objects.checkIndex(index, size());
        values[index] = value;
    }

    public void inc(final int index) throws IndexOutOfBoundsException
    {
        set(index, get(index) + 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // I/O
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc} Writes the feature vector data to the specified
     * {@link DataOutput}.
     *
     * @param out The {@link DataOutput} to which the feature vector data is
     *            written.
     * @throws IOException If an I/O error occurs during data writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        final boolean empty = isEmpty();
        out.writeBoolean(empty);
        VarInt.write(values.length, out);

        if (!empty) {
            for (final int feature : values) {
                VarInt.write(feature, out);
            }
        }
    }

    /**
     * {@inheritDoc} Reads the feature vector data from the specified
     * {@link DataInput}.
     *
     * @param in The {@link DataInput} from which to read the feature vector
     *           data.
     * @throws IOException If an I/O error occurs during data reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final boolean empty = in.readBoolean();
        final int length = (int) VarInt.read(in);

        values = new int[length];
        if (!empty) {
            for (int i = 0; i < length; i++) {
                values[i] = (int) VarInt.read(in);
            }
        }
    }
}
