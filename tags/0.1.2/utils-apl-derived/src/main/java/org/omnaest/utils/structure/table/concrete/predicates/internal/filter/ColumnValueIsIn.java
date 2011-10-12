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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ColumnValueIsIn<E> implements PredicateFilter<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long       serialVersionUID  = -8287655781277028388L;
  
  /* ********************************************** Variables ********************************************** */
  protected Collection<Column<E>> columnCollection  = null;
  protected Collection<E>         elementCollection = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param column
   * @param elementCollection
   */
  public ColumnValueIsIn( Collection<Column<E>> columnCollection, Collection<E> elementCollection )
  {
    super();
    this.columnCollection = columnCollection;
    this.elementCollection = elementCollection;
  }
  
  /**
   * @param columnCollection
   * @param elements
   */
  public ColumnValueIsIn( Collection<Column<E>> columnCollection, E... elements )
  {
    this( columnCollection, Arrays.asList( elements ) );
  }
  
  /**
   * @param column
   * @param elementCollection
   */
  @SuppressWarnings("unchecked")
  public ColumnValueIsIn( Column<E> column, Collection<E> elementCollection )
  {
    this( Arrays.asList( column ), elementCollection );
  }
  
  /**
   * @param column
   * @param elements
   */
  @SuppressWarnings("unchecked")
  public ColumnValueIsIn( Column<E> column, E... elements )
  {
    this( Arrays.asList( column ), Arrays.asList( elements ) );
  }
  
  /**
   * @param elementCollection
   * @param columns
   */
  public ColumnValueIsIn( Collection<E> elementCollection, Column<E>... columns )
  {
    this( Arrays.asList( columns ), elementCollection );
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
        Map<Column<E>, Set<StripeData<E>>> columnToStripeDataListMap = new HashMap<Column<E>, Set<StripeData<E>>>();
        for ( Column<E> column : columnCollectionMatching )
        {
          //
          ScannableStripeDataContainer<E> scannableStripeDataContainer = tableBlock.getColumnToScannableStripeDataContainerMap()
                                                                                   .get( column );
          if ( scannableStripeDataContainer != null )
          {
            Set<StripeData<E>> rowStripeDataSetContainingAnyElement = new LinkedHashSet<StripeData<E>>();
            for ( E element : this.elementCollection )
            {
              rowStripeDataSetContainingAnyElement.addAll( scannableStripeDataContainer.determineStripeDataListContainingElement( element ) );
            }
            columnToStripeDataListMap.put( column, rowStripeDataSetContainingAnyElement );
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
