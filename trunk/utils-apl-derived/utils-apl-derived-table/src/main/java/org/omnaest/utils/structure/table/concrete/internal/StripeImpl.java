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

import java.util.Iterator;
import java.util.Map;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.adapter.StripeToMapAdapter;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;

/**
 * @see StripeInternal
 * @see StripeAbstract
 * @author Omnaest
 * @param <E>
 */
public class StripeImpl<E> extends StripeAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID    = 5552519174349074630L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private int               cachedIndexPosition = 0;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @param tableInternal
   * @param stripeData
   */
  public StripeImpl( TableInternal<E> tableInternal, StripeData<E> stripeData )
  {
    super( tableInternal, stripeData );
  }
  
  @Override
  public Title title()
  {
    //
    Title retval = null;
    
    //
    if ( this.stripeData != null )
    {
      retval = this.stripeData.getTitleInternal();
    }
    
    //
    return retval;
  }
  
  @Override
  public Iterator<Cell<E>> iterator()
  {
    return new Iterator<Cell<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int indexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        //
        boolean retval = false;
        
        //
        if ( StripeImpl.this.stripeData != null && StripeImpl.this.tableInternal != null )
        {
          //
          StripeType stripeType = StripeImpl.this.stripeData.resolveStripeType();
          stripeType = StripeTypeHelper.determineOrthogonalStripeType( stripeType );
          int size = StripeImpl.this.tableInternal.getTableContent().determineStripeListSize( stripeType );
          
          //
          retval = this.indexPosition + 1 < size;
        }
        
        //
        return retval;
      }
      
      @Override
      public Cell<E> next()
      {
        return StripeImpl.this.getCell( ++this.indexPosition );
      }
      
      @Override
      public void remove()
      {
        StripeImpl.this.setCellElement( this.indexPosition--, null );
      }
    };
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    return cell instanceof CellInternal && this.stripeData.contains( ( (CellInternal<E>) cell ).getCellData() );
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        return StripeImpl.this.iterator();
      }
    };
  }
  
  @Override
  public Cell<E> getCell( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this.stripeData, indexPosition );
  }
  
  @Override
  public Cell<E> getCell( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this.stripeData, titleValue );
  }
  
  @Override
  public Cell<E> getCell( Stripe<E> stripeOrthogonal )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( stripeOrthogonal instanceof StripeInternal )
    {
      //
      StripeInternal<E> stripeInternalOrthogonal = (StripeInternal<E>) stripeOrthogonal;
      retval = this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this.stripeData,
                                                                                  stripeInternalOrthogonal.getStripeData() );
      
    }
    return retval;
  }
  
  @Override
  public Stripe<E> setCellElement( int indexPosition, E element )
  {
    //
    Cell<E> cell = this.resolvesOrCreateCellWithinNewTableArea( indexPosition );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    // 
    return this;
  }
  
  @Override
  public Stripe<E> setCellElement( Object titleValue, E element )
  {
    //
    Cell<E> cell = this.resolvesOrCreateCellWithinNewTableArea( titleValue );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    // 
    return this;
  }
  
  @Override
  public int determineNumberOfCells()
  {
    //
    int retval = -1;
    
    //
    StripeType stripeTypeInverted = StripeTypeHelper.determineOrthogonalStripeType( this.stripeData.resolveStripeType() );
    
    //
    TableContent<E> stripeListContainer = this.tableInternal.getTableContent();
    retval = stripeListContainer.determineStripeListSize( stripeTypeInverted );
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( E element )
  {
    return this.stripeData.contains( element );
  }
  
  @Override
  public StripeData<E> getStripeData()
  {
    return this.stripeData;
  }
  
  /**
   * @param indexPosition
   * @return
   */
  protected Cell<E> resolvesOrCreateCellWithinNewTableArea( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCellWithinNewTableArea( this.stripeData, indexPosition );
  }
  
  /**
   * @param titleValue
   * @return
   */
  protected Cell<E> resolvesOrCreateCellWithinNewTableArea( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCellWithinNewTableArea( this.stripeData, titleValue );
  }
  
  @Override
  public boolean hasTitle()
  {
    //
    boolean retval = false;
    
    //
    if ( this.stripeData != null )
    {
      retval = this.stripeData.getTitleInternal().getValue() != null;
    }
    
    //
    return retval;
  }
  
  @Override
  public int determineIndexPosition()
  {
    //
    int retval = -1;
    
    //
    StripeDataList<E> stripeDataList = this.tableInternal.getTableContent()
                                                         .getStripeDataList( this.stripeData.resolveStripeType() );
    if ( stripeDataList != null )
    {
      if ( stripeDataList.getStripeData( this.cachedIndexPosition ) == this.stripeData )
      {
        retval = this.cachedIndexPosition;
      }
      else
      {
        retval = this.cachedIndexPosition = stripeDataList.indexOf( this.stripeData );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Stripe<E> setCellElements( Iterable<E> elements )
  {
    //
    this.clearCellElements();
    
    //
    int indexPosition = 0;
    for ( E element : elements )
    {
      //
      Cell<E> cell = this.resolvesOrCreateCellWithinNewTableArea( indexPosition );
      if ( cell != null )
      {
        cell.setElement( element );
      }
      
      //
      indexPosition++;
    }
    
    // 
    return this;
  }
  
  @Override
  public Stripe<E> clearCellElements()
  {
    //
    for ( Cell<E> cell : this )
    {
      if ( cell != null )
      {
        cell.setElement( null );
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public Object getTitleValue()
  {
    return this.title() != null ? this.title().getValue() : null;
  }
  
  @Override
  public Map<Object, E> asMap()
  {
    return new StripeToMapAdapter<E>( this );
  }
  
}
