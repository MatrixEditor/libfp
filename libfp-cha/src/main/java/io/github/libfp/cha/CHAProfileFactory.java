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
package io.github.libfp.cha;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.profile.ProfileFactory;
import io.github.libfp.profile.features.IStep;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.similarity.IStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collection;

public class CHAProfileFactory extends ProfileFactory<AnalysisScope>
{
    @Override
    public CHAProfile load(@NotNull File file, @NotNull ProfileManager manager,
                           @NotNull IStrategy<?> strategy) throws IOException
    {
        CHAProfile profile = new CHAProfile(manager);
        profile.getManager().setStrategy(strategy);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            profile.readExternal(new DataInputStream(fis));
        }

        return profile;
    }

    @Override
    public CHAProfile build(@NotNull AnalysisScope context,
                            @NotNull ProfileManager manager,
                            @NotNull IStrategy<?> strategy) throws Exception
    {
        CHAProfile profile = new CHAProfile(manager);
        profile.getManager().setStrategy(strategy);
        final IClassHierarchy hierarchy =
                ClassHierarchyFactory.makeWithRoot(context);

        final Collection<IStep<IClassHierarchy, CHAProfile>> extractors =
                profile.getManager().getStrategy()
                        .getFeatureExtractors(CHAProfile.class);

        for (final IStep<IClassHierarchy, CHAProfile> step : extractors) {
            if (step.test(profile.getClass())) {
                step.process(hierarchy, profile);
            }
        }
        return profile;
    }
}
