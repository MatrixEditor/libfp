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
package io.github.libfp.cli.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.libfp.benchmark.TestResult;
import io.github.libfp.profile.Profile;
import io.github.libfp.profile.extensions.Constants;

import java.lang.reflect.Type;

public class TestResultSerializer implements JsonSerializer<TestResult>
{
    @Override
    public JsonElement serialize(TestResult testResult, Type type,
                                 JsonSerializationContext jsonSerializationContext)
    {
        JsonObject object = new JsonObject();

        JsonObject status = new JsonObject();
        TestResult.Status s = testResult.status();
        status.addProperty("value", s.getClass().getSimpleName());
        if (s instanceof TestResult.Failure) {
            status.addProperty("message",
                    ((TestResult.Failure) s).cause.getMessage());
        }

        object.add("status", status);
        object.addProperty("similarity",
                !Double.isNaN(testResult.similarity()) ?
                        testResult.similarity() : 0.0);
        object.addProperty("time", testResult.milliTime());

        Profile profile = testResult.lib();
        if (profile.getManager().hasExtension(Constants.class)) {
            Constants.Literal version = profile.getManager()
                    .getExtension(Constants.class).get("name");
            if (version != null) {
                object.addProperty("name", version.value);
            }
        }
        return object;
    }

}
