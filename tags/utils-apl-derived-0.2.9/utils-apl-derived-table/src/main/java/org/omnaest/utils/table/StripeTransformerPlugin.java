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
package org.omnaest.utils.table;

import java.io.Serializable;

/**
 * A {@link StripeTransformerPlugin} allows to specify a type and a way to transform a {@link Stripe} into this type
 * 
 * @see ImmutableTable#register(StripeTransformerPlugin)
 * @see Table#register(StripeTransformerPlugin)
 * @author Omnaest
 */
public interface StripeTransformerPlugin<E, T> extends Serializable
{
  /**
   * Returns the {@link Class} type the {@link StripeTransformerPlugin} will create instances of
   * 
   * @return
   */
  public Class<T> getType();
  
  /**
   * Transforms a given {@link ImmutableStripe} into an instance of another type
   * 
   * @param stripe
   *          {@link ImmutableStripe}
   * @return new instance
   */
  public T transform( ImmutableStripe<E> stripe );
  
  /**
   * Similar to {@link #transform(ImmutableStripe)} using the given instance
   * 
   * @param stripe
   *          {@link ImmutableStripe}
   * @param instance
   * @return new instance
   */
  public T transform( ImmutableStripe<E> stripe, T instance );
}
