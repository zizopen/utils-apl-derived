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

import java.util.Map;

/**
 * An {@link AutowiredPropertyContainer} is a facade for autowired properties of a Java Bean class. It allows to put property
 * objects into itself and translates the changes to an underlying Java Bean object.
 * 
 * @see AutowiredContainer
 * @author Omnaest
 */
public interface AutowiredPropertyContainer extends AutowiredContainer<Object>
{
  /**
   * Returns a {@link Map} for all properties which can be assigned to the given {@link Class}. The {@link Map} has the property
   * names as key and the values of the properties as values.
   * 
   * @param <O>
   * @param clazz
   */
  public <O> Map<String, O> getPropertynameToValueMap( Class<O> clazz );
  
  /**
   * Adds an {@link Object} to the {@link AutowiredPropertyContainer}. This will update all properties of the underlying Java Bean
   * object which can be assigned by the given {@link Object}.
   * 
   * @param propertyname
   * @param object
   * @return this
   */
  public AutowiredPropertyContainer put( String propertyname, Object object );
}
