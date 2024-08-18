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

import javax.vecmath.Matrix4f;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The base class for all nodes in the scene graph.
 */

public abstract class Node implements NodeChangeListener {
    private final UUID uuid = UUID.randomUUID();
    private final Matrix4f transform;
    protected Set<NodeChangeListener> listeners = ConcurrentHashMap.newKeySet();
    private Node parent;
    private String name = getClass().getSimpleName();
    protected Node() {
        transform = new Matrix4f();
        transform.setIdentity();
    }

    /**
     * Returns this transform matrix which can be used for translating, rotating and scaling
     *
     * @return the nodes matrix
     */
    public Matrix4f getTransform() {
        return transform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Performs the "typical" operation for this node when an action is
     * applied to it. The default implementation does nothing.
     */
    public void doAction(Action action) {
    }

    /**
     * Support for the built-in GLRenderAction. Note that supplying
     * virtual methods in Node subclasses to support various actions is
     * not required due to the framework supporting action methods, but
     * for built-in actions it may make it simpler.
     */
    public void render(GLRenderAction action) {
        doAction(action);
    }

    /**
     * Support for the built-in RayPickAction. Note that supplying
     * virtual methods in Node subclasses to support various actions is
     * not required due to the framework supporting action methods, but
     * for built-in actions it may make it simpler.
     */
    public void rayPick(RayPickAction action) {
        doAction(action);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node node) {
        // Remove old parent
        if (parent != null) {
            removeNodeChangeListener(parent);
        }

        // Set new parent
        parent = node;
        if (parent != null) {
            addNodeChangeListener(parent);
        }
    }

    public void addNodeChangeListener(NodeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeNodeChangeListener(NodeChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void childAdded(NodeChangeEvent evt) {
        listeners.forEach(l -> l.childAdded(evt));
    }

    @Override
    public void childRemoved(NodeChangeEvent evt) {
        listeners.forEach(l -> l.childRemoved(evt));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Node)) {
            return false;
        }

        return this.uuid.equals(((Node) obj).uuid);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getUUID() {
        return uuid.toString();
    }
}
