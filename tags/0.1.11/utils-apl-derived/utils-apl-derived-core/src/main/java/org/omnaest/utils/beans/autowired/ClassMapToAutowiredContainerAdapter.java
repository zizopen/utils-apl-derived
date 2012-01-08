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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Adapter to allow {@link Map}s to be treated as {@link AutowiredContainer}. The key of the {@link Map} will be the {@link Class}
 * of an element and the value will be the element itself.
 * 
 * @see AutowiredContainer
 * @author Omnaest
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassMapToAutowiredContainerAdapter<E> extends AutowiredContainerAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long          serialVersionUID = -6432970316176438546L;
  
  /* ********************************************** Variables ********************************************** */
  @XmlElementWrapper(name = "entries")
  private Map<Class<? extends E>, E> classToObjectMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new instance of a {@link AutowiredContainer} using a regular {@link LinkedHashMap}
   * 
   * @return
   */
  public static <E> AutowiredContainer<E> newInstanceUsingLinkedHashMap()
  {
    return newInstance( null );
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
    retval = new ClassMapToAutowiredContainerAdapter<E>( classToObjectMap );
    
    //
    return retval;
  }
  
  /**
   * @param classToObjectMap
   */
  protected ClassMapToAutowiredContainerAdapter( Map<Class<? extends E>, E> classToObjectMap )
  {
    super();
    this.classToObjectMap = classToObjectMap != null ? classToObjectMap : new LinkedHashMap<Class<? extends E>, E>();
  }
  
  /**
   * @see ClassMapToAutowiredContainerAdapter
   */
  protected ClassMapToAutowiredContainerAdapter()
  {
    this( null );
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
      //
      Set<Class<? extends E>> assignableTypeSet = this.determineAssignableTypeSet( type );
      for ( Class<? extends E> iType : assignableTypeSet )
      {
        retset.add( (O) this.classToObjectMap.get( iType ) );
      }
    }
    
    //
    return retset;
  }
  
  /**
   * @param type
   * @return
   */
  protected Set<Class<? extends E>> determineAssignableTypeSet( Class<? extends E> type )
  {
    //    
    Set<Class<? extends E>> retset = new HashSet<Class<? extends E>>();
    
    //
    if ( type != null )
    {
      for ( Class<? extends E> iType : this.classToObjectMap.keySet() )
      {
        if ( iType != null && type.isAssignableFrom( iType ) )
        {
          retset.add( iType );
        }
      }
    }
    
    //
    return retset;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public AutowiredContainer<E> put( E object )
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
  public <O extends E> AutowiredContainer<E> put( O object, Class<? extends O>... types )
  {
    //
    if ( object != null && types.length > 0 )
    {
      for ( Class<?> type : types )
      {
        if ( type != null )
        {
          //
          this.classToObjectMap.put( (Class<E>) type, object );
        }
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public AutowiredContainer<E> remove( Class<? extends E> type )
  {
    
    //
    Set<Class<? extends E>> assignableTypeSet = this.determineAssignableTypeSet( type );
    if ( !assignableTypeSet.isEmpty() )
    {
      for ( Class<? extends E> iType : assignableTypeSet )
      {
        this.classToObjectMap.remove( iType );
      }
    }
    
    // 
    return this;
  }
}
