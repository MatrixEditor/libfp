package io.github.libfp.cha.step;//@date 27.10.2023

import com.ibm.wala.classLoader.IField;
import io.github.libfp.cha.FieldProfile;
import io.github.libfp.profile.features.IStep;

public interface IFieldStep
        extends IStep<IField, FieldProfile>
{
    @Override
    void process(IField ref, FieldProfile target);
}
