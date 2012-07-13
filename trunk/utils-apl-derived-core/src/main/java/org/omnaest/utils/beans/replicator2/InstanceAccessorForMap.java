/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.beans.replicator2;

import java.util.Map;

import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectToString;

/**
 * {@link InstanceAccessor} for {@link Map} instances
 * 
 * @see InstanceAccessor
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class InstanceAccessorForMap implements InstanceAccessor
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -4549376308631699218L;
  
  /* *************************************************** Methods **************************************************** */
  
  @Override
  public PropertyAccessor getPropertyAccessor( final String propertyName, final Object instance )
  {
    final Map<Object, Object> map = instanceAsMap( instance );
    return new PropertyAccessor()
    {
      private static final long serialVersionUID = -7943517135842868232L;
      
      @Override
      public void setValue( Object value )
      {
        if ( map != null )
        {
          map.put( propertyName, value );
        }
      }
      
      @Override
      public Object getValue()
      {
        Object retval = null;
        {
          if ( map != null )
          {
            retval = map.get( propertyName );
          }
        }
        return retval;
      }
      
      @Override
      public Class<?> getType()
      {
        Class<?> retval = null;
        {
          Object value = this.getValue();
          if ( value != null )
          {
            retval = value.getClass();
          }
        }
        return retval;
      }
      
      @Override
      public String getPropertyName()
      {
        return propertyName;
      }
      
      @Override
      public Object getFactoryParameter()
      {
        return map != null ? map.size() : 0;
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  private static Map<Object, Object> instanceAsMap( Object instance )
  {
    return instance instanceof Map ? (Map<Object, Object>) instance : null;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Iterable<String> getPropertyNameIterable( Object instance )
  {
    return instance instanceof Map ? SetUtils.convert( ( (Map) instance ).keySet(), new ElementConverterObjectToString() )
                                  : SetUtils.emptySet();
  }
  
  @Override
  public Class<?> getType()
  {
    return Map.class;
  }
  
}
