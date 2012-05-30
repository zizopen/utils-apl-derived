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
package org.omnaest.utils.structure.collection;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Composite {@link Collection} consisting of multiple given {@link Collection} instances of the same type <br>
 * <br>
 * The {@link #add(Object)} method will add new elements to the {@link Collection} with the smallest size.
 * 
 * @author Omnaest
 * @param <E>
 */
public class CollectionComposite<E> extends CollectionAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long     serialVersionUID = 4042012340715370368L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Collection<E>[] collections;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see CollectionComposite
   * @param collections
   */
  public CollectionComposite( Collection<E>... collections )
  {
    super();
    this.collections = collections;
  }
  
  @SuppressWarnings("unchecked")
  public CollectionComposite( Collection<Collection<E>> collectionOfCollections )
  {
    super();
    Assert.isNotNull( collectionOfCollections, "The given collection of collections must not be null" );
    this.collections = collectionOfCollections.toArray( new Collection[collectionOfCollections.size()] );
  }
  
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    for ( Collection<E> collection : this.collections )
    {
      if ( collection != null )
      {
        retval += collection.size();
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( Object o )
  {
    //
    boolean retval = false;
    
    //
    for ( Collection<E> collection : this.collections )
    {
      if ( collection != null && collection.contains( o ) )
      {
        retval = true;
        break;
      }
    }
    
    // 
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Iterator<E> iterator()
  {
    return IteratorUtils.chainedIterator( ArrayUtils.convertArray( this.collections, Iterator.class,
                                                                   new ElementConverter<Collection<E>, Iterator<E>>()
                                                                   {
                                                                     @Override
                                                                     public Iterator<E> convert( Collection<E> collection )
                                                                     {
                                                                       return collection != null ? collection.iterator() : null;
                                                                     }
                                                                   } ) );
  }
  
  @Override
  public boolean add( E e )
  {
    //
    boolean retval = false;
    
    //
    Collection<E> currentCollection = null;
    {
      //
      int currentSize = Integer.MAX_VALUE;
      for ( Collection<E> collection : this.collections )
      {
        //
        int size = collection.size();
        if ( size < currentSize )
        {
          currentCollection = collection;
          currentSize = size;
        }
      }
    }
    
    //
    if ( currentCollection != null )
    {
      retval = currentCollection.add( e );
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean remove( Object o )
  {
    //
    boolean retval = false;
    
    //
    for ( Collection<E> collection : this.collections )
    {
      if ( collection != null )
      {
        retval |= collection.remove( o );
      }
    }
    
    // 
    return retval;
  }
  
}
