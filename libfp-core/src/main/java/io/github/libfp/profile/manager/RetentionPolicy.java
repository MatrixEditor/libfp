package io.github.libfp.profile.manager;//@date 31.10.2023

/**
 * An enum specifying whether an extension is only available during profile
 * generation ({@link #SOURCE}) or remains available when comparing profiles
 * ({@link #RUNTIME}).
 */
public enum RetentionPolicy
{
    /**
     * Indicates that the extension remains available at runtime when comparing
     * profiles. <b>This is the default setting.</b>
     */
    RUNTIME,

    /**
     * Indicates that the extension is only available during profile
     * generation.
     */
    SOURCE
}

