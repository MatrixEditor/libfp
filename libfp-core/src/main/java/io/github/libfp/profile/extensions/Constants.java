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
package io.github.libfp.profile.extensions; //

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import io.github.libfp.profile.manager.IExtension;
import io.github.libfp.util.IO;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

/**
 * The Constants class is used to store and manage various constant values
 * associated with their corresponding keys.
 */
public final class Constants extends IExtension
{

    /**
     * A map that associates the hash of a class type with a factory for
     * creating instances of that type.
     */
    public static final Map<Integer, Supplier<ISerializable>> types =
            new HashMap<>();

    // Initialize default types
    static {
        registerType(Numeric.class, Numeric::new);
        registerType(Literal.class, Literal::new);
    }

    /**
     * A map that stores the constants along with their keys.
     */
    private @NotNull Map<Integer, ISerializable> constants = new HashMap<>();

    /**
     * Registers a class type and its associated factory for creating
     * instances.
     *
     * @param type    The class type to be registered.
     * @param factory A factory for creating instances of the class type.
     * @param <T>     The type of IExtension.
     */
    public static <T extends ISerializable> void registerType(
            final @NotNull Class<T> type,
            final Supplier<ISerializable> factory
    )
    {
        types.put(TypeNames.hash(type), factory);
    }

    /**
     * The Literal class represents a constant value of type String.
     */
    public static final class Literal implements ISerializable
    {
        public String value;

        public Literal()
        {
        }

        public Literal(String value)
        {
            this.value = value;
        }

        @Override
        public void readExternal(@NotNull DataInput in) throws IOException
        {
            value = IO.readString(in);
        }

