package com.breiler.msg.test.explorer;

import com.breiler.msg.math.Rotf;
import com.breiler.msg.misc.FPSCounter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector3f;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Explorer extends JFrame implements GLEventListener, MouseListener {
    public static final String PROPERTY_USE_SCREEN_MENU = "apple.laf.useScreenMenuBar";
    private GLCanvas canvas;
    private Scene scene;
    private Animator animator;
    private FPSCounter fpsCounter;

    public Explorer() {
        setupLookAndFeel();
        initScene();

        setTitle("Explorer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));

        JComponent toolsComponent = createToolsComponent();
        Component mainComponent = createMainComponent();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                mainComponent, toolsComponent);
        splitPane.setResizeWeight(0.95);

        getContentPane().add(splitPane, BorderLayout.CENTER);
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Disposing GL context");
                animator.stop();
                canvas.destroy();
            }
        });
    }

    private static void setupLookAndFeel() {
        System.setProperty(PROPERTY_USE_SCREEN_MENU, "true");
        UIManager.put("MenuBar.background", "@background");
    }

    public static void main(String[] args) {
        new Explorer();
    }

    private void initScene() {
        scene = new Scene();
    }

    private Component createMainComponent() {
        GLCapabilities glCaps = new GLCapabilities(null);
        canvas = new GLCanvas(glCaps);
        canvas.addGLEventListener(this);
        canvas.setMinimumSize(new Dimension(10, 10));

        CanvasMouseListener canvasMouseListener = new CanvasMouseListener(scene);
        canvas.addMouseListener(canvasMouseListener);
        canvas.addMouseMotionListener(canvasMouseListener);
        animator = new Animator(canvas);
        animator.start();
        return canvas;
    }

    private JComponent createToolsComponent() {
        JPanel panel = new JPanel();
        JCheckBox usePerspective = new JCheckBox("Use perspective camera");

        usePerspective.addActionListener(event -> scene.setPerspectiveCamera(usePerspective.getModel().isSelected()));
        panel.add(usePerspective);

        JSlider xSlider = new JSlider(-10000, 10000);
        xSlider.setValue(Math.round(scene.getCamera().getPosition().getX() * 100));
        JSlider ySlider = new JSlider(-10000, 10000);
        ySlider.setValue(Math.round(scene.getCamera().getPosition().getY() * 100));
        JSlider zSlider = new JSlider(-10000, 10000);
        zSlider.setValue(Math.round(scene.getCamera().getPosition().getZ() * 100));


        ChangeListener changeListener = e -> scene.getCamera().setPosition(new Vector3f(xSlider.getValue() / 100f, ySlider.getValue() / 100f, zSlider.getValue() / 100f));
        xSlider.addChangeListener(changeListener);
        ySlider.addChangeListener(changeListener);
        zSlider.addChangeListener(changeListener);


        double PI2 = Math.PI * 2;
        int max = (int) Math.round(PI2 * 1000);
        JSlider rotXSlider = new JSlider(0, max);
        rotXSlider.setValue(Math.round(scene.getCamera().getOrientation().get(new Vector3f(1, 0, 0)) * 1000));
        JSlider rotYSlider = new JSlider(0, max);
        rotYSlider.setValue(Math.round(scene.getCamera().getOrientation().get(new Vector3f(0, 1, 0)) * 1000));
        JSlider rotZSlider = new JSlider(0, max);
        rotZSlider.setValue(Math.round(scene.getCamera().getOrientation().get(new Vector3f(0, 0, 1)) * 1000));

        changeListener = e -> {
            System.out.println(rotXSlider.getValue() / 1000f);
            Rotf rotX = new Rotf(new Vector3f(1, 0, 0), rotXSlider.getValue() / 1000f);
            Rotf rotY = new Rotf(new Vector3f(0, 1, 0), rotYSlider.getValue() / 1000f);
            Rotf rotZ = new Rotf(new Vector3f(0, 0, 1), rotZSlider.getValue() / 1000f);

            Rotf orientation = new Rotf();
            orientation.mul(rotX, rotY);

            Rotf orientation2 = new Rotf();
            orientation2.mul(orientation, rotZ);

            //orientation.set(new Vec3f(0, 1,0), rotYSlider.getValue() / 1000f);
            //orientation.set(new Vec3f(0, 0,1), rotZSlider.getValue() / 1000f);
            scene.getCamera().setOrientation(orientation2);
            //scene.getCamera().getOrientation().rotateX(rotXSlider.getValue()/ 1000f);

            //System.out.println(orientation2 + " " + rotXSlider.getValue() + " " + (rotXSlider.getValue() / 1000f));
        };
        rotXSlider.addChangeListener(changeListener);
        rotYSlider.addChangeListener(changeListener);
        rotZSlider.addChangeListener(changeListener);

        panel.add(xSlider);
        panel.add(ySlider);
        panel.add(zSlider);

        panel.add(rotXSlider);
        panel.add(rotYSlider);
        panel.add(rotZSlider);

        scene.getCamera().addCameraListener(() -> {
            SwingUtilities.invokeLater(() -> {
                float newXRot = scene.getCamera().getOrientation().get(new Vector3f(1f, 0, 0)) ;
                System.out.println(newXRot + " " + rotXSlider.getValue());
            });
        });

        JTree tree = new SceneTree(scene);

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tree), panel);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1, 1, 1, 1);
        fpsCounter = new FPSCounter(drawable, new Font("SansSerif", Font.BOLD, 12));
        fpsCounter.setColor(0, 0, 0, 1);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        scene.render();
        fpsCounter.draw();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
