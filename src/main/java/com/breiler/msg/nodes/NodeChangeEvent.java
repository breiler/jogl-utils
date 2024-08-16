package com.breiler.msg.nodes;

public class NodeChangeEvent {
    private final Node parent;
    private final Node child;

    public NodeChangeEvent(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public String toString() {
        return parent.toString() + " -> " + child.toString();
    }

    public Node getParent() {
        return parent;
    }

    public Node getChild() {
        return child;
    }
}
