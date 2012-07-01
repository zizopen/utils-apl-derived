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

import java.util.List;
import java.util.Set;

/**
 * Transforms the elements of a {@link Stripe} into a new independent other container instance
 * 
 * @see ImmutableStripe#to()
 * @author Omnaest
 * @param <E>
 */
public interface StripeTransformer<E>
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
}
