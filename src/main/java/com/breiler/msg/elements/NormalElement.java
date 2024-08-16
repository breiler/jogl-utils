package com.breiler.msg.elements;

import com.breiler.msg.misc.State;
import com.breiler.msg.misc.StateIndex;

import java.nio.FloatBuffer;

/**
 * Represents the current set of normals, which are applied on a
 * per-vertex basis to any drawn geometry.
 */
public class NormalElement extends Element {
    private static final StateIndex index = State.registerElementType();

    protected FloatBuffer normals;

    /**
     * Returns the instance of this element in the passed State.
     */
    public static NormalElement getInstance(State state) {
        return (NormalElement) state.getElement(index);
    }

    /**
     * Enables this element in the passed state, which should be the
     * default for a given action.
     */
    public static void enable(State defaultState) {
        NormalElement tmp = new NormalElement();
        defaultState.setElement(tmp.getStateIndex(), tmp);
    }

    /**
     * Indicates whether this element is enabled in the given default
     * state for a particular action.
     */
    public static boolean isEnabled(State state) {
        return (state.getDefaults().getElement(index) != null);
    }

    /**
     * Sets the color data in the passed state.
     */
    public static void set(State state, FloatBuffer colors) {
        getInstance(state).setElt(colors);
    }

    /**
     * Returns the color data in the passed state.
     */
    public static FloatBuffer get(State state) {
        return getInstance(state).normals;
    }

    public StateIndex getStateIndex() {
        return index;
    }

    public Element newInstance() {
        return new NormalElement();
    }

    public void push(State state) {
        NormalElement prev = (NormalElement) getNextInStack();
        if (prev != null) {
            // Pull down the data from the previous element
            normals = prev.normals;
        }
    }

    /**
     * Sets the color data in this element.
     */
    public void setElt(FloatBuffer normals) {
        this.normals = normals;
    }
}
