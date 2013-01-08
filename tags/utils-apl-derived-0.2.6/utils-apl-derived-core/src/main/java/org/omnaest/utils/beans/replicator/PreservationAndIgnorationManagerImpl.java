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

import java.util.HashSet;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link PreservationAndIgnorationManager} implementation
 * 
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class PreservationAndIgnorationManagerImpl implements PreservationAndIgnorationManager
{
  private static final long   serialVersionUID       = 353683779213474767L;
  
  private final Set<Class<?>> typePreservedSet       = new HashSet<Class<?>>();
  private final Set<Path>     pathPreservedSet       = new HashSet<Path>();
  
  private final Set<Class<?>> typeIgnoredSet         = new HashSet<Class<?>>();
  private final Set<Path>     pathIgnoredSet         = new HashSet<Path>();
  
  private int                 deepnessLevelIgnored   = -1;
  private int                 deepnessLevelPreserved = -1;
  
  @Override
  public boolean isPreservedType( Class<?> type )
  {
    return this.typePreservedSet.contains( type );
  }
  
  @Override
  public void addPreservedType( Class<?> type )
  {
    if ( type != null )
    {
      this.typePreservedSet.add( type );
    }
  }
  
  @Override
  public void addAllPreservedTypes( Iterable<? extends Class<?>> typeIterable )
  {
    SetUtils.addAll( this.typePreservedSet, typeIterable );
  }
  
  @Override
  public boolean isIgnoredType( Class<?> type )
  {
    return this.typeIgnoredSet.contains( type );
  }
  
  @Override
  public void addIgnoredType( Class<?> type )
  {
    if ( type != null )
    {
      this.typeIgnoredSet.add( type );
    }
  }
  
  @Override
  public void addAllIgnoredTypes( Iterable<? extends Class<?>> typeIterable )
  {
    SetUtils.addAll( this.typeIgnoredSet, typeIterable );
  }
  
  @Override
  public void addPreservedPath( String path )
  {
    if ( path != null )
    {
      SetUtils.add( this.pathPreservedSet, new Path( path ) );
    }
  }
  
  @Override
  public void addIgnoredPath( String path )
  {
    if ( path != null )
    {
      SetUtils.add( this.pathIgnoredSet, new Path( path ) );
    }
  }
  
  @Override
  public boolean isPreservedPath( Path path )
  {
    boolean retval = this.pathPreservedSet.contains( path );
    retval |= this.deepnessLevelPreserved >= 0 && path.size() >= this.deepnessLevelPreserved;
    return retval;
  }
  
  @Override
  public boolean isIgnoredPath( Path subPath )
  {
    boolean retval = this.deepnessLevelIgnored >= 0 && subPath.size() >= this.deepnessLevelIgnored;
    retval |= this.pathIgnoredSet.contains( subPath );
    return retval;
  }
  
  @Override
  public void setIgnoredDeepnessLevel( int deepnessLevel )
  {
    this.deepnessLevelIgnored = deepnessLevel;
  }
  
  @Override
  public void setPreservedDeepnessLevel( int deepnessLevel )
  {
    this.deepnessLevelPreserved = deepnessLevel;
  }
}
