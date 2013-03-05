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
package org.omnaest.utils.structure.array;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * Helper methods for arrays.
 * 
 * @see CollectionUtils
 * @author Omnaest
 */
public class ArrayUtils
{
  
  /**
   * Returns an {@link Array} containing all elements of each of the given {@link Array}s. If any given {@link Array} is null, it
   * will be ignored.<br>
   * <br>
   * As long as at least one {@link Array} instance is given this function returns an {@link Array} instance itself. If no
   * {@link Array} instance is available null will be returned.
   * 
   * @param elementArrays
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E[] merge( E[]... elementArrays )
  {
    //
    final List<E> retlist = new ArrayList<E>();
    
    //
    Class<?> componentType = null;
    for ( E[] elements : elementArrays )
    {
      if ( elements != null )
      {
        //
        if ( componentType == null )
        {
          componentType = componentType( elements.getClass() );
        }
        
        //
        retlist.addAll( Arrays.asList( elements ) );
      }
    }
    
    //
    return componentType != null ? retlist.toArray( (E[]) Array.newInstance( componentType, retlist.size() ) ) : null;
  }
  
  /**
   * Converts a given array into a new array with a new element type.
   * 
   * @param arrayFrom
   * @param arrayToType
   * @param elementConverter
   *          {@link ElementConverter}
   * @return
   */
  public static <TO, FROM> TO[] convertArray( FROM[] arrayFrom,
                                              Class<TO> arrayToType,
                                              ElementConverter<FROM, ? extends TO> elementConverter )
  {
    // 
    @SuppressWarnings("unchecked")
    final TO[] arrayTo = (TO[]) ( arrayFrom != null ? Array.newInstance( arrayToType, arrayFrom.length ) : new Object[0] );
    return convertArray( arrayFrom, arrayTo, elementConverter );
  }
  
