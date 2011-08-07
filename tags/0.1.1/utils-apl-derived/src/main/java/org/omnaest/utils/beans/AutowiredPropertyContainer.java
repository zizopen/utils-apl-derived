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
package org.omnaest.utils.beans;

import java.util.Map;
import java.util.Set;

/**
 * An {@link AutowiredPropertyContainer} is a facade for autowired properties of a Java Bean class. It allows to put property
 * objects into itself and translates the changes to an underlying Java Bean object.
 * 
 * @author Omnaest
 */
public interface AutowiredPropertyContainer extends Iterable<Object>
{
  /**
   * Adds an {@link Object} to the {@link AutowiredPropertyContainer}. This will update all properties of the underlying Java Bean
   * object which can be assigned by the given {@link Object}.
   * 
   * @param object
   * @return the number of assigned properties
   */
  public int put( Object object );
  
  /**
   * Adds an {@link Object} to the {@link AutowiredPropertyContainer}. This will update all properties of the underlying Java Bean
   * object which can be assigned by the given {@link Object}.
   * 
   * @param propertyname
   * @param object
   * @return true : assign was successful
   */
  public boolean put( String propertyname, Object object );
  
  /**
   * Returns a {@link Map} for all properties which can be assigned to the given {@link Class}. The {@link Map} has the property
   * names as key and the values of the properties as values.
   * 
   * @param <O>
   * @param clazz
   */
  public <O> Map<String, O> getPropertynameToValueMap( Class<O> clazz );
  
  /**
   * Returns a {@link Set} for all properties which can be assigned to the given {@link Class}.
   * 
   * @param <O>
   * @param clazz
   */
  public <O> Set<O> getValueSet( Class<O> clazz );
  
  /**
   * Returns the value of the property which can be assigned as value to the given {@link Class} type. If there are multiple
   * properties available with fitting types null is returned.
   * 
   * @param <O>
   * @param clazz
   * @return
   */
  public <O> O getValue( Class<O> clazz );
}
