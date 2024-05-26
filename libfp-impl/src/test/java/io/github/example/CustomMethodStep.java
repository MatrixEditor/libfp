package io.github.example;  

import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.cha.MethodProfile;
import org.jetbrains.annotations.NotNull;

public class CustomMethodStep implements IMethodStep
{

    @Override
    public @NotNull Class<? extends ManagedProfile> targetProfileClass()
    {
        // our target class type - (TIP) override test(...) to
        // accept more than one profile class.
        return MethodProfile.class;
    }

    @Override
    public void process(IMethod ref, @NotNull MethodProfile target)
    {
        // Query our custom extension using its class.
        final CustomExtension extension = target
                .getManager()
                .getExtension(CustomExtension.class);

        // apply program logic
    }
}
