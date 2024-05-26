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
import io.github.libfp.benchmark.DataSet;
import io.github.libfp.benchmark.GroundTruth;
import io.github.libfp.cli.CommonOptions;
import io.github.libfp.cli.Config;
import io.github.libfp.cli.ICmd;

import java.util.List;

public class Inspect implements ICmd
{

    @ParametersDelegate
    public CommonOptions options = new CommonOptions();

    @Parameter(names = {"-D",
            "--dataset"}, description = "Path to the dataset configuration file (.conf)", required = true)
    public String datasetConfPath;

    @Parameter(description = "<app-domain>", required = true)
    public String app;

    public static void main(String[] args) throws Exception
    {
        ICmd.run(Inspect.class, args);
    }

    @Override
    public CommonOptions getOptions()
    {
        return options;
    }

    @Override
    public void run() throws Exception
    {
        DataSet dataSet = Config.getDataSet(datasetConfPath, null);
        System.out.printf("""
                        \u001B[36mImported Dataset:\u001B[0m
                            | base-dir: \u001B[92m'%s'\u001B[0m
                            | android-jar: \u001B[92m'%s'\u001B[0m
                            | application-dir: \u001B[92m'%s'\u001B[0m
                            | lib-dir: \u001B[92m'%s'\u001B[0m
                        """,
                dataSet.datasetBaseDirectory(),
                dataSet.frameworkPath(),
                dataSet.getApplicationDirectory(),
                dataSet.getLibraryDirectory()
        );
        GroundTruth groundTruth = dataSet.groundTruth();
        List<String> libraries = groundTruth.getLibraries(app);

        System.out.printf("""
                        \u001B[36mLibraries:\u001B[0m
                            | app: \u001B[94m'%s'\u001B[0m
                            | libraries: \u001B[92m[
                                %s
                              ]\u001B[0m
                        """,
                app,
                String.join(",\n        ", libraries));
    }

    @Override
    public String getProgramName()
    {
        return "inspect";
    }
}
