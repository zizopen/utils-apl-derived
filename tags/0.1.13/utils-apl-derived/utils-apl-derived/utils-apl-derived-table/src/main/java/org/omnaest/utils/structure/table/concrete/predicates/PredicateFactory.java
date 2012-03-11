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
package org.omnaest.utils.structure.table.concrete.predicates;

import java.util.Arrays;
import java.util.Collection;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnValueEquals;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnValueIsBetween;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnValueIsIn;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.EqualColumns;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

public class PredicateFactory<E>
{
  
  /**
   * @see ColumnValueEquals
   * @param column
   * @param element
   * @return
   */
  public static <E> Predicate<E> columnValueEquals( Column<E> column, E element )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( column != null )
    {
      retval = new ColumnValueEquals<E>( column, element );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsIn
   * @param column
   * @param elementCollection
   * @return
   */
  public static <E> Predicate<E> columnValueIsIn( Collection<E> elementCollection, Column<E>... columns )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columns != null )
    {
      retval = new ColumnValueIsIn<E>( elementCollection, columns );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsIn
   * @param columnCollection
   * @param elementCollection
   * @return
   */
  public static <E> Predicate<E> columnValueIsIn( Collection<Column<E>> columnCollection, Collection<E> elementCollection )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columnCollection != null )
    {
      retval = new ColumnValueIsIn<E>( columnCollection, elementCollection );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsIn
   * @param columnCollection
   * @param elements
   * @return
   */
  public static <E> Predicate<E> columnValueIsIn( Collection<Column<E>> columnCollection, E... elements )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columnCollection != null )
    {
      retval = new ColumnValueIsIn<E>( columnCollection, elements );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsIn
   * @param column
   * @param elementCollection
   * @return
   */
  public static <E> Predicate<E> columnValueIsIn( Column<E> column, Collection<E> elementCollection )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( column != null )
    {
      retval = new ColumnValueIsIn<E>( column, elementCollection );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsIn
   * @param column
   * @param elements
   * @return
   */
  public static <E> Predicate<E> columnValueIsIn( Column<E> column, E... elements )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( column != null )
    {
      retval = new ColumnValueIsIn<E>( column, elements );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueEquals
   * @param column
   * @param element
   * @return
   */
  public static <E> Predicate<E> columnValueEquals( E element, Column<E>... columns )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columns != null )
    {
      retval = new ColumnValueEquals<E>( element, columns );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueEquals
   * @param columnCollection
   * @param element
   * @return
   */
  public static <E> Predicate<E> columnValueEquals( Collection<Column<E>> columnCollection, E element )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columnCollection != null )
    {
      retval = new ColumnValueEquals<E>( columnCollection, element );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsBetween
   * @param column
   * @param elementFrom
   * @param elementTo
   * @return
   */
  public static <E> Predicate<E> columnValueIsBetween( Column<E> column, E elementFrom, E elementTo )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( column != null )
    {
      retval = new ColumnValueIsBetween<E>( column, elementFrom, elementTo );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsBetween
   * @param column
   * @param elementFrom
   * @param elementTo
   * @return
   */
  public static <E> Predicate<E> columnValueIsBetween( E elementFrom, E elementTo, Column<E>... columns )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columns != null )
    {
      retval = new ColumnValueIsBetween<E>( elementFrom, elementTo, columns );
    }
    
    //
    return retval;
  }
  
  /**
   * @see ColumnValueIsBetween
   * @param columnCollection
   * @param elementFrom
   * @param elementTo
   * @return
   */
  public static <E> Predicate<E> columnValueIsBetween( Collection<Column<E>> columnCollection, E elementFrom, E elementTo )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columnCollection != null )
    {
      retval = new ColumnValueIsBetween<E>( columnCollection, elementFrom, elementTo );
    }
    
    //
    return retval;
  }
  
  /**
   * @see EqualColumns
   * @param columns
   * @return
   */
  public static <E> Predicate<E> equalColumns( Column<E>... columns )
  {
    //    
    Predicate<E> retval = null;
    
    //
    if ( columns != null )
    {
      retval = new EqualColumns<E>( Arrays.asList( columns ) );
    }
    
    //
    return retval;
  }
}
