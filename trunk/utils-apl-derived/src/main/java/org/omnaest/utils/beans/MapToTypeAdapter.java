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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator;

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
public class MapToTypeAdapter<T, M extends Map<String, ?>>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<String, Object> map                      = null;
  protected T                   classAdapter             = null;
  protected boolean             hasAccessToUnderlyingMap = false;
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * This interface makes a derivative type aware of an underlying map implementation. This is normally used in combination with
   * an {@link MapToTypeAdapter}.
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
   * A {@link MethodInterceptor} implementation special for this {@link MapToTypeAdapter}
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
        BeanMethodInformation beanMethodInformation = BeanUtils.determineBeanMethodInformation( method );
        if ( beanMethodInformation != null )
        {
          //
          boolean accessToUnderlyingMap = MapToTypeAdapter.this.hasAccessToUnderlyingMap
                                          && "underlyingMap".equals( beanMethodInformation.getReferencedFieldName() );
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          
          boolean isMapNotNull = MapToTypeAdapter.this.map != null;
          
          //
          if ( !accessToUnderlyingMap )
          {
            if ( isMapNotNull )
            {
              if ( isGetter )
              {
                //
                retval = MapToTypeAdapter.this.map.get( beanMethodInformation.getReferencedFieldName() );
              }
              else if ( isSetter )
              {
                //
                MapToTypeAdapter.this.map.put( beanMethodInformation.getReferencedFieldName(), args[0] );
                
                //
                retval = Void.TYPE;
              }
            }
          }
          else
          {
            if ( isGetter )
            {
              //
              retval = MapToTypeAdapter.this.map;
            }
            else if ( isSetter )
            {
              //
              MapToTypeAdapter.this.map = (Map<String, Object>) args[0];
              
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
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Factory methods to create a new {@link MapToTypeAdapter} for a given {@link Map} with the given {@link Class} as facade.
   * 
   * @see #newInstance(Map, Class)
   * @param map
   * @param clazz
   * @param underlyingMapAware
   *          : true > returned stub implements {@link UnderlyingMapAware}
   * @return new
   */
  public static <T> T newInstance( Map<String, ?> map, Class<? extends T> clazz, boolean underlyingMapAware )
  {
    //    
    T retval = null;
    
    //
    if ( clazz != null && map != null )
    {
      //
      MapToTypeAdapter<T, Map<String, ?>> mapToInterfaceAdapter = new MapToTypeAdapter<T, Map<String, ?>>( map, clazz,
                                                                                                           underlyingMapAware );
      
      //
      retval = mapToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * Factory methods to create a new {@link MapToTypeAdapter} for a given {@link Map} with the given {@link Class} as facade.
   * 
   * @see #newInstance(Map, Class, boolean)
   * @param map
   * @param clazz
   * @return new
   */
  public static <T> T newInstance( Map<String, ?> map, Class<? extends T> clazz )
  {
    boolean underlyingMapAware = false;
    return MapToTypeAdapter.newInstance( map, clazz, underlyingMapAware );
  }
  
  /**
   * Internal constructor. See {@link #newInstance(Map, Class)} instead.
   * 
   * @param map
   * @param clazz
   * @param underlyingMapAware
   */
  @SuppressWarnings("unchecked")
  protected MapToTypeAdapter( M map, Class<? extends T> clazz, boolean underlyingMapAware )
  {
    //
    super();
    this.map = (Map<String, Object>) map;
    
    this.hasAccessToUnderlyingMap = underlyingMapAware;
    
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
