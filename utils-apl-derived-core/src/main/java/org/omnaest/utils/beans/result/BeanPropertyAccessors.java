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
package org.omnaest.utils.beans.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * Container for a ordered set of {@link BeanPropertyAccessor} instances.
 * 
 * @param <B>
 *          type of the Java Bean
 * @author Omnaest
 */
public class BeanPropertyAccessors<B> implements Collection<BeanPropertyAccessor<B>>
{
  /* ********************************************** Variables ********************************************** */
  protected List<BeanPropertyAccessor<B>> beanPropertyAccessorList = new ArrayList<BeanPropertyAccessor<B>>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see BeanPropertyAccessors
   */
  public BeanPropertyAccessors()
  {
    super();
  }
  
  /**
   * @see BeanPropertyAccessors
   * @param beanPropertyAccessorCollection
   */
  public BeanPropertyAccessors( Collection<BeanPropertyAccessor<B>> beanPropertyAccessorCollection )
  {
    super();
    this.beanPropertyAccessorList.addAll( beanPropertyAccessorCollection );
  }
  
  /**
   * Copies the values of all related properties of the given source Java Bean to the destination Java Bean. The affected
   * properties are based on the {@link BeanPropertyAccessor} instances within the {@link BeanPropertyAccessors} container.
   * 
   * @param beanSource
   * @param beanDestination
   */
  public void copyPropertyValues( B beanSource, B beanDestination )
  {
    //
    for ( BeanPropertyAccessor<B> beanPropertyAccessor : this.beanPropertyAccessorList )
    {
      try
      {
        //
        beanPropertyAccessor.copyPropertyValue( beanSource, beanDestination );
      }
      catch ( Exception e )
      {
      }
    }
  }
  
  /**
   * Returns the size of the {@link BeanPropertyAccessors} container
   */
  @Override
  public int size()
  {
    return this.beanPropertyAccessorList.size();
  }
  
  /**
   * Returns true if the {@link BeanPropertyAccessors} container has no {@link BeanPropertyAccessor} instances
   * 
   * @return
   */
  @Override
  public boolean isEmpty()
  {
    return this.beanPropertyAccessorList.isEmpty();
  }
  
  /**
   * Adds a {@link BeanPropertyAccessor} to the {@link BeanPropertyAccessors} container
   * 
   * @param e
   * @return
   */
  @Override
  public boolean add( BeanPropertyAccessor<B> e )
  {
    return this.beanPropertyAccessorList.add( e );
  }
  
  /**
   * Removes a {@link BeanPropertyAccessor} instance from the {@link BeanPropertyAccessors} container
   * 
   * @param beanPropertyAccessor
   * @return
   */
  public boolean remove( BeanPropertyAccessor<B> beanPropertyAccessor )
  {
    return this.beanPropertyAccessorList.remove( beanPropertyAccessor );
  }
  
  /**
   * Adds {@link BeanPropertyAccessor} instances to the {@link BeanPropertyAccessors} container
   * 
   * @param collection
   *          {@link Collection}
   * @return
   */
  @Override
  public boolean addAll( Collection<? extends BeanPropertyAccessor<B>> collection )
  {
    return this.beanPropertyAccessorList.addAll( collection );
  }
  
  /**
   * Adds {@link BeanPropertyAccessor} instances to the {@link BeanPropertyAccessors} container
   * 
   * @param iterable
   * @return
   */
  public boolean addAll( Iterable<? extends BeanPropertyAccessor<B>> iterable )
  {
    return this.addAll( ListUtils.valueOf( iterable ) );
  }
  
  /**
   * Clears the {@link BeanPropertyAccessors} container
   */
  @Override
  public void clear()
  {
    this.beanPropertyAccessorList.clear();
  }
  
  @Override
  public Iterator<BeanPropertyAccessor<B>> iterator()
  {
    return this.beanPropertyAccessorList.iterator();
  }
  
  /**
   * Removes the {@link BeanPropertyAccessor} at the given index position
   * 
   * @param index
   * @return
   */
  public BeanPropertyAccessor<B> remove( int index )
  {
    return this.beanPropertyAccessorList.remove( index );
  }
  
  /**
   * Gets the {@link BeanPropertyAccessor} at the given index position
   * 
   * @param index
   * @return
   */
  public BeanPropertyAccessor<B> get( int index )
  {
    return this.beanPropertyAccessorList.get( index );
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.beanPropertyAccessorList.contains( o );
  }
  
  @Override
  public Object[] toArray()
  {
    return this.beanPropertyAccessorList.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    return this.beanPropertyAccessorList.toArray( a );
  }
  
  @Override
  public boolean containsAll( Collection<?> c )
  {
    return this.beanPropertyAccessorList.containsAll( c );
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    return this.beanPropertyAccessorList.retainAll( c );
  }
  
  @Override
  public boolean remove( Object o )
  {
    return this.beanPropertyAccessorList.remove( o );
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    return this.beanPropertyAccessorList.removeAll( c );
  }
  
}
