package io.github.example;  

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import io.github.libfp.cha.*;
import io.github.libfp.cha.extension.ClassProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.impl.CallSideNormalizer;
import io.github.libfp.impl.UniqueFuzzyILFactory;
import io.github.libfp.impl.bloom.Bloom;
import io.github.libfp.profile.bytecode.BytecodeNormalizer;
import io.github.libfp.profile.extensions.Descriptors;
import io.github.libfp.profile.extensions.ProfileInfo;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.profile.manager.ProfileManager;
import io.github.libfp.profile.manager.RetentionPolicy;
import io.github.libfp.threshold.IThresholdConfig;
import io.github.libfp.threshold.SimpleThresholdConfig;
import io.github.libfp.util.AnalysisScopeBuilder;

import java.io.File;

public class CustomProfileExample
{

    public static void main(String[] args) throws Exception
    {
        // 0. Choose an appropriate bytecode normalizer and descriptor
        // creator. Note that you won't need a bytecode normalizer for
        // certain profiles.
        ILFactory ilFactory = new UniqueFuzzyILFactory();
        BytecodeNormalizer normalizer = new CallSideNormalizer(ilFactory);

        // 1. Prepare the ProfileManager: this will be the heart of your
        // profile, where its components are specified. The manager is
        // designed to be extensible, so you can define your own extensions
        // that will be serialized.
        // In this case, we will construct a profile with method profiles
        // storing bloom filters and basic (extended) class profiles.
        ProfileManager manager =
                new ProfileManager(ilFactory, normalizer)
                        .with(new ProfileInfo())
                        .with(new Descriptors())
                        .with(new CustomExtension(), RetentionPolicy.SOURCE)
                        .with(new MethodProfileList(MethodProfile::new))
                        .with(new ClassProfileList())
                        // To exclude fine-grained profile data, use
                        // "onlySource". This might be helpful if you only want
                        // to include data of certain layers.
                        .onlySource(MethodProfileList.class);

        // 1.5 Optionally prepare blueprints to insert additional attributes to
        // each profile type. (use IIntegration classes)
        ICHAIntegration integration = Bloom.getInstance();
        integration.update(manager, MethodProfile::new);
        integration.update(manager, ClassProfile::new);

        // 2. Set up the Strategy: Each strategy defines how a profile will
        // be constructed and how the similarity will be calculated.
        // Here, we need a processor for bloom method profiles and basic class
        // profiles (actually, BasicClassProfile and BasicMethodProfile both
        //  don't need any processor).
        CHAStrategy strategy = new CHAStrategy()
                .with(CHAProfile.class, new CHAProfileStep())
                // this step will be executed whenever an instance of
                // MethodProfile has been created.
                .with(MethodProfile.class, new CustomMethodStep())
                // This call re-assigns the similarity strategy for
                // MethodProfiles to our custom strategy. (Map.put
                // behavior)
                .with(MethodProfile.class, new CustomMethodStrategy());

        // layer applies both ISimilarityStrategy and IStep, where ...step()
        // only adds an IStep instance (use integration again)
        integration.setClassLayer(strategy);
        integration.setMethodLayer(strategy);

        // NOTE: Step 0-3 can be wrapped using a custom implementation of
        // IProfileContext.

        // 3. Build the AnalysisScope: Every analysis scope must contain the
        // android JAR as a framework reference. Using an
        // AnalysisScopeBuilder, the build-process is simplified.
        final AnalysisScope libScope = new AnalysisScopeBuilder()
                .withAndroidFramework("android.jar")
                .with("library.aar|jar")
                .build();

        // 4. Create the profile instance: This instantiation will result in
        // the collection of all profile-related information and might take
        // some time.
        CHAProfileFactory factory = new CHAProfileFactory();
        CHAProfile profile = factory.build(libScope, manager, strategy);

        // 5. Store the profile
        profile.saveTo("library.fp");


        // Further notes:
        //    - Make sure to call reset() every time you use a ProfileManager
        //    - To import a saved profile, you will need a ProfileManager
        //    with the same types of extensions.
        //    - The same applies to the strategy. It is necessary to provide
        //    a strategy that covers all used profile types.
        manager.reset();
        final File profileLocation = new File("library.fp");
        CHAProfile importedProfile = factory.load(profileLocation, manager,
                strategy);

        //   - To compute the similarity of two profiles, the Strategy
        //   instance is essential. Additionally, you can provide a
        //   configuration to certain thresholds used in the calculation.
        IThresholdConfig config = new SimpleThresholdConfig()
                .set(CHAProfile.class, 0.8);

        // the returned value will be in a range of 0 (no match) to 1
        // (absolute match).
        double result = profile.similarityTo(importedProfile, config);
    }
}
