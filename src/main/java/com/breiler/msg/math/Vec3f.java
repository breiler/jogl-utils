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

import javax.vecmath.Vector3f;

/**
 * 3-element single-precision vector
 */

public class Vec3f extends Vector3f {
    public static final Vec3f X_AXIS = new Vec3f(1, 0, 0);
    public static final Vec3f Y_AXIS = new Vec3f(0, 1, 0);
    public static final Vec3f Z_AXIS = new Vec3f(0, 0, 1);

    public Vec3f() {
    }

    public Vec3f(Vec3f arg) {
        set(arg);
    }

    public Vec3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vec3f(double x, double y, double z) {
        set((float) x, (float) y, (float) z);
    }

    /**
     * Sets the ith component, 0 &lt;= i &lt; 3
     */
    public void set(int i, float val) {
        switch (i) {
            case 0:
                x = val;
                break;
            case 1:
                y = val;
                break;
            case 2:
                z = val;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Gets the ith component, 0 &lt;= i &lt; 3
     */
    public float get(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Returns this * val; creates new vector
     */
    public Vec3f times(float val) {
        Vec3f tmp = new Vec3f(this);
        tmp.scale(val);
        return tmp;
    }

    /**
     * Returns this + arg; creates new vector
     */
    public Vec3f plus(Vec3f arg) {
        Vec3f tmp = new Vec3f();
        tmp.add(this, arg);
        return tmp;
    }


    /**
     * Returns this - arg; creates new vector
     */
    public Vec3f minus(Vector3f arg) {
        Vec3f tmp = new Vec3f();
        tmp.sub(this, arg);
        return tmp;
    }


    /**
     * Returns this cross arg; creates new vector
     */
    public Vec3f cross(Vector3f arg) {
        Vec3f tmp = new Vec3f();
        tmp.cross(this, arg);
        return tmp;
    }
}
