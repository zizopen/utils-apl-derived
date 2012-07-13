/*******************************************************************************
 * Copyright 2012 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.beans.replicator2;

import java.io.Serializable;

/**
 * @author Omnaest
 */
class TypeAndType implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -5394700948131761785L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<?>    typeFrom;
  private final Class<?>    typeTo;
  
  /* *************************************************** Methods **************************************************** */
  /**
   * @see TypeAndType
   * @param typeFrom
   * @param typeTo
   */
  TypeAndType( Class<?> typeFrom, Class<?> typeTo )
  {
    super();
    this.typeFrom = typeFrom;
    this.typeTo = typeTo;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "TypeAndType [typeFrom=" );
    builder.append( this.typeFrom );
    builder.append( ", typeTo=" );
    builder.append( this.typeTo );
    builder.append( "]" );
    return builder.toString();
  }
  
  public Class<?> getTypeFrom()
  {
    return this.typeFrom;
  }
  
  public Class<?> getTypeTo()
  {
    return this.typeTo;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.typeFrom == null ) ? 0 : this.typeFrom.hashCode() );
    result = prime * result + ( ( this.typeTo == null ) ? 0 : this.typeTo.hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof TypeAndType ) )
    {
      return false;
    }
    TypeAndType other = (TypeAndType) obj;
    if ( this.typeFrom == null )
    {
      if ( other.typeFrom != null )
      {
        return false;
      }
    }
    else if ( !this.typeFrom.equals( other.typeFrom ) )
    {
      return false;
    }
    if ( this.typeTo == null )
    {
      if ( other.typeTo != null )
      {
        return false;
      }
    }
    else if ( !this.typeTo.equals( other.typeTo ) )
    {
      return false;
    }
    return true;
  }
  
}
