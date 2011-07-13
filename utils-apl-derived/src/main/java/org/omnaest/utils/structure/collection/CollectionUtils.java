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

import java.util.Collection;

import org.omnaest.utils.structure.collection.list.ListUtil;

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
   * Tests, if the two collection have the same elements.
   * 
   * @param collection1
   * @param collection2
   * @return
   */
  public static boolean equalsUnordered( Collection<?> collection1, Collection<?> collection2 )
  {
    boolean retval = collection1.size() == collection2.size();
    
    for ( Object iObject : collection1 )
    {
      retval &= collection2.contains( iObject );
    }
    
    return retval;
  }
  
  /**
   * Converts a given collection into another collection with other element types.
   * 
   * @param collectionFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> Collection<TO> convertCollection( Collection<FROM> collectionFrom,
                                                             ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtil.convertList( collectionFrom, elementConverter );
  }
  
  /**
   * Converts a given collection into another collection with other element types, whereby all elements which convert to null will
   * not be inserted into the target list.
   * 
   * @param collectionFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> Collection<TO> convertCollectionExcludingNullElements( Collection<FROM> collectionFrom,
                                                                                  ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtil.convertListExcludingNullElements( collectionFrom, elementConverter );
  }
  
}
