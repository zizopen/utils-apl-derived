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

import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable {@link Table}
 * 
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableTable<E> extends Iterable<ImmutableRow<E>>, StripeTransformerPluginRegistration<E>
{
  
  /**
   * Returns an {@link ImmutableCell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link ImmutableCell} instance
   */
  public ImmutableCell<E> cell( int rowIndex, int columnIndex );
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableCell}s which traverses through the {@link ImmutableRow}s traversing
   * through their {@link ImmutableRow#cells()}
   * 
   * @return
   */
  public Iterable<? extends ImmutableCell<E>> cells();
  
  /**
   * Clones the current {@link ImmutableTable} structure into a new instance
   * 
   * @return new instance
   */
  public ImmutableTable<E> clone();
  
  /**
   * Returns a new {@link ImmutableColumn} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link ImmutableColumn} instance
   */
  public ImmutableColumn<E> column( int columnIndex );
  
  /**
   * Similar to {@link #column(int)} based on the first matching column title
   * 
   * @param columnTitle
   * @return
   */
  public ImmutableColumn<E> column( String columnTitle );
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableColumn}s
   * 
   * @return
   */
  public Iterable<? extends ImmutableColumn<E>> columns();
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableColumn}s which have a column title matched by the given {@link Matcher}
   * 
   * @return
   */
  public Iterable<? extends ImmutableColumn<E>> columns( Pattern columnTitlePattern );
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableColumn}s which have a column title included in the given {@link Set} of
   * titles
   * 
   * @return
   */
  public Iterable<? extends ImmutableColumn<E>> columns( Set<String> columnTitleSet );
  
  /**
   * Returns all {@link ImmutableColumn}s which have a column title included in the given titles
   * 
   * @param columnTitles
   * @return
   */
  public Iterable<? extends ImmutableColumn<E>> columns( String... columnTitles );
  
  /**
   * Returns the number of {@link Column}s
   * 
   * @return
   */
  public int columnSize();
  
  /**
   * Returns the component type of the {@link Table}
   * 
   * @return
   */
  public Class<E> elementType();
  
  /**
   * Returns true if the content of this and the given {@link ImmutableTable} are {@link #equals(Object)}
   * 
   * @param table
   *          {@link ImmutableTable}
   * @return
   */
  public boolean equalsInContent( ImmutableTable<E> table );
  
  /**
   * Returns true if the content of this and the given {@link ImmutableTable} are {@link #equals(Object)}, and if the table name,
   * the row and column titles are equal
   * 
   * @param table
   *          {@link ImmutableTable}
   * @return
   */
  public boolean equalsInContentAndMetaData( ImmutableTable<E> table );
  
  /**
   * Executes a {@link TableExecution} with a table wide {@link ReadLock}
   * 
   * @see #executeWithReadLock(TableExecution, ImmutableTable...)
   * @param tableExecution
   *          {@link TableExecution}
   * @return this
   */
  public ImmutableTable<E> executeWithReadLock( TableExecution<ImmutableTable<E>, E> tableExecution );
  
  /**
   * Executes a {@link TableExecution} with a table wide {@link ReadLock} on the current and all further given
   * {@link ImmutableTable}s
   * 
   * @see #executeWithReadLock(TableExecution)
   * @param tableExecution
   *          {@link TableExecution}
   * @param furtherLockedTables
   *          {@link Table}
   * @return this
   */
  public ImmutableTable<E> executeWithReadLock( TableExecution<ImmutableTable<E>, E> tableExecution,
                                                ImmutableTable<E>... furtherLockedTables );
  
  /**
   * Returns the column index position for the given column title. If no column title matches -1 is returned
   * 
   * @param columnTitle
   * @return
   */
  public int getColumnIndex( String columnTitle );
  
  /**
   * Returns the title of the column with the given index position
   * 
   * @param columnIndex
   * @return
   */
  public String getColumnTitle( int columnIndex );
  
  /**
   * Returns a {@link List} of all column titles
   * 
   * @return
   */
  public List<String> getColumnTitleList();
  
  /**
   * Returns the element at the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return
   */
  public E getElement( int rowIndex, int columnIndex );
  
  /**
   * Similar to {@link #getElement(int, int)}
   * 
   * @param rowIndex
   * @param columnTitle
   * @return element instance
   */
  public E getElement( int rowIndex, String columnTitle );
  
  /**
   * Similar to {@link #getElement(int, int)}
   * 
   * @param rowTitle
   * @param columnIndex
   * @return element instance
   */
  public E getElement( String rowTitle, int columnIndex );
  
  /**
   * Similar to {@link #getElement(int, int)}
   * 
   * @param rowTitle
   * @param columnTitle
   * @return element instance
   */
  public E getElement( String rowTitle, String columnTitle );
  
  /**
   * Returns the title of the row with the given index position
   * 
   * @param rowIndex
   * @return
   */
  public String getRowTitle( int rowIndex );
  
  /**
   * Returns a {@link List} of all row titles
   * 
   * @return
   */
  public List<String> getRowTitleList();
  
  /**
   * Returns the table name
   * 
   * @return
   */
  public String getTableName();
  
  /**
   * Returns true if the {@link ImmutableTable} has column titles
   * 
   * @return
   */
  public boolean hasColumnTitles();
  
  /**
   * Returns true if the {@link ImmutableTable} has row titles
   * 
   * @return
   */
  public boolean hasRowTitles();
  
  /**
   * Returns true if the {@link ImmutableTable} has a table name
   * 
   * @return
   */
  public boolean hasTableName();
  
  /**
   * Returns the {@link TableIndexManager} instance which allows to create {@link TableIndex} instances based on
   * {@link ImmutableColumn}s of the {@link ImmutableTable}
   * 
   * @return
   */
  public TableIndexManager<E, ? extends ImmutableCell<E>> index();
  
  /**
   * Returns the last {@link ImmutableRow}
   * 
   * @return
   */
  public ImmutableRow<E> lastRow();
  
  @Override
  public ImmutableTable<E> register( StripeTransformerPlugin<E, ?> stripeTransformerPlugin );
  
  /**
   * Returns a new {@link ImmutableRow} currently related to the given row index position
   * 
   * @param rowIndex
   * @return new {@link ImmutableRow} instance
   */
  public ImmutableRow<E> row( int rowIndex );
  
  /**
   * Similar to {@link #row(int)} based on the first matching row title
   * 
   * @param rowTitle
   * @return
   */
  public ImmutableRow<E> row( String rowTitle );
  
  /**
   * Returns an {@link Iterable} instance over all {@link ImmutableRow}s of the {@link Table}
   * 
   * @return new {@link Rows}
   */
  public Rows<E, ? extends ImmutableRow<E>> rows();
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableRow}s where the row index position has an enabled bit within the filter
   * {@link BitSet}
   * 
   * @param indexFilter
   * @return new {@link Rows}
   */
  public Rows<E, ? extends ImmutableRow<E>> rows( BitSet indexFilter );
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableRow}s which are between the two given row index positions. The lower
   * index is inclusive the upper index position is exclusive.
   * 
   * @param rowIndexFrom
   * @param rowIndexTo
   * @return new {@link Rows}
   */
  public Rows<E, ? extends ImmutableRow<E>> rows( int rowIndexFrom, int rowIndexTo );
  
  /**
   * Returns the number of {@link Row}s
   * 
   * @return
   */
  public int rowSize();
  
  /**
   * Returns a new {@link TableSelect} instance which allows to select areas or joining with other {@link ImmutableTable}
   * instances
   * 
   * @return
   */
  public TableSelect<E> select();
  
  /**
   * Returns a {@link ImmutableTableSerializer} instance
   * 
   * @return
   */
  public ImmutableTableSerializer<E> serializer();
  
  /**
   * Returns a {@link TableTransformer} instance
   * 
   * @return
   */
  public TableTransformer<E> to();
  
  /**
   * Transforms the given {@link ImmutableStripe} into an instance of the given {@link Class} type. This requires a
   * {@link StripeTransformerPlugin} being registered using {@link #register(StripeTransformerPlugin)} before.
   * 
   * @param type
   * @param stripe
   *          {@link ImmutableStripe}
   * @return new instance
   */
  public <T> T transformStripeInto( Class<T> type, ImmutableStripe<E> stripe );
  
  /**
   * Similar to {@link #transformStripeInto(Class, ImmutableStripe)} using the given instance and returning it in an enriched
   * manner.
   * 
   * @param instance
   * @param stripe
   * @return given instance
   */
  public <T> T transformStripeInto( T instance, ImmutableStripe<E> stripe );
}
