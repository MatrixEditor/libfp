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

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.FieldProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.cha.extension.PackageProfileList;
import io.github.libfp.cha.step.*;
import io.github.libfp.profile.features.IProfileStep;
import io.github.libfp.profile.features.IStep;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.util.TypeNames;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * A default implementation of the {@link IProfileStep} interface that:
 * <ul>
 *     <li>Automatically creates a package tree.</li>
 *     <li>Extracts default class information and extended class
 *     information defined in {@link ExtendedClassProfile}.</li>
 *     <li>Invokes {@link IClassStep}, {@link IMethodStep}, {@link IFieldStep},
 *     and {@link IPackageStep} during the processing.</li>
 * </ul>
 */
public class CHAProfileStep implements ICHAProfileStep<CHAProfile>
{

    /**
     * Process the input class hierarchy and generate the output profile.
     *
     * @param ref    The input class hierarchy to process.
     * @param target The output profile to populate with processed data.
     */
    @Override
    public void process(
            @NotNull IClassHierarchy ref,
            @NotNull CHAProfile target)
    {
        StreamSupport.stream(ref.spliterator(), false)
                .filter(TypeNames::isAppScope)
                .forEach(c -> processClass(c, target));

        ProfileManager manager = target.getManager();
        if (manager.hasExtension(PackageProfileList.class)) {
            for (final PackageProfile profile :
                    manager.getExtension(PackageProfileList.class)) {
                processPackage(ref, profile, target);
            }
        }
    }

    public void processClass(@NotNull IClass iClass, @NotNull CHAProfile target)
    {
        ProfileManager manager = target.getManager();

        // Get all feature extractors for class, field and method profiles
        final Collection<IStep<IClass, ClassProfile>> extractors =
                manager.getStrategy()
                        .getFeatureExtractors(ClassProfile.class);

        final Collection<IStep<IMethod, MethodProfile>> methodExtractors =
                manager.getStrategy()
                        .getFeatureExtractors(MethodProfile.class);

        ClassProfileList profiles = target.getClasses();
        final int index = profiles.addClass(iClass);

        ClassProfile profile = profiles.get(index);
        extractors.stream()
                .filter(extractor -> extractor.test(profile.getClass()))
                .forEach(extractor -> extractor.process(iClass, profile));

        // Process methods if necessary
        processMethods(iClass, profile, methodExtractors, target);

        // process fields if necessary
        if (manager.hasExtension(FieldProfileList.class)) {
            processFields(iClass, profile, target);
        }

        if (manager.hasExtension(PackageProfileList.class)) {
            processClassPackages(iClass, index, target);
        }
    }

    /**
     * Process a package and its associated profiles.
     *
     * @param hierarchy The class hierarchy to process.
     * @param profile   The PackageProfile to populate.
     */
    public void processPackage(
            final IClassHierarchy hierarchy,
            @NotNull PackageProfile profile, @NotNull CHAProfile target)
    {
        final Collection<IStep<IClassHierarchy, PackageProfile>> extractors =
                target.getManager()
                        .getStrategy()
                        .getFeatureExtractors(PackageProfile.class);

        extractors.stream()
                .filter(extractor -> extractor.test(profile.getClass()))
                .forEach(extractor -> extractor.process(hierarchy, profile));
    }

    ///////////////////////////////////////////////////////////////////////////
    // private API
    ///////////////////////////////////////////////////////////////////////////
    private void processFields(
            @NotNull IClass iClass,
            ClassProfile profile,
            @NotNull CHAProfile target)
    {
        final Collection<IStep<IField, FieldProfile>> fieldExtractors =
                target.getManager()
                        .getStrategy()
                        .getFeatureExtractors(FieldProfile.class);

        FieldProfileList fieldProfiles = target.getFields();
        if (profile instanceof ExtendedClassProfile ecp) {
            ecp.fieldsStartIndex = fieldProfiles.size();
            ecp.staticFieldCount = iClass.getDeclaredStaticFields().size();
            ecp.instanceFieldCount = iClass.getDeclaredInstanceFields().size();
        }

        // IMPORTANT: instance fields must come first (due to specification of
        // ExtendedClassProfile).
        processFields(
                fieldExtractors,
                iClass.getDeclaredInstanceFields(),
                fieldProfiles
        );
        processFields(
                fieldExtractors,
                iClass.getDeclaredStaticFields(),
                fieldProfiles
        );
    }

