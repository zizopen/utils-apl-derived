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
package org.omnaest.utils.structure.collection.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IteratorUtils;

/**
 * Composite {@link Set} consisting of multiple given {@link Set} instances of the same type <br>
 * <br>
 * The {@link #add(Object)} method will add new elements to the {@link Set} with the smallest size.
 * 
 * @see SetUtils#composite(Collection)
 * @author Omnaest
 * @param <E>
 */
public class SetComposite<E> extends SetAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 4042018600715370368L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Set<E>[]    sets;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SetComposite
   * @param sets
   */
  public SetComposite( Set<E>... sets )
  {
    super();
    this.sets = sets;
  }
  
  @SuppressWarnings("unchecked")
  public SetComposite( Collection<Set<E>> setCollection )
  {
    super();
    Assert.isNotNull( setCollection, "The given collection of sets must not be null" );
    this.sets = setCollection.toArray( new Set[setCollection.size()] );
  }
  
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    for ( Set<E> set : this.sets )
    {
      if ( set != null )
      {
        retval += set.size();
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
    for ( Set<E> set : this.sets )
    {
      if ( set != null && set.contains( o ) )
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
    return IteratorUtils.chained( ArrayUtils.convertArray( this.sets, Iterator.class, new ElementConverter<Set<E>, Iterator<E>>()
    {
      @Override
      public Iterator<E> convert( Set<E> set )
      {
        return set != null ? set.iterator() : null;
      }
    } ) );
  }
  
  @Override
  public boolean add( E e )
  {
    //
    boolean retval = false;
    
    //
    Set<E> currentSet = null;
    {
      //
      int currentSize = Integer.MAX_VALUE;
      for ( Set<E> set : this.sets )
      {
        //
        int size = set.size();
        if ( size < currentSize )
        {
          currentSet = set;
          currentSize = size;
        }
      }
    }
    
    //
    if ( currentSet != null )
    {
      retval = currentSet.add( e );
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
    for ( Set<E> set : this.sets )
    {
      if ( set != null )
      {
        retval |= set.remove( o );
      }
    }
    
    // 
    return retval;
  }
  
}
