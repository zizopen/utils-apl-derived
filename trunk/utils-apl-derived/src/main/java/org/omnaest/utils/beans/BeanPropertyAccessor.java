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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * Java Bean property access object for {@link Method}s and {@link Field} of a special Java Bean type.
 * 
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public class BeanPropertyAccessor<B>
{
  /* ********************************************** Variables ********************************************** */
  protected String   propertyname = null;
  protected Field    field        = null;
  protected Method   methodGetter = null;
  protected Method   methodSetter = null;
  protected Class<B> beanClass    = null;
  
  /* ********************************************** Methods ********************************************** */
  @SuppressWarnings("unchecked")
  protected BeanPropertyAccessor( Field field, Method methodGetter, Method methodSetter, String propertyname )
  {
    //
    super();
    
    //
    this.propertyname = propertyname;
    this.field = field;
    this.methodGetter = methodGetter;
    this.methodSetter = methodSetter;
    
    //
    TypeVariable<?> typeVariable = this.getClass().getTypeParameters()[0];
    this.beanClass = (Class<B>) typeVariable.getBounds()[0];
  }
  
  /**
   * Copies the value of one Java Bean for the underlying Java Bean property of this {@link BeanPropertyAccessor} to another Java
   * Bean.
   * 
   * @param beanSource
   * @param beanDestination
   * @return true, if no error occurs
   */
  public boolean copyFieldValue( B beanSource, B beanDestination )
  {
    //
    boolean retval = false;
    
    //
    if ( beanDestination != null && beanSource != null && this.hasGetterAndSetter() )
    {
      //
      try
      {
        //
        this.methodSetter.invoke( beanDestination, this.methodGetter.invoke( beanSource, new Object[] {} ) );
        
        //
        retval = true;
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the property value for the underlying Java Bean property from the given Java Bean object.
   * 
   * @param bean
   * @return value or null if no value could be resolved
   */
  public Object getPropertyValue( B bean )
  {
    //
    Object retval = null;
    
    //
    if ( this.hasGetter() )
    {
      try
      {
        retval = this.methodGetter.invoke( bean, new Object[] {} );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Sets the property value for the underlying Java Bean property for the given Java Bean object.
   * 
   * @param bean
   * @param value
   * @return true if no error occurs
   */
  public boolean setPropertyValue( B bean, Object value )
  {
    //
    boolean retval = false;
    
    //
    if ( this.hasSetter() )
    {
      try
      {
        //
        this.methodSetter.invoke( bean, value );
        
        //
        retval = true;
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true, if the underlying {@link Field} has at least a getter or a setter method or both available.
   * 
   * @return
   */
  public boolean hasGetterOrSetter()
  {
    return this.hasGetter() || this.hasSetter();
  }
  
  /**
   * Returns true, if the underlying {@link Field} has a getter and a setter method.
   * 
   * @return
   */
  public boolean hasGetterAndSetter()
  {
    return this.hasGetter() && this.hasSetter();
  }
  
  /**
   * Returns true, if the method has no parameters but a return type.
   * 
   * @return
   */
  public boolean hasGetter()
  {
    return this.methodGetter != null;
  }
  
  /**
   * Is true, if a method has only one parameter and begins with "set". A return type is optional.
   * 
   * @return
   */
  public boolean hasSetter()
  {
    return this.methodSetter != null;
  }
  
  /**
   * Returns the field name referenced by the underlying {@link Field}.
   * 
   * @return
   */
  public String getReferencedFieldName()
  {
    return this.field != null ? this.field.getName() : null;
  }
  
  protected Method getMethod()
  {
    return this.methodGetter;
  }
  
  public Class<?> getBeanClass()
  {
    return this.beanClass;
  }
  
  public String getPropertyname()
  {
    return this.propertyname;
  }
  
  public Field getField()
  {
    return this.field;
  }
  
  public Method getMethodGetter()
  {
    return this.methodGetter;
  }
  
  public Method getMethodSetter()
  {
    return this.methodSetter;
  }
  
}
