package io.github.libfp.cha; //@date 24.10.2023

import io.github.libfp.VarInt;
import io.github.libfp.cha.extension.FieldProfileList;
import io.github.libfp.cha.extension.MethodProfileList;
import io.github.libfp.profile.manager.ProfileManager;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * ExtendedClassProfile is a class that extends ClassProfile to provide
 * additional information about class profiles.
 */
public class ExtendedClassProfile extends ClassProfile
{

    /**
     * The number of instance fields in the class.
     */
    public int instanceFieldCount;

    /**
     * The number of static fields in the class.
     */
    public int staticFieldCount;

    /**
     * The start index of fields.
     */
    public int fieldsStartIndex;

    /**
     * The number of instance methods in the class.
     */
    public int instanceMethodCount;

    /**
     * The number of static methods in the class.
     */
    public int staticMethodCount;

    /**
     * The start index of methods.
     */
    public int methodsStartIndex;

    /**
     * A flag indicating whether fields are used.
     */
    public boolean useFields;

    /**
     * A flag indicating whether methods are used.
     */
    public boolean useMethods;

    /**
     * Constructor for ExtendedClassProfile.
     *
     * @param manager The ProfileManager associated with this profile.
     */
    public ExtendedClassProfile(@NotNull ProfileManager manager)
    {
        super(manager);
        useMethods = manager.hasExtension(MethodProfileList.class);
    }

    public int getMethodCount()
    {
        return instanceMethodCount + staticMethodCount;
    }

    public int getFieldCount()
    {
        return instanceFieldCount + staticFieldCount;
    }

    /**
     * Get a collection of MethodProfile objects associated with this class
     * profile.
     *
     * @return A collection of MethodProfile objects, or an empty list if no
     *         methods are available.
     */
    public final @NotNull Collection<MethodProfile> getMethods()
    {
        if (getMethodCount() == 0 || !useMethods) {
            return Collections.emptyList();
        }

        return getCollection(MethodProfileList.class, methodsStartIndex,
                getMethodCount());
    }

    public final @NotNull Collection<FieldProfile> getFields()
    {
        if (getFieldCount() == 0 || !useFields) {
            return Collections.emptyList();
        }
        return getCollection(FieldProfileList.class, fieldsStartIndex,
                getFieldCount());
    }

    public final @NotNull Collection<FieldProfile> getStaticFields()
    {
        if (staticFieldCount == 0 || !useFields) {
            return Collections.emptyList();
        }

        return getCollection(
                FieldProfileList.class,
                fieldsStartIndex + instanceFieldCount,
                staticFieldCount
        );
    }

    public final @NotNull Collection<FieldProfile> getInstanceFields()
    {
        if (instanceFieldCount == 0 || !useFields) {
            return Collections.emptyList();
        }
        return getCollection(FieldProfileList.class, fieldsStartIndex,
                instanceFieldCount);
    }

    public final @NotNull Collection<MethodProfile> getStaticMethods()
    {
        if (staticMethodCount == 0 || !useMethods) {
            return Collections.emptyList();
        }

        return getCollection(
                MethodProfileList.class,
                methodsStartIndex + instanceMethodCount,
                staticMethodCount
        );
    }

    public final @NotNull Collection<MethodProfile> getInstanceMethods()
    {
        if (instanceFieldCount == 0 || !useMethods) {
            return Collections.emptyList();
        }
        return getCollection(MethodProfileList.class, methodsStartIndex,
                instanceMethodCount);
    }

    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        super.readExternal(in);
        useFields = in.readBoolean();
        useMethods = in.readBoolean();

        instanceFieldCount = (int) VarInt.read(in);
        staticFieldCount = (int) VarInt.read(in);

        instanceMethodCount = (int) VarInt.read(in);
        staticMethodCount = (int) VarInt.read(in);

        if (useFields) {
            fieldsStartIndex = (int) VarInt.read(in);
        }
        if (useMethods) {
            methodsStartIndex = (int) VarInt.read(in);
        }
    }

    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        super.writeExternal(out);
        out.writeBoolean(useFields);
        out.writeBoolean(useMethods);

        VarInt.write(instanceFieldCount, out);
        VarInt.write(staticFieldCount, out);
        VarInt.write(instanceMethodCount, out);
        VarInt.write(staticMethodCount, out);

        if (useFields) {
            VarInt.write(fieldsStartIndex, out);
        }
        if (useMethods) {
            VarInt.write(methodsStartIndex, out);
        }
    }

}

