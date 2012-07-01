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
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface Column<E> extends Stripe<E>, Iterable<E>, ImmutableColumn<E>
{
  
  /**
   * Adds an element to the {@link Column}
   * 
   * @param element
   * @return
   */
  public Column<E> add( E element );
  
  /**
   * Sets the element at the given row index position
   * 
   * @param rowIndex
   * @param element
   * @return this
   */
  public Column<E> setCellElement( int rowIndex, E element );
  
  /**
   * Sets the title of the {@link Column}
   * 
   * @param columnTitle
   * @return
   */
  public Column<E> setTitle( String columnTitle );
}
