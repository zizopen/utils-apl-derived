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
package org.omnaest.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectClass;

/**
 * Helper for Java Reflection.
 * 
 * @author Omnaest
 */
public class ReflectionUtils
{
  /**
   * Determines the index position of a declared {@link Method} within a given {@link Class}.
   * 
   * @param clazz
   * @param method
   * @return
   */
  public static int declaredMethodIndexPosition( Class<?> clazz, Method method )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && method != null )
    {
      //
      Method[] declaredMethods = clazz.getDeclaredMethods();
      if ( declaredMethods != null )
      {
        retval = ArrayUtils.indexOf( declaredMethods, method );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Determines the index position of a declared {@link Field} within a given {@link Class}.
   * 
   * @param clazz
   * @param field
   * @return
   */
  public static int declaredFieldIndexPosition( Class<?> clazz, Field field )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && field != null )
    {
      //
      Field[] declaredFields = clazz.getDeclaredFields();
      if ( declaredFields != null )
      {
        retval = ArrayUtils.indexOf( declaredFields, field );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Determines the index position of a declared {@link Field} within a given {@link Class}.
   * 
   * @param clazz
   * @param field
   * @return -1 if the {@link Field} could not be determined at all.
   */
  public static int declaredFieldIndexPosition( Class<?> clazz, String fieldname )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && fieldname != null )
    {
      //
      try
      {
        //
        Field field = clazz.getField( fieldname );
        
        //
        if ( field != null )
        {
          //
          Field[] declaredFields = clazz.getDeclaredFields();
          if ( declaredFields != null )
          {
            retval = ArrayUtils.indexOf( declaredFields, field );
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
   * Returns the number of declared {@link Field}s of a {@link Class}.
   * 
   * @param clazz
   * @return number of declared {@link Field}s or 0 if clazz == null
   */
  public static int numberOfDeclaredFields( Class<?> clazz )
  {
    return clazz != null ? clazz.getDeclaredFields().length : 0;
  }
  
  /**
   * Returns the number of declared {@link Method}s of a {@link Class}.
   * 
   * @param clazz
   * @return number of declared {@link Method}s or 0 if clazz == null
   */
  public static int numberOfDeclaredMethods( Class<?> clazz )
  {
    return clazz != null ? clazz.getDeclaredMethods().length : 0;
  }
  
  /**
   * Creates a new instance of a given {@link Class} using a constructor which has the same parameter signature as the provided
   * arguments.
   * 
   * @param clazz
   * @param args
   */
  public static <B> B createInstanceOf( Class<? extends B> clazz, Object... args )
  {
    //
    B retval = null;
    
    //
    if ( clazz != null )
    {
      //
      Class<?>[] parameterTypes = ListUtils.transform( Arrays.asList( args ), new ElementConverterObjectClass() )
                                           .toArray( new Class<?>[args.length] );
      
      //
      try
      {
        //
        Constructor<? extends B> constructor = clazz.getDeclaredConstructor( parameterTypes );
        
        //
        boolean accessible = constructor.isAccessible();
        
        //
        try
        {
          constructor.setAccessible( true );
          retval = constructor.newInstance( args );
        }
        catch ( Exception e )
        {
        }
        
        //
        constructor.setAccessible( accessible );
      }
      catch ( Exception e )
      {
      }
      
    }
    
    //
    return retval;
  }
}
