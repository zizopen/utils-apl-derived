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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.proxy.BeanProperty;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.tuple.Tuple2;

/**
 * JavaBean property access object for {@link Method}s and {@link Field} of a special Java Bean type.<br>
 * <br>
 * The {@link BeanPropertyAccessor} is {@link Serializable} and immutable. Changing the {@link PropertyAccessType} e.g. does
 * return a new instance. See {@link #newBeanPropertyAccessorWithPropertyAccessType(PropertyAccessType)}.<br>
 * <br>
 * The default {@link PropertyAccessType} is on property level.
 * 
 * @see BeanUtils
 * @see BeanProperty
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public class BeanPropertyAccessor<B> implements Serializable
{
  
  /* ************************************************** Constants *************************************************** */
  private static final long               serialVersionUID           = 7631705587737553708L;
  private static final PropertyAccessType DEFAULT_PROPERTYACCESSTYPE = PropertyAccessType.PROPERTY;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected String                        propertyName;
  protected transient Field               field;
  protected transient Method              methodGetter;
  protected transient Method              methodSetter;
  protected Class<B>                      beanType;
  protected PropertyAccessType            propertyAccessType;
  
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
    this( field, methodGetter, methodSetter, propertyName, beanType, DEFAULT_PROPERTYACCESSTYPE );
  }
  
  /**
   * @see BeanPropertyAccessor
   * @param field
   * @param methodGetter
   * @param methodSetter
   * @param propertyName
   * @param beanType
   * @param propertyAccessType
   */
  public BeanPropertyAccessor( Field field, Method methodGetter, Method methodSetter, String propertyName, Class<B> beanType,
                               PropertyAccessType propertyAccessType )
  {
    //
    super();
    
    //
    this.propertyName = propertyName;
    this.field = field;
    this.methodGetter = methodGetter;
    this.methodSetter = methodSetter;
    this.beanType = beanType;
    this.propertyAccessType = propertyAccessType;
  }
  
  /**
   * This should only be used in combination of {@link #readResolve()}
   * 
   * @see BeanPropertyAccessor
   */
  @SuppressWarnings("unused")
  private BeanPropertyAccessor()
  {
    super();
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
   * Copies the addressed bean value of the given source instance using this {@link BeanPropertyAccessor} to another instance
   * using the given {@link BeanPropertyAccessor}
   * 
   * @param beanSource
   * @param beanPropertyAccessorTarget
   * @param beanTarget
   * @return true, if anything copied
   */
  public boolean copyPropertyValue( B beanSource, BeanPropertyAccessor<B> beanPropertyAccessorTarget, B beanTarget )
  {
    boolean retval = false;
    if ( beanPropertyAccessorTarget != null )
    {
      Object value = this.getPropertyValue( beanSource );
      beanPropertyAccessorTarget.setPropertyValue( beanTarget, value );
      retval = true;
    }
    return retval;
  }
  
  /**
   * Similar to {@link #getDeclaringPropertyType(PropertyAccessType)} but using all given {@link PropertyAccessType}s.
   * 
   * @return
   */
  public Class<?> getDeclaringPropertyType( PropertyAccessType... propertyAccessTypes )
  {
    Class<?> retval = null;
    if ( propertyAccessTypes != null )
    {
      for ( PropertyAccessType propertyAccessType : propertyAccessTypes )
      {
        retval = this.getDeclaringPropertyType( propertyAccessType );
        
        if ( retval != null )
        {
          break;
        }
      }
    }
    return retval;
  }
  
  /**
   * Similar to {@link #getDeclaringPropertyType(PropertyAccessType)} using the default {@link PropertyAccessType}
   * 
   * @return
   */
  public Class<?> getDeclaringPropertyType()
  {
    return this.getDeclaringPropertyType( this.propertyAccessType );
  }
  
  /**
   * Determines the {@link Class} type for the property. This is also known as the declaring return or field type not the type of
   * any actual object instance.<br>
   * <br>
   * Depending on the {@link PropertyAccessType} only the field type or the method return or parameter type is investigated, not
   * all at the same time.
   * 
   * @param propertyAccessType
   *          {@link PropertyAccessType}
   * @return
   */
  public Class<?> getDeclaringPropertyType( PropertyAccessType propertyAccessType )
  {
    //
    Class<?> retval = null;
    
    //
    if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
    {
      if ( this.field != null )
      {
        retval = this.field.getType();
      }
    }
    else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
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
   * Similar to {@link #getAnnotation(Class, PropertyAccessType)} using the default {@link PropertyAccessType}
   * 
   * @param annotationType
   *          {@link Class} of the {@link Annotation}
   * @return the {@link Annotation} instance or null
   */
  public <A extends Annotation> A getAnnotation( Class<A> annotationType )
  {
    return this.getAnnotation( annotationType, this.propertyAccessType );
  }
  
  /**
   * Returns the {@link Annotation} instance declared on the field or methods for the related property depending on the
   * {@link PropertyAccessType}.
   * 
   * @param annotationType
   *          {@link Class} of the {@link Annotation}
   * @param propertyAccessType
   *          {@link PropertyAccessType}
   * @return the {@link Annotation} instance or null
   */
  @SuppressWarnings("unchecked")
  public <A extends Annotation> A getAnnotation( Class<A> annotationType, PropertyAccessType propertyAccessType )
  {
    A retval = null;
    {
      //
      Map<Class<Annotation>, Annotation> annotationTypeToAnnotationMap = null;
      {
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          if ( this.field != null )
          {
            annotationTypeToAnnotationMap = ReflectionUtils.annotationTypeToAnnotationMap( this.field );
          }
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          if ( this.methodGetter != null )
          {
            annotationTypeToAnnotationMap = ReflectionUtils.annotationTypeToAnnotationMap( this.methodGetter );
          }
          else if ( this.methodSetter != null )
          {
            annotationTypeToAnnotationMap = ReflectionUtils.annotationTypeToAnnotationMap( this.methodSetter );
          }
        }
      }
      
      if ( annotationTypeToAnnotationMap != null )
      {
        retval = (A) annotationTypeToAnnotationMap.get( annotationType );
      }
    }
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
   * Similar to {@link #getPropertyValue(Object, ExceptionHandler, PropertyAccessType...)} with no {@link ExceptionHandler}
   * 
   * @param bean
   * @param propertyAccessTypes
   * @return
   */
  public Object getPropertyValue( B bean, PropertyAccessType... propertyAccessTypes )
  {
    ExceptionHandler exceptionHandler = null;
    return this.getPropertyValue( bean, exceptionHandler, propertyAccessTypes );
  }
  
  /**
   * Similar to {@link #getPropertyValue(Object, PropertyAccessType, ExceptionHandler)} using all the given
   * {@link PropertyAccessType}s
   * 
   * @param bean
   * @param exceptionHandler
   * @param propertyAccessTypes
   * @return
   */
  public Object getPropertyValue( B bean, ExceptionHandler exceptionHandler, PropertyAccessType... propertyAccessTypes )
  {
    Object retval = null;
    if ( propertyAccessTypes != null )
    {
      for ( PropertyAccessType propertyAccessType : propertyAccessTypes )
      {
        Tuple2<Object, Boolean> propertyValueAndSuccess = this.getPropertyValueAndSuccess( bean, propertyAccessType,
                                                                                           exceptionHandler );
        
        if ( propertyValueAndSuccess.getValueSecond() )
        {
          retval = propertyValueAndSuccess.getValueFirst();
          break;
        }
      }
    }
    return retval;
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
    return this.getPropertyValueAndSuccess( bean, propertyAccessType, exceptionHandler ).getValueFirst();
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
  public Tuple2<Object, Boolean> getPropertyValueAndSuccess( B bean,
                                                             PropertyAccessType propertyAccessType,
                                                             ExceptionHandler exceptionHandler )
  {
    //
    Object retval = null;
    boolean success = false;
    
    //
    final BeanPropertyAccessor<B> beanPropertyAccessor = this.propertyAccessType.equals( propertyAccessType ) ? this
                                                                                                             : this.newBeanPropertyAccessorWithPropertyAccessType( propertyAccessType );
    
    //
    if ( beanPropertyAccessor.isReadable() )
    {
      try
      {
        //
        AccessibleObject accessibleObject = null;
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          accessibleObject = beanPropertyAccessor.field;
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          accessibleObject = beanPropertyAccessor.methodGetter;
        }
        
        //
        boolean accessible = accessibleObject.isAccessible();
        if ( !accessible )
        {
          accessibleObject.setAccessible( true );
        }
        
        //
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          retval = beanPropertyAccessor.field.get( bean );
          success = true;
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          retval = beanPropertyAccessor.methodGetter.invoke( bean, new Object[] {} );
          success = true;
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
    return new Tuple2<Object, Boolean>( retval, success );
  }
  
  /**
   * Returns the property value for the underlying Java Bean property from the given Java Bean object.
   * 
   * @see #newBeanPropertyAccessorWithPropertyAccessType(PropertyAccessType)
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
   * Similar to {@link #setPropertyValue(Object, Object, PropertyAccessType)} using all given {@link PropertyAccessType}s
   * 
   * @param bean
   * @param value
   * @param propertyAccessTypes
   * @return
   */
  public boolean setPropertyValue( B bean, Object value, PropertyAccessType... propertyAccessTypes )
  {
    final ExceptionHandler exceptionHandler = null;
    return this.setPropertyValue( bean, value, exceptionHandler, propertyAccessTypes );
  }
  
  /**
   * Similar to {@link #setPropertyValue(Object, Object, PropertyAccessType, ExceptionHandler)} using all given
   * {@link PropertyAccessType}s
   * 
   * @param bean
   * @param value
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @param propertyAccessTypes
   * @return
   */
  public boolean setPropertyValue( B bean,
                                   Object value,
                                   ExceptionHandler exceptionHandler,
                                   PropertyAccessType... propertyAccessTypes )
  {
    boolean retval = false;
    if ( propertyAccessTypes != null )
    {
      for ( PropertyAccessType propertyAccessType : propertyAccessTypes )
      {
        retval = this.setPropertyValue( bean, value, propertyAccessType, exceptionHandler );
        if ( retval )
        {
          break;
        }
      }
    }
    return retval;
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
    final BeanPropertyAccessor<B> beanPropertyAccessor = this.propertyAccessType.equals( propertyAccessType ) ? this
                                                                                                             : this.newBeanPropertyAccessorWithPropertyAccessType( propertyAccessType );
    
    //
    if ( beanPropertyAccessor.isWritable() && bean != null )
    {
      try
      {
        //
        AccessibleObject accessibleObject = null;
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          accessibleObject = beanPropertyAccessor.field;
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          accessibleObject = beanPropertyAccessor.methodSetter;
        }
        
        //
        boolean accessible = accessibleObject.isAccessible();
        if ( !accessible )
        {
          accessibleObject.setAccessible( true );
        }
        
        //
        if ( PropertyAccessType.FIELD.equals( propertyAccessType ) )
        {
          //
          beanPropertyAccessor.field.set( bean, value );
        }
        else if ( PropertyAccessType.PROPERTY.equals( propertyAccessType ) )
        {
          //
          beanPropertyAccessor.methodSetter.invoke( bean, value );
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
    return BeanPropertyAccessor.DEFAULT_PROPERTYACCESSTYPE.equals( this.propertyAccessType );
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
   * Returns a new {@link BeanPropertyAccessor} which has the given {@link PropertyAccessType} set. This does not changed the
   * current instance.
   * 
   * @param propertyAccessType
   *          {@link PropertyAccessType}
   * @return this
   */
  public BeanPropertyAccessor<B> newBeanPropertyAccessorWithPropertyAccessType( PropertyAccessType propertyAccessType )
  {
    return new BeanPropertyAccessor<B>( this.field, this.methodGetter, this.methodSetter, this.propertyName, this.beanType,
                                        ObjectUtils.defaultIfNull( propertyAccessType, DEFAULT_PROPERTYACCESSTYPE ) );
  }
  
  public Object readResolve() throws ObjectStreamException
  {
    return BeanUtils.beanPropertyAccessor( this.beanType, this.propertyName )
                    .newBeanPropertyAccessorWithPropertyAccessType( this.propertyAccessType );
  }
  
}
