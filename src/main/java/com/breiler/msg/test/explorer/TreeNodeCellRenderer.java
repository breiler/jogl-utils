package com.breiler.msg.test.explorer;

import com.breiler.msg.nodes.Camera;
import com.breiler.msg.nodes.Color4;
import com.breiler.msg.nodes.Node;
import com.breiler.msg.nodes.Shape;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

public class TreeNodeCellRenderer extends DefaultTreeCellRenderer {
    private final ImageIcon videoCamIcon;
    private final ImageIcon polylineIcon;
    private final ImageIcon paletteIcon;

    public TreeNodeCellRenderer() {
        videoCamIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/videocam_16.png"));
        polylineIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/polyline_16.png"));
        paletteIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/palette_16.png"));
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof TreeNode) {
            Node node = ((TreeNode) value).getNode();
            if (node instanceof Camera) {
                setIcon(videoCamIcon);
            } else if (node instanceof Shape) {
                setIcon(polylineIcon);
            }else if (node instanceof Color4) {
                setIcon(paletteIcon);
            }
        }

        return this;
    }
}
