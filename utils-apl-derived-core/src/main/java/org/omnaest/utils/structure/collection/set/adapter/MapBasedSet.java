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
package org.omnaest.utils.structure.collection.set.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetAbstract;

/**
 * A {@link Set} based on a given {@link Map} implementation
 * 
 * @author Omnaest
 * @param <E>
 */
public class MapBasedSet<E> extends SetAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long    serialVersionUID = 59020838477262671L;
  private static final Object  MARKER_VALUE     = new Object();
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Map<E, Object> map;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see MapBasedSet
   * @param map
   *          {@link Map}
   */
  public MapBasedSet( Map<E, Object> map )
  {
    super();
    this.map = map;
  }
  
  @Override
  public int size()
  {
    return this.map.size();
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.map.containsKey( o );
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.map.keySet().iterator();
  }
  
  @Override
  public boolean add( E e )
  {
    return this.map.put( e, MARKER_VALUE ) != null;
  }
  
  @Override
  public boolean remove( Object o )
  {
    return this.map.remove( o ) != null;
  }
  
  @Override
  public void clear()
  {
    this.map.clear();
  }
  
  @Override
  public boolean containsAll( Collection<?> collection )
  {
    return this.map.keySet().containsAll( collection );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    return this.map.keySet().removeAll( c );
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    return this.map.keySet().retainAll( c );
  }
  
  @Override
  public Object[] toArray()
  {
    return this.map.keySet().toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    return this.map.keySet().toArray( a );
  }
  
  private Set<E> keySet()
  {
    return this.map.keySet();
  }
  
  @Override
  public String toString()
  {
    return String.valueOf( this.keySet() );
  }
  
}
