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
package io.github.libfp.models;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.dalvik.classLoader.DexIMethod;
import io.github.libfp.cha.*;
import io.github.libfp.cha.step.IClassStep;
import io.github.libfp.cha.step.IMethodStep;
import io.github.libfp.hash.BloomFilter;
import io.github.libfp.impl.ManhattanDiff;
import io.github.libfp.impl.Strategies;
import io.github.libfp.impl.bloom.Bloom;
import io.github.libfp.impl.bloom.BloomFilterStrategy;
import io.github.libfp.matching.IResultHandler;
import io.github.libfp.profile.Blueprint;
import io.github.libfp.profile.ExtensibleProfile;
import io.github.libfp.profile.ManagedProfile;
import io.github.libfp.profile.features.FeatureVector;
import io.github.libfp.profile.il.ILFactory;
import io.github.libfp.similarity.IStrategy;
import io.github.libfp.threshold.IThresholdConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jf.dexlib2.Format;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <h2>Approach 1: BloomFilter and Method AST Feature Vector</h2>
 * Implementation of the first approach described in the documentation of all
 * approaches. It combines a class-correspondence mechanism using a
 * {@link BloomFilter} and an {@link FeatureVector} on the method level.
 * <p>
 * The XML profile definition is stored in the repository under the
 * {@code profiles/} directory using the name of this class.
 * <h3>Processing Steps</h3>
 * <hr>
 * First, we will generate a {@link BloomFilter} by using the <i>document</i>
 * if a class. Since the document is generated using an {@link ILFactory}, it
 * makes it resilient to <i>identifier renaming</i>. At the same time, we assert
 * that no <i>dead code</i> exists in the <i>document</i>, which makes the
 * approach vulnerable to <code>dead code insertion/deletion</code>.
 * <p>
 * Next, we will generate a {@link FeatureVector} from the code of each method
 * within each class and leave the vector empty if it is an abstract method.
 */
@ApiStatus.Experimental
public class BloomAndASTVector implements ICHAIntegration
{

    // most important: blueprint
    @Override
    public <E extends ExtensibleProfile> void updateBlueprint(
            @NotNull Blueprint<E> blueprint, @NotNull Class<E> type)
    {
        if (type == ExtendedClassProfile.class) {
            Bloom.addToBlueprint(blueprint);
        }
        if (type == MethodProfile.class) {
            blueprint.add("vector", FeatureVector::new);
        }
    }

    // BloomFilter only applies to class-level
    @Override
    public void setClassLayer(CHAStrategy strategy)
    {
        // NOTE: the CHAProfileStep queries only for steps that target the
        // ClassProfile.class even though, this approach uses ExtendedClassProfile
        // objects.
        strategy.with(ClassProfile.class,
                (IClassStep) new Bloom.ClassStrategy());
        strategy.with(ExtendedClassProfile.class, new BloomClassStrategy());

    }

    @Override
    public void addMethodStep(CHAStrategy strategy)
    {
        strategy.with(MethodProfile.class, new MethodASTToVectorStep());
    }

    @Override
    public void setProfileStrategy(IStrategy<?> strategy)
    {
        strategy.with(CHAProfile.class,
                Strategies.maximumWeightBipartiteMatching(
                        new ClassCorrespondenceStrategy(),
                        CHAProfile::getClasses));
    }

    @Override
    public void setMethodStrategy(IStrategy<?> strategy)
    {
        // use manhattan distance for the method level
        strategy.with(MethodProfile.class, new ManhattanDiff<>(true));
    }

    // MethodStep implementation requires a set of dimensions which will
    // be translated into a feature vector. For instance, the dimension
    // VRT (invoke-virtual) together with PAR (parameter) will be converted
    // into the index position defined by the enum ordinal.
    public enum ASTDimension
    {
        // the first dimension refers to virtual method calls
        // - stmt: 'invoke-virtual'
        VRT,

        // the second dimension refers to direct method calls
        // - stmt: 'invoke-direct'
        DIR,

        // the next two dimensions specify simple parameters and
        // local values
        LOC,
        PAR,

        // All following dimensions represent a combination of two
        // vertical features.
        VRT_LOC,
        VRT_PAR,
        DIR_LOC,
        DIR_PAR,

        // The next two dimensions represent a combination of two
        // horizontal features.
        PAR_PAR,
        LOC_LOC
    }

