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

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

public class StripeListContainerImpl<E> implements StripeListContainer<E>
{
  /* ********************************************** Variables ********************************************** */
  @SuppressWarnings("unchecked")
  protected StripeList<E>[] stripeLists = new StripeList[] { new StripeListArray<E>( Row.class ),
      new StripeListArray<E>( Column.class ) };
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Returns the Stripe for the given type.
   * 
   * @param type
   * @return
   */
  @Override
  public StripeList<E> getStripeList( Class<StripeType> type )
  {
    //    
    StripeList<E> retval = null;
    
    //
    if ( type != null )
    {
      for ( StripeList<E> stripeList : this.stripeLists )
      {
        if ( type.equals( stripeList.getStripeType() ) )
        {
          //
          retval = this.stripeLists[0];
          break;
        }
      }
    }
    
    //
    return retval;
  }
}
