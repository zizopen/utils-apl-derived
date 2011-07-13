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

import java.util.Iterator;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.ColumnInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.RowInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;

/**
 * @see Stripe
 * @see Row
 * @see Column
 * @see StripeInternal
 * @see RowInternal
 * @see ColumnInternal
 * @see TableInternal
 * @author Omnaest
 * @param <E>
 */
public class StripeImpl<E> implements RowInternal<E>, ColumnInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 5552519174349074630L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal    = null;
  protected StripeData<E>    stripeData       = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableInternal
   * @param stripeData
   */
  public StripeImpl( TableInternal<E> tableInternal, StripeData<E> stripeData )
  {
    super();
    this.tableInternal = tableInternal;
    this.stripeData = stripeData;
  }
  
  @Override
  public Title getTitle()
  {
    return this.stripeData.getTitleInternal();
  }
  
  @Override
  public Iterator<Cell<E>> iterator()
  {
    return new Iterator<Cell<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int indexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        //
        StripeType stripeType = StripeImpl.this.stripeData.resolveStripeType();
        int size = StripeImpl.this.tableInternal.getTableContent().determineStripeListSize( stripeType );
        
        //
        return this.indexPosition + 1 < size;
      }
      
      @Override
      public Cell<E> next()
      {
        return StripeImpl.this.getCell( ++this.indexPosition );
      }
      
      @Override
      public void remove()
      {
        StripeImpl.this.setCellElement( this.indexPosition--, null );
      }
    };
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    return this.stripeData.contains( cell );
  }
  
  @Override
  public Cell<E> getCell( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveCell( this.stripeData, indexPosition );
  }
  
  @Override
  public Cell<E> getCell( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveCell( this.stripeData, titleValue );
  }
  
  @Override
  public E getCellElement( int indexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( indexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public Stripe<E> setCellElement( int indexPosition, E element )
  {
    //
    Cell<E> cell = this.resolvesOrCreateCell( indexPosition );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    // 
    return this;
  }
  
  @Override
  public Stripe<E> setCellElement( Object titleValue, E element )
  {
    //
    Cell<E> cell = this.resolvesOrCreateCell( titleValue );
    if ( cell != null )
    {
      cell.setElement( null );
    }
    
    // 
    return this;
  }
  
  @Override
  public E getCellElement( Object titleValue )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( titleValue );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public int determineNumberOfCells()
  {
    //
    int retval = -1;
    
    //
    StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( this.stripeData.resolveStripeType() );
    
    //
    TableContent<E> stripeListContainer = this.tableInternal.getTableContent();
    retval = stripeListContainer.determineStripeListSize( stripeTypeInverted );
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( E element )
  {
    return this.stripeData.contains( element );
  }
  
  @Override
  public StripeData<E> getStripeData()
  {
    return this.stripeData;
  }
  
  /**
   * @param indexPosition
   * @return
   */
  protected Cell<E> resolvesOrCreateCell( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this.stripeData, indexPosition );
  }
  
  /**
   * @param titleValue
   * @return
   */
  protected Cell<E> resolvesOrCreateCell( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this.stripeData, titleValue );
  }
  
  @Override
  public boolean hasTitle()
  {
    return this.stripeData.getTitleInternal().getValue() != null;
  }
  
}
