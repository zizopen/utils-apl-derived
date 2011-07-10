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

import java.util.Arrays;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.ColumnInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.RowInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

/**
 * @see TableInternal
 * @see CellAndStripeResolver
 * @see CellAndStripeResolverAbstract
 * @author Omnaest
 * @param <E>
 */
public class CellAndStripeResolverImpl<E> extends CellAndStripeResolverAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 7793892246619215531L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal    = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   */
  public CellAndStripeResolverImpl( TableInternal<E> tableInternal )
  {
    super();
    this.tableInternal = tableInternal;
  }
  
  @Override
  public Cell<E> resolveCell( RowInternal<E> row, ColumnInternal<E> column )
  {
    return this.resolveCell( (StripeInternal<E>) row, (StripeInternal<E>) column );
  }
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> stripe, StripeInternal<E> stripeOrthogonal )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripe != null && stripeOrthogonal != null )
    {
      //
      for ( Cell<E> cell : stripe.getCellSet() )
      {
        if ( stripeOrthogonal.contains( cell ) )
        {
          retval = cell;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( RowInternal<E> row, ColumnInternal<E> column )
  {
    return this.resolveOrCreateCell( (StripeInternal<E>) row, (StripeInternal<E>) column );
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Cell<E> resolveOrCreateCell( StripeInternal<E> stripeFirst, StripeInternal<E> stripeSecond )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeFirst != null && stripeSecond != null )
    {
      //
      retval = this.resolveCell( stripeFirst, stripeSecond );
      
      //
      if ( retval == null )
      {
        //
        retval = new CellImpl<E>( Arrays.asList( stripeFirst, stripeSecond ) );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves a {@link Row} for the given {@link Row} index position.
   * 
   * @param rowIndexPosition
   * @return
   */
  @Override
  public RowInternal<E> resolveRow( int rowIndexPosition )
  {
    //
    RowInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.ROW, rowIndexPosition );
    if ( stripe instanceof RowInternal )
    {
      retval = (RowInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  @Override
  public ColumnInternal<E> resolveColumn( Object titleValue )
  {
    //
    ColumnInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.COLUMN, titleValue );
    if ( stripe instanceof ColumnInternal )
    {
      retval = (ColumnInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  @Override
  public RowInternal<E> resolveRow( Object titleValue )
  {
    //
    RowInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.ROW, titleValue );
    if ( stripe instanceof RowInternal )
    {
      retval = (RowInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves the {@link StripeInternal} instance for the given {@link StripeType} and the {@link Title#getValue()}
   * 
   * @param stripeType
   * @param titleValue
   * @return
   */
  protected StripeInternal<E> resolveStripe( StripeType stripeType, Object titleValue )
  {
    //
    StripeInternal<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeType );
      
      //
      retval = stripeList.getStripe( titleValue );
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves a {@link Column} for the given {@link Column} index position.
   * 
   * @param rowIndexPosition
   * @return
   */
  @Override
  public ColumnInternal<E> resolveColumn( int columnIndexPosition )
  {
    //
    ColumnInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.COLUMN, columnIndexPosition );
    if ( stripe instanceof ColumnInternal )
    {
      retval = (ColumnInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  @Override
  public StripeInternal<E> resolveStripe( StripeType stripeType, int indexPosition )
  {
    //
    StripeInternal<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeType );
      if ( stripeList != null )
      {
        //      
        retval = stripeList.getStripe( indexPosition );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * @see #resolveOrCreateStripe(StripeType, int)
   * @param columnIndexPosition
   */
  @Override
  public ColumnInternal<E> resolveOrCreateColumn( int columnIndexPosition )
  {
    //
    ColumnInternal<E> column = null;
    
    //
    StripeInternal<E> stripeInternal = this.resolveOrCreateStripe( StripeType.COLUMN, columnIndexPosition );
    if ( stripeInternal instanceof ColumnInternal )
    {
      //
      column = (ColumnInternal<E>) stripeInternal;
    }
    
    //
    return column;
  }
  
  /**
   * @see #resolveOrCreateStripe(StripeType, int)
   * @param rowIndexPosition
   */
  @Override
  public RowInternal<E> resolveOrCreateRow( int rowIndexPosition )
  {
    //
    RowInternal<E> row = null;
    
    //
    StripeInternal<E> stripeInternal = this.resolveOrCreateStripe( StripeType.ROW, rowIndexPosition );
    if ( stripeInternal instanceof RowInternal )
    {
      //
      row = (RowInternal<E>) stripeInternal;
    }
    
    //
    return row;
  }
  
  @Override
  public StripeInternal<E> resolveOrCreateStripe( StripeType stripeType, int indexPosition )
  {
    //
    StripeInternal<E> stripe = null;
    
    //
    if ( indexPosition >= 0 )
    {
      //
      StripeList<E> stripeList = this.tableInternal.getStripeListContainer().getStripeList( stripeType );
      if ( stripeList != null )
      {
        //
        while ( ( stripe = this.resolveStripe( stripeType, indexPosition ) ) == null )
        {
          stripeList.addNewStripe();
        }
      }
    }
    
    //
    return stripe;
  }
  
  @Override
  public StripeInternal<E> resolveOrCreateStripe( StripeType stripeType, Object titleValue )
  {
    //
    StripeInternal<E> stripe = null;
    
    //
    if ( titleValue != null )
    {
      //
      StripeList<E> stripeList = this.tableInternal.getStripeListContainer().getStripeList( stripeType );
      if ( stripeList != null )
      {
        //
        stripe = this.resolveStripe( stripeType, titleValue );
        
        //
        if ( stripe == null )
        {
          //
          stripe = stripeList.addNewStripe();
          
          //
          stripe.getTitle().setValue( titleValue );
        }
      }
    }
    
    //
    return stripe;
  }
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> stripeInternal, int indexPosition )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeInternal != null && indexPosition >= 0 )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( stripeInternal.resolveStripeType() );
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeInternal<E> stripeOrthogonal = stripeList.getStripe( indexPosition );
        if ( stripeOrthogonal != null )
        {
          retval = this.resolveCell( stripeInternal, stripeOrthogonal );
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> stripeInternal, Object titleValue )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeInternal != null && titleValue != null )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( stripeInternal.resolveStripeType() );
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeInternal<E> stripeOrthogonal = stripeList.getStripe( titleValue );
        if ( stripeOrthogonal != null )
        {
          retval = this.resolveCell( stripeInternal, stripeOrthogonal );
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  protected int determineRowIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    //
    int retval = -1;
    
    //
    int columnListSize = this.tableInternal.getStripeListContainer().getColumnList().size();
    if ( columnListSize > 0 )
    {
      retval = cellIndexPosition / columnListSize;
    }
    
    //
    return retval;
  }
  
  @Override
  protected int determineColumnIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    //
    int retval = -1;
    
    //
    int columnListSize = this.tableInternal.getStripeListContainer().getColumnList().size();
    if ( columnListSize > 0 )
    {
      retval = cellIndexPosition % columnListSize;
    }
    
    //
    return retval;
  }
  
}
