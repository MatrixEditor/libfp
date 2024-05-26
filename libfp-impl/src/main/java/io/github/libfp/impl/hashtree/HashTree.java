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
package io.github.libfp.impl.hashtree;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.cha.*;
import io.github.libfp.cha.step.ICHAProfileStep;
import io.github.libfp.cha.step.IClassStep;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.cha.step.IPackageStep;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.extensions.Constants.PrimitiveArray;
import io.github.libfp.profile.manager.ProfileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * The {@code HashTree} class provides utility methods for working with hash
 * trees in the context of profiles. It includes methods for getting and setting
 * hashes, checking if a profile is a leaf, and performing tree operations. It
 * also defines several classes that implement different steps in the hash tree
 * construction.
 */
public final class HashTree
{
    private static final String HASH_KEY = "hash";

    private HashTree()
    {
    }

    public static <E extends ExtensibleProfile> Blueprint<E> addToBlueprint(
            @NotNull Blueprint<E> blueprint)
    {
        return blueprint.add(HASH_KEY, PrimitiveArray.bytes());
    }

    public static boolean isHashed(@NotNull ExtensibleProfile profile)
    {
        byte[] hash = getHash(profile);
        return hash != null && hash.length > 0;
    }


    /**
     * Creates a blueprint for a hash tree based on the provided factory.
     *
     * @param factory The profile factory for which the blueprint is created.
     * @param <E>     The type of the extensible profile.
     * @return The blueprint for the hash tree.
     */
    public static <E extends ExtensibleProfile> Blueprint<E> getBlueprint(
            @NotNull IManagedProfileFactory<E> factory)
    {
        return addToBlueprint(Blueprint.make(factory));
    }

    /**
     * Applies the hash tree blueprint to the specified profile manager.
     *
     * @param manager The profile manager to which the hash tree blueprint is
     *                applied.
     * @param factory The profile factory for which the blueprint is created.
     * @param <E>     The type of the extensible profile.
     */
    public static <E extends ExtensibleProfile> void applyBlueprint(
            @NotNull ProfileManager manager,
            @NotNull IManagedProfileFactory<E> factory)
    {
        manager.with(getBlueprint(factory));
    }

    /**
     * Checks if the given profile is a leaf in the hash tree.
     *
     * @param profile The extensible profile to check.
     * @return {@code true} if the profile is a leaf, {@code false} otherwise.
     */
    public static boolean isLeaf(@NotNull ExtensibleProfile profile)
    {
        if (profile instanceof ExtendedClassProfile cp) {
            return cp.getMethodCount() == 0;
        }
        if (profile instanceof PackageProfile pp) {
            return pp.getClassCount() == 0;
        }
        return true;
    }

    /**
     * Gets the hash associated with the specified profile.
     *
     * @param profile The extensible profile for which to get the hash.
     * @return The hash as a byte array.
     */
    public static byte @Nullable [] getHash(@NotNull ExtensibleProfile profile)
    {
        PrimitiveArray value = Blueprint.getValue(profile, HASH_KEY);
        return value != null ? (byte[]) value.array : null;
    }

    /**
     * Sets the hash for the specified profile.
     *
     * @param profile The extensible profile for which to set the hash.
     * @param array   The byte array representing the hash.
     */
    public static void setHash(
            @NotNull ExtensibleProfile profile,
            byte @NotNull [] array)
    {
        profile.put(HASH_KEY, new PrimitiveArray(array));
    }

    /**
     * Gets the vertices (children) of the specified profile.
     *
     * @param profile The extensible profile for which to get the vertices.
     * @param <E>     The type of the managed profile.
     * @return The collection of vertices.
     */
    public static <E extends ManagedProfile> @NotNull Collection<E> getVertices(
            @NotNull ExtensibleProfile profile)
    {
        if (isLeaf(profile)) {
            return Collections.emptyList();
        }
        if (profile instanceof ExtendedClassProfile cp) {
            //noinspection unchecked
            return (Collection<E>) cp.getMethods();
        }
        if (profile instanceof PackageProfile pp) {
            //noinspection unchecked
            return (Collection<E>) pp.getClasses();
        }
        return Collections.emptyList();
    }

    /**
     * Represents a step in the hash tree construction for packages.
     */
    public static class PackageStep extends HashStep implements IPackageStep
    {
        public PackageStep(String algorithm)
        {
            super(algorithm);
        }

        @Override
        public @NotNull Class<? extends ManagedProfile> targetProfileClass()
        {
            return PackageProfile.class;
        }

        @Override
        public int priority()
        {
            return 1;
        }

        @Override
        public void process(IClassHierarchy ref, @NotNull PackageProfile target)
        {
            MessageDigest digest = newInstance();
            final Collection<ClassProfile> vertices =
                    getVertices(target);

            vertices.stream()
                    .map(HashTree::getHash)
                    .filter(Objects::nonNull)
                    .sorted(Arrays::compare)
                    .forEach(digest::update);

            if (!vertices.isEmpty()) {
                setHash(target, digest.digest());
            } else {
                setHash(target, new byte[0]);
            }
        }
    }

    /**
     * Represents a step in the hash tree construction for methods.
     */
    public static class MethodStep extends HashStep implements IMethodStep
    {
        public MethodStep(String algorithm)
        {
            super(algorithm);
        }

        @Override
        public @NotNull Class<? extends ManagedProfile> targetProfileClass()
        {
            return MethodProfile.class;
        }

        @Override
        public void process(IMethod ref, @NotNull MethodProfile target)
        {
            MessageDigest digest = newInstance();
            digest.update(target.getDescriptor().getBytes());
            HashTree.setHash(target, digest.digest());
        }
    }

    /**
     * Represents a step in the hash tree construction for classes.
     */
    public static class ClassStep extends HashStep implements IClassStep
    {
        public ClassStep(String algorithm)
        {
            super(algorithm);
        }

        @Override
        public @NotNull Class<? extends ManagedProfile> targetProfileClass()
        {
            return ClassProfile.class;
        }

        @Override
        public int priority()
        {
            return 1;
        }

        @Override
        public void process(IClass ref, @NotNull ClassProfile target)
        {
            MessageDigest digest = newInstance();

            final Collection<MethodProfile> vertices =
                    HashTree.getVertices(target);

            digest.update(target.getDescriptor().getBytes());
            vertices.stream()
                    .map(HashTree::getHash)
                    .filter(Objects::nonNull)
                    .sorted(Arrays::compare)
                    .forEach(digest::update);

            HashTree.setHash(target, digest.digest());
        }
    }

    /**
     * Represents a step in the hash tree construction for profiles.
     */
    public static class ProfileStep extends HashStep
            implements ICHAProfileStep<CHAProfile>
    {
        public ProfileStep(String algorithm)
        {
            super(algorithm);
        }

        @Override
        public int priority()
        {
            return 1;
        }

        @Override
        public void process(IClassHierarchy ref, @NotNull CHAProfile target)
        {
            PackageProfile root = target.getPackages().getRootPackage();
            MessageDigest digest = newInstance();

            root.getSubPackages()
                    .stream()
                    .map(HashTree::getHash)
                    .filter(Objects::nonNull)
                    .sorted(Arrays::compare)
                    .forEach(digest::update);

            HashTree.setHash(root, digest.digest());
        }
    }
}