    // The following step will generate a feature vector on the method level
    // using the dimensions from above. It will count all vertical (e.g. VRT
    // invoke and PARameter) and horizontal (e.g. LOCal and PARameter) features.
    public static final class MethodASTToVectorStep
            implements IMethodStep
    {

        public static boolean isFormat35c(Instruction instruction)
        {
            // we are only interested in format35c instructions with
            // up to 5 registers
            return instruction.getOpcode().format == Format.Format35c;
        }

        public static boolean isDirectOrVirtual(Instruction instruction)
        {
            // we are only interested in direct and virtual calls
            Opcode opcode = instruction.getOpcode();
            return opcode == Opcode.INVOKE_DIRECT ||
                    opcode == Opcode.INVOKE_VIRTUAL;
        }

        @Override
        public Class<? extends ManagedProfile> targetProfileClass()
        {
            // default target class
            return MethodProfile.class;
        }

        @Override
        public void process(IMethod ref, MethodProfile target)
        {
            // The feature vector may contain only 0 values for abstract
            // methods or if the method is not implemented, but it will never
            // be null.
            FeatureVector vector = new FeatureVector(
                    (int) ASTDimension.values().length);
            target.put("vector", vector);

            if (ref.isAbstract() || !(ref instanceof DexIMethod)) {
                return;
            }

            // must be a dex-backed method
            Method method = ((DexIMethod) ref).toEncodedMethod();
            MethodImplementation impl = method.getImplementation();
            if (impl == null) {
                return;
            }

            List<Instruction> instructions = new LinkedList<>();
            try {
                impl.getInstructions().forEach(instructions::add);
            } catch (Exception e) {
                // REVISIT: maybe report this exception
                return;
            }

            instructions.stream()
                    .parallel()
                    .filter(BloomAndASTVector.MethodASTToVectorStep::isFormat35c)
                    .filter(BloomAndASTVector.MethodASTToVectorStep::isDirectOrVirtual)
                    .filter(x -> x instanceof FiveRegisterInstruction)
                    .map(x -> (FiveRegisterInstruction) x)
                    .forEach(x -> visit(x, vector, impl));
        }

        private void visit(FiveRegisterInstruction x, FeatureVector vector,
                           MethodImplementation impl)
        {
            int methodRegisters = impl.getRegisterCount();
            int instructionRegisters = x.getRegisterCount();

            ASTDimension dim = ASTDimension.DIR;
            if (x.getOpcode() == Opcode.INVOKE_VIRTUAL) {
                dim = ASTDimension.VRT;
            }
            // increase the initial dimension
            vector.inc(dim.ordinal());

            for (int i = 0; i < instructionRegisters; i++) {
                int reg = switch (i) {
                    case 0 -> x.getRegisterC();
                    case 1 -> x.getRegisterD();
                    case 2 -> x.getRegisterE();
                    case 3 -> x.getRegisterF();
                    case 4 -> x.getRegisterG();
                    default -> throw new RuntimeException(
                            "Unexpected register count: " +
                                    instructionRegisters);
                };

                ASTDimension type =
                        (reg >= instructionRegisters - methodRegisters)
                                ? ASTDimension.PAR
                                : ASTDimension.LOC;

                // parameter/local increment
                vector.inc(type.ordinal());
                // type + type dimension increment (horizontal feature)
                vector.inc(ASTDimension.valueOf(type.name() + "_" + type.name())
                        .ordinal());
                // type + dim dimension increment (vertical feature)
                vector.inc(ASTDimension.valueOf(dim.name() + "_" + type.name())
                        .ordinal());
            }
        }
    }

    public static final class ClassCorrespondenceStrategy
            implements IResultHandler<ClassProfile, DefaultWeightedEdge>
    {

        @Override

        public double apply(
                MatchingAlgorithm.@NotNull Matching<ClassProfile, DefaultWeightedEdge> matching,
                @NotNull Collection<ClassProfile> app,
                @NotNull Collection<ClassProfile> lib,
                @NotNull IThresholdConfig config)
        {

            Set<DefaultWeightedEdge> matches = matching.getEdges();
            if (matches.isEmpty()) {
                return 0.0;
            }

            double score = 0.0;
            for (DefaultWeightedEdge edge : matches) {
                ExtendedClassProfile libProfile = (ExtendedClassProfile) matching.getGraph()
                        .getEdgeSource(edge);
                ExtendedClassProfile appProfile = (ExtendedClassProfile) matching.getGraph()
                        .getEdgeTarget(edge);

                try {
                    score += Strategies.maximumWeightBipartiteMatching(
                                    ExtendedClassProfile::getMethods)
                            .similarityOf(appProfile, libProfile, config);
                } catch (Exception e) {
                    e.printStackTrace();
                    // REVISIT: maybe report this exception
                }
            }
            return score / lib.size();
        }
    }

    // This class is just for clarification of the underlying similarity
    // strategy
    public static final class BloomClassStrategy
            extends BloomFilterStrategy<ExtendedClassProfile>
    {

        @Override
        public double similarityOf(@NotNull ExtendedClassProfile app,
                                   @NotNull ExtendedClassProfile lib,
                                   IThresholdConfig config)
        {
            if (!app.getDescriptor().equals(lib.getDescriptor())) {
                return 0;
            }

            BloomFilter appFilter = Bloom.getFilter(app);
            BloomFilter libFilter = Bloom.getFilter(lib);
            if (!libFilter.isSuperSetOf(appFilter)) {
                return 0.0;
            }

            if (app.getMethodCount() < lib.getMethodCount()) {
                /// we assert no dead code removal or code optimizations
                return 0.0;
            }
            return (double) appFilter.entries() / libFilter.entries();
        }
    }
}
