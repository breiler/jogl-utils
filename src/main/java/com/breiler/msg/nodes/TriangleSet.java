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
import com.breiler.msg.elements.ColorElement;
import com.breiler.msg.elements.CoordinateElement;
import com.breiler.msg.elements.TextureCoordinateElement;
import com.breiler.msg.elements.TextureElement;
import com.breiler.msg.math.Mat4f;
import com.breiler.msg.math.MathUtils;
import com.breiler.msg.math.Vec3f;
import com.breiler.msg.math.Vec4f;
import com.breiler.msg.misc.PrimitiveVertex;
import com.breiler.msg.misc.State;
import com.breiler.msg.misc.TriangleCallback;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

import javax.vecmath.Vector2f;
import java.nio.FloatBuffer;

/**
 * A TriangleSet assembles the coordinates specified by a Coordinate3
 * node, and any auxiliary nodes such as a TextureCoordinate2 node,
 * into a set of triangles.
 */

public class TriangleSet extends TriangleBasedShape {
    // Helper routine for setting up a texture matrix to allow texture
    // coords in the scene graph to always be specified from (0..1)
    private final Mat4f textureMatrix = new Mat4f();

    public void render(final GLRenderAction action) {
        final State state = action.getState();
        if (!CoordinateElement.isEnabled(state) || CoordinateElement.get(state) == null) {
            return;
        }

        // OK, we have coordinates to send down, at least

        final GL2 gl = action.getGL();

        Texture tex = null;
        boolean haveTexCoords = false;

        if (TextureElement.isEnabled(state) && TextureCoordinateElement.isEnabled(state)) {
            final Texture2 texNode = TextureElement.get(state);
            if (texNode != null) {
                tex = texNode.getTexture(gl);
            }
            haveTexCoords = (TextureCoordinateElement.get(state) != null);
        }

        if (tex != null) {
            // Set up the texture matrix to uniformly map [0..1] to the used
            // portion of the texture image
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();
            if (gl.isExtensionAvailable("GL_VERSION_1_3")) {
                gl.glLoadTransposeMatrixf(MathUtils.getRowMajorData(getTextureMatrix(tex)), 0);
            } else {
                gl.glLoadMatrixf(MathUtils.getColumnMajorData(getTextureMatrix(tex)), 0);
            }
            gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        } else if (haveTexCoords) {
            // Want to turn off the use of texture coordinates to avoid errors
            // FIXME: not 100% sure whether we need to do this, but think we should
            gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
        }

        // For now, assume the triangle set and the number of available
        // coordinates match -- may want to add debugging information
        // for this later
        int numTriangles = CoordinateElement.get(state).limit() / 3 / 3;
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3 * numTriangles);

        if (tex != null) {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        } else if (haveTexCoords) {
            // Might want this the next time we render a shape
            gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
        }

    }

    public void generateTriangles(final Action action, final TriangleCallback cb) {
        final State state = action.getState();
        FloatBuffer coords = null;
        FloatBuffer texCoords = null;
        // FIXME: normals and lighting not supported yet
        //    FloatBuffer normals   = null;
        FloatBuffer colors = null;
        if (CoordinateElement.isEnabled(state)) {
            coords = CoordinateElement.get(state);
        }
        // No point in continuing if we don't have coordinates
        if (coords == null) return;
        if (TextureCoordinateElement.isEnabled(state)) {
            texCoords = TextureCoordinateElement.get(state);
        }
        //    if (NormalElement.isEnabled(state)) {
        //      texCoords = NormalElement.get(state);
        //    }
        if (ColorElement.isEnabled(state)) {
            colors = ColorElement.get(state);
        }
        final PrimitiveVertex v0 = new PrimitiveVertex();
        final PrimitiveVertex v1 = new PrimitiveVertex();
        final PrimitiveVertex v2 = new PrimitiveVertex();
        v0.setCoord(new Vec3f());
        v1.setCoord(new Vec3f());
        v2.setCoord(new Vec3f());
        if (texCoords != null) {
            v0.setTexCoord(new Vector2f());
            v1.setTexCoord(new Vector2f());
            v2.setTexCoord(new Vector2f());
        }
        if (colors != null) {
            v0.setColor(new Vec4f());
            v1.setColor(new Vec4f());
            v2.setColor(new Vec4f());
        }

        int coordIdx = 0;
        int numTriangles = coords.limit() / 3 / 3;
        for (int i = 0; i < numTriangles; i++) {
            // Vertex 0
            v0.getCoord().set(coords.get(3 * coordIdx), coords.get(3 * coordIdx + 1), coords.get(3 * coordIdx + 2));
            if (texCoords != null) {
                v0.getTexCoord().set(texCoords.get(2 * coordIdx), texCoords.get(2 * coordIdx + 1));
            }
            if (colors != null) {
                v0.getColor().set(colors.get(4 * coordIdx), colors.get(4 * coordIdx + 1), colors.get(4 * coordIdx + 2), colors.get(4 * coordIdx + 3));
            }

            // Vertex 1
            v1.getCoord().set(coords.get(3 * (coordIdx + 1)), coords.get(3 * (coordIdx + 1) + 1), coords.get(3 * (coordIdx + 1) + 2));
            if (texCoords != null) {
                v1.getTexCoord().set(texCoords.get(2 * (coordIdx + 1)), texCoords.get(2 * (coordIdx + 1) + 1));
            }
            if (colors != null) {
                v1.getColor().set(colors.get(4 * (coordIdx + 1)), colors.get(4 * (coordIdx + 1) + 1), colors.get(4 * (coordIdx + 1) + 2), colors.get(4 * (coordIdx + 1) + 3));
            }

            // Vertex 2
            v2.getCoord().set(coords.get(3 * (coordIdx + 2)), coords.get(3 * (coordIdx + 2) + 1), coords.get(3 * (coordIdx + 2) + 2));
            if (texCoords != null) {
                v2.getTexCoord().set(texCoords.get(2 * (coordIdx + 2)), texCoords.get(2 * (coordIdx + 2) + 1));
            }
            if (colors != null) {
                v2.getColor().set(colors.get(4 * (coordIdx + 2)), colors.get(4 * (coordIdx + 2) + 1), colors.get(4 * (coordIdx + 2) + 2), colors.get(4 * (coordIdx + 2) + 3));
            }

            // Call callback
            cb.triangleCB(i, v0, 3 * i, v1, 3 * i + 1, v2, 3 * i + 2);

            coordIdx += 3;
        }
    }

    private Mat4f getTextureMatrix(final Texture texture) {
        textureMatrix.setIdentity();
        final TextureCoords coords = texture.getImageTexCoords();
        // Horizontal scale
        textureMatrix.setElement(0, 0, coords.right() - coords.left());
        // Vertical scale (may be negative if texture needs to be flipped vertically)
        final float vertScale = coords.top() - coords.bottom();
        textureMatrix.setElement(1, 1, vertScale);
        textureMatrix.setElement(0, 3, coords.left());
        textureMatrix.setElement(1, 3, coords.bottom());
        return textureMatrix;
    }
}
