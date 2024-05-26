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
package io.github.libfp.impl.cra;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.CodeScanner;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeReference;
import io.github.libfp.cha.CHAProfile;
import io.github.libfp.cha.CHAUtilities;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.step.ICHAProfileStep;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.StreamSupport;

@ApiStatus.Experimental
public class ClassRelationStep
        implements ICHAProfileStep<CHAProfile>
{
    @Override
    public int priority()
    {
        return 1;
    }

    @Override
    public void process(
            @NotNull IClassHierarchy ref,
            @NotNull CHAProfile target)
    {
        ClassProfileList classProfiles = target.getClasses();

        StreamSupport.stream(ref.spliterator(), false)
                .filter(TypeNames::isAppScope)
                .forEach(x -> process(x,
                        classProfiles.getClassProfile(x)));
    }

    public void process(@Nullable IClass ref, @Nullable ClassProfile target)
    {
        if (ref == null || target == null) {
            return;
        }

        CRClassProfile profile = (CRClassProfile) target;

        ref.getDeclaredMethods()
                .stream()
                .filter(CHAUtilities.getNonCompilerGeneratedMethodFilter())
                .filter(m -> !m.isAbstract())
                .map(m -> applyTypeReferences(m, ref, profile))
                .map(this::getCallSiteReferences)
                .forEach(r -> applyCallSites(r, ref, profile));

        ref.getDeclaredStaticFields()
                .forEach(f -> addRef(ref, profile, f.getFieldTypeReference(),
                        ClassRelation.REFERENCE));

        ref.getDeclaredInstanceFields()
                .forEach(f -> addRef(ref, profile, f.getFieldTypeReference(),
                        ClassRelation.REFERENCE));

        ref.getAllImplementedInterfaces()
                .forEach(c -> addClassRef(ref, profile,
                        ClassRelation.INHERITANCE,
                        c));
    }

    private Collection<CallSiteReference> getCallSiteReferences(
            @NotNull IMethod method)
    {
        try {
            return CodeScanner.getCallSites(method);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void applyCallSites(
            @NotNull Collection<CallSiteReference> refs,
            @NotNull IClass type, @NotNull CRClassProfile profile)
    {
        for (final CallSiteReference reference : refs) {
            TypeReference typeReference =
                    reference.getDeclaredTarget().getDeclaringClass();
            addRef(type, profile, typeReference, ClassRelation.CALL);
        }
    }

    @Contract("_, _, _ -> param1")
    private @NotNull IMethod applyTypeReferences(
            @NotNull IMethod method,
            @NotNull IClass type,
            @NotNull CRClassProfile profile)
    {
        for (int i = method.isStatic() ? 0 : 1;
             i < method.getNumberOfParameters(); i++) {
            TypeReference typeReference =
                    method.getParameterType(i);

            addRef(type, profile, typeReference, ClassRelation.REFERENCE);
        }
        return method;
    }

    private void addRef(
            @NotNull IClass type,
            @NotNull CRClassProfile profile,
            @NotNull TypeReference typeReference,
            @NotNull ClassRelation classRelation)
    {
        if (typeReference.isArrayType()) {
            typeReference = typeReference.getArrayElementType();
        }

        IClass target = TypeNames.lookup(type.getClassHierarchy(),
                typeReference.getName().toString());
        if (target != null && TypeNames.isAppScope(target)) {
            addClassRef(type, profile, classRelation, target);
        }
    }

    private void addClassRef(
            @NotNull IClass type,
            @NotNull CRClassProfile profile,
            @NotNull ClassRelation classRelation,
            @NotNull IClass target)
    {
        if (target
                .getName()
                .toString()
                .equals(type.getName().toString()))
        {
            // we don't want to include reference loops
            return;
        }

        ClassProfileList classProfiles =
                profile.getManager().getExtension(ClassProfileList.class);

        profile.addRef(
                classRelation,
                classProfiles.getClassProfileIndex(target)
        );
    }
}
