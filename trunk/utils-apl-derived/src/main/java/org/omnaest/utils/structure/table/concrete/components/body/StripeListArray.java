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
import java.util.Iterator;
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
  protected List<Stripe<E>> stripeList = new ArrayList<Table.Stripe<E>>();
  protected StripeType      stripeType = null;
  
  /* ********************************************** Methods ********************************************** */

  public StripeListArray( StripeType stripeType )
  {
    super();
    this.stripeType = stripeType;
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
  public Stripe<E> get( int index )
  {
    return index >= 0 && index < this.stripeList.size() ? this.stripeList.get( index ) : null;
  }
  
  @Override
  public Iterator<Stripe<E>> iterator()
  {
    return this.stripeList.iterator();
  }
  
}
