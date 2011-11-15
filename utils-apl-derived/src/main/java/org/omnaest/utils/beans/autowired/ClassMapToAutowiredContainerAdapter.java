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
import java.util.LinkedHashMap;
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
   * Creates a new instance of a {@link AutowiredContainer} using a regular {@link LinkedHashMap}
   * 
   * @return
   */
  public static <E> AutowiredContainer<E> newInstanceUsingLinkedHashMap()
  {
    return newInstance( new LinkedHashMap<Class<? extends E>, E>() );
  }
  
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
  public <O extends E> Set<O> getValueSet( Class<? extends O> type )
  {
    //    
    Set<O> retset = new LinkedHashSet<O>();
    
    //
    if ( type != null )
    {
      for ( Class<? extends E> iType : this.classToObjectMap.keySet() )
      {
        if ( iType != null && type.isAssignableFrom( iType ) )
        {
          retset.add( (O) this.classToObjectMap.get( iType ) );
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
    return put( object, object != null ? (Class<E>) object.getClass() : null );
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.classToObjectMap.values().iterator();
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <O extends E> int put( O object, Class<? extends O>... types )
  {
    //
    int retval = 0;
    
    //
    if ( object != null && types.length > 0 )
    {
      for ( Class<?> type : types )
      {
        if ( type != null )
        {
          //
          E previous = this.classToObjectMap.put( (Class<E>) type, object );
          retval += ( previous != null ) ? 1 : 0;
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.classToObjectMap == null ) ? 0 : this.classToObjectMap.hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof ClassMapToAutowiredContainerAdapter ) )
    {
      return false;
    }
    ClassMapToAutowiredContainerAdapter<?> other = (ClassMapToAutowiredContainerAdapter<?>) obj;
    if ( this.classToObjectMap == null )
    {
      if ( other.classToObjectMap != null )
      {
        return false;
      }
    }
    else if ( !this.classToObjectMap.equals( other.classToObjectMap ) )
    {
      return false;
    }
    return true;
  }
  
}
