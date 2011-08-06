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
package org.omnaest.utils.beans;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;
import org.omnaest.utils.structure.map.MapAbstract;

/**
 * Adapter to create a {@link Map} view on an underlying Java Bean object. Modifications to the bean or the map will be reflected
 * to each other.
 * 
 * @see MapToTypeAdapter
 * @author Omnaest
 * @param <B>
 */
public class TypeToPropertynameMapAdapter<B> extends MapAbstract<String, Object>
{
  /* ********************************************** Variables ********************************************** */
  protected B                                    bean                                  = null;
  protected Map<String, BeanPropertyAccessor<B>> propertynameToBeanPropertyAccessorMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Factory method to create a {@link Map} view on a given Java Bean object. Changes to this map will be translated to the Java
   * Bean object and vice versa.
   * 
   * @param <B>
   * @param bean
   * @return
   */
  public static <B> Map<String, Object> newInstance( B bean )
  {
    //
    Map<String, Object> retmap = null;
    
    //
    if ( bean != null )
    {
      retmap = new TypeToPropertynameMapAdapter<B>( bean );
    }
    
    //
    return retmap;
  }
  
  /**
   * @see #newInstance(Object)
   * @param bean
   */
  protected TypeToPropertynameMapAdapter( B bean )
  {
    //
    super();
    
    //
    @SuppressWarnings("unchecked")
    Class<B> beanClass = (Class<B>) bean.getClass();
    
    //
    this.bean = bean;
    this.propertynameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass );
  }
  
  @Override
  public Object get( Object key )
  {
    //
    Object retval = null;
    
    //
    BeanPropertyAccessor<B> beanPropertyAccessor = this.propertynameToBeanPropertyAccessorMap.get( key );
    if ( beanPropertyAccessor != null && beanPropertyAccessor.hasGetter() )
    {
      retval = beanPropertyAccessor.getPropertyValue( this.bean );
    }
    
    // 
    return retval;
  }
  
  @Override
  public Object put( String key, Object value )
  {
    //
    Object retval = null;
    
    //
    BeanPropertyAccessor<B> beanPropertyAccessor = this.propertynameToBeanPropertyAccessorMap.get( key );
    if ( beanPropertyAccessor != null )
    {
      //
      if ( beanPropertyAccessor.hasGetter() )
      {
        retval = beanPropertyAccessor.getPropertyValue( this.bean );
      }
      
      //
      if ( beanPropertyAccessor.hasSetter() )
      {
        beanPropertyAccessor.setPropertyValue( this.bean, value );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Object remove( Object key )
  {
    //
    Object retval = null;
    
    //
    if ( key instanceof String )
    {
      retval = this.put( (String) key, null );
    }
    
    //
    return retval;
  }
  
  @Override
  public Set<String> keySet()
  {
    return this.propertynameToBeanPropertyAccessorMap.keySet();
  }
  
  @Override
  public Collection<Object> values()
  {
    ElementTransformer<BeanPropertyAccessor<B>, Object> elementTransformer = new ElementTransformer<BeanPropertyAccessor<B>, Object>()
    {
      @Override
      public Object transformElement( BeanPropertyAccessor<B> beanPropertyAccessor )
      {
        return beanPropertyAccessor.hasGetter() ? beanPropertyAccessor.getPropertyValue( TypeToPropertynameMapAdapter.this.bean )
                                               : null;
      }
    };
    return ListUtils.transform( this.propertynameToBeanPropertyAccessorMap.values(), elementTransformer );
  }
}
