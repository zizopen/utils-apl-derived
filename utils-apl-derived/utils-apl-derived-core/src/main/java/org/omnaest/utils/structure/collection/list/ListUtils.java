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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.decorator.LockingListDecorator;
import org.omnaest.utils.structure.collection.list.decorator.LockingListIteratorDecorator;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;
import org.omnaest.utils.structure.element.converter.MultiElementConverter;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.filter.ElementFilter;
import org.omnaest.utils.structure.element.filter.ElementFilter.FilterMode;
import org.omnaest.utils.structure.element.filter.ElementFilterIndexPositionBasedForGivenIndexes;
import org.omnaest.utils.structure.element.filter.ElementFilterNotBlank;
import org.omnaest.utils.structure.element.filter.ExcludingElementFilter;
import org.omnaest.utils.structure.element.filter.ExcludingElementFilterConstant;
import org.omnaest.utils.structure.element.filter.ExcludingElementFilterIndexPositionBased;
import org.omnaest.utils.structure.element.filter.ExcludingElementFilterNotNull;
import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * Helper class for modifying {@link List} instances.
 * 
 * @author Omnaest
 */
public class ListUtils
{
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param elementConverter
   */
  public static <FROM, TO> List<TO> convert( Collection<FROM> collection, ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtils.convert( collection, elementConverter, false );
  }
  
  /**
   * @see #convert(Collection, ElementConverter)
   * @see #valueOf(Object...)
   * @param elementConverter
   * @param elements
   * @return
   */
  public static <FROM, TO> List<TO> convert( ElementConverter<FROM, TO> elementConverter, FROM... elements )
  {
    return ListUtils.convert( ListUtils.valueOf( elements ), elementConverter );
  }
  
