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

/**
 * Represents a rotation in 3D space with single-precision
 * components. Uses a quaternion as the internal representation.
 */

public class Rotf {
    private static final float EPSILON = 1.0e-7f;

    // Representation is a quaternion. Element 0 is the scalar part (=
    // cos(theta/2)), elements 1..3 the imaginary/"vector" part (=
    // sin(theta/2) * axis).
    private float w;
    private float x;
    private float y;
    private float z;

    /**
     * Default constructor initializes to the identity quaternion
     */
    public Rotf() {
        init();
    }

    /**
     * Axis does not need to be normalized but must not be the zero
     * vector. Angle is in radians.
     */
    public Rotf(Vec3f axis, float angle) {
        set(axis, angle);
    }

    /**
     * Re-initialize this quaternion to be the identity quaternion "e"
     * (i.e., no rotation)
     */
    public void init() {
        w = 1;
        x = y = z = 0;
    }


    /**
     * Axis does not need to be normalized but must not be the zero
     * vector. Angle is in radians.
     */
    public void set(Vec3f axis, float angle) {
        float halfTheta = angle / 2.0f;
        w = (float) Math.cos(halfTheta);
        float sinHalfTheta = (float) Math.sin(halfTheta);
        Vec3f realAxis = new Vec3f(axis);
        realAxis.normalize();
        x = realAxis.getX() * sinHalfTheta;
        y = realAxis.getY() * sinHalfTheta;
        z = realAxis.getZ() * sinHalfTheta;
    }

    /**
     * Sets this rotation to the contents of the passed one.
     */
    public void set(Rotf arg) {
        w = arg.w;
        x = arg.x;
        y = arg.y;
        z = arg.z;
    }

    /**
     * Sets this rotation to that which will rotate vector "from" into
     * vector "to". from and to do not have to be the same length.
     */
    public void set(Vec3f from, Vec3f to) {
        Vec3f axis = from.cross(to);
        if (axis.lengthSquared() < EPSILON) {
            init();
            return;
        }
        float dotp = from.dot(to);
        float denom = from.length() * to.length();
        if (denom < EPSILON) {
            init();
            return;
        }
        dotp /= denom;
        set(axis, (float) Math.acos(dotp));
    }

    /**
     * Returns angle (in radians) and mutates the given vector to be
     * the axis.
     */
    public float get(Vec3f axis) {
        // FIXME: Is this numerically stable? Is there a better way to
        // extract the angle from a quaternion?
        // NOTE: remove (float) to illustrate compiler bug
        float retval = (float) (2.0f * Math.acos(w));
        axis.set(x, y, z);
        float len = axis.length();
        if (len == 0.0f) {
            axis.set(0, 0, 1);
        } else {
            axis.scale(1.0f / len);
        }
        return retval;
    }

    /**
     * Compose two rotations: this = A * B in that order. NOTE that
     * because we assume a column vector representation that this
     * implies that a vector rotated by the cumulative rotation will be
     * rotated first by B, then A. NOTE: "this" must be different than
     * both a and b.
     */
    public Rotf mul(Rotf a, Rotf b) {
        this.w = (a.w * b.w - a.x * b.x -
                a.y * b.y - a.z * b.z);
        this.x = (a.w * b.x + a.x * b.w +
                a.y * b.z - a.z * b.y);
        this.y = (a.w * b.y + a.y * b.w -
                a.x * b.z + a.z * b.x);
        this.z = (a.w * b.z + a.z * b.w +
                a.x * b.y - a.y * b.x);
        return this;
    }

    /**
     * Turns this rotation into a 3x3 rotation matrix. NOTE: only
     * mutates the upper-left 3x3 of the passed Mat4f. Implementation
     * from B. K. P. Horn's <u>Robot Vision</u> textbook.
     */
    public void toMatrix(Mat4f mat) {
        float q00 = w * w;
        float q11 = x * x;
        float q22 = y * y;
        float q33 = z * z;
        // Diagonal elements
        mat.set(0, 0, q00 + q11 - q22 - q33);
        mat.set(1, 1, q00 - q11 + q22 - q33);
        mat.set(2, 2, q00 - q11 - q22 + q33);
        // 0,1 and 1,0 elements
        float q03 = w * z;
        float q12 = x * y;
        mat.set(0, 1, 2.0f * (q12 - q03));
        mat.set(1, 0, 2.0f * (q03 + q12));
        // 0,2 and 2,0 elements
        float q02 = w * y;
        float q13 = x * z;
        mat.set(0, 2, 2.0f * (q02 + q13));
        mat.set(2, 0, 2.0f * (q13 - q02));
        // 1,2 and 2,1 elements
        float q01 = w * x;
        float q23 = y * z;
        mat.set(1, 2, 2.0f * (q23 - q01));
        mat.set(2, 1, 2.0f * (q01 + q23));
    }

    /**
     * Turns the upper left 3x3 of the passed matrix into a rotation.
     * Implementation from Watt and Watt, <u>Advanced Animation and
     * Rendering Techniques</u>.
     *
     * @see Mat4f#getRotation
     */
    public void fromMatrix(Mat4f mat) {
        // FIXME: Should reimplement to follow Horn's advice of using
        // eigenvector decomposition to handle roundoff error in given
        // matrix.

        float tr, s;
        int i, j, k;

        tr = mat.get(0, 0) + mat.get(1, 1) + mat.get(2, 2);
        if (tr > 0.0) {
            s = (float) Math.sqrt(tr + 1.0f);
            w = s * 0.5f;
            s = 0.5f / s;
            x = (mat.get(2, 1) - mat.get(1, 2)) * s;
            y = (mat.get(0, 2) - mat.get(2, 0)) * s;
            z = (mat.get(1, 0) - mat.get(0, 1)) * s;
        } else {
            i = 0;
            if (mat.get(1, 1) > mat.get(0, 0))
                i = 1;
            if (mat.get(2, 2) > mat.get(i, i))
                i = 2;
            j = (i + 1) % 3;
            k = (j + 1) % 3;
            s = (float) Math.sqrt((mat.get(i, i) - (mat.get(j, j) + mat.get(k, k))) + 1.0f);
            setQ(i + 1, s * 0.5f);
            s = 0.5f / s;
            w = (mat.get(k, j) - mat.get(j, k)) * s;
            setQ(j + 1, (mat.get(j, i) + mat.get(i, j)) * s);
            setQ(k + 1, (mat.get(k, i) + mat.get(i, k)) * s);
        }
    }

    /**
     * Rotate a vector by this quaternion. Implementation is from
     * Horn's <u>Robot Vision</u>. NOTE: src and dest must be different
     * vectors.
     */
    public void rotateVector(Vec3f src, Vec3f dest) {
        Vec3f qVec = new Vec3f(x, y, z);
        Vec3f qCrossX = qVec.cross(src);
        Vec3f qCrossXCrossQ = qCrossX.cross(qVec);
        qCrossX.scale(2.0f * w);
        qCrossXCrossQ.scale(-2.0f);
        dest.add(src, qCrossX);
        dest.add(dest, qCrossXCrossQ);
    }

    /**
     * Rotate a vector by this quaternion, returning newly-allocated result.
     */
    public Vec3f rotateVector(Vec3f src) {
        Vec3f tmp = new Vec3f();
        rotateVector(src, tmp);
        return tmp;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    private void setQ(int i, float val) {
        switch (i) {
            case 0:
                w = val;
                break;
            case 1:
                x = val;
                break;
            case 2:
                y = val;
                break;
            case 3:
                z = val;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
