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
import java.util.Set;
import java.util.regex.Pattern;

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
  
  /**
   * Returns a new {@link NodeMap} based on a normal {@link Map} containing hierarchical structured keys. A hierarchical key could
   * be e.g. node1.subnode1.subsubnode1 where '.' is the delimiter. Note: the point has to be quoted actually using
   * {@link Pattern#quote(String)}.
   * 
   * @see Pattern#quote(String)
   * @param map
   *          {@link Map}
   * @param delimiterRegEx
   * @return
   */
  public static <V> NodeMap<String, V> newNodeMapFromHierarchicalKeyMap( Map<String, V> map, String delimiterRegEx )
  {
    NodeMap<String, V> retmap = new NodeMapImpl<String, V>();
    
    if ( map != null )
    {
      for ( String key : map.keySet() )
      {
        if ( key != null )
        {
          V value = map.get( key );
          
          String[] tokens = key.split( delimiterRegEx );
          if ( tokens != null )
          {
            NodeMap<String, V> currentNodeMap = retmap;
            
            if ( tokens.length > 1 )
            {
              for ( String token : tokens )
              {
                if ( token != null )
                {
                  NodeMap<String, V> nodeMap = currentNodeMap.get( token );
                  if ( nodeMap == null )
                  {
                    NodeMap<String, V> newNodeMap = new NodeMapImpl<String, V>();
                    currentNodeMap.put( token, newNodeMap );
                    currentNodeMap = newNodeMap;
                  }
                  else
                  {
                    currentNodeMap = nodeMap;
                  }
                }
              }
            }
            
            if ( currentNodeMap != null )
            {
              currentNodeMap.setModel( value );
            }
            
          }
        }
      }
    }
    
    return retmap;
  }
  
  /**
   * Converts a {@link NodeMap} into a normal {@link Map} with hierarchical keys.<br>
   * Example:
   * 
   * <pre>
   * parent +
   *        |
   *        +-subnode1
   *        +-subnode2
   * </pre>
   * 
   * results in<br>
   * parent.subnode1=xyz<br>
   * parent.subnode2=xyz
   * 
   * @param nodeMap
   * @param delimiter
   * @return
   */
  public static <M> Map<String, M> convertNodeMapToHierarchicalKeyMap( NodeMap<String, M> nodeMap, String delimiter )
  {
    final String parent = null;
    final Map<String, M> retmap = new LinkedHashMap<String, M>();
    convertNodeMapToHierarchicalKeyMap( nodeMap, parent, retmap, delimiter );
    return retmap;
  }
  
  private static <M> void convertNodeMapToHierarchicalKeyMap( NodeMap<String, M> nodeMap,
                                                              String parent,
                                                              Map<String, M> retmap,
                                                              String delimiter )
  {
    if ( nodeMap != null )
    {
      if ( parent != null )
      {
        M model = nodeMap.getModel();
        if ( model != null )
        {
          retmap.put( parent, model );
        }
      }
      
      Set<String> keySet = nodeMap.keySet();
      for ( String key : keySet )
      {
        final NodeMap<String, M> subNodeMap = nodeMap.get( key );
        convertNodeMapToHierarchicalKeyMap( subNodeMap, ( parent != null ? parent + delimiter : "" ) + key, retmap, delimiter );
      }
    }
  }
  
}
