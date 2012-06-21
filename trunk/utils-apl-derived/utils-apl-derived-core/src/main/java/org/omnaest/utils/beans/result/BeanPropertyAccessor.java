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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.proxy.BeanProperty;

/**
 * JavaBean property access object for {@link Method}s and {@link Field} of a special Java Bean type.
 * 
 * @see BeanUtils
 * @see BeanProperty
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public class BeanPropertyAccessor<B>
{
  /* ********************************************** Variables ********************************************** */
  private final String       propertyName;
  private final Field        field;
  private final Method       methodGetter;
  private final Method       methodSetter;
  private final Class<B>     beanType;
  private PropertyAccessType propertyAccessType = PropertyAccessType.PROPERTY;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see BeanPropertyAccessor
   * @author Omnaest
   */
  public static enum PropertyAccessType
  {
    FIELD,
    PROPERTY
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see BeanPropertyAccessor
   * @param field
   * @param methodGetter
   * @param methodSetter
   * @param propertyName
   * @param beanType
   */
  public BeanPropertyAccessor( Field field, Method methodGetter, Method methodSetter, String propertyName, Class<B> beanType )
  {
    //
    super();
    
    //
    this.propertyName = propertyName;
    this.field = field;
    this.methodGetter = methodGetter;
    this.methodSetter = methodSetter;
    this.beanType = beanType;
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
   * Determines the {@link Class} type for the property. This is also known as the declaring return or field type not the type of
   * any actual object instance.
   * 
   * @return
   */
  public Class<?> getDeclaringPropertyType()
  {
    //
    Class<?> retval = null;
    
    //
    if ( PropertyAccessType.FIELD.equals( this.propertyAccessType ) )
    {
      if ( this.field != null )
      {
        retval = this.field.getType();
      }
    }
    else if ( PropertyAccessType.PROPERTY.equals( this.propertyAccessType ) )
    {
      if ( this.methodGetter != null )
      {
        retval = this.methodGetter.getReturnType();
      }
      else if ( this.methodSetter != null )
      {
        retval = this.methodSetter.getParameterTypes()[0];
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the property value for the underlying Java Bean property from the given Java Bean object.
   * 
   * @see PropertyAccessType
   * @param bean
   * @param propertyAccessType
   * @return value or null if no value could be resolved
   */
  public Object getPropertyValue( B bean, PropertyAccessType propertyAccessType )
  {
    ExceptionHandler exceptionHandler = null;
    return this.getPropertyValue( bean, propertyAccessType, exceptionHandler );
  }
  
  /**
   * Returns the property value for the underlying Java Bean property from the given Java Bean object.
   * 
   * @see PropertyAccessType
   * @param bean
   * @param propertyAccessType
   * @param exceptionHandler
   * @return value or null if no value could be resolved
   */
  public Object getPropertyValue( B bean, PropertyAccessType propertyAccessType, ExceptionHandler exceptionHandler )
  {
    //
    Object retval = null;
    
    //
    if ( this.isReadable() )
    {
      try
      {
        //
        AccessibleObject accessibleObject = null;
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          accessibleObject = this.field;
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          accessibleObject = this.methodGetter;
        }
        
        //
        boolean accessible = accessibleObject.isAccessible();
        accessibleObject.setAccessible( true );
        try
        {
          //
          if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
          {
            retval = this.field.get( bean );
          }
          else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
          {
            retval = this.methodGetter.invoke( bean, new Object[] {} );
          }
        }
        finally
        {
          accessibleObject.setAccessible( accessible );
        }
      }
      catch ( Exception e )
      {
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the property value for the underlying Java Bean property from the given Java Bean object.
   * 
   * @see #setPropertyAccessType(PropertyAccessType)
   * @param bean
   * @return value or null if no value could be resolved
   */
  public Object getPropertyValue( B bean )
  {
    return this.getPropertyValue( bean, this.propertyAccessType );
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
    return this.setPropertyValue( bean, value, this.propertyAccessType );
  }
  
  /**
   * Sets the property value for the underlying Java Bean property for the given Java Bean object using the given
   * {@link PropertyAccessType}.
   * 
   * @param bean
   * @param value
   * @param propertyAccessType
   * @return true if no error occurs
   */
  public boolean setPropertyValue( B bean, Object value, PropertyAccessType propertyAccessType )
  {
    final ExceptionHandler exceptionHandler = null;
    return this.setPropertyValue( bean, value, propertyAccessType, exceptionHandler );
  }
  
  /**
   * Sets the property value for the underlying Java Bean property for the given Java Bean object using the given
   * {@link PropertyAccessType}.
   * 
   * @param bean
   * @param value
   * @param propertyAccessType
   * @param exceptionHandler
   * @return true if no error occurs
   */
  public boolean setPropertyValue( B bean, Object value, PropertyAccessType propertyAccessType, ExceptionHandler exceptionHandler )
  {
    //
    boolean retval = false;
    
    //
    if ( this.isWritable() && bean != null )
    {
      try
      {
        //
        AccessibleObject accessibleObject = null;
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          accessibleObject = this.field;
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          accessibleObject = this.methodSetter;
        }
        
        //
        boolean accessible = accessibleObject.isAccessible();
        accessibleObject.setAccessible( true );
        try
        {
          if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
          {
            //
            this.field.set( bean, value );
          }
          else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
          {
            //
            this.methodSetter.invoke( bean, value );
          }
        }
        finally
        {
          accessibleObject.setAccessible( accessible );
        }
        
        //
        retval = true;
      }
      catch ( Exception e )
      {
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
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
   * Returns true if the {@link PropertyAccessType} is field and the class definition has a field or if {@link PropertyAccessType}
   * is property and the class declares a getter and a setter.
   * 
   * @return
   */
  public boolean isReadAndWritable()
  {
    return ( this.isPropertyAccessingByGetterAndSetter() && this.hasGetterAndSetter() )
           || ( this.isPropertyAccessingByField() && this.hasField() );
  }
  
  /**
   * Returns true if the {@link PropertyAccessType} is field and the class definition has a field or if {@link PropertyAccessType}
   * is property and the class declares a getter.
   * 
   * @return
   */
  public boolean isReadable()
  {
    return ( this.isPropertyAccessingByGetterAndSetter() && this.hasGetter() )
           || ( this.isPropertyAccessingByField() && this.hasField() );
  }
  
  /**
   * Returns true if the {@link PropertyAccessType} is field and the class definition has a field or if {@link PropertyAccessType}
   * is property and the class declares a setter.
   * 
   * @return
   */
  public boolean isWritable()
  {
    return ( this.isPropertyAccessingByGetterAndSetter() && this.hasSetter() )
           || ( this.isPropertyAccessingByField() && this.hasField() );
  }
  
  /**
   * @return
   */
  private boolean isPropertyAccessingByField()
  {
    return PropertyAccessType.FIELD.equals( this.propertyAccessType );
  }
  
  /**
   * @return
   */
  private boolean isPropertyAccessingByGetterAndSetter()
  {
    return PropertyAccessType.PROPERTY.equals( this.propertyAccessType );
  }
  
  /**
   * Returns true if an underlying field is available
   * 
   * @return
   */
  public boolean hasField()
  {
    return this.field != null;
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
           && beanPropertyAccessorA.beanType != null && beanPropertyAccessorA.beanType.equals( beanPropertyAccessorB.beanType ) )
      {
        //
        String propertyname = beanPropertyAccessorA.propertyName != null ? beanPropertyAccessorA.propertyName
                                                                        : beanPropertyAccessorB.propertyName;
        Class<B> beanClass = beanPropertyAccessorA.beanType != null ? beanPropertyAccessorA.beanType
                                                                   : beanPropertyAccessorB.beanType;
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
  
  /**
   * Returns the class object of accepted bean type
   * 
   * @return
   */
  public Class<B> getBeanClass()
  {
    return this.beanType;
  }
  
  /**
   * Returns the name of the property
   * 
   * @return
   */
  public String getPropertyName()
  {
    return this.propertyName;
  }
  
  /**
   * Returns the underlying {@link Field}
   * 
   * @return
   */
  public Field getField()
  {
    return this.field;
  }
  
  /**
   * Returns the getter {@link Method}
   * 
   * @return
   */
  public Method getMethodGetter()
  {
    return this.methodGetter;
  }
  
  /**
   * Returns the setter {@link Method}
   * 
   * @return
   */
  public Method getMethodSetter()
  {
    return this.methodSetter;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "BeanPropertyAccessor [propertyName=" );
    builder.append( this.propertyName );
    builder.append( ", field=" );
    builder.append( this.field );
    builder.append( ", methodGetter=" );
    builder.append( this.methodGetter );
    builder.append( ", methodSetter=" );
    builder.append( this.methodSetter );
    builder.append( ", beanClass=" );
    builder.append( this.beanType );
    builder.append( ", propertyAccessType=" );
    builder.append( this.propertyAccessType );
    builder.append( "]" );
    return builder.toString();
  }
  
  /**
   * Sets the {@link PropertyAccessType}
   * 
   * @param propertyAccessType
   */
  public void setPropertyAccessType( PropertyAccessType propertyAccessType )
  {
    if ( propertyAccessType != null )
    {
      this.propertyAccessType = propertyAccessType;
    }
  }
  
}
