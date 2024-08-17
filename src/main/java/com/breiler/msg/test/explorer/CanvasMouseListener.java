package com.breiler.msg.test.explorer;

import com.breiler.msg.actions.RayPickAction;
import com.breiler.msg.math.Rotf;
import com.breiler.msg.nodes.Node;

import javax.swing.SwingUtilities;
import javax.vecmath.Vector3f;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

public class CanvasMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final RayPickAction rayPickAction;
    private final Scene scene;

    private final Point2D startClick = new Point2D.Double(0,0);

    public CanvasMouseListener(Scene scene) {
        this.rayPickAction = new RayPickAction();
        this.scene = scene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            startClick.setLocation(e.getX(), e.getY());
        }

        SwingUtilities.invokeLater(() -> {
            System.out.println("Ray pick");
            rayPickAction.setPoint(e.getX(), e.getY(), e.getComponent());
            rayPickAction.apply(scene.getRoot());

            rayPickAction.getPickedPoints().stream().forEach(pickedPoint -> {
                Node node = pickedPoint.getPath().get(pickedPoint.getPath().size() - 1);
                System.out.println(node + " " + node.getUUID());
            });
        });


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println(e);

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float xDistance = (float) (e.getX() - startClick.getX());
        float yDistance = (float) (e.getY() - startClick.getY());

        float xV = (float)(Math.PI * 2f) * (yDistance / 1000f);
        float yV = (float)(Math.PI * 2f) * (xDistance / 1000f);

        //System.out.println(xDistance + (xDistance / 10000f) + " " + v);
        Rotf orientation = scene.getCamera().getOrientation();

        Rotf rotX = new Rotf(new Vector3f(1, 0, 0), xV);
        Rotf rotY = new Rotf(new Vector3f(0, 1, 0), yV);
        Rotf rotZ = new Rotf(new Vector3f(0, 0, 1), orientation.get(new Vector3f(0, 0, 1)));

        Rotf orientation1 = new Rotf();
        orientation1.mul(rotX, rotY);
        Rotf orientation2 = new Rotf();
        orientation2.mul(orientation1, rotZ);

        scene.getCamera().setOrientation(orientation2);

        //startClick.setLocation(e.getX(), e.getY());


    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
