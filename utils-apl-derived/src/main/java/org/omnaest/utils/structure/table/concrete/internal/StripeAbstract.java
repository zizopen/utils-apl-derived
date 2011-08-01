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

import java.util.Arrays;

import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see StripeInternal
 * @author Omnaest
 * @param <E>
 */
public abstract class StripeAbstract<E> implements StripeInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 5543219174349074630L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal    = null;
  protected StripeData<E>    stripeData       = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableInternal
   * @param stripeData
   */
  public StripeAbstract( TableInternal<E> tableInternal, StripeData<E> stripeData )
  {
    super();
    this.tableInternal = tableInternal;
    this.stripeData = stripeData;
  }

  @Override
  public int determineColumnIndexPosition()
  {
    return this.determineIndexPosition();
  }

  @Override
  public int determineRowIndexPosition()
  {
    return this.determineIndexPosition();
  }

  @Override
  public Stripe<E> setCellElements( E... elements )
  {
    //
    this.setCellElements( Arrays.asList( elements ) );
    
    //
    return this;
  }

  @Override
  public TableInternal<E> getTableInternal()
  {
    return this.tableInternal;
  }
  
}
