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
package org.omnaest.utils.beans.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter.Configuration.RegardedAnnotationScope;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecorator;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecoratorAdapter;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecoratorPropertyAccessOption;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecoratorPropertyNameTemplate;
import org.omnaest.utils.beans.autowired.AutowiredContainer;
import org.omnaest.utils.beans.autowired.ClassMapToAutowiredContainerAdapter;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecorator;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.springframework.util.Assert;

/**
 * The {@link SourcePropertyAccessorToTypeAdapter} will provide an adapter from a {@link SourcePropertyAccessor} to any given
 * {@link Class} type.
 * 
 * @see Configuration
 * @author Omnaest
 */
public class SourcePropertyAccessorToTypeAdapter<T>
{
  /* ********************************************** Variables ********************************************** */
  protected T                                  classAdapter                               = null;
  protected Class<T>                           type                                       = null;
  protected SourcePropertyAccessor             sourcePropertyAccessor                     = null;
  protected List<Annotation>                   declaredAnnotationListOfType               = null;
  protected Map<Method, Set<Annotation>>       declaredMethodToAnnotationSetMap           = null;
  protected Map<String, Set<Annotation>>       propertyNameToBeanPropertyAnnotationSetMap = null;
  protected Map<Method, Class<?>>              methodToReturnTypeMap                      = null;
  protected Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap       = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  protected class ClassAdapterMethodInvocationHandler implements MethodInvocationHandler
  {
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      //
      final Method method = methodCallCapture.getMethod();
      final String methodName = methodCallCapture.getMethodName();
      final Object[] args = methodCallCapture.getArguments();
      
      //
      Object retval = null;
      
      //
      try
      {
        //        
        BeanMethodInformation beanMethodInformation = SourcePropertyAccessorToTypeAdapter.this.methodNameToBeanMethodInformationMap.get( methodName );
        if ( beanMethodInformation != null )
        {
          //
          final Class<?> returnType = SourcePropertyAccessorToTypeAdapter.this.methodToReturnTypeMap.get( method );
          final String referencedFieldName = beanMethodInformation.getReferencedFieldName();
          
          //          
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isGetterWithAddtionalArguments = beanMethodInformation.isGetterWithAdditionalArguments() && args.length >= 1;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          boolean isSetterWithAdditionalArguments = beanMethodInformation.isSetterWithAdditionalArguments() && args.length >= 2;
          boolean isPropertyAccessorNotNull = SourcePropertyAccessorToTypeAdapter.this.sourcePropertyAccessor != null;
          
          //          
          if ( isPropertyAccessorNotNull )
          {
            //
            AutowiredContainer<Annotation> propertyAnnotationAutowiredContainer = ClassMapToAutowiredContainerAdapter.newInstance( new LinkedHashMap<Class<? extends Annotation>, Annotation>() );
            AutowiredContainer<Annotation> classAnnotationAutowiredContainer = ClassMapToAutowiredContainerAdapter.newInstance( new LinkedHashMap<Class<? extends Annotation>, Annotation>() );
            
            //
            classAnnotationAutowiredContainer.putAll( SourcePropertyAccessorToTypeAdapter.this.declaredAnnotationListOfType );
            propertyAnnotationAutowiredContainer.putAll( SourcePropertyAccessorToTypeAdapter.this.propertyNameToBeanPropertyAnnotationSetMap.get( referencedFieldName ) );
            propertyAnnotationAutowiredContainer.putAll( SourcePropertyAccessorToTypeAdapter.this.declaredMethodToAnnotationSetMap.get( method ) );
            
            //
            if ( isGetter || isGetterWithAddtionalArguments )
            {
              //
              Object[] additionalArguments = isGetterWithAddtionalArguments ? Arrays.copyOf( args, args.length ) : new Object[0];
              PropertyMetaInformation propertyMetaInformation = new PropertyMetaInformation(
                                                                                             additionalArguments,
                                                                                             propertyAnnotationAutowiredContainer,
                                                                                             classAnnotationAutowiredContainer );
              
              //              
              retval = SourcePropertyAccessorToTypeAdapter.this.sourcePropertyAccessor.getValue( referencedFieldName, returnType,
                                                                                                 propertyMetaInformation );
            }
            else if ( isSetter || isSetterWithAdditionalArguments )
            {
              //
              String propertyName = referencedFieldName;
              Object value = args[0];
              
              //
              Object[] additionalArguments = isSetterWithAdditionalArguments ? Arrays.copyOfRange( args, 1, args.length )
                                                                            : new Object[0];
              PropertyMetaInformation propertyMetaInformation = new PropertyMetaInformation(
                                                                                             additionalArguments,
                                                                                             propertyAnnotationAutowiredContainer,
                                                                                             classAnnotationAutowiredContainer );
              
              SourcePropertyAccessorToTypeAdapter.this.sourcePropertyAccessor.setValue( propertyName, value,
                                                                                        propertyMetaInformation );
              
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
  
  /**
   * The {@link Configuration} of a {@link SourcePropertyAccessorToTypeAdapter} includes following settings:<br>
   * <br>
   * <ul>
   * <li>{@link #setPropertyAccessOption(PropertyAccessOption)}</li>
   * <li>{@link #setRegardingAdapterAnnotation(boolean)}</li>
   * <li>{@link #setRegardingPropertyNameTemplate(boolean)}</li>
   * </ul>
   * <br>
   * <br>
   * If {@link #setPropertyAccessOption(PropertyAccessOption)} is set to another {@link PropertyAccessOption} it is possible to
   * access the underlying {@link Map} keys e.g. with lowercased keys instead of the case sensitive property names. <br>
   * <br>
   * If {@link #setRegardingAdapterAnnotation(boolean)} is set to true, the {@link Converter} are evaluated and the respective
   * {@link ElementConverter} be called to translate the setter parameter or the getter return value. <br>
   * <br>
   * If {@link #setRegardingPropertyNameTemplate(boolean)} is set to true, all {@link PropertyNameTemplate} {@link Annotation}s
   * are considered.<br>
   * 
   * @see Converter
   * @see PropertyNameTemplate
   * @see SourcePropertyAccessorToTypeAdapter
   * @author Omnaest
   */
  public static class Configuration
  {
    /* ********************************************** Variables ********************************************** */
    private Class<?>[]                         interfaces                        = null;
    private MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators = null;
    private SourcePropertyAccessorDecorator[]  sourcePropertyAccessorDecorators  = null;
    private PropertyAccessOption               propertyAccessOption              = PropertyAccessOption.PROPERTY;
    private boolean                            isRegardingAdapterAnnotation      = false;
    private boolean                            isRegardingPropertyNameTemplate   = false;
    private RegardedAnnotationScope            regardedAnnotationScope           = RegardedAnnotationScope.INHERITED;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * Scope {@link Annotation}s are scanned
     * 
     * @see Configuration
     * @author Omnaest
     */
    public static enum RegardedAnnotationScope
    {
      /** No {@link Annotation}s are scanned */
      NONE,
      /** Only local {@link Annotation} declarations of the given type are scanned */
      DECLARED,
      /** Local declared and inherited {@link Annotation}s are scanned */
      INHERITED
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param propertyAccessOption
     * @param isRegardingAdapterAnnotation
     * @param isRegardingPropertyNameTemplate
     */
    public Configuration( PropertyAccessOption propertyAccessOption, boolean isRegardingAdapterAnnotation,
                          boolean isRegardingPropertyNameTemplate )
    {
      super();
      this.propertyAccessOption = propertyAccessOption;
      this.isRegardingAdapterAnnotation = isRegardingAdapterAnnotation;
      this.isRegardingPropertyNameTemplate = isRegardingPropertyNameTemplate;
    }
    
    /**
     * @param methodInvocationHandlerDecorators
     * @param sourcePropertyAccessorDecorators
     */
    public Configuration( MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators,
                          SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators )
    {
      super();
      this.methodInvocationHandlerDecorators = methodInvocationHandlerDecorators;
      this.sourcePropertyAccessorDecorators = sourcePropertyAccessorDecorators;
    }
    
    /**
     * 
     */
    public Configuration()
    {
      super();
    }
    
    /**
     * @param interfaces
     */
    public Configuration( Class<?>[] interfaces )
    {
      super();
      this.interfaces = interfaces;
    }
    
    /**
     * @param interfaceType
     */
    public void addInterface( Class<?> interfaceType )
    {
      //
      this.interfaces = this.interfaces == null ? this.interfaces = new Class[0] : this.interfaces;
      this.interfaces = ArrayUtils.add( this.interfaces, interfaceType );
    }
    
    /**
     * @see SourcePropertyAccessorDecorator
     * @param sourcePropertyAccessorDecorator
     */
    public void addSourcePropertyAccessorDecorator( SourcePropertyAccessorDecorator sourcePropertyAccessorDecorator )
    {
      //
      this.sourcePropertyAccessorDecorators = this.sourcePropertyAccessorDecorators == null ? new SourcePropertyAccessorDecorator[0]
                                                                                           : this.sourcePropertyAccessorDecorators;
      this.sourcePropertyAccessorDecorators = ArrayUtils.add( this.sourcePropertyAccessorDecorators,
                                                              sourcePropertyAccessorDecorator );
    }
    
    /**
     * @see MethodInvocationHandlerDecorator
     * @param methodInvocationHandlerDecorator
     */
    public void addMethodInvocationHandlerDecorator( MethodInvocationHandlerDecorator methodInvocationHandlerDecorator )
    {
      //
      this.methodInvocationHandlerDecorators = this.methodInvocationHandlerDecorators == null ? new MethodInvocationHandlerDecorator[0]
                                                                                             : this.methodInvocationHandlerDecorators;
      this.methodInvocationHandlerDecorators = ArrayUtils.add( this.methodInvocationHandlerDecorators,
                                                               methodInvocationHandlerDecorator );
    }
    
    public Class<?>[] getInterfaces()
    {
      return this.interfaces;
    }
    
    public void setInterfaces( Class<?>[] interfaces )
    {
      this.interfaces = interfaces;
    }
    
    public MethodInvocationHandlerDecorator[] getMethodInvocationHandlerDecorators()
    {
      return this.methodInvocationHandlerDecorators;
    }
    
    public void setMethodInvocationHandlerDecorators( MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators )
    {
      this.methodInvocationHandlerDecorators = methodInvocationHandlerDecorators;
    }
    
    public PropertyAccessOption getPropertyAccessOption()
    {
      return this.propertyAccessOption;
    }
    
    public void setPropertyAccessOption( PropertyAccessOption propertyAccessOption )
    {
      this.propertyAccessOption = propertyAccessOption;
    }
    
    public SourcePropertyAccessorDecorator[] getSourcePropertyAccessorDecorators()
    {
      return this.sourcePropertyAccessorDecorators;
    }
    
    public void setSourcePropertyAccessorDecorators( SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators )
    {
      this.sourcePropertyAccessorDecorators = sourcePropertyAccessorDecorators;
    }
    
    public boolean isRegardingAdapterAnnotation()
    {
      return this.isRegardingAdapterAnnotation;
    }
    
    public void setRegardingAdapterAnnotation( boolean isRegardingAdapterAnnotation )
    {
      this.isRegardingAdapterAnnotation = isRegardingAdapterAnnotation;
    }
    
    public boolean isRegardingPropertyNameTemplate()
    {
      return this.isRegardingPropertyNameTemplate;
    }
    
    public void setRegardingPropertyNameTemplate( boolean isRegardingPropertyNameTemplate )
    {
      this.isRegardingPropertyNameTemplate = isRegardingPropertyNameTemplate;
    }
    
    public RegardedAnnotationScope getRegardedAnnotationScope()
    {
      return this.regardedAnnotationScope;
    }
    
    public void setRegardedAnnotationScope( RegardedAnnotationScope regardedAnnotationScope )
    {
      this.regardedAnnotationScope = regardedAnnotationScope;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new {@link SourcePropertyAccessorToTypeAdapter} instance for the given {@link Class} based on the given
   * {@link SourcePropertyAccessor}
   * 
   * @param type
   * @param sourcePropertyAccessor
   * @return
   */
  public static <T> T newInstance( Class<T> type, SourcePropertyAccessor sourcePropertyAccessor )
  {
    Configuration configuration = null;
    return newInstance( type, sourcePropertyAccessor, configuration );
  }
  
  /**
   * Creates a new {@link SourcePropertyAccessorToTypeAdapter} instance for the given {@link Class} based on the given
   * {@link SourcePropertyAccessor}
   * 
   * @see Configuration
   * @param type
   * @param sourcePropertyAccessor
   * @param configuration
   * @return
   */
  public static <T> T newInstance( Class<T> type, SourcePropertyAccessor sourcePropertyAccessor, Configuration configuration )
  {
    //    
    T retval = null;
    
    //
    if ( type != null && sourcePropertyAccessor != null )
    {
      //      
      SourcePropertyAccessorToTypeAdapter<T> propertyAccessorToTypeAdapter = new SourcePropertyAccessorToTypeAdapter<T>(
                                                                                                                         type,
                                                                                                                         sourcePropertyAccessor,
                                                                                                                         configuration );
      
      //
      retval = propertyAccessorToTypeAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * @see #newInstance(Class, SourcePropertyAccessor, Class...)
   * @param type
   * @param sourcePropertyAccessor
   * @param configuration
   */
  protected SourcePropertyAccessorToTypeAdapter( Class<T> type, SourcePropertyAccessor sourcePropertyAccessor,
                                                 Configuration configuration )
  {
    //
    super();
    
    //
    Assert.notNull( type );
    Assert.notNull( sourcePropertyAccessor );
    
    //
    configuration = configuration != null ? configuration : new Configuration();
    
    //
    if ( configuration.isRegardingAdapterAnnotation() )
    {
      sourcePropertyAccessor = new SourcePropertyAccessorDecoratorAdapter( sourcePropertyAccessor );
    }
    if ( configuration.isRegardingPropertyNameTemplate() )
    {
      sourcePropertyAccessor = new SourcePropertyAccessorDecoratorPropertyNameTemplate( sourcePropertyAccessor );
    }
    if ( configuration.getPropertyAccessOption() != null
         && !PropertyAccessOption.PROPERTY.equals( configuration.getPropertyAccessOption() ) )
    {
      sourcePropertyAccessor = new SourcePropertyAccessorDecoratorPropertyAccessOption( sourcePropertyAccessor,
                                                                                        configuration.getPropertyAccessOption() );
    }
    
    //
    if ( configuration.getSourcePropertyAccessorDecorators() != null )
    {
      //
      SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators = configuration.getSourcePropertyAccessorDecorators();
      for ( SourcePropertyAccessorDecorator sourcePropertyAccessorDecorator : sourcePropertyAccessorDecorators )
      {
        if ( sourcePropertyAccessorDecorator != null )
        {
          sourcePropertyAccessor = sourcePropertyAccessorDecorator.setPropertyAccessorDecorator( sourcePropertyAccessor );
        }
      }
    }
    
    //
    this.sourcePropertyAccessor = sourcePropertyAccessor;
    this.type = type;
    
    //
    boolean isRegardingAnnotations = RegardedAnnotationScope.DECLARED.equals( configuration.getRegardedAnnotationScope() )
                                     || RegardedAnnotationScope.INHERITED.equals( configuration.getRegardedAnnotationScope() );
    boolean isRegardingInheritedAnnotations = RegardedAnnotationScope.INHERITED.equals( configuration.getRegardedAnnotationScope() );
    if ( isRegardingAnnotations )
    {
      //
      this.declaredAnnotationListOfType = isRegardingInheritedAnnotations ? ReflectionUtils.annotationList( type )
                                                                         : ReflectionUtils.declaredAnnotationList( type );
      this.declaredMethodToAnnotationSetMap = isRegardingInheritedAnnotations ? ReflectionUtils.methodToAnnotationSetMap( type )
                                                                             : ReflectionUtils.declaredMethodToAnnotationSetMap( type );
      this.propertyNameToBeanPropertyAnnotationSetMap = BeanUtils.propertyNameToBeanPropertyAnnotationSetMap( type );
      this.methodToReturnTypeMap = isRegardingInheritedAnnotations ? ReflectionUtils.methodToReturnTypeMap( type )
                                                                  : ReflectionUtils.declaredMethodToReturnTypeMap( type );
      this.methodNameToBeanMethodInformationMap = BeanUtils.methodNameToBeanMethodInformationMap( type );
    }
    
    //
    this.initializeClassAdapter( type, configuration );
  }
  
  /**
   * Creates the stub
   * 
   * @param type
   * @param interfaces
   * @param underlyingMapAware
   */
  protected void initializeClassAdapter( Class<? extends T> type, Configuration configuration )
  {
    //
    try
    {
      //       
      MethodInvocationHandler methodInvocationHandler = new ClassAdapterMethodInvocationHandler();
      Class<?>[] interfaces = configuration.getInterfaces();
      
      //
      MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators = configuration.getMethodInvocationHandlerDecorators();
      if ( methodInvocationHandlerDecorators != null )
      {
        for ( MethodInvocationHandlerDecorator methodInvocationHandlerDecorator : methodInvocationHandlerDecorators )
        {
          if ( methodInvocationHandlerDecorator != null )
          {
            methodInvocationHandler = methodInvocationHandlerDecorator.setMethodInvocationHandler( methodInvocationHandler );
          }
        }
      }
      
      //
      this.classAdapter = StubCreator.newStubInstance( type, interfaces, methodInvocationHandler );
      
    }
    catch ( Exception e )
    {
    }
  }
}