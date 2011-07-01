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

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * This class creates a proxy implementation for a given class or interface type which is used as a facade to an underlying
 * Map&lt;String,?&gt;.
 * 
 * @author Omnaest
 * @see #newInstance(Map, Class)
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
   * @param map
   * @param clazz
   */
  public static <T, M extends Map<String, ?>> T newInstance( M map, Class<? extends T> clazz )
  {
    //    
    T retval = null;
    
    //
    if ( clazz != null && ( map != null || MapToTypeAdapter.isAssignableFromUnderlyingMapAwareInterface( clazz ) ) )
    {
      //
      MapToTypeAdapter<T, M> mapToInterfaceAdapter = new MapToTypeAdapter<T, M>( map, clazz );
      retval = mapToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  protected MapToTypeAdapter( M map, Class<? extends T> clazz )
  {
    //
    super();
    this.map = (Map<String, Object>) map;
    
    this.hasAccessToUnderlyingMap = MapToTypeAdapter.isAssignableFromUnderlyingMapAwareInterface( clazz );
    
    //
    this.initializeClassAdapter( clazz );
  }
  
  @SuppressWarnings("unchecked")
  protected void initializeClassAdapter( Class<? extends T> clazz )
  {
    //
    try
    {
      //      
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass( clazz );
      Callback callback = new ClassAdapterMethodInterceptor();
      enhancer.setCallback( callback );
      
      //
      this.classAdapter = (T) enhancer.create();
    }
    catch ( Exception e )
    {
    }
  }
  
  protected static boolean isAssignableFromUnderlyingMapAwareInterface( Class<?> clazz )
  {
    return clazz != null && UnderlyingMapAware.class.isAssignableFrom( clazz );
  }
  
}
