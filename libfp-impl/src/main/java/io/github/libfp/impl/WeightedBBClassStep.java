package io.github.libfp.impl; //@date 28.10.2023

import com.ibm.wala.cfg.ShrikeCFG;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeCTMethod;
import com.ibm.wala.classLoader.ShrikeIRFactory;
import com.ibm.wala.dalvik.classLoader.DexCFG;
import com.ibm.wala.dalvik.classLoader.DexIMethod;
import com.ibm.wala.dalvik.classLoader.DexIRFactory;
import io.github.libfp.cha.CHAUtilities;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.extensions.Constants;
import io.github.libfp.profile.features.IStep;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that implements the {@link IStep} interface to process an input class
 * and generate a target profile with a weight based on the number of nodes in
 * the control flow graphs (CFGs) of the declared methods (number of basic
 * blocks).
 *
 * @param <T> The type of the target profile
 *
 * @implNote This approach was used within {@code LibPecker} to assign
 *         each class a certain weight, which was then used to calculate the
 *         similarity.
 */
public final class WeightedBBClassStep<T extends ExtensibleProfile>
        implements IStep<IClass, T>
{

    private static final DexIRFactory dexFactory = new DexIRFactory();
    private static final ShrikeIRFactory shrikeFactory = new ShrikeIRFactory();

    private final Class<T> type;

    /**
     * Constructs a WeightedBBClassStep with the specified target profile type.
     *
     * @param type The type of the target profile
     */
    public WeightedBBClassStep(Class<T> type)
    {
        this.type = type;
    }

    @Override
    public Class<? extends ManagedProfile> targetProfileClass()
    {
        return type;
    }

    /**
     * Process the input class and populate the target profile with a weight
     * based on the number of nodes in the control flow graphs (CFGs) of
     * declared methods.
     *
     * @param ref    The input class to process.
     * @param target The target profile to populate with the weight.
     */
    @Override
    public void process(@NotNull IClass ref, @NotNull T target)
    {
        final List<? extends IMethod> methods = ref
                .getDeclaredMethods()
                .stream()
                .filter(CHAUtilities.getNonCompilerGeneratedMethodFilter())
                .toList();

        int weight = 0;
        for (final IMethod iMethod : methods) {
            if (iMethod instanceof DexIMethod dexIMethod) {
                // Calculate weight based on the number of nodes in a Dex CFG.
                DexCFG cfg = (DexCFG) dexFactory.makeCFG(dexIMethod, null);
                weight += cfg.getNumberOfNodes();
            } else if (iMethod instanceof ShrikeCTMethod shrikeCTMethod) {
                // Calculate weight based on the number of nodes in a Shrike
                // CFG.
                ShrikeCFG cfg = shrikeFactory.makeCFG(shrikeCTMethod);
                weight += cfg.getNumberOfNodes();
            }
        }
        target.put("weight", new Constants.Numeric(weight));
    }
}
