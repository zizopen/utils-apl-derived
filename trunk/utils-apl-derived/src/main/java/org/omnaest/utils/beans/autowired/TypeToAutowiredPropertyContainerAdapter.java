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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.MapToTypeAdapter;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;

/**
 * Adapter to create a {@link Map} view on an underlying Java Bean object. Modifications to the bean or the map will be reflected
 * to each other.
 * 
 * @see MapToTypeAdapter
 * @author Omnaest
 * @param <B>
 */
public class TypeToAutowiredPropertyContainerAdapter<B> implements AutowiredPropertyContainer
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
  public int put( Object value )
  {
    //
    int retval = 0;
    
    //
    if ( value != null )
    {
      //
      Class<? extends Object> objectClass = value.getClass();
      for ( Class<?> propertyType : this.propertyTypeToBeanPropertyAccessorSetMap.keySet() )
      {
        if ( propertyType.isAssignableFrom( objectClass ) )
        {
          //
          Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet = this.propertyTypeToBeanPropertyAccessorSetMap.get( propertyType );
          for ( BeanPropertyAccessor<B> beanPropertyAccessor : beanPropertyAccessorSet )
          {
            if ( beanPropertyAccessor.hasSetter() )
            {
              //
              beanPropertyAccessor.setPropertyValue( this.bean, value );
              
              //
              retval++;
            }
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean put( String propertyname, Object value )
  {
    //
    boolean retval = false;
    
    //
    if ( propertyname != null )
    {
      //
      BeanPropertyAccessor<B> beanPropertyAccessor = this.propertynameToBeanPropertyAccessorMap.get( propertyname );
      if ( beanPropertyAccessor != null && beanPropertyAccessor.hasSetter() )
      {
        //
        retval = beanPropertyAccessor.setPropertyValue( this.bean, value );
      }
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <O> Map<String, O> getPropertynameToValueMap( Class<O> clazz )
  {
    //
    Map<String, O> retmap = new HashMap<String, O>();
    
    //
    if ( clazz != null )
    {
      //
      for ( Class<?> propertyType : this.propertyTypeToBeanPropertyAccessorSetMap.keySet() )
      {
        if ( clazz.isAssignableFrom( propertyType ) )
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
  
  @Override
  public <O> Set<O> getValueSet( Class<O> clazz )
  {
    return new HashSet<O>( this.<O> getPropertynameToValueMap( clazz ).values() );
  }
  
  @Override
  public <O> O getValue( Class<O> clazz )
  {
    //
    O retval = null;
    
    //
    if ( clazz != null )
    {
      //
      Set<O> valueSet = this.getValueSet( clazz );
      if ( valueSet != null && valueSet.size() == 1 )
      {
        retval = valueSet.iterator().next();
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Iterator<Object> iterator()
  {
    return this.getValueSet( Object.class ).iterator();
  }
  
}
