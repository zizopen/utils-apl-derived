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
package org.omnaest.utils.beans.autowired;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;

/**
 * Adapter to create a {@link Map} view on an underlying Java Bean object. Modifications to the bean or the map will be reflected
 * to each other.
 * 
 * @see PropertynameMapToTypeAdapter
 * @author Omnaest
 * @param <B>
 */
public class TypeToAutowiredPropertyContainerAdapter<B> extends AutowiredContainerAbstract<Object> implements
                                                                                                  AutowiredPropertyContainer
{
  /* ********************************************** Constants ********************************************** */
  private static final long                             serialVersionUID                         = -4028601259144341930L;
  /* ********************************************** Variables ********************************************** */
  protected B                                           bean                                     = null;
  protected Map<String, BeanPropertyAccessor<B>>        propertynameToBeanPropertyAccessorMap    = null;
  protected Map<Class<?>, Set<BeanPropertyAccessor<B>>> propertyTypeToBeanPropertyAccessorSetMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Factory method to create a {@link Map} view on a given Java Bean object. Changes to this map will be translated to the Java
   * Bean object and vice versa.
   * 
   * @param <B>
   * @param bean
   * @return
   */
  public static <B> AutowiredPropertyContainer newInstance( B bean )
  {
    //
    AutowiredPropertyContainer retval = null;
    
    //
    if ( bean != null )
    {
      retval = new TypeToAutowiredPropertyContainerAdapter<B>( bean );
    }
    
    //
    return retval;
  }
  
  /**
   * @see #newInstance(Object)
   * @param bean
   */
  protected TypeToAutowiredPropertyContainerAdapter( B bean )
  {
    //
    super();
    
    //
    @SuppressWarnings("unchecked")
    Class<B> beanClass = (Class<B>) bean.getClass();
    
    //
    this.bean = bean;
    this.propertynameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass );
    this.propertyTypeToBeanPropertyAccessorSetMap = BeanUtils.propertyTypeToBeanPropertyAccessorSetMap( beanClass );
  }
  
  @Override
  public <O> AutowiredContainer<Object> put( O object, Class<? extends O>... types )
  {
    //
    if ( object != null && types.length > 0 )
    {
      //
      for ( Class<? extends Object> type : types )
      {
        if ( type != null )
        {
          for ( Class<?> propertyType : this.propertyTypeToBeanPropertyAccessorSetMap.keySet() )
          {
            if ( propertyType.isAssignableFrom( type ) )
            {
              //
              Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet = this.propertyTypeToBeanPropertyAccessorSetMap.get( propertyType );
              for ( BeanPropertyAccessor<B> beanPropertyAccessor : beanPropertyAccessorSet )
              {
                if ( beanPropertyAccessor.hasSetter() )
                {
                  //
                  beanPropertyAccessor.setPropertyValue( this.bean, object );
                }
              }
            }
          }
        }
      }
    }
    
    //
    return this;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public AutowiredContainer<Object> put( Object object )
  {
    this.put( object, ( object != null ) ? object.getClass() : null );
    return this;
  }
  
  @Override
  public AutowiredPropertyContainer put( String propertyname, Object value )
  {
    //
    if ( propertyname != null )
    {
      //
      BeanPropertyAccessor<B> beanPropertyAccessor = this.propertynameToBeanPropertyAccessorMap.get( propertyname );
      if ( beanPropertyAccessor != null && beanPropertyAccessor.hasSetter() )
      {
        //
        beanPropertyAccessor.setPropertyValue( this.bean, value );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public <O> Map<String, O> getPropertynameToValueMap( Class<O> type )
  {
    return this.getPropertynameToValueMapForArbitraryType( type );
  }
  
  @SuppressWarnings("unchecked")
  protected <O> Map<String, O> getPropertynameToValueMapForArbitraryType( Class<?> type )
  {
    //
    Map<String, O> retmap = new LinkedHashMap<String, O>();
    
    //
    if ( type != null )
    {
      //
      for ( Class<?> propertyType : this.propertyTypeToBeanPropertyAccessorSetMap.keySet() )
      {
        if ( type.isAssignableFrom( propertyType ) )
        {
          //
          Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet = this.propertyTypeToBeanPropertyAccessorSetMap.get( propertyType );
          for ( BeanPropertyAccessor<B> beanPropertyAccessor : beanPropertyAccessorSet )
          {
            //
            String propertyName = beanPropertyAccessor.getPropertyName();
            Object propertyValue = beanPropertyAccessor.getPropertyValue( this.bean );
            
            //
            retmap.put( propertyName, (O) propertyValue );
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <O> Set<O> getValueSet( Class<? extends O> type )
  {
    return new LinkedHashSet<O>( this.<O> getPropertynameToValueMap( (Class<O>) type ).values() );
  }
  
  @Override
  public Iterator<Object> iterator()
  {
    return this.getPropertynameToValueMapForArbitraryType( Object.class ).values().iterator();
  }
  
  @Override
  public AutowiredContainer<Object> removeAllHavingExactTypeOf( Class<? extends Object> type )
  {
    return this.put( null, type );
  }
  
  @Override
  public AutowiredContainer<Object> removeAllAssignableTo( Class<? extends Object> type )
  {
    return this.removeAllHavingExactTypeOf( type );
  }
  
}
