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

import java.util.Collection;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetUtils;

import com.google.common.collect.ImmutableSet;

/**
 * {@link PreservedTypeInstanceManager} implementation
 * 
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class PreservedTypeInstanceManagerImpl implements PreservedTypeInstanceManager
{
  private static final long serialVersionUID = 353683779213474767L;
  
  private Set<Class<?>>     typeSet          = ImmutableSet.<Class<?>> of();
  
  @Override
  public boolean contains( Class<?> type )
  {
    return this.typeSet.contains( type );
  }
  
  @Override
  public void add( Class<?> type )
  {
    this.typeSet = ImmutableSet.<Class<?>> builder().addAll( this.typeSet ).add( type ).build();
  }
  
  @Override
  public void remove( Class<?> type )
  {
    this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.remove( this.typeSet, type ) ).add( type ).build();
  }
  
  @Override
  public void addAll( Collection<? extends Class<?>> typeCollection )
  {
    this.typeSet = ImmutableSet.<Class<?>> builder().addAll( this.typeSet ).addAll( typeCollection ).build();
  }
  
  @Override
  public void retainAll( Collection<? extends Class<?>> typeCollection )
  {
    this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.retainAll( this.typeSet, typeCollection ) ).build();
  }
  
  @Override
  public void removeAll( Collection<? extends Class<?>> typeCollection )
  {
    this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.removeAll( this.typeSet, typeCollection ) ).build();
  }
  
}
