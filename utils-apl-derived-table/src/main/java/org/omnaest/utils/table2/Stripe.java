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

/**
 * Base type of {@link Row} and {@link Column}
 * 
 * @see ImmutableStripe
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface Stripe<E> extends ImmutableStripe<E>
{
  /**
   * Returns the {@link Cell} for the given orthogonal index position
   * 
   * @param index
   * @return new {@link Cell} instance
   */
  public Cell<E> cell( int index );
  
  /**
   * Returns a new {@link Iterable} instance over the {@link Cell}s
   * 
   * @return
   */
  public Iterable<Cell<E>> cells();
  
  /**
   * Returns the underlying {@link Table}
   * 
   * @return
   */
  public Table<E> table();
  
  /**
   * Detaches from the underlying {@link Table}. This means any change to the {@link Table} will not be reflected by the
   * {@link Stripe} which could lead to inconsistent modifications.
   * 
   * @return this
   */
  public Stripe<E> detach();
}
