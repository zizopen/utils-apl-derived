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
package org.omnaest.utils.beans.result;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

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
  protected String   propertyName = null;
  protected Field    field        = null;
  protected Method   methodGetter = null;
  protected Method   methodSetter = null;
  protected Class<B> beanClass    = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public BeanPropertyAccessor( Field field, Method methodGetter, Method methodSetter, String propertyName, Class<B> beanClass )
  {
    //
    super();
    
    //
    this.propertyName = propertyName;
    this.field = field;
    this.methodGetter = methodGetter;
    this.methodSetter = methodSetter;
    this.beanClass = beanClass;
  }
  
  /**
   * Copies the value of one Java Bean for the underlying Java Bean property of this {@link BeanPropertyAccessor} to another Java
   * Bean.
   * 
   * @param beanSource
   * @param beanDestination
   * @return true, if no error occurs
   */
  public boolean copyPropertyValue( B beanSource, B beanDestination )
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
   * Determines the {@link Class} type for the property.
   * 
   * @return
   */
  public Class<?> determinePropertyType()
  {
    //
    Class<?> retval = null;
    
    //
    if ( this.field != null )
    {
      retval = this.field.getType();
    }
    else if ( this.methodGetter != null )
    {
      retval = this.methodGetter.getReturnType();
    }
    else if ( this.methodSetter != null )
    {
      retval = this.methodSetter.getParameterTypes()[0];
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
        e.printStackTrace();
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
    if ( this.hasSetter() && bean != null )
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
        e.printStackTrace();
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
   * Merges two {@link BeanPropertyAccessor} instances into one if the underlying {@link Class} and property are equal.
   * 
   * @param <B>
   * @param beanPropertyAccessorA
   * @param beanPropertyAccessorB
   * @return
   */
  public static <B> BeanPropertyAccessor<B> merge( BeanPropertyAccessor<B> beanPropertyAccessorA,
                                                   BeanPropertyAccessor<B> beanPropertyAccessorB )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanPropertyAccessorA != null && beanPropertyAccessorB != null )
    {
      //
      if ( StringUtils.equals( beanPropertyAccessorA.propertyName, beanPropertyAccessorB.propertyName )
           && beanPropertyAccessorA.beanClass != null && beanPropertyAccessorA.beanClass.equals( beanPropertyAccessorB.beanClass ) )
      {
        //
        String propertyname = beanPropertyAccessorA.propertyName != null ? beanPropertyAccessorA.propertyName
                                                                        : beanPropertyAccessorB.propertyName;
        Class<B> beanClass = beanPropertyAccessorA.beanClass != null ? beanPropertyAccessorA.beanClass
                                                                    : beanPropertyAccessorB.beanClass;
        Field field = beanPropertyAccessorA.field != null ? beanPropertyAccessorA.field : beanPropertyAccessorB.field;
        Method methodGetter = beanPropertyAccessorA.methodGetter != null ? beanPropertyAccessorA.methodGetter
                                                                        : beanPropertyAccessorB.methodGetter;
        Method methodSetter = beanPropertyAccessorA.methodSetter != null ? beanPropertyAccessorA.methodSetter
                                                                        : beanPropertyAccessorB.methodSetter;
        
        //
        retval = new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, propertyname, beanClass );
      }
    }
    
    //
    return retval;
  }
  
  protected Method getMethod()
  {
    return this.methodGetter;
  }
  
  public Class<?> getBeanClass()
  {
    return this.beanClass;
  }
  
  /**
   * @return
   */
  public String getPropertyName()
  {
    return this.propertyName;
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
  
  @Override
  public String toString()
  {
    return "BeanPropertyAccessor [propertyname=" + this.propertyName + ", beanClass=" + this.beanClass + "]";
  }
  
}
