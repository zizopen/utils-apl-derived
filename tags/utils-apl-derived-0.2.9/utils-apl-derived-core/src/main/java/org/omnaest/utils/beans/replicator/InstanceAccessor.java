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

import java.io.Serializable;
import java.util.Map;

import org.omnaest.utils.structure.element.factory.FactoryParameterized;

/**
 * @author Omnaest
 */
interface InstanceAccessor extends Serializable
{
  /**
   * @see PropertyAccessor
   * @param propertyName
   * @param instance
   * @return
   */
  @SuppressWarnings("javadoc")
  public PropertyAccessor getPropertyAccessor( String propertyName, Object instance );
  
  /**
   * @param instance
   * @return
   */
  public Iterable<String> getPropertyNameIterable( Object instance );
  
  /**
   * @return
   */
  public Class<?> getType();
  
  /**
   * Determines a implementation specific {@link Map} of meta parameters allowing any {@link FactoryParameterized} instance to
   * create related instances
   * 
   * @param instance
   * @return
   */
  public Map<String, Object> determineFactoryMetaInformation( Object instance );
}
