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
 * Immutable {@link Cell}
 * 
 * @see ImmutableTable
 * @see ImmutableStripe
 * @see ImmutableRow
 * @see ImmutableColumn
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableCell<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Position representation of an {@link ImmutableCell} which implements {@link #hashCode()} and {@link #equals(Object)}
   * 
   * @author Omnaest
   */
  public static interface Position
  {
    /**
     * @return
     */
    public int rowIndex();
    
    /**
     * @return
     */
    public int columnIndex();
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Returns a {@link Position} instance for the current position state
   * 
   * @return
   */
  public Position getPosition();
  
  /**
   * Returns the underlying element
   * 
   * @return
   */
  public E getElement();
  
  /**
   * @return
   */
  public int rowIndex();
  
  /**
   * @return
   */
  public int columnIndex();
  
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
   * Returns an {@link ImmutableRow} related to this {@link ImmutableCell}
   * 
   * @return
   */
  public ImmutableRow<E> row();
  
  /**
   * Returns an {@link ImmutableColumn} related to this {@link ImmutableCell}
   * 
   * @return
   */
  public ImmutableColumn<E> column();
}
