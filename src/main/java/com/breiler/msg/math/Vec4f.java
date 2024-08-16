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

import javax.vecmath.Vector4f;

/** 4-element single-precision vector */

public class Vec4f extends Vector4f {

  public Vec4f() {}

  public Vec4f(Vector4f arg) {
    set(arg);
  }

  public Vec4f(float x, float y, float z, float w) {
    set(x, y, z, w);
  }


  /** Sets the ith component, 0 &lt;= i &lt; 4 */
  public void set(int i, float val) {
    switch (i) {
    case 0: x = val; break;
    case 1: y = val; break;
    case 2: z = val; break;
    case 3: w = val; break;
    default: throw new IndexOutOfBoundsException();
    }
  }

  /** Gets the ith component, 0 &lt;= i &lt; 4 */
  public float get(int i) {
    switch (i) {
    case 0: return x;
    case 1: return y;
    case 2: return z;
    case 3: return w;
    default: throw new IndexOutOfBoundsException();
    }
  }


  public String toString() {
    return "(" + x + ", " + y + ", " + z + ", " + w + ")";
  }
}
