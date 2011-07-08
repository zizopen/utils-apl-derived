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
package org.omnaest.utils.structure.table.concrete.components;

import java.util.Iterator;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see Stripe
 * @see StripeInternal
 * @author Omnaest
 * @param <E>
 */
public abstract class StripeCore<E> implements StripeInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 5552519174349074630L;
  /* ********************************************** Variables ********************************************** */
  protected TitleInternal    title            = new TitleImpl<E>( this );
  protected TableInternal<E> tableInternal    = null;
  protected Set<Cell<E>>     cellSet          = null;
  
  /* ********************************************** Methods ********************************************** */

  @Override
  public Title getTitle()
  {
    return this.title;
  }
  
  @Override
  public Iterator<Cell<E>> iterator()
  {
    return this.cellSet.iterator();
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    return this.cellSet.contains( cell );
  }
  
  @Override
  public Cell<E> getCell( int indexPosition )
  {
    return this.tableInternal.getCellResolver().resolveCell( this, indexPosition );
  }
  
  @Override
  public Cell<E> getCell( Object titleValue )
  {
    return this.tableInternal.getCellResolver().resolveCell( this, titleValue );
  }
  
  @Override
  public void addCell( Cell<E> cell )
  {
    //
    if ( cell != null )
    {
      this.cellSet.add( cell );
    }
  }
  
}
