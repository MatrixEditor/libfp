package io.github.example; //@date 25.10.2023

import io.github.libfp.cha.MethodProfile;
import io.github.libfp.similarity.ISimilarityStrategy;
import io.github.libfp.threshold.IThresholdConfig;

public class CustomMethodStrategy
        implements ISimilarityStrategy<MethodProfile>
{

    @Override
    public double similarityOf(
            MethodProfile app,
            MethodProfile lib,
            IThresholdConfig config)
    {
        // this is where the comparison logic would be implemented
        return 0;
    }
}
