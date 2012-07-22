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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Base type of {@link Row} and {@link Column}.<br>
 * <br>
 * The {@link #to()} method allows to transform the content of the given {@link Stripe} into other forms like {@link List},
 * {@link Set}, {@link Map}, etc.
 * 
 * @see StripeTransformer
 * @see ImmutableStripe
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface Stripe<E> extends ImmutableStripe<E>
{
  /**
   * Applies the given {@link ElementConverter} to all the elements and rewrites the result to the underyling {@link Table}
   * 
   * @param elementConverter
   * @return this
   */
  public Stripe<E> apply( ElementConverter<E, E> elementConverter );
  
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
   * Sets all {@link Cell} elements to null
   * 
   * @return this
   */
  public Stripe<E> clear();
  
  /**
   * Detaches from the underlying {@link Table}. This means any change to the {@link Table} will not be reflected by the
   * {@link Stripe} which could lead to inconsistent modifications.
   * 
   * @return this
   */
  public Stripe<E> detach();
  
  /**
   * @param orthogonalIndex
   * @param element
   * @return this
   */
  public Stripe<E> setElement( int orthogonalIndex, E element );
  
  /**
   * @param orthogonalTitle
   * @param element
   * @return this
   */
  public Stripe<E> setElement( String orthogonalTitle, E element );
  
  /**
   * Clears and sets all elements of the {@link Stripe}
   * 
   * @param elements
   * @return this
   */
  public Stripe<E> setElements( E... elements );
  
  /**
   * Returns the underlying {@link Table}
   * 
   * @return
   */
  public Table<E> table();
}
