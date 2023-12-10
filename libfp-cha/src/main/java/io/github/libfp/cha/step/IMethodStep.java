package io.github.libfp.cha.step;//@date 25.10.2023

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.MethodProfile;
import io.github.libfp.profile.features.IStep;

/**
 * The <code>IMethodStep</code> interface represents a step in a processing
 * pipeline that operates on objects of type {@link IMethod} and managed
 * profiles of type {@link MethodProfile}.
 */
public interface IMethodStep extends IStep<IMethod, MethodProfile>
{
}
