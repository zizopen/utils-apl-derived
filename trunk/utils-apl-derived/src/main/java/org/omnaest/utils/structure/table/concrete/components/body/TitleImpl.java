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

import org.omnaest.utils.structure.CloneableDeep;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.components.tableheader.title.TableTitleList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal.TitleInternal;

/**
 * @see TableTitleList
 * @author Omnaest
 */
public class TitleImpl<E> implements TitleInternal, CloneableDeep<TitleImpl<E>>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -4806596052790124334L;
  /* ********************************************** Variables ********************************************** */
  protected Object          value            = null;
  protected Stripe<E>       stripe           = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new {@link TitleImpl} instance
   */
  public TitleImpl()
  {
    super();
  }
  
  /**
   * Determines the index position of this {@link TableTitleList} within its {@link TableTitleList}.
   * 
   * @return
   */
  public int determineIndexPosition()
  {
    //
    int retval = -1;
    
    //
    if ( this.stripe != null )
    {
      retval = this.stripe.determineIndexPosition();
    }
    
    //
    return retval;
  }
  
  @Override
  public TitleImpl<E> cloneDeep()
  {
    //
    TitleImpl<E> clone = new TitleImpl<E>();
    clone.value = this.value;
    
    //
    return clone;
  }
  
  /**
   * @see Stripe
   * @return
   */
  protected Stripe<E> getStripe()
  {
    return this.stripe;
  }
  
  /**
   * @see Stripe
   * @param stripe
   */
  protected void setStripe( Stripe<E> stripe )
  {
    this.stripe = stripe;
  }
  
  @Override
  public Object getValue()
  {
    return this.value;
  }
  
  @Override
  public String getValueAsString()
  {
    //
    String retval = null;
    
    //
    try
    {
      retval = String.valueOf( this.getValue() );
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean hasEqualValueTo( Title title )
  {
    return this.value == title.getValue() || ( this.value != null && this.value.equals( title.getValue() ) );
  }
  
  @Override
  public void setValue( Object value )
  {
    this.value = value;
  }
  
}
