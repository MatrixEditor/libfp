package io.github.libfp.cha.step;//@date 25.10.2023

import com.ibm.wala.ipa.cha.IClassHierarchy;
import io.github.libfp.cha.PackageProfile;
import io.github.libfp.profile.features.IStep;

/**
 * The <code>IPackageStep</code> interface represents a step in a processing
 * pipeline that operates on objects of type {@link IClassHierarchy} and managed
 * profiles of type {@link PackageProfile}.
 */
public interface IPackageStep extends IStep<IClassHierarchy, PackageProfile>
{
}

