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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
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
  public boolean contains( CellData<E> cellData )
  {
    //
    boolean retval = false;
    
    //
    for ( StripeData<E> stripeData : this.stripeDataList )
    {
      if ( stripeData.contains( cellData ) )
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
  public void registerCell( CellData<E> cellData )
  {
    this.stripeDataModification.registerCell( cellData );
  }
  
  @Override
  public void unregisterCell( CellData<E> cellData )
  {
    this.stripeDataModification.unregisterCell( cellData );
  }
  
  @Override
  public StripeType resolveStripeType()
  {
    return this.stripeDataModification.resolveStripeType();
  }
  
  @Override
  public Set<CellData<E>> getCellDataSet()
  {
    //
    Set<CellData<E>> retset = new HashSet<CellData<E>>();
    
    //
    for ( StripeData<E> stripeData : this.stripeDataAggregateList )
    {
      retset.addAll( stripeData.getCellDataSet() );
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
  public void unregisterCells( Collection<CellData<E>> cellDataCollection )
  {
    //
    if ( cellDataCollection != null )
    {
      for ( CellData<E> cellData : cellDataCollection )
      {
        this.unregisterCell( cellData );
      }
    }
  }
  
  @Override
  public void registerCells( Collection<CellData<E>> cellDataCollection )
  {
    //
    if ( cellDataCollection != null )
    {
      for ( CellData<E> cellData : cellDataCollection )
      {
        this.registerCell( cellData );
      }
    }
  }
  
  @Override
  public Set<CellData<E>> findCellDataSetHavingCellElement( E element )
  {
    //
    Set<CellData<E>> retset = new HashSet<CellData<E>>();
    
    //
    for ( StripeData<E> stripeData : this.stripeDataAggregateList )
    {
      retset.addAll( stripeData.findCellDataSetHavingCellElement( element ) );
    }
    
    // 
    return retset;
  }
  
}