  /**
   * Similar to {@link #convertArray(Object[], Class, ElementConverter)} but uses the given target array if the size fits
   * otherwise it will construct a new array with the same component type
   * 
   * @param arrayFrom
   * @param arrayTo
   *          : instance of the array later returned. Use the return value to set this list. The array will not be necessarily
   *          changed only by the call as parameter.
   * @param elementConverter
   *          {@link ElementConverter}
   * @return
   */
  public static <TO, FROM> TO[] convertArray( FROM[] arrayFrom,
                                              TO[] arrayTo,
                                              ElementConverter<FROM, ? extends TO> elementConverter )
  {
    //
    TO[] retvals = null;
    
    //
    if ( arrayTo != null && arrayFrom != null && elementConverter != null )
    {
      //
      if ( arrayTo.length == arrayFrom.length )
      {
        //
        retvals = arrayTo;
        
        //
        int index = 0;
        for ( FROM element : arrayFrom )
        {
          //          
          final TO convertedElement = elementConverter.convert( element );
          retvals[index++] = convertedElement;
        }
      }
      else
      {
        //
        @SuppressWarnings({ "cast", "unchecked" })
        final Class<TO> arrayToType = (Class<TO>) componentType( (Class<TO[]>) arrayTo.getClass() );
        retvals = convertArray( arrayFrom, arrayToType, elementConverter );
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Converts a given array into a new list with a new element type, whereby all elements which convert to null will be excluded
   * in the target list.
   * 
   * @param arrayFrom
   * @param arrayTo
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> TO[] convertArrayExcludingNullElements( FROM[] arrayFrom,
                                                                   TO[] arrayTo,
                                                                   ElementConverter<FROM, TO> elementConverter )
  {
    List<TO> listTo = ListUtils.convertExcludingNullElements( Arrays.asList( arrayFrom ), elementConverter );
    return listTo == null ? null : listTo.toArray( arrayTo );
  }
  
  /**
   * Returns a new array with all tokens of the given string array being trimmed
   * 
   * @param stringArray
   * @return new array with all trimmed tokens
   */
  public static String[] trimStringArrayTokens( String[] stringArray )
  {
    //
    String[] retvals = null;
    
    //
    if ( stringArray != null )
    {
      //
      retvals = new String[stringArray.length];
      
      //
      for ( int ii = 0; ii < retvals.length; ii++ )
      {
        //
        String value = stringArray[ii];
        value = value != null ? value.trim() : null;
        
        //
        retvals[ii] = value;
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns a non primitive {@link Array} for a primitive one
   * 
   * @param primitiveArray
   * @return
   */
  public static Object[] toObject( Object primitiveArray )
  {
    //    
    Object[] retvals = null;
    
    //
    if ( primitiveArray != null )
    {
      try
      {
        //
        Class<? extends Object> primitiveArrayType = primitiveArray.getClass();
        Class<?> componentType = primitiveArrayType.getComponentType();
        if ( componentType.isPrimitive() )
        {
          componentType = ObjectUtils.primitiveWrapperTypeFor( componentType );
        }
        int length = Array.getLength( primitiveArray );
        retvals = (Object[]) Array.newInstance( componentType, length );
        
        //
        for ( int ii = 0; ii < retvals.length; ii++ )
        {
          retvals[ii] = Array.get( primitiveArray, ii );
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
   * Returns true if the given type is not null and {@link Class#isArray()} is true
   * 
   * @param type
   * @return
   */
  public static boolean isArrayType( Class<?> type )
  {
    return type != null && type.isArray();
  }
  
  /**
   * Returns true if the object is an {@link Array}
   * 
   * @param object
   * @return
   */
  public static boolean isArray( Object object )
  {
    return object != null && isArrayType( object.getClass() );
  }
  
  /**
   * Returns the component type of a given array type. Returns null if the given type is no array type or there can no component
   * type be detected.
   * 
   * @see #arrayType(Class)
   * @param arrayType
   * @return
   */
  public static Class<?> componentType( Class<?> arrayType )
  {
    return isArrayType( arrayType ) ? arrayType.getComponentType() : null;
  }
  
  /**
   * Returns the length of a given array instance or -1 if the given instance is not an array instance.
   * 
   * @param arrayObject
   * @return
   */
  public static int length( Object arrayObject )
  {
    return arrayObject != null && isArray( arrayObject ) ? Array.getLength( arrayObject ) : -1;
  }
  
  /**
   * Returns an typed {@link Array} based on the given {@link Collection} and the given array component type<br>
   * <br>
   * If null is given as {@link Collection} null is returned, too. If the given type is null, {@link Object} is used as component
   * type.
   * 
   * @see Collection#toArray(Object[])
   * @param collection
   *          {@link Collection}
   * @param type
   *          {@link Class}
   * @return new {@link Array} instance
   */
  @SuppressWarnings("unchecked")
  public static <E> E[] valueOf( Collection<? extends E> collection, Class<E> type )
  {
    //
    E[] retvals = null;
    
    //
    if ( collection != null )
    {
      retvals = collection.toArray( (E[]) Array.newInstance( type != null ? type : Object.class, collection.size() ) );
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns an typed {@link Array} based on the given {@link Iterable} and the given array component type<br>
   * <br>
   * If null is given as {@link Iterable} null is returned, too. If the given type is null, {@link Object} is used as component
   * type.
   * 
   * @see Collection#toArray(Object[])
   * @param iterable
   *          {@link Collection}
   * @param type
   *          {@link Class}
   * @return new {@link Array} instance
   */
  @SuppressWarnings("unchecked")
  public static <E> E[] valueOf( Iterable<? extends E> iterable, Class<E> type )
  {
    //
    E[] retvals = null;
    
    //
    if ( iterable != null )
    {
      final List<? extends E> list = ListUtils.valueOf( iterable );
      retvals = list.toArray( (E[]) Array.newInstance( type != null ? type : Object.class, list.size() ) );
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns a new {@link Array} instance based on the {@link Class} types of the given elements. The first shared type which can
   * be cast to the expected return type is used.<br>
   * <br>
   * The automatic detection of element types is based on reflection and has a high impact on performance if the given element
   * types have large inherited type graphs.
   * 
   * @see #valueOf(Class, Object...)
   * @param elements
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> E[] valueOf( E... elements )
  {
    //
    final boolean inherited = true;
    final boolean onlyReturnInterfaces = false;
    final boolean intersection = true;
    final Class<?>[] types = ArrayUtils.convertArray( elements, Class.class, new ElementConverter<E, Class>()
    {
      @Override
      public Class convert( E element )
      {
        return element != null ? element.getClass() : null;
      }
    } );
    final Set<Class<?>> assignableTypeSet = ReflectionUtils.assignableTypeSet( inherited, onlyReturnInterfaces, intersection,
                                                                               types );
    
    //
    final Class<E> type = (Class<E>) IterableUtils.firstElement( assignableTypeSet );
    
    //
    return valueOf( type, elements );
  }
  
  /**
   * @param elements
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> E[] valueOf( Class<E> type, E... elements )
  {
    //
    E[] retvals = null;
    
    //
    final Class componentType = type == null ? Object.class : type;
    final int length = elements.length;
    retvals = (E[]) Array.newInstance( componentType, length );
    
    //
    for ( int ii = 0; ii < length; ii++ )
    {
      retvals[ii] = elements[ii];
    }
    
    //
    return retvals;
  }
  
  /**
   * Return the sum of all given {@link Integer} values
   * 
   * @param values
   * @return
   */
  public static int sumOf( int... values )
  {
    //
    int retval = 0;
    
    //
    for ( int value : values )
    {
      retval += value;
    }
    
    //
    return retval;
  }
  
  /**
   * Calculates a {@link BitSet} which has its bits set to true at the same index where the related element from one array to the
   * other array is not {@link #equals(Object)}
   * 
   * @param elements1
   * @param elements2
   * @return {@link BitSet} with modified index bits set to true
   */
  public static <E> BitSet differenceBitSet( E[] elements1, E[] elements2 )
  {
    final BitSet retvals = new BitSet();
    if ( elements1 == null && elements2 != null )
    {
      retvals.set( 0, elements2.length );
    }
    else if ( elements1 != null && elements2 == null )
    {
      retvals.set( 0, elements1.length );
    }
    else if ( elements1 != null && elements2 != null )
    {
      for ( int ii = 0; ii < elements1.length || ii < elements2.length; ii++ )
      {
        if ( ii >= elements1.length || ii >= elements2.length )
        {
          retvals.set( ii );
        }
        else if ( !org.apache.commons.lang3.ObjectUtils.equals( elements1[ii], elements2[ii] ) )
        {
          retvals.set( ii );
        }
      }
    }
    return retvals;
  }
  
  /**
   * Returns an array type based on the element type
   * 
   * @see #componentType(Class)
   * @param componentType
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> Class<E[]> arrayType( Class<E> componentType )
  {
    return (Class<E[]>) Array.newInstance( componentType, 0 ).getClass();
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[][] splitAsArrayGroups( Class<T> elementType, T[] array, int... groupStarts )
  {
    List<T[]> retlist = new ArrayList<T[]>();
    if ( groupStarts != null )
    {
      boolean first = true;
      int groupStart = 0;
      for ( int groupEnd : groupStarts )
      {
        if ( !first )
        {
          T[] subArray = Arrays.copyOfRange( array, groupStart, groupEnd );
          retlist.add( subArray );
        }
        groupStart = groupEnd;
        first = false;
      }
      T[] subArray = Arrays.copyOfRange( array, groupStart, array.length );
      retlist.add( subArray );
    }
    return retlist.toArray( (T[][]) Array.newInstance( elementType, 0, 0 ) );
  }
}
