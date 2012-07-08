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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.adapter.CollectionToCollectionAdapter;
import org.omnaest.utils.structure.collection.decorator.LockingCollectionDecorator;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectToString;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.structure.map.MapUtils;

import com.google.common.base.Joiner;

public class CollectionUtils
{
  
  /**
   * Transformer interface for transforming whole {@link Collection}s into a single object
   * 
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static interface CollectionConverter<FROM, TO>
  {
    /**
     * Processes the given {@link Collection} element. This method will be called for each element of the original
     * {@link Collection}
     * 
     * @param element
     */
    public void process( FROM element );
    
    /**
     * Returns the result of the transformation process
     * 
     * @return
     */
    public TO result();
  }
  
  /**
   * {@link CollectionConverter} which produces a {@link String} from a {@link Collection}
   * 
   * @author Omnaest
   * @param <FROM>
   */
  public static abstract class CollectionTransformerToString<FROM> implements CollectionConverter<FROM, String>
  {
    /* ********************************************** Variables ********************************************** */
    private StringBuilder resultStringBuilder = new StringBuilder();
    
    @Override
    public void process( FROM element )
    {
      this.process( element, this.resultStringBuilder );
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * The {@link #process(Object, StringBuilder)} method will be invoked for every element of the processed {@link Collection}.
     * The given {@link StringBuilder} instance will build the resulting {@link String} and should be used to store the iteration
     * result.
     * 
     * @param element
     * @param resultStringBuilder
     */
    public abstract void process( FROM element, StringBuilder resultStringBuilder );
    
    @Override
    public String result()
    {
      return this.resultStringBuilder.toString();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Adds an array of integer values to a collection of Integer
   * 
   * @param integerCollection
   * @param intArray
   */
  public static void addAll( Collection<Integer> integerCollection, int[] intArray )
  {
    for ( int iValue : intArray )
    {
      integerCollection.add( iValue );
    }
  }
  
  /**
   * Adds an array of long values to a collection of Long
   * 
   * @param longCollection
   * @param intArray
   */
  public static void addAll( Collection<Long> longCollection, long[] intArray )
  {
    for ( long iValue : intArray )
    {
      longCollection.add( iValue );
    }
  }
  
  /**
   * Returns true if the given {@link Collection} contains the given object. Contains uses the "object == element" instead of the
   * "equals" method to determine the object identity.
   * 
   * @param collection
   * @param object
   * @return
   */
  public static <E> boolean containsObjectIdentity( Collection<E> collection, Object object )
  {
    return indexOfObjectIdentity( collection, object ) >= 0;
  }
  
  /**
   * Converts a given {@link Collection} instance from one generic type into a single value using a {@link CollectionConverter}
   * 
   * @see CollectionConverter
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
   * Converts a given {@link Collection} into another {@link Collection} with other element types using an
   * {@link ElementConverter}.
   * 
   * @see ElementConverter
   * @param collectionFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> Collection<TO> convertCollection( Collection<FROM> collectionFrom,
                                                             ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtils.convert( collectionFrom, elementConverter );
  }
  
  /**
   * Converts a given {@link Collection} into another {@link Collection} with other element types, whereby all elements which
   * convert to null will not be inserted into the target {@link Collection}.
   * 
   * @see ElementConverter
   * @param collectionFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> Collection<TO> convertCollectionExcludingNullElements( Collection<FROM> collectionFrom,
                                                                                  ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtils.convertExcludingNullElements( collectionFrom, elementConverter );
  }
  
  /**
   * Returns true if the elements of the two given {@link Collection}s are equal and have the same order
   * 
   * @param collection1
   * @param collection2
   * @return
   */
  public static boolean equals( Iterable<?> collection1, Iterable<?> collection2 )
  {
    //
    boolean retval = false;
    
    //
    if ( collection1 != null && collection2 != null )
    {
      
      //
      Iterator<?> iteratorOther = collection2.iterator();
      Iterator<?> iteratorThis = collection1.iterator();
      
      //
      retval = true;
      
      //
      while ( iteratorOther.hasNext() && iteratorThis.hasNext() )
      {
        //
        Object elementOther = iteratorOther.next();
        Object elementThis = iteratorThis.next();
        
        //
        retval &= elementThis == elementOther || ( elementThis != null && elementThis.equals( elementOther ) );
        
        //
        if ( !retval )
        {
          break;
        }
      }
      
      //
      retval &= !iteratorOther.hasNext() && !iteratorThis.hasNext();
      
    }
    
    //    
    return retval;
  }
  
  /**
   * Tests, if the two collection have the same elements.
   * 
   * @param collection1
   * @param collection2
   * @return
   */
  public static boolean equalsUnordered( Collection<?> collection1, Collection<?> collection2 )
  {
    //
    boolean retval = collection1.size() == collection2.size();
    
    //
    for ( Object iObject : collection1 )
    {
      retval &= collection2.contains( iObject );
    }
    
    //
    return retval;
  }
  
  /**
   * Calculates the hash code for a given collection including the order of elements
   * 
   * @param collection
   * @return
   */
  public static <E> int hashCode( Collection<E> collection )
  {
    final int prime = 31;
    int result = 1;
    if ( collection != null )
    {
      Iterator<E> iterator = collection.iterator();
      while ( iterator.hasNext() )
      {
        E next = iterator.next();
        result = prime * result + ( next != null ? next.hashCode() : 0 );
      }
    }
    return result;
  }
  
  /**
   * Calculates the hash code for a given collection not including the order of elements. This can be used for {@link Set}
   * implementations e.g.
   * 
   * @param collection
   * @return
   */
  public static <E> int hashCodeUnordered( Collection<E> collection )
  {
    int result = 1;
    if ( collection != null )
    {
      Iterator<E> iterator = collection.iterator();
      while ( iterator.hasNext() )
      {
        E next = iterator.next();
        result = result * ( next != null ? next.hashCode() : 0 );
      }
    }
    return result;
  }
  
  /**
   * Returns the index position of the first occurring object within the {@link Collection}. Comparisons uses the
   * "object == element" instead of the "equals" method to determine the object identity.
   * 
   * @param collection
   * @param object
   * @return index position of the first matching element;-1 if no element is matching
   */
  public static <E> int indexOfObjectIdentity( Collection<E> collection, Object object )
  {
    //
    int retval = -1;
    
    //
    if ( collection != null )
    {
      Iterator<E> iterator = collection.iterator();
      if ( iterator != null )
      {
        int indexPosition = 0;
        while ( retval < 0 && iterator.hasNext() )
        {
          //
          E element = iterator.next();
          if ( element == object )
          {
            retval = indexPosition;
          }
          
          //
          indexPosition++;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the index position of the last occurring object within the {@link Collection}. Comparisons uses the
   * "object == element" instead of the "equals" method to determine the object identity.
   * 
   * @param collection
   * @param object
   * @return index position of the first matching element;-1 if no element is matching
   */
  public static <E> int lastIndexOfObjectIdentity( Collection<E> collection, Object object )
  {
    //
    int retval = -1;
    
    //
    if ( collection != null )
    {
      Iterator<E> iterator = collection.iterator();
      if ( iterator != null )
      {
        int indexPosition = 0;
        boolean lastElementWasIdenticalElement = false;
        while ( ( retval < 0 || lastElementWasIdenticalElement ) && iterator.hasNext() )
        {
          //
          lastElementWasIdenticalElement = false;
          
          //
          E element = iterator.next();
          if ( element == object )
          {
            retval = indexPosition;
            lastElementWasIdenticalElement = true;
          }
          
          //
          indexPosition++;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link Collection} instance which keeps the
   * order of the elements.
   * 
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> Collection<E> mergeAll( Collection<E>... collections )
  {
    return ListUtils.mergeAll( collections );
  }
  
  /**
   * Returns a {@link String} representation of the given {@link Iterable} using {@link String#valueOf(boolean)}
   * 
   * @param iterable
   * @return
   */
  public static String toString( Iterable<?> iterable )
  {
    //
    StringBuilder retval = new StringBuilder();
    retval.append( "[" );
    
    //
    boolean first = true;
    for ( Object iElement : iterable )
    {
      //
      try
      {
        //
        retval.append( !first ? "," : "" );
        retval.append( "\n " + String.valueOf( iElement ) );
      }
      catch ( Exception e )
      {
      }
      
      //
      first = false;
    }
    
    //
    retval.append( "\n]\n" );
    
    //
    return retval.toString();
  }
  
  /**
   * @param collection
   * @param joiner
   *          {@link Joiner}
   * @return
   */
  public static <E> String toString( Collection<E> collection, Joiner joiner )
  {
    @SuppressWarnings("unchecked")
    ElementConverter<E, String> elementConverter = (ElementConverter<E, String>) new ElementConverterObjectToString();
    return toString( collection, elementConverter, joiner );
  }
  
  public static <E> String toString( Collection<E> collection, ElementConverter<E, String> elementConverter, Joiner joiner )
  {
    if ( joiner == null )
    {
      joiner = Joiner.on( "," );
    }
    List<String> partList = ListUtils.convert( collection, elementConverter );
    return joiner.join( partList );
  }
  
  /**
   * Returns the sum of the values within an {@link Double} {@link Iterable}.
   * 
   * @param doubleIterable
   * @return
   */
  public static double sumOfCollectionDouble( Iterable<Double> doubleIterable )
  {
    //
    double retval = 0;
    
    //
    for ( Double iValue : doubleIterable )
    {
      if ( iValue != null )
      {
        retval += iValue;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the sum of the values within an {@link Integer} {@link Iterable}.
   * 
   * @param integerIterable
   * @return
   */
  public static int sumOfCollectionInteger( Iterable<Integer> integerIterable )
  {
    //
    int retval = 0;
    
    //
    for ( Integer iValue : integerIterable )
    {
      if ( iValue != null )
      {
        retval += iValue;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Converts a given {@link Collection} into a typed array
   * 
   * @param collection
   * @param clazz
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E[] toArray( Collection<E> collection, Class<? extends E> clazz )
  {
    //
    E[] retvals = null;
    
    //
    if ( collection != null )
    {
      try
      {
        ///
        retvals = (E[]) Array.newInstance( clazz, collection.size() );
        
        //
        Iterator<E> iterator = collection.iterator();
        for ( int indexPosition = 0; iterator.hasNext(); indexPosition++ )
        {
          retvals[indexPosition] = iterator.next();
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns the values of a {@link Integer} {@link Collection} as array.
   * 
   * @param integerCollection
   * @return
   */
  public static int[] toArrayInt( Collection<Integer> integerCollection )
  {
    //
    int[] retvals = new int[integerCollection.size()];
    
    //
    int ii = 0;
    for ( Integer iValue : integerCollection )
    {
      retvals[ii++] = iValue;
    }
    
    //
    return retvals;
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
  public static <K, V, E> Map<K, V> toMap( Iterable<E> iterable,
                                           ElementConverterElementToMapEntry<E, K, V> elementToMapEntryTransformer )
  {
    return MapUtils.valueOf( iterable, elementToMapEntryTransformer );
  }
  
  /**
   * Returns a view of the given {@link Collection} which uses the given {@link Lock} to synchronize all of its methods
   * 
   * @param collection
   * @param lock
   * @return
   */
  public static <E> Collection<E> locked( Collection<E> collection, Lock lock )
  {
    return new LockingCollectionDecorator<E>( collection, lock );
  }
  
  /**
   * Returns a view of the given {@link Collection} which uses a new {@link ReentrantLock} instance to synchronize all of its
   * methods
   * 
   * @param collection
   *          F@return
   */
  public static <E> Collection<E> lockedByReentrantLock( Collection<E> collection )
  {
    Lock lock = new ReentrantLock();
    return new LockingCollectionDecorator<E>( collection, lock );
  }
  
  /**
   * Drains the elements of the given {@link Iterable} to the given {@link Collection}
   * 
   * @param iterable
   * @param collection
   */
  public static <E> void drainTo( Iterable<E> iterable, Collection<E> collection )
  {
    //
    if ( iterable != null && collection != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      IteratorUtils.drainTo( iterator, collection );
    }
  }
  
  /**
   * Drains the elements of the given {@link Iterable} by the given maximum number of elements to the given {@link Collection}.
   * 
   * @param iterable
   * @param collection
   * @param maxNumberOfElements
   */
  public static <E> void drainTo( Iterable<E> iterable, Collection<E> collection, int maxNumberOfElements )
  {
    //
    if ( iterable != null && collection != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      IteratorUtils.drainTo( iterator, collection, maxNumberOfElements );
    }
  }
  
  /**
   * Copies all elements from the given {@link Iterable} into the given {@link Collection}
   * 
   * @param collection
   * @param iterable
   */
  public static <E> void copyIntoCollectionFrom( Collection<E> collection, Iterable<E> iterable )
  {
    if ( collection != null && iterable != null )
    {
      for ( E element : iterable )
      {
        collection.add( element );
      }
    }
  }
  
  /**
   * Copies all elements from the given {@link Iterable} into the given {@link Collection}
   * 
   * @param collection
   * @param iterable
   * @param maxNumberOfElements
   */
  public static <E> void copyIntoCollectionFrom( Collection<E> collection, Iterable<E> iterable, int maxNumberOfElements )
  {
    if ( collection != null && iterable != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      copyIntoCollectionFrom( collection, iterator, maxNumberOfElements );
    }
  }
  
  /**
   * Copies all elements from the given {@link Iterator} into the given {@link Collection} <br>
   * This traverses the {@link Iterator}
   * 
   * @param collection
   * @param iterator
   */
  public static <E> void copyIntoCollectionFrom( Collection<E> collection, Iterator<E> iterator )
  {
    if ( collection != null && iterator != null )
    {
      while ( iterator.hasNext() )
      {
        final E element = iterator.next();
        collection.add( element );
      }
    }
  }
  
  /**
   * Copies all elements from the given {@link Iterator} into the given {@link Collection}.This traverses the {@link Iterator}
   * only as far as necessary.
   * 
   * @param collection
   * @param iterator
   * @param maxNumberOfElements
   */
  public static <E> void copyIntoCollectionFrom( Collection<E> collection, Iterator<E> iterator, int maxNumberOfElements )
  {
    if ( collection != null && iterator != null )
    {
      for ( int ii = 0; ii < maxNumberOfElements && iterator.hasNext(); ii++ )
      {
        final E element = iterator.next();
        collection.add( element );
      }
    }
  }
  
  /**
   * Returns a new {@link CollectionToCollectionAdapter} instance
   * 
   * @param collection
   *          {@link Collection}
   * @param elementBidirectionalConverter
   *          {@link ElementBidirectionalConverter}
   * @return new {@link CollectionToCollectionAdapter} instance
   */
  public static <FROM, TO> Collection<TO> adapter( Collection<FROM> collection,
                                                   ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    return new CollectionToCollectionAdapter<FROM, TO>( collection, elementBidirectionalConverter );
  }
  
  /**
   * Returns a new {@link CollectionComposite} instance for the given {@link Collection}s
   * 
   * @param collectionOfCollections
   * @return
   */
  public static <E> Collection<E> composite( Collection<Collection<E>> collectionOfCollections )
  {
    return new CollectionComposite<E>( collectionOfCollections );
  }
  
  /**
   * Returns a new {@link CollectionComposite} instance for the given {@link Collection}s
   * 
   * @param collections
   * @return
   */
  public static <E> Collection<E> composite( Collection<E>... collections )
  {
    return new CollectionComposite<E>( collections );
  }
}
