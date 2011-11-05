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

import java.io.Serializable;
import java.util.Set;

/**
 * An {@link AutowiredContainer} provides a ordered collection of objects. All objects can be retrieved by {@link Class}es they
 * are assignable to.
 * 
 * @author Omnaest
 */
public interface AutowiredContainer<E> extends Iterable<E>, Serializable
{
  
  /**
   * Returns the value which can be assigned as value to the given {@link Class} type. If there are multiple values available with
   * fitting types null is returned.
   * 
   * @param <O>
   * @param type
   * @return
   */
  public <O extends E> O getValue( Class<O> type );
  
  /**
   * Returns a {@link Set} for all values which can be assigned to the given {@link Class}.
   * 
   * @param <O>
   * @param type
   */
  public <O extends E> Set<O> getValueSet( Class<O> type );
  
  /**
   * Returns true if the current container contains any type which can be assigned to the given one.
   * 
   * @param type
   * @return
   */
  public <O extends E> boolean containsAssignable( Class<O> type );
  
  /**
   * Adds an {@link Object} to the {@link AutowiredContainer}.
   * 
   * @see #putAll(Iterable)
   * @param object
   * @return the number of assigned values within the underlying structure
   */
  public int put( E object );
  
  /**
   * Adds multiple {@link Object}s to the {@link AutowiredContainer}.
   * 
   * @see #put(Object)
   * @param iterable
   * @return
   */
  public int putAll( Iterable<E> iterable );
  
  /**
   * Returns true if there are no elements within this container.
   * 
   * @return
   */
  public boolean isEmpty();
  
}
