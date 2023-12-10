package io.github.libfp.benchmark; //@date 01.11.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.profile.IProfileBuilder;
import io.github.libfp.profile.IProfileFactory;
import io.github.libfp.profile.manager.IProfileManagerFactory;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.util.AnalysisScopeBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * A class representing a Test Suite for benchmarking application profiles. It
 * provides methods for preparing, benchmarking, and running tests.
 */
@ApiStatus.Experimental
public final class CHATestSuite
        extends TestSuite<AnalysisScope>
{

    public CHATestSuite(
            IProfileManagerFactory managerFactory,
            IProfileBuilder<AnalysisScope> profileBuilder,
            IProfileFactory<AnalysisScope> profileFactory,
            DataSet dataSet,
            IStrategy<?> strategy)
    {
        super(managerFactory, profileBuilder, profileFactory, dataSet,
                strategy);
    }

    @Override
    protected AnalysisScope createContext(@NotNull File source)
            throws IOException
    {
        return new AnalysisScopeBuilder()
                .withAndroidFramework(dataSet().frameworkPath())
                .with(source.getAbsolutePath())
                .build();
    }
}
