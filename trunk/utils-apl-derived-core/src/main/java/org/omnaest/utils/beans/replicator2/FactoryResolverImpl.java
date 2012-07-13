/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.beans.replicator2;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Builder;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Configuration;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see FactoryResolver
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class FactoryResolverImpl implements FactoryResolver
{
  private static final long serialVersionUID = 971678462369981695L;
  
  private static final class ArrayFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = -4135079052234294623L;
    
    private final Class<?>    type;
    
    public ArrayFactory( Class<?> type )
    {
      this.type = type;
    }
    
    @Override
    public Object newInstance( Object parameter )
    {
      return Array.newInstance( this.type, (Integer) parameter );
    }
  }
  
  private static final class ArrayListFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = -5519865687797126874L;
    
    @Override
    public Object newInstance( Object parameter )
    {
      return new ArrayList<Object>();
    }
  }
  
  private static final class LinkedHashMapFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = -42829675978851169L;
    
    @Override
    public Object newInstance( Object parameter )
    {
      return new LinkedHashMap<Object, Object>();
    }
  }
  
  private static final class TreeMapFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = 7216896364811227889L;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance( Object parameter )
    {
      return parameter instanceof Comparator ? new TreeMap<Object, Object>( (Comparator<? super Object>) parameter )
                                            : new TreeMap<Object, Object>();
    }
  }
  
  private static final class HashMapFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = 4781955850743187459L;
    
    @Override
    public Object newInstance( Object parameter )
    {
      return new HashMap<Object, Object>();
    }
  }
  
  private static final class ConcurrentHashMapFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = 4781745850743187459L;
    
    @Override
    public Object newInstance( Object parameter )
    {
      return new ConcurrentHashMap<Object, Object>();
    }
  }
  
  private static final class ConcurrentSkipListMapFactory implements FactoryParameterized<Object, Object>, Serializable
  {
    private static final long serialVersionUID = -8296675902240723281L;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance( Object parameter )
    {
      return parameter instanceof Comparator ? new ConcurrentSkipListMap<Object, Object>( (Comparator<? super Object>) parameter )
                                            : new ConcurrentSkipListMap<Object, Object>();
    }
  }
  
  private Map<Class<?>, FactoryParameterized<Object, Object>> typeToFactoryMap = newInitializedTypeToFactoryMap();
  
  private static Map<Class<?>, FactoryParameterized<Object, Object>> newInitializedTypeToFactoryMap()
  {
    return new MapBuilder<Class<?>, FactoryParameterized<Object, Object>>().concurrentHashMap()
                                                                           .put( HashMap.class, new HashMapFactory() )
                                                                           .put( ConcurrentHashMap.class,
                                                                                 new ConcurrentHashMapFactory() )
                                                                           .put( ConcurrentSkipListMap.class,
                                                                                 new ConcurrentSkipListMapFactory() )
                                                                           .build();
  }
  
  @Override
  public FactoryParameterized<Object, Object> resolveFactory( final Class<?> type )
  {
    FactoryParameterized<Object, Object> factory = this.typeToFactoryMap.get( type );
    if ( factory == null )
    {
      if ( SortedMap.class.isAssignableFrom( type ) )
      {
        factory = new TreeMapFactory();
      }
      else if ( Map.class.isAssignableFrom( type ) )
      {
        factory = new LinkedHashMapFactory();
      }
      else if ( List.class.isAssignableFrom( type ) )
      {
        factory = new ArrayListFactory();
      }
      else if ( ArrayUtils.isArrayType( type ) )
      {
        factory = new ArrayFactory( type );
      }
      else if ( type.isInterface() )
      {
        factory = new FactoryParameterized<Object, Object>()
        {
          private Builder<?> builder = PropertynameMapToTypeAdapter.builder( type, new Configuration() );
          
          @Override
          public Object newInstance( Object parameter )
          {
            return this.builder.newTypeAdapter( new HashMap<String, Object>() );
          }
        };
      }
      else
      {
        factory = new FactoryParameterized<Object, Object>()
        {
          @Override
          public Object newInstance( Object parameter )
          {
            return ReflectionUtils.newInstanceOf( type );
          }
        };
      }
      
      this.typeToFactoryMap.put( type, factory );
    }
    return factory;
  }
  
}
