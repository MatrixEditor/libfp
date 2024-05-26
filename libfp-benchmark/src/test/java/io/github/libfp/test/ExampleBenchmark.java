package io.github.libfp.test;

import io.github.libfp.benchmark.*;
import io.github.libfp.cha.*;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.impl.Strategies;
import io.github.libfp.impl.bloom.Bloom;
import io.github.libfp.impl.hierarchy.FuzzyHierarchyILFactory;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.threshold.IThresholdConfig;
import io.github.libfp.threshold.SimpleThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExampleBenchmark
{

    @NotNull
    public static DataSet getDataSet() throws IOException
    {
        // Specify the root directory of your dataset, which takes the form
        // as described in {@link DataSet}.
        String baseDir = "Demo";

        // Define the subdirectory, which will contain all profiles generated
        // by the TestSuite.
        String targetDir = "bloom";

        // The Android framework JAR storing all subbed classes (we only need
        // the class structure).
        String frameworkPath = "android.jar";

        // Each profile type should be identified by its extension. It is
        // recommended to end an extension with ".fp" to mark the file as a
        // profile.
        String extension = ".b.fp";

        // The path to a file storing the ground truth app mapping. For more
        // details on the specific file format, please refer to the
        // GroundTruth class.
        String groundTruthPath = "demo/100apps-ground-truth.txt";

        // 1. Create the ground truth and the dataset to use
        GroundTruth groundTruth = new GroundTruth(groundTruthPath);
        return new DataSet(
                baseDir,
                targetDir,
                frameworkPath,
                extension,
                groundTruth
        );
    }

    public static void main(String[] args) throws IOException
    {
        // 1. retrieve the dataset
        DataSet dataSet = getDataSet();

        // 2. Create the TestSuite with all relevant factories. Please follow
        // the steps provided in CustomProfileExample to create
        // ProfileManager and Strategy instances.
        ICHAProfileProvider context = new ExampleProfileContext();
        CHATestSuite suite = new TestSuiteBuilder<>(CHATestSuite::new)
                .setDataSet(dataSet)
                .setProvider(context)
                .createTestSuite();

        // 3. Specify the app name(s) to test.
        String appName = "003-nodomain.freeyourgadget.gadgetbridge";

        // 3.5. (optional) Prepare the profiles.
        suite.prepareApp(appName);
        suite.prepareLibraries();

        // 4. Benchmark a strategy by using a custom threshold configuration
        IThresholdConfig config = new SimpleThresholdConfig();
        BenchmarkResult result = suite.benchmark(appName, config);

        // 5. Retrieve the accuracy of the chosen strategy by providing the
        // appType (a default type is "").
        Whitelist whitelist = dataSet
                .groundTruth()
                .getVersionWhitelist(appName);

        for (final String appType : result.getTestedAppTypes()) {
            TestAccuracy accuracy =
                    result.getTestAccuracy(appType, 0.8, whitelist);
            System.out.println("-".repeat(50));
            System.out.println(appType.equals(BenchmarkResult.defaultAppType)
                    ? "default" : appType);
            System.out.println(accuracy);
        }
    }

    public static class ExampleProfileContext implements ICHAProfileProvider
    {

        @Override
        public @NotNull ProfileManager newProfileManager()
        {
            ICHAIntegration integration = getIntegration();
            ProfileManager manager =
                    ProfileManager.getInstance(new FuzzyHierarchyILFactory());

            integration.update(manager, ClassProfile::new);
            // we don't need methods here as our similarity calculation
            // doesn't care about them.
            return manager.with(new ClassProfileList(ClassProfile::new));
        }

        @Override
        public @NotNull ICHAIntegration getIntegration()
        {
            return Bloom.getInstance();
        }

        @Override
        public @NotNull CHAStrategy getStrategy()
        {
            CHAStrategy strategy = ICHAProfileProvider.super.getStrategy();
            return strategy.with(
                    CHAProfile.class,
                    Strategies.profileMaximumWeightBipartiteMatching()
            );
        }
    }
}
