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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.list.adapter.ListToSetAdapter;
import org.omnaest.utils.structure.collection.set.adapter.SetToSetAdapter;
import org.omnaest.utils.structure.collection.set.decorator.LockingSetDecorator;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.filter.ElementFilter;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * Helper for {@link Set} types
 * 
 * @see CollectionUtils
 * @author Omnaest
 */
public class SetUtils
{
  
  /**
   * Returns a new {@link SetDelta} instance for the given {@link Set}s
   * 
   * @see MapUtils#delta(java.util.Map, java.util.Map)
   * @param firstSet
   * @param secondSet
   * @return {@link SetDelta} instance
   */
  public static <E> SetDelta<E> delta( Set<E> firstSet, Set<E> secondSet )
  {
    return new SetDelta<E>( firstSet, secondSet );
  }
  
  /**
   * Returns the given {@link Set} instance reduced by the elements of the given {@link Iterable}
   * 
   * @param set
   *          {@link Set}
   * @param removingIterable
   *          {@link Iterable}
   * @return same {@link Set} instance or null, if null is given
   */
  public static <E> Set<E> removeAll( Set<E> set, Iterable<? extends E> removingIterable )
  {
    final Set<E> retset = set;
    if ( retset != null && removingIterable != null )
    {
      for ( E element : removingIterable )
      {
        retset.remove( element );
      }
    }
    return retset;
  }
  
  /**
   * Removes the given element from the given {@link Set} instance.
   * 
   * @param set
   * @param element
   * @return the given {@link Set} instance or null if null is given
   */
  public static <E> Set<E> remove( Set<E> set, E element )
  {
    final Set<E> retset = set;
    if ( retset != null )
    {
      retset.remove( element );
    }
    return retset;
  }
  
  /**
   * Returns a new {@link Set} instance containing the elements of the given {@link Set} reduced by the elements of the given
   * {@link Iterable}
   * 
   * @param set
   *          {@link Set}
   * @param removingIterable
   *          {@link Iterable}
   * @return new {@link Set} instance always
   */
  public static <E> Set<E> removeAllAsNewSet( Set<E> set, Iterable<E> removingIterable )
  {
    return removeAll( valueOf( set ), removingIterable );
  }
  
  /**
   * Returns the given {@link Set}, or a new instance if the given {@link Set} is null. The returned {@link Set} will have the
   * given elements added.
   * 
   * @param set
   * @param elements
   * @return given {@link Set} or new instance if null
   */
  public static <E> Set<E> add( Set<E> set, E... elements )
  {
    Set<E> retset = set != null ? set : new LinkedHashSet<E>();
    if ( elements != null )
    {
      for ( E element : elements )
      {
        retset.add( element );
      }
    }
    return retset;
  }
  
  /**
   * Adds the elements from the given {@link Iterable} to the given {@link Set}. If the given {@link Set} is null, a new
   * {@link LinkedHashSet} is returned. If the given {@link Iterable} is null, nothing is added.
   * 
   * @param set
   *          {@link Set}
   * @param elementIterable
   *          {@link Iterable}
   * @return given {@link Set} instance or new {@link LinkedHashSet}
   */
  @SuppressWarnings("unchecked")
  public static <E> Set<E> addAll( Set<? extends E> set, Iterable<? extends E> elementIterable )
  {
    Set<E> retset = set != null ? (Set<E>) set : new LinkedHashSet<E>();
    if ( elementIterable != null )
    {
      for ( E element : elementIterable )
      {
        retset.add( element );
      }
    }
    return retset;
  }
  
  /**
   * Does call {@link Set#retainAll(Collection)} on the given {@link Set} instance. If null is given as {@link Set} null is
   * returned as result. If null is given as retainable element {@link Collection} the given {@link Set} is cleared.
   * 
   * @param set
   * @param retainableCollection
   * @return given instance or null if null is given
   */
  public static <E> Set<E> retainAll( Set<E> set, Collection<? extends E> retainableCollection )
  {
    final Set<E> retset = set;
    if ( retset != null )
    {
      if ( retainableCollection != null )
      {
        retset.retainAll( retainableCollection );
      }
      else
      {
        retset.clear();
      }
    }
    return retset;
  }
  
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
    //
    Set<E> retset = new LinkedHashSet<E>();
    
