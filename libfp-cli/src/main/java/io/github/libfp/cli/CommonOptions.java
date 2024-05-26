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
package io.github.libfp.cli;

import com.beust.jcommander.Parameter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CommonOptions
{

    @Parameter(names = {"--help",
            "-h"}, help = true, description = "Displays this help message")
    public boolean help = false;

    @Parameter(names = {"-m",
            "--mute"}, description = "Disables console output")
    public boolean noVerboseOutput = false;

    public void println(String msg)
    {
        if (!noVerboseOutput) {
            System.out.println(msg);
        }
    }

    public void printf(String format, Object... args)
    {
        if (!noVerboseOutput) {
            System.out.printf(format, args);
        }
    }

    public void printerr(Exception e)
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            e.printStackTrace(new PrintStream(out));
            out.flush();
            String trace = out.toString();
            System.out.printf("\u001B[31m%s\u001B[0m\n", trace);
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
}
