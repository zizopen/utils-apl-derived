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
package org.omnaest.utils.structure.table.subspecification;

import java.io.Serializable;
import java.util.Map;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * Defines the {@link TableSelectable#select()} method of a {@link Table}
 * 
 * @see Selection
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface TableSelectable<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link Selection} from a {@link Table}. The {@link Selection} uses a builder pattern to create a {@link TableView} or a new
   * {@link Table} instance based on parameters like {@link Table}s, {@link Predicate}s and {@link Order}
   * 
   * @see Table#select()
   * @see Selection#columns(Column...)
   * @see Selection#allColumns()
   * @see Selection#from(Table)
   * @see Selection#where(Predicate...)
   * @see Selection#orderBy(Order)
   * @see Selection#asView()
   * @see Selection#asTable()
   * @see SelectionJoin
   * @see Predicate
   * @see Order
   * @see TableView
   * @see TableSelectable
   * @see Table
   */
  public static interface Selection<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see Selection
     * @see Selection#orderBy(Column, Order)
     * @author Omnaest
     */
    public static enum Order
    {
      ASCENDING,
      DESCENDING;
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Sets the {@link Column}s of the {@link Selection}.
     * 
     * @param columns
     * @return this
     */
    public Selection<E> columns( Column<E>... columns );
    
    /**
     * Sets all {@link Column}s to be selected
     * 
     * @return this
     */
    public Selection<E> allColumns();
    
    /**
     * Sets the {@link Table}
     * 
     * @param tables
     * @return this
     */
    public Selection<E> from( Table<E>... tables );
    
    /**
     * {@link Join} clause of a {@link Selection}
     * 
     * @param table
     * @return this as {@link SelectionJoin}
     */
    public SelectionJoin<E> innerJoin( Table<E> table );
    
    /**
     * Where clause of a {@link Selection} which declares one ore multiple {@link Predicate}s
     * 
     * @param predicates
     * @return this
     */
    public Selection<E> where( Predicate<E>... predicates );
    
    /**
     * Adds a {@link Column} and {@link Order} declaration for a {@link Selection} which results in an ordered {@link Table} after
     * the given {@link Column} by the given {@link Order}. If this method is called multiple times the {@link Column}s and
     * {@link Order} will be chained together.
     * 
     * @see #orderBy(Column)
     * @param column
     * @param order
     * @return this
     */
    public Selection<E> orderBy( Column<E> column, Order order );
    
    /**
     * Orders the {@link Selection} after the given {@link Column} by {@link Order#ASCENDING}
     * 
     * @see #orderBy(Column, Order)
     * @param column
     * @return
     */
    public Selection<E> orderBy( Column<E> column );
    
    /**
     * Creates a {@link TableView} of the {@link Selection}. This should be called after all other configurations have been set.
     * 
     * @return {@link TableView}
     */
    public TableView<E> asView();
    
    /**
     * Merges {@link Row}s if there {@link Cell} elements are all equal
     * 
     * @return this
     */
    public Selection<E> distinct();
    
    /**
     * Returns the declared {@link Selection} as a new {@link Table} instance. . This should be called after all other
     * configurations have been set.
     * 
     * @return
     */
    public Table<E> asTable();
  }
  
  /**
   * A {@link Predicate} defines the condition which has to full filled by a {@link Row} to make her included into the result of a
   * {@link Selection}
   * 
   * @param <E>
   * @see Where
   * @author Omnaest
   */
  public static interface Predicate<E> extends Serializable
  {
  }
  
  /**
   * {@link Where} clause of a {@link Selection}
   * 
   * @see Selection
   * @see Predicate
   * @author Omnaest
   * @deprecated
   */
  @Deprecated
  public static interface Where<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see #whereElementEquals(int, Comparable)
     * @param columnTitleEnum
     * @param element
     * @return new table
     */
    public Table<E> whereElementEquals( Enum<?> columnTitleEnum, E element );
    
    /**
     * @see #whereElementEquals(int, Comparable)
     * @param columnTitle
     * @param element
     * @return new table
     */
    public Table<E> whereElementEquals( String columnTitle, E element );
    
    /**
     * Returns a new indexed table where all rows from the table where the elements of a given column do match are included.
     * 
     * @see #whereElementEquals(String, Comparable)
     * @param columnIndexPosition
     * @param element
     * @return new table
     */
    public Table<E> whereElementEquals( int columnIndexPosition, E element );
    
    /**
     * Returns a new indexed table where all rows from the table where the elements of a given column do match all criteria given
     * by a map of column index positions and elements to be matched.
     * 
     * @see #whereElementEquals(int, Comparable)
     * @param columnIndexToElementMap
     * @return
     */
    public Table<E> whereElementEqualsColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
    
    /**
     * @see #whereElementEqualsColumnIndexMap(Map)
     * @param columnTitleToElementMap
     * @return
     */
    public Table<E> whereElementEqualsColumnTitleMap( Map<String, E> columnTitleToElementMap );
    
    /**
     * @see #whereElementEqualsColumnIndexMap(Map)
     * @param columnEnumToElementMap
     * @return
     */
    public Table<E> whereElementEqualsColumnEnumMap( Map<Enum<?>, E> columnEnumToElementMap );
    
    /**
     * @see #whereElementEqualsColumnTitleMap(Map)
     * @param beanObject
     * @return
     */
    public <B> Table<E> whereElementEqualsBeanObject( B beanObject );
    
    /**
     * Ignoring all values of the bean object which are null. Only the other properties are used for the equal constrain. This
     * allows to fill a bean partially and make a where statement for the values filled into the bean.
     * 
     * @see #whereElementEqualsColumnIndexMap(Map)
     * @param beanObject
     * @return
     */
    public <B> Table<E> whereElementEqualsBeanObjectIgnoringNullValues( B beanObject );
    
    /**
     * @see #whereElementIsBetween(int, Comparable, Comparable)
     * @param columnTitle
     * @param lowerElement
     * @param upperElement
     * @return new table
     */
    public Table<E> whereElementIsBetween( String columnTitle, E lowerElement, E upperElement );
    
    /**
     * Returns a new table with all rows from the table where the elements of the given column are between the range created by
     * the lower and upper element given as parameter.
     * 
     * @see #whereElementIsBetween(String, Comparable, Comparable)
     * @param columnIndexPosition
     * @param lowerElement
     * @param upperElement
     * @return new table
     */
    public Table<E> whereElementIsBetween( int columnIndexPosition, E lowerElement, E upperElement );
    
    /**
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @param columnTitle
     * @param element
     * @return new table
     */
    public Table<E> whereElementIsGreaterThan( String columnTitle, E element );
    
    /**
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @param columnIndexToElementMap
     * @return
     */
    public Table<E> whereElementIsGreaterThanColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
    
    /**
     * @see #whereElementIsGreaterThanColumnIndexMap(Map)
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @param columnTitleToElementMap
     * @return
     */
    public Table<E> whereElementIsGreaterThanColumnTitleMap( Map<String, E> columnTitleToElementMap );
    
    /**
     * @see #whereElementIsGreaterThanBeanObjectIgnoringNullValues(Object)
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @see #whereElementIsGreaterThanColumnIndexMap(Map)
     * @param beanObject
     * @return
     */
    public <B> Table<E> whereElementIsGreaterThanBeanObject( B beanObject );
    
    /**
     * @see #whereElementIsGreaterThanBeanObject(Object)
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @see #whereElementIsGreaterThanColumnTitleMap(Map)
     * @param beanObject
     * @return
     */
    public <B> Table<E> whereElementIsGreaterThanBeanObjectIgnoringNullValues( B beanObject );
    
    /**
     * @see #whereElementIsGreaterThan(int, Comparable)
     * @param columnTitleEnum
     * @param element
     * @return
     */
    public Table<E> whereElementIsGreaterThan( Enum<?> columnTitleEnum, E element );
    
    /**
     * @see #whereElementIsLesserThan(int, Comparable)
     * @param columnTitle
     * @param element
     * @return new table
     */
    public Table<E> whereElementIsLesserThan( String columnTitle, E element );
    
    /**
     * Returns a new table with all rows from the current table where elements in the given column are greater than the given
     * element.
     * 
     * @see #whereElementIsLesserThan(String, Comparable)
     * @param columnIndexPosition
     * @param element
     * @return new table
     */
    public Table<E> whereElementIsGreaterThan( int columnIndexPosition, E element );
    
    /**
     * Returns a new table where all rows of the current table where elements in the given column and are lesser than the given
     * element.
     * 
     * @see #whereElementIsLesserThan(String, Comparable)
     * @param columnIndexPosition
     * @param element
     * @return
     */
    public Table<E> whereElementIsLesserThan( int columnIndexPosition, E element );
    
  }
  
  /**
   * A {@link SelectionJoin} represents the underlying {@link Selection} and allows additionally to add further {@link Predicate}s
   * to a join using the {@link #on(Predicate...)} method.
   * 
   * @param
   * @author Omnaest
   */
  public static interface SelectionJoin<E> extends Selection<E>
  {
    /**
     * Adds further {@link Predicate}s to an {@link SelectionJoin}
     * 
     * @param predicate
     * @return this
     */
    public SelectionJoin<E> on( Predicate<E>... predicates );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * A {@link Selection} offers methods to select a subset of {@link Row}s and {@link Column}s of a {@link Table} and provide the
   * result as further {@link Table} or {@link TableView}
   * 
   * @see Selection
   * @see Table
   */
  public Selection<E> select();
  
}
