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
import com.breiler.msg.actions.RayPickAction;
import com.breiler.msg.elements.GLModelMatrixElement;
import com.breiler.msg.elements.GLProjectionMatrixElement;
import com.breiler.msg.elements.GLViewingMatrixElement;
import com.breiler.msg.elements.ModelMatrixElement;
import com.breiler.msg.elements.ProjectionMatrixElement;
import com.breiler.msg.elements.ViewingMatrixElement;
import com.breiler.msg.math.Line;
import com.breiler.msg.math.MathUtils;
import static com.breiler.msg.math.MathUtils.minus;
import com.breiler.msg.math.Rotf;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a camera which is used to view the scene. The camera
 * should be added to the scene graph before the geometry it is
 * intended to view. <P>
 * <p>
 * The camera's default parameters are a position at (0, 0, 1),
 * facing down the negative Z axis with the Y axis up, an aspect
 * ratio of 1.0, a near distance of 1.0, a far distance of 100.0, and
 * a focal distance of 10.0.
 */

public abstract class Camera extends Node {
    static {
        // Enable the elements this node affects for known actions
        // Note that all of these elements are interdependent
        GLModelMatrixElement.enable(GLRenderAction.getDefaultState());
        GLProjectionMatrixElement.enable(GLRenderAction.getDefaultState());
        GLViewingMatrixElement.enable(GLRenderAction.getDefaultState());

        ModelMatrixElement.enable(RayPickAction.getDefaultState());
        ProjectionMatrixElement.enable(RayPickAction.getDefaultState());
        ViewingMatrixElement.enable(RayPickAction.getDefaultState());
    }

    private final Set<CameraListener> cameraListeners = ConcurrentHashMap.newKeySet();
    private final Vector3f position;
    private final Rotf orientation;
    protected boolean projDirty;
    protected boolean viewDirty;
    protected Matrix4f projMatrix;
    protected Matrix4f viewMatrix;
    private float aspectRatio = 1.0f;
    private float nearDistance = 1.0f;
    private float farDistance = 100.0f;
    private float focalDistance = 10.0f;
    public Camera() {
        position = new Vector3f(0, 0, 1);
        orientation = new Rotf();

        projMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        projDirty = true;
        viewDirty = true;
    }

    public void addCameraListener(CameraListener cameraListener) {
        cameraListeners.add(cameraListener);
    }

    /**
     * Returns the position of the camera.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the position of the camera.
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
        viewDirty = true;
        cameraListeners.forEach(CameraListener::positionChanged);
    }

    /**
     * Returns the orientation of the camera.
     */
    public Rotf getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of the camera.
     */
    public void setOrientation(Rotf orientation) {
        this.orientation.set(orientation);
        viewDirty = true;
        cameraListeners.forEach(CameraListener::positionChanged);
    }

