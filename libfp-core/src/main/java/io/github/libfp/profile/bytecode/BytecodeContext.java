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
package io.github.libfp.profile.bytecode;

import com.ibm.wala.ipa.cha.IClassHierarchy;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * The <code>IBytecodeContext</code> record represents a context for bytecode
 * normalization, specifically designed for a given class hierarchy.
 *
 * <p>
 * An <code>IBytecodeContext</code> is designed to encapsulate the class
 * hierarchy information required for normalizing bytecode instructions. It
 * provides a context that can be used during the normalization process to
 * access the class hierarchy.
 * </p>
 */
@ApiStatus.Experimental
public class BytecodeContext
{
    private final IClassHierarchy hierarchy;

    /**
     * @param hierarchy The class hierarchy associated with the bytecode
     *                  normalization context.
     */
    public BytecodeContext(IClassHierarchy hierarchy)
    {
        this.hierarchy = hierarchy;
    }

    public IClassHierarchy hierarchy()
    {
        return hierarchy;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BytecodeContext) obj;
        return Objects.equals(this.hierarchy, that.hierarchy);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hierarchy);
    }

    @Override
    public String toString()
    {
        return "IBytecodeContext[" +
                "hierarchy=" + hierarchy + ']';
    }

}

