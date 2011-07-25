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
package org.omnaest.utils.structure.table.concrete.selection;

import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see SelectionImpl
 * @author Omnaest
 * @param <E>
 */
public class SelectionExecutor<E>
{
  /* ********************************************** Variables ********************************************** */
  protected SelectionImpl<E> selectionImpl = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see StripeInternal
   * @see StripeData
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  protected static class StripeInternalData<E>
  {
    /* ********************************************** Variables ********************************************** */
    protected StripeData<E>    stripeData    = null;
    protected TableInternal<E> tableInternal = null;
    
    /* ********************************************** Variables ********************************************** */
    
    /**
     * @param stripeData
     * @param tableInternal
     */
    public StripeInternalData( StripeData<E> stripeData, TableInternal<E> tableInternal )
    {
      super();
      this.stripeData = stripeData;
      this.tableInternal = tableInternal;
    }
    
    /**
     * @return
     */
    public StripeData<E> getStripeData()
    {
      return this.stripeData;
    }
    
    /**
     * @return
     */
    public TableInternal<E> getTableInternal()
    {
      return this.tableInternal;
    }
  }
  
  /**
   * Makes the {@link ArrayTable.ArrayTableInternal} accessible for the {@link SelectionExecutor}
   * 
   * @see ArrayTable
   * @see TableInternal
   * @see ArrayTableInternal
   * @author Omnaest
   * @param <E>
   */
  protected static class ArrayTableWithAccessibleTableInternal<E> extends ArrayTable<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 883639402376632483L;
    
    /* ********************************************** Variables ********************************************** */
    
    @Override
    protected TableInternal<E> getArrayTableInternal()
    {
      return super.getArrayTableInternal();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param selectionImpl
   */
  public SelectionExecutor( SelectionImpl<E> selectionImpl )
  {
    super();
    this.selectionImpl = selectionImpl;
  }
  
  /**
   * @param selection
   * @return
   */
  public Table<E> execute()
  {
    //
    Table<E> rettable = null;
    
    //
    if ( this.selectionImpl.isSelectAllColumns() )
    {
      this.resolveAllColumnsFromTablesAndAttachThemToTheSelectionColumnList();
    }
    
    //
    rettable = this.createTableWithDeclaredColumns();
    
    //
    return rettable;
  }
  
  private void resolveRowStripeDataList()
  {
    
  }
  
  /**
   * 
   */
  private void resolveAllColumnsFromTablesAndAttachThemToTheSelectionColumnList()
  {
    for ( TableInternal<E> tableInternal : this.selectionImpl.getTableInternalList() )
    {
      //
      if ( tableInternal != null )
      {
        //
        Table<E> table = tableInternal.getUnderlyingTable();
        if ( table != null )
        {
          //
          List<Column<E>> columnList = this.selectionImpl.getColumnList();
          for ( Column<E> column : table.getColumnList() )
          {
            if ( !columnList.contains( column ) )
            {
              columnList.add( column );
            }
          }
        }
      }
    }
  }
  
  /**
   * @return
   */
  private Table<E> createTableWithDeclaredColumns()
  {
    //
    ArrayTableWithAccessibleTableInternal<E> rettable = new ArrayTableWithAccessibleTableInternal<E>();
    
    //
    TableInternal<E> tableInternal = rettable.getArrayTableInternal();
    
    //
    for ( Column<E> column : this.selectionImpl.getColumnList() )
    {
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataFromStripe( column );
      if ( stripeInternalData != null )
      {
        //
        tableInternal.getTableContent().getColumnList().addStripeData( stripeInternalData.getStripeData() );
      }
    }
    
    //
    return rettable;
  }
  
  /**
   * @see StripeInternalData
   * @param stripe
   * @return
   */
  private static <E> StripeInternalData<E> determineStripeDataFromStripe( Stripe<E> stripe )
  {
    //
    StripeInternalData<E> retval = null;
    
    //
    if ( stripe instanceof StripeInternal )
    {
      //
      StripeInternal<E> stripeInternal = (StripeInternal<E>) stripe;
      
      //
      StripeData<E> stripeData = stripeInternal.getStripeData();
      TableInternal<E> tableInternal = stripeInternal.getTableInternal();
      
      //
      retval = new StripeInternalData<E>( stripeData, tableInternal );
    }
    
    //
    return retval;
  }
  
}
