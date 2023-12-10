package io.github.libfp.profile.features;//@date 05.11.2023

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

    /**
     * Checks the presence of a specific feature in the provided object
     * {@code t}.
     *
     * @param t The object to check for the presence of a feature.
     *
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
}

