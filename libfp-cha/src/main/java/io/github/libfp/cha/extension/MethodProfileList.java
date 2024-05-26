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

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.profile.IManagedProfileFactory;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileList;
import org.jetbrains.annotations.NotNull;

public final class MethodProfileList extends ProfileList<MethodProfile>
{

    public MethodProfileList()
    {
        this(MethodProfile::new);
    }

    public MethodProfileList(IManagedProfileFactory<MethodProfile> factory)
    {
        super(factory);
    }

    public int addMethod(final @NotNull IMethod iMethod)
    {
        final int index = add();
        final MethodProfile profile = get(index);
        if (getManager().hasExtension(Descriptors.class)) {
            final String descriptor = getManager()
                    .getILFactory()
                    .getDescriptor(iMethod);

            profile.descriptor = getManager()
                    .getExtension(Descriptors.class)
                    .addDescriptor(descriptor);
        }
        return index;
    }
}
