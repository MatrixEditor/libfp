package io.github.libfp.impl; //@date 23.10.2023

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import io.github.libfp.Descriptor;
import io.github.libfp.profile.il.ILContext;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * The {@code UniqueFuzzyILFactory} class extends ILFactory and is responsible
 * for generating unique fuzzy descriptors for methods and classes.
 */
public class UniqueFuzzyILFactory extends BasicFuzzyILFactory
{

    /**
     * The UniqueILContext class is an inner class that extends ILContext and is
     * used for generating unique descriptors with sequence numbers.
     */
    public static final class UniqueILContext extends ILContext
    {

        /**
         * Creates a UniqueILContext for the given declaring class.
         *
         * @param declaringClass The declaring class associated with the
         *                       context.
         */
        public UniqueILContext(IClass declaringClass)
        {
            super(declaringClass);
        }

        /**
         * Generates a unique descriptor for a given index, incorporating a
         * sequence number for uniqueness.
         *
         * @param index The index for which a unique descriptor is generated.
         *
         * @return A unique descriptor including a sequence number.
         */
        @Override
        public @NotNull String getDescriptor(int index)
        {
            final Descriptor descriptor = descriptors.get(index);
            final String value = descriptor.toString() + ":" + descriptor.count;
            descriptor.count++;
            return value;
        }
    }

    /**
     * Generates a unique fuzzy descriptor for an IClass (class) based on the
     * provided IClass instance and a UniqueILContext. The descriptor includes
     * the superclasses and implemented interfaces.
     *
     * @param iClass The IClass for which a unique fuzzy descriptor is
     *               generated.
     *
     * @return A unique fuzzy descriptor representing the IClass.
     */
    @Override
    public @NotNull String getDescriptor(@NotNull IClass iClass)
    {
        return getDescriptor(iClass, new UniqueILContext(iClass));
    }

    /**
     * Generates a unique fuzzy descriptor for an IMethod (method) based on the
     * provided IMethod instance and a UniqueILContext. The descriptor is unique
     * and includes the sequence number.
     *
     * @param iMethod The IMethod for which a unique fuzzy descriptor is
     *                generated.
     *
     * @return A unique fuzzy descriptor representing the IMethod.
     */
    @Override
    public String getDescriptor(@NotNull IMethod iMethod)
    {
        return getDescriptor(iMethod,
                new UniqueILContext(iMethod.getDeclaringClass()));
    }

    /**
     * Generates a unique fuzzy descriptor for an IField (field) based on the
     * provided IField instance and a UniqueILContext. The descriptor is unique
     * and includes the sequence number.
     *
     * @param iField The IField for which a unique fuzzy descriptor is
     *               generated.
     *
     * @return A unique fuzzy descriptor representing the IField.
     */
    @Override
    public String getDescriptor(@NotNull IField iField)
    {
        return getDescriptor(iField,
                new UniqueILContext(iField.getDeclaringClass()));
    }

    @Override
    public Stream<String> getDocument(@NotNull IClass iClass)
    {
        return super.getDocument(iClass, new UniqueILContext(iClass));
    }

    @Override
    public Stream<String> getMethods(@NotNull IClass iClass)
    {
        return super.getMethods(iClass, new UniqueILContext(iClass));
    }

    /**
     * Generates a unique fuzzy descriptor for an IClass (class) based on the
     * provided IClass instance and the ILContext. The descriptor includes
     * information about the class's superclasses and implemented interfaces.
     *
     * @param iClass  The IClass for which a fuzzy descriptor is generated.
     * @param context The ILContext associated with the operation.
     *
     * @return A fuzzy descriptor representing the IClass with sequence
     *         numbers.
     */
    @Override
    public @NotNull String getDescriptor(
            @NotNull IClass iClass,
            @NotNull ILContext context)
    {
        final IClass superClass = iClass.getSuperclass();
        final String superName = superClass != null ?
                super.getDescriptor(iClass.getSuperclass(), context) :
                "";

        StringJoiner builder = new StringJoiner("", "[", "]");
        iClass.getAllImplementedInterfaces()
              .stream()
              .map(super::getDescriptor)
              .forEach(builder::add);

        return context.addDescriptor(superName + builder);
    }
}
