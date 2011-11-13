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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.omnaest.utils.reflection.ReflectionUtils;

/**
 * Offers methods for arbitrary {@link Object}s
 * 
 * @author Omnaest
 */
public class ObjectUtils
{
  
  /**
   * Casts a given {@link Object} to a given {@link Class} type. If the given type is a primitive the valueOf(...) method is used
   * .
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
        boolean isObjectTypeString = String.class.isAssignableFrom( objectType );
        
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
              retval = (C) object;
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
   * Returns the object parameter if it is not null, otherwise the defaultObject. <br>
   * <br>
   * Examples:
   * 
   * <pre>
   * ObjectUtils.defaultObject( x, * ) = x;
   * ObjectUtils.defaultObject( null, default ) = default;
   * ObjectUtils.defaultObject( null, null ) = null;
   * </pre>
   * 
   * @param object
   * @param defaultObject
   * @return
   */
  public static <O extends Object> O defaultObject( O object, O defaultObject )
  {
    return object != null ? object : defaultObject;
  }
}
