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
package io.github.libfp.ast;  

import io.github.libfp.ISerializable;
import io.github.libfp.VarInt;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.util.SupplierUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

/**
 * The {@code AbstractSyntaxTree} class represents an abstract syntax tree (AST)
 * that is a hierarchical structure of nodes, each of which can be of a type
 * that extends the {@code ISerializable} interface.
 * <p>
 *
 * @param <N> The type of nodes in the AST, which must implement the
 *            {@code ISerializable} interface.
 */
@ApiStatus.Experimental
public class AbstractSyntaxTree<N extends Node>
        implements ISerializable, Iterable<N>
{

    private transient final Supplier<N> nodeFactory;

    private List<N> nodes;
    private Map<N, Set<Integer>> children;

    public AbstractSyntaxTree(Class<? extends N> nodeType)
    {
        this(SupplierUtil.createSupplier(nodeType));
    }

    public AbstractSyntaxTree(@NotNull Supplier<N> nodeFactory)
    {
        this.nodeFactory = nodeFactory;
        this.nodes = new ArrayList<>();
        this.children = new IdentityHashMap<>();
        // place root node
        addNode();
    }

    /**
     * Get the number of nodes in the abstract syntax tree (AST).
     *
     * @return The number of nodes.
     */
    public int size()
    {
        return nodes.size();
    }

    /**
     * Get the root node of the abstract syntax tree (AST).
     *
     * @return The root node.
     */
    public N getRoot()
    {
        return nodes.get(0);
    }

    /**
     * Get a set of child indices for a given node in the AST.
     *
     * @param node The node for which to retrieve child indices.
     * @return A set of child indices.
     */
    public Set<Integer> getChildren(final N node)
    {
        return children.get(node);
    }

    /**
     * Add a child node to the AST under the specified parent node. The child
     * node is created using the node factory, and its index is returned.
     *
     * @param node The parent node under which to add the child.
     * @return The index of the added child node.
     */
    public int addChild(final N node)
    {
        final int index = addNode();
        children.get(node).add(index);

        final int parent = indexOf(node);
        get(index).parent = parent == -1 ? 0 : parent;
        return index;
    }

    public int indexOf(final N node)
    {
        return nodes.indexOf(node);
    }

    /**
     * Add a child node to the AST under the specified parent node. The child
     * node is provided explicitly, and its index is returned.
     *
     * @param node  The parent node under which to add the child.
     * @param child The child node to add.
     * @return The index of the added child node.
     */
    public int addChild(final N node, final N child)
    {
        final int index = addNode(child);
        children.get(node).add(index);

        final int parent = indexOf(node);
        child.parent = parent == -1 ? 0 : parent;
        return index;
    }

    /**
     * Add an existing child node to the AST under the specified parent node at
     * the specified index.
     *
     * @param node  The parent node under which to add the child.
     * @param index The index at which to add the child.
     */
    public void addChild(final N node, final int index)
    {
        children.get(node).add(index);
    }

    /**
     * Get a node from the AST based on its index.
     *
     * @param index The index of the node to retrieve.
     * @return The node at the specified index.
     */
    public N get(final int index)
    {
        return nodes.get(index);
    }

    /**
     * Add a new node to the AST using the node factory and return its index.
     *
     * @return The index of the added node.
     */
    public int addNode()
    {
        Supplier<N> factory = getNodeFactory();
        return addNode(factory.get());
    }

    /**
     * Add an existing node to the AST and return its index.
     *
     * @param node The node to add to the AST.
     * @return The index of the added node.
     */
    public int addNode(N node)
    {
        final int index = nodes.size();
        children.put(node, new HashSet<>());
        nodes.add(node);
        return index;
    }

    /**
     * Get the node factory used to create nodes for the AST.
     *
     * @return The node factory.
     * @throws IllegalStateException if the node factory is not specified.
     */
    public Supplier<N> getNodeFactory()
    {
        if (nodeFactory == null) {
            throw new IllegalStateException("NodeFactory not specified!");
        }
        return nodeFactory;
    }

    /**
     * Get an iterator for iterating through the nodes in the AST.
     *
     * @return An iterator for the nodes.
     */
    @NotNull
    @Override
    public Iterator<N> iterator()
    {
        return nodes.iterator();
    }

    ///////////////////////////////////////////////////////////////////////////
    // export
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Serialize the abstract syntax tree (AST) and write it to the specified
     * data output.
     *
     * @param out The data output to write the serialized AST to.
     * @throws IOException if an I/O error occurs during serialization.
     */
    @Override
    public void writeExternal(@NotNull DataOutput out) throws IOException
    {
        // structure:
        //  - size: int32
        VarInt.write(size(), out);

        //  - nodes:
        //      | node: <content>
        //          | childCount: int32
        //          | children: int32[]
        for (final N node : this) {
            node.writeExternal(out);

            Set<Integer> children = getChildren(node);
            VarInt.write(children.size(), out);
            if (!children.isEmpty()) {
                for (final int index : children) {
                    VarInt.write(index, out);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // import
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Deserialize the abstract syntax tree (AST) from the specified data
     * input.
     *
     * @param in The data input from which to read the serialized AST.
     * @throws IOException if an I/O error occurs during deserialization.
     */
    @Override
    public void readExternal(@NotNull DataInput in) throws IOException
    {
        final int nodeCount = (int) VarInt.read(in);
        nodes = new ArrayList<>(nodeCount);
        children = new IdentityHashMap<>(nodeCount);

        for (int i = 0; i < nodeCount; i++) {
            N node = getNodeFactory().get();
            node.readExternal(in);
            addNode(node);

            final int childCount = (int) VarInt.read(in);
            Set<Integer> nodeChildren = new HashSet<>(childCount);
            for (int j = 0; j < childCount; j++) {
                final int index = (int) VarInt.read(in);
                nodeChildren.add(index);
            }
            children.put(node, nodeChildren);
        }
    }
}
