package io.github.libfp.threshold; //@date 15.11.2023

import java.util.HashMap;
import java.util.Map;

public class SimpleThresholdConfig implements IThresholdConfig
{

    private final Map<Class<?>, Double> configuration;

    public SimpleThresholdConfig()
    {
        this(new HashMap<>());
    }

    public SimpleThresholdConfig(Map<Class<?>, Double> configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public double getThreshold(Class<?> context)
    {
        return configuration.getOrDefault(context, 0.0);
    }

    public SimpleThresholdConfig set(Class<?> context, double threshold) {
        configuration.put(context, threshold);
        return this;
    }
}