    //
    if ( !collectionOfCollections.isEmpty() )
    {
      //
      final Iterator<? extends Collection<E>> collectionOfCollectionsIterator = collectionOfCollections.iterator();
      Collection<E> collection = collectionOfCollectionsIterator.next();
      if ( collection != null )
      {
        retset.addAll( collection );
      }
      
      //
      while ( collectionOfCollectionsIterator.hasNext() )
      {
        final Collection<E> collectionOther = collectionOfCollectionsIterator.next();
        if ( collectionOther != null )
        {
          retset.retainAll( collectionOther );
        }
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns the intersection of two given {@link Set} instances by iterating over the smaller given {@link Set} and testing on
   * the {@link Set#contains(Object)} method of the larger {@link Set}.
   * 
   * @param set1
   * @param set2
   * @return new {@link Set} instance
   */
  public static <E> Set<E> intersection( Set<E> set1, Set<E> set2 )
  {
    //
    final Set<E> retset = new LinkedHashSet<E>();
    
    //
    if ( set1 != null && set2 != null )
    {
      //
      Iterable<E> iterable;
      Collection<E> collection;
      if ( set1.size() > set2.size() )
      {
        collection = set1;
        iterable = set2;
      }
      else
      {
        collection = set2;
        iterable = set1;
      }
      
      //
      for ( E element : iterable )
      {
        if ( collection.contains( element ) )
        {
          retset.add( element );
        }
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns the intersection of the {@link Collection}s of the given container {@link Collection}
   * 
   * @param collections
   * @return
   */
  public static <E> Set<E> intersection( Collection<E>... collections )
  {
    //
    final Set<E> retset = new LinkedHashSet<E>();
    
    //
    if ( collections.length > 0 )
    {
      //
      Collection<E> collection = collections[0];
      if ( collection != null )
      {
        retset.addAll( collection );
      }
      
      //
      for ( int ii = 1; ii < collections.length && !retset.isEmpty(); ii++ )
      {
        //
        Collection<E> collectionOther = collections[ii];
        if ( collectionOther != null )
        {
          retset.retainAll( collectionOther );
        }
      }
    }
    
    return retset;
  }
  
  /**
   * Returns the intersection of the {@link Collection}s of the given container {@link Collection}
   * 
   * @param collectionFirst
   * @param collectionSecond
   * @return new {@link Set} instance
   */
  @SuppressWarnings("unchecked")
  public static <E> Set<E> intersection( Collection<E> collectionFirst, Collection<E> collectionSecond )
  {
    return intersection( Arrays.asList( collectionFirst, collectionSecond ) );
  }
  
  /**
   * Same as {@link #valueOf(Iterable)} for one or more elements
   * 
   * @param elements
   * @return a new {@link LinkedHashSet} instance containing all given elements
   */
  public static <E> Set<E> valueOf( E... elements )
  {
    final Set<E> retset = new LinkedHashSet<E>();
    if ( elements != null )
    {
      for ( E element : elements )
      {
        retset.add( element );
      }
    }
    return retset;
  }
  
  /**
   * Returns a new {@link LinkedHashSet} instance
   * 
   * @return
   */
  public static <E> Set<E> emptySet()
  {
    return new LinkedHashSet<E>();
  }
  
  /**
   * Similar to {@link #valueOf(Iterable)}
   * 
   * @param iterator
   * @return
   */
  public static <E> Set<E> valueOf( Iterator<E> iterator )
  {
    return valueOf( IterableUtils.valueOf( iterator ) );
  }
  
  /**
   * Returns a new {@link LinkedHashSet} instance with the element of the given {@link Iterable} which keeps the order of the
   * elements
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
  
  /**
   * Returns a new {@link SetToSetAdapter} for the given {@link Set}
   * 
   * @param set
   *          {@link Set}
   * @param elementBidirectionalConverter
   *          {@link ElementBidirectionalConverter}
   * @return new {@link SetToSetAdapter} instance
   */
  public static <FROM, TO> Set<TO> adapter( Set<FROM> set, ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    return new SetToSetAdapter<FROM, TO>( set, elementBidirectionalConverter );
  }
  
  /**
   * Returns a new {@link ListToSetAdapter} for the given {@link List}
   * 
   * @param list
   *          {@link List}
   * @return {@link Set} adapter instance
   */
  public static <E> Set<E> adapter( List<E> list )
  {
    return new ListToSetAdapter<E>( list );
  }
  
  /**
   * Returns a {@link SetComposite} instance for the given {@link Set}s
   * 
   * @param sets
   * @return
   */
  public static <E> Set<E> composite( Set<E>... sets )
  {
    return new SetComposite<E>( sets );
  }
  
  /**
   * Returns a {@link SetComposite} instance for the given {@link Set}s
   * 
   * @param setCollection
   * @return
   */
  public static <E> Set<E> composite( Collection<Set<E>> setCollection )
  {
    return new SetComposite<E>( setCollection );
  }
  
  /**
   * Returns true if the given type is assignable to the {@link Set} interface
   * 
   * @see #isSortedSetType(Class)
   * @param type
   * @return
   */
  public static boolean isSetType( Class<?> type )
  {
    boolean retval = false;
    if ( type != null )
    {
      retval = Set.class.isAssignableFrom( type );
    }
    return retval;
  }
  
  /**
   * Returns true if the given type is assignable to the {@link SortedSet} interface
   * 
   * @see #isSetType(Class)
   * @param type
   * @return
   */
  public static boolean isSortedSetType( Class<?> type )
  {
    boolean retval = false;
    if ( type != null )
    {
      retval = SortedSet.class.isAssignableFrom( type );
    }
    return retval;
  }
  
}
