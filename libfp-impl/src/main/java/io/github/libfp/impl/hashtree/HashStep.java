package io.github.libfp.impl.hashtree; //@date 12.11.2023

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
