package io.github.test.benchmark; //@date 14.11.2023

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.benchmark.BenchmarkResult;
import io.github.libfp.benchmark.CHATestSuite;
import io.github.libfp.benchmark.DataSet;
import io.github.libfp.benchmark.TestResult;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.impl.hierarchy.HierarchyStrategies;
import io.github.libfp.profile.IProfileContext;
import io.github.libfp.threshold.IThresholdConfig;
import io.github.libfp.threshold.SimpleThresholdConfig;
import org.junit.jupiter.api.Test;

/**
 * Benchmark test class specifically for hierarchy + rolling hash profiles.
 */
public final class HierarchyProfileBenchmarkTest
        extends BenchmarkTest<AnalysisScope>
{

    // Configuration for the benchmark test
    private final IThresholdConfig config = new IThresholdConfig() {
        @Override
        public double getThreshold(Class<?> context)
        {
            return 0.8;
        }
    };

    // Name of the app for the benchmark test
    private final String appName = "003-nodomain.freeyourgadget.gadgetbridge";

    /**
     * Constructor for the HierarchyProfileBenchmarkTest class.
     */
    public HierarchyProfileBenchmarkTest()
    {
        // Use CHATestSuite as the factory for creating the test suite
        super(CHATestSuite::new);
    }

    /**
     * Create the dataset for the benchmark test.
     *
     * @return The created dataset.
     */
    @Override
    protected DataSet createDataSet()
    {
        // Create the dataset with the specified target directory and extension
        return getDataSet("hierarchy", ".h+rh.fp");
    }

    /**
     * Create the context for the benchmark test.
     *
     * @return The created context.
     */
    @Override
    protected IProfileContext<AnalysisScope> createContext()
    {
        return HierarchyStrategies.getInstance();
    }

    /**
     * Set up the test suite for the benchmark test.
     */
    @Test
    void setUp()
    {
        prepareTestSuite(appName);
    }

    /**
     * Perform the hierarchy and rolling hash benchmark test.
     */
    @Test
    public void hierarchyAndRollingHashBenchmark()
    {
        benchmarkTest(appName, config);
    }

    @Test
    public void hierarchyAndRollingHashSingleTest()
    {
        IThresholdConfig thresholdConfig = new SimpleThresholdConfig()
                .set(TestResult.class, 0.75)
                .set(ClassProfile.class, 0.8);

        testOne(appName, BenchmarkResult.defaultAppType, thresholdConfig);
    }
}

