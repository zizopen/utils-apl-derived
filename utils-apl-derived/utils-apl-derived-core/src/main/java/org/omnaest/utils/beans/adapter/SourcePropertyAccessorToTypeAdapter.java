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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter.Configuration.RegardedAnnotationScope;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor.PropertyMetaInformation;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecorator;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecoratorAdapter;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecoratorDefaultValue;
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
import org.omnaest.utils.structure.element.cached.CachedElement;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * The {@link SourcePropertyAccessorToTypeAdapter} will provide an adapter from a {@link SourcePropertyAccessor} to any given
 * {@link Class} type.
 * 
 * @see Configuration
 * @author Omnaest
 */
public class SourcePropertyAccessorToTypeAdapter<T> implements Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 5957600557802418027L;
  
  /* ********************************************** Variables ********************************************** */
  private final Builder<T>  builder;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link Builder} for multiple instances based on the same {@link Class} type and {@link Configuration} instance. <br>
   * This is much faster for many instances, since the {@link Class} has only to be generated once as prototype and can then be
   * cloned to generate new instances.<br>
   * <br>
   * It has to be ensured that any decorator given within the {@link Configuration} is stateless, since it might encounter
   * concurrent access through different facade instances.
   * 
   * @author Omnaest
   */
  @SuppressWarnings("javadoc")
  public static interface Builder<T> extends Serializable
  {
    
    /**
     * Creates the type adapter stub
     * 
     * @param sourcePropertyAccessor
     *          {@link SourcePropertyAccessor}
     * @return new instance of type
     */
    public T newTypeAdapter( SourcePropertyAccessor sourcePropertyAccessor );
    
    /**
     * Similar to {@link #newTypeAdapter(SourcePropertyAccessor)} but allows to add further
     * {@link SourcePropertyAccessorDecorator} and {@link MethodInvocationHandlerDecorator} instances.
     * 
     * @param sourcePropertyAccessor
     *          {@link SourcePropertyAccessor}
     * @param sourcePropertyAccessorDecorators
     *          {@link SourcePropertyAccessorDecorator}
     * @param methodInvocationHandlerDecorators
     *          {@link MethodInvocationHandlerDecorator}
     * @return new instance of type
     */
    public T newTypeAdapter( SourcePropertyAccessor sourcePropertyAccessor,
                             SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators,
                             MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators );
    
  }
  
  /**
   * @see Builder
   * @author Omnaest
   * @param <T>
   */
  private static class BuilderImpl<T> implements Builder<T>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long                        serialVersionUID  = 9056642721076392704L;
    /* ********************************************** Variables / State ********************************************** */
    private final List<Annotation>                   declaredAnnotationListOfType;
    private final Map<Method, Set<Annotation>>       declaredMethodToAnnotationSetMap;
    private final Map<String, Set<Annotation>>       propertyNameToBeanPropertyAnnotationSetMap;
    private final Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap;
    
    private final Class<T>                           type;
    private final Configuration                      configuration;
    private final CachedElement<StubCreator<T>>      cachedStubFactory = new CachedElement<StubCreator<T>>(
                                                                                                            new ValueResolver<StubCreator<T>>()
                                                                                                            {
                                                                                                              @Override
                                                                                                              public StubCreator<T> resolveValue()
                                                                                                              {
                                                                                                                // 
                                                                                                                return new StubCreator<T>(
                                                                                                                                           BuilderImpl.this.type,
                                                                                                                                           BuilderImpl.this.configuration.getInterfaces() );
                                                                                                              }
                                                                                                            } );
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see BuilderImpl
     * @param declaredAnnotationListOfType
     * @param declaredMethodToAnnotationSetMap
     * @param propertyNameToBeanPropertyAnnotationSetMap
     * @param methodNameToBeanMethodInformationMap
     * @param type
     * @param configuration
     */
    public BuilderImpl( List<Annotation> declaredAnnotationListOfType,
                        Map<Method, Set<Annotation>> declaredMethodToAnnotationSetMap,
                        Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap,
                        Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap, Class<T> type,
                        Configuration configuration )
    {
      super();
      this.declaredAnnotationListOfType = declaredAnnotationListOfType;
      this.declaredMethodToAnnotationSetMap = declaredMethodToAnnotationSetMap;
      this.propertyNameToBeanPropertyAnnotationSetMap = propertyNameToBeanPropertyAnnotationSetMap;
      this.methodNameToBeanMethodInformationMap = methodNameToBeanMethodInformationMap;
      this.type = type;
      this.configuration = configuration;
    }
    
    @Override
    public T newTypeAdapter( SourcePropertyAccessor sourcePropertyAccessor )
    {
      //
      final SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators = null;
      final MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators = null;
      return this.newTypeAdapter( sourcePropertyAccessor, sourcePropertyAccessorDecorators, methodInvocationHandlerDecorators );
    }
    
    @Override
    public T newTypeAdapter( SourcePropertyAccessor sourcePropertyAccessor,
                             SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators,
                             MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators )
    {
      //
      T retval = null;
      
      //
      Assert.isNotNull( sourcePropertyAccessor, "sourcePropertyAccessor must not be null" );
      try
      {
        //
        if ( this.configuration.isRegardingAdapterAnnotation() )
        {
          sourcePropertyAccessor = new SourcePropertyAccessorDecoratorAdapter( sourcePropertyAccessor );
        }
        if ( this.configuration.isRegardingPropertyNameTemplateAnnotation() )
        {
          sourcePropertyAccessor = new SourcePropertyAccessorDecoratorPropertyNameTemplate( sourcePropertyAccessor );
        }
        if ( this.configuration.getPropertyAccessOption() != null
             && !PropertyAccessOption.PROPERTY.equals( this.configuration.getPropertyAccessOption() ) )
        {
          sourcePropertyAccessor = new SourcePropertyAccessorDecoratorPropertyAccessOption(
                                                                                            sourcePropertyAccessor,
                                                                                            this.configuration.getPropertyAccessOption() );
        }
        if ( this.configuration.isRegardingDefaultValueAnnotation() )
        {
          sourcePropertyAccessor = new SourcePropertyAccessorDecoratorDefaultValue( sourcePropertyAccessor );
        }
        
        //
        if ( this.configuration.getSourcePropertyAccessorDecorators() != null )
        {
          //
          sourcePropertyAccessorDecorators = org.omnaest.utils.structure.array.ArrayUtils.merge( this.configuration.getSourcePropertyAccessorDecorators(),
                                                                                                 sourcePropertyAccessorDecorators );
          for ( SourcePropertyAccessorDecorator sourcePropertyAccessorDecorator : sourcePropertyAccessorDecorators )
          {
            if ( sourcePropertyAccessorDecorator != null )
            {
              sourcePropertyAccessor = sourcePropertyAccessorDecorator.setPropertyAccessorDecorator( sourcePropertyAccessor );
            }
          }
        }
        
        //       
        MethodInvocationHandler methodInvocationHandler = new ClassAdapterMethodInvocationHandler(
                                                                                                   sourcePropertyAccessor,
                                                                                                   this.declaredAnnotationListOfType,
                                                                                                   this.declaredMethodToAnnotationSetMap,
                                                                                                   this.propertyNameToBeanPropertyAnnotationSetMap,
                                                                                                   this.methodNameToBeanMethodInformationMap );
        
        //
        methodInvocationHandlerDecorators = org.omnaest.utils.structure.array.ArrayUtils.merge( this.configuration.getMethodInvocationHandlerDecorators(),
                                                                                                methodInvocationHandlerDecorators );
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
        retval = this.cachedStubFactory.getValue().build( methodInvocationHandler );
        
      }
      catch ( Exception e )
      {
      }
      
      //
      return retval;
    }
  }
  
  /**
   * @author Omnaest
   */
  private static class ClassAdapterMethodInvocationHandler implements MethodInvocationHandler, Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long                        serialVersionUID       = 7923602793508877717L;
    /* ********************************************** Variables / State ********************************************** */
    private SourcePropertyAccessor                   sourcePropertyAccessor = null;
    
    private final List<Annotation>                   declaredAnnotationListOfType;
    private final Map<Method, Set<Annotation>>       declaredMethodToAnnotationSetMap;
    private final Map<String, Set<Annotation>>       propertyNameToBeanPropertyAnnotationSetMap;
    private final Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ClassAdapterMethodInvocationHandler
     * @param sourcePropertyAccessor
     * @param declaredAnnotationListOfType
     * @param declaredMethodToAnnotationSetMap
     * @param propertyNameToBeanPropertyAnnotationSetMap
     * @param methodNameToBeanMethodInformationMap
     */
    public ClassAdapterMethodInvocationHandler( SourcePropertyAccessor sourcePropertyAccessor,
                                                List<Annotation> declaredAnnotationListOfType,
                                                Map<Method, Set<Annotation>> declaredMethodToAnnotationSetMap,
                                                Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap,
                                                Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap )
    {
      super();
      this.sourcePropertyAccessor = sourcePropertyAccessor;
      this.declaredAnnotationListOfType = declaredAnnotationListOfType;
      this.declaredMethodToAnnotationSetMap = declaredMethodToAnnotationSetMap;
      this.propertyNameToBeanPropertyAnnotationSetMap = propertyNameToBeanPropertyAnnotationSetMap;
      this.methodNameToBeanMethodInformationMap = methodNameToBeanMethodInformationMap;
    }
    
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
        BeanMethodInformation beanMethodInformation = this.methodNameToBeanMethodInformationMap.get( methodName );
        if ( beanMethodInformation != null )
        {
          //
          final String referencedFieldName = beanMethodInformation.getReferencedFieldName();
          
          //          
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isGetterWithAddtionalArguments = beanMethodInformation.isGetterWithAdditionalArguments() && args.length >= 1;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          boolean isSetterWithAdditionalArguments = beanMethodInformation.isSetterWithAdditionalArguments() && args.length >= 2;
          boolean isPropertyAccessorNotNull = this.sourcePropertyAccessor != null;
          
          //          
          if ( isPropertyAccessorNotNull )
          {
            //
            AutowiredContainer<Annotation> propertyAnnotationAutowiredContainer = ClassMapToAutowiredContainerAdapter.newInstance( new LinkedHashMap<Class<? extends Annotation>, Annotation>() );
            AutowiredContainer<Annotation> classAnnotationAutowiredContainer = ClassMapToAutowiredContainerAdapter.newInstance( new LinkedHashMap<Class<? extends Annotation>, Annotation>() );
            
            //
            classAnnotationAutowiredContainer.putAll( this.declaredAnnotationListOfType );
            propertyAnnotationAutowiredContainer.putAll( this.propertyNameToBeanPropertyAnnotationSetMap.get( referencedFieldName ) );
            propertyAnnotationAutowiredContainer.putAll( this.declaredMethodToAnnotationSetMap.get( method ) );
            
            //
            if ( isGetter || isGetterWithAddtionalArguments )
            {
              //
              final Class<?> returnType = beanMethodInformation.getMethod().getReturnType();
              final Type genericReturnType = beanMethodInformation.getMethod().getGenericReturnType();
              final ParameterizedType genericType = (ParameterizedType) ( genericReturnType instanceof ParameterizedType ? genericReturnType
                                                                                                                        : null );
              
              //
              Object[] additionalArguments = isGetterWithAddtionalArguments ? Arrays.copyOf( args, args.length ) : new Object[0];
              PropertyMetaInformation propertyMetaInformation = new PropertyMetaInformation(
                                                                                             additionalArguments,
                                                                                             genericType,
                                                                                             propertyAnnotationAutowiredContainer,
                                                                                             classAnnotationAutowiredContainer );
              
              //              
              retval = this.sourcePropertyAccessor.getValue( referencedFieldName, returnType, propertyMetaInformation );
            }
            else if ( isSetter || isSetterWithAdditionalArguments )
            {
              //
              String propertyName = referencedFieldName;
              Object value = args[0];
              
              final Class<?>[] targetParameterTypes = beanMethodInformation.getMethod().getParameterTypes();
              final Class<?> parameterType = targetParameterTypes != null && targetParameterTypes.length >= 1 ? targetParameterTypes[0]
                                                                                                             : null;
              
              final Type[] genericParameterTypes = beanMethodInformation.getMethod().getGenericParameterTypes();
              final ParameterizedType genericParameterType = (ParameterizedType) ( genericParameterTypes != null
                                                                                   && genericParameterTypes.length >= 1
                                                                                   && genericParameterTypes[0] instanceof ParameterizedType ? genericParameterTypes[0]
                                                                                                                                           : null );
              
              //
              Object[] additionalArguments = isSetterWithAdditionalArguments ? Arrays.copyOfRange( args, 1, args.length )
                                                                            : new Object[0];
              PropertyMetaInformation propertyMetaInformation = new PropertyMetaInformation(
                                                                                             additionalArguments,
                                                                                             genericParameterType,
                                                                                             propertyAnnotationAutowiredContainer,
                                                                                             classAnnotationAutowiredContainer );
              
              this.sourcePropertyAccessor.setValue( propertyName, value, parameterType, propertyMetaInformation );
              
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
   * <li>{@link #setRegardedAnnotationScope(RegardedAnnotationScope)}</li>
   * <li>{@link #setRegardingDefaultValueAnnotation(boolean)}</li>
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
  public static class Configuration implements Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long                  serialVersionUID                          = 1537703849251094863L;
    /* ********************************************** Variables ********************************************** */
    private Class<?>[]                         interfaces                                = new Class<?>[0];
    private MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators         = null;
    private SourcePropertyAccessorDecorator[]  sourcePropertyAccessorDecorators          = null;
    private PropertyAccessOption               propertyAccessOption                      = PropertyAccessOption.PROPERTY;
    private boolean                            isRegardingAdapterAnnotation              = false;
    private boolean                            isRegardingPropertyNameTemplateAnnotation = false;
    private boolean                            isRegardingDefaultValueAnnotation         = false;
    private RegardedAnnotationScope            regardedAnnotationScope                   = RegardedAnnotationScope.INHERITED;
    
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
     * @see Configuration
     * @param propertyAccessOption
     * @param isRegardingAdapterAnnotation
     * @param isRegardingPropertyNameTemplateAnnotation
     * @param isRegardingDefaultValueAnnotation
     */
    public Configuration( PropertyAccessOption propertyAccessOption, boolean isRegardingAdapterAnnotation,
                          boolean isRegardingPropertyNameTemplateAnnotation, boolean isRegardingDefaultValueAnnotation )
    {
      super();
      this.propertyAccessOption = propertyAccessOption;
      this.isRegardingAdapterAnnotation = isRegardingAdapterAnnotation;
      this.isRegardingPropertyNameTemplateAnnotation = isRegardingPropertyNameTemplateAnnotation;
      this.isRegardingDefaultValueAnnotation = isRegardingDefaultValueAnnotation;
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
    public Configuration( Class<?>... interfaces )
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
    
    public boolean isRegardingPropertyNameTemplateAnnotation()
    {
      return this.isRegardingPropertyNameTemplateAnnotation;
    }
    
    public void setRegardingPropertyNameTemplate( boolean isRegardingPropertyNameTemplate )
    {
      this.isRegardingPropertyNameTemplateAnnotation = isRegardingPropertyNameTemplate;
    }
    
    public RegardedAnnotationScope getRegardedAnnotationScope()
    {
      return this.regardedAnnotationScope;
    }
    
    public void setRegardedAnnotationScope( RegardedAnnotationScope regardedAnnotationScope )
    {
      this.regardedAnnotationScope = regardedAnnotationScope;
    }
    
    public boolean isRegardingDefaultValueAnnotation()
    {
      return this.isRegardingDefaultValueAnnotation;
    }
    
    public void setRegardingDefaultValueAnnotation( boolean isRegardingDefaultValueAnnotation )
    {
      this.isRegardingDefaultValueAnnotation = isRegardingDefaultValueAnnotation;
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
      retval = builder( type, configuration ).newTypeAdapter( sourcePropertyAccessor );
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new {@link Builder} instance which allows to create multiple adapter instances much faster.
   * 
   * @see #newInstance(Class, SourcePropertyAccessor, Configuration)
   * @param type
   *          {@link Class}
   * @param configuration
   *          {@link Configuration}
   * @return {@link Builder}
   */
  public static <T> Builder<T> builder( Class<T> type, Configuration configuration )
  {
    //
    Builder<T> retval = null;
    
    //
    if ( type != null )
    {
      //      
      final SourcePropertyAccessorToTypeAdapter<T> propertyAccessorToTypeAdapter = new SourcePropertyAccessorToTypeAdapter<T>(
                                                                                                                               type,
                                                                                                                               configuration );
      
      //
      retval = propertyAccessorToTypeAdapter.getBuilder();
    }
    
    //
    return retval;
  }
  
  /**
   * @see #newInstance(Class, SourcePropertyAccessor, Configuration)
   * @param type
   * @param configuration
   */
  protected SourcePropertyAccessorToTypeAdapter( Class<T> type, Configuration configuration )
  {
    //
    super();
    
    //
    Assert.isNotNull( type );
    
    //
    configuration = configuration != null ? configuration : new Configuration();
    
    //
    List<Annotation> declaredAnnotationListOfType = null;
    Map<Method, Set<Annotation>> declaredMethodToAnnotationSetMap = null;
    Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap = null;
    Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap = null;
    
    //
    final boolean isRegardingAnnotations = RegardedAnnotationScope.DECLARED.equals( configuration.getRegardedAnnotationScope() )
                                           || RegardedAnnotationScope.INHERITED.equals( configuration.getRegardedAnnotationScope() );
    final boolean isRegardingInheritedAnnotations = RegardedAnnotationScope.INHERITED.equals( configuration.getRegardedAnnotationScope() );
    
    if ( isRegardingAnnotations )
    {
      //
      declaredAnnotationListOfType = ImmutableList.<Annotation> builder()
                                                  .addAll( ( isRegardingInheritedAnnotations ? ReflectionUtils.annotationList( type )
                                                                                            : ReflectionUtils.declaredAnnotationList( type ) ) )
                                                  .build();
      declaredMethodToAnnotationSetMap = ImmutableMap.<Method, Set<Annotation>> builder()
                                                     .putAll( isRegardingInheritedAnnotations ? ReflectionUtils.methodToAnnotationSetMap( type )
                                                                                             : ReflectionUtils.declaredMethodToAnnotationSetMap( type ) )
                                                     .build();
      propertyNameToBeanPropertyAnnotationSetMap = ImmutableMap.<String, Set<Annotation>> builder()
                                                               .putAll( BeanUtils.propertyNameToBeanPropertyAnnotationSetMap( type ) )
                                                               .build();
      methodNameToBeanMethodInformationMap = ImmutableMap.<String, BeanMethodInformation> builder()
                                                         .putAll( BeanUtils.methodNameToBeanMethodInformationMap( type ) )
                                                         .build();
    }
    
    //
    this.builder = new BuilderImpl<T>( declaredAnnotationListOfType, declaredMethodToAnnotationSetMap,
                                       propertyNameToBeanPropertyAnnotationSetMap, methodNameToBeanMethodInformationMap, type,
                                       configuration );
    
  }
  
  private Builder<T> getBuilder()
  {
    return this.builder;
  }
  
}
