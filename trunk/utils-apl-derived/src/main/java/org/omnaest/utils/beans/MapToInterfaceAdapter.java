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
 * This class allows to link the key and values of an arbitrary {@link Map} instance to a given {@link Class} schemata.
 * 
 * @author Omnaest
 * @see #newInstance(Map, Class)
 * @param <T>
 * @param <M>
 */
@SuppressWarnings("rawtypes")
public class MapToInterfaceAdapter<T, M extends Map>
{
  /* ********************************************** Variables ********************************************** */
  protected M map          = null;
  protected T classAdapter = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * A {@link MethodInterceptor} implementation special for this {@link MapToInterfaceAdapter}
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
      BeanMethodNameUtils.BeanMethodInformation beanMethodInformation = BeanMethodNameUtils.determineBeanMethodInformation( method );
      if ( beanMethodInformation != null )
      {
        if ( beanMethodInformation.isGetter() )
        {
          retval = MapToInterfaceAdapter.this.map.get( beanMethodInformation.getReferencedFieldName() );
        }
        else if ( beanMethodInformation.isSetter() && args.length == 1 )
        {
          //
          MapToInterfaceAdapter.this.map.put( beanMethodInformation.getReferencedFieldName(), args[0] );
          
          //
          retval = Void.TYPE;
        }
      }
      
      // 
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Factory methods to create a new {@link MapToInterfaceAdapter} for a given {@link Map} with the given {@link Class} as facade.
   * 
   * @param map
   * @param clazz
   */
  public static <T, M extends Map> T newInstance( M map, Class<? extends T> clazz )
  {
    //    
    T retval = null;
    
    //
    MapToInterfaceAdapter<T, M> mapToInterfaceAdapter = new MapToInterfaceAdapter<T, M>( map, clazz );
    retval = mapToInterfaceAdapter.classAdapter;
    
    //
    return retval;
  }
  
  protected MapToInterfaceAdapter( M map, Class<? extends T> clazz )
  {
    //
    super();
    this.map = map;
    
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
      
      this.classAdapter = (T) enhancer.create();
    }
    catch ( Exception e )
    {
    }
  }
  
  public M getMap()
  {
    return this.map;
  }
  
  public void setMap( M map )
  {
    this.map = map;
  }
  
}
