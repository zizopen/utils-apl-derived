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
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see StripeDataList
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public class StripeDataListImpl<E> implements StripeDataList<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID = -3102496365938486288L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<StripeData<E>> stripeDataList   = new ArrayList<StripeData<E>>();
  protected StripeType          stripeType       = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param stripeType
   */
  public StripeDataListImpl( StripeType stripeType )
  {
    super();
    this.stripeType = stripeType;
  }
  
  /**
   * Adds a new created {@link Stripe} instance to the {@link StripeDataListImpl}.
   */
  @Override
  public StripeData<E> addNewStripeData()
  {
    //
    StripeData<E> retval = new StripeDataImpl<E>( this );
    
    //
    this.stripeDataList.add( retval );
    
    //
    return retval;
  }
  
  @Override
  public StripeData<E> addNewStripeData( int indexPosition )
  {
    //
    StripeData<E> retval = new StripeDataImpl<E>( this );
    
    //
    while ( indexPosition > this.size() )
    {
      this.addNewStripeData();
    }
    
    //
    this.stripeDataList.add( indexPosition, retval );
    
    //
    return retval;
  }
  
  @Override
  public int size()
  {
    return this.stripeDataList.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.stripeDataList.isEmpty();
  }
  
  @Override
  public void clear()
  {
    this.stripeDataList.clear();
  }
  
  @Override
  public int indexOf( StripeData<E> stripeData )
  {
    return this.stripeDataList.indexOf( stripeData );
  }
  
  @Override
  public StripeType getStripeType()
  {
    return this.stripeType;
  }
  
  @Override
  public void setStripeType( StripeType stripeType )
  {
    this.stripeType = stripeType;
  }
  
  @Override
  public StripeData<E> getStripeData( int index )
  {
    return index >= 0 && index < this.stripeDataList.size() ? this.stripeDataList.get( index ) : null;
  }
  
  @Override
  public Iterator<StripeData<E>> iterator()
  {
    return this.stripeDataList.iterator();
  }
  
  @Override
  public StripeData<E> findStripeDataContaining( CellData<E> cellData )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( cellData != null )
    {
      for ( StripeData<E> stripe : this.stripeDataList )
      {
        if ( stripe.contains( cellData ) )
        {
          //
          retval = stripe;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( StripeData<E> stripeData )
  {
    return this.stripeDataList.contains( stripeData );
  }
  
  @Override
  public boolean contains( Object titleValue )
  {
    return this.getStripeData( titleValue ) != null;
  }
  
  @Override
  public StripeData<E> getStripeData( Object titleValue )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( titleValue != null )
    {
      for ( StripeData<E> stripe : this.stripeDataList )
      {
        //
        Object value = stripe.getTitleInternal().getValue();
        
        //
        if ( titleValue.equals( value ) )
        {
          retval = stripe;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void removeStripeData( StripeData<E> stripe )
  {
    this.stripeDataList.remove( stripe );
  }
  
  @Override
  public StripeData<E> removeStripeData( int indexPosition )
  {
    //
    StripeData<E> stripeData = this.getStripeData( indexPosition );
    if ( stripeData != null )
    {
      //
      this.stripeDataList.remove( indexPosition );
    }
    
    //
    return stripeData;
  }
  
  @Override
  public void addStripeData( StripeData<E> stripeData )
  {
    //
    if ( stripeData != null )
    {
      this.stripeDataList.add( stripeData );
    }
  }
  
  @Override
  public void addAllStripeData( Iterable<StripeData<E>> stripeDataIterable )
  {
    if ( stripeDataIterable != null )
    {
      for ( StripeData<E> stripeData : stripeDataIterable )
      {
        this.addStripeData( stripeData );
      }
    }
    
  }
  
}
