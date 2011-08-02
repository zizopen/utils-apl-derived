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
package org.omnaest.utils.structure.table.concrete.predicates.internal;

import java.util.HashSet;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * @see Predicate
 * @see PredicateInternal
 * @author Omnaest
 */
public class ColumnValueEquals<E> implements PredicateInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -8287655781277028388L;
  
  /* ********************************************** Variables ********************************************** */
  protected Column<E>       column           = null;
  protected E               element          = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param column
   * @param element
   */
  public ColumnValueEquals( Column<E> column, E element )
  {
    super();
    this.column = column;
    this.element = element;
  }
  
  @Override
  public void filterStripeDataSet( TableBlock<E> tableBlock )
  {
    //
    Set<StripeData<E>> remainingStripeDataSet = new HashSet<StripeData<E>>();
    
    //
    StripeData<E> columnStripeData = StripeDataHelper.extractStripeDataFromStripe( this.column );
    if ( columnStripeData != null )
    {
      //
      Set<CellData<E>> cellDataSet = columnStripeData.findCellDataSetHavingCellElement( this.element );
      if ( cellDataSet != null )
      {
        for ( CellData<E> cellData : cellDataSet )
        {
          //
          StripeData<E> stripeData = tableBlock.getTableInternal()
                                               .getTableContent()
                                               .getRowStripeDataList()
                                               .findStripeDataContaining( cellData );
          
          //
          if ( stripeData != null )
          {
            remainingStripeDataSet.add( stripeData );
          }
        }
        
      }
      
    }
    
    //
    tableBlock.getRowStripeDataSet().retainAll( remainingStripeDataSet );
    
    //FIXME improve performance here
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Column<E>[] requiredColumns()
  {
    return new Column[] { this.column };
  }
}
