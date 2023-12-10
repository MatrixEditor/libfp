package io.github.libfp.profile;//@date 15.11.2023

import java.util.function.Predicate;

/**
 * The {@code ProfilePolicy} record represents a policy for determining the
 * validity of a given context type.
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>{@code
 * // Create a ProfilePolicy for a specific context type and executor predicate
 * ProfilePolicy<MyContextType> policy = new ProfilePolicy<>(MyContextType.class, context -> {
 *     // Define the executor predicate logic
 *     // ...
 *     return isValid;
 * });
 *
 * // Check if a class is assignable from the specified context type
 * boolean isAssignable = policy.isContextClass(SomeClass.class);
 *
 * // Check if a context satisfies the executor predicate
 * MyContextType context = //...
 * boolean isValidContext = policy.isValid(context);
 *
 * // Create a new policy by combining with another policy using logical AND
 * ProfilePolicy<MyContextType> combinedPolicy = policy.and(anotherPolicy);
 *
 * // Create a new policy by combining with a predicate using logical AND
 * ProfilePolicy<MyContextType> combinedWithPredicate = policy.and(somePredicate);
 *
 * // Create a new policy by combining with another policy using logical OR
 * ProfilePolicy<MyContextType> orCombinedPolicy = policy.or(anotherPolicy);
 *
 * // Create a new policy by combining with a predicate using logical OR
 * ProfilePolicy<MyContextType> orCombinedWithPredicate = policy.or(somePredicate);
 *
 * // Create a new policy by negating the executor predicate
 * ProfilePolicy<MyContextType> negatedPolicy = policy.negate();
 * }</pre>
 *
 * @param <C> The type of the context.
 */
public record ProfilePolicy<C>(
        Class<? extends C> contextType,
        Predicate<C> executor)
{

    /**
     * Checks if the provided class is assignable from the context type
     * specified in the policy.
     *
     * @param c The class to check.
     *
     * @return {@code true} if the provided class is assignable from the context
     *         type.
     */
    public boolean isContextClass(Class<?> c)
    {
        return c.isAssignableFrom(contextType);
    }

    /**
     * Checks if the provided context satisfies the executor predicate specified
     * in the policy.
     *
     * @param c The context to check.
     *
     * @return {@code true} if the provided context satisfies the executor
     *         predicate.
     */
    public boolean isValid(C c)
    {
        return executor.test(c);
    }

    /**
     * Combines the current policy with another policy using logical AND.
     *
     * @param next The next policy to combine with.
     *
     * @return A new policy representing the logical AND combination of the two
     *         policies.
     */
    public ProfilePolicy<C> and(ProfilePolicy<C> next)
    {
        return and(next.executor());
    }

    /**
     * Combines the current policy with a predicate using logical AND.
     *
     * @param predicate The predicate to combine with.
     *
     * @return A new policy representing the logical AND combination of the
     *         current policy and the provided predicate.
     */
    public ProfilePolicy<C> and(Predicate<? super C> predicate)
    {
        return new ProfilePolicy<>(contextType, executor.and(predicate));
    }

    /**
     * Combines the current policy with another policy using logical OR.
     *
     * @param next The next policy to combine with.
     *
     * @return A new policy representing the logical OR combination of the two
     *         policies.
     */
    public ProfilePolicy<C> or(ProfilePolicy<C> next)
    {
        return or(next.executor());
    }

    /**
     * Combines the current policy with a predicate using logical OR.
     *
     * @param predicate The predicate to combine with.
     *
     * @return A new policy representing the logical OR combination of the
     *         current policy and the provided predicate.
     */
    public ProfilePolicy<C> or(Predicate<? super C> predicate)
    {
        return new ProfilePolicy<>(contextType, executor.or(predicate));
    }

    /**
     * Negates the executor predicate of the current policy.
     *
     * @return A new policy with the executor negated.
     */
    public ProfilePolicy<C> negate()
    {
        return new ProfilePolicy<>(contextType, executor.negate());
    }
}
