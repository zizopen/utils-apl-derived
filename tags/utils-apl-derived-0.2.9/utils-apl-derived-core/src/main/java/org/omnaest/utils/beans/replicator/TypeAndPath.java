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
package org.omnaest.utils.beans.replicator;

import java.io.Serializable;

/**
 * @author Omnaest
 */
class TypeAndPath implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 2177856576905085345L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Path        path;
  private final Class<?>    type;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TypeAndPath
   * @param path
   * @param type
   */
  TypeAndPath( String path, Class<?> type )
  {
    super();
    this.path = new Path( path );
    this.type = type;
  }
  
  /**
   * @see TypeAndPath
   * @param path
   * @param type
   */
  TypeAndPath( Path path, Class<?> type )
  {
    super();
    this.path = path;
    this.type = type;
  }
  
  public Path getPath()
  {
    return this.path;
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "TypeAndPath [path=" );
    builder.append( this.path );
    builder.append( ", type=" );
    builder.append( this.type );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.path == null ) ? 0 : this.path.hashCode() );
    result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
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
    if ( !( obj instanceof TypeAndPath ) )
    {
      return false;
    }
    TypeAndPath other = (TypeAndPath) obj;
    if ( this.path == null )
    {
      if ( other.path != null )
      {
        return false;
      }
    }
    else if ( !this.path.equals( other.path ) )
    {
      return false;
    }
    if ( this.type == null )
    {
      if ( other.type != null )
      {
        return false;
      }
    }
    else if ( !this.type.equals( other.type ) )
    {
      return false;
    }
    return true;
  }
  
}
