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
import io.github.libfp.profile.IProfileProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code ICHAProfileContext} interface extends {@code IProfileContext} and
 * specifies the type parameter as {@code AnalysisScope}. It provides default
 * implementations for creating a {@code ProfileBuilder}, a
 * {@code ProfileFactory}, and retrieving the default {@code CHAStrategy}.
 *
 * @see CHAProfile
 * @see CHAStrategy
 */
public interface ICHAProfileProvider extends IProfileProvider<AnalysisScope>
{
    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull CHAProfileFactory getProfileFactory()
    {
        return new CHAProfileFactory();
    }

    @Override
    @Nullable ICHAIntegration getIntegration();


    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull CHAStrategy getStrategy()
    {
        CHAStrategy strategy = new CHAStrategy();
        ICHAIntegration integration = getIntegration();
        if (integration != null) {
            integration.setPolicies(strategy);
            integration.addProfileStep(strategy);
            integration.setProfileStrategy(strategy);

            // layers
            integration.setPackageLayer(strategy);
            integration.setClassLayer(strategy);
            integration.setMethodLayer(strategy);
            integration.setFieldLayer(strategy);
        }
        return strategy;
    }
}

