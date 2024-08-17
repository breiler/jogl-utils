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

package com.breiler.msg.math;

import javax.vecmath.Matrix4f;

/**
 * A (very incomplete) 4x4 matrix class. Representation assumes
 * row-major order and multiplication by column vectors on the
 * right.
 */

public class Mat4f extends Matrix4f {


    /**
     * Creates new matrix initialized to the zero matrix
     */
    public Mat4f() {
        super();
    }

    /**
     * Creates new matrix initialized to argument's contents
     */
    public Mat4f(Matrix4f arg) {
        super(arg);
    }

    /**
     * Sets the rotation component of this matrix (i.e., the upper left
     * 3x3) without touching any of the other parts of the matrix
     *
     * @return this matrix for fluid operations
     */
    public Mat4f setRotation(Rotf rot) {
        rot.toMatrix(this);
        return this;
    }

    /**
     * Inverts this matrix assuming that it represents a rigid
     * transform (i.e., some combination of rotations and
     * translations). Assumes column vectors. Algorithm: transposes
     * upper left 3x3; negates translation in rightmost column and
     * transforms by inverted rotation.
     */
    public void invertRigid() {
        float t;
        // Transpose upper left 3x3
        t = getElement(0, 1);
        setElement(0, 1, getElement(1, 0));
        setElement(1, 0, t);
        t = getElement(0, 2);
        setElement(0, 2, getElement(2, 0));
        setElement(2, 0, t);
        t = getElement(1, 2);
        setElement(1, 2, getElement(2, 1));
        setElement(2, 1, t);
        // Transform negative translation by this
        Vec3f negTrans = new Vec3f(-getElement(0, 3), -getElement(1, 3), -getElement(2, 3));
        Vec3f trans = new Vec3f();
        xformDir(negTrans, trans);
        setElement(0, 3, trans.getX());
        setElement(1, 3, trans.getY());
        setElement(2, 3, trans.getZ());
    }

    /**
     * Multiply a 4D vector by this matrix. NOTE: src and dest must be
     * different vectors.
     */
    public void xformVec(Vec4f src, Vec4f dest) {
        for (int rc = 0; rc < 4; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 4; cc++) {
                tmp += getElement(rc, cc) * src.get(cc);
            }
            dest.set(rc, tmp);
        }
    }

    /**
     * Transforms a 3D vector as though it had a homogeneous coordinate
     * and assuming that this matrix represents only rigid
     * transformations; i.e., is not a full transformation. NOTE: src
     * and dest must be different vectors.
     */
    public void xformPt(Vec3f src, Vec3f dest) {
        for (int rc = 0; rc < 3; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 3; cc++) {
                tmp += getElement(rc, cc) * src.get(cc);
            }
            tmp += getElement(rc, 3);
            dest.set(rc, tmp);
        }
    }

    /**
     * Transforms src using only the upper left 3x3. NOTE: src and dest
     * must be different vectors.
     */
    public void xformDir(Vec3f src, Vec3f dest) {
        for (int rc = 0; rc < 3; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 3; cc++) {
                tmp += getElement(rc, cc) * src.get(cc);
            }
            dest.set(rc, tmp);
        }
    }

    /**
     * Transforms the given line (origin plus direction) by this
     * matrix.
     */
    public Line xformLine(Line line) {
        Vec3f pt = new Vec3f();
        Vec3f dir = new Vec3f();
        xformPt(line.getPoint(), pt);
        xformDir(line.getDirection(), dir);
        return new Line(dir, pt);
    }
}
