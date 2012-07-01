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
}
