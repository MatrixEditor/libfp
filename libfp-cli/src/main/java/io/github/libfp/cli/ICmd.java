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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.jgrapht.util.SupplierUtil;

/**
 * A simple abstraction layer to support multiple CLI classes to use the same
 * entry point.
 *
 * @see io.github.libfp.cli.cmd.Build
 */
public interface ICmd
{

    /**
     * Executes a single command by first parsing the arguments and then
     * executing it.
     *
     * @param type the command type to execute
     * @throws Exception if an error occurs
     */
    static void run(Class<? extends ICmd> type, String[] args)
            throws Exception
    {
        ICmd obj = SupplierUtil.createSupplier(type).get();
        JCommander cmd = JCommander.newBuilder()
                .addObject(obj)
                .build();

        cmd.setProgramName(obj.getProgramName());
        try {
            cmd.parse(args);
        } catch (ParameterException e) {
            String m = e.getMessage();
            System.out.printf(
                    "\u001B[31m%s. Use --help for detailed list of options.\u001B[0m\n",
                    m);
            return;
        }
        if (obj.getOptions().help) {
            cmd.usage();
            return;
        }

        try {
            obj.run();
        } catch (Exception e) {
            obj.getOptions().printerr(e);
        }
    }

    /**
     * @return the {@link CommonOptions} instance
     */
    CommonOptions getOptions();

    /**
     * Executes the command.
     */
    void run() throws Exception;

    /**
     * @return the program name of this command.
     */
    String getProgramName();
}
