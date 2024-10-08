/*
 * Copyright (c) 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 */

package com.breiler.msg.nodes;

import com.breiler.msg.actions.Action;
import com.breiler.msg.actions.GLRenderAction;
import com.breiler.msg.actions.RayPickAction;
import com.breiler.msg.elements.GLModelMatrixElement;
import com.breiler.msg.elements.GLProjectionMatrixElement;
import com.breiler.msg.elements.GLViewingMatrixElement;
import com.breiler.msg.elements.ModelMatrixElement;
import com.breiler.msg.elements.ProjectionMatrixElement;
import com.breiler.msg.elements.ViewingMatrixElement;
import com.breiler.msg.misc.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A node which manages other Node instances.
 */

public class Group extends Node implements Iterable<Node> {

    static {
        GLModelMatrixElement.enable(GLRenderAction.getDefaultState());
        ModelMatrixElement.enable(RayPickAction.getDefaultState());
    }

    private final List<Node> children = new ArrayList<>();

    /**
     * Append a child node to the list of children nodes this group node is managing.
     */
    public void addChild(Node child) {
        if (child == null)
            throw new IllegalArgumentException("child may not be null");
        child.setParent(this);
        children.add(child);
        listeners.forEach(l -> l.childAdded(new NodeChangeEvent(this, child)));
    }

    /**
     * Adds a child so that it becomes the one with the given index.
     */
    public void insertChild(int index, Node child) {
        if (child == null)
            throw new IllegalArgumentException("child may not be null");
        child.setParent(this);
        children.add(index, child);
        listeners.forEach(l -> l.childAdded(new NodeChangeEvent(this, child)));
    }

    /**
     * Returns the child node with the given index.
     */
    public Node getChild(int index) {
        return children.get(index);
    }

    /**
     * Finds the index of given child within the group. Returns -1 if not found.
     */
    public int findChild(Node node) {
        return children.indexOf(node);
    }

    /**
     * Returns number of children.
     */
    public int getNumChildren() {
        return children.size();
    }

    /**
     * Removes the child with given index from the group.
     *
     * @throws IndexOutOfBoundsException if the index is less than 0 or
     *                                   greater than the number of children
     */
    public void removeChild(int index) throws IndexOutOfBoundsException {
        Node child = children.remove(index);
        if (child != null) {
            listeners.forEach(l -> l.childRemoved(new NodeChangeEvent(this, child)));
            child.setParent(null);
        }
    }

    /**
     * Removes the given child from the group. This is a convenience
     * method equivalent to calling {@link #findChild findChild} and,
     * if the node is found, passing the index to {@link #removeChild
     * removeChild}.
     */
    public void removeChild(Node node) {
        int idx = findChild(node);
        if (idx >= 0)
            removeChild(idx);
    }

    /**
     * Removes all children from this Group node.
     */
    public void removeAllChildren() {
        children.forEach(child -> {
            listeners.forEach(l -> l.childRemoved(new NodeChangeEvent(this, child)));
            child.setParent(null);
        });
        children.clear();
    }

    /**
     * Replaces the child at the given index with the new child.
     *
     * @throws IndexOutOfBoundsException if the index is less than 0 or
     *                                   greater than the number of children
     */
    public void replaceChild(int index, Node newChild) throws IndexOutOfBoundsException {
        if (newChild == null)
            throw new IllegalArgumentException("child may not be null");
        removeChild(index);
        insertChild(index, newChild);
    }

    /**
     * Replaces the old child with the new child. This is a convenience
     * method. It will simply call {@link #findChild findChild} with
     * oldChild as argument, and call replaceChild(int, SoNode*) if the
     * child is found.
     */
    public void replaceChild(Node oldChild, Node newChild) {
        if (newChild == null)
            throw new IllegalArgumentException("child may not be null");
        int idx = findChild(oldChild);
        if (idx >= 0)
            replaceChild(idx, newChild);
    }

    /**
     * Returns an Iterator over the nodes this Group contains.
     */
    public Iterator<Node> iterator() {
        return children.iterator();
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    public void doAction(Action action) {
        State state = action.getState();
        state.push();

        if (ModelMatrixElement.isEnabled(state)) {
            ModelMatrixElement.mult(state, getTransform());
        }

        try {
            forEach(action::apply);
        } finally {
            state.pop();
        }
    }
}
