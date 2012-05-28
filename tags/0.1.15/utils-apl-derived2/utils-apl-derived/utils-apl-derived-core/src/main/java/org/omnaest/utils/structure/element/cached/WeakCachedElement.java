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

import java.lang.ref.WeakReference;

/**
 * This is a {@link CachedElement} which uses a {@link WeakReference} to cache a value. If the {@link WeakReference} becomes
 * invalid the element is resolved again.
 * 
 * @see ValueResolver
 * @see CachedElement
 * @author Omnaest
 * @param <T>
 */
public class WeakCachedElement<T> extends CachedElement<T>
{
  
  /**
   * @see WeakCachedElement
   * @param valueResolver
   */
  public WeakCachedElement( CachedElement.ValueResolver<T> valueResolver )
  {
    super( valueResolver );
  }
  
  @Override
  protected CachedElement.CachedValue<T> newCachedValue()
  {
    //
    return new CachedValue<T>()
    {
      /* ********************************************** Variables ********************************************** */
      private WeakReference<T> weakReference = null;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public T getValue()
      {
        return this.weakReference != null ? this.weakReference.get() : null;
      }
      
      @Override
      public void setValue( T value )
      {
        this.weakReference = new WeakReference<T>( value );
      }
    };
  }
  
}
