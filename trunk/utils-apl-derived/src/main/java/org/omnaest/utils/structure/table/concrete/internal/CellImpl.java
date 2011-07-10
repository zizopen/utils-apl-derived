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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see Cell
 * @see CellInternal
 * @author Omnaest
 * @param <E>
 */
public class CellImpl<E> implements CellInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long         serialVersionUID   = 4937853192103513084L;
  protected List<StripeInternal<E>> stripeInternalList = new ArrayList<StripeInternal<E>>();
  protected E                       element            = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param stripeInternalCollection
   */
  protected CellImpl( Collection<StripeInternal<E>> stripeInternalCollection )
  {
    //
    super();
    
    //
    this.stripeInternalList.addAll( stripeInternalCollection );
    
    //
    for ( StripeInternal<E> stripeInternal : stripeInternalCollection )
    {
      if ( stripeInternal != null )
      {
        stripeInternal.registerCell( this );
      }
    }
  }
  
  @Override
  public E getElement()
  {
    return this.element;
  }
  
  @Override
  public void setElement( E element )
  {
    this.element = element;
  }
  
  @Override
  public boolean hasElement( E element )
  {
    return this.element == element || ( this.element != null && this.element.equals( element ) );
  }
  
  @Override
  public Cell<E> detachFromTable()
  {
    //
    for ( StripeInternal<E> stripeInternal : this.stripeInternalList )
    {
      stripeInternal.unregisterCell( this );
    }
    
    //
    return this;
  }
  
  protected StripeInternal<E> resolveStripe( StripeType stripeType )
  {
    //
    StripeInternal<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      for ( StripeInternal<E> stripeInternal : this.stripeInternalList )
      {
        if ( stripeType.equals( stripeInternal.resolveStripeType() ) )
        {
          //
          retval = stripeInternal;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Column<E> getColumn()
  {
    return (Column<E>) this.resolveStripe( StripeType.COLUMN );
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Row<E> getRow()
  {
    return (Row<E>) this.resolveStripe( StripeType.ROW );
  }
  
}
