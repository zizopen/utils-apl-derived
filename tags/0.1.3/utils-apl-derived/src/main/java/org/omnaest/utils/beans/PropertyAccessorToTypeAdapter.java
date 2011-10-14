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

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator;

/**
 * The {@link PropertyAccessorToTypeAdapter} will provide an adapter
 * 
 * @author Omnaest
 */
public class PropertyAccessorToTypeAdapter<T>
{
  /* ********************************************** Variables ********************************************** */
  protected T                classAdapter     = null;
  protected Class<T>         clazz            = null;
  protected PropertyAccessor propertyAccessor = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Simple {@link PropertyAccessor} interface which reduces to a {@link #setValue(String, Object)} and {@link #getValue(String)}
   * method signature.
   * 
   * @author Omnaest
   */
  public static interface PropertyAccessor
  {
    /**
     * Sets the given value for the given property name.
     * 
     * @param propertyName
     * @param value
     */
    public void setValue( String propertyName, Object value );
    
    /**
     * Returns the value related to the given property name.
     * 
     * @param propertyName
     * @return
     */
    public Object getValue( String propertyName );
  }
  
  /**
   * A {@link MethodInterceptor} implementation special for this {@link PropertynameMapToTypeAdapter}
   */
  protected class ClassAdapterMethodInterceptor implements MethodInterceptor
  {
    @Override
    public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      try
      {
        BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
        if ( beanMethodInformation != null )
        {
          //
          final String referencedFieldName = beanMethodInformation.getReferencedFieldName();
          
          //          
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          boolean isPropertyAccessorNotNull = PropertyAccessorToTypeAdapter.this.propertyAccessor != null;
          
          //          
          if ( isPropertyAccessorNotNull )
          {
            //
            if ( isGetter )
            {
              //              
              retval = PropertyAccessorToTypeAdapter.this.propertyAccessor.getValue( referencedFieldName );
            }
            else if ( isSetter )
            {
              //
              String propertyName = referencedFieldName;
              Object value = args[0];
              
              PropertyAccessorToTypeAdapter.this.propertyAccessor.setValue( propertyName, value );
              
              //
              retval = Void.TYPE;
            }
          }
        }
      }
      catch ( Exception e )
      {
      }
      
      // 
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new {@link PropertyAccessorToTypeAdapter} instance for the given {@link Class} based on the given
   * {@link PropertyAccessor}
   * 
   * @param type
   * @param propertyAccessor
   * @param interfaces
   * @return
   */
  public static <T> T newInstance( Class<T> type, PropertyAccessor propertyAccessor, Class<?>... interfaces )
  {
    //    
    T retval = null;
    
    //
    if ( type != null && propertyAccessor != null )
    {
      //      
      PropertyAccessorToTypeAdapter<T> propertyAccessorToTypeAdapter = new PropertyAccessorToTypeAdapter<T>( type,
                                                                                                             propertyAccessor,
                                                                                                             interfaces );
      
      //
      retval = propertyAccessorToTypeAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * @see #newInstance(Class, PropertyAccessor, Class...)
   * @param type
   * @param propertyAccessor
   * @param interfaces
   */
  protected PropertyAccessorToTypeAdapter( Class<T> type, PropertyAccessor propertyAccessor, Class<?>... interfaces )
  {
    //
    super();
    
    //
    this.clazz = type;
    this.propertyAccessor = propertyAccessor;
    
    //
    this.initializeClassAdapter( type, interfaces );
  }
  
  /**
   * Creates the stub
   * 
   * @param type
   * @param interfaces
   * @param underlyingMapAware
   */
  protected void initializeClassAdapter( Class<? extends T> type, Class<?>... interfaces )
  {
    //
    try
    {
      //       
      MethodInterceptor methodInterceptor = new ClassAdapterMethodInterceptor();
      
      //
      this.classAdapter = StubCreator.newStubInstance( type, interfaces, methodInterceptor );
      
    }
    catch ( Exception e )
    {
    }
  }
}
