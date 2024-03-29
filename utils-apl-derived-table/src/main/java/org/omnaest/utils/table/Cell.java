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

/**
 * @see Table
 * @see ImmutableCell
 * @author Omnaest
 * @param <E>
 */
public interface Cell<E> extends ImmutableCell<E>
{
  /**
   * Sets the underlying element
   * 
   * @param element
   * @return this
   */
  public Cell<E> setElement( E element );
  
  /**
   * Returns a {@link Row} related to this {@link Cell}
   * 
   * @return
   */
  @Override
  public Row<E> row();
  
  /**
   * Returns a {@link Column} related to this {@link Cell}
   * 
   * @return
   */
  @Override
  public Column<E> column();
  
  /**
   * Sets the underlying {@link Cell#setElement(Object)} to null
   * 
   * @return the previously set element
   */
  public E clear();
  
}
