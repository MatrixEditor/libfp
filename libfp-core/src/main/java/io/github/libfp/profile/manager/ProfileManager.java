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
package io.github.libfp.profile.manager;

import io.github.libfp.ISerializable;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.profile.bytecode.IBytecodeNormalizerFactory;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ProfileManager} class represents a central management facility for
 * profiles and extensions in a system. It maintains registries of extensions,
 * modules, and blueprints, and it provides methods to configure and retrieve
 * various components used in profile processing.
 *
 * <p>
 * Extensions are components that provide additional functionality to the
 * profile processing system. Modules are components that handle specific
 * aspects of profile processing, such as bytecode normalization. Blueprints are
 * used to define and manage fields dynamically associated with
 * {@link ExtensibleProfile} instances.
 * </p>
 *
 * <p>
 * The profile manager supports the configuration of various components, such as
 * bytecode normalizers, IL factories, and strategies. It also manages the
 * extensions that contribute to the profile processing workflow. Extensions can
 * have different retention policies, such as SOURCE or RUNTIME, which determine
 * when they are applied during the profile processing lifecycle.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * // Create a profile manager with default settings
 * ProfileManager profileManager = new ProfileManager();
 *
 * // Configure the profile manager with an IL factory and a bytecode normalizer
 * ILFactory ilFactory = // ...;
 * IBytecodeNormalizer normalizer = // ...;
 * profileManager.with(ilFactory).with(normalizer);
 *
 * // Register an extension with a specific retention policy
 * MyExtension myExtension = new MyExtension();
 * profileManager.with(myExtension, RetentionPolicy.SOURCE);
 *
 * // Get an instance of a specific extension
 * MyExtension retrievedExtension = profileManager.getExtension(MyExtension.class);
 *
 * // Check if a specific extension is registered
 * boolean hasExtension = profileManager.hasExtension(MyExtension.class);
 * }</pre>
 *
 * @see IModule
 * @see IExtension
 * @see Blueprint
 */
public class ProfileManager implements ISerializable
{
    protected final Map<Integer, IExtension> extensions = new HashMap<>();
    // the next two maps are only used as an internal cache
    protected final Map<Class<?>, Integer> class2Extension = new HashMap<>();
    protected final Map<String, Integer> name2Extension = new HashMap<>();

    // all local modules and blueprints won't be serialized
    protected final Map<Integer, IModule> modules = new HashMap<>();
    protected final Map<Integer, Blueprint<?>> blueprints = new HashMap<>();

    private IStrategy<?> strategy;

    public ProfileManager()
    {
        this(null, null);
    }

    public ProfileManager(ILFactory ilFactory)
    {
        this(ilFactory, null);
    }

    public ProfileManager(BytecodeNormalizer normalizer)
    {
        this(null, normalizer);
    }

    public ProfileManager(ILFactory ilFactory, BytecodeNormalizer normalizer)
    {
        with(ilFactory);
        with(BytecodeNormalizer.class, normalizer);
    }

    public static @NotNull ProfileManager getInstance()
    {
        return getInstance(null);
    }

    public static @NotNull ProfileManager getInstance(
            @Nullable ILFactory factory)
    {
        return new ProfileManager(factory)
                .with(new ProfileInfo())
                .with(new Descriptors())
                .with(new Constants());
    }

    /**
     * Resets all registered extensions by invoking their
     * {@link IExtension#reset()} methods.
     */
    public void reset()
    {
        for (IExtension extension : extensions.values()) {
            extension.reset();
        }
    }

    /**
     * Configures the specified extension to have a retention policy of
     * {@link RetentionPolicy#SOURCE}.
     *
     * @param aClass The class of the extension to configure.
     * @return The updated {@code ProfileManager} instance.
     */
    public ProfileManager onlySource(
            Class<? extends IExtension> aClass)
    {
        return withRetention(aClass, RetentionPolicy.SOURCE);
    }

    /**
     * Configures the specified extension to have the specified retention
     * policy.
     *
     * @param aClass The class of the extension to configure.
     * @param policy The retention policy to set.
     * @return The updated {@code ProfileManager} instance.
     */
    public ProfileManager withRetention(
            Class<? extends IExtension> aClass,
            RetentionPolicy policy)
    {
        getExtension(aClass).setRetentionPolicy(policy);
        return this;
    }

