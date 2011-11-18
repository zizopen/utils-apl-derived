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
 * A {@link CachedElement} provides an abstract cache mechanism around a given value. The value is initially resolved once from a
 * given {@link ValueResolver}.
 * 
 * @param
 * @author Omnaest
 */
public class CachedElement<T>
{
  /* ********************************************** Variables ********************************************** */
  protected CachedValue<T>   cachedValue   = this.newCachedValue();
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
  
  /**
   * Used to store and retrieve the {@link CachedValue}
   * 
   * @author Omnaest
   * @param <T>
   */
  protected static interface CachedValue<T>
  {
    public T getValue();
    
    public void setValue( T value );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CachedElement
   * @see ValueResolver
   * @param valueResolver
   */
  public CachedElement( ValueResolver<T> valueResolver )
  {
    this.valueResolver = valueResolver;
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
    T value = this.cachedValue.getValue();
    if ( value == null && this.valueResolver != null )
    {
      this.cachedValue.setValue( value = this.valueResolver.resolveValue() );
    }
    
    //
    return value;
  }
  
  /**
   * Returns the cached value and does not resolve the value if the cached value is null.
   * 
   * @return
   */
  public T getCachedValue()
  {
    return this.cachedValue.getValue();
  }
  
  /**
   * Returns true, if the cache has resolved a value from the underlying {@link ValueResolver} instance.
   * 
   * @return
   */
  public boolean hasValueResolved()
  {
    return this.getCachedValue() != null;
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
    this.cachedValue.setValue( null );
    return this;
  }
  
  /**
   * Creates a new {@link CachedValue} instance. Override this to alternate the behavior of the {@link CachedElement}
   * 
   * @return
   */
  protected CachedValue<T> newCachedValue()
  {
    return new CachedValue<T>()
    {
      private T value = null;
      
      @Override
      public void setValue( T value )
      {
        this.value = value;
      }
      
      @Override
      public T getValue()
      {
        return this.value;
      }
    };
  }
}
