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

package com.breiler.msg.test;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.breiler.msg.actions.GLRenderAction;
import com.breiler.msg.actions.RayPickAction;
import com.breiler.msg.collections.Vec2fCollection;
import com.breiler.msg.collections.Vec3fCollection;
import com.breiler.msg.collections.Vec4fCollection;
import com.breiler.msg.misc.PickedPoint;
import com.breiler.msg.nodes.Color4;
import com.breiler.msg.nodes.Coordinate3;
import com.breiler.msg.nodes.Group;
import com.breiler.msg.nodes.TextureCoordinate2;
import com.breiler.msg.nodes.Transform;
import com.breiler.msg.nodes.TriangleSet;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import com.breiler.msg.nodes.PerspectiveCamera;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/** A very basic test of the Minimal Scene Graph library. */

public class Test {
  private static GLCanvas canvas;

  public static void main(final String[] args) {
    final Frame frame = new Frame("Minimal Scene Graph (MSG) Test");
    canvas = new GLCanvas();
    canvas.addGLEventListener(new Listener());
    frame.add(canvas);
    frame.setSize(512, 512);
    frame.setVisible(true);
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(final WindowEvent e) {
          new Thread(() -> System.exit(0)).start();
        }
      });


  }

  static class Listener implements GLEventListener {
    private Group root;
    private GLRenderAction renderAction;

    public void init(final GLAutoDrawable drawable) {
      root = new Group();
      final PerspectiveCamera cam = new PerspectiveCamera();
      cam.setPosition(new Vector3f(0, 0, 2));
      root.addChild(cam);
      final Coordinate3 coordNode = new Coordinate3();
      final Vec3fCollection coords = new Vec3fCollection();
      // First triangle
      coords.add(new Vector3f( 1,  1, 0));
      coords.add(new Vector3f(-1,  1, 0));
      coords.add(new Vector3f(-1, -1, 0));
      // Second triangle
      coords.add(new Vector3f( 1,  1, 0));
      coords.add(new Vector3f(-1, -1, 0));
      coords.add(new Vector3f( 1, -1, 0));
      coordNode.setData(coords);
      root.addChild(coordNode);

      // Texture coordinates
      final TextureCoordinate2 texCoordNode = new TextureCoordinate2();
      final Vec2fCollection texCoords = new Vec2fCollection();
      // First triangle
      texCoords.add(new Vector2f( 1,  1));
      texCoords.add(new Vector2f( 0,  1));
      texCoords.add(new Vector2f( 0,  0));
      // Second triangle
      texCoords.add(new Vector2f( 1,  1));
      texCoords.add(new Vector2f( 0,  0));
      texCoords.add(new Vector2f( 1,  0));
      texCoordNode.setData(texCoords);
      root.addChild(texCoordNode);

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
      root.addChild(colorNode);

      final TriangleSet tris = new TriangleSet();
      root.addChild(tris);

      // Testing transforms
      final Transform xform = new Transform();
      xform.getTransform().setTranslation(new Vector3f(2, -2, 0));
      //      xform.getTransform().setRotation(new Rotf(new Vec3f(0, 1, 0), (float) (-Math.PI / 4)));
      root.addChild(xform);

      root.addChild(tris);

      final GL gl = drawable.getGL();
      gl.glEnable(GL.GL_DEPTH_TEST);

      renderAction = new GLRenderAction();

      canvas.addMouseListener(new MouseAdapter() {
        RayPickAction ra = new RayPickAction();

        public void mousePressed(final MouseEvent e) {
          ra.setPoint(e.getX(), e.getY(), e.getComponent());
          // Apply to the scene root
          ra.apply(root);
          final List<PickedPoint> pickedPoints = ra.getPickedPoints();
          System.out.println(pickedPoints.stream().map(s -> "[" + s.getPath().stream().map(Objects::toString).collect(Collectors.joining(", ")) + "]").collect(Collectors.joining(", ")));
        }
      });
    }

    public void display(final GLAutoDrawable drawable) {
      final GL gl = drawable.getGL();
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
      renderAction.apply(root);
    }

    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int w, final int h) {}
    public void dispose(final GLAutoDrawable drawable) {}
  }
}
