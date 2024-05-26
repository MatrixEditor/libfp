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

import com.ibm.wala.classLoader.IField;
import io.github.libfp.cha.FieldProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.NotNull;

/**
 * The FieldProfileList class is a specific implementation of the ProfileList
 * class for managing a list of FieldProfile instances.
 */
public final class FieldProfileList extends ProfileList<FieldProfile>
{

    /**
     * Constructs a FieldProfileList with the given ManagedProfileFactory.
     */
    public FieldProfileList()
    {
        this(FieldProfile::new);
    }

    /**
     * Constructs a FieldProfileList with the given ManagedProfileFactory.
     *
     * @param factory The factory used to create new instances of FieldProfile.
     */
    public FieldProfileList(IManagedProfileFactory<FieldProfile> factory)
    {
        super(factory);
    }

    /**
     * Adds a new field profile to the list associated with the given IField.
     *
     * @param field The IField for which to create a field profile.
     * @return The index of the newly added field profile in the list.
     */
    public int addField(final @NotNull IField field)
    {
        final int index = add();
        FieldProfile profile = factory.newInstance(getManager());

        if (getManager().hasExtension(Descriptors.class)) {
            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(getManager()
                            .getILFactory()
                            .getDescriptor(field));
        }

        profiles.add(profile);
        return index;
    }
}
