package com.breiler.msg.test.explorer;

import com.breiler.msg.actions.GLRenderAction;
import com.breiler.msg.collections.Vec2fCollection;
import com.breiler.msg.collections.Vec3fCollection;
import com.breiler.msg.collections.Vec4fCollection;
import com.breiler.msg.math.Rotf;
import com.breiler.msg.math.Vec3f;
import com.breiler.msg.math.Vec4f;
import com.breiler.msg.nodes.Camera;
import com.breiler.msg.nodes.Color4;
import com.breiler.msg.nodes.Coordinate3;
import com.breiler.msg.nodes.Group;
import com.breiler.msg.nodes.Node;
import com.breiler.msg.nodes.NodeChangeEvent;
import com.breiler.msg.nodes.NodeChangeListener;
import com.breiler.msg.nodes.OrthographicCamera;
import com.breiler.msg.nodes.PerspectiveCamera;
import com.breiler.msg.nodes.TextureCoordinate2;
import com.breiler.msg.nodes.TriangleSet;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

public class Scene {
    private final Group root;
    private final GLRenderAction renderAction = new GLRenderAction();
    private PerspectiveCamera perspectiveCamera;
    private OrthographicCamera orthCamera;
    private Camera camera;

    public Scene() {
        root = new Group();
        root.addNodeChangeListener(new NodeChangeListener() {
            @Override
            public void childAdded(NodeChangeEvent evt) {
                System.out.println("Node added " + evt);
            }

            @Override
            public void childRemoved(NodeChangeEvent evt) {
                System.out.println("Node removed " + evt);
            }
        });

        initCamera();

        Group plane = createVertexObject();


        Group object1 = createVertexObject();
        object1.getTransform().setTranslation(new Vec3f(-1, -1, -0.5));

        Group object2 = createVertexObject();
        object2.getTransform().setTranslation(new Vec3f(0, 0, -2));
        object2.getTransform().setRotation(new Rotf(new Vec3f(0, 1, 0), (float) (Math.PI)))
                .setScale(5);


        /*// Testing transforms


        final TriangleSet tris2 = new TriangleSet();
        tris2.setNumTriangles(2);
        things.addChild(tris2);*/


        Group things = new Group();
        things.addChild(plane);
        things.addChild(object1);


        things.addChild(object2);
        root.addChild(things);
    }

    private static Group createVertexObject() {
        Group object1 = new Group();

        final Coordinate3 coordNode = new Coordinate3();
        final Vec3fCollection coords = new Vec3fCollection();
        // First triangle
        coords.add(new Vec3f(1, 1, 0));
        coords.add(new Vec3f(-1, 1, 0));
        coords.add(new Vec3f(-1, -1, 0));
        // Second triangle
        coords.add(new Vec3f(1, 1, 0));
        coords.add(new Vec3f(-1, -1, 0));
        coords.add(new Vec3f(1, -1, 0));
        coordNode.setData(coords);
        object1.addChild(coordNode);

        // Texture coordinates
        final TextureCoordinate2 texCoordNode = new TextureCoordinate2();
        final Vec2fCollection texCoords = new Vec2fCollection();
        // First triangle
        texCoords.add(new Vector2f(1, 1));
        texCoords.add(new Vector2f(0, 1));
        texCoords.add(new Vector2f(0, 0));
        // Second triangle
        texCoords.add(new Vector2f(1, 1));
        texCoords.add(new Vector2f(0, 0));
        texCoords.add(new Vector2f(1, 0));
        texCoordNode.setData(texCoords);
        object1.addChild(texCoordNode);

        // Colors
        final Color4 colorNode = new Color4();
        final Vec4fCollection colors = new Vec4fCollection();
        // First triangle
        colors.add(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
        // Second triangle
        colors.add(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        colors.add(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
        colors.add(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
        colorNode.setData(colors);
        object1.addChild(colorNode);

        final TriangleSet tris = new TriangleSet();
        object1.addChild(tris);
        return object1;
    }

    public void setPerspectiveCamera(boolean usePerspectiveCamera) {
        if (usePerspectiveCamera) {
            root.replaceChild(orthCamera, perspectiveCamera);
            camera = perspectiveCamera;
        } else {
            root.replaceChild(perspectiveCamera, orthCamera);
            camera = orthCamera;
        }
    }

    void initCamera() {
        perspectiveCamera = new PerspectiveCamera();
        perspectiveCamera.setPosition(new Vec3f(0, 0, 4));

        orthCamera = new OrthographicCamera();
        orthCamera.setPosition(new Vec3f(0, 0, 10));
        orthCamera.setHeight(20);
        camera = orthCamera;
        root.addChild(orthCamera);
    }

    public void render() {
        renderAction.apply(root);
    }

    public Node getRoot() {
        return root;
    }

    public Camera getCamera() {
        return camera;
    }
}
