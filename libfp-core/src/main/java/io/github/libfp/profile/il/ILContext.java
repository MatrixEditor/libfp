package io.github.libfp.profile.il;//@date 23.10.2023

import com.ibm.wala.classLoader.IClass;
import io.github.libfp.Descriptor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * The <code>ILContext</code> class represents a context for generating
 * Intermediate Language (IL) descriptors and maintaining a list of descriptors
 * for a specific declaring class.
 *
 * <p>
 * An <code>ILContext</code> is associated with a specific class, and it allows
 * the generation and management of IL descriptors for various class members,
 * such as fields and methods. This class maintains a list of descriptors unique
 * to the declaring class.
 * </p>
 */
public class ILContext
{

    /**
     * The class for which the IL descriptors are being generated.
     */
    public final IClass declaringClass;

    /**
     * A list of IL descriptors associated with the declaring class.
     */
    public final @NotNull List<Descriptor> descriptors;

    /**
     * Constructs a new <code>ILContext</code> for a specific declaring class.
     *
     * @param declaringClass The class for which IL descriptors are generated.
     */
    public ILContext(final IClass declaringClass)
    {
        this.declaringClass = declaringClass;
        this.descriptors = new LinkedList<>();
    }

    /**
     * Adds an IL descriptor to the context and ensures its uniqueness.
     *
     * @param value The IL descriptor to add.
     *
     * @return The IL descriptor, either the newly added one or an existing one
     *         from the list.
     */
    public String addDescriptor(@NotNull String value)
    {
        final int index = descriptors.indexOf(new Descriptor(value));
        if (index != -1) {
            value = getDescriptor(index);
        } else descriptors.add(new Descriptor(value));

        return value;
    }

    /**
     * Retrieves the IL descriptor at the specified index.
     *
     * @param index The index of the IL descriptor to retrieve.
     *
     * @return The IL descriptor at the specified index.
     */
    public String getDescriptor(final int index)
    {
        return descriptors.get(index).toString();
    }
}
