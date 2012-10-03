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
package org.omnaest.utils.structure.element.cached;


/**
 * {@link ThreadLocal} variant of a {@link CachedElement} which caches resolved element values for each thread independently.<br>
 * <br>
 * If instantiated with active inheritance an {@link InheritableThreadLocal} is used, which allows to inherit values to child
 * threads. By default inheritance is not used.
 * 
 * @see Thread
 * @see ThreadLocal
 * @see InheritableThreadLocal
 * @author Omnaest
 * @param <T>
 */
public class ThreadLocalCachedElement<T> extends CachedElement<T>
{
  /* ********************************************** Variables ********************************************** */
  protected boolean inherited = false;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param valueResolver
   */
  public ThreadLocalCachedElement( ValueResolver<T> valueResolver )
  {
    super( valueResolver );
  }
  
  /**
   * @param valueResolver
   * @param inherited
   */
  public ThreadLocalCachedElement( org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver<T> valueResolver,
                                   boolean inherited )
  {
    super( valueResolver );
    this.inherited = inherited;
  }
  
  @Override
  protected CachedElement.CachedValue<T> newCachedValue()
  {
    return new CachedValue<T>()
    {
      /* ********************************************** Variables ********************************************** */
      private ThreadLocal<T> threadLocalValue = ThreadLocalCachedElement.this.inherited ? new InheritableThreadLocal<T>()
                                                                                       : new ThreadLocal<T>();
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public T getValue()
      {
        return this.threadLocalValue.get();
      }
      
      @Override
      public void setValue( T value )
      {
        this.threadLocalValue.set( value );
      }
    };
  }
}