    /**
     * Returns the aspect ratio of the camera -- the width of the
     * viewport divided by the height of the viewport.
     */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /**
     * Sets the aspect ratio of the camera -- the width of the viewport
     * divided by the height of the viewport.
     */
    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio == this.aspectRatio)
            return;
        this.aspectRatio = aspectRatio;
        projDirty = true;
        cameraListeners.forEach(CameraListener::positionChanged);
    }

    /**
     * Returns the distance from the eye point to the near clipping plane.
     *
     * @return the near distance
     */
    public float getNearDistance() {
        return nearDistance;
    }

    /**
     * Sets the distance from the eye point to the near clipping plane.
     *
     * @param nearDistance the near distance
     */
    public void setNearDistance(float nearDistance) {
        this.nearDistance = nearDistance;
        projDirty = true;
        cameraListeners.forEach(CameraListener::positionChanged);
    }

    /**
     * Returns the distance from the eye point to the far clipping plane.
     *
     * @return the far distance
     */
    public float getFarDistance() {
        return farDistance;
    }

    /**
     * Sets the distance from the eye point to the far clipping plane.
     *
     * @param farDistance the far distance
     */
    public void setFarDistance(float farDistance) {
        this.farDistance = farDistance;
        projDirty = true;
        cameraListeners.forEach(CameraListener::positionChanged);
    }

    /**
     * Returns the distance from the eye point to the focal point of
     * the scene. This is only used for mouse-based interaction with
     * the scene and is not factored in to the rendering process.
     */
    public float getFocalDistance() {
        return focalDistance;
    }

    /**
     * Sets the distance from the eye point to the focal point of the
     * scene. This is only used for mouse-based interaction with the
     * scene and is not factored in to the rendering process.
     */
    public void setFocalDistance(float focalDistance) {
        this.focalDistance = focalDistance;
        projDirty = true;
    }

    /**
     * Returns the viewing matrix associated with this camera's parameters.
     */
    public Matrix4f getViewingMatrix() {
        if (viewDirty) {
            viewMatrix.setIdentity();
            viewDirty = false;

            getOrientation().toMatrix(viewMatrix);
            viewMatrix.setTranslation(getPosition());
            MathUtils.invertRigid(viewMatrix);
        }

        return viewMatrix;
    }

    /**
     * Returns the projection matrix associated with this camera's parameters.
     */
    public abstract Matrix4f getProjectionMatrix();

    /**
     * Un-projects the given on-screen point to a line in 3D space
     * which can be used for picking or other operations. The x and y
     * coordinates of the point must be in normalized coordinates,
     * where (0, 0) is the lower-left corner of the viewport and (1, 1)
     * is the upper-right. Allocates new storage for the returned
     * Line.
     */
    public Line unproject(Vector2f point) {
        Line line = new Line();
        unproject(point, line);
        return line;
    }

    /**
     * Un-projects the given on-screen point in to the given line in 3D
     * space (in world coordinates) which can be used for picking or
     * other operations. The x and y coordinates of the point must be
     * in normalized coordinates, where (0, 0) is the lower-left corner
     * of the viewport and (1, 1) is the upper-right.
     */
    public void unproject(Vector2f point, Line line) throws CameraException {
        // First, we are going to compute the 3D point which corresponds
        // to the given point on the near plane. Map the screen
        // coordinates to the (-1, 1) range. Note that because the camera
        // points down the -Z axis, we use as the initial Z coordinate of
        // the 3D point we need to unproject the negation of the near
        // distance.
        Vector4f pt3d = new Vector4f(2 * point.getX() - 1,
                2 * point.getY() - 1,
                -getNearDistance(),
                1);
        // Compute the cumulative view and projection matrices
        Matrix4f mat = new Matrix4f();
        mat.mul(getProjectionMatrix(), getViewingMatrix());
        // Compute the inverse of this matrix
        mat.invert();
        // Multiply
        Vector4f unproj = new Vector4f();
        MathUtils.xformVec(mat, pt3d, unproj);
        if (unproj.getW() == 0) {
            // FIXME: is this the right exception to throw in this case?
            throw new CameraException();
        }
        float ooW = 1.0f / unproj.getW();
        Vector3f to = new Vector3f(unproj.getX() * ooW,
                unproj.getY() * ooW,
                unproj.getZ() * ooW);
        Vector3f from = getRayStartPoint(point, to);
        Vector3f dir = minus(to, from);

        //    System.err.println("unprojected point: " + to);
        //    System.err.println("unprojected dir  : " + dir);

        line.setPoint(from);
        line.setDirection(dir);
    }

    /**
     * Computes the start point of a ray for picking, given a point in
     * normalized screen coordinates ((0, 0) to (1, 1)) and a 3D point
     * which that point unprojects to.
     */
    protected abstract Vector3f getRayStartPoint(Vector2f point, Vector3f unprojectedPoint);

    public void doAction(Action action) {

        if (ViewingMatrixElement.isEnabled(action.getState())) {
            ViewingMatrixElement.set(action.getState(), getViewingMatrix());
        }
        if (ProjectionMatrixElement.isEnabled(action.getState())) {
            ProjectionMatrixElement.set(action.getState(), getProjectionMatrix());
        }
    }

    public void rayPick(RayPickAction action) {
        doAction(action);
        action.recomputeRay(this);
    }
}
