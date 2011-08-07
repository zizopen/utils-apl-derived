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
package org.omnaest.utils.structure.table.concrete.predicates.internal.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.concrete.internal.selection.scannable.ScannableStripeDataContainer;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * @see Predicate
 * @see PredicateFilter
 * @author Omnaest
 */
public class ColumnValueEquals<E> implements PredicateFilter<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long       serialVersionUID = -8287655781277028388L;
  
  /* ********************************************** Variables ********************************************** */
  protected Collection<Column<E>> columnCollection = null;
  protected E                     element          = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param column
   * @param element
   */
  public ColumnValueEquals( Collection<Column<E>> columnCollection, E element )
  {
    super();
    this.columnCollection = columnCollection;
    this.element = element;
  }
  
  /**
   * @param column
   * @param element
   */
  @SuppressWarnings("unchecked")
  public ColumnValueEquals( Column<E> column, E element )
  {
    this( Arrays.asList( column ), element );
  }
  
  /**
   * @param column
   * @param element
   */
  public ColumnValueEquals( E element, Column<E>... columns )
  {
    this( Arrays.asList( columns ), element );
  }
  
  @Override
  public void filterStripeDataSet( Collection<TableBlock<E>> tableBlockCollection )
  {
    //
    if ( tableBlockCollection != null && !tableBlockCollection.isEmpty() )
    {
      for ( TableBlock<E> tableBlock : tableBlockCollection )
      {
        //
        Collection<Column<E>> columnCollectionMatching = new HashSet<Column<E>>( this.columnCollection );
        columnCollectionMatching.retainAll( tableBlock.getColumnList() );
        
        //
        Map<Column<E>, List<StripeData<E>>> columnToStripeDataListMap = new HashMap<Column<E>, List<StripeData<E>>>();
        for ( Column<E> column : columnCollectionMatching )
        {
          //
          ScannableStripeDataContainer<E> scannableStripeDataContainer = tableBlock.getColumnToScannableStripeDataContainerMap()
                                                                                   .get( column );
          if ( scannableStripeDataContainer != null )
          {
            List<StripeData<E>> rowStripeDataSetContainingElement = scannableStripeDataContainer.determineStripeDataListContainingElement( this.element );
            columnToStripeDataListMap.put( column, rowStripeDataSetContainingElement );
          }
        }
        
        //
        List<StripeData<E>> stripeDataListIntersection = ListUtils.intersection( columnToStripeDataListMap.values() );
        tableBlock.getRowStripeDataSet().retainAll( stripeDataListIntersection );
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Column<E>[] getRequiredColumns()
  {
    return this.columnCollection.toArray( new Column[0] );
  }
  
}
