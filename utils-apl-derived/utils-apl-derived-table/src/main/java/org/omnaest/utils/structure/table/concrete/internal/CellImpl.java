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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * Wrapper for a {@link CellDataImpl} instance which additionally holds references to the {@link StripeData} instances which led
 * to the underlying {@link CellDataImpl}
 * 
 * @see Cell
 * @see CellInternal
 * @see CellDataImpl
 * @author Omnaest
 * @param <E>
 */
public class CellImpl<E> implements CellInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID = 4937853192103513084L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<StripeData<E>> stripeDataList   = new ArrayList<StripeData<E>>();
  protected CellData<E>         cellData         = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CellImpl
   * @param cellData
   * @param stripeDatas
   */
  protected CellImpl( CellData<E> cellData, StripeData<E>... stripeDatas )
  {
    this( cellData, Arrays.asList( stripeDatas ) );
  }
  
  /**
   * @param stripeDataCollection
   */
  protected CellImpl( CellData<E> cellData, Collection<StripeData<E>> stripeDataCollection )
  {
    //
    super();
    
    //
    this.stripeDataList.addAll( stripeDataCollection );
    this.cellData = cellData;
    
    //
    if ( cellData != null )
    {
      for ( StripeData<E> stripeData : stripeDataCollection )
      {
        if ( stripeData != null )
        {
          stripeData.registerCell( cellData );
        }
      }
    }
  }
  
  @Override
  public Cell<E> detachFromTable()
  {
    //
    for ( StripeData<E> stripeInternal : this.stripeDataList )
    {
      stripeInternal.unregisterCell( this.cellData );
    }
    
    //
    return this;
  }
  
  protected StripeData<E> resolveStripeData( StripeType stripeType )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      for ( StripeData<E> stripeInternal : this.stripeDataList )
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
  public E getElement()
  {
    return this.cellData.getElement();
  }
  
  @Override
  public void setElement( E element )
  {
    this.cellData.setElement( element );
  }
  
  @Override
  public boolean hasElement( E element )
  {
    return this.cellData.hasElement( element );
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.cellData == null ) ? 0 : this.cellData.hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof CellImpl ) )
    {
      return false;
    }
    CellImpl<?> other = (CellImpl<?>) obj;
    if ( this.cellData == null )
    {
      if ( other.cellData != null )
      {
        return false;
      }
    }
    else if ( !this.cellData.equals( other.cellData ) )
    {
      return false;
    }
    return true;
  }
  
  @Override
  public CellData<E> getCellData()
  {
    return this.cellData;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Cell [element=" );
    builder.append( this.getElement() );
    builder.append( "]" );
    return builder.toString();
  }
  
}
