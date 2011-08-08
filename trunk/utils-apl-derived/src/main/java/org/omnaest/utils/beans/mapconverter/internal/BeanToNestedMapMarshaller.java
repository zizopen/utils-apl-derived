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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.omnaest.utils.beans.TypeToPropertynameMapAdapter;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter.BeanConversionFilter;

/**
 * @see BeanToNestedMapConverter
 * @author Omnaest
 * @param <B>
 */
public class BeanToNestedMapMarshaller
{
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
  
  private boolean hasToConvertBean( Object bean )
  {
    return this.beanConversionFilter != null && this.beanConversionFilter.hasBeanToBeConverted( bean );
  }
  
  /**
   * @param bean
   * @return
   */
  public Map<String, Object> marshal( Object bean )
  {
    //
    Map<String, Object> retmap = new HashMap<String, Object>();
    
    //
    if ( bean != null )
    {
      //
      if ( this.objectToMapMap.containsKey( bean ) )
      {
        retmap = this.objectToMapMap.get( bean );
      }
      else
      {
        //
        Map<String, Object> mapProxied = TypeToPropertynameMapAdapter.newInstance( bean );
        
        //
        for ( String key : mapProxied.keySet() )
        {
          //
          Object object = mapProxied.get( key );
          
          //
          boolean hasToConvertBean = this.hasToConvertBean( object );
          if ( hasToConvertBean )
          {
            Map<String, Object> map = this.marshal( object );
            this.objectToMapMap.put( object, map );
            object = map;
          }
          
          //
          retmap.put( key, object );
        }
      }
    }
    
    //
    return retmap;
  }
  
}
