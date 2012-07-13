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
class PropertyNameAndTypeAndPath implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -9180764463885578940L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final String      propertyName;
  private final Class<?>    type;
  private final Path        path;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see PropertyNameAndTypeAndPath
   * @param propertyName
   * @param type
   * @param path
   */
  PropertyNameAndTypeAndPath( String propertyName, Class<?> type, String path )
  {
    super();
    this.propertyName = propertyName;
    this.type = type;
    this.path = new Path( path );
  }
  
  /**
   * @see PropertyNameAndTypeAndPath
   * @param propertyName
   * @param type
   * @param path
   */
  PropertyNameAndTypeAndPath( String propertyName, Class<?> type, Path path )
  {
    super();
    this.propertyName = propertyName;
    this.type = type;
    this.path = path;
  }
  
  /**
   * @see Path
   * @return
   */
  Path getPath()
  {
    return this.path;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "PropertyNameAndTypeAndPath [propertyName=" );
    builder.append( this.propertyName );
    builder.append( ", type=" );
    builder.append( this.type );
    builder.append( ", path=" );
    builder.append( this.path );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.path == null ) ? 0 : this.path.hashCode() );
    result = prime * result + ( ( this.propertyName == null ) ? 0 : this.propertyName.hashCode() );
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
    if ( !( obj instanceof PropertyNameAndTypeAndPath ) )
    {
      return false;
    }
    PropertyNameAndTypeAndPath other = (PropertyNameAndTypeAndPath) obj;
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
    if ( this.propertyName == null )
    {
      if ( other.propertyName != null )
      {
        return false;
      }
    }
    else if ( !this.propertyName.equals( other.propertyName ) )
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
  
  public String getPropertyName()
  {
    return this.propertyName;
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
}
