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
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * This class creates a proxy implementation for a given class or interface type which is used as a facade to an underlying
 * Map&lt;String,?&gt;.
 * 
 * @author Omnaest
 * @see #newInstance(Map, Class)
 * @see TypeToPropertynameMapAdapter
 * @param <T>
 * @param <M>
 */
public class PropertynameMapToTypeAdapter<T, M extends Map<? super String, Object>>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<? super String, Object> map                      = null;
  protected T                           classAdapter             = null;
  protected Class<T>                    clazz                    = null;
  protected boolean                     hasAccessToUnderlyingMap = false;
  protected boolean                     simulatesToString        = false;
  protected PropertyAccessOption        propertyAccessOption     = PropertyAccessOption.PROPERTY;
  
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
   * This interface makes a derivative type aware of an underlying map implementation. This is normally used in combination with
   * an {@link PropertynameMapToTypeAdapter}.
   */
  public static interface UnderlyingMapAware<M extends Map<String, Object>>
  {
    /**
     * Returns the {@link Map} which underlies this class type facade.
     * 
     * @return
     */
    public M getUnderlyingMap();
    
    /**
     * Sets the {@link Map} which should underly this class type facade.
     * 
     * @param underlyingMap
     */
    public void setUnderlyingMap( M underlyingMap );
  }
  
  /**
   * A {@link MethodInterceptor} implementation special for this {@link PropertynameMapToTypeAdapter}
   */
  protected class ClassAdapterMethodInterceptor implements MethodInterceptor
  {
    @SuppressWarnings("unchecked")
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
          boolean accessToUnderlyingMap = PropertynameMapToTypeAdapter.this.hasAccessToUnderlyingMap
                                          && "underlyingMap".equals( referencedFieldName );
          boolean simulatingToString = PropertynameMapToTypeAdapter.this.simulatesToString
                                       && "toString".equals( method.getName() );
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          
          boolean isMapNotNull = PropertynameMapToTypeAdapter.this.map != null;
          
          //
          if ( !accessToUnderlyingMap && !simulatingToString )
          {
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
              }
              else if ( isSetter )
              {
                //
                PropertynameMapToTypeAdapter.this.map.put( referenceFieldNameAsMapKey, args[0] );
                
                //
                retval = Void.TYPE;
              }
            }
          }
          else
          {
            //
            if ( accessToUnderlyingMap )
            {
              if ( isGetter )
              {
                //
                retval = PropertynameMapToTypeAdapter.this.map;
              }
              else if ( isSetter )
              {
                //
                PropertynameMapToTypeAdapter.this.map = (Map<Object, Object>) args[0];
                
                //
                retval = Void.TYPE;
              }
            }
            else if ( simulatingToString )
            {
              //
              Set<String> filterKeySet = BeanUtils.propertyNameSetForMethodAccess( PropertynameMapToTypeAdapter.this.clazz );
              retval = MapUtils.toString( MapUtils.filteredMap( (Map<String, Object>) PropertynameMapToTypeAdapter.this.map,
                                                                filterKeySet ) );
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
  public static <T> T newInstance( Map<? super String, Object> map, Class<? extends T> clazz, boolean underlyingMapAware )
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
  public static <T> T newInstance( Map<? super String, Object> map,
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
  public static <T> T newInstance( Map<? super String, Object> map,
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
  @SuppressWarnings("unchecked")
  public static <T> T newInstance( Map<? super String, Object> map,
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
      PropertynameMapToTypeAdapter<T, Map<Object, Object>> mapToInterfaceAdapter = new PropertynameMapToTypeAdapter<T, Map<Object, Object>>(
                                                                                                                                             (Map<Object, Object>) map,
                                                                                                                                             clazz,
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
  @SuppressWarnings({ "unchecked", "cast" })
  public static <T> T newInstance( Map<? super String, Object> map, Class<? extends T> clazz )
  {
    boolean underlyingMapAware = UnderlyingMapAware.class.isAssignableFrom( clazz );
    return PropertynameMapToTypeAdapter.newInstance( (Map<Object, Object>) map, clazz, underlyingMapAware );
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
  @SuppressWarnings({ "unchecked", "cast" })
  public static <T> T newInstance( Map<? super String, Object> map,
                                   Class<? extends T> clazz,
                                   PropertyAccessOption propertyAccessOption )
  {
    boolean underlyingMapAware = false;
    return PropertynameMapToTypeAdapter.newInstance( (Map<Object, Object>) map, clazz, underlyingMapAware, propertyAccessOption );
  }
  
  /**
   * Internal constructor. See {@link #newInstance(Map, Class)} instead.
   * 
   * @param map
   * @param clazz
   * @param underlyingMapAware
   * @param simulatesToString
   * @param propertyAccessOption
   */
  @SuppressWarnings("unchecked")
  protected PropertynameMapToTypeAdapter( M map, Class<? extends T> clazz, boolean underlyingMapAware, boolean simulatesToString,
                                          PropertyAccessOption propertyAccessOption )
  {
    //
    super();
    this.map = map;
    this.clazz = (Class<T>) clazz;
    
    this.hasAccessToUnderlyingMap = underlyingMapAware;
    this.simulatesToString = simulatesToString;
    if ( propertyAccessOption != null )
    {
      this.propertyAccessOption = propertyAccessOption;
    }
    
    //
    this.initializeClassAdapter( clazz, underlyingMapAware );
  }
  
  /**
   * Creates the stub
   * 
   * @param clazz
   * @param underlyingMapAware
   */
  protected void initializeClassAdapter( Class<? extends T> clazz, boolean underlyingMapAware )
  {
    //
    try
    {
      //       
      Class<?>[] interfaces = underlyingMapAware ? new Class[] { UnderlyingMapAware.class } : null;
      MethodInterceptor methodInterceptor = new ClassAdapterMethodInterceptor();
      this.classAdapter = StubCreator.newStubInstance( clazz, interfaces, methodInterceptor );
    }
    catch ( Exception e )
    {
    }
  }
  
}
