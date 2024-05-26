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
package io.github.libfp.profile;

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
