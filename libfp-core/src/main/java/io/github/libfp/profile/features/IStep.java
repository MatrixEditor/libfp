package io.github.libfp.profile.features;//@date 19.10.2023

import io.github.libfp.profile.ManagedProfile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * The <code>IStep</code> interface defines a contract for processing steps that
 * operate on objects of type {@code <T>} and managed profiles of type
 * {@code <M>}.
 *
 * <p>
 * The {@link #test(Class)} method, inherited from {@link Predicate}, checks if
 * a given class is the target class for processing or is assignable to the
 * target class. The target class is defined by the
 * {@link #targetProfileClass()} method.
 * </p>
 *
 * @param <T> The type of the reference object to be processed.
 * @param <M> The type of the managed profile.
 */
public interface IStep<T, M extends ManagedProfile>
        extends Predicate<Class<? extends ManagedProfile>>
{

    /**
     * Tests whether a given class is applicable for processing.
     *
     * @param targetType The class to test for applicability.
     *
     * @return <code>true</code> if the class is the target class or assignable
     *         to it; otherwise, <code>false</code>.
     */
    @Override
    default boolean test(@NotNull Class<? extends ManagedProfile> targetType)
    {
        final Class<? extends ManagedProfile> target = targetProfileClass();
        return target == targetType || target.isAssignableFrom(targetType);
    }

    /**
     * Returns the class representing the target profile for processing.
     *
     * @return The class representing the target profile.
     */
    Class<? extends ManagedProfile> targetProfileClass();

    /**
     * Gets the priority of the processing step.
     *
     * @return The priority of the processing step.
     */
    default int priority()
    {
        return 0;
    }

    /**
     * Processes a reference object of type {@code <T>} and a managed profile of
     * type {@code <M>}.
     *
     * @param ref    The reference object to be processed.
     * @param target The managed profile on which the processing is performed.
     */
    void process(T ref, M target);
}
