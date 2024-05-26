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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@code HashStep} class provides a base implementation for steps that
 * involve hashing using a specified algorithm. It includes a constructor to set
 * the hashing algorithm and a method to create a new instance of
 * {@link MessageDigest} based on the specified algorithm.
 */
public abstract class HashStep
{
    private final String algorithm;

    /**
     * Constructs a {@code HashStep} with the specified hashing algorithm.
     *
     * @param algorithm The hashing algorithm to be used.
     */
    public HashStep(String algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * Creates a new instance of {@link MessageDigest} based on the hashing
     * algorithm.
     *
     * @return A new instance of {@link MessageDigest}.
     * @throws IllegalStateException If the specified algorithm is not
     *                               available.
     */
    protected MessageDigest newInstance()
    {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
