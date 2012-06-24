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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see StripeDataList
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public class StripeDataListImpl<E> implements StripeDataList<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                    serialVersionUID           = -3102496365938486288L;
  
  /* ********************************************** Variables ********************************************** */
  private List<StripeData<E>>                  stripeDataList             = new ArrayList<StripeData<E>>();
  private StripeType                           stripeType                 = null;
  
  private Map<CellData<E>, Set<StripeData<E>>> cellDataToStripeDataSetMap = new HashMap<CellData<E>, Set<StripeData<E>>>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param stripeType
   */
  public StripeDataListImpl( StripeType stripeType )
  {
    super();
    this.stripeType = stripeType;
  }
  
  /**
   * Adds a new created {@link Stripe} instance to the {@link StripeDataListImpl}.
   */
  @Override
  public StripeData<E> addNewStripeData()
  {
    //
    final int cachedIndexPosition = this.stripeDataList.size();
    StripeData<E> retval = new StripeDataImpl<E>( this );
    
    //
    this.stripeDataList.add( retval );
    
    //
    return retval;
  }
  
  @Override
  public StripeData<E> addNewStripeData( int indexPosition )
  {
    //    
    final int cachedIndexPosition = indexPosition;
    StripeData<E> retval = new StripeDataImpl<E>( this );
    
    //
    while ( indexPosition > this.size() )
    {
      this.addNewStripeData();
    }
    
    //
    this.stripeDataList.add( indexPosition, retval );
    
    //
    return retval;
  }
  
  @Override
  public int size()
  {
    return this.stripeDataList.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.stripeDataList.isEmpty();
  }
  
  @Override
  public void clear()
  {
    this.stripeDataList.clear();
  }
  
  @Override
  public int indexOf( StripeData<E> stripeData )
  {
    return this.stripeDataList.indexOf( stripeData );
  }
  
  @Override
  public StripeType getStripeType()
  {
    return this.stripeType;
  }
  
  @Override
  public void setStripeType( StripeType stripeType )
  {
    this.stripeType = stripeType;
  }
  
  @Override
  public StripeData<E> getStripeData( int index )
  {
    return index >= 0 && index < this.stripeDataList.size() ? this.stripeDataList.get( index ) : null;
  }
  
  @Override
  public Iterator<StripeData<E>> iterator()
  {
    return this.stripeDataList.iterator();
  }
  
  @Override
  public List<StripeData<E>> findStripeDataListContaining( CellData<E> cellData )
  {
    //
    List<StripeData<E>> retlist = new ArrayList<StripeData<E>>();
    
    //
    if ( cellData != null )
    {
      for ( StripeData<E> stripe : this.stripeDataList )
      {
        if ( stripe.contains( cellData ) )
        {
          //
          retlist.add( stripe );
        }
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public boolean contains( StripeData<E> stripeData )
  {
    return this.stripeDataList.contains( stripeData );
  }
  
  @Override
  public boolean contains( Object titleValue )
  {
    return this.getStripeData( titleValue ) != null;
  }
  
  /**
   * Returns the {@link Set} of {@link StripeData} instances which all hold a reference to the given {@link CellData}
   * 
   * @param cellData
   * @return
   */
  public Set<StripeData<E>> getStripeDataSet( CellData<E> cellData )
  {
    return this.cellDataToStripeDataSetMap.get( cellData );
  }
  
  @Override
  public StripeData<E> getStripeData( Object titleValue )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( titleValue != null )
    {
      for ( StripeData<E> stripe : this.stripeDataList )
      {
        //
        Object value = stripe.getTitleInternal().getValue();
        
        //
        if ( titleValue.equals( value ) )
        {
          retval = stripe;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void removeStripeData( StripeData<E> stripeData )
  {
    //
    this.stripeDataList.remove( stripeData );
    
    //
    this.unregisterStripeDataForCellDatas( stripeData.getCellDataSet(), stripeData );
  }
  
  @Override
  public StripeData<E> removeStripeData( int indexPosition )
  {
    //
    StripeData<E> stripeData = this.getStripeData( indexPosition );
    if ( stripeData != null )
    {
      //
      this.removeStripeData( stripeData );
    }
    
    //
    return stripeData;
  }
  
  @Override
  public void addStripeData( StripeData<E> stripeData )
  {
    //
    if ( stripeData != null )
    {
      //
      this.stripeDataList.add( stripeData );
      
      //
      this.registerStripeDataForCellDatas( stripeData.getCellDataSet(), stripeData );
    }
  }
  
  @Override
  public void registerStripeDataForCellDatas( Collection<CellData<E>> cellDataCollection, StripeData<E> stripeData )
  {
    //
    if ( cellDataCollection != null && stripeData != null )
    {
      for ( CellData<E> cellData : cellDataCollection )
      {
        //
        if ( !this.cellDataToStripeDataSetMap.containsKey( cellData ) )
        {
          //
          this.cellDataToStripeDataSetMap.put( cellData, new HashSet<StripeData<E>>() );
        }
        
        //
        Set<StripeData<E>> stripeDataSet = this.cellDataToStripeDataSetMap.get( cellData );
        stripeDataSet.add( stripeData );
      }
    }
  }
  
  @Override
  public void unregisterStripeDataForCellDatas( Collection<CellData<E>> cellDataCollection, StripeData<E> stripeData )
  {
    //
    if ( cellDataCollection != null && stripeData != null )
    {
      for ( CellData<E> cellData : cellDataCollection )
      {
        //
        if ( this.cellDataToStripeDataSetMap.containsKey( cellData ) )
        {
          //
          Set<StripeData<E>> stripeDataSet = this.cellDataToStripeDataSetMap.get( cellData );
          
          //
          stripeDataSet.remove( stripeData );
          
          //
          if ( stripeDataSet.isEmpty() )
          {
            this.cellDataToStripeDataSetMap.remove( cellData );
          }
        }
      }
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public void unregisterCells( Iterable<CellData<E>> cellDataIterable )
  {
    if ( cellDataIterable != null )
    {
      for ( CellData<E> cellData : cellDataIterable )
      {
        if ( cellData != null )
        {
          //
          Set<StripeData<E>> stripeDataSet = this.getStripeDataSet( cellData );
          if ( stripeDataSet != null )
          {
            for ( StripeData<E> stripeData : stripeDataSet )
            {
              //
              if ( stripeData != null )
              {
                //
                stripeData.unregisterCell( cellData );
              }
              
              //
              this.unregisterStripeDataForCellDatas( Arrays.asList( cellData ), stripeData );
            }
          }
        }
      }
    }
  }
  
  @Override
  public void addAllStripeData( Iterable<StripeData<E>> stripeDataIterable )
  {
    if ( stripeDataIterable != null )
    {
      for ( StripeData<E> stripeData : stripeDataIterable )
      {
        this.addStripeData( stripeData );
      }
    }
    
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "StripeDataListImpl [stripeType=" );
    builder.append( this.stripeType );
    builder.append( "]" );
    return builder.toString();
  }
  
}
