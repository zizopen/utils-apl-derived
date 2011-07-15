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

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see CellAndStripeResolver
 * @author Omnaest
 * @param <E>
 */
public abstract class CellAndStripeResolverAbstract<E> implements CellAndStripeResolver<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -5213948878611712685L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param cellIndexPosition
   * @return
   */
  protected abstract int determineRowIndexPositionForCellIndexPosition( int cellIndexPosition );
  
  /**
   * @param cellIndexPosition
   * @return
   */
  protected abstract int determineColumnIndexPositionForCellIndexPosition( int cellIndexPosition );
  
  /**
   * Resolves the {@link StripeData} instance for the given {@link StripeType} and the {@link Title#getValue()}
   * 
   * @param stripeType
   * @param titleValue
   * @return
   */
  protected abstract StripeData<E> resolveStripeData( StripeType stripeType, Object titleValue );
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> row, StripeInternal<E> column )
  {
    return this.resolveCell( row.getStripeData(), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( StripeInternal<E> row, StripeInternal<E> column )
  {
    return this.resolveOrCreateCell( row.getStripeData(), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveCell( Object rowTitleValue, Object columnTitleValue )
  {
    return this.resolveCell( this.resolveRowStripeData( rowTitleValue ), this.resolveColumnStripeData( columnTitleValue ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.resolveCell( this.resolveRowStripeData( rowIndexPosition ), this.resolveColumnStripeData( columnTitleValue ) );
  }
  
  @Override
  public Cell<E> resolveCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.resolveCell( this.resolveRowStripeData( rowTitleValue ), this.resolveColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveCell( this.resolveRowStripeData( rowIndexPosition ), this.resolveColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> row, int columnIndexPosition )
  {
    return this.resolveCell( row.getStripeData(), this.resolveColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( StripeInternal<E> row, Object columnTitleValue )
  {
    return this.resolveCell( row.getStripeData(), this.resolveColumnStripeData( columnTitleValue ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, StripeInternal<E> column )
  {
    return this.resolveCell( this.resolveRowStripeData( rowIndexPosition ), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveCell( Object rowTitleValue, StripeInternal<E> column )
  {
    return this.resolveCell( this.resolveRowStripeData( rowTitleValue ), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( this.resolveRowStripeData( rowIndexPosition ),
                                     this.resolveColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( this.resolveOrCreateRowStripeData( rowIndexPosition ),
                                     this.resolveOrCreateColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int rowIndexPosition, StripeInternal<E> column )
  {
    return this.resolveOrCreateCell( this.resolveRowStripeData( rowIndexPosition ), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( int rowIndexPosition, StripeInternal<E> column )
  {
    return this.resolveOrCreateCell( this.resolveOrCreateRowStripeData( rowIndexPosition ), column.getStripeData() );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( StripeInternal<E> row, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( row.getStripeData(), this.resolveColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeInternal<E> row, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( row.getStripeData(), this.resolveOrCreateColumnStripeData( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( StripeData<E> stripeData, int indexPosition )
  {
    return this.resolveOrCreateCell( stripeData,
                                     this.resolveStripeData( StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() ),
                                                             indexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeData<E> stripeData, int indexPosition )
  {
    return this.resolveOrCreateCell( stripeData,
                                     this.resolveOrCreateStripeData( StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() ),
                                                                     indexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( int cellIndexPosition )
  {
    //
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    
    //
    return this.resolveCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int cellIndexPosition )
  {
    //
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    
    //
    return this.resolveOrCreateCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( int cellIndexPosition )
  {
    //
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    
    //
    return this.resolveOrCreateCellWithinNewTableArea( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public StripeData<E> resolveRowStripeData( int rowIndexPosition )
  {
    return this.resolveStripeData( StripeType.ROW, rowIndexPosition );
  }
  
  @Override
  public StripeData<E> resolveColumnStripeData( Object titleValue )
  {
    return this.resolveStripeData( StripeType.COLUMN, titleValue );
  }
  
  @Override
  public StripeData<E> resolveColumnStripeData( int columnIndexPosition )
  {
    return this.resolveStripeData( StripeType.COLUMN, columnIndexPosition );
  }
  
  @Override
  public StripeData<E> resolveRowStripeData( Object titleValue )
  {
    return this.resolveStripeData( StripeType.ROW, titleValue );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( StripeData<E> stripeData, Object titleValue )
  {
    return this.resolveOrCreateCell( stripeData,
                                     this.resolveStripeData( StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() ),
                                                             titleValue ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCellWithinNewTableArea( StripeData<E> stripeData, Object titleValue )
  {
    return this.resolveOrCreateCell( stripeData,
                                     this.resolveOrCreateStripeData( StripeTypeHelper.determineInvertedStripeType( stripeData.resolveStripeType() ),
                                                                     titleValue ) );
  }
  
  @Override
  public StripeData<E> resolveOrCreateColumnStripeData( int columnIndexPosition )
  {
    return this.resolveOrCreateStripeData( StripeType.COLUMN, columnIndexPosition );
  }
  
  @Override
  public StripeData<E> resolveOrCreateRowStripeData( int rowIndexPosition )
  {
    return this.resolveOrCreateStripeData( StripeType.ROW, rowIndexPosition );
  }
  
}
