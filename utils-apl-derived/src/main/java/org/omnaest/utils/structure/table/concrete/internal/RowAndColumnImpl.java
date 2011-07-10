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

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.ColumnInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.RowInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;

/**
 * @see StripeCore
 * @see Row
 * @see Column
 * @author Omnaest
 * @param <E>
 */
public class RowAndColumnImpl<E> extends StripeCore<E> implements RowInternal<E>, ColumnInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -2038384778853074830L;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   * @param stripeList
   */
  protected RowAndColumnImpl( TableInternal<E> tableInternal, StripeList<E> stripeList )
  {
    //
    super( tableInternal, stripeList );
  }
  
}
