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

import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * Factory for {@link StripeInternal} instances of a predefined {@link TableInternal}
 * 
 * @author Omnaest
 * @param <E>
 */
public class StripeFactory<E>
{
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableInternal
   */
  public StripeFactory( TableInternal<E> tableInternal )
  {
    super();
    this.tableInternal = tableInternal;
  }
  
  /**
   * Creates a new instance for the {@link TableInternal} instance of the {@link StripeFactory} for the given {@link StripeData}
   * 
   * @param stripeData
   * @return
   */
  public StripeInternal<E> newInstanceOfStripeInternal( StripeData<E> stripeData )
  {
    //
    TableInternal<E> tableInternal = this.tableInternal;
    return new StripeImpl<E>( tableInternal, stripeData );
  }
}
