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
package org.omnaest.utils.structure.hierarchy.nodemap;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * Helper to create {@link NodeMap} instances
 * 
 * @author Omnaest
 */
public class NodeMapFactory
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   * @param <K>
   * @param <V>
   * @param <M>
   */
  public static interface ModelAndChildExtractor<K, V, M>
  {
    public boolean isModelValue( V value );
    
    public M extractModelValue( V value );
    
    public NodeMap<K, Map<K, M>> extractChild( V value );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @param map
   * @param modelAndChildExtractor
   * @return
   */
  public static <K, V, M> NodeMap<K, Map<K, M>> newNodeMap( Map<K, V> map, ModelAndChildExtractor<K, V, M> modelAndChildExtractor )
  {
    final NodeMap<K, Map<K, M>> retmap = new NodeMapImpl<K, Map<K, M>>();
    final Map<K, M> model = new LinkedHashMap<K, M>();
    if ( map != null && modelAndChildExtractor != null )
    {
      for ( K key : map.keySet() )
      {
        final V value = map.get( key );
        final boolean isModelValue = modelAndChildExtractor.isModelValue( value );
        if ( isModelValue )
        {
          final M modelValue = modelAndChildExtractor.extractModelValue( value );
          model.put( key, modelValue );
        }
        else
        {
          final NodeMap<K, Map<K, M>> childMap = modelAndChildExtractor.extractChild( value );
          retmap.put( key, childMap );
        }
      }
    }
    retmap.setModel( model );
    return retmap;
  }
  
  /**
   * Returns a new {@link NodeMap} for a given {@link Object} which is based on the Java bean properties
   * 
   * @param object
   * @return
   */
  public static NodeMap<String, Map<String, Object>> newNodeMap( Object object )
  {
    final Map<Object, NodeMap<String, Map<String, Object>>> objectToObjectNodeMap = new IdentityHashMap<Object, NodeMap<String, Map<String, Object>>>();
    return newNodeMap( object, objectToObjectNodeMap );
  }
  
  /**
   * @see #newNodeMap(Object)
   * @param object
   * @param objectToObjectNodeMap
   * @return
   */
  private static NodeMap<String, Map<String, Object>> newNodeMap( Object object,
                                                                  final Map<Object, NodeMap<String, Map<String, Object>>> objectToObjectNodeMap )
  {
    final Map<String, Object> map = BeanUtils.propertyNameToBeanPropertyValueMap( object );
    final ModelAndChildExtractor<String, Object, Object> modelAndChildExtractor = new ModelAndChildExtractor<String, Object, Object>()
    {
      @Override
      public boolean isModelValue( Object value )
      {
        return value != null
               && ( value instanceof String || value instanceof Date || ObjectUtils.isPrimitiveOrPrimitiveWrapperType( value.getClass() ) );
      }
      
      @Override
      public Object extractModelValue( Object value )
      {
        return value;
      }
      
      @Override
      public NodeMap<String, Map<String, Object>> extractChild( Object value )
      {
        NodeMap<String, Map<String, Object>> nodeMap = objectToObjectNodeMap.get( value );
        if ( nodeMap == null )
        {
          nodeMap = newNodeMap( value, objectToObjectNodeMap );
        }
        return nodeMap;
      }
    };
    final NodeMap<String, Map<String, Object>> retmap = newNodeMap( map, modelAndChildExtractor );
    objectToObjectNodeMap.put( object, retmap );
    return retmap;
  }
  
  /**
   * Returns a new {@link NodeMap} for a given {@link Map} which contains nested further {@link Map} instances
   * 
   * @param nestedMap
   * @return
   */
  public static NodeMap<String, Map<String, Object>> newNodeMap( Map<String, Object> nestedMap )
  {
    final Map<Object, NodeMap<String, Map<String, Object>>> objectToObjectNodeMap = new IdentityHashMap<Object, NodeMap<String, Map<String, Object>>>();
    return newNodeMap( nestedMap, objectToObjectNodeMap );
  }
  
  /**
   * @see #newNodeMap(Map)
   * @param object
   * @param objectToObjectNodeMap
   * @return
   */
  private static NodeMap<String, Map<String, Object>> newNodeMap( Map<String, Object> nestedMap,
                                                                  final Map<Object, NodeMap<String, Map<String, Object>>> objectToObjectNodeMap )
  {
    final ModelAndChildExtractor<String, Object, Object> modelAndChildExtractor = new ModelAndChildExtractor<String, Object, Object>()
    {
      @Override
      public boolean isModelValue( Object value )
      {
        return !( value instanceof Map );
      }
      
      @Override
      public Object extractModelValue( Object value )
      {
        return value;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public NodeMap<String, Map<String, Object>> extractChild( Object value )
      {
        Map<String, Object> map = (Map<String, Object>) value;
        return newNodeMap( map, objectToObjectNodeMap );
      }
    };
    final NodeMap<String, Map<String, Object>> retmap = newNodeMap( nestedMap, modelAndChildExtractor );
    objectToObjectNodeMap.put( nestedMap, retmap );
    return retmap;
  }
}
