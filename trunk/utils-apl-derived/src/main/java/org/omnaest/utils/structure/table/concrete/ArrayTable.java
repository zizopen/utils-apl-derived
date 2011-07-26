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
package org.omnaest.utils.structure.table.concrete;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.internal.CellAndStripeResolverImpl;
import org.omnaest.utils.structure.table.concrete.internal.StripeFactory;
import org.omnaest.utils.structure.table.concrete.internal.TableContentImpl;
import org.omnaest.utils.structure.table.concrete.selection.SelectionImpl;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContentResolver;

import com.rits.cloning.Cloner;

/**
 * Implementation of {@link Table} that uses two array lists as row and column data structure.
 * 
 * @see Table
 * @author Omnaest
 */
public class ArrayTable<E> extends TableAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long          serialVersionUID      = 1763808639838518679L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E>         arrayTableInternal    = new ArrayTableInternal();
  protected TableContentResolver<E>  tableContentResolver  = new TableContentResolver<E>()
                                                           {
                                                             @Override
                                                             public TableContent<E> resolveTableContent()
                                                             {
                                                               return ArrayTable.this.tableContent;
                                                             }
                                                           };
  protected Object                   tableName             = null;
  protected StripeFactory<E>         stripeFactory         = new StripeFactory<E>( this.arrayTableInternal );
  protected TableContent<E>          tableContent          = new TableContentImpl<E>();
  protected CellAndStripeResolver<E> cellAndStripeResolver = new CellAndStripeResolverImpl<E>( this.tableContentResolver );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see ArrayTable
   * @see TableInternal
   * @author Omnaest
   */
  public class ArrayTableInternal implements TableInternal<E>
  {
    
    @Override
    public TableContent<E> getTableContent()
    {
      return ArrayTable.this.tableContent;
    }
    
    @Override
    public CellAndStripeResolver<E> getCellAndStripeResolver()
    {
      return ArrayTable.this.cellAndStripeResolver;
    }
    
    @Override
    public Table<E> getUnderlyingTable()
    {
      return ArrayTable.this;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  public ArrayTable()
  {
    super();
  }
  
  @Override
  public Table<E> setRowTitleValue( Object titleValue, int rowIndexPosition )
  {
    //
    StripeData<E> rowStripeData = this.cellAndStripeResolver.resolveOrCreateRowStripeData( rowIndexPosition );
    if ( rowStripeData != null )
    {
      rowStripeData.getTitleInternal().setValue( titleValue );
    }
    
    //
    return this;
  }
  
  @Override
  protected void setStripeTitleValueList( List<? extends Object> titleValueList, StripeType stripeType )
  {
    //
    if ( titleValueList != null && stripeType != null )
    {
      //
      for ( int indexPosition = 0; indexPosition < titleValueList.size(); indexPosition++ )
      {
        //
        StripeData<E> stripe = this.cellAndStripeResolver.resolveOrCreateStripeData( stripeType, indexPosition );
        
        //
        Title title = stripe.getTitleInternal();
        title.setValue( titleValueList.get( indexPosition ) );
      }
    }
  }
  
  @Override
  public Table<E> setColumnTitleValue( Object titleValue, int columnIndexPosition )
  {
    //
    StripeData<E> columnStripeData = this.cellAndStripeResolver.resolveOrCreateColumnStripeData( columnIndexPosition );
    if ( columnStripeData != null )
    {
      columnStripeData.getTitleInternal().setValue( titleValue );
    }
    
    //
    return this;
  }
  
  @Override
  protected List<Object> getStripeTitleValueList( StripeType stripeType )
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    if ( stripeType != null )
    {
      //
      StripeDataList<E> stripeDataList = this.tableContent.getStripeDataList( stripeType );
      
      //
      for ( StripeData<E> stripeData : stripeDataList )
      {
        //
        retlist.add( stripeData.getTitleInternal().getValue() );
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  protected Object getStripeTitleValue( StripeType stripeType, int indexPosition )
  {
    //
    Object retval = null;
    
    //
    if ( stripeType != null && indexPosition >= 0 )
    {
      //
      StripeData<E> stripe = this.cellAndStripeResolver.resolveStripeData( stripeType, indexPosition );
      
      //
      if ( stripe != null )
      {
        retval = stripe.getTitleInternal().getValue();
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.tableContent.getTableSize();
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    //
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCellWithinNewTableArea( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveOrCreateCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Table<E> addColumnCellElements( List<? extends E> columnCellElementList )
  {
    //
    int columnIndexPosition = this.getTableSize().getColumnSize();
    this.setColumnCellElements( columnIndexPosition, columnCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //    
    StripeData<E> newStripe = this.tableContent.getColumnStripeDataList().addNewStripeData( columnIndexPosition );
    if ( newStripe != null )
    {
      this.setColumnCellElements( columnIndexPosition, columnCellElementList );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRowCellElements( List<? extends E> rowCellElementList )
  {
    //
    int rowIndexPosition = this.getTableSize().getRowSize();
    this.setRowCellElements( rowIndexPosition, rowCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    StripeData<E> newStripe = this.tableContent.getRowStripeDataList().addNewStripeData( rowIndexPosition );
    if ( newStripe != null )
    {
      this.setRowCellElements( rowIndexPosition, rowCellElementList );
    }
    
    // 
    return this;
  }
  
  @Override
  public List<E> removeRow( int rowIndexPosition )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    StripeData<E> rowStripeData = this.cellAndStripeResolver.resolveRowStripeData( rowIndexPosition );
    if ( rowStripeData != null )
    {
      //
      retlist.addAll( rowStripeData.getCellElementList() );
      
      //
      StripeData<E> removedStripeData = this.tableContent.getRowStripeDataList().removeStripeData( rowIndexPosition );
      if ( removedStripeData != null )
      {
        for ( StripeData<E> stripeData : this.tableContent.getColumnStripeDataList() )
        {
          stripeData.unregisterCells( removedStripeData.getCellDataSet() );
        }
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public List<E> removeColumn( int columnIndexPosition )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    StripeData<E> columnStripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnIndexPosition );
    if ( columnStripeData != null )
    {
      //
      retlist.addAll( columnStripeData.getCellElementList() );
      
      //
      StripeData<E> removedStripeData = this.tableContent.getColumnStripeDataList().removeStripeData( columnIndexPosition );
      if ( removedStripeData != null )
      {
        for ( StripeData<E> stripeData : this.tableContent.getRowStripeDataList() )
        {
          stripeData.unregisterCells( removedStripeData.getCellDataSet() );
        }
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveRowStripeData( rowIndexPosition );
    return this.stripeFactory.newInstanceOfStripeInternal( stripeData );
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveRowStripeData( rowTitleValue );
    return this.stripeFactory.newInstanceOfStripeInternal( stripeData );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnIndexPosition );
    return this.stripeFactory.newInstanceOfStripeInternal( stripeData );
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnTitleValue );
    return this.stripeFactory.newInstanceOfStripeInternal( stripeData );
  }
  
  @Override
  public Table<E> clear()
  {
    //
    this.tableName = null;
    this.tableContent.clear();
    
    //
    return this;
  }
  
  @Override
  public Table<E> cloneStructure()
  {
    //
    ArrayTable<E> retval = new ArrayTable<E>();
    retval.tableContent = this.tableContent.cloneStructure();
    retval.tableName = this.tableName;
    
    //
    return retval;
  }
  
  @Override
  public Table<E> cloneStructureWithContent()
  {
    //
    ArrayTable<E> retval = new ArrayTable<E>();
    retval.setNumberOfColumns( this.getTableSize().getColumnSize() );
    retval.setNumberOfRows( this.getTableSize().getRowSize() );
    TableCellVisitor<E> tableCellVisitor = new TableCellVisitor<E>()
    {
      /* ********************************************** Variables ********************************************** */
      private Cloner cloner = new Cloner();
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell )
      {
        //
        E elementClone = null;
        
        //
        E cellElement = ArrayTable.this.getCellElement( rowIndexPosition, columnIndexPosition );
        if ( cellElement != null )
        {
          //
          elementClone = this.cloner.deepClone( cellElement );
        }
        
        //
        cell.setElement( elementClone );
      }
    };
    retval.processTableCells( tableCellVisitor );
    retval.setColumnTitleValues( this.getColumnTitleValueList() );
    retval.setRowTitleValues( this.getRowTitleValueList() );
    retval.tableName = this.tableName;
    
    //
    return retval;
  }
  
  @Override
  public Object getTableName()
  {
    return this.tableName;
  }
  
  @Override
  public Table<E> setTableName( Object tableName )
  {
    //
    this.tableName = tableName;
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( cellIndexPosition );
  }
  
  @Override
  public Table<E> setCellElement( int cellIndexPosition, E element )
  {
    //
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCellWithinNewTableArea( cellIndexPosition );
    
    //
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> transpose()
  {
    //
    this.tableContent.switchRowAndColumnStripeList();
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveCell( rowTitleValue, columnTitleValue );
  }
  
  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( rowTitleValue, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveCell( rowIndexPosition, columnTitleValue );
  }
  
  @Override
  public E getCellElement( int cellIndexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( cellIndexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public Selection<E> select()
  {
    return new SelectionImpl<E>( this.arrayTableInternal );
  }
  
  protected TableInternal<E> getArrayTableInternal()
  {
    return this.arrayTableInternal;
  }
  
}
