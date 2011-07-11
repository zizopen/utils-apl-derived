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

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see StripeDataList
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public class StripeListArray<E> implements StripeDataList<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID = -3102496365938486288L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<StripeData<E>> stripeList       = new ArrayList<StripeData<E>>();
  protected StripeType          stripeType       = null;
  protected TableInternal<E>    tableInternal    = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableInternal
   * @param stripeType
   */
  public StripeListArray( TableInternal<E> tableInternal, StripeType stripeType )
  {
    super();
    this.tableInternal = tableInternal;
    this.stripeType = stripeType;
  }
  
  /**
   * Adds a new created {@link Stripe} instance to the {@link StripeListArray}.
   */
  @Override
  public StripeData<E> addNewStripe()
  {
    //
    StripeData<E> retval = new StripeDataImpl<E>( this.tableInternal, this );
    
    //
    this.stripeList.add( retval );
    
    //
    return retval;
  }
  
  @Override
  public StripeData<E> addNewStripe( int indexPosition )
  {
    //
    StripeData<E> retval = new StripeDataImpl<E>( this.tableInternal, this );
    
    //
    while ( indexPosition > this.size() )
    {
      this.addNewStripe();
    }
    
    //
    this.stripeList.add( indexPosition, retval );
    
    //
    return retval;
  }
  
  @Override
  public int size()
  {
    return this.stripeList.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.stripeList.isEmpty();
  }
  
  @Override
  public void clear()
  {
    this.stripeList.clear();
  }
  
  @Override
  public int indexOf( Stripe<E> stripe )
  {
    return this.stripeList.indexOf( stripe );
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
  public StripeData<E> getStripe( int index )
  {
    return index >= 0 && index < this.stripeList.size() ? this.stripeList.get( index ) : null;
  }
  
  @Override
  public Iterator<StripeData<E>> iterator()
  {
    return this.stripeList.iterator();
  }
  
  @Override
  public StripeData<E> getStripe( Cell<E> cell )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( cell != null )
    {
      for ( StripeData<E> stripe : this.stripeList )
      {
        if ( stripe.contains( cell ) )
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
  public boolean contains( Stripe<E> stripe )
  {
    return this.stripeList.contains( stripe );
  }
  
  @Override
  public boolean contains( Object titleValue )
  {
    return this.getStripe( titleValue ) != null;
  }
  
  @Override
  public StripeData<E> getStripe( Object titleValue )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( titleValue != null )
    {
      for ( StripeData<E> stripe : this.stripeList )
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
  public void removeStripeAndDetachCellsFromTable( StripeData<E> stripe )
  {
    //
    if ( stripe != null )
    {
      stripe.detachAllCellsFromTable();
    }
    
    //
    this.stripeList.remove( stripe );
  }
  
  @Override
  public void removeStripeDataAndDetachCellsFromTable( int indexPosition )
  {
    //
    StripeData<E> stripeInternal = this.getStripe( indexPosition );
    if ( stripeInternal != null )
    {
      //
      stripeInternal.detachAllCellsFromTable();
      
      //
      this.stripeList.remove( indexPosition );
    }
  }
  
}
