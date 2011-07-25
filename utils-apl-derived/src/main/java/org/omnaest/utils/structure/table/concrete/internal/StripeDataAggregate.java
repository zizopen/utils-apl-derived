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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * Aggregates multiple {@link StripeData} instances into one single {@link StripeDataAggregate} which acts as one single
 * {@link StripeData} instance.
 * 
 * @see StripeData
 * @author Omnaest
 * @param <E>
 */
public class StripeDataAggregate<E> implements StripeData<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID        = 7008620131833544295L;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  protected StripeDataList<E>   stripeDataList          = null;
  
  /* ********************************************** Variables ********************************************** */
  protected StripeData<E>       stripeDataModification  = new StripeDataImpl<E>( this.stripeDataList );
  @SuppressWarnings("unchecked")
  protected List<StripeData<E>> stripeDataAggregateList = new ArrayList<StripeData<E>>(
                                                                                        Arrays.asList( this.stripeDataModification ) );
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public TitleInternal getTitleInternal()
  {
    return this.stripeDataModification.getTitleInternal();
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    //
    boolean retval = false;
    
    //
    for ( StripeData<E> stripeData : this.stripeDataList )
    {
      if ( stripeData.contains( cell ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
    //
    for ( StripeData<E> stripeData : this.stripeDataList )
    {
      if ( stripeData.contains( element ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void registerCell( CellInternal<E> cell )
  {
    this.stripeDataModification.registerCell( cell );
  }
  
  @Override
  public void unregisterCell( Cell<E> cell )
  {
    this.stripeDataModification.unregisterCell( cell );
  }
  
  @Override
  public StripeType resolveStripeType()
  {
    return this.stripeDataModification.resolveStripeType();
  }
  
  @Override
  public Set<CellInternal<E>> getCellSet()
  {
    //
    Set<CellInternal<E>> retset = new HashSet<CellInternal<E>>();
    
    //
    for ( StripeData<E> stripeData : this.stripeDataAggregateList )
    {
      retset.addAll( stripeData.getCellSet() );
    }
    
    // 
    return retset;
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    for ( StripeData<E> stripeData : this.stripeDataAggregateList )
    {
      retlist.addAll( stripeData.getCellElementList() );
    }
    
    // 
    return retlist;
  }
  
  @Override
  public void detachAllCellsFromTable()
  {
    //
    for ( StripeData<E> stripeData : this.stripeDataAggregateList )
    {
      stripeData.detachAllCellsFromTable();
    }
  }
  
}
