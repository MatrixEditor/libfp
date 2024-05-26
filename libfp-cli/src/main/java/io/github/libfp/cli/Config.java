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

import io.github.libfp.benchmark.DataSet;
import io.github.libfp.benchmark.GroundTruth;
import io.github.libfp.cli.xml.XMLProfile;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Config
{


    private Config()
    {
    }

    public static String clean(String value)
    {
        return value != null ? value.replaceAll("^\"+", "")
                .replaceAll("\"+$", "") : null;
    }

    public static boolean isEnabled(String value)
    {
        return clean(value).equalsIgnoreCase("true");
    }

    public static <T> T getInstance(String path, Class<T> tClass,
                                    Class<?>[] argTypes,
                                    Object... args)
    {
        if (path == null) {
            return null;
        }

        String[] nodes = clean(path).split(":");
        try {
            Class<?> cls = Class.forName(nodes[0]);
            if (nodes.length == 1) {
                return tClass.cast(
                        cls.getDeclaredConstructor(argTypes).newInstance(args));
            } else {
                return tClass.cast(cls.getMethod(nodes[1]).invoke(null));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSet getDataSet(String path,
                                     @Nullable XMLProfile xmlProfile)
            throws IOException
    {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        }
        return new DataSet(
                clean(properties.getProperty("base-dir")),
                xmlProfile != null && xmlProfile.target != null ?
                        xmlProfile.target : clean(
                        properties.getProperty("target-dir")),
                clean(properties.getProperty("android-path")),
                xmlProfile != null && xmlProfile.extension != null ?
                        xmlProfile.extension : clean(
                        properties.getProperty("extension")),
                new GroundTruth(clean(properties.getProperty("ground-truth")))
        );
    }
}
