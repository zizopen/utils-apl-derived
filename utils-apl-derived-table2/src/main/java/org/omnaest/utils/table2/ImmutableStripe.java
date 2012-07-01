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

import java.util.Set;

/**
 * Immutable {@link Stripe}
 * 
 * @see Stripe
 * @see ImmutableCell
 * @see ImmutableColumn
 * @see ImmutableRow
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableStripe<E> extends Iterable<E>
{
  /**
   * Returns the number of contained elements
   * 
   * @return
   */
  public int size();
  
  /**
   * Returns the element for the given index position
   * 
   * @param index
   * @return
   */
  public E getCellElement( int index );
  
  /**
   * Returns the {@link ImmutableCell} for the given orthogonal index position
   * 
   * @param index
   * @return new {@link ImmutableCell} instance
   */
  public ImmutableCell<E> getCell( int index );
  
  /**
   * Returns the index position
   * 
   * @return
   */
  public int index();
  
  /**
   * Returns true if the underlying data is deleted
   * 
   * @return
   */
  public boolean isDeleted();
  
  /**
   * Returns true, if the underlying data is modified since creation of this instance
   * 
   * @return
   */
  public boolean isModified();
  
  /**
   * Returns a new {@link Iterable} instance over the {@link ImmutableCell}s
   * 
   * @return
   */
  public Iterable<? extends ImmutableCell<E>> cells();
  
  /**
   * Returns a transformer which allows to transform into other types like array, {@link Set}, ...
   * 
   * @return
   */
  public StripeTransformer<E> to();
  
  /**
   * Returns the underlying {@link ImmutableTable}
   * 
   * @return
   */
  public ImmutableTable<E> table();
  
  /**
   * Returns true if the content of this {@link Stripe} is {@link #equals(Object)} to the content of the given one
   * 
   * @param stripe
   *          {@link ImmutableStripe}
   * @return
   */
  public boolean equalsInContent( ImmutableStripe<E> stripe );
}
