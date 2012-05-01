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
package org.omnaest.utils.structure.element.accessor.adapter;

import org.omnaest.utils.structure.element.accessor.Accessor;

/**
 * Adapter for any {@link ThreadLocal} instance to be used as {@link Accessor}
 * 
 * @author Omnaest
 * @param <E>
 */
public class ThreadLocalToAccessorAdapter<E> implements Accessor<E>
{
  /* ********************************************** Variables ********************************************** */
  private final ThreadLocal<E> threadLocal;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new default {@link ThreadLocal} instance
   * 
   * @see ThreadLocalToAccessorAdapter
   */
  public ThreadLocalToAccessorAdapter()
  {
    super();
    this.threadLocal = new ThreadLocal<E>();
  }
  
  /**
   * @see ThreadLocalToAccessorAdapter
   * @param threadLocal
   *          {@link ThreadLocal}
   */
  public ThreadLocalToAccessorAdapter( ThreadLocal<E> threadLocal )
  {
    super();
    this.threadLocal = threadLocal;
  }
  
  @Override
  public E getElement()
  {
    return this.threadLocal.get();
  }
  
  @Override
  public Accessor<E> setElement( E element )
  {
    this.threadLocal.set( element );
    return this;
  }
  
}
