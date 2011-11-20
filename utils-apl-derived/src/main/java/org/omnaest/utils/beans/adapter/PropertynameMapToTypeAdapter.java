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
import java.util.Map;

import org.omnaest.utils.beans.adapter.source.DefaultValue;
import org.omnaest.utils.beans.adapter.source.DefaultValues;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessor;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecorator;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecorator;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecoratorToString;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecoratorUnderlyingMapAware;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.UnderlyingMapAware;

/**
 * This class creates a proxy implementation for a given {@link Class} or interface type which is used as a facade to an
 * underlying Map&lt;String,?&gt;. <br>
 * <br>
 * It supports the {@link Converter} annotation which allows to annotate getter and setter methods with {@link ElementConverter}
 * classes which are used to convert return values or the first available parameter if present. Be aware that if there are getter
 * and setter methods for the same property normally two different {@link Converter} annotations have to be set two support
 * conversion in two directions. <br>
 * <br>
 * Also the {@link PropertyNameTemplate} {@link Annotation} is supported. This allows to specify alternative property names,
 * including dynamic names which are based on additional parameters of the method itself. <br>
 * <br>
 * Further the {@link DefaultValue} and {@link DefaultValues} {@link Annotation} can be used to specify default values which are
 * used instead of the return value or parameter values in the case the return value or parameter value would be null. <br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * {
 *   Map&lt;String, Object&gt; map = new HashMap&lt;String, Object&gt;();
 *   ExampleType exampleType = PropertynameMapToTypeAdapter.newInstance( map, ExampleType.class );
 * }
 * </pre>
 * 
 * <pre>
 * protected static interface ExampleType
 * {
 *   &#064;PropertyNameTemplate(&quot;fieldDoubleRenamed&quot;)
 *   public Double getFieldDouble();
 *   
 *   public void setFieldDouble( Double fieldDouble );
 *   
 *   &#064;Converter(type = ElementConverterIntegerToString.class)
 *   public String getFieldString();
 *   
 *   &#064;Converter(type = ElementConverterStringToInteger.class)
 *   public void setFieldString( String fieldString );
 *   
 * }
 * </pre>
 * 
 * @see Configuration
 * @see #newInstance(Map, Class)
 * @see TypeToPropertynameMapAdapter
 * @see SourcePropertyAccessorToTypeAdapter
 * @see Converter
 * @see PropertyNameTemplate
 * @see PropertyAccessOption
 * @see DefaultValue
 * @see DefaultValues
 * @param <T>
 * @author Omnaest
 */
