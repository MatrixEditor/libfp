package io.github.test.hashtree; //@date 30.10.2023

import io.github.libfp.cha.*;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.cha.extension.PackageProfileList;
import io.github.libfp.impl.UniqueFuzzyILFactory;
import io.github.libfp.impl.hashtree.HashTree;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.profile.manager.RetentionPolicy;
import io.github.libfp.similarity.IStrategy;
import io.github.test.ProfileTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class HashTreeTest extends ProfileTest
{


    public HashTreeTest()
    {
        super(
                "demo",
                "003-nodomain.freeyourgadget.gadgetbridge",
                "hashtree",
                CHAProfile::new,
                ".ht.fp",
                "../android.jar");
    }

    @Override
    public @NotNull ProfileManager getManager()
    {
        return ProfileManager
                .getInstance(new UniqueFuzzyILFactory())
                .with(new PackageProfileList())
                .with(new ClassProfileList())
                // discard methods, keep with .RUNTIME
                .with(new MethodProfileList(), RetentionPolicy.SOURCE);
    }

    @Override
    public @NotNull IStrategy<CHAStrategy> getStrategy()
    {
        String algorithm = "MD5";
        return CHAStrategy
                .getDefaultInstance()
                .with(CHAProfile.class, new HashTree.ProfileStep(algorithm))
                .with(ClassProfile.class, new HashTree.ClassStep(algorithm))
                .with(MethodProfile.class, new HashTree.MethodStep(algorithm))
                .with(PackageProfile.class,
                        new HashTree.PackageStep(algorithm));
    }

    @Test
    void doSetup()
    {
        System.out.println("Profiles created!");
    }

}
