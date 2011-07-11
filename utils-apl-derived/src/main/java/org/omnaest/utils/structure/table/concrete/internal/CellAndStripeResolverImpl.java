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

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;

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
  private static final long serialVersionUID = 7793892246619215531L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableContent<E> tableContent     = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param stripeListContainer
   */
  public CellAndStripeResolverImpl( TableContent<E> stripeListContainer )
  {
    super();
    this.tableContent = stripeListContainer;
  }
  
  @Override
  public Cell<E> resolveCell( StripeData<E> stripeData, StripeData<E> stripeDataOrthogonal )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeData != null && stripeDataOrthogonal != null )
    {
      //
      for ( Cell<E> cell : stripeData.getCellSet() )
      {
        if ( stripeDataOrthogonal.contains( cell ) )
        {
          retval = cell;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  @SuppressWarnings("unchecked")
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
        retval = new CellImpl<E>( Arrays.asList( stripeData, stripeDataOrthogonal ) );
      }
    }
    
    //
    return retval;
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
      TableContent<E> stripeListContainer = this.getTableContent();
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeType );
      
      //
      retval = stripeList.getStripe( titleValue );
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
      TableContent<E> stripeListContainer = this.getTableContent();
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        //      
        retval = stripeList.getStripe( indexPosition );
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
      StripeDataList<E> stripeList = this.getTableContent().getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        //
        while ( ( stripe = this.resolveStripeData( stripeType, indexPosition ) ) == null )
        {
          stripeList.addNewStripe();
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
      StripeDataList<E> stripeList = this.getTableContent().getStripeDataList( stripeType );
      if ( stripeList != null )
      {
        //
        stripe = this.resolveStripeData( stripeType, titleValue );
        
        //
        if ( stripe == null )
        {
          //
          stripe = stripeList.addNewStripe();
          
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
      TableContent<E> stripeListContainer = this.getTableContent();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() );
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeData<E> stripeOrthogonal = stripeList.getStripe( indexPosition );
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
      TableContent<E> stripeListContainer = this.getTableContent();
      
      //
      StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() );
      
      //
      StripeDataList<E> stripeList = stripeListContainer.getStripeDataList( stripeTypeInverted );
      if ( stripeList != null )
      {
        //
        StripeData<E> stripeOrthogonal = stripeList.getStripe( titleValue );
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
    int columnListSize = this.getTableContent().getColumnList().size();
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
    int columnListSize = this.getTableContent().getColumnList().size();
    if ( columnListSize > 0 )
    {
      retval = cellIndexPosition % columnListSize;
    }
    
    //
    return retval;
  }
  
  protected TableContent<E> getTableContent()
  {
    return this.tableContent;
  }
  
}
