package com.breiler.msg.test.explorer;

import com.breiler.msg.nodes.NodeChangeEvent;
import com.breiler.msg.nodes.NodeChangeListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

public class SceneTree extends JTree {
    public SceneTree(Scene scene) {
        super(new TreeNode(scene.getRoot()));
        setCellRenderer(new TreeNodeCellRenderer());

        scene.getRoot().addNodeChangeListener(new NodeChangeListener() {
            @Override
            public void childAdded(NodeChangeEvent evt) {
                DefaultTreeModel model = (DefaultTreeModel) treeModel;
                model.nodeChanged(new TreeNode(evt.getChild()));
            }

            @Override
            public void childRemoved(NodeChangeEvent evt) {
                DefaultTreeModel model = (DefaultTreeModel) treeModel;
                model.nodeChanged(new TreeNode(evt.getChild()));
            }
        });
    }
}
