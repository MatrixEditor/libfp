package io.github.libfp.impl.hierarchy; //@date 23.10.2023

import io.github.libfp.cha.ClassProfile;
import io.github.libfp.hash.RollingHash;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * HierarchyDescriptorFilter + rolling-hash + simple matching
 */
public final class HierarchyClassProfile extends ClassProfile
{

    public RollingHash signatures;

    public HierarchyClassProfile(ProfileManager manager)
    {
        super(manager);
    }

    @Override
    public double similarityTo(ClassProfile other, IThresholdConfig config)
    {
        double result = super.similarityTo(other, config);
        if (result == 0.0) {
            return 0.0;
        }

        HierarchyClassProfile libProfile = (HierarchyClassProfile) other;
        int a = 0;
        int count = Math.max(signatures.size() /*+ libProfile.signatures.size
        ()*/, 1);

        for (final int hash : signatures) {
            a += libProfile.signatures.contains(hash) ? 1 : 0;
        }
        return a != 0 ? a * 1.0 / count : 0;
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        signatures = new RollingHash();
        signatures.readExternal(in);
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        signatures.writeExternal(out);
    }
}
