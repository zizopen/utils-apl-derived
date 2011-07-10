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
import java.util.List;

import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

/**
 * TODO serializable interface is missing
 * 
 * @author Omnaest
 * @see StripeListContainerAbstract
 * @see StripeListContainer
 * @param <E>
 */
public class StripeListContainerImpl<E> extends StripeListContainerAbstract<E>
{
  /* ********************************************** Variables ********************************************** */
  protected List<StripeList<E>> stripeListList = null;
  protected TableInternal<E>    tableInternal  = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   */
  @SuppressWarnings("unchecked")
  public StripeListContainerImpl( TableInternal<E> tableInternal )
  {
    //
    super();
    this.tableInternal = tableInternal;
    
    //
    this.stripeListList = new ArrayList<StripeList<E>>(
                                                        Arrays.asList( new StripeListArray<E>( this.tableInternal, StripeType.ROW ),
                                                                       new StripeListArray<E>( this.tableInternal,
                                                                                               StripeType.COLUMN ) ) );
  }
  
  @Override
  public void switchRowAndColumnStripeList()
  {
    //
    for ( StripeList<E> stripeList : this.stripeListList )
    {
      stripeList.setStripeType( StripeTypeHelper.determineInvertedStripeType( stripeList.getStripeType() ) );
    }
  }
  
  /**
   * Returns the Stripe for the given type.
   * 
   * @param stripeType
   * @return
   */
  @Override
  public StripeList<E> getStripeList( StripeType stripeType )
  {
    //    
    StripeList<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      for ( StripeList<E> stripeList : this.stripeListList )
      {
        if ( stripeType.equals( stripeList.getStripeType() ) )
        {
          //
          retval = stripeList;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public int determineStripeListSize( StripeType stripeType )
  {
    //
    int retval = -1;
    
    //
    StripeList<E> stripeList = this.getStripeList( stripeType );
    if ( stripeList != null )
    {
      retval = stripeList.size();
    }
    
    //
    return retval;
  }
  
  @Override
  public void clear()
  {
    for ( StripeList<E> stripeList : this.stripeListList )
    {
      stripeList.clear();
    }
  }
}
