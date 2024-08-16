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
     * Gets the (i,j)th element of this matrix, where i is the row
     * index and j is the column index
     */
    public float get(int i, int j) {
        if (i == 0 && j == 0) {
            return getM00();
        } else if (i == 0 && j == 1) {
            return getM01();
        } else if (i == 0 && j == 2) {
            return getM02();
        } else if (i == 0 && j == 3) {
            return getM03();
        } else if (i == 1 && j == 0) {
            return getM10();
        } else if (i == 1 && j == 1) {
            return getM11();
        } else if (i == 1 && j == 2) {
            return getM12();
        } else if (i == 1 && j == 3) {
            return getM13();
        } else if (i == 2 && j == 0) {
            return getM20();
        } else if (i == 2 && j == 1) {
            return getM21();
        } else if (i == 2 && j == 2) {
            return getM22();
        } else if (i == 2 && j == 3) {
            return getM23();
        } else if (i == 3 && j == 0) {
            return getM30();
        } else if (i == 3 && j == 1) {
            return getM31();
        } else if (i == 3 && j == 2) {
            return getM32();
        } else if (i == 3 && j == 3) {
            return getM33();
        }

        return 0;
    }

    /**
     * Sets the (i,j)th element of this matrix, where i is the row
     * index and j is the column index
     */
    public void set(int i, int j, float val) {
        if (i == 0 && j == 0) {
            setM00(val);
        } else if (i == 0 && j == 1) {
            setM01(val);
        } else if (i == 0 && j == 2) {
            setM02(val);
        } else if (i == 0 && j == 3) {
            setM03(val);
        } else if (i == 1 && j == 0) {
            setM10(val);
        } else if (i == 1 && j == 1) {
            setM11(val);
        } else if (i == 1 && j == 2) {
            setM12(val);
        } else if (i == 1 && j == 3) {
            setM13(val);
        } else if (i == 2 && j == 0) {
            setM20(val);
        } else if (i == 2 && j == 1) {
            setM21(val);
        } else if (i == 2 && j == 2) {
            setM22(val);
        } else if (i == 2 && j == 3) {
            setM23(val);
        } else if (i == 3 && j == 0) {
            setM30(val);
        } else if (i == 3 && j == 1) {
            setM31(val);
        } else if (i == 3 && j == 2) {
            setM32(val);
        } else if (i == 3 && j == 3) {
            setM33(val);
        }
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
     * Gets the upper left 3x3 of this matrix as a rotation. Currently
     * does not work if there are scales. Ignores translation
     * component.
     */
    public void getRotation(Rotf rot) {
        rot.fromMatrix(this);
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
        t = get(0, 1);
        set(0, 1, get(1, 0));
        set(1, 0, t);
        t = get(0, 2);
        set(0, 2, get(2, 0));
        set(2, 0, t);
        t = get(1, 2);
        set(1, 2, get(2, 1));
        set(2, 1, t);
        // Transform negative translation by this
        Vec3f negTrans = new Vec3f(-get(0, 3), -get(1, 3), -get(2, 3));
        Vec3f trans = new Vec3f();
        xformDir(negTrans, trans);
        set(0, 3, trans.getX());
        set(1, 3, trans.getY());
        set(2, 3, trans.getZ());
    }

    /**
     * Multiply a 4D vector by this matrix. NOTE: src and dest must be
     * different vectors.
     */
    public void xformVec(Vec4f src, Vec4f dest) {
        for (int rc = 0; rc < 4; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 4; cc++) {
                tmp += get(rc, cc) * src.get(cc);
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
                tmp += get(rc, cc) * src.get(cc);
            }
            tmp += get(rc, 3);
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
                tmp += get(rc, cc) * src.get(cc);
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

    /**
     * Copies data in column-major (OpenGL format) order into passed
     * float array, which must have length 16 or greater.
     */
    public void getColumnMajorData(float[] out) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[4 * j + i] = get(i, j);
            }
        }
    }


    /**
     * Returns the matrix data in row-major format, which is the
     * opposite of OpenGL's convention.
     */
    public float[] getRowMajorData() {
        return new float[]{
                getM00(),
                getM01(),
                getM02(),
                getM03(),
                getM10(),
                getM11(),
                getM12(),
                getM13(),
                getM20(),
                getM21(),
                getM22(),
                getM23(),
                getM30(),
                getM31(),
                getM32(),
                getM33(),
        };
    }
}
