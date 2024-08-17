package com.breiler.msg.math;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class MathUtils {


    /**
     * Copies data in column-major (OpenGL format) order into a
     * float array, which will have length 16.
     *
     * @return the matrix as a float array
     */
    public static float[] getColumnMajorData(Matrix4f matrix) {
        float[] out = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[4 * j + i] = matrix.getElement(i, j);
            }
        }
        return out;
    }

    /**
     * Returns the matrix data in row-major format, which is the
     * opposite of OpenGL's convention.
     */
    public static float[] getRowMajorData(Matrix4f matrix) {
        return new float[]{
                matrix.getM00(),
                matrix.getM01(),
                matrix.getM02(),
                matrix.getM03(),
                matrix.getM10(),
                matrix.getM11(),
                matrix.getM12(),
                matrix.getM13(),
                matrix.getM20(),
                matrix.getM21(),
                matrix.getM22(),
                matrix.getM23(),
                matrix.getM30(),
                matrix.getM31(),
                matrix.getM32(),
                matrix.getM33(),
        };
    }

    /**
     * Transforms a 3D vector as though it had a homogeneous coordinate
     * and assuming that this matrix represents only rigid
     * transformations; i.e., is not a full transformation. NOTE: src
     * and dest must be different vectors.
     */
    public static void xformPt(Matrix4f matrix, Vec3f src, Vec3f dest) {
        for (int rc = 0; rc < 3; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 3; cc++) {
                tmp += matrix.getElement(rc, cc) * getVectorElement(src, cc);
            }
            tmp += matrix.getElement(rc, 3);
            setVectorElement(dest, rc, tmp);
        }
    }

    public static void setVectorElement(Vector3f vector, int element, float value) {
        switch (element) {
            case 0:
                vector.setX(value);
                break;
            case 1:
                vector.setY(value);
                break;
            case 2:
                vector.setZ(value);
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Gets the ith component, 0 &lt;= i &lt; 3
     */
    public static float getVectorElement(Vector3f vector, int element) {
        switch (element) {
            case 0:
                return vector.getX();
            case 1:
                return vector.getY();
            case 2:
                return vector.getZ();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Transforms src using only the upper left 3x3. NOTE: src and dest
     * must be different vectors.
     */
    public static void xformDir(Matrix4f matrix, Vec3f src, Vec3f dest) {
        for (int rc = 0; rc < 3; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 3; cc++) {
                tmp += matrix.getElement(rc, cc) * getVectorElement(src, cc);
            }
            setVectorElement(dest, rc, tmp);
        }
    }

    /**
     * Transforms the given line (origin plus direction) by this
     * matrix.
     */
    public static Line xformLine(Matrix4f matrix, Line line) {
        Vec3f pt = new Vec3f();
        Vec3f dir = new Vec3f();

        MathUtils.xformPt(matrix, line.getPoint(), pt);
        MathUtils.xformDir(matrix, line.getDirection(), dir);
        return new Line(dir, pt);
    }

    /**
     * Multiply a 4D vector by this matrix. NOTE: src and dest must be
     * different vectors.
     */
    public static void xformVec(Matrix4f matrix, Vec4f src, Vec4f dest) {
        for (int rc = 0; rc < 4; rc++) {
            float tmp = 0.0f;
            for (int cc = 0; cc < 4; cc++) {
                tmp += matrix.getElement(rc, cc) * src.get(cc);
            }
            dest.set(rc, tmp);
        }
    }

    /**
     * Inverts this matrix assuming that it represents a rigid
     * transform (i.e., some combination of rotations and
     * translations). Assumes column vectors. Algorithm: transposes
     * upper left 3x3; negates translation in rightmost column and
     * transforms by inverted rotation.
     */
    public static void invertRigid(Matrix4f matrix) {
        float t;
        // Transpose upper left 3x3
        t = matrix.getElement(0, 1);
        matrix.setElement(0, 1, matrix.getElement(1, 0));
        matrix.setElement(1, 0, t);
        t = matrix.getElement(0, 2);
        matrix.setElement(0, 2, matrix.getElement(2, 0));
        matrix.setElement(2, 0, t);
        t = matrix.getElement(1, 2);
        matrix.setElement(1, 2, matrix.getElement(2, 1));
        matrix.setElement(2, 1, t);
        // Transform negative translation by this
        Vec3f negTrans = new Vec3f(-matrix.getElement(0, 3), -matrix.getElement(1, 3), -matrix.getElement(2, 3));
        Vec3f trans = new Vec3f();
        MathUtils.xformDir(matrix, negTrans, trans);
        matrix.setElement(0, 3, trans.getX());
        matrix.setElement(1, 3, trans.getY());
        matrix.setElement(2, 3, trans.getZ());
    }


    /**
     * Returns this * val; creates new vector
     */
    public static Vec3f times(Vector3f vector, float val) {
        Vec3f tmp = new Vec3f(vector);
        tmp.scale(val);
        return tmp;
    }

    /**
     * Returns vector1 + vector2; creates new vector
     */
    public static Vec3f plus(Vector3f vector1, Vec3f vector2) {
        Vec3f tmp = new Vec3f(vector1);
        tmp.add(vector2);
        return tmp;
    }

    /**
     * Returns this - vector2; creates new vector
     */
    public static Vec3f minus(Vector3f vector1, Vector3f vector2) {
        Vec3f tmp = new Vec3f(vector1);
        tmp.sub(vector2);
        return tmp;
    }


    /**
     * Returns this cross arg; creates new vector
     */
    public static Vec3f cross(Vector3f vector1, Vector3f arg) {
        Vec3f tmp = new Vec3f();
        tmp.cross(vector1, arg);
        return tmp;
    }
}
