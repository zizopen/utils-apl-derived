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
package org.omnaest.utils.structure.element;
/*******************************************************************************
 * Copyright (c) 2011 Danny Kunz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Danny Kunz - initial API and implementation
 ******************************************************************************/


/**
 * A {@link CachedElement} provides an abstract cache mechanism around a given value. The value is initially resolved once from a
 * given {@link ValueResolver}.
 * 
 * @param
 * @author Omnaest
 */
public class CachedElement<T>
{
  /* ********************************************** Variables ********************************************** */
  protected T                cachedValue   = null;
  protected ValueResolver<T> valueResolver = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * @see #resolveValue()
   * @see CachedElement
   */
  public static interface ValueResolver<T>
  {
    /**
     * Resolves a value.
     * 
     * @return
     */
    public T resolveValue();
  }
  
  /**
   * Simple {@link ValueResolver} which returns the object initially given to the constructor.
   * 
   * @author Omnaest
   * @param <T>
   */
  public static class ValueResolverSimple<T> implements ValueResolver<T>
  {
    /* ********************************************** Variables ********************************************** */
    protected T value = null;
    
    /* ********************************************** Methods ********************************************** */
    public ValueResolverSimple( T value )
    {
      this.value = value;
    }
    
    @Override
    public T resolveValue()
    {
      return this.value;
    }
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @see CachedElement
   * @see ValueResolver
   * @param valueResolver
   */
  public CachedElement( ValueResolver<T> valueFactory )
  {
    this.valueResolver = valueFactory;
  }
  
  /**
   * @see CachedElement
   * @see ValueResolver
   * @see ValueResolverSimple
   * @param valueResolver
   */
  public CachedElement( T value )
  {
    this.valueResolver = new ValueResolverSimple<T>( value );
  }
  
  /**
   * Returns the value from the cache or resolves it from the underlying factory.
   */
  public T getValue()
  {
    //
    if ( this.cachedValue == null && this.valueResolver != null )
    {
      this.cachedValue = this.valueResolver.resolveValue();
    }
    
    //
    return this.cachedValue;
  }
  
  /**
   * Returns the cached value and does not resolve the value if the cached value is null.
   * 
   * @return
   */
  public T getCachedValue()
  {
    return this.cachedValue;
  }
  
  /**
   * Returns true, if the cache has resolved a value from the underlying {@link ValueResolver} instance.
   * 
   * @return
   */
  public boolean hasValueResolved()
  {
    return this.cachedValue != null;
  }
  
  public void setValueResolver( ValueResolver<T> valueResolver )
  {
    this.valueResolver = valueResolver;
  }
  
  public ValueResolver<T> getValueResolver()
  {
    return this.valueResolver;
  }
  
  /**
   * Clears the cached value, so the next call to {@link #getValue()} will resolve the value once again from the underlying
   * {@link ValueResolver}.
   * 
   * @return this
   */
  public CachedElement<T> clearCache()
  {
    this.cachedValue = null;
    return this;
  }
}
