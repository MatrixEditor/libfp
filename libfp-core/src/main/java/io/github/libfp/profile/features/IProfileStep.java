package io.github.libfp.profile.features;//@date 28.10.2023

import io.github.libfp.profile.Profile;

/**
 * An interface representing a profile step in a processing pipeline.
 */
public interface IProfileStep<C, T extends Profile<?>>
        extends IStep<C, T>
{

    /**
     * Process the input data and produce the output data, typically modifying
     * or enhancing a profile.
     *
     * @param ref    The input data, often a class hierarchy.
     * @param target The output data, often a profile, where the result of
     *               processing is stored.
     */
    @Override
    void process(C ref, T target);
}
