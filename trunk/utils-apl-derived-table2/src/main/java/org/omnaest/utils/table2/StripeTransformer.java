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
package org.omnaest.utils.table2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transforms the elements of a {@link Stripe} into a new independent other container instance
 * 
 * @see ImmutableStripe#to()
 * @author Omnaest
 * @param <E>
 */
public interface StripeTransformer<E> extends Serializable
{
  /**
   * Returns a new {@link Set} instance containing all elements
   * 
   * @return
   */
  public Set<E> set();
  
  /**
   * Returns an new array containing all elements
   * 
   * @return
   */
  public E[] array();
  
  /**
   * Returns a new {@link List} instance containing all elements
   * 
   * @return
   */
  public List<E> list();
  
  /**
   * Returns a new {@link StripeEntity} instance
   * 
   * @return
   */
  public StripeEntity<E> entity();
  
  /**
   * Returns the {@link Stripe} transformed into the given type. <br>
   * <br>
   * To allow this to work there has to be a {@link StripeTransformerPlugin} registered to the underlying {@link Table}.
   * 
   * @param type
   *          {@link Class}
   * @return
   */
  public <T> T instanceOf( Class<T> type );
  
  /**
   * Similar to {@link #instanceOf(Class)} using a given instance which is returned enriched with the data of the {@link Stripe}
   * 
   * @param instance
   * @return given instance
   */
  public <T> T instance( T instance );
  
  /**
   * Returns the {@link Stripe} as a {@link Map}. The keys are the orthogonal titles and the values are the actual elements of the
   * {@link Stripe}
   * 
   * @return
   */
  public Map<String, E> map();
}
