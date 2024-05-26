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

public class TestSuiteConfig
{

    @Parameter(names = "--strict", description = "Enables strict mode on dataset verification")
    public boolean strict = false;

    @Parameter(names = "--verify", description = "Verifies the dataset")
    public boolean verify = false;

    @Parameter(names = "--no-parallel", description = "Disables parallel execution")
    public boolean noParallel = false;

    @Parameter(names = {"-f",
            "--force"}, description = "Forces overwriting existing files")
    public boolean overwrite = false;

    @Parameter(names = {
            "--gc"}, description = "Enables garbage collection at the end of each test or profile creation")
    public boolean forceGC = false;

    @Parameter(names = {
            "--tasks"}, description = "Enables more verbose output on each task")
    public boolean taskOutput = false;

    @Parameter(names = {
            "--no-cache"}, description = "Disables caching of profiles")
    public boolean noCache = false;
}
