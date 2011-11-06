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
package org.omnaest.utils.beans.mapconverter;

import java.util.Map;

import org.omnaest.utils.beans.mapconverter.internal.BeanToNestedMapMarshaller;
import org.omnaest.utils.beans.mapconverter.internal.BeanToNestedMapUnMarshaller;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;

/**
 * A {@link BeanToNestedMapConverter} marshalls a given JavaBean into a {@link Map} or unmarshalls a given {@link Map} into a
 * JavaBean. This process is transitive and allows to translate arbitrary object graphs into nested {@link Map}s. <br>
 * <br>
 * With {@link #setPropertyAccessType(PropertyAccessType)} it can be declared in which way the {@link BeanToNestedMapConverter}
 * should access given JavaBeans. With the default value of {@link PropertyAccessType#PROPERTY} the
 * {@link BeanToNestedMapConverter} will only use getters and setters whereby with {@link PropertyAccessType#FIELD} the fields of
 * the JavaBeans are accessed directly ignoring any getters or setters.
 * 
 * @author Omnaest
 */
public class BeanToNestedMapConverter<B>
{
  /* ********************************************** Variables ********************************************** */
  private BeanToNestedMapMarshaller      beanToNestedMapMarshaller   = null;
  private BeanToNestedMapUnMarshaller<B> beanToNestedMapUnMarshaller = null;
  private BeanConversionFilter           beanConversionFilter        = null;
  private Class<? extends B>             beanClass                   = null;
  private PropertyAccessType             propertyAccessType          = PropertyAccessType.PROPERTY;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * A {@link BeanConversionFilter} will decide which JavaBean should be converted into a {@link Map} and which should be put
   * directly into the {@link Map} as {@link Object}
   * 
   * @author Omnaest
   * @param <B>
   */
  public static interface BeanConversionFilter
  {
    /**
     * Returns true, if a given JavaBean should be converted to a {@link Map}
     * 
     * @param declaringType
     *          : type which is declared by the referencing property (this can be for example an interface or any supertype of the
     *          actual incoming bean object)
     * @param bean
     *          : the actual bean to be converted
     * @return
     */
    public boolean hasBeanToBeConverted( Class<?> declaringType, Object bean );
    
  }
  
  /**
   * Excludes all object instances which are primitives, wrappers for primitives or {@link String}
   * 
   * @see BeanConversionFilter
   * @author Omnaest
   * @param <B>
   */
  public static class BeanConversionFilterExcludingPrimitiveAndString implements BeanConversionFilter
  {
    @Override
    public boolean hasBeanToBeConverted( Class<?> declaringType, Object bean )
    {
      //
      return !( bean instanceof Byte || bean instanceof Short || bean instanceof Integer || bean instanceof Long
                || bean instanceof Float || bean instanceof Double || bean instanceof Boolean || bean instanceof String );
    }
    
  }
  
  /**
   * Excludes all object instances which are assigned to a declared class which is an type from the "java.*" package space
   * 
   * @see BeanConversionFilter
   * @author Omnaest
   * @param <B>
   */
  public static class BeanConversionFilterExcludingDeclaringJavaBaseClass implements BeanConversionFilter
  {
    @Override
    public boolean hasBeanToBeConverted( Class<?> declaringType, Object bean )
    {
      return bean != null && !bean.getClass().getCanonicalName().startsWith( "java" );
    }
    
  }
  
  /**
   * Excludes all object instances which are an instance from the "java.*" package space
   * 
   * @see BeanConversionFilter
   * @author Omnaest
   * @param <B>
   */
  public static class BeanConversionFilterExcludingJavaBaseClass implements BeanConversionFilter
  {
    @Override
    public boolean hasBeanToBeConverted( Class<?> declaringType, Object bean )
    {
      return bean != null && !bean.getClass().getCanonicalName().startsWith( "java" );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param beanClass
   */
  public BeanToNestedMapConverter( Class<? extends B> beanClass )
  {
    super();
    this.beanClass = beanClass;
    this.beanConversionFilter = new BeanConversionFilterExcludingJavaBaseClass();
    this.beanToNestedMapMarshaller = new BeanToNestedMapMarshaller( this.beanConversionFilter );
    this.beanToNestedMapUnMarshaller = new BeanToNestedMapUnMarshaller<B>( this.beanClass );
  }
  
  /**
   * Marshalls a given JavaBean into a nested {@link Map}
   * 
   * @param bean
   * @return
   */
  public Map<String, Object> marshal( B bean )
  {
    return this.beanToNestedMapMarshaller.marshal( bean, this.propertyAccessType );
  }
  
  /**
   * Unmarshalls a given nested {@link Map} in the respective JavaBean
   * 
   * @param map
   * @return
   */
  public B unmarshal( Map<String, Object> map )
  {
    return this.beanToNestedMapUnMarshaller.unmarshal( map, this.propertyAccessType );
  }
  
  /**
   * @param beanConversionFilter
   */
  public void setBeanConversionFilter( BeanConversionFilter beanConversionFilter )
  {
    this.beanConversionFilter = beanConversionFilter;
  }
  
  public void setPropertyAccessType( PropertyAccessType propertyAccessType )
  {
    this.propertyAccessType = propertyAccessType;
  }
  
}
