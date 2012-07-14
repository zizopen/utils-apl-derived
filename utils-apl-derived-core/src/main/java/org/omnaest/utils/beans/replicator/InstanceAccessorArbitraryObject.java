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
package org.omnaest.utils.beans.replicator;

import java.util.Map;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;
import org.omnaest.utils.events.exception.ExceptionHandler;

import com.google.common.collect.ImmutableMap;

/**
 * @see InstanceAccessor
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class InstanceAccessorArbitraryObject implements InstanceAccessor
{
  /* ************************************************** Constants *************************************************** */
  private static final long                          serialVersionUID = 7255593284457472131L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Map<String, BeanPropertyAccessor<?>> propertyNameToBeanPropertyAccessorMap;
  private final Class<?>                             type;
  private final ExceptionHandler                     exceptionHandler;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  static final class PropertyForArbitraryObject implements PropertyAccessor
  {
    private static final long                  serialVersionUID   = 8531632621722929109L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final String                       propertyName;
    private final BeanPropertyAccessor<Object> beanPropertyAccessor;
    private final Object                       instance;
    private final ExceptionHandler             exceptionHandler;
    private final PropertyAccessType           propertyAccessType = PropertyAccessType.PROPERTY;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see PropertyForArbitraryObject
     * @param propertyName
     * @param beanPropertyAccessor
     * @param instance
     * @param exceptionHandler
     */
    public PropertyForArbitraryObject( String propertyName, BeanPropertyAccessor<Object> beanPropertyAccessor, Object instance,
                                       ExceptionHandler exceptionHandler )
    {
      this.propertyName = propertyName;
      this.beanPropertyAccessor = beanPropertyAccessor;
      this.instance = instance;
      this.exceptionHandler = exceptionHandler;
    }
    
    @Override
    public void setValue( Object value )
    {
      this.beanPropertyAccessor.setPropertyValue( this.instance, value, this.propertyAccessType, this.exceptionHandler );
    }
    
    @Override
    public Object getValue()
    {
      return this.beanPropertyAccessor.getPropertyValue( this.instance, this.propertyAccessType, this.exceptionHandler );
    }
    
    @Override
    public Class<?> getType()
    {
      return this.beanPropertyAccessor.getDeclaringPropertyType();
    }
    
    @Override
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see InstanceAccessorArbitraryObject
   * @param type
   * @param exceptionHandler
   */
  InstanceAccessorArbitraryObject( Class<?> type, ExceptionHandler exceptionHandler )
  {
    super();
    this.type = type;
    this.exceptionHandler = exceptionHandler;
    this.propertyNameToBeanPropertyAccessorMap = ImmutableMap.<String, BeanPropertyAccessor<?>> copyOf( BeanUtils.propertyNameToBeanPropertyAccessorMap( type ) );
  }
  
  @Override
  public PropertyAccessor getPropertyAccessor( final String propertyName, final Object instance )
  {
    @SuppressWarnings("unchecked")
    final BeanPropertyAccessor<Object> beanPropertyAccessor = (BeanPropertyAccessor<Object>) this.propertyNameToBeanPropertyAccessorMap.get( propertyName );
    return beanPropertyAccessor == null ? null : new PropertyForArbitraryObject( propertyName, beanPropertyAccessor, instance,
                                                                                 this.exceptionHandler );
  }
  
  @Override
  public Iterable<String> getPropertyNameIterable( Object instance )
  {
    return this.propertyNameToBeanPropertyAccessorMap.keySet();
  }
  
  @Override
  public Class<?> getType()
  {
    return this.type;
  }
  
  @Override
  public Map<String, Object> determineFactoryMetaInformation( Object instance )
  {
    return null;
  }
  
}