        @Override
        public void writeExternal(@NotNull DataOutput out) throws IOException
        {
            IO.writeString(value, out);
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    /**
     * The Numeric class represents a constant value of numeric types.
     */
    public static final class Numeric implements ISerializable
    {
        public Number value;

        boolean writeHash = true;
        int templateHash = -1;

        public Numeric()
        {
        }

        public Numeric(Number value)
        {
            this.value = value;
        }

        @Override
        public void writeExternal(@NotNull DataOutput out) throws IOException
        {
            if (writeHash) {
                out.writeInt(TypeNames.hash(value.getClass()));
            }

            if (value instanceof Integer) {
                out.writeInt(value.intValue());
            } else if (value instanceof Long) {
                out.writeLong(value.longValue());
            } else if (value instanceof Short) {
                out.writeShort(value.shortValue());
            } else if (value instanceof Byte) {
                out.writeByte(value.byteValue());
            } else if (value instanceof Double) {
                out.writeDouble(value.doubleValue());
            } else if (value instanceof Float) {
                out.writeFloat(value.floatValue());
            }
        }

        @Override
        public void readExternal(@NotNull DataInput in) throws IOException
        {
            int hash = writeHash ? in.readInt() : templateHash;

            if (hash == TypeNames.hash(Integer.class)) {
                value = in.readInt();
            } else if (hash == TypeNames.hash(Long.class)) {
                value = in.readLong();
            } else if (hash == TypeNames.hash(Short.class)) {
                value = in.readShort();
            } else if (hash == TypeNames.hash(Byte.class)) {
                value = in.readByte();
            } else if (hash == TypeNames.hash(Double.class)) {
                value = in.readDouble();
            } else if (hash == TypeNames.hash(Float.class)) {
                value = in.readFloat();
            }
        }
    }

    /**
     * The {@code PrimitiveArray} class represents a serializable wrapper for
     * primitive arrays, providing a convenient way to handle different types of
     * primitive arrays, such as int[], long[], short[], byte[], double[], and
     * float[].
     *
     * <p>
     * This class allows setting the length of the array, accessing elements by
     * index, and serializing/deserializing the array along with its type.
     * </p>
     *
     * <p>
     * Usage example:
     * </p>
     *
     * <pre>{@code
     * // Create a new PrimitiveArray for int
     * PrimitiveArray intArray = PrimitiveArray.ints().get();
     *
     * // Set the length of the array
     * intArray.setLength(5);
     *
     * // Set values at specific indices
     * intArray.set(0, 42);
     * intArray.set(1, 24);
     *
     * // Get the length of the array
     * int length = intArray.length();
     *
     * // Access values by index
     * int valueAtIndex = intArray.at(1).intValue();
     *
     * // Serialize the array to a DataOutput stream
     * DataOutput out = //...;
     * intArray.writeExternal(out);
     *
     * // Deserialize the array from a DataInput stream
     * DataInput in = //...;
     * PrimitiveArray deserializedArray = new PrimitiveArray(int.class);
     * deserializedArray.readExternal(in);
     * }</pre>
     */
    public static final class PrimitiveArray implements ISerializable
    {
        private final int INT_ARRAY = TypeNames.hash(int[].class);
        private final int LONG_ARRAY = TypeNames.hash(long[].class);
        private final int SHORT_ARRAY = TypeNames.hash(short[].class);
        private final int BYTE_ARRAY = TypeNames.hash(byte[].class);
        private final int FLOAT_ARRAY = TypeNames.hash(float[].class);
        private final int DOUBLE_ARRAY = TypeNames.hash(double[].class);

        /**
         * The underlying array object.
         */
        public Object array;

        /**
         * The component type of the array.
         */
        public Class<?> componentType;

        /**
         * Constructs a new {@code PrimitiveArray} instance for the specified
         * component type.
         *
         * @param componentType The component type of the primitive array.
         */
        public PrimitiveArray(Class<?> componentType)
        {
            this.componentType = componentType;
        }

        public PrimitiveArray(Object array)
        {
            this.array = array;
            this.componentType = array.getClass().getComponentType();
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for the specified component type.
         *
         * @param componentType The component type of the primitive array.
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray}.
         */
        public static Supplier<PrimitiveArray> asFactory(Class<?> componentType)
        {
            return () -> new PrimitiveArray(componentType);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for int arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for int arrays.
         */
        public static Supplier<PrimitiveArray> ints()
        {
            return asFactory(int.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for byte arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for byte arrays.
         */
        public static Supplier<PrimitiveArray> bytes()
        {
            return asFactory(byte.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for long arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for long arrays.
         */
        public static Supplier<PrimitiveArray> longs()
        {
            return asFactory(long.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for double arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for double arrays.
         */
        public static Supplier<PrimitiveArray> doubles()
        {
            return asFactory(double.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for float arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for float arrays.
         */
        public static Supplier<PrimitiveArray> floats()
        {
            return asFactory(float.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveArray} for short arrays.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveArray} for short arrays.
         */
        public static Supplier<PrimitiveArray> shorts()
        {
            return asFactory(short.class);
        }

        /**
         * Sets the length of the array.
         *
         * @param newLength The new length of the array.
         */
        public void setLength(int newLength)
        {
            array = Array.newInstance(componentType, newLength);
        }

        /**
         * Returns the length of the array.
         *
         * @return The length of the array.
         */
        public int length()
        {
            return Array.getLength(array);
        }

        /**
         * Retrieves the element at the specified index.
         *
         * @param index The index of the element to retrieve.
         * @return The element at the specified index.
         */
        public Number at(final int index)
        {
            return (Number) Array.get(array, index);
        }

        /**
         * Sets the value at the specified index.
         *
         * @param index The index at which to set the value.
         * @param value The value to set at the specified index.
         */
        public void set(final int index, final Number value)
        {
            Array.set(array, index, value);
        }

        /**
         * Writes the array and its type to the specified {@code DataOutput}
         * stream.
         *
         * @param out The {@code DataOutput} stream to write to.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void writeExternal(@NotNull DataOutput out) throws IOException
        {
            int hash = TypeNames.hash(array.getClass());
            out.writeInt(hash);

            final int length = length();
            VarInt.write(length, out);
            for (int i = 0; i < length; i++) {
                Number value = at(i);
                if (hash == INT_ARRAY) {
                    VarInt.write(value.intValue(), out);
                } else if (hash == LONG_ARRAY) {
                    VarInt.write(value.longValue(), out);
                } else if (hash == SHORT_ARRAY) {
                    out.writeShort(value.shortValue());
                } else if (hash == BYTE_ARRAY) {
                    out.writeByte(value.byteValue());
                } else if (hash == DOUBLE_ARRAY) {
                    out.writeDouble(value.doubleValue());
                } else if (hash == FLOAT_ARRAY) {
                    out.writeFloat(value.floatValue());
                }
            }
        }

        /**
         * Reads the array and its type from the specified {@code DataInput}
         * stream.
         *
         * @param in The {@code DataInput} stream to read from.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void readExternal(@NotNull DataInput in) throws IOException
        {
            int hash = in.readInt();

            final int length = (int) VarInt.read(in);
            array = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (hash == INT_ARRAY) {
                    set(i, (int) VarInt.read(in));
                } else if (hash == LONG_ARRAY) {
                    set(i, VarInt.read(in));
                } else if (hash == SHORT_ARRAY) {
                    set(i, in.readShort());
                } else if (hash == BYTE_ARRAY) {
                    set(i, in.readByte());
                } else if (hash == DOUBLE_ARRAY) {
                    set(i, in.readDouble());
                } else if (hash == FLOAT_ARRAY) {
                    set(i, in.readFloat());
                }
            }
        }
    }

    /**
     * The {@code ValueList} class represents a serializable wrapper for lists
     * of elements implementing the {@code ISerializable} interface. It provides
     * convenient ways to create and manage lists, including serialization and
     * deserialization support.
     *
     * <p>
     * Usage example:
     * </p>
     *
     * <pre>{@code
     * // Create a new ValueList for elements implementing ISerializable
     * ValueList<MySerializableElement> myList = new ValueList<>(MySerializableElement::new);
     *
     * // Add elements to the list
     * MySerializableElement e = myList.add();
     * myList.add(new MySerializableElement());
     *
     * // Get the size of the list
     * int size = myList.size();
     *
     * // Serialize the list to a DataOutput stream
     * DataOutput out = //...;
     * myList.writeExternal(out);
     *
     * // Deserialize the list from a DataInput stream
     * DataInput in = //...;
     * ValueList<MySerializableElement> deserializedList = new ValueList<>(MySerializableElement::new);
     * deserializedList.readExternal(in);
     * }</pre>
     *
     * @param <T> The type of elements in the list, extending
     *            {@code ISerializable}.
     */
    public static final class ValueList<T extends ISerializable>
            extends AbstractCollection<T> implements ISerializable
    {

        private final Supplier<T> factory;
        private List<T> delegate;

        /**
         * Constructs a new {@code ValueList} instance using the provided
         * {@code Supplier} for creating elements and a default
         * {@code LinkedList} as the underlying list implementation.
         *
         * @param factory The {@code Supplier} for creating elements.
         */
        public ValueList(Supplier<T> factory)
        {
            this(factory, LinkedList::new);
        }

        /**
         * Constructs a new {@code ValueList} instance using the provided
         * {@code Supplier} for creating elements and a custom supplier for the
         * underlying list implementation.
         *
         * @param factory         The {@code Supplier} for creating elements.
         * @param delegateFactory The {@code Supplier} for creating the
         *                        underlying list.
         */
        private ValueList(
                Supplier<T> factory,
                @NotNull Supplier<List<T>> delegateFactory)
        {
            this.factory = factory;
            this.delegate = delegateFactory.get();
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code ValueList}.
         *
         * @param valueFactory The {@code Supplier} for creating elements.
         * @param <E>          The type of elements extending
         *                     {@code ISerializable}.
         * @return A supplier function for creating instances of
         *         {@code ValueList}.
         */
        public static <E extends ISerializable> Supplier<ValueList<E>> asFactory(
                @NotNull Supplier<E> valueFactory)
        {
            return asFactory(valueFactory, LinkedList::new);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code ValueList} with a custom supplier for the underlying list
         * implementation.
         *
         * @param valueFactory    The {@code Supplier} for creating elements.
         * @param delegateFactory The {@code Supplier} for creating the
         *                        underlying list.
         * @param <E>             The type of elements extending
         *                        {@code ISerializable}.
         * @return A supplier function for creating instances of
         *         {@code ValueList}.
         */
        public static <E extends ISerializable> Supplier<ValueList<E>> asFactory(
                @NotNull Supplier<E> valueFactory,
                @NotNull Supplier<List<E>> delegateFactory)
        {
            return () -> new ValueList<>(valueFactory, delegateFactory);
        }

        public T add()
        {
            T t = factory.get();
            add(t);
            return t;
        }

        @Override
        public boolean add(T t)
        {
            return delegate.add(t);
        }

        /**
         * Writes the list size and elements to the specified {@code DataOutput}
         * stream.
         *
         * @param out The {@code DataOutput} stream to write to.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void writeExternal(@NotNull DataOutput out) throws IOException
        {
            VarInt.write(delegate.size(), out);
            IO.writeList(delegate, out);
        }

        /**
         * Reads the list size and elements from the specified {@code DataInput}
         * stream.
         *
         * @param in The {@code DataInput} stream to read from.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void readExternal(@NotNull DataInput in) throws IOException
        {
            final int size = (int) VarInt.read(in);
            delegate = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                T element = factory.get();
                element.readExternal(in);
                delegate.add(element);
            }
        }

        /**
         * Returns an iterator over the elements in this list.
         *
         * @return An iterator over the elements in this list.
         */
        @Override
        public Iterator<T> iterator()
        {
            return delegate.listIterator();
        }

        /**
         * Returns the number of elements in this list.
         *
         * @return The number of elements in this list.
         */
        @Override
        public int size()
        {
            return delegate.size();
        }
    }

    /**
     * The {@code PrimitiveList} class represents a serializable wrapper for
     * lists of {@code Number} objects, providing a convenient way to handle
     * different types of primitive numbers, such as int, long, short, byte,
     * double, and float.
     *
     * <p>
     * This class allows setting the type of numbers it stores, accessing
     * elements by index, and serializing/deserializing the list along with its
     * type.
     * </p>
     *
     * <p>
     * Usage example:
     * </p>
     *
     * <pre>{@code
     * // Create a new PrimitiveList for int
     * PrimitiveList intList = new PrimitiveList(Integer.class);
     *
     * // Add numbers to the list
     * intList.add(42);
     * intList.add(24.1); // 24 will be written
     *
     * // Get the size of the list
     * int size = intList.size();
     *
     * // Serialize the list to a DataOutput stream
     * DataOutput out = //...;
     * intList.writeExternal(out);
     *
     * // Deserialize the list from a DataInput stream
     * DataInput in = //...;
     * PrimitiveList deserializedList = new PrimitiveList(Integer.class);
     * deserializedList.readExternal(in);
     * }</pre>
     *
     * @implNote If you add double values to an integer list, their
     *         integer value will be used within serialization.
     */
    public static final class PrimitiveList extends AbstractCollection<Number>
            implements ISerializable
    {

        private final Numeric template;
        private List<Number> numbers;

        /**
         * Constructs a new {@code PrimitiveList} instance for the specified
         * number type.
         *
         * @param type The class object representing the number type.
         */
        public PrimitiveList(Class<?> type)
        {
            template = new Numeric();
            template.templateHash = TypeNames.hash(type);
            template.writeHash = false;
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for integers.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> ints()
        {
            return asFactory(Integer.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for bytes.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> bytes()
        {
            return asFactory(Byte.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for short values.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> shorts()
        {
            return asFactory(Short.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for {@code long} values.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> longs()
        {
            return asFactory(Long.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for values of type float.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> floats()
        {
            return asFactory(Float.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for values of type double.
         *
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> doubles()
        {
            return asFactory(Integer.class);
        }

        /**
         * Returns a supplier function for creating instances of
         * {@code PrimitiveList} for the specified number type.
         *
         * @param numberType The class object representing the number type.
         * @return A supplier function for creating instances of
         *         {@code PrimitiveList}.
         */
        public static Supplier<PrimitiveList> asFactory(Class<?> numberType)
        {
            return () -> new PrimitiveList(numberType);
        }

        /**
         * Writes the list size and elements to the specified {@code DataOutput}
         * stream.
         *
         * @param out The {@code DataOutput} stream to write to.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void writeExternal(@NotNull DataOutput out) throws IOException
        {
            VarInt.write(size(), out);
            if (isEmpty()) return;

            for (final Number number : this) {
                template.value = number;
                template.writeExternal(out);
            }
        }

        /**
         * Reads the list size and elements from the specified {@code DataInput}
         * stream.
         *
         * @param in The {@code DataInput} stream to read from.
         * @throws IOException If an I/O error occurs.
         */
        @Override
        public void readExternal(@NotNull DataInput in) throws IOException
        {
            final int size = (int) VarInt.read(in);
            numbers = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                template.readExternal(in);
                numbers.add(template.value);
            }
        }

        /**
         * Adds a number to the list.
         *
         * @param number The number to add to the list.
         * @return {@code true} if the operation is successful.
         */
        @Override
        public boolean add(Number number)
        {
            return numbers.add(number);
        }

        /**
         * Returns an iterator over the elements in this list.
         *
         * @return An iterator over the elements in this list.
         */
        @Override
        public Iterator<Number> iterator()
        {
            return numbers.iterator();
        }

        /**
         * Returns the number of elements in this list.
         *
         * @return The number of elements in this list.
         */
        @Override
        public int size()
        {
            return numbers.size();
        }
    }

    /**
     * Gets a constant value using a key in the form of a string.
     *
     * @param key The key (in string form) for the constant value.
     * @param <E> The type of IExtension.
     * @return The constant value associated with the key.
     */
    public <E extends ISerializable> E get(final @NotNull String key)
    {
        return get(key.hashCode());
    }

    /**
     * Gets a constant value using a key in the form of an integer.
     *
     * @param key The key (in integer form) for the constant value.
     * @param <E> The type of IExtension.
     * @return The constant value associated with the key.
     */
    public <E extends ISerializable> E get(final int key)
    {
        //noinspection unchecked
        return (E) constants.get(key);
    }

    public void put(final @NotNull String key, final String value)
    {
        put(key, new Literal(value));
    }

    public void put(final @NotNull String key, final Number value)
    {
        put(key, new Numeric(value));
    }

    /**
     * Associates a constant value with a key in the form of a string.
     *
     * @param key   The key (in string form) for the constant value.
     * @param value The constant value to be associated with the key.
     */
    public void put(final @NotNull String key, ISerializable value)
    {
        constants.put(key.hashCode(), value);
    }

    /**
     * Associates a constant value with a key in the form of an integer.
     *
     * @param key   The key (in integer form) for the constant value.
     * @param value The constant value to be associated with the key.
     */
    public void put(final int key, ISerializable value)
    {
        constants.put(key, value);
    }

    public boolean contains(final String key)
    {
        return contains(key.hashCode());
    }

    private boolean contains(int hash)
    {
        return constants.containsKey(hash);
    }

    /**
     * Writes the constants and their associated keys to a data output stream.
     *
     * @param out The DataOutput stream to which the constants are written.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeInt(constants.size());
        for (final int key : constants.keySet()) {
            out.writeInt(key);

            final ISerializable value = constants.get(key);
            out.writeInt(TypeNames.hash(value.getClass()));
            value.writeExternal(out);
        }
    }

    /**
     * Reads constants and their associated keys from a data input stream
     * (serialization).
     *
     * @param in The DataInput stream from which the constants are read.
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int count = in.readInt();
        constants = new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            final int key = in.readInt();
            final int hash = in.readInt();

            if (!types.containsKey(hash)) {
                throw new IllegalStateException("could not resolve constant: "
                        + hash);
            }

            ISerializable value = types.get(hash).get();
            value.readExternal(in);
            constants.put(key, value);
        }
    }

    @Override
    public void reset()
    {
        constants.clear();
    }
}
