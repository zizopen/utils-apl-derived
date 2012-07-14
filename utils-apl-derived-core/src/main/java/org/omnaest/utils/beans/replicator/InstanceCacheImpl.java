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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @see InstanceCache
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class InstanceCacheImpl implements InstanceCache
{
  /* ************************************************** Constants *************************************************** */
  private static final long                              serialVersionUID                    = 8839858336083004326L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<InstanceCacheImpl.TypeAndInstance, Object> typeAndInstanceToReplicaInstanceMap = new ConcurrentHashMap<InstanceCacheImpl.TypeAndInstance, Object>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  private static class TypeAndInstance
  {
    private final Class<?> type;
    private final Object   instance;
    
    public TypeAndInstance( Class<?> type, Object instance )
    {
      super();
      this.type = type;
      this.instance = instance;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.instance == null ) ? 0 : this.instance.hashCode() );
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
      if ( !( obj instanceof InstanceCacheImpl.TypeAndInstance ) )
      {
        return false;
      }
      InstanceCacheImpl.TypeAndInstance other = (InstanceCacheImpl.TypeAndInstance) obj;
      if ( this.instance == null )
      {
        if ( other.instance != null )
        {
          return false;
        }
      }
      else if ( this.instance != other.instance )
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
  
  /* *************************************************** Methods **************************************************** */
  
  @Override
  public void addReplicaInstance( Class<?> type, Object instance, Object replicaInstance )
  {
    if ( replicaInstance != null )
    {
      final InstanceCacheImpl.TypeAndInstance key = new TypeAndInstance( type, instance );
      this.typeAndInstanceToReplicaInstanceMap.put( key, replicaInstance );
    }
  }
  
  @Override
  public Object getReplicaInstance( Class<?> type, Object instance )
  {
    final InstanceCacheImpl.TypeAndInstance key = new TypeAndInstance( type, instance );
    return this.typeAndInstanceToReplicaInstanceMap.get( key );
  }
  
}