    private void processFields(
            @NotNull Collection<IStep<IField, FieldProfile>> fieldExtractors,
            @NotNull Collection<IField> fields,
            @NotNull FieldProfileList fieldProfiles)
    {
        for (final IField iField : fields) {
            final int fieldIndex = fieldProfiles.addField(iField);
            FieldProfile fieldProfile = fieldProfiles.get(fieldIndex);

            fieldExtractors
                    .stream()
                    .filter(x -> x.test(fieldProfile.getClass()))
                    .forEach(x -> x.process(iField, fieldProfile));
        }
    }

    private void processMethods(
            @NotNull IClass iClass,
            ClassProfile profile,
            @NotNull Collection<IStep<IMethod, MethodProfile>> methodExtractors,
            @NotNull CHAProfile target)
    {
        final Collection<? extends IMethod> staticMethods =
                getStaticMethods(iClass);

        final Collection<? extends IMethod> instanceMethods =
                getInstanceMethods(iClass);

        ExtendedClassProfile ecp = null;
        if (profile instanceof ExtendedClassProfile) {
            ecp = (ExtendedClassProfile) profile;
            ecp.instanceMethodCount = instanceMethods.size();
            ecp.staticMethodCount = staticMethods.size();
        }

        if (!profile.getManager().hasExtension(MethodProfileList.class)) {
            return;
        }

        MethodProfileList methodProfiles = target.getMethods();
        if (ecp != null) {
            ecp.methodsStartIndex = methodProfiles.size();
        }

        if (instanceMethods.size() + staticMethods.size() > 0) {
            for (final IMethod iMethod : instanceMethods) {
                processMethod(methodExtractors, iMethod, methodProfiles);
            }
            for (final IMethod iMethod : staticMethods) {
                processMethod(methodExtractors, iMethod, methodProfiles);
            }

        }
    }

    @NotNull
    protected Collection<? extends IMethod> getInstanceMethods(
            @NotNull IClass iClass)
    {
        return CHAUtilities.getInstanceMethods(iClass, false);
    }

    @NotNull
    protected Collection<? extends IMethod> getStaticMethods(
            @NotNull IClass iClass)
    {
        return CHAUtilities.getStaticMethods(iClass, false);
    }

    private void processClassPackages(
            @NotNull IClass iClass,
            int index,
            @NotNull CHAProfile target)
    {
        PackageProfileList packageProfiles = target.getPackages();
        final List<String> packages =
                TypeNames.getPackages(iClass.getName());

        // building a package tree here
        PackageProfile current = packageProfiles.getRootPackage();
        int currentIndex = 0; // root index
        for (final String packageName : packages) {
            final int packageProfileIndex =
                    packageProfiles.addPackage(packageName);

            PackageProfile packageProfile =
                    packageProfiles.get(packageProfileIndex);
            if (current != null) {
                packageProfile.parent = currentIndex;
                current.addPackage(packageProfileIndex);
                currentIndex = packageProfileIndex;
            }
            current = packageProfile;
        }

        if (current != null) {
            current.addClass(index);
            processPackage(iClass, current);
        }
    }

    private void processMethod(
            @NotNull Collection<IStep<IMethod, MethodProfile>> methodExtractors,
            @NotNull IMethod iMethod,
            @NotNull MethodProfileList methodProfiles)
    {
        final int methodIndex = methodProfiles.addMethod(iMethod);

        MethodProfile method = methodProfiles.get(methodIndex);
        methodExtractors
                .stream()
                .filter(extractor -> extractor.test(method.getClass()))
                .forEach(extractor -> extractor.process(iMethod,
                        method));
    }

    protected void processPackage(
            @NotNull IClass iClass,
            @NotNull PackageProfile packageProfile)
    {
    }
}
