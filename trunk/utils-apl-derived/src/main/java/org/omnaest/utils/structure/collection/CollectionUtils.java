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
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;

public class CollectionUtils
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Used to convert a collection element.
   * 
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static interface ElementConverter<FROM, TO>
  {
    /**
     * Converts a given element into another element with the given return type.
     * 
     * @param from
     * @return
     */
    public TO convert( FROM from );
  }
  
  /**
   * Does not change the type of the element and the value of the element being converted.
   * 
   * @author Omnaest
   */
  public static class IdentityElementConverter<T> implements ElementConverter<T, T>
  {
    @Override
    public T convert( T from )
    {
      return from;
    }
  }
  
  /**
   * Does not change the type of the element and the value of the element being converted.
   * 
   * @author Omnaest
   */
  public static class IdentityCastElementConverter<FROM, TO> implements ElementConverter<FROM, TO>
  {
    @SuppressWarnings("unchecked")
    @Override
    public TO convert( FROM from )
    {
      return (TO) from;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns the sum of the values within an {@link Integer} {@link Collection}.
   * 
   * @param collectionInteger
   * @return
   */
  public static int sumOfCollectionInteger( Collection<Integer> collectionInteger )
  {
    //
    int retval = 0;
    
    //
    for ( Integer iValue : collectionInteger )
    {
      retval += iValue;
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the sum of the values within an {@link Double} {@link Collection}.
   * 
   * @param collectionDouble
   * @return
   */
  public static double sumOfCollectionDouble( Collection<Double> collectionDouble )
  {
    //
    double retval = 0;
    
    //
    for ( Double iValue : collectionDouble )
    {
      retval += iValue;
    }
    
    //
    return retval;
  }
  
  /**
   * Prints the collection values to the system out.
   * 
   * @param collection
   */
  public static void printCollection( Collection<?> collection )
  {
    for ( Object iElement : collection )
    {
      try
      {
        System.out.println( String.valueOf( iElement ) );
      }
      catch ( Exception e )
      {
      }
    }
  }
  
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
   * Returns the values of a Integer collection as array.
   * 
   * @param integerList
   * @return
   */
  public static int[] toArrayInt( Collection<Integer> integerList )
  {
    //
    int[] retvals = new int[integerList.size()];
    
    //
    int ii = 0;
    for ( Integer iValue : integerList )
    {
      retvals[ii++] = iValue;
    }
    
    //
    return retvals;
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
   * Converts a given {@link Collection} into another {@link Collection} with other element types using an
   * {@link ElementTransformer}.
   * 
   * @see ElementTransformer
   * @param collectionFrom
   * @param elementTransformer
   * @return
   */
  public static <TO, FROM> Collection<TO> transformCollection( Collection<FROM> collectionFrom,
                                                               ElementTransformer<FROM, TO> elementTransformer )
  {
    return ListUtils.transform( collectionFrom, elementTransformer );
  }
  
  /**
   * Converts a given {@link Collection} into another {@link Collection} with other element types, whereby all elements which
   * convert to null will not be inserted into the target {@link Collection}.
   * 
   * @see ElementTransformer
   * @param collectionFrom
   * @param elementTransformer
   * @return
   */
  public static <TO, FROM> Collection<TO> transformCollectionExcludingNullElements( Collection<FROM> collectionFrom,
                                                                                    ElementTransformer<FROM, TO> elementTransformer )
  {
    return ListUtils.transformListExcludingNullElements( collectionFrom, elementTransformer );
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
  
}