public class PropertynameMapToTypeAdapter<T> implements Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID = 2245226860618141171L;
  /* ********************************************** Variables ********************************************** */
  
  protected Map<String, Object> map              = null;
  protected T                   classAdapter     = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * The {@link Configuration} of a {@link PropertynameMapToTypeAdapter} includes following settings:<br>
   * <br>
   * <ul>
   * <li>{@link #setSimulatingToString(boolean)}</li>
   * <li>{@link #setUnderlyingMapAware(boolean)}</li>
   * </ul>
   * <br>
   * <br>
   * From the {@link org.omnaest.utils.beans.adapter.SourcePropertyAccessorToTypeAdapter.Configuration} there are several more
   * settings available, too. <br>
   * <br>
   * If {@link #setUnderlyingMapAware(boolean)} is set to true, the created proxy will also implement the
   * {@link UnderlyingMapAware} interface. Which allows to cast proxies to that type and access the underlying {@link Map}
   * content. <br>
   * <br>
   * If {@link #setSimulatingToString(boolean)} is set to true, the proxy will simulate the {@link Object#toString()} method by
   * rendering the underlying {@link Map}. <br>
   * <br>
   * 
   * @see PropertynameMapToTypeAdapter
   * @see SourcePropertyAccessorToTypeAdapter.Configuration
   * @author Omnaest
   */
  public static class Configuration extends SourcePropertyAccessorToTypeAdapter.Configuration
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID   = -4976626371731665509L;
    /* ********************************************** Variables ********************************************** */
    private boolean           underlyingMapAware = false;
    private boolean           simulatingToString = true;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Configuration
     */
    public Configuration()
    {
      super();
    }
    
    /**
     * @see Configuration
     * @param propertyAccessOption
     * @param isRegardingAdapterAnnotation
     * @param isRegardingPropertyNameTemplateAnnotation
     * @param isRegardingDefaultValueAnnotation
     * @param underlyingMapAware
     * @param simulatingToString
     */
    public Configuration( PropertyAccessOption propertyAccessOption, boolean isRegardingAdapterAnnotation,
                          boolean isRegardingPropertyNameTemplateAnnotation, boolean isRegardingDefaultValueAnnotation,
                          boolean underlyingMapAware, boolean simulatingToString )
    {
      super( propertyAccessOption, isRegardingAdapterAnnotation, isRegardingPropertyNameTemplateAnnotation,
             isRegardingDefaultValueAnnotation );
      this.underlyingMapAware = underlyingMapAware;
      this.simulatingToString = simulatingToString;
    }
    
    /**
     * @param underlyingMapAware
     * @param simulatingToString
     */
    public Configuration( boolean underlyingMapAware, boolean simulatingToString )
    {
      super();
      this.underlyingMapAware = underlyingMapAware;
      this.simulatingToString = simulatingToString;
    }
    
    /**
     * @param methodInvocationHandlerDecorators
     * @param sourcePropertyAccessorDecorators
     */
    public Configuration( MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators,
                          SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators )
    {
      super( methodInvocationHandlerDecorators, sourcePropertyAccessorDecorators );
    }
    
    /**
     * @param interfaces
     */
    public Configuration( Class<?>[] interfaces )
    {
      super( interfaces );
    }
    
    /**
     * @see UnderlyingMapAware
     * @return
     */
    public boolean isUnderlyingMapAware()
    {
      return this.underlyingMapAware;
    }
    
    /**
     * @see UnderlyingMapAware
     * @param underlyingMapAware
     */
    public void setUnderlyingMapAware( boolean underlyingMapAware )
    {
      this.underlyingMapAware = underlyingMapAware;
    }
    
    /**
     * @return
     */
    public boolean isSimulatingToString()
    {
      return this.simulatingToString;
    }
    
    /**
     * @param simulatingToString
     */
    public void setSimulatingToString( boolean simulatingToString )
    {
      this.simulatingToString = simulatingToString;
    }
    
  }
  
  /**
   * @see SourcePropertyAccessor
   * @author Omnaest
   */
  protected class SourePropertyAccessorForMap implements SourcePropertyAccessor
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 5413119292636127784L;
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public void setValue( String propertyName,
                          Object value,
                          Class<?> parameterType,
                          PropertyMetaInformation propertyMetaInformation )
    {
      PropertynameMapToTypeAdapter.this.map.put( propertyName, value );
    }
    
    @Override
    public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
    {
      return PropertynameMapToTypeAdapter.this.map.get( propertyName );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see PropertynameMapToTypeAdapter#newInstance(Map, Class)
   */
  protected PropertynameMapToTypeAdapter()
  {
    super();
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param type
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @param propertyAccessOption
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map, Class<? extends T> type )
  {
    Configuration configuration = null;
    return newInstance( map, type, configuration );
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param type
   * @param configuration
   *          {@link Configuration}
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map, Class<? extends T> type, Configuration configuration )
  {
    //    
    T retval = null;
    
    //
    if ( type != null && map != null )
    {
      //
      PropertynameMapToTypeAdapter<T> mapToInterfaceAdapter = new PropertynameMapToTypeAdapter<T>( map, type, configuration );
      
      //
      retval = mapToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * Internal constructor. See {@link #newInstance(Map, Class)} instead.
   * 
   * @param map
   * @param type
   * @param underlyingMapAware
   * @param simulatingToString
   * @param propertyAccessOption
   */
  @SuppressWarnings("unchecked")
  protected <M extends Map<String, Object>> PropertynameMapToTypeAdapter( M map, Class<? extends T> type,
                                                                          Configuration configuration )
  {
    //
    super();
    this.map = map;
    
    //
    configuration = configuration != null ? configuration : new Configuration();
    
    //
    if ( configuration.isUnderlyingMapAware() )
    {
      //
      configuration.addInterface( UnderlyingMapAware.class );
      
      //
      configuration.addMethodInvocationHandlerDecorator( new MethodInvocationHandlerDecoratorUnderlyingMapAware()
      {
        
        @Override
        public void setUnderlyingMap( Map<?, ?> underlyingMap )
        {
          PropertynameMapToTypeAdapter.this.map = (Map<String, Object>) underlyingMap;
        }
        
        @Override
        public Map<?, ?> getUnderlyingMap()
        {
          return PropertynameMapToTypeAdapter.this.map;
        }
      } );
    }
    if ( configuration.isSimulatingToString() )
    {
      //
      configuration.addMethodInvocationHandlerDecorator( new MethodInvocationHandlerDecoratorToString()
      {
        
        @Override
        public String handleToString()
        {
          return MapUtils.toString( PropertynameMapToTypeAdapter.this.map );
        }
      } );
    }
    
    //
    this.initializeClassAdapter( type, configuration );
  }
  
  /**
   * Creates the stub
   * 
   * @param clazz
   * @param underlyingMapAware
   * @param configuration
   */
  protected void initializeClassAdapter( Class<? extends T> type, Configuration configuration )
  {
    //
    try
    {
      //
      SourcePropertyAccessor sourcePropertyAccessor = new SourePropertyAccessorForMap();
      this.classAdapter = SourcePropertyAccessorToTypeAdapter.newInstance( type, sourcePropertyAccessor, configuration );
    }
    catch ( Exception e )
    {
    }
  }
  
}