    /**
     * Gets the bytecode normalizer associated with this profile manager.
     *
     * @return The bytecode normalizer.
     */
    public BytecodeNormalizer getNormalizer()
    {
        return getModule(BytecodeNormalizer.class);
    }

    /**
     * Configures the profile manager with the specified bytecode normalizer
     * factory, creating an instance of the bytecode normalizer using the
     * factory.
     *
     * @param factory The bytecode normalizer factory.
     * @return The updated {@code ProfileManager} instance.
     */
    public @NotNull ProfileManager with(
            @NotNull IBytecodeNormalizerFactory factory)
    {
        return with(BytecodeNormalizer.class,
                factory.newInstance(getILFactory()));
    }

    public @NotNull ProfileManager with(
            @NotNull BytecodeNormalizer normalizer)
    {
        return with(BytecodeNormalizer.class, normalizer);
    }

    /**
     * Configures the profile manager with the specified module.
     *
     * @param type   The class type of the module.
     * @param module The module instance to configure.
     * @return The updated {@code ProfileManager} instance.
     */
    public @NotNull ProfileManager with(
            @NotNull Class<? extends IModule> type,
            IModule module)
    {
        this.modules.put(TypeNames.hash(type), module);
        return this;
    }

    /**
     * Gets the module associated with the specified class type hash.
     *
     * @param hash The hash value representing the class type of the module.
     * @return The module associated with the specified hash.
     */
    public IModule getModule(final int hash)
    {
        return modules.get(hash);
    }

    /**
     * Gets the module associated with the specified class type.
     *
     * @param <M>  The type of the module.
     * @param type The class type of the module.
     * @return The module associated with the specified class type.
     */
    public <M extends IModule> M getModule(@NotNull Class<M> type)
    {
        IModule module = getModule(TypeNames.hash(type));
        return type.cast(module);
    }

    /**
     * Configures the profile manager with the specified blueprint, associating
     * it with the class type of the specified profile. The blueprint is used to
     * define and manage fields dynamically associated with instances of the
     * specified profile class type.
     *
     * @param blueprint The blueprint to configure.
     * @param <T>       The type of the profile associated with the blueprint.
     * @return The updated {@code ProfileManager} instance.
     */
    public <T extends ExtensibleProfile> ProfileManager with(
            @NotNull Blueprint<T> blueprint)
    {
        //noinspection unchecked
        Class<? extends T> type =
                (Class<? extends T>) blueprint.newInstance(this).getClass();

        return with(type, blueprint);
    }

    /**
     * Configures the profile manager with the specified blueprint, associating
     * it with the specified profile class type. The blueprint is used to define
     * and manage fields dynamically associated with instances of the specified
     * profile class type.
     *
     * @param type      The class type of the profile associated with the
     *                  blueprint.
     * @param blueprint The blueprint to configure.
     * @param <T>       The type of the profile associated with the blueprint.
     * @return The updated {@code ProfileManager} instance.
     */
    public <T extends ExtensibleProfile> ProfileManager with(
            @NotNull Class<? extends T> type,
            @NotNull Blueprint<T> blueprint)
    {
        blueprints.put(TypeNames.hash(type), blueprint);
        return this;
    }

    /**
     * Gets the blueprint associated with the specified profile class type.
     *
     * @param <T>  The type of the profile associated with the blueprint.
     * @param type The class type of the profile.
     * @return The blueprint associated with the specified profile class type,
     *         or {@code null} if not found.
     */
    public <T extends ExtensibleProfile> @Nullable Blueprint<T> getBlueprint(
            @NotNull Class<? extends T> type)
    {
        //noinspection unchecked
        return (Blueprint<T>) blueprints.get(TypeNames.hash(type));
    }

    /**
     * Gets the IL factory associated with this profile manager.
     *
     * @return The IL factory.
     */
    public ILFactory getILFactory()
    {
        return getModule(ILFactory.class);
    }

    /**
     * Configures the profile manager with the specified IL factory.
     *
     * @param ilFactory The IL factory.
     * @return The updated {@code ProfileManager} instance.
     */
    public @NotNull ProfileManager with(ILFactory ilFactory)
    {
        return with(ILFactory.class, ilFactory);
    }

    /**
     * Gets the strategy associated with this profile manager.
     *
     * @return The strategy.
     */
    public IStrategy<?> getStrategy()
    {
        return strategy;
    }

