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

package com.breiler.msg.misc;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/** Represents a vertex on a primitive, including pieces of relevant
    information such as location, surface normal and texture
    coordinates. */

public class PrimitiveVertex implements Cloneable {
  private Vector3f coord;
  private Vector2f texCoord;
  private Vector4f color;
  private Vector3f normal;

  public PrimitiveVertex() {}

  public Object clone() {
    try {
      PrimitiveVertex vtx = (PrimitiveVertex) super.clone();
      if (coord != null) {
        vtx.setCoord(new Vector3f(coord));
      }
      if (texCoord != null) {
        vtx.setTexCoord(new Vector2f(texCoord));
      }
      if (color != null) {
        vtx.setColor(new Vector4f(color));
      }
      if (normal != null) {
        vtx.setNormal(new Vector3f(normal));
      }
      return vtx;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  /** Performs a "deep copy" of this PrimitiveVertex so that it shares
      none of its contents with this one. */
  public PrimitiveVertex copy() {
    return (PrimitiveVertex) clone();
  }

  /** Sets the coordinate in this PrimitiveVertex. Refers to the passed vector by reference. */
  public void  setCoord(Vector3f coord)       { this.coord = coord; }
  /** Returns the coordinate in this PrimitiveVertex, or null if it is not known. */
  public Vector3f getCoord()                  { return coord;       }

  /** Sets the texture coordinate in this PrimitiveVertex. Refers to the passed vector by reference. */
  public void  setTexCoord(Vector2f texCoord) { this.texCoord = texCoord; }
  /** Returns the texture coordinate in this PrimitiveVertex, or null if it is not known. */
  public Vector2f getTexCoord()               { return texCoord;          }

  /** Sets the color in this PrimitiveVertex. Refers to the passed vector by reference. */
  public void  setColor(Vector4f color)       { this.color = color; }
  /** Returns the color in this PrimitiveVertex, or null if it is not known. */
  public Vector4f getColor()                  { return color;       }

  /** Sets the normal in this PrimitiveVertex. Refers to the passed vector by reference. */
  public void  setNormal(Vector3f normal)     { this.normal = normal; }
  /** Returns the normal in this PrimitiveVertex, or null if it is not known. */
  public Vector3f getNormal()                 { return normal;       }
}
