/*
 * MIT License
 *
 * Copyright (c) 2024 MatrixEditor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.libfp.cli.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.benchmark.*;
import io.github.libfp.cli.CommonOptions;
import io.github.libfp.cli.Config;
import io.github.libfp.cli.ICmd;
import io.github.libfp.cli.TestSuiteConfig;
import io.github.libfp.cli.json.TestResultSerializer;
import io.github.libfp.cli.xml.XMLProfile;
import io.github.libfp.cli.xml.XMLProfileProvider;
import io.github.libfp.threshold.SimpleThresholdConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public final class Benchmark implements ICmd
{

    @ParametersDelegate
    public CommonOptions options = new CommonOptions();

    @Parameter(names = {"-D",
            "--dataset"}, description = "Path to the dataset configuration file (.conf)", required = true)
    public String datasetConfPath;

    @Parameter(names = {"-p",
            "--profile"}, description = "The profile path (XML)", required = true)
    public String profilePath;

    @Parameter(names = {"-o", "--output"}, description = "The output json file")
    public String output;

    @ParametersDelegate
    public TestSuiteConfig testSuiteConfig = new TestSuiteConfig();

    @Parameter(names = {
            "-A", "--all-results"
    }, description = "Enables output of all results (only if JSON output is active)")
    public boolean allResults = false;

    @Parameter(names = {
            "-nR", "--no-results"
    }, description = "Disables output of results (only if JSON output is active)")
    public boolean noResults = false;

    @Parameter(names = {
            "-roc"
    }, description = "Enables output of ROC curve (only if JSON output is active)",
            variableArity = true)
    public List<Double> rocThresholds = new LinkedList<>();

    @Parameter(names = {
            "-roc-level"
    }, description = "Specifies the ROC curve level (only if JSON output is active)")
    public String rocLevel = TestResult.class.getName();

    @Parameter(names = {"-a",
            "--app"}, description = "The app domain", required = true)
    public String app;

    @Parameter(description = "[ case-type | '_' ] ...", required = true)
    public List<String> cases;

    public static void main(String[] args) throws Exception
    {
        ICmd.run(Benchmark.class, args);
    }

    @Override
    public CommonOptions getOptions()
    {
        return options;
    }

    @Override
    public void run() throws Exception
    {
        XMLProfile profile = XMLProfile.parse(profilePath);
        DataSet dataSet = Config.getDataSet(datasetConfPath, profile);

        XMLProfileProvider profileProvider = new XMLProfileProvider(
                profile);
        BasicTestSuite<AnalysisScope> testSuite = new BasicTestSuite<>(
                profileProvider,
                dataSet,
                testSuiteConfig.verify,
                testSuiteConfig.strict);

        testSuite.parallelProcessing = !testSuiteConfig.noParallel;
        testSuite.wrapProcesssing = !options.noVerboseOutput;
        testSuite.forceGC = testSuiteConfig.forceGC;
        testSuite.displayTasks = testSuiteConfig.taskOutput;
        testSuite.cacheProfiles = !testSuiteConfig.noCache;

        options.println(profile.toANSIString(profileProvider));

        BenchmarkResult benchmarkResult = null;
        if (cases.contains("-*")) {
            // benchmark all cases
            options.println("\u001B[96mBenchmarking:\u001B[0m");
            benchmarkResult = testSuite.benchmark(app, profile.config);
        } else {
            // benchmark all other cases
            String dir = dataSet.getApplicationDirectory(app);
            options.printf(
                    "\u001B[96mBenchmarking: %d case(s)\u001B[0m\n",
                    cases.size());
            testSuite.loadLibraries();
            for (File file : Objects.requireNonNull(
                    new File(dir).listFiles())) {
                if (file.isFile()) {
                    String[] parts = DataSet.parseAppFileName(
                            file.getName());
                    if (cases.contains(parts[0]) ||
                            (parts[0].isEmpty() && cases.contains("_")))
                    {
                        testSuite.benchmark(file, profile.config);
                    }
                }
            }
            benchmarkResult = testSuite.getBenchmarkResult();
        }

        options.println("\n\u001B[96mResults:\u001B[0m");
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(TestResult.class,
                        new TestResultSerializer());

        Gson gson = builder.create();

        Whitelist whitelist = dataSet.groundTruth().getVersionWhitelist(app);


        if (rocThresholds == null || rocThresholds.isEmpty()) {
            rocThresholds = List.of(
                    profile.config.getThreshold(TestResult.class));
        }
        JsonArray root = new JsonArray();
        for (double threshold : rocThresholds) {
            options.println(
                    "\u001B[96m(Threshold):\u001B[0m " + threshold
            );
            profile.config.set(TestResult.class, threshold);
            JsonObject obj = new JsonObject();
            for (String key : benchmarkResult.getTestedAppTypes()) {
                options.println(
                        "  > Type: " + (key.isEmpty() ? "default" : key));
                TestAccuracy accuracy = benchmarkResult.getTestAccuracy(key,
                        threshold,
                        whitelist);


                options.printf(
                        """
                                    | \u001B[1;37mAccuracy:\u001B[0m %.2f
                                        - Precision: %.2f
                                        - Recall: %.2f
                                        - F1-Score: %.2f
                                    
                                    | \u001B[1;37mConfusion Matrix:\u001B[0m
                                             POS     NEG
                                          +-------+-------+
                                        P |       |       |
                                        O | %5d | %5d |
                                        S |       |       |
                                          +-------+-------+
                                        N |       |       |
                                        E | %5d | %5d |
                                        G |       |       |
                                          +-------+-------+
                                    
                                        + FN: %10s (lib isn't reported but in app)
                                        + FP: %10s (lib reported but not in app)
                                        + TN: %10s (lib isn't reported & in not app)
                                        + TP: %10s (lib reported & in app)
                                    
                                    | \u001B[1;37mStatistics:\u001B[0m
                                        - FOR (false omission rate): %.2f
                                        - FDR (false discovery rate): %.2f
                                        - FPR (false positive rate): %.2f
                                        - NPV (false negative rate): %.2f
                                        - Specificity: %.2f
                                    
                                """,
                        accuracy.accuracy(),
                        accuracy.precision(),
                        accuracy.recall(),
                        accuracy.f1score(),

                        accuracy.TP(),
                        accuracy.FP(),
                        accuracy.FN(),
                        accuracy.TN(),

                        accuracy.FN(),
                        accuracy.FP(),
                        accuracy.TN(),
                        accuracy.TP(),

                        accuracy.FOR(),
                        accuracy.FDR(),
                        accuracy.fallOut(),
                        accuracy.NPV(),
                        accuracy.specificity()
                );

                JsonObject data = getJsonObject(accuracy);
                data.addProperty("milliTime",
                        benchmarkResult.getTestResults(key)
                                .stream()
                                .mapToDouble(TestResult::milliTime)
                                .sum());
                data.addProperty("nanoTime",
                        benchmarkResult.getTestResults(key)
                                .stream()
                                .mapToDouble(TestResult::nanoTime)
                                .sum());
                if (!noResults) {
                    data.add("tests",
                            gson.toJsonTree(benchmarkResult.getTestResults(key)
                                    .stream()
                                    .filter(x -> allResults ? true :
                                            x.similarity() >= threshold)
                                    .toList()));
                }
                obj.add(key, data);

            }

            JsonObject config = new JsonObject();
            SimpleThresholdConfig cnf = profile.config;
            cnf.getConfiguration().forEach((cls, value) -> {
                config.addProperty(cls.getName(), value);
            });
            obj.add("config", config);
            root.add(obj);
        }


        if (output == null) {
            return;
        }
        options.println("\n\u001B[96mSaving to\u001B[0m " + output);
        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(gson.toJson(root).getBytes(StandardCharsets.UTF_8));
            fos.flush();
        }
    }

    @Override
    public String getProgramName()
    {
        return "benchmark";
    }

    @NotNull
    private JsonObject getJsonObject(TestAccuracy accuracy)
    {
        JsonObject data = new JsonObject();
        JsonObject matrix = new JsonObject();
        matrix.addProperty("FN", accuracy.FN());
        matrix.addProperty("FP", accuracy.FP());
        matrix.addProperty("TN", accuracy.TN());
        matrix.addProperty("TP", accuracy.TP());
        data.add("matrix", matrix);
        return data;
    }

}
