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
package org.omnaest.utils.beans.replicator.adapter;

import java.util.Map;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;
import org.omnaest.utils.structure.element.factory.concrete.FactoryTypeAwareReflectionBased;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * This {@link Adapter} utilizes a given {@link Map} which contains mappings of {@link Class} types. The key represents the source
 * type and the value the target type. <br>
 * <br>
 * As default the inverted direction is included, too. To disable this behavior use
 * {@link #AdapterSourceToTargetTypeMapBased(Map, boolean)}<br>
 * <br>
 * This {@link Adapter} utilizes the {@link AdapterSourceToTargetFactoryMapBased} and performs its operations as fast as this
 * {@link Adapter}.
 * 
 * @see Adapter
 * @see AdapterSourceToTargetFactoryMapBased
 * @see BeanReplicator
 * @author Omnaest
 */
public class AdapterSourceToTargetTypeMapBased extends AdapterDecorator
{
  /* ********************************************** Constants ********************************************** */
  private final static ElementConverter<Class<?>, FactoryTypeAware<?>> valueElementConverter = new ElementConverter<Class<?>, FactoryTypeAware<?>>()
                                                                                             {
                                                                                               @Override
                                                                                               public FactoryTypeAware<?> convert( Class<?> type )
                                                                                               {
                                                                                                 return new FactoryTypeAwareReflectionBased<Object>(
                                                                                                                                                     type );
                                                                                               }
                                                                                             };
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see AdapterSourceToTargetTypeMapBased
   * @param sourceToTargetTypeMap
   */
  public AdapterSourceToTargetTypeMapBased( Map<Class<?>, Class<?>> sourceToTargetTypeMap )
  {
    this( sourceToTargetTypeMap, true );
  }
  
  /**
   * @see AdapterSourceToTargetTypeMapBased
   * @param sourceToTargetTypeMap
   * @param usingBothDirections
   *          if true key to value as well as value to key pairs are used as source to target {@link Class} type bindings
   */
  public AdapterSourceToTargetTypeMapBased( Map<Class<?>, Class<?>> sourceToTargetTypeMap, boolean usingBothDirections )
  {
    super(
           new AdapterSourceToTargetFactoryMapBased( MapUtils.convertMap( sourceToTargetTypeMap, valueElementConverter,
                                                                          valueElementConverter ) ).setUsingBothDirections( usingBothDirections ) );
    Assert.isNotNull( sourceToTargetTypeMap, "sourceToTargetTypeMap must not be null" );
  }
  
}
