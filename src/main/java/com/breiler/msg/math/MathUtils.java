package com.breiler.msg.math;

import javax.vecmath.Matrix4f;

public class MathUtils {



    /**
     * Copies data in column-major (OpenGL format) order into passed
     * float array, which must have length 16 or greater.
     */
    public static void getColumnMajorData(Matrix4f matrix, float[] out) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[4 * j + i] = matrix.getElement(i, j);
            }
        }
    }

    /**
     * Copies data in column-major (OpenGL format) order into passed
     * float array, which must have length 16 or greater.
     *
     * @return the matrix as a float array
     */
    public static float[] getColumnMajorData(Matrix4f matrix) {
        float[] out = new float[16];
        getColumnMajorData(matrix, out);
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
}
