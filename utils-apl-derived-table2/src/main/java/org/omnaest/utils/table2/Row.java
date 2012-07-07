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
 * @see ImmutableRow
 * @see Table
 * @see Column
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public interface Row<E> extends Stripe<E>, ImmutableRow<E>
{
  /**
   * Adds an element to the {@link Row}
   * 
   * @param element
   * @return this
   */
  public Row<E> add( E element );
  
  /**
   * Sets the element at the given column index position
   * 
   * @param columnIndex
   * @param element
   * @return this
   */
  public Row<E> setCellElement( int columnIndex, E element );
  
  /**
   * Sets the title of the {@link Row}
   * 
   * @param rowTitle
   * @return
   */
  public Row<E> setTitle( String rowTitle );
  
  /**
   * Removes the current {@link Row} from its {@link Table}. The {@link Row} will return true for {@link #isDeleted()} afterwards.
   * 
   * @return this
   */
  public Row<E> remove();
  
  /**
   * Clears and sets all elements of the {@link Row}
   * 
   * @param elements
   * @return this
   */
  public Row<E> setCellElements( E... elements );
  
  /**
   * Sets all cell elements to null
   * 
   * @return this
   */
  public Row<E> clear();
  
  /**
   * Moves this {@link Row} to the given row index position. This is like an {@link #remove()} and
   * {@link Table#addRowElements(int, Object...)} operation
   * 
   * @param newRowIndex
   * @return this
   */
  public Row<E> moveTo( int newRowIndex );
  
  /**
   * Switches the current {@link Row} with the row at the given index position
   * 
   * @param otherRowIndex
   * @return this
   */
  public Row<E> switchWith( int otherRowIndex );
  
  /**
   * Switches the current {@link Row} with the other {@link Row} within the {@link Table}
   * 
   * @param otherRow
   * @return this
   */
  public Row<E> switchWith( Row<E> otherRow );
}
