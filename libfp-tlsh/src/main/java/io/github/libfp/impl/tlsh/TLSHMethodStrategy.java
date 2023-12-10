package io.github.libfp.impl.tlsh; //@date 26.10.2023

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.hash.TrendMicroLSH;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

public class TLSHMethodStrategy
        implements ISimilarityStrategy<TLSHMethodProfile>,
                   IMethodStep
{
    @Override
    public @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        return TLSHMethodProfile.class;
    }

    @Override
    public void process(@NotNull IMethod ref, @NotNull MethodProfile target)
    {
        TLSHMethodProfile method = (TLSHMethodProfile) target;

        method.hash = new TrendMicroLSH();
        target.getManager().getNormalizer()
              .normalize(ref)
              .forEach(x -> method.hash.creator.update(x.getBytes()));

        // REVISIT maybe flag methods with no hash
        method.hash.tlsh = method.hash.creator.getHashNoThrow();
        method.hash.creator = null;
    }


    @Override
    public double similarityOf(
            @NotNull TLSHMethodProfile app,
            @NotNull TLSHMethodProfile lib,
            IThresholdConfig config)
    {
        if (!app.getDescriptor().equals(lib.getDescriptor())) {
            return 0;
        }

        if (lib.hash.tlsh == null || app.hash.tlsh == null) {
            // same descriptor and no hash: we have to flag them as equal
            return lib.hash.tlsh == null && app.hash.tlsh == null
                    ? 1 : 0;
        }

        final double diff = lib.hash.tlsh.totalDiff(app.hash.tlsh, false);
        if (diff == 0) {
            // zero means absolute similarity
            return 1.0;
        }

        double upperBound = ITLSHThresholdConfig.defaultUpperBounds;
        if (config instanceof ITLSHThresholdConfig) {
            upperBound =
                    ((ITLSHThresholdConfig) config).getUpperDifferenceBounds();
        }

        if (diff > upperBound) {
            return 0.0; // too far away
        }
        return (upperBound - diff) / upperBound;
    }
}
