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
package org.omnaest.utils.structure.table.internal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.CloneableStructure;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.Table.TableComponent;
import org.omnaest.utils.structure.table.Table.TableSize;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface TableInternal<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Marker interface for internal {@link Table} components
   * 
   * @see TableComponent
   * @author Omnaest
   */
  public static interface TableComponentInternal extends TableComponent
  {
  }
  
  /**
   * @see TableContent
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface TableContentResolver<E>
  {
    /**
     * Resolves the {@link TableContent}
     * 
     * @return
     */
    public TableContent<E> resolveTableContent();
  }
  
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public interface CellAndStripeResolver<E> extends TableComponentInternal
  {
    /**
     * Resolves the {@link Cell} for the given index positions.
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} for the given {@link Row} and {@link Column} index position.
     * 
     * @param row
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveCell( StripeInternal<E> row, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} by a row index position and a {@link Column}.
     * 
     * @param rowIndexPosition
     * @param column
     * @return
     */
    public Cell<E> resolveCell( int rowIndexPosition, StripeInternal<E> column );
    
    /**
     * Resolves a {@link Cell} by a given {@link Row} and {@link Column}
     * 
     * @param row
     * @param column
     * @return
     */
    public Cell<E> resolveCell( StripeInternal<E> row, StripeInternal<E> column );
    
    /**
     * Resolves a {@link Cell} by a given {@link Stripe} and the complementary index position
     * 
     * @param stripe
     * @param indexPosition
     * @return
     */
    public Cell<E> resolveCell( StripeData<E> stripe, int indexPosition );
    
    /**
     * Resolves a {@link Cell} by a given {@link Stripe} and the title value of the orthogonal stripe
     * 
     * @param stripe
     * @param titleValue
     * @return
     */
    public Cell<E> resolveCell( StripeData<E> stripe, Object titleValue );
    
    /**
     * Resolves a {@link ColumnInternal} for the given {@link Column} index position.
     * 
     * @param columnIndexPosition
     * @return
     */
    public StripeData<E> resolveColumnStripeData( int columnIndexPosition );
    
    /**
     * Tries to resolve the {@link Column} for the given {@link Column} index position. If the {@link Column} does not exists but
     * would have a valid {@link Column} index position it will be created.
     * 
     * @param columnIndexPosition
     * @return
     */
    public StripeData<E> resolveOrCreateColumnStripeData( int columnIndexPosition );
    
    /**
     * Resolves a {@link RowInternal} for the given {@link Row} index position.
     * 
     * @param rowIndexPosition
     * @return
     */
    public StripeData<E> resolveRowStripeData( int rowIndexPosition );
    
    /**
     * Tries to resolve the {@link Row} for the given {@link Row} index position. If the {@link Row} does not exists but would
     * have a valid {@link Row} index position it will be created.
     * 
     * @param rowIndexPosition
     * @return
     */
    public StripeData<E> resolveOrCreateRowStripeData( int rowIndexPosition );
    
    /**
     * Tries to resolve a {@link StripeData} instance for the given index position and {@link StripeType}. If no {@link Stripe}
     * instance could be resolved for the given valid index position as many are created until there is an instance available for
     * the given index position.
     * 
     * @param stripeType
     * @param indexPosition
     * @return
     */
    public StripeData<E> resolveOrCreateStripeData( StripeType stripeType, int indexPosition );
    
    /**
     * Tries to resolve a {@link StripeData} instance for the given index position and {@link StripeType}. If no {@link Stripe}
     * instance could be resolved for the given valid {@link Title#getValue()} a new {@link Stripe} is created with the given
     * {@link Title#getValue()}.
     * 
     * @param stripeType
     * @param titleValue
     * @return
     */
    public StripeData<E> resolveOrCreateStripeData( StripeType stripeType, Object titleValue );
    
    /**
     * Resolves the {@link Stripe} for the given {@link StripeType} and index position from the internal {@link Table} reference.
     * 
     * @param stripeType
     * @param indexPosition
     * @return
     */
    public StripeData<E> resolveStripeData( StripeType stripeType, int indexPosition );
    
    /**
     * Resolves the {@link Row} by the given {@link Title#getValue()}
     * 
     * @param titleValue
     * @return
     */
    public StripeData<E> resolveRowStripeData( Object titleValue );
    
    /**
     * Resolves the {@link Column} by the given {@link Title#getValue()}
     * 
     * @param titleValue
     * @return
     */
    public StripeData<E> resolveColumnStripeData( Object titleValue );
    
    /**
     * Resolves a {@link Cell} by the {@link Title#getValue()} of the {@link Row} and the {@link Column}
     * 
     * @param rowTitleValue
     * @param column
     * @return
     */
    public Cell<E> resolveCell( Object rowTitleValue, StripeInternal<E> column );
    
    /**
     * Resolves a {@link Cell} by the {@link Row} and the {@link Title#getValue()} of the {@link Column}
     * 
     * @param row
     * @param columnTitleValue
     * @return
     */
    public Cell<E> resolveCell( StripeInternal<E> row, Object columnTitleValue );
    
    /**
     * Tries to resolve a {@link Cell} by a given {@link RowInternal} and {@link ColumnInternal}. If there is no {@link Cell}
     * available a new one is created.
     * 
     * @param row
     * @param column
     * @return
     */
    public Cell<E> resolveOrCreateCell( StripeInternal<E> row, StripeInternal<E> column );
    
    /**
     * Tries to resolve a {@link Cell} by the given {@link Row} and {@link Column} index position. If no {@link Cell} is available
     * a new one will be created.
     * 
     * @param row
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCell( StripeInternal<E> row, int columnIndexPosition );
    
    /**
     * Tries to resolve a {@link Cell} by the given {@link Row} index position and a given {@link Column}. If no {@link Cell} is
     * available a new one will be created.
     * 
     * @param rowIndexPosition
     * @param column
     * @return
     */
    public Cell<E> resolveOrCreateCell( int rowIndexPosition, StripeInternal<E> column );
    
    /**
     * Resolves a {@link Cell} by its {@link Cell} index position. The {@link Cell} index position is counted from left to right
     * starting from top to bottom of the {@link Table}. First index position is 0. If the {@link Cell} index position is out of
     * boundary null is returned.
     * 
     * @param cellIndexPosition
     * @return
     */
    public Cell<E> resolveCell( int cellIndexPosition );
    
    /**
     * Resolves a {@link Cell} by its {@link Cell} index position or creates it if it cannot be resolved. The {@link Cell} index
     * position is counted from left to right starting from top to bottom of the {@link Table}. First index position is 0. If the
     * {@link Cell} index position is out of boundary null is returned.
     * 
     * @param cellIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCell( int cellIndexPosition );
    
    /**
     * Resolves a {@link Cell} by its {@link Row} and {@link Column} index positions. If no {@link Cell} can be resolved a new one
     * is created.
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCell( int rowIndexPosition, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} by two orthogonal {@link StripeData} instances
     * 
     * @param stripeData
     * @param stripeDataOrthogonal
     * @return
     */
    public Cell<E> resolveCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal );
    
    /**
     * Tries to resolve or creates a {@link Cell} by two orthogonal {@link StripeData} instances
     * 
     * @param stripeData
     * @param stripeDataOrthogonal
     * @return
     */
    public Cell<E> resolveOrCreateCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal );
    
    /**
     * Tries to resolve or create a {@link Cell} for the given {@link Stripe} instance and its orthogonal {@link Stripe} and index
     * position relation
     * 
     * @param stripeInternal
     * @param indexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCell( StripeData<E> stripeInternal, int indexPosition );
    
    /**
     * Tries to resolve or create a {@link Cell} for the given {@link Stripe} instance and its orthogonal {@link Stripe} with the
     * given {@link Title#getValue()}
     * 
     * @param stripeInternal
     * @param titleValue
     * @return
     */
    public Cell<E> resolveOrCreateCell( StripeData<E> stripeInternal, Object titleValue );
    
    /**
     * Resolves a {@link Cell} by the {@link Row} index position and the {@link Column} title value
     * 
     * @param rowIndexPosition
     * @param columnTitleValue
     * @return
     */
    public Cell<E> resolveCell( int rowIndexPosition, Object columnTitleValue );
    
    /**
     * Resolves a {@link Cell} by the {@link Row} title value and the {@link Column} index position
     * 
     * @param rowTitleValue
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveCell( Object rowTitleValue, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} by the {@link Row} title value and the {@link Column} title value
     * 
     * @param rowTitleValue
     * @param columnTitleValue
     * @return
     */
    public Cell<E> resolveCell( Object rowTitleValue, Object columnTitleValue );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( int rowIndexPosition, int columnIndexPosition );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param rowIndexPosition
     * @param column
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( int rowIndexPosition, StripeInternal<E> column );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param row
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeInternal<E> row, int columnIndexPosition );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param stripeData
     * @param indexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeData<E> stripeData, int indexPosition );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param cellIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( int cellIndexPosition );
    
    /**
     * Resolves or creates a {@link Cell} even if it is out of the current {@link Table} area
     * 
     * @param stripeData
     * @param titleValue
     * @return
     */
    public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeData<E> stripeData, Object titleValue );
  }
  
  /**
   * Resolver for {@link Stripe} instances
   * 
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeResolver<E> extends TableComponentInternal
  {
    
    /**
     * Resolves a {@link Stripe} by its {@link StripeType} and {@link Title}
     * 
     * @param stripeType
     * @param title
     * @return
     */
    public Stripe<E> resolveStripe( StripeType stripeType, Title title );
  }
  
  /**
   * Container for {@link StripeDataList} instances
   * 
   * @author Omnaest
   */
  public static interface TableContent<E> extends TableComponentInternal, CloneableStructure<TableContent<E>>
  {
    
    /**
     * Returns the {@link StripeDataList} for the given {@link StripeType}
     * 
     * @param stripeType
     * @return
     */
    public StripeDataList<E> getStripeDataList( StripeType stripeType );
    
    /**
     * Returns the {@link Row} list
     * 
     * @return
     */
    public StripeDataList<E> getRowList();
    
    /**
     * Returns the {@link Column} list
     * 
     * @return
     */
    public StripeDataList<E> getColumnList();
    
    /**
     * Switches the {@link StripeDataList} for the {@link Row} and {@link Column}.
     */
    public void switchRowAndColumnStripeList();
    
    /**
     * Determines the size of the {@link StripeDataList} for the given {@link StripeType}
     * 
     * @param stripeType
     * @return
     */
    public int determineStripeListSize( StripeType stripeType );
    
    /**
     * Clears the {@link TableContent}
     */
    public void clear();
    
    /**
     * Returns a {@link TableSize} instance
     * 
     * @return
     */
    public TableSize getTableSize();
    
  }
  
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeDataList<E> extends Iterable<StripeData<E>>, TableComponentInternal
  {
    /* ********************************************** Methods ********************************************** */
    /**
     * Returns the {@link StripeType} information of the {@link StripeDataList}
     * 
     * @return
     */
    public StripeType getStripeType();
    
    /**
     * Sets the {@link StripeType} information of the {@link StripeDataList}
     * 
     * @param stripeType
     */
    public void setStripeType( StripeType stripeType );
    
    /**
     * Returns the index of a given {@link Stripe}
     * 
     * @param stripeData
     * @return
     */
    public int indexOf( StripeData<E> stripeData );
    
    /**
     * Returns the stripe at the given index position. If the index position is out of bounds null is returned.
     * 
     * @param indexPosition
     * @return
     */
    public StripeData<E> getStripeData( int indexPosition );
    
    /**
     * Returns the {@link Stripe} which contains the given {@link Cell}
     * 
     * @param cell
     * @return
     */
    public StripeData<E> findStripeDataContaining( Cell<E> cell );
    
    /**
     * Returns true if the {@link StripeDataList} does not contain any {@link Stripe} instance.
     * 
     * @return
     */
    public boolean isEmpty();
    
    /**
     * Returns the size of the {@link StripeDataList}
     * 
     * @return
     */
    public int size();
    
    /**
     * Clears the {@link StripeDataList}.
     */
    public void clear();
    
    /**
     * Adds a new created {@link StripeData} instance to the end of the {@link StripeDataList}
     * 
     * @return
     */
    public StripeData<E> addNewStripeData();
    
    /**
     * Adds a new created {@link StripeData} instance into the given index position of the {@link StripeDataList}. This moves an
     * already existing {@link Stripe} at that position and all following {@link Stripe} instances one position further.
     * 
     * @param indexPosition
     * @return
     */
    public StripeData<E> addNewStripeData( int indexPosition );
    
    /**
     * Adds a {@link StripeData} instance to the current {@link StripeDataList}
     * 
     * @see #addAllStripeData(Collection)
     * @param stripeData
     */
    public void addStripeData( StripeData<E> stripeData );
    
    /**
     * @see #addStripeData(StripeData)
     * @param stripeDataIterable
     */
    public void addAllStripeData( Iterable<StripeData<E>> stripeDataIterable );
    
    /**
     * Returns true if the {@link StripeDataList} contains the given {@link StripeData}
     * 
     * @param stripeData
     * @return
     */
    public boolean contains( StripeData<E> stripeData );
    
    /**
     * Returns true if the {@link StripeDataList} contains a {@link Stripe} with the given {@link Title#getValue()}
     * 
     * @param stripe
     * @return
     */
    public boolean contains( Object titleValue );
    
    /**
     * Returns the {@link StripeData} with the given {@link Title#getValue()}. If no {@link Stripe} has the given {@link Title}
     * null is returned.
     * 
     * @param titleValue
     * @return
     */
    public StripeData<E> getStripeData( Object titleValue );
    
    /**
     * Removes a {@link StripeData} from the {@link StripeDataList} and detaches its {@link Cell}s too.
     * 
     * @param stripeData
     */
    public void removeStripeAndDetachCellsFromTable( StripeData<E> stripeData );
    
    /**
     * Removes a {@link StripeData} from the {@link StripeDataList} and detaches its {@link Cell}s too.
     * 
     * @param indexPosition
     */
    public void removeStripeDataAndDetachCellsFromTable( int indexPosition );
  }
  
  /**
   * @see Stripe
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeData<E> extends TableComponentInternal
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * @see StripeData
     * @see Title
     * @author Omnaest
     */
    public static interface TitleInternal extends Title, TableComponentInternal
    {
      /**
       * Returns true if this and the given {@link Title} instance have an equal value. This uses the
       * {@link Object#equals(Object)} method to compare two values.
       * 
       * @param title
       * @return
       */
      public boolean hasEqualValueTo( Title title );
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns the {@link TitleInternal}.
     * 
     * @return
     */
    public TitleInternal getTitleInternal();
    
    /**
     * Returns true if this {@link StripeData} contains the given {@link Cell}.
     * 
     * @param cell
     * @return
     */
    public boolean contains( Cell<E> cell );
    
    /**
     * Returns true if one of the {@link Cell}s of this {@link StripeData} contains the given element
     * 
     * @param element
     * @return
     */
    public boolean contains( E element );
    
    /**
     * Adds a {@link Cell} to the {@link Stripe} if it does not contain the {@link Cell} already. This should allow registering
     * from {@link Cell} constructor.
     * 
     * @see #unregisterCell(org.omnaest.utils.structure.table.Table.Cell)
     * @param cell
     */
    public void registerCell( CellInternal<E> cell );
    
    /**
     * Removes a {@link Cell} from the {@link Stripe}
     * 
     * @see #registerCell(CellInternal<E>)
     * @param cell
     */
    public void unregisterCell( Cell<E> cell );
    
    /**
     * Resolves the {@link StripeType} from the underlying {@link StripeDataList}
     * 
     * @return
     */
    public StripeType resolveStripeType();
    
    /**
     * Returns the internal {@link Cell} {@link Set}
     * 
     * @return
     */
    public Set<CellInternal<E>> getCellSet();
    
    /**
     * Returns all {@link Cell#getElement()} instances as ordered {@link List} as currently ordered within the {@link Table}. If
     * the {@link Table} order changes the {@link List} will no change. Changes to the {@link List} are not reflected into the
     * {@link Table}.
     * 
     * @return
     */
    public List<E> getCellElementList();
    
    /**
     * Detaches all {@link Cell}s within this {@link Stripe} from the {@link Table}
     */
    public void detachAllCellsFromTable();
  }
  
  /**
   * @see Stripe
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeInternal<E> extends Column<E>, Row<E>
  {
    /**
     * Returns the underlying {@link StripeData}
     * 
     * @return
     */
    public StripeData<E> getStripeData();
    
    /**
     * Returns the {@link TableInternal} reference the current {@link StripeInternal} belongs to
     * 
     * @return
     */
    public TableInternal<E> getTableInternal();
  }
  
  /**
   * @see Cell
   * @author Omnaest
   * @param <E>
   */
  public static interface CellInternal<E> extends Cell<E>, TableComponentInternal
  {
    
    /**
     * Removes a {@link Cell} from its related {@link Row} and {@link Column}.
     * 
     * @return
     */
    public Cell<E> detachFromTable();
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see TableContent
   * @return
   */
  public TableContent<E> getTableContent();
  
  /**
   * Returns the {@link CellAndStripeResolver} of the {@link Table}
   * 
   * @return
   */
  public CellAndStripeResolver<E> getCellAndStripeResolver();
  
  /**
   * Returns the underlying {@link Table} instance
   * 
   * @return
   */
  public Table<E> getUnderlyingTable();
  
}
