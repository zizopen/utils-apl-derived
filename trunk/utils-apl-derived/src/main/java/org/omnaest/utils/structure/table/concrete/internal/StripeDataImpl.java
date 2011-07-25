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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see StripeData
 * @author Omnaest
 * @param <E>
 */
public class StripeDataImpl<E> implements StripeData<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long      serialVersionUID = 5552519174349074630L;
  /* ********************************************** Variables ********************************************** */
    protected TitleInternal        title            = new TitleImpl<E>();
  protected Set<CellInternal<E>> cellSet          = new HashSet<CellInternal<E>>();
  
  /* ********************************************** Beans / Services / References ********************************************** */
  protected StripeDataList<E>    stripeDataList   = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param stripeDataList
   */
  public StripeDataImpl( StripeDataList<E> stripeDataList )
  {
    super();
    this.stripeDataList = stripeDataList;
  }
  
  @Override
  public StripeType resolveStripeType()
  {
    return this.stripeDataList.getStripeType();
  }
  
  @Override
  public TitleInternal getTitleInternal()
  {
    return this.title;
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    return this.cellSet.contains( cell );
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
    //
    if ( element != null )
    {
      for ( Cell<E> cell : this.cellSet )
      {
        if ( element.equals( cell.getElement() ) )
        {
          retval = true;
          break;
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public void registerCell( CellInternal<E> cell )
  {
    //
    if ( cell != null )
    {
      this.cellSet.add( cell );
    }
  }
  
  @Override
  public void unregisterCell( Cell<E> cell )
  {
    if ( cell != null )
    {
      this.cellSet.remove( cell );
    }
  }
  
  @Override
  public Set<CellInternal<E>> getCellSet()
  {
    return this.cellSet;
  }
  
  @Override
  public void detachAllCellsFromTable()
  {
    for ( CellInternal<E> cellInternal : new HashSet<CellInternal<E>>( this.cellSet ) )
    {
      cellInternal.detachFromTable();
    }
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //
    Collection<CellInternal<E>> collection = this.cellSet;
    ElementTransformer<CellInternal<E>, E> elementTransformer = new ElementTransformer<CellInternal<E>, E>()
    {
      @Override
      public E transformElement( CellInternal<E> cell )
      {
        // 
        return cell == null ? null : cell.getElement();
      }
    };
    return ListUtils.transform( collection, elementTransformer );
  }
  
}
