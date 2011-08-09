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
package org.omnaest.utils.beans.mapconverter.internal;

import java.util.IdentityHashMap;
import java.util.Map;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;
import org.omnaest.utils.reflection.ReflectionUtils;

/**
 * @see BeanToNestedMapConverter
 * @author Omnaest
 * @param <B>
 */
public class BeanToNestedMapUnMarshaller<B>
{
  /* ********************************************** Variables ********************************************** */
  private Class<? extends B>               beanClass      = null;
  private Map<Map<String, Object>, Object> mapToObjectMap = new IdentityHashMap<Map<String, Object>, Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param beanClass
   */
  public BeanToNestedMapUnMarshaller( Class<? extends B> beanClass )
  {
    super();
    this.beanClass = beanClass;
  }
  
  /**
   * @param map
   * @param propertyAccessType
   * @return
   */
  @SuppressWarnings("unchecked")
  public B unmarshal( Map<String, Object> map, PropertyAccessType propertyAccessType )
  {
    return (B) this.unmarshalToObject( map, propertyAccessType );
  }
  
  /**
   * @param map
   * @param propertyAccessType
   * @return
   */
  @SuppressWarnings("unchecked")
  private Object unmarshalToObject( Map<String, Object> map, PropertyAccessType propertyAccessType )
  {
    //
    Object retval = null;
    
    //
    if ( propertyAccessType == null )
    {
      propertyAccessType = PropertyAccessType.PROPERTY;
    }
    
    //
    if ( map != null )
    {
      //
      if ( this.mapToObjectMap.containsKey( map ) )
      {
        retval = this.mapToObjectMap.get( map );
      }
      else
      {
        //        
        Class<Object> objectClass = (Class<Object>) this.beanClass;
        
        //
        String canonicalClassName = (String) map.get( BeanToNestedMapMarshaller.CLASS_IDENTIFIER );
        if ( canonicalClassName != null )
        {
          objectClass = BeanToNestedMapUnMarshaller.determineObjectClass( canonicalClassName );
        }
        
        //
        Object beanNew = ReflectionUtils.createInstanceOf( objectClass );
        
        //
        if ( beanNew != null )
        {
          Map<String, BeanPropertyAccessor<Object>> propertyNameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( objectClass );
          for ( String propertyName : propertyNameToBeanPropertyAccessorMap.keySet() )
          {
            //
            BeanPropertyAccessor<Object> beanPropertyAccessor = propertyNameToBeanPropertyAccessorMap.get( propertyName );
            beanPropertyAccessor.setPropertyAccessType( propertyAccessType );
            if ( beanPropertyAccessor.isWritable() )
            {
              //
              Object value = map.get( propertyName );
              if ( value == null )
              {
                beanPropertyAccessor.setPropertyValue( beanNew, value );
              }
              else
              {
                //
                Class<?> propertyType = beanPropertyAccessor.getDeclaringPropertyType();
                if ( value instanceof Map && propertyType != null && !Map.class.isAssignableFrom( propertyType ) )
                {
                  //
                  Map<String, Object> subMap = (Map<String, Object>) value;
                  Object valueUnmarshalled = this.unmarshalToObject( subMap, propertyAccessType );
                  this.mapToObjectMap.put( subMap, valueUnmarshalled );
                  beanPropertyAccessor.setPropertyValue( beanNew, valueUnmarshalled );
                }
                else
                {
                  beanPropertyAccessor.setPropertyValue( beanNew, value );
                }
              }
            }
          }
          
          //
          retval = beanNew;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * @param className
   * @return
   */
  @SuppressWarnings("unchecked")
  private static Class<Object> determineObjectClass( String className )
  {
    //
    Class<Object> retval = null;
    
    //
    if ( className != null )
    {
      // 
      try
      {
        //
        retval = (Class<Object>) Class.forName( className );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
}
