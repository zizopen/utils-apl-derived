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
package org.omnaest.utils.structure.collection.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.list.ListUtils.ElementFilter;
import org.omnaest.utils.structure.collection.set.decorator.LockingSetDecorator;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Helper for {@link Set} types
 * 
 * @author Omnaest
 */
public class SetUtils
{
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link Set} instance.
   * 
   * @see #mergeAll(Collection)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> Set<E> mergeAll( Collection<E>... collections )
  {
    return SetUtils.mergeAll( Arrays.asList( collections ) );
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link Set} instance .
   * 
   * @see #mergeAll(Collection...)
   * @param <E>
   * @param collectionOfCollections
   * @return
   */
  public static <E> Set<E> mergeAll( Collection<? extends Collection<E>> collectionOfCollections )
  {
    return new LinkedHashSet<E>( ListUtils.mergeAll( collectionOfCollections ) );
  }
  
  /**
   * Returns the intersection of the {@link Collection}s of the given container {@link Collection}
   * 
   * @param collectionOfCollections
   * @return
   */
  public static <E> Set<E> intersection( Collection<? extends Collection<E>> collectionOfCollections )
  {
    return new LinkedHashSet<E>( ListUtils.intersection( collectionOfCollections ) );
  }
  
  /**
   * Returns the intersection of the {@link Collection}s of the given container {@link Collection}
   * 
   * @param collections
   * @return
   */
  public static <E> Set<E> intersection( Collection<E>... collections )
  {
    return new LinkedHashSet<E>( ListUtils.intersection( collections ) );
  }
  
  /**
   * Same as {@link #valueOf(Iterable)} for one or more elements
   * 
   * @param elements
   * @return
   */
  public static <E> Set<E> valueOf( E... elements )
  {
    return valueOf( Arrays.asList( elements ) );
  }
  
  /**
   * Returns a new ordered {@link Set} instance with the element of the given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> Set<E> valueOf( Iterable<E> iterable )
  {
    //    
    Set<E> retset = new LinkedHashSet<E>();
    
    //
    if ( iterable != null )
    {
      for ( E element : iterable )
      {
        retset.add( element );
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns a view of the given {@link Set} using the given {@link Lock} to synchronize all of its methods
   * 
   * @see #lockedByReentrantLock(Set)
   * @param set
   * @param lock
   * @return
   */
  public static <E> Set<E> locked( Set<E> set, Lock lock )
  {
    return new LockingSetDecorator<E>( set, lock );
  }
  
  /**
   * Returns a view of the given {@link Set} using a new {@link ReentrantLock} instance to synchronize all of its methods
   * 
   * @see #locked(Set, Lock)
   * @param set
   * @return
   */
  public static <E> Set<E> lockedByReentrantLock( Set<E> set )
  {
    Lock lock = new ReentrantLock();
    return locked( set, lock );
  }
  
  /**
   * Transforms elements to a {@link Set} of instances of another type using a given {@link ElementConverter}.
   * 
   * @see #convert(Iterable, ElementConverter)
   * @param elementConverter
   * @param elements
   */
  public static <FROM, TO> Set<TO> convert( ElementConverter<FROM, TO> elementConverter, FROM... elements )
  {
    return convert( SetUtils.valueOf( elements ), elementConverter );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * 
   * @param iterable
   * @param elementConverter
   */
  public static <FROM, TO> Set<TO> convert( Iterable<FROM> iterable, ElementConverter<FROM, TO> elementConverter )
  {
    return new LinkedHashSet<TO>( ListUtils.convert( iterable, elementConverter ) );
  }
  
  /**
   * Filters the given {@link Iterable} and returns a {@link Set} which only contains the elements which are not filtered out by
   * the given {@link ElementFilter}.
   * 
   * @param iterable
   *          {@link Iterable}
   * @param elementFilter
   *          {@link ElementFilter}
   * @return new {@link Set} instance
   */
  public static <E> Set<E> filter( Iterable<E> iterable, ElementFilter<E> elementFilter )
  {
    return SetUtils.valueOf( ListUtils.filter( iterable, elementFilter ) );
  }
  
}
