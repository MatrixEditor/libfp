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
package io.github.libfp.benchmark;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Name implements Comparable<Name>
{

    public static final String defaultType = "";


    public final String type;
    public final String id;
    public final String name;


    public Name(String type, String id, String name)
    {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public static Name parse(String fileName)
    {
        String n = fileName;
        if (fileName.endsWith(".apk")) {
            n = fileName.substring(0, fileName.length() - 4);
        }
        String[] parts = n.split("-");

        if (parts.length < 3) {
            return new Name(defaultType, parts[0], parts[1]);
        } else {
            // last two parts are name and number
            String name = parts[parts.length - 1];
            String number = parts[parts.length - 2];
            return new Name(
                    String.join("-", Arrays.copyOf(parts, parts.length - 2)),
                    number, name);

        }
    }


    public boolean isDefaultType()
    {
        return type.equals(defaultType);
    }

    @Override
    public String toString()
    {
        if (isDefaultType()) {
            return id + "-" + name;
        }
        return type + "-" + id + "-" + name;
    }

    @Override
    public int compareTo(@NotNull Name name)
    {
        return name.toString().compareTo(toString());
    }
}
