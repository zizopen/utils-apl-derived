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
package org.omnaest.utils.spring.logging;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.AssertLogger;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;
import org.omnaest.utils.structure.map.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * The {@link LoggerInjectorBeanPostProcessor} is a Spring based {@link BeanPostProcessor} which injects {@link org.slf4j.Logger}
 * instances into beans. <br>
 * <br>
 * Based on {@link #setInjectingOnLoggerAnnotation(boolean)} and {@link #setInjectingOnLoggerType(boolean)} this
 * {@link BeanPostProcessor} will inject instances in fields or methods which have the {@link LoggerInject} annotation set on top
 * or any fields or methods which have the field or parameter type of the {@link org.slf4j.Logger} interface. <br>
 * <br>
 * As default any existing instances will not be overwritten. To change that behavior use
 * {@link #setOverwritingExistingInstances(boolean)}. <br>
 * <br>
 * Example:
 * 
 * <pre>
 * &#064;LoggerInject
 * private Logger logger;
 * </pre>
 * 
 * @author Omnaest
 */
public class LoggerInjectorBeanPostProcessor implements BeanPostProcessor, Serializable
{
  private static final long              serialVersionUID               = -7223936740019303970L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private boolean                        isInjectingOnLoggerType        = false;
  private boolean                        isInjectingOnLoggerAnnotation  = true;
  private boolean                        isOverwritingExistingInstances = false;
  
  protected Map<Class<?>, LoggerFactory> loggerTypeToLoggerFactoryMap   = MapUtils.builder()
                                                                                  .<Class<?>, LoggerFactory> put( org.slf4j.Logger.class,
                                                                                                                  new LoggerFactory()
                                                                                                                  {
                                                                                                                    private static final long serialVersionUID = -3371535263923949458L;
                                                                                                                    
                                                                                                                    @Override
                                                                                                                    public Object newInstance( Class<?> clazz )
                                                                                                                    {
                                                                                                                      return org.slf4j.LoggerFactory.getLogger( clazz );
                                                                                                                    }
                                                                                                                  } )
                                                                                  .put( AssertLogger.class, new LoggerFactory()
                                                                                  {
                                                                                    private static final long serialVersionUID = -3371535263923949458L;
                                                                                    
                                                                                    @Override
                                                                                    public Object newInstance( Class<?> clazz )
                                                                                    {
                                                                                      return new AssertLogger( clazz );
                                                                                    }
                                                                                  } )
                                                                                  .buildAs()
                                                                                  .linkedHashMap();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @Documented
  public static @interface LoggerInject
  {
  }
  
  protected static interface LoggerFactory extends FactoryParameterized<Object, Class<?>>, Serializable
  {
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Override
  public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException
  {
    return bean;
  }
  
  @Override
  public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException
  {
    if ( bean != null )
    {
      @SuppressWarnings("unchecked")
      final Class<Object> beanType = (Class<Object>) bean.getClass();
      
      Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSet = BeanUtils.beanPropertyAccessorSet( beanType );
      for ( BeanPropertyAccessor<Object> beanPropertyAccessor : beanPropertyAccessorSet )
      {
        boolean inject = false;
        Class<?> loggerType = beanPropertyAccessor.getDeclaringPropertyType( PropertyAccessType.PROPERTY,
                                                                             PropertyAccessType.FIELD );
        
        if ( !this.isOverwritingExistingInstances )
        {
          Object instance = beanPropertyAccessor.getPropertyValue( bean, PropertyAccessType.PROPERTY, PropertyAccessType.FIELD );
          if ( instance != null )
          {
            continue;
          }
        }
        
        if ( this.isInjectingOnLoggerType && !inject )
        {
          if ( loggerType != null )
          {
            inject = ObjectUtils.isAnyTypeAssignableFromType( this.loggerTypeToLoggerFactoryMap.keySet(), loggerType );
          }
        }
        
        if ( this.isInjectingOnLoggerAnnotation && !inject )
        {
          Annotation annotation = ObjectUtils.defaultIfNull( beanPropertyAccessor.getAnnotation( LoggerInject.class,
                                                                                                 PropertyAccessType.PROPERTY ),
                                                             beanPropertyAccessor.getAnnotation( LoggerInject.class,
                                                                                                 PropertyAccessType.FIELD ) );
          
          inject = annotation != null;
        }
        
        if ( inject )
        {
          beanPropertyAccessor.setPropertyValue( bean, newLoggerInstance( loggerType, beanType ), PropertyAccessType.PROPERTY,
                                                 PropertyAccessType.FIELD );
        }
      }
    }
    return bean;
  }
  
  protected Object newLoggerInstance( final Class<?> loggerType, final Class<Object> beanType )
  {
    return this.loggerTypeToLoggerFactoryMap.get( loggerType ).newInstance( beanType );
  }
  
  public void setInjectingOnLoggerType( boolean isInjectingOnLoggerType )
  {
    this.isInjectingOnLoggerType = isInjectingOnLoggerType;
  }
  
  public void setInjectingOnLoggerAnnotation( boolean isInjectingOnLoggerAnnotation )
  {
    this.isInjectingOnLoggerAnnotation = isInjectingOnLoggerAnnotation;
  }
  
  public void setOverwritingExistingInstances( boolean overwritingExistingInstances )
  {
    this.isOverwritingExistingInstances = overwritingExistingInstances;
  }
}
