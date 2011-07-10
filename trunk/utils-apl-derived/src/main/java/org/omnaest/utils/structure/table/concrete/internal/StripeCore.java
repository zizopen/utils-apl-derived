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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

/**
 * @see Stripe
 * @see StripeInternal
 * @author Omnaest
 * @param <E>
 */
public abstract class StripeCore<E> implements StripeInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long      serialVersionUID = 5552519174349074630L;
  /* ********************************************** Variables ********************************************** */
  protected TitleInternal        title            = new TitleImpl<E>( this );
  protected Set<CellInternal<E>> cellSet          = new HashSet<CellInternal<E>>();
  
  /* ********************************************** Beans / Services ********************************************** */
  protected TableInternal<E>     tableInternal    = null;
  protected StripeList<E>        stripeList       = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   * @param stripeList
   */
  public StripeCore( TableInternal<E> tableInternal, StripeList<E> stripeList )
  {
    super();
    this.tableInternal = tableInternal;
    this.stripeList = stripeList;
  }
  
  @Override
  public StripeType resolveStripeType()
  {
    return this.stripeList.getStripeType();
  }
  
  @Override
  public Title getTitle()
  {
    return this.title;
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
        return this.indexPosition + 1 < StripeCore.this.cellSet.size();
      }
      
      @Override
      public Cell<E> next()
      {
        return StripeCore.this.getCell( ++this.indexPosition );
      }
      
      @Override
      public void remove()
      {
        StripeCore.this.setCellElement( this.indexPosition--, null );
      }
    };
  }
  
  @Override
  public boolean contains( Cell<E> cell )
  {
    return this.cellSet.contains( cell );
  }
  
  @Override
  public Cell<E> getCell( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveCell( this, indexPosition );
  }
  
  public Cell<E> resolvesOrCreateCell( int indexPosition )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this, indexPosition );
  }
  
  public Cell<E> resolvesOrCreateCell( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveOrCreateCell( this, titleValue );
  }
  
  @Override
  public Cell<E> getCell( Object titleValue )
  {
    return this.tableInternal.getCellAndStripeResolver().resolveCell( this, titleValue );
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
  public E getCellElement( int indexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( indexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public Stripe<E> setCellElement( int indexPosition, E element )
  {
    //
    Cell<E> cell = this.resolvesOrCreateCell( indexPosition );
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
    Cell<E> cell = this.resolvesOrCreateCell( titleValue );
    if ( cell != null )
    {
      cell.setElement( null );
    }
    
    // 
    return this;
  }
  
  @Override
  public E getCellElement( Object titleValue )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( titleValue );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    int cellSize = this.determineNumberOfCells();
    for ( int indexPosition = 0; indexPosition < cellSize; indexPosition++ )
    {
      //
      Cell<E> cell = this.tableInternal.getCellAndStripeResolver().resolveCell( this, indexPosition );
      retlist.add( cell == null ? null : cell.getElement() );
    }
    
    //
    return retlist;
  }
  
  @Override
  public int determineNumberOfCells()
  {
    //
    int retval = -1;
    
    //
    StripeType stripeTypeInverted = StripeTypeHelper.determineInvertedStripeType( this.resolveStripeType() );
    
    //
    StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
    retval = stripeListContainer.determineStripeListSize( stripeTypeInverted );
    
    //
    return retval;
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
  public Set<CellInternal<E>> getCellSet()
  {
    return this.cellSet;
  }
  
  @Override
  public void detachAllCellsFromTable()
  {
    for ( CellInternal<E> cellInternal : this.cellSet )
    {
      cellInternal.detachFromTable();
    }
  }
  
}
