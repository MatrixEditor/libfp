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

import org.jetbrains.annotations.NotNull;

/**
 * The {@code IFeatureSpec} interface represents a specification for extracting
 * and indexing features from objects of type {@code T}. Implementing classes
 * must provide methods for checking the presence of a feature and returning its
 * index.
 *
 * @param <T> The type of objects from which features are extracted.
 */
public interface IFeatureSpec<T>
{
    /**
     * Checks the presence of a specific feature in the provided object
     * {@code t}.
     *
     * @param t The object to check for the presence of a feature.
     * @return An integer representing the presence of the feature (e.g., 1 for
     *         present, 0 for absent, or any other custom value).
     */
    int hasFeature(@NotNull T t);

    /**
     * Retrieves the index associated with this feature specification.
     *
     * @return The index of the feature specified by this object.
     */
    int index();

    /**
     * The {@code OfEnum} interface represents a feature specification
     * specifically designed to be used with enums. It extends the
     * {@link IFeatureSpec} interface and provides default methods for feature
     * indexing based on enum ordinal values.
     *
     * @param <T> The type of objects from which features are extracted.
     * @param <E> The enum type to be used for feature indexing.
     */
    interface OfEnum<T, E extends Enum<E>>
            extends IFeatureSpec<T>
    {
        /**
         * {@inheritDoc} Retrieves the index associated with this feature
         * specification, which is based on the ordinal value of the enum
         * instance.
         *
         * @return The index of the feature specified by this object, based on
         *         the ordinal value of the enum instance.
         */
        @Override
        default int index()
        {
            //noinspection unchecked
            return ((E) this).ordinal();
        }
    }
}

