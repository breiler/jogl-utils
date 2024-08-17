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

import static com.breiler.msg.math.MathUtils.minus;
import static com.breiler.msg.math.MathUtils.times;

import javax.vecmath.Vector3f;

/** Represents a line in 3D space. */

public class Line {
  private final Vector3f point;
  /** Normalized */
  private final Vector3f direction;
  /** For computing projections along line */
  private final Vector3f alongVec;

  /** Default constructor initializes line to point (0, 0, 0) and
      direction (1, 0, 0) */
  public Line() {
    point = new Vector3f(0, 0, 0);
    direction = new Vector3f(1, 0, 0);
    alongVec = new Vector3f();
    recalc();
  }

  /** Line goes in direction <b>direction</b> through the point
      <b>point</b>. <b>direction</b> does not need to be normalized but must
      not be the zero vector. */
  public Line(Vector3f direction, Vector3f point) {
    this.direction = new Vector3f(direction);
    this.direction.normalize();
    this.point = new Vector3f(point);
    alongVec = new Vector3f();
    recalc();
  }

  /** Setter does some work to maintain internal caches.
      <b>direction</b> does not need to be normalized but must not be
      the zero vector. */
  public void setDirection(Vector3f direction) {
    this.direction.set(direction);
    this.direction.normalize();
    recalc();
  }

  /** Direction is normalized internally, so <b>direction</b> is not
      necessarily equal to <code>plane.setDirection(direction);
      plane.getDirection();</code> */
  public Vector3f getDirection() {
    return direction;
  }

  /** Setter does some work to maintain internal caches. */
  public void setPoint(Vector3f point) {
    this.point.set(point);
    recalc();
  }

  public Vector3f getPoint() {
    return point;
  }



  //----------------------------------------------------------------------
  // Internals only below this point
  //
  
  private void recalc() {
    float denom = direction.lengthSquared();
    if (denom == 0.0f) {
      throw new RuntimeException("Line.recalc: ERROR: direction was the zero vector " +
                                 "(not allowed)");
    }
    alongVec.set(minus(point, times(direction, point.dot(direction))));
  }
}
