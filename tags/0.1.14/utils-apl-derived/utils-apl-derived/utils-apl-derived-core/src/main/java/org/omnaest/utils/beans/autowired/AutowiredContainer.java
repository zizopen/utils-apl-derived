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

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * An {@link AutowiredContainer} provides a ordered collection of objects. All objects can be retrieved by {@link Class} types
 * they are assignable to.<br>
 * <br>
 * This results in the use of interfaces or classes as keys when object instances are put into the {@link AutowiredContainer}.<br>
 * <br>
 * Usage example:<br>
 * 
 * <pre>
 * public interface ExampleInterface
 * {
 * }
 * 
 * public class Example extends ExampleInterface
 * {
 * }
 * </pre>
 * 
 * <pre>
 * {
 *   Example example = new Example();
 *   autowiredContainer.put( example );
 *   
 *   assertEquals( 1, autowiredContainer.getValueSet( Example.class ).size() );
 *   assertEquals( example, autowiredContainer.getValue( Example.class ) );
 *   
 *   assertEquals( 1, autowiredContainer.getValueSet( ExampleInterface.class ).size() );
 *   assertEquals( example, autowiredContainer.getValue( ExampleInterface.class ) );
 * }
 * </pre>
 * 
 * @see AutowiredContainerUtils
 * @see ListUtils#valueOf(Iterable)
 * @see SetUtils#valueOf(Iterable)
 * @see AutowiredContainerAbstract
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
  public <O extends E> O getValue( Class<? extends O> type );
  
  /**
   * Returns a {@link Set} for all values which can be assigned to the given {@link Class}.
   * 
   * @param <O>
   * @param type
   */
  public <O extends E> Set<O> getValueSet( Class<? extends O> type );
  
  /**
   * Returns true if the current container contains any type which can be assigned to the given one.
   * 
   * @param type
   * @return
   */
  public <O extends E> boolean containsAssignable( Class<O> type );
  
  /**
   * Returns true if there are no elements within this container.
   * 
   * @return
   */
  public boolean isEmpty();
  
  /**
   * Adds an {@link Object} to the {@link AutowiredContainer}.
   * 
   * @see #put(Object, Class...)
   * @see #putAll(Iterable)
   * @param object
   * @return this
   */
  public AutowiredContainer<E> put( E object );
  
  /**
   * Adds multiple {@link Object}s to the {@link AutowiredContainer}.
   * 
   * @see #put(Object)
   * @param iterable
   * @return this
   */
  public AutowiredContainer<E> putAll( Iterable<E> iterable );
  
  /**
   * Adds an {@link Object} to the {@link AutowiredContainer} for one or more given {@link Class} types.
   * 
   * @see #put(Object)
   * @param object
   * @param types
   * @return this
   */
  public <O extends E> AutowiredContainer<E> put( O object, Class<? extends O>... types );
  
  /**
   * Removes the given element from the {@link AutowiredContainer}
   * 
   * @param object
   * @return this
   */
  public <O extends E> AutowiredContainer<E> remove( O object );
  
  /**
   * Removes all elements within the {@link AutowiredContainer} which are {@link Class#isAssignableFrom(Class)} to the given type.
   * 
   * @param type
   * @return this
   */
  public AutowiredContainer<E> remove( Class<? extends E> type );
  
  /**
   * Returns the size of the {@link AutowiredContainer}
   * 
   * @return
   */
  public int size();
  
}
