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
package org.omnaest.utils.beans.replicator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Builder;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Configuration;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * @see FactoryResolver
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class FactoryResolverImpl implements FactoryResolver
{
  /* ************************************************** Constants *************************************************** */
  private static final long              serialVersionUID = 971678462369981695L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<Class<?>, InstanceFactory> typeToFactoryMap = newInitializedTypeToFactoryMap();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static final class LinkedHashSetFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -6895746761649478523L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new LinkedHashSet<Object>();
    }
  }
  
  private static final class HashSetFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -689574345649478523L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new HashSet<Object>();
    }
  }
  
  private static final class ArrayFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -4135079052234294623L;
    
    private final Class<?>    type;
    
    public ArrayFactory( Class<?> type )
    {
      this.type = type;
    }
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      Assert.isNotNull( parameterMap,
                        "Factory for arrays is incompatible with the respective source instance accessor. The instance accessor has to provide a parameter map containing the 'size' key with the expected size of the array" );
      final Integer size = (Integer) parameterMap.get( "size" );
      return Array.newInstance( ArrayUtils.componentType( this.type ), size );
    }
  }
  
  private static final class ArrayListFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -5519865687797126874L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new ArrayList<Object>();
    }
  }
  
  private static final class LinkedHashMapFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -42829675978851169L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new LinkedHashMap<Object, Object>();
    }
  }
  
  private static final class TreeMapFactory implements InstanceFactory
  {
    private static final long serialVersionUID = 7216896364811227889L;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      final Comparator<Object> comparator = parameterMap != null ? (Comparator<Object>) parameterMap.get( "comparator" ) : null;
      return comparator != null ? new TreeMap<Object, Object>( comparator ) : new TreeMap<Object, Object>();
    }
  }
  
  private static final class TreeSetFactory implements InstanceFactory
  {
    private static final long serialVersionUID = 4609633205635635614L;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      final Comparator<Object> comparator = parameterMap != null ? (Comparator<Object>) parameterMap.get( "comparator" ) : null;
      return comparator != null ? new TreeSet<Object>( comparator ) : new TreeSet<Object>();
    }
  }
  
  private static final class HashMapFactory implements InstanceFactory
  {
    private static final long serialVersionUID = 4781955850743187459L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new HashMap<Object, Object>();
    }
  }
  
  private static final class ConcurrentHashMapFactory implements InstanceFactory
  {
    private static final long serialVersionUID = 4781745850743187459L;
    
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      return new ConcurrentHashMap<Object, Object>();
    }
  }
  
  private static final class ConcurrentSkipListMapFactory implements InstanceFactory
  {
    private static final long serialVersionUID = -8296675902240723281L;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance( Map<String, Object> parameterMap )
    {
      final Comparator<Object> comparator = (Comparator<Object>) ( parameterMap != null ? parameterMap.get( "comparator" ) : null );
      return comparator != null ? new ConcurrentSkipListMap<Object, Object>( comparator )
                               : new ConcurrentSkipListMap<Object, Object>();
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  private static Map<Class<?>, InstanceFactory> newInitializedTypeToFactoryMap()
  {
    return MapUtils.builder()
                   .<Class<?>, InstanceFactory> put( HashMap.class, new HashMapFactory() )
                   .put( LinkedHashMap.class, new LinkedHashMapFactory() )
                   .put( ConcurrentHashMap.class, new ConcurrentHashMapFactory() )
                   .put( ConcurrentSkipListMap.class, new ConcurrentSkipListMapFactory() )
                   .put( HashSet.class, new HashSetFactory() )
                   .put( TreeSet.class, new TreeSetFactory() )
                   .put( LinkedHashSet.class, new LinkedHashSetFactory() )
                   .buildAs()
                   .concurrentHashMap();
  }
  
  @Override
  public InstanceFactory resolveFactory( final Class<?> type )
  {
    InstanceFactory factory = this.typeToFactoryMap.get( type );
    if ( factory == null )
    {
      if ( MapUtils.isSortedMapType( type ) )
      {
        factory = new TreeMapFactory();
      }
      else if ( MapUtils.isMapType( type ) )
      {
        factory = new LinkedHashMapFactory();
      }
      else if ( ListUtils.isListType( type ) )
      {
        factory = new ArrayListFactory();
      }
      else if ( ArrayUtils.isArrayType( type ) )
      {
        factory = new ArrayFactory( type );
      }
      else if ( SetUtils.isSortedSetType( type ) )
      {
        factory = new TreeSetFactory();
      }
      else if ( SetUtils.isSetType( type ) )
      {
        factory = new LinkedHashSetFactory();
      }
      else if ( IterableUtils.isIterableType( type ) )
      {
        factory = new ArrayListFactory();
      }
      else if ( type.isInterface() )
      {
        factory = new InstanceFactory()
        {
          private static final long serialVersionUID = -3100752693360156274L;
          
          private Builder<?>        builder          = PropertynameMapToTypeAdapter.builder( type, new Configuration() );
          
          @Override
          public Object newInstance( Map<String, Object> parameterMap )
          {
            return this.builder.newTypeAdapter( new HashMap<String, Object>() );
          }
        };
      }
      else
      {
        factory = new InstanceFactory()
        {
          private static final long serialVersionUID = -5458144693782050218L;
          
          @Override
          public Object newInstance( Map<String, Object> parameterMap )
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
