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

import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Functional interface for creating profile instances (build and import).
 */
public abstract class ProfileFactory<C>
{

    /**
     * Creates a new profile instance using the provided file, profile manager,
     * and strategy.
     *
     * @param file     The file associated with the profile.
     * @param manager  The profile manager for managing profiles.
     * @param strategy The strategy used for creating the profile.
     * @return The newly created profile instance.
     * @throws IOException If an I/O error occurs during the profile creation
     *                     process.
     */
    public abstract Profile load(
            final @NotNull File file,
            final @NotNull ProfileManager manager,
            final @NotNull IStrategy<?> strategy) throws IOException;

    /**
     * Builds a profile using the provided context, profile manager, and
     * strategy.
     *
     * @param context  The context associated with the profile.
     * @param manager  The profile manager for managing profiles.
     * @param strategy The strategy used for building the profile.
     * @return The built profile.
     * @throws Exception If an error occurs during the profile building
     *                   process.
     */
    public abstract Profile build(
            final @NotNull C context,
            final @NotNull ProfileManager manager,
            final @NotNull IStrategy<?> strategy) throws Exception;
}


