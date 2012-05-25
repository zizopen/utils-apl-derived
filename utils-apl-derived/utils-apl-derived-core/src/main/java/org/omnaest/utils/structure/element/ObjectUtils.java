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
package org.omnaest.utils.structure.element;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterRegistration;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectToTreeNodeAdapter;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNavigator;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode.ObjectModel;

/**
 * Helper which offers methods allowing to deal with arbitrary {@link Object}s
 * 
 * @author Omnaest
 */
public class ObjectUtils
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Result of the analysis for primitive arrays
   * 
   * @author Omnaest
   */
  protected static class PrimitiveTypeArrayAnalysisResult
  {
    /* ********************************************** Variables ********************************************** */
    protected final boolean isByteArray;
    protected final boolean isShortArray;
    protected final boolean isIntArray;
    protected final boolean isLongArray;
    protected final boolean isFloatArray;
    protected final boolean isDoubleArray;
    protected final boolean isBooleanArray;
    protected final boolean isCharArray;
    protected final boolean isPrimitiveArray;
    
    /* ********************************************** Methods ********************************************** */
    public PrimitiveTypeArrayAnalysisResult( boolean isByteArray, boolean isShortArray, boolean isIntArray, boolean isLongArray,
                                             boolean isFloatArray, boolean isDoubleArray, boolean isBooleanArray,
                                             boolean isCharArray, boolean isPrimitiveArray )
    {
      super();
      this.isByteArray = isByteArray;
      this.isShortArray = isShortArray;
      this.isIntArray = isIntArray;
      this.isLongArray = isLongArray;
      this.isFloatArray = isFloatArray;
      this.isDoubleArray = isDoubleArray;
      this.isBooleanArray = isBooleanArray;
      this.isCharArray = isCharArray;
      this.isPrimitiveArray = isPrimitiveArray;
    }
    
    public boolean isByteArray()
    {
      return this.isByteArray;
    }
    
    public boolean isShortArray()
    {
      return this.isShortArray;
    }
    
    public boolean isIntArray()
    {
      return this.isIntArray;
    }
    
    public boolean isLongArray()
    {
      return this.isLongArray;
    }
    
    public boolean isFloatArray()
    {
      return this.isFloatArray;
    }
    
    public boolean isDoubleArray()
    {
      return this.isDoubleArray;
    }
    
    public boolean isBooleanArray()
    {
      return this.isBooleanArray;
    }
    
    public boolean isCharArray()
    {
      return this.isCharArray;
    }
    
    public boolean isPrimitiveArray()
    {
      return this.isPrimitiveArray;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "PrimitiveTypeArrayAnalysisResult [isByteArray=" );
      builder.append( this.isByteArray );
      builder.append( ", isShortArray=" );
      builder.append( this.isShortArray );
      builder.append( ", isIntArray=" );
      builder.append( this.isIntArray );
      builder.append( ", isLongArray=" );
      builder.append( this.isLongArray );
      builder.append( ", isFloatArray=" );
      builder.append( this.isFloatArray );
      builder.append( ", isDoubleArray=" );
      builder.append( this.isDoubleArray );
      builder.append( ", isBooleanArray=" );
      builder.append( this.isBooleanArray );
      builder.append( ", isCharArray=" );
      builder.append( this.isCharArray );
      builder.append( ", isPrimitiveArray=" );
      builder.append( this.isPrimitiveArray );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Casts a given {@link Array} to a {@link Map} class. This is done by toggling between key and value for every index position
   * of the given array.
   * 
   * @param mapType
   * @param keyType
   * @param valueType
   * @param array
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <C> C castArrayToMap( Class<? extends C> mapType, Class<?> keyType, Class<?> valueType, Object array )
  {
    //
    C retval = null;
    
    //
    if ( mapType != null && keyType != null && valueType != null )
    {
      //
      try
      {
        //
        if ( ( array instanceof Object[] ) )
        {
          //
          Object[] objects = (Object[]) array;
          
          //          
          if ( LinkedHashMap.class.equals( mapType ) || mapType.isInterface() )
          {
            //
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            
            //
            boolean toggleIsKey = true;
            Object lastKey = null;
            for ( Object iObject : objects )
            {
              if ( toggleIsKey )
              {
                lastKey = iObject;
              }
              else
              {
                //
                Object key = castTo( keyType, lastKey );
                Object value = castTo( valueType, iObject );
                
                //
                map.put( key, value );
              }
              
              //
              toggleIsKey = !toggleIsKey;
            }
            
            //
            if ( !toggleIsKey )
            {
              //
              Object key = castTo( keyType, lastKey );
              Object value = null;
              
              //
              map.put( key, value );
            }
            
            //
            retval = (C) map;
          }
          else if ( ReflectionUtils.hasConstructorFor( mapType, Map.class ) )
          {
            Map<Object, Object> map = castArrayToMap( LinkedHashMap.class, keyType, valueType, array );
            retval = ReflectionUtils.createInstanceOf( mapType, map );
          }
        }
        else if ( array != null && array.getClass().isArray() )
        {
          //
          Object[] objects = ArrayUtils.toObject( array );
          retval = castArrayToMap( mapType, keyType, valueType, objects );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    if ( retval == null )
    {
      try
      {
        retval = (C) array;
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Casts a given {@link Array} to a wrapper {@link Class} type and its elements to the element {@link Class} type using
   * {@link #castTo(Class, Object)}.
   * 
   * @param wrapperType
   * @param elementType
   * @param array
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <C> C castArrayTo( Class<C> wrapperType, Class<?> elementType, Object array )
  {
    //
    C retval = null;
    
    //
    if ( wrapperType != null && elementType != null )
    {
      //
      try
      {
        //
        if ( ( array instanceof Object[] ) )
        {
          //
          Object[] objects = (Object[]) array;
          
          //
          final PrimitiveTypeArrayAnalysisResult primitiveTypeArrayAnalysisResult;
          final boolean isWrapperTypeAnArray;
          
          //
          if ( ArrayList.class.equals( wrapperType )
               || ( List.class.isAssignableFrom( wrapperType ) && wrapperType.isInterface() ) )
          {
            //
            List<Object> list = new ArrayList<Object>();
            for ( Object iObject : objects )
            {
              list.add( castTo( elementType, iObject ) );
            }
            retval = (C) list;
          }
          else if ( HashSet.class.equals( wrapperType )
                    || ( Set.class.isAssignableFrom( wrapperType ) && wrapperType.isInterface() ) )
          {
            //
            Set<Object> set = new HashSet<Object>();
            for ( Object iObject : objects )
            {
              set.add( castTo( elementType, iObject ) );
            }
            retval = (C) set;
          }
          else if ( Map.class.isAssignableFrom( wrapperType ) )
          {
            //
            Class<Map<?, ?>> mapType = (Class<Map<?, ?>>) wrapperType;
            Class<?> keyType = elementType;
            Class<?> valueType = elementType;
            retval = (C) castArrayToMap( mapType, keyType, valueType, array );
          }
          else if ( ( isWrapperTypeAnArray = wrapperType.isArray() )
                    && ( primitiveTypeArrayAnalysisResult = analyzeForPrimitiveTypeArray( wrapperType ) ).isPrimitiveArray() )
          {
            //
            Object[] newObjects = castArrayTo( Object[].class, elementType, objects );
            if ( primitiveTypeArrayAnalysisResult.isBooleanArray() )
            {
              boolean[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Boolean[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isByteArray() )
            {
              byte[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Byte[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isCharArray() )
            {
              char[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Character[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isDoubleArray() )
            {
              double[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Double[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isFloatArray() )
            {
              float[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Float[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isIntArray() )
            {
              int[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Integer[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isLongArray() )
            {
              long[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Long[]) newObjects );
              retval = (C) newArray;
            }
            else if ( primitiveTypeArrayAnalysisResult.isShortArray() )
            {
              short[] newArray = org.apache.commons.lang3.ArrayUtils.toPrimitive( (Short[]) newObjects );
              retval = (C) newArray;
            }
          }
          else if ( isWrapperTypeAnArray )
          {
            //
            Object[] newObjects = ArrayUtils.toObject( Array.newInstance( elementType, objects.length ) );
            for ( int ii = 0; ii < newObjects.length; ii++ )
            {
              newObjects[ii] = castTo( elementType, objects[ii] );
            }
            
            //
            retval = (C) newObjects;
          }
          else if ( ReflectionUtils.hasConstructorFor( wrapperType, array ) )
          {
            retval = ReflectionUtils.createInstanceOf( wrapperType, array );
          }
          else if ( ReflectionUtils.hasConstructorFor( wrapperType, Collection.class ) )
          {
            Collection<Object> collection = castArrayTo( ArrayList.class, elementType, array );
            retval = ReflectionUtils.createInstanceOf( wrapperType, collection );
          }
        }
        else if ( array != null && array.getClass().isArray() )
        {
          //
          Object[] objects = ArrayUtils.toObject( array );
          retval = castArrayTo( wrapperType, elementType, objects );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    if ( retval == null )
    {
      try
      {
        retval = (C) array;
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  private static PrimitiveTypeArrayAnalysisResult analyzeForPrimitiveTypeArray( Class<?> type )
  {
    //    
    boolean isByteArray = byte[].class.equals( type );
    boolean isShortArray = !isByteArray && short[].class.equals( type );
    boolean isIntArray = !isShortArray && !isByteArray && int[].class.equals( type );
    boolean isLongArray = !isIntArray && !isShortArray && !isByteArray && long[].class.equals( type );
    boolean isFloatArray = !isLongArray && !isIntArray && !isShortArray && !isByteArray && float[].class.equals( type );
    boolean isDoubleArray = !isFloatArray && !isLongArray && !isIntArray && !isShortArray && !isByteArray
                            && double[].class.equals( type );
    boolean isBooleanArray = !isDoubleArray && !isFloatArray && !isLongArray && !isIntArray && !isShortArray && !isByteArray
                             && boolean[].class.equals( type );
    boolean isCharArray = !isBooleanArray && !isDoubleArray && !isFloatArray && !isLongArray && !isIntArray && !isShortArray
                          && !isByteArray && char[].class.equals( type );
    boolean isPrimitiveArray = isByteArray || isShortArray || isIntArray || isLongArray || isFloatArray || isDoubleArray
                               || isBooleanArray || isCharArray;
    return new PrimitiveTypeArrayAnalysisResult( isByteArray, isShortArray, isIntArray, isLongArray, isFloatArray, isDoubleArray,
                                                 isBooleanArray, isCharArray, isPrimitiveArray );
  }
  
  /**
   * Casts a given {@link Object} to a given {@link Class} type. If the given type is a primitive the valueOf(...) method is used.
   * 
   * @param type
   * @param object
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <C> C castTo( Class<C> type, Object object )
  {
    //
    C retval = null;
    
    //
    if ( object != null )
    {
      //
      try
      {
        //
        Class<?> objectType = object.getClass();
        
        //
        boolean isObjectTypeString = String.class.equals( objectType );
        
        //
        if ( String.class.equals( type ) )
        {
          retval = (C) String.valueOf( object );
        }
        else if ( Integer.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Integer.valueOf( (String) object );
        }
        else if ( int.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Integer.valueOf( (String) object );
        }
        else if ( Long.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Long.valueOf( (String) object );
        }
        else if ( long.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Long.valueOf( (String) object );
        }
        else if ( Byte.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Byte.valueOf( (String) object );
        }
        else if ( byte.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Byte.valueOf( (String) object );
        }
        else if ( Short.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Short.valueOf( (String) object );
        }
        else if ( short.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Short.valueOf( (String) object );
        }
        else if ( Float.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Float.valueOf( (String) object );
        }
        else if ( float.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Float.valueOf( (String) object );
        }
        else if ( Double.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Double.valueOf( (String) object );
        }
        else if ( double.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Double.valueOf( (String) object );
        }
        else if ( BigDecimal.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) new BigDecimal( (String) object );
        }
        else if ( BigInteger.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) new BigInteger( (String) object );
        }
        else if ( Boolean.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Boolean.valueOf( (String) object );
        }
        else if ( boolean.class.equals( type ) && isObjectTypeString )
        {
          retval = (C) Boolean.valueOf( (String) object );
        }
        else if ( ( double.class.equals( type ) || Double.class.equals( type ) )
                  && ( float.class.equals( objectType ) || Float.class.equals( objectType ) ) )
        {
          retval = (C) ( (Double) ( (double) ( (Float) object ) ) );
        }
        else if ( ( long.class.equals( type ) || Long.class.equals( type ) )
                  && ( int.class.equals( objectType ) || Integer.class.equals( objectType ) ) )
        {
          retval = (C) ( (Long) ( (long) ( (Integer) object ) ) );
        }
        else if ( ( long.class.equals( type ) || Long.class.equals( type ) )
                  && ( short.class.equals( objectType ) || Short.class.equals( objectType ) ) )
        {
          retval = (C) ( (Long) ( (long) ( (Short) object ) ) );
        }
        else if ( ( long.class.equals( type ) || Long.class.equals( type ) )
                  && ( byte.class.equals( objectType ) || Byte.class.equals( objectType ) ) )
        {
          retval = (C) ( (Long) ( (long) ( (Byte) object ) ) );
        }
        else if ( ( int.class.equals( type ) || Integer.class.equals( type ) )
                  && ( short.class.equals( objectType ) || Short.class.equals( objectType ) ) )
        {
          retval = (C) ( (Integer) ( (int) ( (Short) object ) ) );
        }
        else if ( ( int.class.equals( type ) || Integer.class.equals( type ) )
                  && ( byte.class.equals( objectType ) || Byte.class.equals( objectType ) ) )
        {
          retval = (C) ( (Integer) ( (int) ( (Byte) object ) ) );
        }
        else
        {
          //
          final ElementConverter<Object, C> elementConverter = ElementConverterRegistration.determineElementConverterFor( (Class<Object>) objectType,
                                                                                                                          type );
          if ( elementConverter != null )
          {
            retval = elementConverter.convert( object );
          }
          else
          {
            
            //
            C createdInstance = ReflectionUtils.createInstanceOf( type, object );
            if ( createdInstance != null )
            {
              retval = createdInstance;
            }
            else
            {
              //
              createdInstance = ReflectionUtils.createInstanceUsingValueOfMethod( type, object );
              if ( createdInstance != null )
              {
                retval = createdInstance;
              }
              else
              {
                try
                {
                  retval = type.cast( object );
                }
                catch ( Exception e )
                {
                }
                
                //
                if ( retval == null )
                {
                  retval = (C) object;
                }
              }
            }
          }
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true if the given {@link Class} type is a wrapper type of a primitive.
   * 
   * @param type
   * @return
   */
  public static boolean isPrimitiveWrapperType( Class<?> type )
  {
    //   
    return type != null
           && ( Integer.class.equals( type ) || Long.class.equals( type ) || Short.class.equals( type )
                || Byte.class.equals( type ) || Character.class.equals( type ) || Float.class.equals( type )
                || Double.class.equals( type ) || Boolean.class.equals( type ) || Void.class.equals( type ) );
  }
  
  /**
   * Returns true if type is not null and {@link Class#isPrimitive()} is true for the given type
   * 
   * @param type
   * @return
   */
  public static boolean isPrimitiveType( Class<?> type )
  {
    return type != null && type.isPrimitive();
  }
  
  /**
   * Returns true if {@link #isPrimitiveType(Class)} or {@link #isPrimitiveWrapperType(Class)} returns true
   * 
   * @param type
   * @return
   */
  public static boolean isPrimitiveOrPrimitiveWrapperType( Class<?> type )
  {
    return isPrimitiveType( type ) || isPrimitiveWrapperType( type );
  }
  
  /**
   * <pre>
   * isString( null ) = false
   * isString( "" ) = true
   * isString( "xyz" ) = true
   * isString( 123 ) = false
   * </pre>
   * 
   * @param object
   * @return
   */
  public static boolean isString( Object object )
  {
    return isAssignableFromInstance( String.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link Map} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isMap( Object object )
  {
    return isAssignableFromInstance( Map.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link SortedMap} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isSortedMap( Object object )
  {
    return isAssignableFromInstance( SortedMap.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link SortedSet} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isSortedSet( Object object )
  {
    return isAssignableFromInstance( SortedSet.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link List} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isList( Object object )
  {
    return isAssignableFromInstance( List.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link Collection} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isCollection( Object object )
  {
    return isAssignableFromInstance( Collection.class, object );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and a {@link Iterable} derived type
   * 
   * @param object
   * @return
   */
  public static boolean isIterable( Object object )
  {
    return isAssignableFromInstance( Iterable.class, object );
  }
  
  /**
   * Returns true if the given target {@link Class} is {@link Class#isAssignableFrom(Class)} from the given {@link Object}. In
   * other words is the assignment "<code>TargetType target = (TargetType) object;</code>" valid
   * 
   * @param targetType
   * @param object
   * @return
   */
  public static boolean isAssignableFromInstance( Class<?> targetType, Object object )
  {
    return object != null && targetType != null && targetType.isAssignableFrom( object.getClass() );
  }
  
  /**
   * Returns true if the given {@link Object} is not null and an {@link Array} type.
   * 
   * @see Class#isArray()
   * @param object
   * @return
   */
  public static boolean isArray( Object object )
  {
    return ArrayUtils.isArray( object );
  }
  
  /**
   * Returns the {@link Object} type for the given type. <br>
   * <br>
   * 
   * <pre>
   * objectTypeFor(null) = null
   * objectTypeFor(double) = Double
   * objectTypeFor(String) = String
   * objectTypeFor(Object) = Object
   * </pre>
   * 
   * @see #primitiveWrapperTypeFor(Class)
   * @see #isPrimitiveOrPrimitiveWrapperType(Class)
   * @param type
   * @return
   */
  public static Class<?> objectTypeFor( Class<?> type )
  {
    return type != null ? ( isPrimitiveType( type ) ? primitiveWrapperTypeFor( type ) : type ) : null;
  }
  
  /**
   * Returns the auto boxing type for a primitive {@link Class} type. <br>
   * <br>
   * 
   * <pre>
   * wrapperTypeForPrimitiveType( null ) = null
   * wrapperTypeForPrimitiveType( boolean ) = Boolean
   * wrapperTypeForPrimitiveType( double ) = Double
   * wrapperTypeForPrimitiveType( primitiveType ) = wrapperType
   * wrapperTypeForPrimitiveType( Double ) = Double
   * wrapperTypeForPrimitiveType( wrapperType ) = wrapperType
   * wrapperTypeForPrimitiveType( String ) = null
   * </pre>
   * 
   * @see #objectTypeFor(Class)
   * @param primitiveType
   * @return
   */
  public static Class<?> primitiveWrapperTypeFor( Class<?> primitiveType )
  {
    //
    Class<?> retval = null;
    
    //
    if ( primitiveType != null )
    {
      //
      if ( primitiveType.isPrimitive() )
      {
        if ( primitiveType.equals( int.class ) )
        {
          retval = Integer.class;
        }
        else if ( primitiveType.equals( long.class ) )
        {
          retval = Long.class;
        }
        else if ( primitiveType.equals( short.class ) )
        {
          retval = Short.class;
        }
        else if ( primitiveType.equals( byte.class ) )
        {
          retval = Byte.class;
        }
        else if ( primitiveType.equals( char.class ) )
        {
          retval = Character.class;
        }
        else if ( primitiveType.equals( float.class ) )
        {
          retval = Float.class;
        }
        else if ( primitiveType.equals( double.class ) )
        {
          retval = Double.class;
        }
        else if ( primitiveType.equals( boolean.class ) )
        {
          retval = Boolean.class;
        }
        else if ( primitiveType.equals( void.class ) )
        {
          retval = Void.class;
        }
      }
      else if ( isPrimitiveWrapperType( primitiveType ) )
      {
        return primitiveType;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the object parameter if it is not null, otherwise the defaultObject. <br>
   * <br>
   * Examples:
   * 
   * <pre>
   * ObjectUtils.defaultObject( x, * ) = x;
   * ObjectUtils.defaultObject( null, default ) = default;
   * ObjectUtils.defaultObject( null, null, null, default ) = default;
   * ObjectUtils.defaultObject( null, default, null, null ) = default;
   * ObjectUtils.defaultObject( null, default1, null, default2 ) = default1;
   * ObjectUtils.defaultObject( null, null ) = null;
   * </pre>
   * 
   * @param object
   * @param defaultObject
   * @param defaultObjects
   * @return
   */
  public static <O extends Object> O defaultIfNull( O object, O defaultObject, O... defaultObjects )
  {
    //
    O retval = null;
    
    //
    if ( object != null )
    {
      retval = object;
    }
    else if ( defaultObject != null )
    {
      retval = defaultObject;
    }
    else if ( defaultObjects.length > 1 )
    {
      retval = defaultIfNull( defaultObjects[0], defaultObjects[1],
                              org.apache.commons.lang3.ArrayUtils.subarray( defaultObjects, 2, defaultObjects.length ) );
    }
    else if ( defaultObjects.length == 1 )
    {
      retval = defaultObjects[0];
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the object parameter if it is not null, otherwise the object created by the given {@link Factory} . <br>
   * <br>
   * Examples:
   * 
   * <pre>
   * ObjectUtils.defaultObject( x, * ) = x;
   * ObjectUtils.defaultObject( null, factory ) = factory.newInstance();
   * ObjectUtils.defaultObject( null, null ) = null;
   * </pre>
   * 
   * @param object
   * @param defaultObjectFactory
   * @return
   */
  public static <O extends Object> O defaultIfNull( O object, Factory<O> defaultObjectFactory )
  {
    return object != null ? object : ( defaultObjectFactory != null ? defaultObjectFactory.newInstance() : null );
  }
  
  /**
   * Generates a hierarchy representation of the object graph for the given {@link Object}.<br>
   * <br>
   * E.g.:<br>
   * 
   * <pre>
   * |--[ org.omnaest.utils.structure.hierarchy.tree.adapter.ObjectToTreeNodeAdapterTest$TestClass@5801319c ]
   *    |--[ fieldDouble = 1.234 ]
   *    |--[ fieldString = value1 ]
   *    |--[ fieldString2 = value2 ]
   *    |--[ testClassSub = org.omnaest.utils.structure.hierarchy.tree.adapter.ObjectToTreeNodeAdapterTest$TestClassSub@790bc49d ]
   *       |--[ fieldString = value3 ]
   * </pre>
   * 
   * @param object
   * @return
   */
  public static String toStringAsNestedHierarchy( Object object )
  {
    return String.valueOf( treeNavigator( object ) );
  }
  
  /**
   * Returns a {@link ObjectTreeNavigator} for the given {@link Object}
   * 
   * @param object
   * @return
   */
  public static ObjectTreeNavigator treeNavigator( Object object )
  {
    return new ObjectTreeNavigator( new ObjectToTreeNodeAdapter( new ObjectModel( object ) ) );
  }
  
}
