/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.structure.table;

import java.util.List;

import org.omnaest.utils.structure.collection.list.IndexList;
import org.omnaest.utils.structure.table.concrete.IndexArrayTable;

/**
 * Represents a indexable table. Defines additional methods for operations on indexes.
 * 
 * @author Omnaest
 * @see Table
 * @see IndexArrayTable
 * @see IndexList
 */
public interface IndexTable<E extends Comparable<E>> extends Table<E>, TableSelectable<E, Table<E>>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see IndexTable#innerJoinByEqualColumn(IndexTable, List)
   * @author Omnaest
   */
  public interface IndexPositionPair
  {
    public int getCurrentTableIndexPosition();
    
    public void setCurrentTableIndexPosition( int currentTableIndexPosition );
    
    public int getJoinTableIndexPosition();
    
    public void setJoinTableIndexPosition( int joinTableIndexPosition );
  }
  
  
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param rowIndexPosition
   * @return
   */
  public boolean isRowIndexed( int rowIndexPosition );
  
  /**
   * @param rowTitleEnum
   * @return
   */
  public boolean isRowIndexed( Enum<?> rowTitleEnum );
  
  /**
   * @param rowTitle
   * @return
   */
  public boolean isRowIndexed( String rowTitle );
  
  /**
   * @param columnIndexPosition
   * @return
   */
  public boolean isColumnIndexed( int columnIndexPosition );
  
  /**
   * @param columnTitleEnum
   * @return
   */
  public boolean isColumnIndexed( Enum<?> columnTitleEnum );
  
  /**
   * @param columnTitle
   * @return
   */
  public boolean isColumnIndexed( String columnTitle );
  
  /**
   * Sets a row to be indexed or not.
   * 
   * @see #setIndexRow(Enum, boolean)
   * @see #setIndexRow(String, boolean)
   * @param rowIndexPosition
   * @param indexed
   *          Specifies if the row should be indexed or not.
   * @return
   */
  public Table<E> setIndexRow( int rowIndexPosition, boolean indexed );
  
  /**
   * @see #setIndexRow(int, boolean)
   * @param rowTitleEnum
   * @param indexed
   * @return
   */
  public Table<E> setIndexRow( Enum<?> rowTitleEnum, boolean indexed );
  
  /**
   * @see #setIndexRow(int, boolean)
   * @param rowTitle
   * @param indexed
   * @return
   */
  public Table<E> setIndexRow( String rowTitle, boolean indexed );
  
  /**
   * Sets a column to be indexed or not.
   * 
   * @see #setIndexColumn(Enum, boolean)
   * @see #setIndexColumn(String, boolean)
   * @param columnIndexPosition
   * @param indexed
   *          defines, if the specified column will be indexed or not
   * @return
   */
  public Table<E> setIndexColumn( int columnIndexPosition, boolean indexed );
  
  /**
   * @see #setIndexColumn(int, boolean)
   * @param columnTitleEnum
   * @param indexed
   * @return
   */
  public Table<E> setIndexColumn( Enum<?> columnTitleEnum, boolean indexed );
  
  /**
   * @see #setIndexColumn(int, boolean)
   * @param columnTitle
   * @param indexed
   * @return
   */
  public Table<E> setIndexColumn( String columnTitle, boolean indexed );
  
  /**
   * Returns the corresponding index position for a column title.
   * 
   * @see #determineColumnIndexPosition(String)
   * @param columnTitleEnum
   * @return
   */
  public int determineColumnIndexPosition( Enum<?> columnTitleEnum );
  
  /**
   * @see #determineColumnIndexPosition(String)
   * @param columnTitle
   * @return
   */
  public int determineColumnIndexPosition( String columnTitle );
  
  /**
   * Returns the row index position of a given row title.
   * 
   * @param rowTitleEnum
   * @return
   */
  public int determineRowIndexPosition( Enum<?> rowTitleEnum );
  
  /**
   * @see #determineRowIndexPosition(Enum)
   * @param rowTitle
   * @return
   */
  public int determineRowIndexPosition( String rowTitle );
  
  /**
   * Additional to the basic structure clone all index settings are cloned, too.
   * 
   * @see Table#cloneTableStructure()
   */
  public IndexTable<E> cloneTableStructure();
  
  /**
   * Returns the index positions of all rows, where the column elements are greater than the given element.
   * 
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public int[] indexesOfRowsWithElementsGreaterThan( int columnIndexPosition, E element );
  
  /**
   * Returns the index positions of all rows, where the column elements are lesser than the given element.
   * 
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public int[] indexesOfRowsWithElementsLesserThan( int columnIndexPosition, E element );
  
  /**
   * Returns all row index positions with contains elements that are between the given lower and upper element. Elements that
   * equals the boundary elements are included with their index positions.
   * 
   * @param columnIndexPosition
   * @param lowerElement
   * @param upperElement
   * @return
   */
  public int[] indexesOfRowsWithElementsBetween( int columnIndexPosition, E lowerElement, E upperElement );
}
