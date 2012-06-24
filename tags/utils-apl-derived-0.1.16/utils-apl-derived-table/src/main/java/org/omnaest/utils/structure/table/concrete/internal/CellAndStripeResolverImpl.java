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
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContentResolver;

/**
 * @see TableInternal
 * @see CellAndStripeResolver
 * @see CellAndStripeResolverAbstract
 * @author Omnaest
 * @param <E>
 */
public class CellAndStripeResolverImpl<E> extends CellAndStripeResolverAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long         serialVersionUID     = 7793892246619215531L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableContentResolver<E> tableContentResolver = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableContentResolver
   */
  public CellAndStripeResolverImpl( TableContentResolver<E> tableContentResolver )
  {
    super();
    this.tableContentResolver = tableContentResolver;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Cell<E> resolveCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal )
  {
    //
    Cell<E> retval = null;
    
    //    
    if ( stripeData != null && stripeDataOrthogonal != null )
    {
      //
      final Set<CellData<E>> cellDataSet = stripeData.getCellDataSet();
      final Set<CellData<E>> cellDataSetOrthogonal = stripeDataOrthogonal.getCellDataSet();
      
      //
      Set<CellData<E>> intersection = SetUtils.intersection( cellDataSet, cellDataSetOrthogonal );
      if ( intersection != null && intersection.size() == 1 )
      {
        final CellData<E> cellData = IterableUtils.firstElement( intersection );
        retval = new CellImpl<E>( cellData, stripeData, stripeDataOrthogonal );
      }
      
    }
    
    //
    return retval;
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeData != null && stripeDataOrthogonal != null )
    {
      //
      retval = this.resolveCell( stripeData, stripeDataOrthogonal );
      
      //
      if ( retval == null )
      {
        //
        retval = createCell( stripeData, stripeDataOrthogonal );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Cell<E> createCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal )
  {
    return new CellImpl<E>( new CellDataImpl<E>(), Arrays.asList( stripeData, stripeDataOrthogonal ) );
  }
  
  @Override
  protected StripeData<E> resolveStripeData( StripeType stripeType, Object titleValue )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      TableContent<E> stripeListContainer = this.resolveTableContent();
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeType );
      
      //
      retval = stripeList.getStripeData( titleValue );
    }
    
    //
    return retval;
  }
  
  @Override
  public StripeData<E> resolveStripeData( StripeType stripeType, int indexPosition )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      TableContent<E> stripeListContainer = this.resolveTableContent();
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        //      
        retval = stripeList.getStripeData( indexPosition );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public StripeData<E> resolveOrCreateStripeData( StripeType stripeType, int indexPosition )
  {
    //
    StripeData<E> stripe = null;
    
    //
    if ( indexPosition >= 0 )
    {
      //
      StripeDataList<E> stripeList = this.resolveTableContent().getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        while ( ( stripe = this.resolveStripeData( stripeType, indexPosition ) ) == null )
        {
          stripeList.addNewStripeData();
        }
      }
    }
    
    //
    return stripe;
  }
  
  @Override
  public StripeData<E> resolveOrCreateStripeData( StripeType stripeType, Object titleValue )
  {
    //
    StripeData<E> stripe = null;
    
    //
    if ( titleValue != null )
    {
      //
      StripeDataList<E> stripeList = this.resolveTableContent().getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        //
        stripe = this.resolveStripeData( stripeType, titleValue );
        
        //
        if ( stripe == null )
        {
          //
          stripe = stripeList.addNewStripeData();
          
          //
          stripe.getTitleInternal().setValue( titleValue );
        }
      }
    }
    
    //
    return stripe;
  }
  
  @Override
  public Cell<E> resolveCell( StripeData<E> stripeData, int indexPosition )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeData != null && indexPosition >= 0 )
    {
      //
      TableContent<E> stripeListContainer = this.resolveTableContent();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineOrthogonalStripeType( stripeData.resolveStripeType() );
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeData<E> stripeOrthogonal = stripeList.getStripeData( indexPosition );
        if ( stripeOrthogonal != null )
        {
          retval = this.resolveCell( stripeData, stripeOrthogonal );
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public Cell<E> resolveCell( StripeData<E> stripeData, Object titleValue )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeData != null && titleValue != null )
    {
      //
      TableContent<E> stripeListContainer = this.resolveTableContent();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineOrthogonalStripeType( stripeData.resolveStripeType() );
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeData<E> stripeOrthogonal = stripeList.getStripeData( titleValue );
        if ( stripeOrthogonal != null )
        {
          retval = this.resolveCell( stripeData, stripeOrthogonal );
        }
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  protected int determineRowIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    //
    int retval = -1;
    
    //
    int columnListSize = this.resolveTableContent().getColumnStripeDataList().size();
    if ( columnListSize > 0 )
    {
      retval = cellIndexPosition / columnListSize;
    }
    
    //
    return retval;
  }
  
  @Override
  protected int determineColumnIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    //
    int retval = -1;
    
    //
    int columnListSize = this.resolveTableContent().getColumnStripeDataList().size();
    if ( columnListSize > 0 )
    {
      retval = cellIndexPosition % columnListSize;
    }
    
    //
    return retval;
  }
  
  /**
   * @return
   */
  protected TableContent<E> resolveTableContent()
  {
    return this.tableContentResolver.resolveTableContent();
  }
  
}
