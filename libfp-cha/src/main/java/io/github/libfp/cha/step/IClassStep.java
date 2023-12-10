package io.github.libfp.cha.step;//@date 25.10.2023

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.cha.ClassProfile;
import io.github.libfp.profile.features.IStep;

/**
 * The <code>IClassStep</code> interface represents a step in a processing
 * pipeline that operates on objects of type {@link IClass} and managed profiles
 * of type {@link ClassProfile}.
 */
public interface IClassStep extends IStep<IClass, ClassProfile>
{

    @Override
    void process(IClass ref, ClassProfile target);
}

