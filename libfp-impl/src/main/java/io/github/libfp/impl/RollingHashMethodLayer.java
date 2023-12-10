package io.github.libfp.impl; //@date 28.10.2023

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.hash.RollingHash;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code RollingHashMethodLayer} class implements the
 * {@code ISimilarityStrategy} and {@code IMethodStep} interfaces to provide a
 * strategy for calculating the similarity of {@code RollingHashMethodProfile}
 * instances and processing method profiles using a rolling hash approach.
 *
 * @see ISimilarityStrategy
 * @see IMethodStep
 */
public final class RollingHashMethodLayer
        implements ISimilarityStrategy<RollingHashMethodProfile>,
                   IMethodStep
{
    /**
     * Returns the class of the target profile that this layer operates on,
     * which is {@code RollingHashMethodProfile}.
     *
     * @return The class of the target profile.
     */
    @Override
    public @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        return RollingHashMethodProfile.class;
    }

    /**
     * Calculates the similarity between two {@code RollingHashMethodProfile}
     * instances based on their rolling hash values.
     *
     * @param app    The application {@code RollingHashMethodProfile} for
     *               comparison.
     * @param lib    The library {@code RollingHashMethodProfile} for
     *               comparison.
     * @param config The similarity threshold configuration.
     *
     * @return The rolling hash-based similarity score between the profiles.
     */
    @Override
    public double similarityOf(
            @NotNull RollingHashMethodProfile app,
            @NotNull RollingHashMethodProfile lib,
            IThresholdConfig config)
    {
        final int size = lib.hash.size();
        int count = 0;
        for (final int value : lib.hash) {
            count += app.hash.contains(value) ? 1 : 0;
        }
        return count == 0 ? 0 : (count * 1.0) / size;
    }

    /**
     * Processes an {@code IMethod} reference and updates the corresponding
     * {@code RollingHashMethodProfile}. This involves computing and storing the
     * rolling hash of the method's normalized content.
     *
     * @param ref    The method reference to process.
     * @param target The method profile to update with rolling hash
     *               information.
     */
    @Override
    public void process(@NotNull IMethod ref, @NotNull MethodProfile target)
    {
        RollingHashMethodProfile rhMethodProfile =
                (RollingHashMethodProfile) target;

        rhMethodProfile.hash = new RollingHash();
        target.getManager()
              .getNormalizer()
              .normalize(ref)
              .forEach(rhMethodProfile.hash::add);
    }
}
