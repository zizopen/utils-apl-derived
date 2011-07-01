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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * Table representation. Allows to create arbitrary table structures. Offers rudimentary methods for joining.
 * 
 * @see IndexTable
 * @see ArrayTable
 * @author Omnaest
 */
public interface Table<E> extends TableCore<E, Table<E>>, Iterable<Table.Row<E>>, Serializable, TableJoinable<Table<E>>

{
  /* ********************************************** Classes ********************************************** */

  /**
   * Interface that is used to convert a table into another table form. For example convert a Table<String> to a Table<Integer>.
   * 
   * @author Omnaest
   * @see TableCore#convert(TableCellConverter)
   */
  public static interface TableCellConverter<FROM, TO>
  {
    /**
     * This method is called for every cell element, which should be converted.
     * 
     * @see TableCellConverter
     * @param cell
     * @return converted value
     */
    public TO convert( FROM cell );
  }
  
  /**
   * Used by the table processor as visitor interface.
   * 
   * @author Omnaest
   * @see TableCore#processTableCells(TableCellVisitor)
   */
  public static interface TableCellVisitor<E>
  {
    public void inspect( int rowIndexPosition, int columnIndexPosition, E cell );
  }
  
  /**
   * Holds the index position for a cell. This is the row and column, as well as the cell index position.
   * 
   * @author Omnaest
   */
  public interface CellIndexPosition
  {
    public int getRowIndexPosition();
    
    public void setRowIndexPosition( int rowIndexPosition );
    
    public int getColumnIndexPosition();
    
    public void setColumnIndexPosition( int columnIndexPosition );
    
    public int getCellIndexPosition();
    
    public void setCellIndexPosition( int cellIndexPosition );
    
  }
  
  /**
   * An advanced list interface as representation of a table row.
   * 
   * @author Omnaest
   */
  public interface Row<E> extends List<E>, Serializable
  {
    /**
     * Returns the element for the given title enumeration. The title enumeration is the same as for the underlying table.
     * 
     * @param rowTitleEnum
     * @return
     */
    public E get( Enum<?> columnTitleEnum );
    
    /**
     * Returns the element for the given title of a table column.
     * 
     * @param rowTitle
     * @return
     */
    public E get( String columnTitle );
    
    /**
     * Returns the given bean object which has the values of the row injected into the properties with the same name as the table
     * column titles. The given bean object can not be null, and it will be returned by the function.
     * 
     * @see TableCore#getRowAsBean(Object, int)
     * @param beanObject
     * @return beanObject
     */
    public <B> B asBean( B beanObject );
    
  }
  
  /**
   * An advanced list interface for a list of rows.
   * 
   * @author Omnaest
   * @param <E>
   */
  public interface RowList<E> extends List<Row<E>>, Serializable
  {
    
    /**
     * @see List#add(Object)
     * @param e
     * @return
     */
    public boolean add( List<E> e );
    
    /**
     * @see List#add(int, Object)
     * @param index
     * @param element
     */
    public void add( int index, List<E> element );
    
    /**
     * @see List#addAll(Collection)
     * @param c
     * @return
     */
    public boolean addAll( List<? extends List<E>> c );
    
    /**
     * @see List#addAll(int, Collection)
     * @param index
     * @param c
     * @return
     */
    public boolean addAll( int index, List<? extends List<E>> c );
    
    /**
     * @see List#set(int, Object)
     * @param index
     * @param element
     * @return
     */
    public List<E> set( int index, List<E> element );
    
  }
  
}
