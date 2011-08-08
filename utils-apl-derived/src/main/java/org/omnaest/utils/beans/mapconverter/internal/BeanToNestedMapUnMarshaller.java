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
   * @return
   */
  @SuppressWarnings("unchecked")
  public B unmarshal( Map<String, Object> map )
  {
    //
    B retval = null;
    
    //
    if ( map != null )
    {
      //
      if ( this.mapToObjectMap.containsKey( map ) )
      {
        retval = (B) this.mapToObjectMap.get( map );
      }
      else
      {
        //
        B beanNew = ReflectionUtils.createInstanceOf( this.beanClass );
        
        //
        Map<String, BeanPropertyAccessor<B>> propertyNameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( (Class<B>) this.beanClass );
        for ( String propertyName : propertyNameToBeanPropertyAccessorMap.keySet() )
        {
          //
          BeanPropertyAccessor<B> beanPropertyAccessor = propertyNameToBeanPropertyAccessorMap.get( propertyName );
          
          //
          Object value = map.get( propertyName );
          if ( value == null )
          {
            beanPropertyAccessor.setPropertyValue( beanNew, value );
          }
          else
          {
            //
            Class<?> propertyType = beanPropertyAccessor.determinePropertyType();
            if ( value instanceof Map && propertyType != null && !Map.class.isAssignableFrom( propertyType ) )
            {
              //
              Map<String, Object> subMap = (Map<String, Object>) value;
              B valueUnmarshalled = this.unmarshal( subMap );
              this.mapToObjectMap.put( subMap, valueUnmarshalled );
              beanPropertyAccessor.setPropertyValue( beanNew, valueUnmarshalled );
            }
            else
            {
              beanPropertyAccessor.setPropertyValue( beanNew, value );
            }
          }
        }
        
        //
        retval = beanNew;
      }
    }
    
    //
    return retval;
  }
}
