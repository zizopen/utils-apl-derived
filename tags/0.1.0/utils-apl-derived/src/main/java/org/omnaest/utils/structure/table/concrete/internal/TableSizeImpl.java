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
package org.omnaest.utils.structure.table.concrete.internal;

import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;

/**
 * @see TableSize
 * @author Omnaest
 */
public class TableSizeImpl implements TableSize
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -3977701815617289460L;
  /* ********************************************** Variables ********************************************** */
  protected TableContent<?> tableContent     = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param stripeListContainer
   */
  public TableSizeImpl( TableContent<?> stripeListContainer )
  {
    super();
    this.tableContent = stripeListContainer;
  }
  
  @Override
  public int getCellSize()
  {
    return this.getRowSize() * this.getColumnSize();
  }
  
  @Override
  public int getRowSize()
  {
    return this.getStripeListContainer().getRowStripeDataList().size();
  }
  
  @Override
  public int getColumnSize()
  {
    return this.getStripeListContainer().getColumnStripeDataList().size();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( Integer.valueOf( this.getColumnSize() ).hashCode() );
    result = prime * result + ( Integer.valueOf( this.getRowSize() ).hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object object )
  {
    //    
    boolean retval = false;
    
    //
    if ( object instanceof TableSize )
    {
      //
      TableSize tableSizeOther = (TableSize) object;
      
      //
      retval = this.getColumnSize() == tableSizeOther.getColumnSize() && this.getRowSize() == tableSizeOther.getRowSize();
    }
    
    //
    return retval;
  }
  
  protected TableContent<?> getStripeListContainer()
  {
    return this.tableContent;
  }
}
