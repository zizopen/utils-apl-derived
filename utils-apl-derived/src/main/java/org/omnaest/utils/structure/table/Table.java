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
import java.util.List;

import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.adapter.TableAdapter;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.subspecification.TableAdaptable;
import org.omnaest.utils.structure.table.subspecification.TableCore;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;
import org.omnaest.utils.structure.table.subspecification.TableSelectable;
import org.omnaest.utils.structure.table.subspecification.TableSerializable;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * This is a structure representing a fully qualified {@link Table}. <br>
 * <br>
 * A {@link Table} divides in several {@link Row}s or/and {@link Column}s. Each {@link Row} and {@link Column} can contain
 * {@link Cell}s. In the regular case one {@link Cell} has only one {@link Row} and one {@link Column} it belongs to. (After a
 * selection the case can occur that one {@link Cell} is shared by multiple {@link Row}s) <br>
 * <br>
 * The {@link Table} supports basic CRUD operations:
 * <ul>
 * <li>{@link #addRowCellElements(List)} or {@link #addColumnCellElements(List)}</li>
 * <li>{@link #getRow(int)} or {@link #getColumn(int)}</li>
 * <li>{@link #setRowCellElements(int, List)} or {@link #setColumnCellElements(int, List)}</li>
 * <li>{@link #removeRow(int)} or {@link #removeColumn(int)}</li>
 * </ul>
 * as well as additional operations nodes like:
 * <ul>
 * <li>{@link #select()}</li>
 * <li>{@link #serializer()}</li>
 * <li>{@link #as()}</li>
 * <li>{@link #clone()}</li>
 * </ul>
 * or simple helper methods like:
 * <ul>
 * <li>{@link #putArray(Object[][], int, int)}</li>
 * <li>{@link #putTable(TableDataSource, int, int)}</li>
 * <li>{@link #transpose()}</li>
 * </ul>
 * To iterate over {@link Row}s you can use the {@link Table} instance itself. The {@link Table} allows to iterate over
 * {@link Cell}s and {@link Column}s too of course:
 * <ul>
 * <li>{@link #cells()}</li>
 * <li>{@link #rows()}</li>
 * <li>{@link #columns()}</li>
 * </ul>
 * 
 * @see ArrayTable
 * @see TableDataSource
 * @see TableAdapter
 * @see TableView
 * @see Selection
 * @see TableCloner
 * @see TableSerializer
 * @see #setTableName(Object)
 * @see #getTableSize()
 * @param <E>
 *          type of the {@link Table} elements
 * @author Omnaest
 */
public interface Table<E> extends TableCore<E>, TableSelectable<E>, Iterable<Row<E>>, TableSerializable<E>, TableAdaptable<E>

{
  /* ********************************************** Classes ********************************************** */
  
  /**
   * Marker interface for components of a {@link Table}
   * 
   * @author Omnaest
   */
  public interface TableComponent extends Serializable
  {
  }
  
  /**
   * Holds methods to return the current table size for rows and columns. If the data of the underlying table changes, the methods
   * will return the new actual results then, too!
   * 
   * @see Table
   */
  public interface TableSize extends TableComponent
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
   * Common abstraction for a {@link Column} and a {@link Row}
   * 
   * @see Table
   * @see Row
   * @see Column
   * @author Omnaest
   * @param <E>
   */
  public static interface Stripe<E> extends TableComponent, Iterable<Cell<E>>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * {@link StripeType} of a {@link Stripe}
     * 
     * @see Stripe
     * @see StripeDataList
     * @see StripeTypeHelper
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
    public static interface Title extends TableComponent
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
     * Returns the {@link Cell#getElement()} for the given index position
     * 
     * @param indexPosition
     * @return
     */
    public E getCellElement( int indexPosition );
    
    /**
     * Returns the {@link Cell} for the title value of the orthogonal {@link Stripe#getTitleValue()}
     * 
     * @param titleValue
     * @return
     */
    public Cell<E> getCell( Object titleValue );
    
    /**
     * Returns the {@link Cell} for the title value of the orthogonal {@link Stripe}
     * 
     * @param stripeOrthogonal
     * @return
     */
    public Cell<E> getCell( Stripe<E> stripeOrthogonal );
    
    /**
     * Returns the {@link Cell#getElement()} for the given index position
     * 
     * @param titleValue
     * @return
     */
    public E getCellElement( Object titleValue );
    
    /**
     * Returns the {@link Cell#getElement()} for the given orthogonal {@link Stripe}
     * 
     * @param stripeOrthogonal
     * @return
     */
    public E getCellElement( Stripe<E> stripeOrthogonal );
    
    /**
     * Returns a new {@link List} instance of all {@link Cell#getElement()} in order
     * 
     * @return
     */
    public List<E> getCellElementList();
    
    /**
     * Returns the {@link Title}.
     * 
     * @return
     */
    public Title title();
    
    /**
     * Returns the {@link Title#getValue()}
     * 
     * @return
     */
    public Object getTitleValue();
    
    /**
     * Returns true if this {@link Stripe} contains the given {@link Cell}.
     * 
     * @param cell
     * @return
     */
    public boolean contains( Cell<E> cell );
    
    /**
     * Returns true if one of the {@link Cell}s of this {@link Stripe} contains the given element
     * 
     * @param element
     * @return
     */
    public boolean contains( E element );
    
    /**
     * Sets the {@link Cell#setElement(Object)} for the {@link Cell} corresponding to the given title value
     * 
     * @param titleValue
     * @param element
     * @return this
     */
    public Stripe<E> setCellElement( Object titleValue, E element );
    
    /**
     * Sets the {@link Cell#setElement(Object)} of the whole {@link Stripe}
     * 
     * @param elements
     * @return this
     */
    public Stripe<E> setCellElements( Iterable<E> elements );
    
    /**
     * @see #setCellElements(Iterable)
     * @param elements
     * @return
     */
    public Stripe<E> setCellElements( E... elements );
    
    /**
     * Set the {@link Cell#setElement(Object)} for the {@link Cell} corresponding to the given orthogonal index position
     * 
     * @param indexPosition
     * @param element
     * @return this
     */
    public Stripe<E> setCellElement( int indexPosition, E element );
    
    /**
     * Determines the number of {@link Cell}s currently stored within this {@link Stripe}
     * 
     * @return
     */
    public int determineNumberOfCells();
    
    /**
     * Returns true if the {@link Stripe} has a {@link Title} value not null
     * 
     * @return
     */
    public boolean hasTitle();
    
    /**
     * Returns an {@link Iterable} for all {@link Cell}s of this {@link Stripe}
     * 
     * @return
     */
    public Iterable<Cell<E>> cells();
    
    /**
     * Sets all available {@link Cell#setElement(Object)} to null
     * 
     * @return
     */
    public Stripe<E> clearCellElements();
    
    /**
     * Returns the underlying {@link Table}
     * 
     * @return
     */
    public Table<E> getTable();
  }
  
  /**
   * A {@link Row} represents the horizontal {@link Stripe} of a {@link Table}. It contains {@link Cell}s which hold the specific
   * values of the {@link Table}. A {@link Row} can have a {@link Title}.
   * 
   * @see Cell
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Row<E> extends Stripe<E>
  {
    /**
     * Determines the index position of this {@link Row} within the {@link Table}
     * 
     * @return
     */
    public int determineRowIndexPosition();
    
    /**
     * @see Table#getColumnTitleValueList()
     * @return
     */
    public List<Object> getColumnTitleValueList();
  }
  
  /**
   * A {@link Column} represents the vertical {@link Stripe} of a {@link Table}. It contains {@link Cell}s which hold the specific
   * values of the {@link Table}. A {@link Column} can have a {@link Title}
   * 
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Column<E> extends Stripe<E>
  {
    /**
     * Determines the index position of this {@link Column} within the {@link Table}
     * 
     * @return
     */
    public int determineColumnIndexPosition();
    
  }
  
  /**
   * A {@link Cell} is the atomic part of a {@link Table}. It is normally part of a {@link Row} and a {@link Column}.
   * 
   * @see Table
   * @see CellImmutable
   * @author Omnaest
   * @param <E>
   */
  public static interface Cell<E> extends CellImmutable<E>, TableComponent
  {
    /**
     * Sets the value of the {@link Cell}
     * 
     * @param element
     */
    public void setElement( E element );
    
  }
  
  /**
   * @see Cell
   * @author Omnaest
   * @param <E>
   */
  public interface CellImmutable<E>
  {
    /**
     * Returns the value of the {@link Cell}
     * 
     * @return
     */
    public E getElement();
    
    /**
     * Returns true if the given element equals the current {@link Cell#getElement()}
     * 
     * @param element
     * @return
     */
    public boolean hasElement( E element );
  }
  
  /**
   * Interface that is used to convert a {@link Table} into another {@link Table} generics form. For example convert a
   * Table&lt;String&gt; to a Table&lt;Integer&gt;.
   * 
   * @param <FROM>
   * @param <TO>
   * @author Omnaest
   * @see TableCore#convert(TableCellConverter)
   */
  public static interface TableCellConverter<FROM, TO>
  {
    /**
     * This method is called for every {@link Cell} element, which should be converted.
     * 
     * @see TableCellConverter
     * @param cellElement
     * @return converted value
     */
    public TO convert( FROM cellElement );
  }
  
  /**
   * Used by the table processor as visitor interface.
   * 
   * @author Omnaest
   * @see TableCore#processTableCells(TableCellVisitor)
   */
  public static interface TableCellVisitor<E>
  {
    /**
     * Inspect method
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @param cell
     */
    public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell );
  }
  
}
