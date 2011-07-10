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

import java.util.Map;

import org.omnaest.utils.structure.table.view.TableView;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 * @param <T>
 */
public interface TableSelectable<E, T extends Table<E>>
{
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * {@link Selection} from a {@link Table}
   * 
   * @see Join
   * @see Where
   * @see Order
   * @see SelectionResult
   * @see TableSelectable
   * @see Table
   */
  public static interface Selection<E> extends Table<E>
  {
    /**
     * Sets the {@link Column}s of the {@link Selection}.
     * 
     * @param columns
     * @return
     */
    public Selection<E> setColumns( Column<E>... columns );
    
    /**
     * Sets the {@link Table}
     * 
     * @param table
     * @return
     */
    public Selection<E> setTable( Table<E> table );
    
    /**
     * {@link Join} clause of a {@link Selection}
     * 
     * @param join
     * @return
     */
    public Selection<E> join( Join join );
    
    /**
     * {@link Where} clause of a {@link Selection}
     * 
     * @param where
     * @return
     */
    public Selection<E> where( Where where );
    
    /**
     * {@link Order} clause of a {@link Selection}
     * 
     * @param order
     * @return
     */
    public Selection<E> orderBy( Order order );
    
    /**
     * Creates the {@link SelectionResult} of the {@link Selection}. This should be called after all other configurations have
     * been set.
     * 
     * @return selection {@link SelectionResult}
     */
    public SelectionResult<E> selectionResult();
    
    /**
     * Merges {@link Row}s if there {@link Cell} elements are all equal
     * 
     * @return this
     */
    public Selection<E> distinct();
  }
  
  /**
   * @see Table
   * @see TableView
   * @see Selection
   * @author Omnaest
   * @param <E>
   */
  public static interface SelectionResult<E> extends TableView<E>
  {
    
  }
  
  /**
   * {@link Order} clause of a {@link Selection}.
   * 
   * @see Table
   * @see Selection
   * @author Omnaest
   */
  public static interface Order
  {
    
  }
  
  /**
   * {@link Where} clause of a {@link Selection}
   * 
   * @see Selection
   * @author Omnaest
   */
  public static interface Where
  {
  }
  
  /**
   * {@link Join} clause of a {@link Selection}.
   * 
   * @author Omnaest
   */
  public static interface Join
  {
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @see Table
   * @see Selection
   */
  public Selection<E> select();
  
  /**
   * @see #whereElementEquals(int, Comparable)
   * @param columnTitleEnum
   * @param element
   * @return new table
   */
  public T whereElementEquals( Enum<?> columnTitleEnum, E element );
  
  /**
   * @see #whereElementEquals(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public T whereElementEquals( String columnTitle, E element );
  
  /**
   * Returns a new indexed table where all rows from the table where the elements of a given column do match are included.
   * 
   * @see #whereElementEquals(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return new table
   */
  public T whereElementEquals( int columnIndexPosition, E element );
  
  /**
   * Returns a new indexed table where all rows from the table where the elements of a given column do match all criteria given by
   * a map of column index positions and elements to be matched.
   * 
   * @see #whereElementEquals(int, Comparable)
   * @param columnIndexToElementMap
   * @return
   */
  public T whereElementEqualsColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
  
  /**
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param columnTitleToElementMap
   * @return
   */
  public T whereElementEqualsColumnTitleMap( Map<String, E> columnTitleToElementMap );
  
  /**
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param columnEnumToElementMap
   * @return
   */
  public T whereElementEqualsColumnEnumMap( Map<Enum<?>, E> columnEnumToElementMap );
  
  /**
   * @see #whereElementEqualsColumnTitleMap(Map)
   * @param beanObject
   * @return
   */
  public <B> T whereElementEqualsBeanObject( B beanObject );
  
  /**
   * Ignoring all values of the bean object which are null. Only the other properties are used for the equal constrain. This
   * allows to fill a bean partially and make a where statement for the values filled into the bean.
   * 
   * @see #whereElementEqualsColumnIndexMap(Map)
   * @param beanObject
   * @return
   */
  public <B> T whereElementEqualsBeanObjectIgnoringNullValues( B beanObject );
  
  /**
   * @see #whereElementIsBetween(int, Comparable, Comparable)
   * @param columnTitle
   * @param lowerElement
   * @param upperElement
   * @return new table
   */
  public T whereElementIsBetween( String columnTitle, E lowerElement, E upperElement );
  
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
  public T whereElementIsBetween( int columnIndexPosition, E lowerElement, E upperElement );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public T whereElementIsGreaterThan( String columnTitle, E element );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnIndexToElementMap
   * @return
   */
  public T whereElementIsGreaterThanColumnIndexMap( Map<Integer, E> columnIndexToElementMap );
  
  /**
   * @see #whereElementIsGreaterThanColumnIndexMap(Map)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitleToElementMap
   * @return
   */
  public T whereElementIsGreaterThanColumnTitleMap( Map<String, E> columnTitleToElementMap );
  
  /**
   * @see #whereElementIsGreaterThanBeanObjectIgnoringNullValues(Object)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @see #whereElementIsGreaterThanColumnIndexMap(Map)
   * @param beanObject
   * @return
   */
  public <B> T whereElementIsGreaterThanBeanObject( B beanObject );
  
  /**
   * @see #whereElementIsGreaterThanBeanObject(Object)
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @see #whereElementIsGreaterThanColumnTitleMap(Map)
   * @param beanObject
   * @return
   */
  public <B> T whereElementIsGreaterThanBeanObjectIgnoringNullValues( B beanObject );
  
  /**
   * @see #whereElementIsGreaterThan(int, Comparable)
   * @param columnTitleEnum
   * @param element
   * @return
   */
  public T whereElementIsGreaterThan( Enum<?> columnTitleEnum, E element );
  
  /**
   * @see #whereElementIsLesserThan(int, Comparable)
   * @param columnTitle
   * @param element
   * @return new table
   */
  public T whereElementIsLesserThan( String columnTitle, E element );
  
  /**
   * Returns a new table with all rows from the current table where elements in the given column are greater than the given
   * element.
   * 
   * @see #whereElementIsLesserThan(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return new table
   */
  public T whereElementIsGreaterThan( int columnIndexPosition, E element );
  
  /**
   * Returns a new table where all rows of the current table where elements in the given column and are lesser than the given
   * element.
   * 
   * @see #whereElementIsLesserThan(String, Comparable)
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public T whereElementIsLesserThan( int columnIndexPosition, E element );
  
  /**
   * Sorts the whole current table by the content of a given column.<br>
   * The underlying sort uses a merge sort algorithm.
   * 
   * @param columnIndexPosition
   * @param ascending
   *          true:ascending false:descending
   * @return this
   */
  public T orderRowsBy( final int columnIndexPosition, final boolean ascending );
  
  /**
   * Orders the rows ascending by the property of a java bean which is not null. <br>
   * If more than one property is not null, it is unspecified by which property the table is sorted.<br>
   * The property, which is not null, has to equal a column title.
   * 
   * @param beanObject
   * @return this
   */
  public <B> T orderRowsAscendingByBeanObjectPropertyNotNull( B beanObject );
  
  /**
   * @param columnTitle
   * @return
   */
  public T orderRowsAscendingBy( String columnTitle );
}
