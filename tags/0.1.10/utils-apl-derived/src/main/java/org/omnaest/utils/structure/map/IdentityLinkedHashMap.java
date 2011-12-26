/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.structure.map;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.IdentityArrayList;
import org.omnaest.utils.structure.collection.set.IdentityArrayListBasedSet;

/**
 * Similar to an {@link LinkedHashMap} but using the identity comparison "object == element" for resolution of keys and values.
 * 
 * @see IdentityHashMap
 * @see IdentityArrayList
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class IdentityLinkedHashMap<K, V> extends MapAbstract<K, V>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<K, V> map       = new IdentityHashMap<K, V>();
  protected List<K>   keyList   = new IdentityArrayList<K>();
  protected List<V>   valueList = new IdentityArrayList<V>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public V get( Object key )
  {
    return this.map.get( key );
  }
  
  @Override
  public V put( K key, V value )
  {
    //
    V retval = this.map.put( key, value );
    if ( retval == null )
    {
      this.keyList.add( key );
      this.valueList.add( value );
    }
    else
    {
      int indexOf = this.keyList.indexOf( key );
      this.keyList.set( indexOf, key );
      this.valueList.set( indexOf, value );
    }
    
    //
    return retval;
  }
  
  @Override
  public V remove( Object key )
  {
    //
    V retval = this.map.remove( key );
    
    //
    int indexOf = this.keyList.indexOf( key );
    if ( indexOf >= 0 )
    {
      this.keyList.remove( indexOf );
      this.valueList.remove( indexOf );
    }
    
    //
    return retval;
  }
  
  @Override
  public Set<K> keySet()
  {
    return new IdentityArrayListBasedSet<K>( this.keyList );
  }
  
  @Override
  public Collection<V> values()
  {
    return new IdentityArrayList<V>( this.valueList );
  }
  
}
