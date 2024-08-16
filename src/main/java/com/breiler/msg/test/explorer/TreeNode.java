package com.breiler.msg.test.explorer;

import com.breiler.msg.nodes.Group;
import com.breiler.msg.nodes.Node;

import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

public class TreeNode implements MutableTreeNode {

    private Node node;

    public TreeNode(Node node) {
        this.node = node;
    }

    @Override
    public javax.swing.tree.TreeNode getChildAt(int childIndex) {
        if (isLeaf()) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return new TreeNode(((Group) node).getChild(childIndex));
    }

    @Override
    public int getChildCount() {
        if(isLeaf()) {
            return 0;
        } else {
            return ((Group) node).getNumChildren();
        }
    }

    @Override
    public javax.swing.tree.TreeNode getParent() {
        if(node == null || node.getParent() == null) {
            return null;
        }
        return new TreeNode(node.getParent());
    }

    @Override
    public int getIndex(javax.swing.tree.TreeNode node) {
        if (node == null) {
            throw new IllegalArgumentException("argument is null");
        }

        if( !(node instanceof TreeNode)) {
            throw new IllegalArgumentException("Unknown node type");
        }

        if (isLeaf()) {
            return -1;
        }

        return ((Group)this.node).findChild(((TreeNode) node).getNode());
    }

    public Node getNode() {
        return this.node;
    }

    @Override
    public boolean getAllowsChildren() {
        return (node instanceof Group);
    }

    @Override
    public boolean isLeaf() {
        return !(node instanceof Group);
    }

    @Override
    public Enumeration<? extends javax.swing.tree.TreeNode> children() {
        if(!(node instanceof Group)) {
            return Collections.emptyEnumeration();
        }
        return Collections.enumeration(((Group) node)
                .getChildren()
                .stream()
                .map(TreeNode::new)
                .collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return node.toString();
    }

    @Override
    public int hashCode() {
        return node.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TreeNode)) {
            return false;
        }

        return ((TreeNode) obj).getNode().equals(this.node);
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        if(isLeaf()) {
            throw new IllegalArgumentException("Not a group");
        }

        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void remove(int index) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void remove(MutableTreeNode node) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void setUserObject(Object object) {
        this.node = (Node) object;
    }

    @Override
    public void removeFromParent() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        throw new IllegalArgumentException("Not implemented");
    }
}
