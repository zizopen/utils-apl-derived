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
import java.util.BitSet;

/**
 * Handler for table events
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableEventHandler<E> extends Serializable
{
  public void handleAddedColumn( int columnIndex, E... elements );
  
  public void handleAddedRow( int rowIndex, E... elements );
  
  public void handleClearTable();
  
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle );
  
  public void handleRemovedRow( int rowIndex, E[] previousElements, String rowTitle );
  
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement );
  
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices );
}
