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

import io.github.libfp.benchmark.DataSet;
import io.github.libfp.cli.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Verify
{

    public static final String USAGE = """
            Usage:
                verify <dataset> <app>
            """;

    public static void main(String[] args) throws Exception
    {
        if (args.length < 2) {
            System.out.println(USAGE);
            return;
        }

        DataSet dataSet = Config.getDataSet(args[0], null);
        String app = args[1];

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
                dataSet.getLibraryDirectory());


        List<String> libraries = dataSet.groundTruth().getLibraries(app);
        if (libraries == null || libraries.isEmpty()) {
            System.out.printf("""
                            \u001B[31m[E] No libraries found for app '%s'\u001B[0m
                            """,
                    app);
            System.exit(1);
        }

        System.out.printf("""
                        \u001B[36mLibraries:\u001B[0m
                            | app: \u001B[94m'%s'\u001B[0m
                            | libraries: \u001B[92m[
                                %s
                              ]\u001B[0m
                        """,
                app,
                String.join(",\n        ", libraries));

        System.out.println("\u001B[36mVerifying libraries:\u001B[0m");
        File[] libs = new File(dataSet.getLibraryDirectory()).listFiles();
        List<String> target = new ArrayList<>();
        for (String library : libraries) {
            Arrays.stream(Objects.requireNonNull(libs))
                    .filter(o -> o.getName().startsWith(library))
                    .findFirst()
                    .ifPresentOrElse(f -> {
                        System.out.println(
                                " -> \u001B[92m" + library +
                                        "\u001B[0m: \u001B[92mOK\u001B[0m");
                    }, () -> {
                        System.out.println(
                                " -> \u001B[31m" + library +
                                        "\u001B[0m: \u001B[31mFAIL\u001B[0m");
                        target.add(library);
                    });

        }
        try (FileOutputStream f = new FileOutputStream("./target.txt")) {
            f.write(String.join("\n", target).getBytes());
        }
    }

}
