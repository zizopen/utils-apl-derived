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
package org.omnaest.utils.beans.replicator.adapter.helper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.tuple.TupleTwo;

public class BeanPropertiesAutowireHelper
{
  /**
   * Returns a {@link Map} containing the propety name of all matching properties as keys and the source to target
   * {@link TupleTwo} for which the match has been found.
   * 
   * @param beanPropertyAccessorSetSource
   * @param beanPropertyAccessorSetTarget
   * @return
   */
  public static Map<String, TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>> determineMatchingPropertyNameToBeanPropertyAccessorSourceToTargetTupleMap( Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSetSource,
                                                                                                                                                                             Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSetTarget )
  {
    //      
    final Map<String, TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>> retmap = new LinkedHashMap<String, TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>>();
    
    //
    if ( beanPropertyAccessorSetSource != null && beanPropertyAccessorSetTarget != null )
    {
      //
      final KeyExtractor<String, BeanPropertyAccessor<Object>> keyExtractor = new KeyExtractor<String, BeanPropertyAccessor<Object>>()
      {
        @Override
        public String extractKey( BeanPropertyAccessor<Object> beanPropertyAccessor )
        {
          // 
          return beanPropertyAccessor.getPropertyName();
        }
      };
      
      //
      final Map<String, BeanPropertyAccessor<Object>> propertyNameToBeanPropertyAccessorMapForSource = ListUtils.toMap( keyExtractor,
                                                                                                                        beanPropertyAccessorSetSource );
      
      final Map<String, BeanPropertyAccessor<Object>> propertyNameToBeanPropertyAccessorMapForTarget = ListUtils.toMap( keyExtractor,
                                                                                                                        beanPropertyAccessorSetTarget );
      
      retmap.putAll( MapUtils.innerJoinMapByKey( propertyNameToBeanPropertyAccessorMapForSource,
                                                 propertyNameToBeanPropertyAccessorMapForTarget ) );
    }
    
    //
    return retmap;
  }
  
  @SuppressWarnings("unchecked")
  public static void copyProperties( Object sourceObject,
                                     Object targetObject,
                                     TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    //
    if ( sourceObject != null && targetObject != null )
    {
      //
      final Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSetTarget = BeanUtils.beanPropertyAccessorSet( (Class<Object>) targetObject.getClass() );
      final Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSetSource = BeanUtils.beanPropertyAccessorSet( (Class<Object>) sourceObject.getClass() );
      
      //
      Map<String, TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>> matchingPropertyNameToBeanPropertyAccessorSourceToTargetTupleMap = BeanPropertiesAutowireHelper.determineMatchingPropertyNameToBeanPropertyAccessorSourceToTargetTupleMap( beanPropertyAccessorSetSource,
                                                                                                                                                                                                                                                                   beanPropertyAccessorSetTarget );
      
      //
      final Set<BeanPropertyAccessor<Object>> unhandledBeanPropertyAccessorSet = new LinkedHashSet<BeanPropertyAccessor<Object>>();
      for ( String propertyName : matchingPropertyNameToBeanPropertyAccessorSourceToTargetTupleMap.keySet() )
      {
        //
        final TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>> beanPropertyAccessorSourceToTargetTuple = matchingPropertyNameToBeanPropertyAccessorSourceToTargetTupleMap.get( propertyName );
        final BeanPropertyAccessor<Object> beanPropertyAccessorSource = beanPropertyAccessorSourceToTargetTuple.getValueFirst();
        final BeanPropertyAccessor<Object> beanPropertyAccessorTarget = beanPropertyAccessorSourceToTargetTuple.getValueSecond();
        
        //
        if ( beanPropertyAccessorSource != null && beanPropertyAccessorTarget != null )
        {
          //
          Object propertyValue = beanPropertyAccessorSource.getPropertyValue( sourceObject );
          
          //
          if ( propertyValue != null )
          {
            //
            boolean isPrimitiveOrPrimitiveWrapperTypeOrString = ObjectUtils.isPrimitiveOrPrimitiveWrapperType( propertyValue.getClass() )
                                                                || ObjectUtils.isString( propertyValue );
            if ( !isPrimitiveOrPrimitiveWrapperTypeOrString )
            {
              propertyValue = transitiveBeanReplicationInvocationHandler.replicate( propertyValue );
            }
          }
          
          //
          beanPropertyAccessorTarget.setPropertyValue( targetObject, propertyValue );
        }
        else
        {
          unhandledBeanPropertyAccessorSet.add( beanPropertyAccessorSource );
        }
      }
      
      //
      final BeanPropertyAccessors<Object> beanPropertyAccessors = new BeanPropertyAccessors<Object>(
                                                                                                     unhandledBeanPropertyAccessorSet );
      transitiveBeanReplicationInvocationHandler.notifyOfUnhandledProperties( beanPropertyAccessors );
    }
  }
}
