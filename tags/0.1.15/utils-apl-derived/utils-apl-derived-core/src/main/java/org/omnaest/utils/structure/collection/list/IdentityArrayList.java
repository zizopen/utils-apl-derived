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
package org.omnaest.utils.structure.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import org.omnaest.utils.structure.collection.CollectionUtils;

/**
 * Uses the "object == element" comparison instead of the equals comparison for methods navigating to the elements from the
 * {@link List} by given element instances.
 * 
 * @see IdentityHashMap
 * @see #indexOf(Object)
 * @see #lastIndexOf(Object)
 * @see #remove(Object)
 * @see #removeAll(java.util.Collection)
 * @see #retainAll(java.util.Collection)
 * @see #contains(Object)
 * @see #containsAll(java.util.Collection)
 * @see ArrayList
 * @author Omnaest
 * @param <E>
 */
public class IdentityArrayList<E> extends ListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 8376220411795363321L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<E>         list             = new ArrayList<E>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param collection
   */
  public IdentityArrayList( Collection<E> collection )
  {
    super();
    
    this.addAll( collection );
  }
  
  /**
   * 
   */
  public IdentityArrayList()
  {
    super();
  }
  
  /**
   * @return
   * @see java.util.List#size()
   */
  @Override
  public int size()
  {
    return this.list.size();
  }
  
  /**
   * @param e
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  @Override
  public boolean add( E e )
  {
    return this.list.add( e );
  }
  
  /**
   * @see java.util.List#clear()
   */
  @Override
  public void clear()
  {
    this.list.clear();
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#get(int)
   */
  @Override
  public E get( int index )
  {
    return this.list.get( index );
  }
  
  /**
   * @param index
   * @param element
   * @return
   * @see java.util.List#set(int, java.lang.Object)
   */
  @Override
  public E set( int index, E element )
  {
    return this.list.set( index, element );
  }
  
  /**
   * @param index
   * @param element
   * @see java.util.List#add(int, java.lang.Object)
   */
  @Override
  public void add( int index, E element )
  {
    this.list.add( index, element );
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#remove(int)
   */
  @Override
  public E remove( int index )
  {
    return this.list.remove( index );
  }
  
  @Override
  public int indexOf( Object o )
  {
    return CollectionUtils.indexOfObjectIdentity( this, o );
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    return CollectionUtils.lastIndexOfObjectIdentity( this, o );
  }
  
  @Override
  public String toString()
  {
    return this.list.toString();
  }
  
}
