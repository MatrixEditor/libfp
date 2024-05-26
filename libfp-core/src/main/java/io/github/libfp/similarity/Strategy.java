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
package io.github.libfp.similarity;

/**
 * The <code>Strategy</code> class represents a configuration and management
 * class for handling various similarity strategies and processing steps. It
 * allows the registration of similarity strategies and processing steps for
 * different types of profiles.
 *
 * <p>
 * The <code>Strategy</code> class uses a collection of strategies and steps
 * organized by their corresponding profile types. It provides methods for
 * registering, retrieving, and managing these strategies and steps.
 * </p>
 *
 * <pre>{@code
 *      Strategy strategy = new Strategy()
 *          .with(Profile.class, new MyProfileSimilarityStrategy())
 *          .with(ClassProfile.class, new MyClassStep())
 *          .with(new Strategy()); // inherit from other strategies
 * }</pre>
 */
public class Strategy
        extends AbstractStrategy<Strategy>
{

    @Override
    protected Strategy self()
    {
        return this;
    }
}
