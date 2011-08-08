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

/**
 * A {@link BeanToNestedMapConverter} marshalls a given JavaBean into a {@link Map} or unmarshalls a given {@link Map} into a
 * JavaBean. This process is transitiv which allows to translate arbitrary object graphs into nested {@link Map}s.
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
     * @param bean
     * @return
     */
    public boolean hasBeanToBeConverted( Object bean );
    
  }
  
  /**
   * @see BeanConversionFilter
   * @author Omnaest
   * @param <B>
   */
  public static class BeanConversionFilterPrimitiveAndString implements BeanConversionFilter
  {
    @Override
    public boolean hasBeanToBeConverted( Object bean )
    {
      //
      return !( bean instanceof Byte || bean instanceof Short || bean instanceof Integer || bean instanceof Long
                || bean instanceof Float || bean instanceof Double || bean instanceof Boolean || bean instanceof String );
    }
    
  }
  
  /**
   * @see BeanConversionFilter
   * @author Omnaest
   * @param <B>
   */
  public static class BeanConversionFilterJavaBaseClass implements BeanConversionFilter
  {
    @Override
    public boolean hasBeanToBeConverted( Object bean )
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
    this.beanConversionFilter = new BeanConversionFilterJavaBaseClass();
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
    return this.beanToNestedMapMarshaller.marshal( bean );
  }
  
  /**
   * Unmarshalls a given nested {@link Map} in the respective JavaBean
   * 
   * @param map
   * @return
   */
  public B unmarshal( Map<String, Object> map )
  {
    return this.beanToNestedMapUnMarshaller.unmarshal( map );
  }
  
  /**
   * @param beanConversionFilter
   */
  public void setBeanConversionFilter( BeanConversionFilter beanConversionFilter )
  {
    this.beanConversionFilter = beanConversionFilter;
  }
  
}
