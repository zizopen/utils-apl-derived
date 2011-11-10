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
package org.omnaest.utils.structure.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;
import org.omnaest.utils.structure.collection.ListUtils.ElementFilterIndexPositionBasedForGivenIndexes.Mode;
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
    /* ********************************************** Variables ********************************************** */
    private Collection<Integer> indexCollection = null;
    private Mode                mode            = null;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
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
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public boolean filter( int indexPosition )
    {
      boolean contained = this.indexCollection.contains( indexPosition );
      return this.indexCollection != null
             && ( ( Mode.INCLUDING.equals( this.mode ) && !contained ) | ( ( Mode.EXCLUDING.equals( this.mode ) && contained ) ) );
    }
    
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
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
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
   * Transforms a given {@link Collection} instance from one generic type into a single value using a
   * {@link CollectionConverter}
   * 
   * @param collection
   * @param collectionConverter
   */
  public static <FROM, TO> TO convert( Collection<FROM> collection, CollectionConverter<FROM, TO> collectionConverter )
  {
    //
    TO retval = null;
    
    //
    if ( collection != null && collectionConverter != null )
    {
      //
      for ( FROM element : collection )
      {
        collectionConverter.process( element );
      }
      
      //
      retval = collectionConverter.result();
    }
    
    //
    return retval;
  }
  
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
   * Transforms a given {@link Collection} instance from one generic type into the other using a given
   * {@link MultiElementConverter}.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param multiElementConverter
   */
  public static <FROM, TO> List<TO> convert( Collection<FROM> collection,
                                               MultiElementConverter<FROM, TO> multiElementConverter )
  {
    return ListUtils.convert( collection, multiElementConverter, false );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementConverter}.
   * Every null value returned by the {@link ElementConverter} will be discarded and not put into the result list.
   * 
   * @see #convert(Collection, ElementConverter, boolean)
   * @param collection
   * @param elementConverter
   */
  public static <FROM, TO> List<TO> convertListExcludingNullElements( Collection<FROM> collection,
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
  public static <E> E lastElementOf( List<E> list, int reverseIndexPosition )
  {
    return list == null || list.isEmpty() || list.size() - reverseIndexPosition <= 0 ? null : list.get( list.size() - 1
                                                                                                        - reverseIndexPosition );
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
   * Transforms a given {@link Iterable} into a {@link Map} using a {@link LinkedHashMap} which keeps the order of the
   * {@link List}. Returns an empty {@link Map} for a null value as {@link Iterable}. Null values within the {@link Iterable} will
   * be excluded from the map, if the respective {@link ElementConverterElementToMapEntry#convert(Object)} returns null.
   * 
   * @see ElementConverterElementToMapEntry
   * @param iterable
   * @param elementToMapEntryTransformer
   * @return
   */
  public static <K, V, E> Map<K, V> asMap( Iterable<E> iterable, ElementConverterElementToMapEntry<E, K, V> elementToMapEntryTransformer )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( iterable != null && elementToMapEntryTransformer != null )
    {
      for ( E element : iterable )
      {
        //
        Entry<K, V> entry = elementToMapEntryTransformer.convert( element );
        if ( entry != null )
        {
          retmap.put( entry.getKey(), entry.getValue() );
        }
      }
    }
    
    //
    return retmap;
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
  public static <E> List<E> filterIncludingIndexPositions( List<E> list, Integer... indexPositions )
  {
    return filterIncludingIndexPositions( list, Arrays.asList( indexPositions ) );
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
   * Returns a new {@link List} for a given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> List<E> from( Iterable<E> iterable )
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
}
