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
package org.omnaest.utils.beans.autowired;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Adapter to allow {@link Map}s to be treated as {@link AutowiredContainer}. The key of the {@link Map} will be the {@link Class}
 * of an element and the value will be the element itself.
 * 
 * @see AutowiredContainer
 * @author Omnaest
 */
public class ClassMapToAutowiredContainerAdapter<E> extends AutowiredContainerAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long          serialVersionUID = -6432970316176438546L;
  
  /* ********************************************** Variables ********************************************** */
  private Map<Class<? extends E>, E> classToObjectMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new instance of a {@link AutowiredContainer} for a given {@link Map}
   * 
   * @param classToObjectMap
   * @return
   */
  public static <E> AutowiredContainer<E> newInstance( Map<Class<? extends E>, E> classToObjectMap )
  {
    //
    AutowiredContainer<E> retval = null;
    
    //
    if ( classToObjectMap != null )
    {
      retval = new ClassMapToAutowiredContainerAdapter<E>( classToObjectMap );
    }
    
    //
    return retval;
  }
  
  /**
   * @param classToObjectMap
   */
  protected ClassMapToAutowiredContainerAdapter( Map<Class<? extends E>, E> classToObjectMap )
  {
    super();
    this.classToObjectMap = classToObjectMap;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <O extends E> Set<O> getValueSet( Class<O> clazz )
  {
    //    
    Set<O> retset = new LinkedHashSet<O>();
    
    //
    if ( clazz != null )
    {
      for ( Class<? extends E> iclazz : this.classToObjectMap.keySet() )
      {
        if ( iclazz != null && clazz.isAssignableFrom( iclazz ) )
        {
          retset.add( (O) this.classToObjectMap.get( iclazz ) );
        }
      }
    }
    
    //
    return retset;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public int put( E object )
  {
    //
    int retval = 0;
    
    //
    if ( object != null )
    {
      this.classToObjectMap.put( (Class<? extends E>) object.getClass(), object );
      retval++;
    }
    
    // 
    return retval;
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.classToObjectMap.values().iterator();
  }
  
}
