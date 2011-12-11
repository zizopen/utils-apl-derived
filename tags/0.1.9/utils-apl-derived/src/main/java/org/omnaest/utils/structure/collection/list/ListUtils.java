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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils.ElementFilterIndexPositionBasedForGivenIndexes.Mode;
import org.omnaest.utils.structure.collection.list.decorator.LockingListDecorator;
import org.omnaest.utils.structure.collection.list.decorator.LockingListIteratorDecorator;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;
import org.omnaest.utils.structure.element.converter.MultiElementConverter;

/**
 * Helper class for modifying {@link List} instances.
 * 
 * @author Omnaest
 */
public class ListUtils
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * An {@link ElementFilter} is used to filter elements.
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface ElementFilter<E>
  {
    /**
     * The filter method should return true if the given element should be filtered out / removed.
     * 
     * @param element
     * @return
     */
    public boolean filter( E element );
  }
  
  /**
   * {@link ElementFilter} which removes all given elements where the constructor element equals to.
   * 
   * @author Omnaest
   * @param <E>
   */
  public static class ElementFilterConstant<E> implements ElementFilter<E>
  {
    /* ********************************************** Variables ********************************************** */
    private E element = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param element
     *          != null
     */
    public ElementFilterConstant( E element )
    {
      super();
      this.element = element;
    }
    
    @Override
    public boolean filter( E element )
    {
      return this.element != null && this.element.equals( element );
    }
    
  }
  
  /**
   * Filter which is based on the index position of an element within the related structure
   * 
   * @author Omnaest
   */
  public static interface ElementFilterIndexPositionBased
  {
    /**
     * Returns true for all elements with the given index position to be filtered out / removed
     * 
     * @param indexPosition
     * @return
     */
    public boolean filter( int indexPosition );
  }
  
  /**
   * {@link ElementFilterIndexPositionBased} which filters all elements which do not have any of the given index numbers
   * 
   * @see Mode
   * @author Omnaest
   * @param <E>
   */
  public static class ElementFilterIndexPositionBasedForGivenIndexes implements ElementFilterIndexPositionBased
  {
    /**
     * Declares the behavior mode which can be {@link #EXCLUDING} or {@link #INCLUDING}
     * 
     * @author Omnaest
     */
    public static enum Mode
    {
      EXCLUDING,
      INCLUDING
    }
    
    /* ********************************************** Variables ********************************************** */
    private Collection<Integer> indexCollection = null;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    private Mode                mode            = null;
    
    /**
     * @see Mode
     * @param indexCollection
     * @param mode
     */
    public ElementFilterIndexPositionBasedForGivenIndexes( Collection<Integer> indexCollection, Mode mode )
    {
      super();
      this.indexCollection = indexCollection;
      this.mode = mode;
    }
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public boolean filter( int indexPosition )
    {
      boolean contained = this.indexCollection.contains( indexPosition );
      return this.indexCollection != null
             && ( ( Mode.INCLUDING.equals( this.mode ) && !contained ) | ( ( Mode.EXCLUDING.equals( this.mode ) && contained ) ) );
    }
    
  }
  
  /**
   * {@link ElementFilter} which filters / removes all null elements
   * 
   * @author Omnaest
   * @param <E>
   */
  public static class ElementFilterNotNull<E> implements ElementFilter<E>
  {
    @Override
    public boolean filter( E element )
    {
      return element == null;
    }
  }
  
  /**
   * {@link ElementFilter} which filters / removes all blank elements. This {@link ElementFilter} can only be applied to elements
   * of type {@link String}
   * 
   * @author Omnaest
   * @param <E>
   */
  public static class ElementFilterNotBlank implements ElementFilter<String>
  {
    @Override
    public boolean filter( String element )
    {
      return StringUtils.isBlank( element );
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
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
    List<TO> retlist = new ArrayList<TO>();
    if ( elementConverter != null )
    {
      MultiElementConverter<FROM, TO> multiElementConverter = new MultiElementConverter<FROM, TO>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public Collection<TO> convert( FROM element )
        {
          return Arrays.asList( elementConverter.convert( element ) );
        }
      };
      retlist = ListUtils.convert( collection, multiElementConverter, eliminateNullValues );
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
   * Returns a filtered {@link List} using a {@link ElementFilter}
   * 
   * @param collection
   * @param elementFilter
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filter( Collection<E> collection, ElementFilter<E> elementFilter )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( collection != null && elementFilter != null )
    {
      for ( E element : collection )
      {
        if ( !elementFilter.filter( element ) )
        {
          retlist.add( element );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a filtered {@link List} using a {@link ElementFilterIndexPositionBased}
   * 
   * @param list
   * @param elementFilterIndexBased
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filter( List<E> list, ElementFilterIndexPositionBased elementFilterIndexBased )
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
   * @param retainingAndOrderingIterable
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
   * @see #filter(List, ElementFilter)
   * @param collection
   * @param element
   * @return a new {@link List} instance containing only the not filtered elements of the given {@link List}
   */
  public static <E> List<E> filterExcludingElement( Collection<E> collection, E element )
  {
    return filter( collection, new ElementFilterConstant<E>( element ) );
  }
  
  /**
   * @see #filter(List, ElementFilterIndexPositionBased)
   * @param list
   * @param indexPositionCollection
   * @return
   */
  public static <E> List<E> filterExcludingIndexPositions( List<E> list, Collection<Integer> indexPositionCollection )
  {
    return filter( list, new ElementFilterIndexPositionBasedForGivenIndexes( indexPositionCollection, Mode.EXCLUDING ) );
  }
  
  /**
   * @see #filter(List, ElementFilterIndexPositionBased)
   * @param list
   * @param indexPositions
   * @return
   */
  public static <E> List<E> filterExcludingIndexPositions( List<E> list, Integer... indexPositions )
  {
    return filterExcludingIndexPositions( list, Arrays.asList( indexPositions ) );
  }
  
  /**
   * Filters all null elements from the given {@link Collection} and returns a new {@link List} instance.
   * 
   * @param collection
   * @return
   */
  public static <E> List<E> filterExcludingNullElements( Collection<E> collection )
  {
    return filter( collection, new ElementFilterNotNull<E>() );
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
   * @see #filter(List, ElementFilterIndexPositionBased)
   * @param list
   * @param indexPositionCollection
   * @return
   */
  public static <E> List<E> filterIncludingIndexPositions( List<E> list, Collection<Integer> indexPositionCollection )
  {
    return filter( list, new ElementFilterIndexPositionBasedForGivenIndexes( indexPositionCollection, Mode.INCLUDING ) );
  }
  
  /**
   * @see #filter(List, ElementFilterIndexPositionBased)
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
      for ( E element : iterable )
      {
        retlist.add( element );
      }
    }
    
    //
    return retlist;
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
   * Returns a new {@link List} with only this elements which are in all of the given {@link List}s
   * 
   * @param listCollection
   * @return
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
   * Same as {@link CollectionUtils#toMap(Iterable, ElementConverterElementToMapEntry)}
   * 
   * @see CollectionUtils#toMap(Iterable, ElementConverterElementToMapEntry)
   * @see ElementConverterElementToMapEntry
   * @param iterable
   * @param elementToMapEntryTransformer
   * @return
   */
  public static <K, V, E> Map<K, V> toMap( Iterable<E> iterable,
                                           ElementConverterElementToMapEntry<E, K, V> elementToMapEntryTransformer )
  {
    //
    return CollectionUtils.toMap( iterable, elementToMapEntryTransformer );
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
  
}
