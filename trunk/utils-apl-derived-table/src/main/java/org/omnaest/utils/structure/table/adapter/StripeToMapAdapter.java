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
package org.omnaest.utils.structure.table.adapter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.map.MapAbstract;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * Adapter to access a given {@link Row} or {@link Column} as {@link Map}
 * 
 * @author Omnaest
 * @param <E>
 */
public class StripeToMapAdapter<E> extends MapAbstract<Object, E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 577066475060838640L;
  /* ********************************************** Variables ********************************************** */
  protected Stripe<E>       stripe           = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param stripe
   */
  public StripeToMapAdapter( Stripe<E> stripe )
  {
    super();
    this.stripe = stripe;
  }
  
  @Override
  public E get( Object key )
  {
    // 
    return this.stripe.getCellElement( key );
  }
  
  @Override
  public E put( Object key, E value )
  {
    //
    E retval = this.get( key );
    
    //
    this.stripe.setCellElement( key, value );
    
    return retval;
  }
  
  @Override
  public E remove( Object key )
  {
    return this.put( key, null );
  }
  
  @Override
  public Set<Object> keySet()
  {
    //
    StripeInternal<E> stripeInternal = (StripeInternal<E>) this.stripe;
    StripeType stripeType = stripeInternal.getStripeData().resolveStripeType();
    
    //
    List<Object> valueList = null;
    if ( StripeType.COLUMN.equals( stripeType ) )
    {
      valueList = ( (Column<E>) this.stripe ).getRowTitleValueList();
    }
    else if ( StripeType.ROW.equals( stripeType ) )
    {
      valueList = ( (Row<E>) this.stripe ).getColumnTitleValueList();
    }
    
    return new LinkedHashSet<Object>( valueList );
  }
  
  @Override
  public Collection<E> values()
  {
    return this.stripe.getCellElementList();
  }
  
}
