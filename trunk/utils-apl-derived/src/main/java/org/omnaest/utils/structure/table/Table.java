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

import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;

/**
 * Table representation. Allows to create arbitrary table structures. Offers rudimentary methods for joining.
 * 
 * @see IndexTable
 * @see ArrayTable
 * @author Omnaest
 */
@XmlRootElement
public interface Table<E> extends TableCore<E, Table<E>>, Iterable<Table.Row<E>>, Serializable, TableJoinable<Table<E>>

{
  /* ********************************************** Classes ********************************************** */

  /**
   * Holds methods to return the current table size for rows and columns. If the data of the underlying table changes, the methods
   * will return the new actual results then, too!
   * 
   * @see Table
   */
  public interface TableSize
  {
    /**
     * Returns the number of all available {@link Cell}s within the {@link Table}
     * 
     * @return
     */
    public int getCellSize();
    
    /**
     * Returns the number of {@link Row}s for the {@link Table}
     * 
     * @return
     */
    public int getRowSize();
    
    /**
     * Returns the number of {@link Column}s for the {@link Table}
     * 
     * @return
     */
    public int getColumnSize();
  }
  
  /**
   * Common base for a {@link Column} and a {@link Row}
   * 
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Stripe<E> extends Serializable, Iterable<Cell<E>>
  {
    
    /* ********************************************** Classes/Interfaces ********************************************** */

    /**
     * Marker interface
     * 
     * @see Stripe
     * @see StripeList
     * @author Omnaest
     */
    public static enum StripeType
    {
      ROW,
      COLUMN
    }
    
    /**
     * {@link Title} of a single {@link Stripe}.
     * 
     * @see Stripe
     */
    public static interface Title extends Serializable
    {
      
      /**
       * Gets the value of a {@link Title}
       * 
       * @return
       */
      public Object getValue();
      
      /**
       * Gets the value of a {@link Title} as {@link String}
       * 
       * @return
       */
      public String getValueAsString();
      
      /**
       * Sets the value of the {@link Title}
       * 
       * @param value
       */
      public void setValue( Object value );
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * Get the {@link Cell} for the given index position.
     * 
     * @param indexPosition
     * @return
     */
    public Cell<E> getCell( int indexPosition );
    
    /**
     * Returns the {@link Cell} for the title value of the orthogonal {@link Stripe}
     * 
     * @param titleValue
     * @return
     */
    public Cell<E> getCell( Object titleValue );
    
    /**
     * Returns the {@link Title}.
     * 
     * @return
     */
    public Title getTitle();
    
    /**
     * Returns true if this {@link Stripe} contains the given {@link Cell}.
     * 
     * @param cell
     * @return
     */
    public boolean contains( Cell<E> cell );
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Row<E> extends Stripe<E>
  {
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Column<E> extends Stripe<E>
  {
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Cell<E> extends Serializable
  {
    
    /**
     * Returns the value of the {@link Cell}
     * 
     * @return
     */
    public E getElement();
    
    /**
     * Sets the value of the {@link Cell}
     * 
     * @param element
     */
    public void setValue( E element );
  }
  
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
