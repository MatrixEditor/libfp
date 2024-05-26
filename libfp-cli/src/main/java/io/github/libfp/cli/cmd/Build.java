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
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.benchmark.BasicTestSuite;
import io.github.libfp.benchmark.DataSet;
import io.github.libfp.cli.CommonOptions;
import io.github.libfp.cli.ICmd;
import io.github.libfp.cli.TestSuiteConfig;
import io.github.libfp.cli.xml.CHAContextFactory;
import io.github.libfp.cli.xml.XMLProfile;
import io.github.libfp.cli.xml.XMLProfileProvider;

import java.io.File;

import static io.github.libfp.cli.Config.getDataSet;

public final class Build implements ICmd
{

    @ParametersDelegate
    public CommonOptions options = new CommonOptions();

    @Parameter(names = {"-p",
            "--profile"}, description = "The profile path (XML)", required = true)
    public String profilePath;

    @Parameter(names = {"-D",
            "--dataset"}, description = "Path to the dataset configuration file (.conf)", required = true)
    public String datasetConfPath;

    @Parameter(description = "<app-domain> | '-*'", required = true)
    public String app;

    @ParametersDelegate
    public TestSuiteConfig testSuiteConfig = new TestSuiteConfig();

    public static void main(String[] args) throws Exception
    {
        ICmd.run(Build.class, args);
    }

    @Override
    public CommonOptions getOptions()
    {
        return options;
    }

    public void run() throws Exception
    {
        XMLProfile profile = XMLProfile.parse(profilePath);
        DataSet dataSet = getDataSet(datasetConfPath, profile);
        XMLProfileProvider profileProvider = new XMLProfileProvider(profile);
        BasicTestSuite<AnalysisScope> suite =
                new BasicTestSuite<>(profileProvider, dataSet,
                        testSuiteConfig.verify, testSuiteConfig.strict);

        suite.parallelProcessing = !testSuiteConfig.noParallel;
        suite.wrapProcesssing = !options.noVerboseOutput;
        suite.forceGC = testSuiteConfig.forceGC;


        options.println(profile.toANSIString(profileProvider));
        CHAContextFactory factory = new CHAContextFactory(suite);
        try {
            if (app.equals("-*")) {
                suite.prepareLibraries(testSuiteConfig.overwrite, factory);
            } else {
                options.printf(
                        """
                                \u001B[36mBuilding profiles for app:\u001B[0m
                                  | Application: \u001B[32m%s\u001B[0m
                                """,
                        app);

                File f = new File(dataSet.getApplicationTargetDirectory(app));
                if (!f.exists() && !f.mkdirs()) {
                    throw new Exception("Could not create directory " + f);
                }
                suite.prepareApplications(app, testSuiteConfig.overwrite,
                        factory);
            }
        } catch (Exception e) {
            options.printerr(e);
        }
    }

    @Override
    public String getProgramName()
    {
        return "build";
    }
}
