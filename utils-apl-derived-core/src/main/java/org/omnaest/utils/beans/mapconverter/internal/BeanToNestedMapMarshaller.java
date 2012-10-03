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

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter.BeanConversionFilter;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;

/**
 * @see BeanToNestedMapConverter
 * @author Omnaest
 * @param <B>
 */
public class BeanToNestedMapMarshaller
{
  /* ********************************************** Constants ********************************************** */
  public final static String               CLASS_IDENTIFIER     = "clazz";
  public final static String               MAP_KEY_IDENTIFIER   = "key";
  public final static String               MAP_VALUE_IDENTIFIER = "value";
  
  /* ********************************************** Variables ********************************************** */
  private BeanConversionFilter             beanConversionFilter = null;
  private Map<Object, Map<String, Object>> objectToMapMap       = new IdentityHashMap<Object, Map<String, Object>>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param beanConversionFilter
   */
  public BeanToNestedMapMarshaller( BeanConversionFilter beanConversionFilter )
  {
    super();
    this.beanConversionFilter = beanConversionFilter;
  }
  
  /**
   * @param bean
   * @param propertyAccessType
   * @return
   */
  public Map<String, Object> marshal( Object bean, PropertyAccessType propertyAccessType )
  {
    //
    final Map<String, Object> retmap = new HashMap<String, Object>();
    
    //
    final PropertyAccessType propertyAccessTypeFinal = propertyAccessType != null ? propertyAccessType
                                                                                 : PropertyAccessType.PROPERTY;
    
    //
    final class Helper
    {
      /**
       * @param declaringPropertyType
       * @param object
       * @param propertyName
       */
      public void convertIfNecessaryAndPutToRetmap( Class<?> declaringPropertyType, Object object, String propertyName )
      {
        //
        boolean hasToConvertBean = hasToConvertBean( declaringPropertyType, object );
        if ( hasToConvertBean )
        {
          //
          Map<String, Object> map = marshal( object, propertyAccessTypeFinal );
          BeanToNestedMapMarshaller.this.objectToMapMap.put( object, map );
          
          //
          retmap.put( propertyName, map );
        }
        else
        {
          //
          retmap.put( propertyName, object );
        }
      }
      
      /**
       * @param declaringType
       * @param bean
       * @return
       */
      private boolean hasToConvertBean( Class<?> declaringType, Object bean )
      {
        return BeanToNestedMapMarshaller.this.beanConversionFilter != null
               && BeanToNestedMapMarshaller.this.beanConversionFilter.hasBeanToBeConverted( declaringType, bean );
      }
    }
    Helper helper = new Helper();
    
    //
    if ( bean != null )
    {
      //
      if ( this.objectToMapMap.containsKey( bean ) )
      {
        return this.objectToMapMap.get( bean );
      }
      else if ( bean instanceof Collection )
      {
        int counter = 0;
        for ( Object object : (Collection<?>) bean )
        {
          //
          Class<?> declaringPropertyType = object != null ? object.getClass() : null;
          String propertyName = "" + counter++;
          
          //
          helper.convertIfNecessaryAndPutToRetmap( declaringPropertyType, object, propertyName );
        }
        
        //
        retmap.put( CLASS_IDENTIFIER, bean.getClass().getName() );
      }
      else if ( bean instanceof Map )
      {
        int counter = 0;
        for ( Entry<?, ?> entry : ( (Map<?, ?>) bean ).entrySet() )
        {
          //
          {
            //
            Object key = entry.getKey();
            
            //
            Class<?> declaringPropertyType = key != null ? key.getClass() : null;
            String propertyName = counter + MAP_KEY_IDENTIFIER;
            helper.convertIfNecessaryAndPutToRetmap( declaringPropertyType, key, propertyName );
          }
          {
            //
            Object value = entry.getValue();
            
            //
            Class<?> declaringPropertyType = value != null ? value.getClass() : null;
            String propertyName = counter + MAP_VALUE_IDENTIFIER;
            helper.convertIfNecessaryAndPutToRetmap( declaringPropertyType, value, propertyName );
          }
          
          //
          counter++;
        }
        
        //
        retmap.put( CLASS_IDENTIFIER, bean.getClass().getName() );
      }
      else
      {
        //
        @SuppressWarnings("unchecked")
        Map<String, BeanPropertyAccessor<Object>> propertyNameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( (Class<Object>) bean.getClass() );
        for ( String propertyName : propertyNameToBeanPropertyAccessorMap.keySet() )
        {
          //
          BeanPropertyAccessor<Object> beanPropertyAccessor = propertyNameToBeanPropertyAccessorMap.get( propertyName )
                                                                                                   .newBeanPropertyAccessorWithPropertyAccessType( propertyAccessType );
          if ( beanPropertyAccessor.isReadable() )
          {
            //
            Object object = beanPropertyAccessor.getPropertyValue( bean );
            Class<?> declaringPropertyType = beanPropertyAccessor.getDeclaringPropertyType();
            
            //
            helper.convertIfNecessaryAndPutToRetmap( declaringPropertyType, object, propertyName );
          }
        }
        
        //
        retmap.put( CLASS_IDENTIFIER, bean.getClass().getName() );
      }
    }
    
    //
    return retmap;
  }
}
