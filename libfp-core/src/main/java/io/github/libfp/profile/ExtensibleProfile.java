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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ExtensibleProfile} class is an abstract subclass of
 * {@link ManagedProfile} that allows dynamic extension by associating
 * additional fields with the profile. It maintains a map of field values
 * identified by their keys. Fields are defined and managed using
 * {@link Blueprint}.
 *
 * <p>
 * To define and manage fields, subclasses of {@code ExtensibleProfile} can use
 * the {@link Blueprint} class. Fields can be added to the blueprint along with
 * their corresponding data types or factory methods. During the serialization
 * and deserialization process, the blueprint is used to write and read the
 * values of these fields to and from the underlying data stream.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * // Define a subclass of ExtensibleProfile
 * public class MyExtensibleProfile extends ExtensibleProfile {
 *     // Constructor
 *     public MyExtensibleProfile(ProfileManager manager) {
 *         super(manager);
 *     }
 * }
 *
 * // Create a blueprint for the MyExtensibleProfile subclass
 * Blueprint<MyExtensibleProfile> blueprint = Blueprint.make(MyExtensibleProfile::new)
 *         .add("fieldName1", TemplateClass1::new)
 *         .add("fieldName2", TemplateClass2::new);
 *
 * // Register the blueprint with the ProfileManager
 * ProfileManager profileManager = // ...;
 * profileManager.with(MyExtensibleProfile.class, blueprint);
 *
 * // Create an instance of MyExtensibleProfile
 * MyExtensibleProfile profile = new MyExtensibleProfile(profileManager);
 *
 * // Set values for the additional fields
 * profile.put("fieldName1", value1);
 * profile.put("fieldName2", value2);
 *
 * // Serialize the profile to a DataOutput stream
 * DataOutput out = // ...;
 * profile.writeExternal(out);
 *
 * // Deserialize the profile from a DataInput stream
 * DataInput in = // ...;
 * MyExtensibleProfile nextProfile = blueprint.newInstance(profileManager);
 * nextProfile.readExternal(in); // or blueprint.read(nextProfile, in);
 * }</pre>
 *
 * @see Blueprint
 * @see ProfileManager
 */
public abstract class ExtensibleProfile extends ManagedProfile
{

    private final Map<String, ISerializable> values;

    /**
     * Constructs an {@code ExtensibleProfile} associated with the given
     * {@link ProfileManager}.
     *
     * @param manager The {@code ProfileManager} that manages this profile.
     */
    protected ExtensibleProfile(ProfileManager manager)
    {
        super(manager);
        this.values = new HashMap<>();
    }

    /**
     * Gets the value of the field associated with the specified key.
     *
     * @param key The key of the field.
     * @return The value of the field, or {@code null} if the key is not
     *         present.
     */
    public @Nullable ISerializable get(String key)
    {
        return values.get(key);
    }

    /**
     * Associates the specified value with the specified key as a field in this
     * profile.
     *
     * @param key   The key of the field.
     * @param value The value to be associated with the key.
     */
    public void put(String key, ISerializable value)
    {
        values.put(key, value);
    }

    ///////////////////////////////////////////////////////////////////////////
    // I/O via Blueprint
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Writes the values of the fields associated with this profile to the
     * specified {@link DataOutput} stream using the associated
     * {@link Blueprint}.
     *
     * @param out The {@code DataOutput} stream to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        Blueprint<ExtensibleProfile> blueprint =
                getManager().getBlueprint(getClass());

        if (blueprint != null) {
            blueprint.write(this, out);
        }
    }

    /**
     * Reads the values of the fields associated with this profile from the
     * specified {@link DataInput} stream using the associated
     * {@link Blueprint}.
     *
     * @param in The {@code DataInput} stream to read from.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        Blueprint<ExtensibleProfile> blueprint =
                getManager().getBlueprint(getClass());

        if (blueprint != null) {
            blueprint.read(this, in);
        }
    }
}
