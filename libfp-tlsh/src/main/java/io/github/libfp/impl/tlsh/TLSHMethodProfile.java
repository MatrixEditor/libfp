package io.github.libfp.impl.tlsh; //@date 24.10.2023

import io.github.libfp.hash.TrendMicroLSH;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TLSHMethodProfile extends MethodProfile
{

    public TrendMicroLSH hash;

    public TLSHMethodProfile(ProfileManager manager)
    {
        super(manager);
        this.hash = new TrendMicroLSH();
    }

    @Override
    public double similarityTo(MethodProfile other, IThresholdConfig config)
    {
        double result = super.similarityTo(other, config);
        if (result == 0) {
            return 0;
        }

        final TLSHMethodProfile appProfile = (TLSHMethodProfile) other;
        if (hash.tlsh == null || appProfile.hash.tlsh == null) {
            return hash.tlsh == null && appProfile.hash.tlsh == null ?
                    result : 0;
        }
        final double diff = hash.tlsh.totalDiff(appProfile.hash.tlsh, false);
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

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        hash = new TrendMicroLSH(in);
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        hash.writeExternal(out);
    }
}
