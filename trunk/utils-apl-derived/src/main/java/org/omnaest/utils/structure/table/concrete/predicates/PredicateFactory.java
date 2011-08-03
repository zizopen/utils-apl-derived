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

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnValueEquals;
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
