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
import java.util.Map;

import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecoratorToString;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecoratorUnderlyingMapAware;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.converter.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.UnderlyingMapAware;

/**
 * This class creates a proxy implementation for a given class or interface type which is used as a facade to an underlying
 * Map&lt;String,?&gt;. <br>
 * <br>
 * It supports the {@link Adapter} annotation which allows to annotate getter and setter methods with {@link ElementConverter}
 * classes which are used to convert return values or the first available parameter if present. Be aware that if there are getter
 * and setter methods for the same property normally two different {@link Adapter} annotations have to be set two support
 * conversion in two directions.
 * 
 * @author Omnaest
 * @see #newInstance(Map, Class)
 * @see TypeToPropertynameMapAdapter
 * @see Adapter
 * @see PropertyAccessOption
 * @param <T>
 * @param <M>
 */
public class PropertynameMapToTypeAdapter<T>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<String, Object>  map                   = null;
  protected T                    classAdapter          = null;
  protected Class<T>             clazz                 = null;
  protected PropertyAccessOption propertyAccessOption  = PropertyAccessOption.PROPERTY;
  protected Map<Method, Adapter> methodToAnnotationMap = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Options to modify the property access behavior regarding the property keys of the {@link Map}
   * 
   * @see PropertynameMapToTypeAdapter
   * @author Omnaest
   */
  public static enum PropertyAccessOption
  {
    PROPERTY,
    PROPERTY_LOWERCASE,
    PROPERTY_UPPERCASE
  }
  
  /**
   * A {@link MethodInvocationHandler} implementation special for this {@link PropertynameMapToTypeAdapter}
   */
  protected class ClassAdapterMethodInvocationHandler implements MethodInvocationHandler
  {
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      //
      Object obj = methodCallCapture.getObject();
      Method method = methodCallCapture.getMethod();
      Object[] args = methodCallCapture.getArguments();
      MethodProxy proxy = methodCallCapture.getProxy();
      return this.intercept( obj, method, args, proxy );
    }
    
    private Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
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
          final Adapter adapter = PropertynameMapToTypeAdapter.this.methodToAnnotationMap.get( method );
          
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          
          boolean isMapNotNull = PropertynameMapToTypeAdapter.this.map != null;
          boolean hasAdapter = adapter != null;
          
          //
          
          if ( isMapNotNull )
          {
            //
            String referenceFieldNameAsMapKey;
            if ( PropertyAccessOption.PROPERTY_LOWERCASE.equals( PropertynameMapToTypeAdapter.this.propertyAccessOption ) )
            {
              referenceFieldNameAsMapKey = referencedFieldName.toLowerCase();
            }
            else if ( PropertyAccessOption.PROPERTY_UPPERCASE.equals( PropertynameMapToTypeAdapter.this.propertyAccessOption ) )
            {
              referenceFieldNameAsMapKey = referencedFieldName.toUpperCase();
            }
            else
            {
              referenceFieldNameAsMapKey = referencedFieldName;
            }
            
            //
            if ( isGetter )
            {
              //
              retval = PropertynameMapToTypeAdapter.this.map.get( referenceFieldNameAsMapKey );
              
              //
              if ( hasAdapter )
              {
                retval = this.convertByAdapter( retval, adapter );
              }
            }
            else if ( isSetter )
            {
              //
              Object value = args[0];
              
              //
              if ( hasAdapter )
              {
                value = this.convertByAdapter( value, adapter );
              }
              
              //
              PropertynameMapToTypeAdapter.this.map.put( referenceFieldNameAsMapKey, value );
              
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object convertByAdapter( Object value, Adapter adapter )
    {
      //
      Object retval = value;
      
      //
      if ( adapter != null )
      {
        //   
        try
        {
          Class<? extends ElementConverter> type = adapter.type();
          ElementConverter elementConverter = ReflectionUtils.createInstanceOf( type );
          if ( elementConverter != null )
          {
            retval = elementConverter.convert( value );
          }
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      return retval;
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
   * @param clazz
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map, Class<? extends T> clazz, boolean underlyingMapAware )
  {
    PropertyAccessOption propertyAccessOption = null;
    return newInstance( map, clazz, underlyingMapAware, propertyAccessOption );
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param clazz
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @param simulatingToString
   *          : simulates the {@link #toString()} method
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map,
                                   Class<? extends T> clazz,
                                   boolean underlyingMapAware,
                                   boolean simulatingToString )
  {
    PropertyAccessOption propertyAccessOption = null;
    return newInstance( map, clazz, underlyingMapAware, simulatingToString, propertyAccessOption );
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param clazz
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @param propertyAccessOption
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map,
                                   Class<? extends T> clazz,
                                   boolean underlyingMapAware,
                                   PropertyAccessOption propertyAccessOption )
  {
    boolean simulatesToString = false;
    return newInstance( map, clazz, underlyingMapAware, simulatesToString, propertyAccessOption );
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param clazz
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @param simulatesToString
   *          : simulates the {@link #toString()} method
   * @param propertyAccessOption
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map,
                                   Class<? extends T> clazz,
                                   boolean underlyingMapAware,
                                   boolean simulatesToString,
                                   PropertyAccessOption propertyAccessOption )
  {
    //    
    T retval = null;
    
    //
    if ( clazz != null && map != null )
    {
      //
      PropertynameMapToTypeAdapter<T> mapToInterfaceAdapter = new PropertynameMapToTypeAdapter<T>( map, clazz,
                                                                                                   underlyingMapAware,
                                                                                                   simulatesToString,
                                                                                                   propertyAccessOption );
      
      //
      retval = mapToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade. If the given {@link Class} implements the {@link UnderlyingMapAware} interface, the
   * {@link UnderlyingMapAware#getUnderlyingMap()} will return the given {@link Map}.
   * 
   * @see #newInstance(Map, Class, boolean)
   * @param map
   * @param clazz
   * @return new
   */
  public static <T> T newInstance( Map<String, Object> map, Class<? extends T> clazz )
  {
    boolean underlyingMapAware = UnderlyingMapAware.class.isAssignableFrom( clazz );
    return PropertynameMapToTypeAdapter.newInstance( map, clazz, underlyingMapAware );
  }
  
  /**
   * Factory methods to create a new {@link PropertynameMapToTypeAdapter} for a given {@link Map} with the given {@link Class} as
   * facade.
   * 
   * @see #newInstance(Map, Class, boolean)
   * @param map
   * @param clazz
   * @param propertyAccessOption
   * @return new
   */
  @SuppressWarnings({ "unchecked" })
  public static <T> T newInstance( Map<? super String, Object> map,
                                   Class<? extends T> clazz,
                                   PropertyAccessOption propertyAccessOption )
  {
    boolean underlyingMapAware = false;
    return PropertynameMapToTypeAdapter.newInstance( (Map<String, Object>) map, clazz, underlyingMapAware, propertyAccessOption );
  }
  
  /**
   * Internal constructor. See {@link #newInstance(Map, Class)} instead.
   * 
   * @param map
   * @param type
   * @param underlyingMapAware
   * @param simulatesToString
   * @param propertyAccessOption
   */
  @SuppressWarnings("unchecked")
  protected <M extends Map<String, Object>> PropertynameMapToTypeAdapter( M map, Class<? extends T> type,
                                                                          boolean underlyingMapAware, boolean simulatesToString,
                                                                          PropertyAccessOption propertyAccessOption )
  {
    //
    super();
    this.map = map;
    this.clazz = (Class<T>) type;
    
    if ( propertyAccessOption != null )
    {
      this.propertyAccessOption = propertyAccessOption;
    }
    this.methodToAnnotationMap = ReflectionUtils.methodToAnnotationMap( type, Adapter.class );
    
    //
    this.initializeClassAdapter( type, underlyingMapAware, simulatesToString );
  }
  
  /**
   * Creates the stub
   * 
   * @param clazz
   * @param underlyingMapAware
   */
  protected void initializeClassAdapter( Class<? extends T> clazz, boolean underlyingMapAware, boolean simulatesToString )
  {
    //
    try
    {
      //
      MethodInvocationHandler methodInvocationHandler = new ClassAdapterMethodInvocationHandler();
      if ( underlyingMapAware )
      {
        methodInvocationHandler = new MethodInvocationHandlerDecoratorUnderlyingMapAware( methodInvocationHandler )
        {
          @SuppressWarnings("unchecked")
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
        };
      }
      if ( simulatesToString )
      {
        methodInvocationHandler = new MethodInvocationHandlerDecoratorToString( methodInvocationHandler )
        {
          
          @Override
          public String handleToString()
          {
            return MapUtils.toString( PropertynameMapToTypeAdapter.this.map );
          }
        };
      }
      
      //       
      Class<?>[] interfaces = underlyingMapAware ? new Class[] { UnderlyingMapAware.class } : null;
      this.classAdapter = StubCreator.newStubInstance( clazz, interfaces, methodInvocationHandler );
    }
    catch ( Exception e )
    {
    }
  }
  
}
