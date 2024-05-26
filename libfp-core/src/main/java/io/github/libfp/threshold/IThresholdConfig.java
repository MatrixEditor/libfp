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

package io.github.libfp.threshold;

/**
 * The <code>IThresholdConfig</code> interface defines the base class for
 * obtaining threshold values for class profiling.
 *
 * <p>
 * Implementations of this interface must provide a method to retrieve the class
 * profile threshold, which is used to determine how classes are included in
 * profiling.
 * </p>
 *
 * <p>
 * The class correspondence threshold typically represents a value above which a
 * correspondence is considered for profiling. The specific threshold value and
 * its meaning may vary depending on the implementation.
 * </p>
 */
public interface IThresholdConfig
{

    /**
     * Returns the threshold value for the given class context.
     *
     * @param context The class context.
     * @return The threshold value.
     */
    default double getThreshold(Class<?> context)
    {
        return 0.0;
    }
}
