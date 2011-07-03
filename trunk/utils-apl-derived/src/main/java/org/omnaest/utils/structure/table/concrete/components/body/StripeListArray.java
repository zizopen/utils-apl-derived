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
package org.omnaest.utils.structure.table.concrete.components.body;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;

/**
 * @see StripeList
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public class StripeListArray<E> implements StripeList<E>
{
  /* ********************************************** Variables ********************************************** */
  protected List<Stripe<E>>             stripeList = new ArrayList<Table.Stripe<E>>();
  protected Class<? extends StripeType> stripeType = null;
  
  /* ********************************************** Methods ********************************************** */

  public StripeListArray( Class<? extends StripeType> stripeType )
  {
    super();
    this.stripeType = stripeType;
  }
  
  public int size()
  {
    return this.stripeList.size();
  }
  
  public boolean isEmpty()
  {
    return this.stripeList.isEmpty();
  }
  
  public boolean contains( Object o )
  {
    return this.stripeList.contains( o );
  }
  
  public boolean add( Stripe<E> e )
  {
    return this.stripeList.add( e );
  }
  
  public boolean remove( Object o )
  {
    return this.stripeList.remove( o );
  }
  
  public void clear()
  {
    this.stripeList.clear();
  }
  
  public Stripe<E> set( int index, Stripe<E> element )
  {
    return this.stripeList.set( index, element );
  }
  
  public void add( int index, Stripe<E> element )
  {
    this.stripeList.add( index, element );
  }
  
  public int indexOf( Object o )
  {
    return this.stripeList.indexOf( o );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Class<StripeType> getStripeType()
  {
    return (Class<StripeType>) this.stripeType;
  }
  
  @Override
  public void setStripeType( Class<StripeType> stripeType )
  {
    this.stripeType = stripeType;
  }
  
}
