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
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author Omnaest
 */
class Path implements Serializable
{
  private static final long serialVersionUID = -6753433772711288639L;
  
  private final String[]    path;
  
  /**
   * @see Path
   */
  Path()
  {
    super();
    this.path = new String[] {};
  }
  
  /**
   * @see Path
   * @param canonicalPath
   */
  Path( String canonicalPath )
  {
    super();
    this.path = ListUtils.valueOf( Splitter.on( '.' ).split( canonicalPath ) ).toArray( new String[] {} );
  }
  
  /**
   * @see Path
   * @param path
   */
  Path( String[] path )
  {
    super();
    this.path = path;
  }
  
  /**
   * @see Path
   * @param parentPath
   * @param propertyName
   */
  Path( Path parentPath, String propertyName )
  {
    super();
    this.path = ArrayUtils.add( parentPath.getPath(), propertyName );
  }
  
  public String[] getPath()
  {
    return this.path;
  }
  
  public int size()
  {
    return this.path.length;
  }
  
  /**
   * Returns the path in the form "property1.property2.[...]"
   * 
   * @return
   */
  public String getCanonicalPath()
  {
    return Joiner.on( '.' ).join( this.path );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Path [path=" );
    builder.append( Arrays.toString( this.path ) );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode( this.path );
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
    if ( !( obj instanceof Path ) )
    {
      return false;
    }
    Path other = (Path) obj;
    if ( !Arrays.equals( this.path, other.path ) )
    {
      return false;
    }
    return true;
  }
  
}
