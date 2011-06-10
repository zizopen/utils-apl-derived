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
import java.util.Map;

import org.omnaest.utils.structure.collection.list.IndexList;

/**
 * Represents a indexable table. Defines additional methods for operations on indexes.
 * 
 * @author Omnaest
 * @see Table
 * @see IndexArrayTable
 * @see IndexList
 */
public interface IndexTable<E extends Comparable<E>> extends Table<E>
{
  
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
  
  /**
   * Holds methods to return the current table size for rows and columns. If the data of the underlying table changes, the methods
   * will return the new actual results then, too!
   */
  public interface TableSize
  {
    public int getCellSize();
    
    public int getRowSize();
    
    public int getColumnSize();
  }
  
  public boolean isRowIndexed( int rowIndexPosition );
  
  public boolean isRowIndexed( Enum<?> rowTitleEnum );
  
  public boolean isRowIndexed( String rowTitle );
  
  public boolean isColumnIndexed( int columnIndexPosition );
  
  public boolean isColumnIndexed( Enum<?> columnTitleEnum );
  
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
   * @see #whereElementEquals(int, Comparable)
   * @param columnTitleEnum
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementEquals( Enum<?> columnTitleEnum, E element );
  
  /**
   * @see #whereElementEquals(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementEquals( String columnTitle, E element );
  
  /**
   * Returns a new indexed table where all rows from the table where the elements of a given column do match are included.
   * 
   * @see #whereElementEquals(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementEquals( int columnIndexPosition, E element );
  
  /**
   * Returns a new indexed table where all rows from the table where the elements of a given column do match all criteria given by
   * a map of column index positions and elements to be matched.
   * 
   * @see #whereElementEquals(int, Comparable)
   * @param columnIndexToElementMap
   * @return
   */
  public IndexTable<E> whereElementEqualsColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
  
  /**
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param columnTitleToElementMap
   * @return
   */
  public IndexTable<E> whereElementEqualsColumnTitleMap( Map<String, E> columnTitleToElementMap );
  
  /**
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param columnEnumToElementMap
   * @return
   */
  public IndexTable<E> whereElementEqualsColumnEnumMap( Map<Enum<?>, E> columnEnumToElementMap );
  
  /**
   * @see #whereElementEqualsColumnTitleMap(Map)
   * @param beanObject
   * @return
   */
  public <B> IndexTable<E> whereElementEqualsBeanObject( B beanObject );
  
  /**
   * Ignoring all values of the bean object which are null. Only the other properties are used for the equal constrain. This
   * allows to fill a bean partially and make a where statement for the values filled into the bean.
   * 
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param beanObject
   * @return
   */
  public <B> IndexTable<E> whereElementEqualsBeanObjectIgnoringNullValues( B beanObject );
  
  /**
   * @see #whereElementIsBetween(int, Comparable, Comparable)
   * @param columnTitle
   * @param lowerElement
   * @param upperElement
   * @return new table
   */
  public IndexTable<E> whereElementIsBetween( String columnTitle, E lowerElement, E upperElement );
  
  /**
   * Returns a new table with all rows from the table where the elements of the given column are between the range created by the
   * lower and upper element given as parameter.
   * 
   * @see #whereElementIsBetween(String, Comparable, Comparable)
   * @param columnIndexPosition
   * @param lowerElement
   * @param upperElement
   * @return new table
   */
  public IndexTable<E> whereElementIsBetween( int columnIndexPosition, E lowerElement, E upperElement );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementIsGreaterThan( String columnTitle, E element );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnIndexToElementMap
   * @return
   */
  public IndexTable<E> whereElementIsGreaterThanColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
  
  /**
   * @see #whereElementIsGreaterThanColumnIndexMap(Map)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitleToElementMap
   * @return
   */
  public IndexTable<E> whereElementIsGreaterThanColumnTitleMap( Map<String, E> columnTitleToElementMap );
  
  /**
   * @see #whereElementIsGreaterThanBeanObjectIgnoringNullValues(Object)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @see #whereElementIsGreaterThanColumnIndexMap(Map)
   * @param beanObject
   * @return
   */
  public <B> IndexTable<E> whereElementIsGreaterThanBeanObject( B beanObject );
  
  /**
   * @see #whereElementIsGreaterThanBeanObject(Object)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @see #whereElementIsGreaterThanColumnTitleMap(Map)
   * @param beanObject
   * @return
   */
  public <B> IndexTable<E> whereElementIsGreaterThanBeanObjectIgnoringNullValues( B beanObject );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitleEnum
   * @param element
   * @return
   */
  public IndexTable<E> whereElementIsGreaterThan( Enum<?> columnTitleEnum, E element );
  
  /**
   * @see #whereElementIsLesserThan(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementIsLesserThan( String columnTitle, E element );
  
  /**
   * Returns a new table with all rows from the current table where elements in the given column are greater than the given
   * element.
   * 
   * @see #whereElementIsLesserThan(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return new table
   */
  public IndexTable<E> whereElementIsGreaterThan( int columnIndexPosition, E element );
  
  /**
   * Returns a new table where all rows of the current table where elements in the given column and are lesser than the given
   * element.
   * 
   * @see #whereElementIsLesserThan(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public IndexTable<E> whereElementIsLesserThan( int columnIndexPosition, E element );
  
  /**
   * Sorts the whole current table by the content of a given column.<br>
   * The underlying sort uses a merge sort algorithm.
   * 
   * @param columnIndexPosition
   * @param ascending
   *          true:ascending false:descending
   * @return this
   */
  public IndexTable<E> orderRowsBy( final int columnIndexPosition, final boolean ascending );
  
  /**
   * Orders the rows ascending by the property of a java bean which is not null. <br>
   * If more than one property is not null, it is unspecified by which property the table is sorted.<br>
   * The property, which is not null, has to equal a column title.
   * 
   * @param beanObject
   * @return this
   */
  public <B> IndexTable<E> orderRowsAscendingByBeanObjectPropertyNotNull( B beanObject );
  
  /**
   * @param columnTitle
   * @return
   */
  public IndexTable<E> orderRowsAscendingBy( String columnTitle );
  
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
