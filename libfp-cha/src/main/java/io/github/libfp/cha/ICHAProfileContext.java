package io.github.libfp.cha;//@date 13.11.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.IProfileContext;
import io.github.libfp.profile.IProfileFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code ICHAProfileContext} interface extends {@code IProfileContext} and
 * specifies the type parameter as {@code AnalysisScope}. It provides default
 * implementations for creating a {@code ProfileBuilder}, a
 * {@code ProfileFactory}, and retrieving the default {@code CHAStrategy}.
 *
 * @see CHAProfile
 * @see CHAStrategy
 */
public interface ICHAProfileContext extends IProfileContext<AnalysisScope>
{
    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull IProfileBuilder<AnalysisScope> getProfileBuilder()
    {
        return CHAProfile::new;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull IProfileFactory<AnalysisScope> getProfileFactory()
    {
        return CHAProfile::new;
    }

    @Override
    @Nullable ICHAIntegration getIntegration();


    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull CHAStrategy getStrategy()
    {
        CHAStrategy strategy = new CHAStrategy();
        ICHAIntegration integration = getIntegration();
        if (integration != null) {
            integration.setPolicies(strategy);
            integration.addProfileStep(strategy);
            integration.setProfileStrategy(strategy);

            // layers
            integration.setPackageLayer(strategy);
            integration.setClassLayer(strategy);
            integration.setMethodLayer(strategy);
            integration.setFieldLayer(strategy);
        }
        return strategy;
    }
}