  /**
   * Transforms a given {@link Iterable} instance from one generic type into the other using a given {@link ElementConverter}
   * 
   * @see #convert(Collection, ElementConverter)
   * @param iterable
   * @param elementConverter
   */
  public static <FROM, TO> List<TO> convert( Iterable<FROM> iterable, ElementConverter<FROM, TO> elementConverter )
  {
    return convert( ListUtils.valueOf( iterable ), elementConverter );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * 
   * @see #convert(Collection, ElementConverter)
   * @param collection
   * @param elementConverter
   * @param eliminateNullValues
   *          : true->all null results from the element transformer will be discarded and not inserted into the result list.
   */
  public static <FROM, TO> List<TO> convert( Collection<FROM> collection,
                                             final ElementConverter<FROM, TO> elementConverter,
                                             boolean eliminateNullValues )
  {
    //
    final List<TO> retlist = new ArrayList<TO>();
    if ( elementConverter != null && collection != null )
    {
      //
      if ( eliminateNullValues )
      {
        for ( FROM element : collection )
        {
          //          
          final TO convertedElement = elementConverter.convert( element );
          if ( convertedElement != null )
          {
            retlist.add( convertedElement );
          }
        }
      }
      else
      {
        for ( FROM element : collection )
        {
          //          
          final TO convertedElement = elementConverter.convert( element );
          retlist.add( convertedElement );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given
   * {@link MultiElementConverter}.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param multiElementConverter
   */
  public static <FROM, TO> List<TO> convert( Collection<FROM> collection, MultiElementConverter<FROM, TO> multiElementConverter )
  {
    return ListUtils.convert( collection, multiElementConverter, false );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * 
   * @see #convert(Collection, ElementConverter)
   * @param collection
   * @param multiElementConverter
   * @param eliminateNullValues
   *          : true->all null results from the element transformer will be discarded and not inserted into the result list.
   * @return always new (ordered) list instance containing transformed elements
   */
  public static <FROM, TO> List<TO> convert( Collection<FROM> collection,
                                             MultiElementConverter<FROM, TO> multiElementConverter,
                                             boolean eliminateNullValues )
  {
    //
    List<TO> retlist = new ArrayList<TO>();
    
    //
    if ( collection != null && multiElementConverter != null )
    {
      for ( FROM element : collection )
      {
        //
        try
        {
          //
          Collection<TO> transformedElementCollection = multiElementConverter.convert( element );
          
          //
          if ( transformedElementCollection != null )
          {
            for ( TO transformedElement : transformedElementCollection )
            {
              if ( !eliminateNullValues || transformedElement != null )
              {
                retlist.add( transformedElement );
              }
            }
          }
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * Every null value returned by the {@link ElementConverter} will be discarded and not put into the result list.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param elementConverter
   */
  public static <FROM, TO> List<TO> convertExcludingNullElements( Collection<FROM> collection,
                                                                  ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtils.convert( collection, elementConverter, true );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given
   * {@link MultiElementConverter}. Every null value returned by the {@link ElementConverter} will be discarded and not put into
   * the result list.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param multiElementConverter
   */
  public static <FROM, TO> List<TO> convertListExcludingNullElements( Collection<FROM> collection,
                                                                      MultiElementConverter<FROM, TO> multiElementConverter )
  {
    return ListUtils.convert( collection, multiElementConverter, true );
  }
  
  /**
   * Returns a filtered {@link List} using a {@link ElementFilter} on the given {@link Collection}
   * 
   * @see ElementFilter
   * @see ExcludingElementFilter
   * @param collection
   * @param elementFilter
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filter( Collection<E> collection, ElementFilter<E> elementFilter )
  {
    return filter( (Iterable<E>) collection, elementFilter );
  }
  
  /**
   * Returns a filtered {@link List} using a {@link ElementFilter} on the given {@link Iterable}
   * 
   * @see ElementFilter
   * @see ExcludingElementFilter
   * @param iterable
   *          {@link Iterable}
   * @param elementFilter
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filter( Iterable<E> iterable, ElementFilter<E> elementFilter )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( iterable != null && elementFilter != null )
    {
      //
      final boolean excludingMode = FilterMode.EXCLUDING.equals( elementFilter.getFilterMode() );
      
      //
      for ( E element : iterable )
      {
        //
        final boolean matchingFilter = elementFilter.filter( element );
        final boolean excluded = ( excludingMode && matchingFilter ) || ( !excludingMode && !matchingFilter );
        if ( !excluded )
        {
          retlist.add( element );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a filtered {@link List} using a {@link ExcludingElementFilterIndexPositionBased}
   * 
   * @param list
   * @param elementFilterIndexBased
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filter( List<E> list, ExcludingElementFilterIndexPositionBased elementFilterIndexBased )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( list != null && elementFilterIndexBased != null )
    {
      for ( int index = 0; index < list.size(); index++ )
      {
        //
        E element = list.get( index );
        
        //
        if ( !elementFilterIndexBased.filter( index ) )
        {
          retlist.add( element );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Creates a new {@link List} instance based on the given source {@link Iterable}. The given {@link Iterable} is then filtered
   * by the additional filter and order providing {@link Iterable}s. Filtering means that the list of source elements will only
   * retain elements, which are in the filter list, too. Additionally the order of the filter lists will be used to sort the ouput
   * {@link List}. Naturally the last applied filter will decide the order only.<br>
   * <br>
   * If null is given as source, null is returned. If filter are not given or null, they are ignored, but still a {@link List}
   * instance is returned.
   * 
   * @param sourceIterable
   * @param filterAndOrderProvidingIterables
   * @return
   */
  public static <E> List<E> filterAndOrderBy( Iterable<E> sourceIterable, Iterable<E>... filterAndOrderProvidingIterables )
  {
    //
    List<E> retlist = null;
    
    //
    if ( sourceIterable != null )
    {
      //
      retlist = ListUtils.valueOf( sourceIterable );
      for ( Iterable<E> iterable : filterAndOrderProvidingIterables )
      {
        if ( iterable != null )
        {
          //
          Set<E> tempSet = new HashSet<E>( retlist );
          retlist = new ArrayList<E>();
          for ( E element : iterable )
          {
            if ( tempSet.contains( element ) )
            {
              retlist.add( element );
            }
          }
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a filtered {@link List} which does not contain the given element
   * 
   * @see #filter(Iterable, ElementFilter)
   * @param collection
   * @param element
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filterExcludingElement( Collection<E> collection, E element )
  {
    return filter( collection, new ExcludingElementFilterConstant<E>( element ) );
  }
  
  /**
   * @see #filter(List, ExcludingElementFilterIndexPositionBased)
   * @param list
   * @param indexPositionCollection
   * @return
   */
  public static <E> List<E> filterExcludingIndexPositions( List<E> list, Collection<Integer> indexPositionCollection )
  {
    return filter( list, new ElementFilterIndexPositionBasedForGivenIndexes( indexPositionCollection, FilterMode.EXCLUDING ) );
  }
  
  /**
   * @see #filter(List, ExcludingElementFilterIndexPositionBased)
   * @param list
   * @param indexPositions
   * @return
   */
  public static <E> List<E> filterExcludingIndexPositions( List<E> list, int... indexPositions )
  {
    return filterExcludingIndexPositions( list, Arrays.asList( (Integer[]) ArrayUtils.toObject( indexPositions ) ) );
  }
  
  /**
   * Filters all null elements from the given {@link Collection} and returns a new {@link List} instance.
   * 
   * @param collection
   * @return
   */
  public static <E> List<E> filterExcludingNullElements( Collection<E> collection )
  {
    return filter( collection, new ExcludingElementFilterNotNull<E>() );
  }
  
  /**
   * Filters all blank elements from the given {@link Collection} with elements of type {@link String} and returns a new
   * {@link List} instance.
   * 
   * @param collection
   * @return
   */
  public static <E> List<String> filterExcludingBlankElements( Collection<String> collection )
  {
    return filter( collection, new ElementFilterNotBlank() );
  }
  
  /**
   * @see #filter(List, ExcludingElementFilterIndexPositionBased)
   * @param list
   * @param indexPositionCollection
   * @return
   */
  public static <E> List<E> filterIncludingIndexPositions( List<E> list, Collection<Integer> indexPositionCollection )
  {
    return filter( list, new ElementFilterIndexPositionBasedForGivenIndexes( indexPositionCollection, FilterMode.INCLUDING ) );
  }
  
  /**
   * @see #filter(List, ExcludingElementFilterIndexPositionBased)
   * @param list
   * @param indexPositions
   * @return
   */
  public static <E> List<E> filterIncludingIndexPositions( List<E> list, Integer... indexPositions )
  {
    return filterIncludingIndexPositions( list, Arrays.asList( indexPositions ) );
  }
  
  /**
   * Returns a new {@link List} for a given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> List<E> valueOf( Iterable<E> iterable )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( iterable != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      if ( iterator != null )
      {
        while ( iterator.hasNext() )
        {
          //
          final E element = iterator.next();
          retlist.add( element );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new {@link List} instance for a given {@link Iterator}
   * 
   * @param iterator
   * @return
   */
  public static <E> List<E> valueOf( Iterator<E> iterator )
  {
    return valueOf( IterableUtils.valueOf( iterator ) );
  }
  
  /**
   * Returns a new {@link List} instance for all the elements up to the given end index position based on the given
   * {@link Iterator}. The upper boundary is exclusive.
   * 
   * @param iterator
   * @param toIndexPosition
   * @return new {@link List} instance
   */
  public static <E> List<E> valueOf( Iterator<E> iterator, int toIndexPosition )
  {
    final int fromIndexPosition = 0;
    return valueOf( iterator, fromIndexPosition, toIndexPosition );
  }
  
  /**
   * Returns a new {@link List} instance for all the elements between the two index position boundaries based on the given
   * {@link Iterator}. The lower boundary is inclusive, the upper boundary is exclusive.
   * 
   * @param iterator
   * @param fromIndexPosition
   * @param toIndexPosition
   * @return new {@link List} instance
   */
  public static <E> List<E> valueOf( Iterator<E> iterator, int fromIndexPosition, int toIndexPosition )
  {
    //
    final List<E> retlist = new ArrayList<E>();
    
    //
    if ( iterator != null )
    {
      for ( int ii = 0; ii < toIndexPosition && iterator.hasNext(); ii++ )
      {
        //
        final E element = iterator.next();
        if ( ii >= fromIndexPosition )
        {
          retlist.add( element );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new {@link List} instance for a given {@link ElementStream}
   * 
   * @param elementStream
   * @return
   */
  public static <E> List<E> valueOf( ElementStream<E> elementStream )
  {
    //
    final Iterable<E> iterable = IterableUtils.valueOf( elementStream );
    return valueOf( iterable );
  }
  
  /**
   * Same as {@link #valueOf(Iterable)} for one or more elements
   * 
   * @param elements
   * @return
   */
  public static <E> List<E> valueOf( E... elements )
  {
    return valueOf( Arrays.asList( elements ) );
  }
  
  /**
   * Returns a {@link List} of all index positions for the given element. If no element can be found at all an empty {@link List}
   * is returned.
   * 
   * @param list
   * @param element
   * @return
   */
  public static <E> List<Integer> indexListOf( List<E> list, E element )
  {
    //    
    List<Integer> retlist = new ArrayList<Integer>();
    
    //
    if ( element != null && list != null )
    {
      for ( int ii = 0; ii < list.size(); ii++ )
      {
        //
        E iElement = list.get( ii );
        
        //
        if ( element.equals( iElement ) )
        {
          retlist.add( ii );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new {@link List} with only this elements which are in all of the given {@link Collection}s
   * 
   * @param collections
   * @return new {@link List} instance
   */
  public static <E> List<E> intersection( Collection<E>... collections )
  {
    return intersection( Arrays.asList( collections ) );
  }
  
  /**
   * Returns a new {@link List} with only this elements which are in all of the given {@link Collection}s
   * 
   * @param listCollection
   * @return new {@link List} instance
   */
  public static <E> List<E> intersection( Collection<? extends Collection<E>> listCollection )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( !listCollection.isEmpty() )
    {
      //
      Iterator<? extends Collection<E>> listCollectionIterator = listCollection.iterator();
      Collection<E> collection = listCollectionIterator.next();
      if ( collection != null )
      {
        retlist.addAll( collection );
      }
      
      //
      while ( listCollectionIterator.hasNext() )
      {
        Collection<E> collectionOther = listCollectionIterator.next();
        if ( collectionOther != null )
        {
          retlist.retainAll( collectionOther );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Creates a new {@link List} from a given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> List<E> iterableAsList( Iterable<E> iterable )
  {
    return iterable == null ? new ArrayList<E>() : iteratorAsList( iterable.iterator() );
  }
  
  /**
   * Creates a new {@link List} from a given {@link Iterator}
   * 
   * @param iterator
   * @return
   */
  public static <E> List<E> iteratorAsList( Iterator<E> iterator )
  {
    //    
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( iterator != null )
    {
      while ( iterator.hasNext() )
      {
        retlist.add( iterator.next() );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the last element of the given {@link List}. Returns null if the list is null or empty.
   * 
   * @param list
   * @return
   */
  public static <E> E lastElementOf( List<E> list )
  {
    return list == null || list.isEmpty() ? null : list.get( list.size() - 1 );
  }
  
  /**
   * Returns the element of the given {@link List} at the given reverse index position which is counted beginning from the last
   * element. This means the last element has the reversed index position 0. Returns null if the list is null or empty.
   * 
   * @param list
   * @param reverseIndexPosition
   * @return
   */
  public static <E> E elementWithReverseIndexPosition( List<E> list, int reverseIndexPosition )
  {
    return list == null || list.isEmpty() || list.size() - reverseIndexPosition <= 0 ? null : list.get( list.size() - 1
                                                                                                        - reverseIndexPosition );
  }
  
  /**
   * Returns the first element of a given {@link List}
   * 
   * @param list
   * @param reverseIndexPosition
   * @return
   */
  public static <E> E firstElementOf( List<E> list, int reverseIndexPosition )
  {
    return list != null && !list.isEmpty() ? list.get( 0 ) : null;
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link List} instance which keeps the order of
   * the elements.
   * 
   * @see #mergeAll(Collection...)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> List<E> mergeAll( Collection<? extends Collection<E>> collections )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( collections != null )
    {
      for ( Collection<E> list : collections )
      {
        retlist.addAll( list );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link List} instance which keeps the order of
   * the elements.
   * 
   * @see #mergeAll(Collection)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> List<E> mergeAll( Collection<E>... collections )
  {
    return ListUtils.mergeAll( Arrays.asList( collections ) );
  }
  
  /**
   * Returns a new {@link List} instance which contains all elements of the given {@link List} and additionally all further given
   * elements. <br>
   * <br>
   * This function will return always a new instance, even if the given list is null.
   * 
   * @param list
   * @param elements
   * @return new {@link List} instance
   */
  public static <E> List<E> addToNewList( List<? extends E> list, E... elements )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( list != null )
    {
      retlist.addAll( list );
    }
    
    //
    for ( E element : elements )
    {
      retlist.add( element );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the given {@link List} instance or a new {@link List} instance if the given one is null. The returned instance will
   * contain all elements of the given {@link List} and additionally all further given elements. <br>
   * <br>
   * This function will return always a new instance, even if the given list is null.
   * 
   * @param list
   * @param elements
   * @return given {@link List} instance or new {@link List} instance if given {@link List} instance is null
   */
  public static <E> List<E> add( List<? extends E> list, E... elements )
  {
    //
    @SuppressWarnings("unchecked")
    List<E> retlist = (List<E>) list;
    
    //
    if ( list == null )
    {
      retlist = new ArrayList<E>();
    }
    
    //
    for ( E element : elements )
    {
      retlist.add( element );
    }
    
    //
    return retlist;
  }
  
  /**
   * Same as {@link CollectionUtils#toMap(Iterable, ElementConverterElementToMapEntry)}
   * 
   * @see CollectionUtils#toMap(Iterable, ElementConverterElementToMapEntry)
   * @see ElementConverterElementToMapEntry
   * @param iterable
   * @param elementToMapEntryConverter
   * @return
   */
  public static <K, V, E> Map<K, V> toMap( Iterable<E> iterable,
                                           ElementConverterElementToMapEntry<E, K, V> elementToMapEntryConverter )
  {
    //
    return CollectionUtils.toMap( iterable, elementToMapEntryConverter );
  }
  
  /**
   * Returns a view of the given {@link List} using the given {@link Lock} to synchronize all of its methods
   * 
   * @see #lockedByReentrantLock(List)
   * @param list
   * @param lock
   * @return
   */
  public static <E> List<E> locked( List<E> list, Lock lock )
  {
    return new LockingListDecorator<E>( list, lock );
  }
  
  /**
   * Returns a view of the given {@link List} using a new {@link ReentrantLock} instance to synchronize all of its methods
   * 
   * @see #locked(List, Lock)
   * @param list
   * @return
   */
  public static <E> List<E> lockedByReentrantLock( List<E> list )
  {
    Lock lock = new ReentrantLock();
    return locked( list, lock );
  }
  
  /**
   * Returns a view on the given {@link ListIterator} which uses a {@link Lock} to synchronize all its methods.
   * 
   * @param listIterator
   * @param lock
   * @return
   */
  public static <E> ListIterator<E> locked( ListIterator<E> listIterator, Lock lock )
  {
    return new LockingListIteratorDecorator<E>( listIterator, lock );
  }
  
  /**
   * Returns a view on the given {@link ListIterator} which uses a {@link ReentrantLock} to synchronize all its methods.
   * 
   * @param listIterator
   * @return
   */
  public static <E> ListIterator<E> lockedByReentrantLock( ListIterator<E> listIterator )
  {
    Lock lock = new ReentrantLock();
    return locked( listIterator, lock );
  }
  
  /**
   * Removes the last element of a given {@link List} instance. If the {@link List} reference is null or the {@link List} is empty
   * nothing will be done to the {@link List}.
   * 
   * @param list
   * @return the removed element
   */
  public static <E> E removeLast( List<E> list )
  {
    //
    E retval = null;
    
    //
    if ( list != null && !list.isEmpty() )
    {
      retval = list.remove( list.size() - 1 );
    }
    
    //
    return retval;
  }
  
  /**
   * Removes the first element of a given {@link List} instance. If the {@link List} reference is null or the {@link List} is
   * empty nothing will be done to the {@link List}.
   * 
   * @param list
   * @return the removed element
   */
  public static <E> E removeFirst( List<E> list )
  {
    int index = 0;
    return remove( list, index );
  }
  
  /**
   * Removes the element at the given index position of a given {@link List} instance. If the {@link List} reference is null or
   * the {@link List} is empty nothing will be done to the {@link List}.
   * 
   * @param list
   * @return the removed element
   */
  public static <E> E remove( List<E> list, int index )
  {
    //
    E retval = null;
    
    //
    if ( list != null && index >= 0 && list.size() > index )
    {
      retval = list.remove( index );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the first element of the given {@link List}. Returns null if the {@link List} reference is null or the
   * {@link List#isEmpty()}.
   * 
   * @see #lastElement(List)
   * @see #elementAt(List, int)
   * @param list
   */
  public static <E> E firstElement( List<E> list )
  {
    int index = 0;
    return elementAt( list, index );
  }
  
  /**
   * Returns the last element of the given {@link List}. Returns null if the {@link List} reference is null or the
   * {@link List#isEmpty()}.
   * 
   * @see #firstElement(List)
   * @see #elementAt(List, int)
   * @param list
   */
  public static <E> E lastElement( List<E> list )
  {
    int index = list != null ? list.size() - 1 : -1;
    return elementAt( list, index );
  }
  
  /**
   * Returns the element at the given inverse index position of the given {@link List}. If the {@link List} reference is null or
   * the {@link List#size()} is to small, null is returned.
   * 
   * @see #firstElement(List)
   * @see #lastElement(List)
   * @see #elementAt(List, int)
   * @param list
   * @param inverseIndex
   */
  public static <E> E elementAtInverseIndex( List<E> list, int inverseIndex )
  {
    int index = list != null ? list.size() - 1 - inverseIndex : -1;
    return elementAt( list, index );
  }
  
  /**
   * Returns the element at the given index position of the given {@link List}. If the {@link List} reference is null or the
   * {@link List#size()} is to small, null is returned.
   * 
   * @see #elementAtInverseIndex(List, int)
   * @see #firstElement(List)
   * @see #lastElement(List)
   * @param list
   * @param index
   */
  public static <E> E elementAt( List<E> list, int index )
  {
    //    
    E retval = null;
    
    //
    if ( list != null && index >= 0 && list.size() > index )
    {
      retval = list.get( index );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a sublist of the given {@link List} with the maximum given size. There will always a new {@link List} instance be
   * returned, but empty. If the given {@link List} has less elements only these are returned and the returned {@link List} will
   * not be filled with additional null references.
   * 
   * @param list
   * @param maximumSize
   * @return
   */
  public static <E> List<E> max( List<E> list, int maximumSize )
  {
    //   
    final List<E> retlist = new ArrayList<E>();
    
    //
    if ( list != null && maximumSize >= 0 )
    {
      retlist.addAll( list.subList( 0, Math.min( list.size(), maximumSize ) ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new {@link List} instance with the same elements of the given {@link Iterable} but in reversed order.
   * 
   * @see Collections#reverse(List)
   * @param iterable
   * @return
   */
  public static <E> List<E> reverse( Iterable<E> iterable )
  {
    //
    List<E> retlist = valueOf( iterable );
    
    //
    Collections.reverse( retlist );
    
    //
    return retlist;
  }
  
  /**
   * Similar to {@link #sorted(Collection, Comparator)} using the {@link Comparable} interface of the given elements
   * 
   * @param collection
   * @return new sorted {@link List} instance, null if given {@link Collection} is null
   */
  public static <E extends Comparable<E>> List<E> sorted( Collection<E> collection )
  {
    //
    final Comparator<E> comparator = null;
    return sorted( collection, comparator );
  }
  
  /**
   * Returns a new {@link List} instance which is based on the elements of the given {@link Collection} and which is sorted using
   * the given {@link Comparator}<br>
   * If the given {@link Comparator} is null the natural order is used. <br>
   * The given {@link Collection} will kept unmodified, only the returned {@link List} will be sorted.
   * 
   * @see Collections#sort(List, Comparator)
   * @param collection
   * @param comparator
   * @return a new {@link List} instance
   */
  public static <E> List<E> sorted( Collection<E> collection, Comparator<E> comparator )
  {
    //
    final List<E> retlist = collection != null ? new ArrayList<E>( collection ) : null;
    
    //
    if ( retlist != null )
    {
      Collections.sort( retlist, comparator );
    }
    
    //
    return retlist;
  }
  
  /**
   * Generates a new {@link List} using the given value {@link Factory} to create all the elements the given number of times.
   * 
   * @param numberOfElements
   * @param valueFactory
   * @return
   */
  public static <E> List<E> generateList( int numberOfElements, Factory<E> valueFactory )
  {
    //
    final List<E> retlist = new ArrayList<E>();
    
    //
    Assert.isNotNull( valueFactory );
    for ( int ii = 0; ii < numberOfElements; ii++ )
    {
      retlist.add( valueFactory.newInstance() );
    }
    
    //
    return retlist;
  }
  
  /**
   * Similar to {@link List#toArray(Object[])}, returns null if a null reference is given.
   * 
   * @param list
   *          {@link List}
   * @return new {@link Array} instance
   */
  public static <E> E[] asArray( List<? extends E> list, Class<E> type )
  {
    return ArrayUtils.valueOf( list, type );
  }
  
}