    /**
     * Sets the strategy for this profile manager.
     *
     * @param strategy The strategy to set.
     */
    public void setStrategy(IStrategy<?> strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Checks if the profile managed by this profile manager is an application
     * profile.
     *
     * @return {@code true} if the profile is an application profile; otherwise,
     *         {@code false}.
     */
    public final boolean isAppProfile()
    {
        return getExtension(ProfileInfo.class).isAppProfile();
    }

    /**
     * Configures the profile manager with the specified extension, setting its
     * retention policy to {@link RetentionPolicy#RUNTIME}.
     *
     * @param extension The extension to configure.
     * @return The updated {@code ProfileManager} instance.
     */
    public final @NotNull ProfileManager with(
            final @NotNull IExtension extension)
    {
        return with(extension, RetentionPolicy.RUNTIME);
    }

    /**
     * Configures the profile manager with the specified extension and retention
     * policy.
     *
     * @param extension The extension to configure.
     * @param policy    The retention policy to set.
     * @return The updated {@code ProfileManager} instance.
     */
    public final @NotNull ProfileManager with(
            final @NotNull IExtension extension,
            final @NotNull RetentionPolicy policy)
    {
        extension.setRetentionPolicy(policy);
        int hash = TypeNames.hash(extension.getClass());
        extensions.putIfAbsent(hash, extension);
        class2Extension.putIfAbsent(extension.getClass(), hash);
        name2Extension.putIfAbsent(extension.getClass().getName(), hash);
        extension.setManager(this);
        return this;
    }

    /**
     * Gets the extension associated with the specified extension name.
     *
     * @param name The name of the extension.
     * @return The extension associated with the specified name.
     * @throws IllegalStateException If the extension is not registered.
     */
    public final @NotNull IExtension getExtension(final @NotNull String name)
            throws IllegalStateException
    {
        return getExtension(name.hashCode());
    }

    /**
     * Gets the extension associated with the specified extension hash.
     *
     * @param hash The hash value representing the extension class.
     * @return The extension associated with the specified hash.
     * @throws IllegalStateException If the extension is not registered.
     */
    public final @NotNull IExtension getExtension(final int hash)
            throws IllegalStateException
    {
        IExtension extension = extensions.get(hash);
        if (extension == null) {
            throw new IllegalStateException(
                    "Extension with type hash (" + hash + ")"
                            + " hasn't been registered yet!"
            );
        }
        return extension;
    }

    /**
     * Gets the extension associated with the specified extension class type.
     *
     * @param <T>  The type of the extension.
     * @param type The class type of the extension.
     * @return The extension associated with the specified class type.
     * @throws IllegalStateException If the extension is not registered.
     */
    public final <T extends IExtension> @NotNull T getExtension(
            final @NotNull Class<T> type)
            throws IllegalStateException
    {
        final IExtension extension = extensions.get(class2Extension.get(type));
        return type.cast(extension);
    }

    /**
     * Gets the count of registered extensions.
     *
     * @return The count of registered extensions.
     */
    public final int getExtensionCount()
    {
        return extensions.size();
    }

    /**
     * Reads the profile manager state from the specified {@link DataInput}
     * stream.
     *
     * @param in The {@code DataInput} stream to read from.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int count = in.readInt();

        for (int i = 0; i < count; i++) {
            final int hash = in.readInt();
            if (!extensions.containsKey(hash)) {
                throw new IOException("Invalid profile: " + hash + " - " +
                        "extension not registered!");
            }

            IExtension extension = getExtension(hash);
            extension.readExternal(in);
        }
    }

    /**
     * Writes the profile manager state to the specified {@link DataOutput}
     * stream.
     *
     * @param out The {@code DataOutput} stream to write to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        out.writeInt(getExtensionCount());
        for (final IExtension extension : extensions.values()) {
            if (extension.getRetention() == RetentionPolicy.SOURCE) {
                continue;
            }

            final int hash = TypeNames.hash(extension.getClass());
            out.writeInt(hash);
            extension.writeExternal(out);
        }
    }

    /**
     * Checks if the specified extension class type is registered with this
     * profile manager.
     *
     * @param aClass The extension class type to check.
     * @return {@code true} if the extension is registered; otherwise,
     *         {@code false}.
     */
    public boolean hasExtension(@NotNull Class<? extends IExtension> aClass)
    {
        return class2Extension.containsKey(aClass);
    }
}



