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

import io.github.libfp.ISerializable;
import io.github.libfp.profile.manager.ProfileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.util.SupplierUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The {@code Blueprint} class represents a factory for creating instances of
 * {@link ExtensibleProfile} or its subclasses. It allows defining a blueprint
 * by specifying the fields to be included in the created profiles along with
 * their corresponding data types or factory methods.
 *
 * <p>
 * The blueprint is used to generate profiles with predefined fields during the
 * deserialization process. The created profiles can be managed by a
 * {@link ProfileManager}.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>{@code
 * // Create a blueprint for a specific ExtensibleProfile subclass
 * Blueprint<MyExtensibleProfile> blueprint =
 *      Blueprint.make(MyExtensibleProfile::new)
 *               .add("fieldName1", Constants.Numeric::new)
 *               .add("fieldName2", Constants.PrimitiveArray.ints());
 *
 * // Serialize the blueprint to a DataOutput stream
 * DataOutput out = // ...;
 * MyExtensibleProfile p0 = // ...;
 * blueprint.write(p0, out); // or p0.writeExternal(out);
 *
 * // Deserialize the blueprint from a DataInput stream
 * DataInput in = // ...;
 * ProfileManager manager = // ...;
 * MyExtensibleProfile p = blueprint.readExternal(in, manager);
 *
 * // Use the blueprint to create a new profile instance
 * MyExtensibleProfile profile = blueprint.newInstance(profileManager);
 * }</pre>
 *
 * @param <T> The type of the {@code ExtensibleProfile} or its subclass for
 *            which the blueprint is created.
 */
public class Blueprint<T extends ExtensibleProfile>
        implements IManagedProfileFactory<T>
{

    private final Map<String, Supplier<? extends ISerializable>> fields;
    private final IManagedProfileFactory<T> delegate;

    private Blueprint(IManagedProfileFactory<T> factory)
    {
        this.delegate = factory;
        this.fields = new HashMap<>();
    }

    public static <T extends ISerializable> @Nullable T getValue(
            @NotNull ExtensibleProfile profile,
            @NotNull String key)
    {
        //noinspection unchecked
        return (T) profile.get(key);
    }

    /**
     * Creates a new instance of {@code Blueprint} for the specified
     * {@link IManagedProfileFactory}.
     *
     * @param factory The factory for creating instances of
     *                {@code ExtensibleProfile} or its subclass.
     * @param <E>     The type of the {@code ExtensibleProfile} or its
     *                subclass.
     * @return A new instance of {@code Blueprint}.
     */
    public static <E extends ExtensibleProfile> Blueprint<E> make(
            @NotNull IManagedProfileFactory<E> factory)
    {
        return new Blueprint<>(factory);
    }

    public static <T extends ISerializable> @NotNull T getOrThrow(
            @NotNull ExtensibleProfile profile,
            String key) throws IllegalArgumentException
    {
        ISerializable value = profile.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No such key: " + key);
        }
        //noinspection unchecked
        return (T) value;
    }

    /**
     * Adds a field to the blueprint with the specified key and data type.
     *
     * @param key           The key of the field.
     * @param templateClass The class representing the data type of the field.
     * @return This {@code Blueprint} instance for method chaining.
     */
    public Blueprint<T> add(
            String key,
            Class<? extends ISerializable> templateClass)
    {
        fields.put(key, SupplierUtil.createSupplier(templateClass));
        return this;
    }

    /**
     * Adds a field to the blueprint with the specified key and factory method.
     *
     * @param key     The key of the field.
     * @param factory The factory method for creating instances of the field
     *                type.
     * @return This {@code Blueprint} instance for method chaining.
     */
    public Blueprint<T> add(
            String key,
            Supplier<? extends ISerializable> factory)
    {
        fields.put(key, factory);
        return this;
    }

    /**
     * Creates a new instance of {@code ExtensibleProfile} or its subclass using
     * the provided {@link ProfileManager}.
     *
     * @param manager The {@code ProfileManager} used to manage the created
     *                profile.
     * @return A new instance of {@code ExtensibleProfile} or its subclass.
     */
    @Override
    public T newInstance(ProfileManager manager)
    {
        return delegate.newInstance(manager);
    }

    ///////////////////////////////////////////////////////////////////////////
    // I/O
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Writes the blueprint to the specified {@link DataOutput} stream.
     *
     * @param obj The profile object associated with this blueprint.
     * @param out The {@code DataOutput} stream to write to.
     * @throws IOException If an I/O error occurs.
     */
    public void write(T obj, DataOutput out) throws IOException
    {
        for (String key : fields.keySet()) {
            ISerializable value = obj.get(key);
            if (value == null) {
                throw new IllegalStateException(key + " is null!");
            }
            value.writeExternal(out);
        }
    }

    /**
     * Reads the blueprint from the specified {@link DataInput} stream using the
     * provided {@link ProfileManager}.
     *
     * @param in      The {@code DataInput} stream to read from.
     * @param manager The {@code ProfileManager} used to manage the created
     *                profile.
     * @return The created profile instance.
     * @throws IOException If an I/O error occurs.
     */
    public T read(DataInput in, ProfileManager manager) throws IOException
    {
        T instance = newInstance(manager);
        read(instance, in);
        return instance;
    }

    /**
     * Reads the values of the fields from the specified {@link DataInput}
     * stream and sets them on the provided profile instance.
     *
     * @param instance The profile instance to set the field values on.
     * @param in       The {@code DataInput} stream to read from.
     * @throws IOException If an I/O error occurs.
     */
    public void read(T instance, DataInput in) throws IOException
    {
        for (String key : fields.keySet()) {
            ISerializable value = fields.get(key).get();

            value.readExternal(in);
            instance.put(key, value);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // misc
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a string representation of this blueprint.
     *
     * @return A string representation of the blueprint.
     */
    @Override
    public String toString()
    {
        return "Blueprint{" +
                "fields=" + fields +
                '}';
    }
}
