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
package io.github.libfp.cha.extension;

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.IClassProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The ClassProfileList class is a specific implementation of the ProfileList
 * class for managing a list of ClassProfile instances.
 *
 * @see ProfileList
 * @see ClassProfile
 * @see IClassProfileFactory
 */
public final class ClassProfileList extends ProfileList<ClassProfile>
{

    // only during the generation process
    private final Map<IClass, Integer> classProfileMap = new HashMap<>();

    /**
     * Constructs a ClassProfileList with the given
     * {@link IClassProfileFactory}.
     */
    public ClassProfileList()
    {
        this(ClassProfile::new);
    }

    /**
     * Constructs a ClassProfileList with the given
     * {@link IClassProfileFactory}.
     *
     * @param factory The factory used to create new instances of ClassProfile.
     */
    public ClassProfileList(IClassProfileFactory factory)
    {
        super(factory);
    }

    /**
     * Adds a new class profile to the list associated with the given IClass.
     *
     * @param iClass The IClass for which to create a class profile.
     * @return The index of the newly added class profile in the list.
     */
    public int addClass(final IClass iClass)
    {
        final int index = add();
        final ClassProfile profile = get(index);

        if (getManager().hasExtension(Descriptors.class)) {
            final String descriptor = getManager()
                    .getILFactory()
                    .getDescriptor(iClass);

            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(descriptor);
        }

        classProfileMap.putIfAbsent(iClass, index);
        return index;
    }

    public @Nullable ClassProfile getClassProfile(final IClass iClass)
    {
        final int index = getClassProfileIndex(iClass);
        if (index == -1) {
            return null;
        }
        return get(index);
    }

    public int getClassProfileIndex(final IClass iClass)
    {
        return classProfileMap.getOrDefault(iClass, -1);
    }

}
