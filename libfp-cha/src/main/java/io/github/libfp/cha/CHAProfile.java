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
package io.github.libfp.cha;

import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.FieldProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.cha.extension.PackageProfileList;
import io.github.libfp.profile.Profile;
import io.github.libfp.profile.manager.ProfileManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class CHAProfile extends Profile
{
    public CHAProfile(
            ProfileManager manager)
            throws IOException
    {
        super(manager);
    }

    /**
     * Get the list of ClassProfile objects associated with this profile.
     *
     * @return The list of ClassProfile objects.
     */
    public @NotNull ClassProfileList getClasses() throws IllegalStateException
    {
        return manager.getExtension(ClassProfileList.class);
    }

    public @NotNull FieldProfileList getFields() throws IllegalStateException
    {
        return manager.getExtension(FieldProfileList.class);
    }

    /**
     * Get the list of MethodProfile objects associated with this profile.
     *
     * @return The list of MethodProfile objects.
     */
    public @NotNull MethodProfileList getMethods() throws IllegalStateException
    {
        return manager.getExtension(MethodProfileList.class);
    }

    /**
     * Get the list of PackageProfile objects associated with this profile.
     *
     * @return The list of PackageProfile objects.
     */
    public @NotNull PackageProfileList getPackages()
            throws IllegalStateException
    {
        return manager.getExtension(PackageProfileList.class);
    }

    public <C> @NotNull Collection<C> getClasses(Class<C> type)
            throws IllegalStateException
    {
        ClassProfileList list = getClasses();
        if (list.size() == 0) {
            return Collections.emptyList();
        }

        Iterator<C> it = list.asIterator(type);
        Collection<C> cs = new ArrayList<>(list.size());
        while (it.hasNext()) {
            cs.add(it.next());
        }
        return cs;
    }

}
