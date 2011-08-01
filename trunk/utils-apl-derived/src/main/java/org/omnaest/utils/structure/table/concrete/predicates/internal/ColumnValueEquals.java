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

import java.util.Set;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
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
  protected Object          cellElement      = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param column
   * @param cellElement
   */
  public ColumnValueEquals( Column<E> column, Object cellElement )
  {
    super();
    this.column = column;
    this.cellElement = cellElement;
  }
  
  @Override
  public void filterStripeDataSet( TableInternal<E> tableInternal )
  {
    //
    StripeData<E> stripeData = StripeDataHelper.extractStripeDataFromStripe( this.column );
    
    //
    Set<CellData<E>> cellDataSet = stripeData.getCellDataSet();
    
    //FIXME go on here
    
  }
  
  @Override
  public Column<E>[] requiredColumns()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
