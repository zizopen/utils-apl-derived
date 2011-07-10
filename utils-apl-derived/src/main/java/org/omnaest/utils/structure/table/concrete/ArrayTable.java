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
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.table.IndexTable.IndexPositionPair;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.internal.CellAndStripeResolverImpl;
import org.omnaest.utils.structure.table.concrete.internal.StripeListContainerImpl;
import org.omnaest.utils.structure.table.concrete.internal.TableSizeImpl;

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
  protected StripeListContainer<E>   stripeListContainer   = new StripeListContainerImpl<E>( this );
  protected CellAndStripeResolver<E> cellAndStripeResolver = new CellAndStripeResolverImpl<E>( this );
  protected TableSize                tableSize             = new TableSizeImpl( this );
  protected Object                   tableName             = null;
  
  /* ********************************************** Methods ********************************************** */

  public ArrayTable()
  {
    super();
  }
  
  //  /**
  //   * Inserts empty rows at the given position and moves existing rows behind the new created rows.
  //   * 
  //   * @param rowIndexPosition
  //   * @param numberOfRows
  //   */
  //  private void insertEmptyRows( int rowIndexPosition, int numberOfRows )
  //  {
  //    for ( int ii = this.rowList.size() - 1 + numberOfRows; ii >= rowIndexPosition + numberOfRows; ii-- )
  //    {
  //      this.setRow( ii, this.rowList.get( ii - numberOfRows ) );
  //    }
  //    for ( int ii = 0; ii < numberOfRows; ii++ )
  //    {
  //      this.setRow( ii + rowIndexPosition, this.createNewEmptyRow() );
  //    }
  //  }
  //  
  //  public Table<E> insertArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  //  {
  //    //
  //    this.insertEmptyRows( rowIndexPosition, elementArray.length );
  //    //
  //    this.putArray( elementArray, rowIndexPosition, columnIndexPosition );
  //    
  //    //
  //    return this;
  //  }
  //  
  //  public Table<E> insertTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition )
  //  {
  //    //
  //    this.insertEmptyRows( rowIndexPosition, insertIndexedTable.getTableSize().getRowSize() );
  //    
  //    //
  //    this.putTable( insertIndexedTable, rowIndexPosition, columnIndexPosition );
  //    
  //    //
  //    return this;
  //  }
  //  
  //  public Table<E> putTable( Table<E> insertTable, int rowIndexPosition, int columnIndexPosition )
  //  {
  //    if ( insertTable != null )
  //    {
  //      //copy the foreign table to the current table cell for cell
  //      for ( int ii = 0; ii < insertTable.getTableSize().getRowSize(); ii++ )
  //      {
  //        for ( int jj = 0; jj < insertTable.getTableSize().getColumnSize(); jj++ )
  //        {
  //          this.setCell( ii + rowIndexPosition, jj + columnIndexPosition, insertTable.getCell( ii, jj ) );
  //        }
  //      }
  //      
  //      //copy titles for rows and columns if necessary
  //      for ( int ii = 0; ii < insertTable.getTableSize().getRowSize(); ii++ )
  //      {
  //        if ( this.getRowTitle( ii + rowIndexPosition ) == null && insertTable.getRowTitle( ii ) != null )
  //        {
  //          this.setRowTitle( insertTable.getRowTitle( ii ), ii + rowIndexPosition );
  //        }
  //      }
  //      for ( int jj = 0; jj < insertTable.getTableSize().getColumnSize(); jj++ )
  //      {
  //        if ( this.getColumnTitle( jj + columnIndexPosition ) == null && insertTable.getColumnTitle( jj ) != null )
  //        {
  //          this.setColumnTitle( insertTable.getColumnTitle( jj ), jj + columnIndexPosition );
  //        }
  //      }
  //    }
  //    //
  //    return this;
  //  }
  //  
  //  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  //  {
  //    //
  //    for ( int ii = 0; ii < elementArray.length; ii++ )
  //    {
  //      for ( int jj = 0; jj < elementArray[ii].length; jj++ )
  //      {
  //        this.setCell( rowIndexPosition + ii, columnIndexPosition + jj, elementArray[ii][jj] );
  //      }
  //    }
  //    
  //    //
  //    return this;
  //  }
  
  @Override
  public Table<E> transpose()
  {
    //
    this.stripeListContainer.switchRowAndColumnStripeList();
    
    //
    return this;
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    //
    boolean retval = this.tableSize.equals( table.getTableSize() );
    
    //
    if ( retval )
    {
      //      
      Iterator<Cell<E>> iteratorCellThis = this.iteratorCell();
      Iterator<Cell<E>> iteratorCellOther = table.iteratorCell();
      
      //
      while ( retval && iteratorCellThis.hasNext() && iteratorCellOther.hasNext() )
      {
        //
        Cell<E> cellThis = iteratorCellThis.next();
        Cell<E> cellOther = iteratorCellOther.next();
        
        //
        retval &= ( cellThis == null && cellOther == null )
                  || ( cellThis != null && cellOther != null && cellThis.hasElement( cellOther.getElement() ) );
      }
      
      //
      retval &= !iteratorCellThis.hasNext() && !iteratorCellOther.hasNext();
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> setRowTitleValue( Object titleValue, int rowIndexPosition )
  {
    //
    RowInternal<E> row = this.cellAndStripeResolver.resolveOrCreateRow( rowIndexPosition );
    row.getTitle().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowTitleValues( List<?> titleValueList )
  {
    //
    this.setStripeTitleValueList( titleValueList, StripeType.ROW );
    
    //
    return this;
  }
  
  /**
   * Sets the {@link Title} elements of the {@link Row}s or {@link Column}s
   * 
   * @param titleValueList
   * @param stripeType
   */
  protected void setStripeTitleValueList( List<? extends Object> titleValueList, StripeType stripeType )
  {
    //
    if ( titleValueList != null && stripeType != null )
    {
      //
      for ( int indexPosition = 0; indexPosition < titleValueList.size(); indexPosition++ )
      {
        //
        StripeInternal<E> stripe = this.cellAndStripeResolver.resolveOrCreateStripe( stripeType, indexPosition );
        
        //
        Title title = stripe.getTitle();
        title.setValue( titleValueList.get( indexPosition ) );
      }
    }
  }
  
  @Override
  public Table<E> setColumnTitleValue( Object titleValue, int columnIndexPosition )
  {
    //
    ColumnInternal<E> column = this.cellAndStripeResolver.resolveOrCreateColumn( columnIndexPosition );
    column.getTitle().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnTitleValues( List<?> titleValueList )
  {
    //
    this.setStripeTitleValueList( titleValueList, StripeType.COLUMN );
    
    //
    return this;
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return this.getStripeTitleValueList( StripeType.ROW );
  }
  
  /**
   * Returns the {@link Title#getValue()} elements within a {@link List}
   * 
   * @param stripeType
   * @return
   */
  protected List<Object> getStripeTitleValueList( StripeType stripeType )
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    if ( stripeType != null )
    {
      //
      StripeList<E> stripeList = this.stripeListContainer.getStripeList( stripeType );
      
      //
      for ( Stripe<E> stripe : stripeList )
      {
        //
        retlist.add( stripe.getTitle().getValue() );
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.getStripeTitleValue( StripeType.ROW, rowIndexPosition );
  }
  
  /**
   * Returns the {@link Title#getValue()} object for the given index position within the respective
   * {@link StripeListContainer#getStripeList(StripeType)}
   * 
   * @param stripeType
   * @param indexPosition
   * @return
   */
  protected Object getStripeTitleValue( StripeType stripeType, int indexPosition )
  {
    //
    Object retval = null;
    
    //
    if ( stripeType != null && indexPosition >= 0 )
    {
      //
      StripeInternal<E> stripe = this.cellAndStripeResolver.resolveStripe( stripeType, indexPosition );
      
      //
      if ( stripe != null )
      {
        retval = stripe.getTitle().getValue();
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    return this.getStripeTitleValueList( StripeType.COLUMN );
  }
  
  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    return this.getStripeTitleValue( StripeType.COLUMN, columnIndexPosition );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.tableSize;
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    //
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCell( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    if ( rowCellElementList != null )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < rowCellElementList.size(); columnIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, rowCellElementList.get( columnIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //
    if ( columnCellElementList != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < columnCellElementList.size(); rowIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, columnCellElementList.get( rowIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> addColumnCellElements( List<? extends E> columnCellElementList )
  {
    //
    int columnIndexPosition = this.tableSize.getColumnSize();
    this.setColumnCellElements( columnIndexPosition, columnCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //    
    StripeInternal<E> newStripe = this.stripeListContainer.getColumnList().addNewStripe( columnIndexPosition );
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
    int rowIndexPosition = this.tableSize.getRowSize();
    this.setRowCellElements( rowIndexPosition, rowCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    StripeInternal<E> newStripe = this.stripeListContainer.getRowList().addNewStripe( rowIndexPosition );
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
    RowInternal<E> rowInternal = this.cellAndStripeResolver.resolveRow( rowIndexPosition );
    if ( rowInternal != null )
    {
      //
      retlist.addAll( rowInternal.getCellElementList() );
      
      //
      this.stripeListContainer.getRowList().removeStripeAndDetachCellsFromTable( rowIndexPosition );
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
    ColumnInternal<E> columnInternal = this.cellAndStripeResolver.resolveColumn( columnIndexPosition );
    if ( columnInternal != null )
    {
      //
      retlist.addAll( columnInternal.getCellElementList() );
      
      //
      this.stripeListContainer.getColumnList().removeStripeAndDetachCellsFromTable( columnIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    return this.cellAndStripeResolver.resolveRow( rowIndexPosition );
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    return this.cellAndStripeResolver.resolveRow( rowTitleValue );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveColumn( columnIndexPosition );
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveColumn( columnTitleValue );
  }
  
  @Override
  public Table<E> clear()
  {
    //
    this.tableName = null;
    this.stripeListContainer.clear();
    
    //
    return this;
  }
  
  @Override
  public Table<E> cloneTableStructure()
  {
    //TODO
    
    //
    return null;
  }
  
  @Override
  public Table<E> clone()
  {
    //TODO
    //
    return null;
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
  public Iterator<Cell<E>> iteratorCell()
  {
    return new Iterator<Cell<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int cellIndexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */

      @Override
      public boolean hasNext()
      {
        return this.cellIndexPosition + 1 < ArrayTable.this.tableSize.getCellSize();
      }
      
      @Override
      public Cell<E> next()
      {
        return ArrayTable.this.getCell( ++this.cellIndexPosition );
      }
      
      @Override
      public void remove()
      {
        Cell<E> cell = ArrayTable.this.getCell( this.cellIndexPosition-- );
        if ( cell != null )
        {
          cell.setElement( null );
        }
      }
    };
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
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCell( cellIndexPosition );
    
    //
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    //
    if ( tableCellVisitor != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        for ( int columnIndexPosition = 0; columnIndexPosition < this.getTableSize().getColumnSize(); columnIndexPosition++ )
        {
          tableCellVisitor.process( rowIndexPosition, columnIndexPosition, this.getCell( rowIndexPosition, columnIndexPosition ) );
        }
      }
    }
    
    //
    return this;
  }
  
  @Override
  public <TO> Table<TO> convert( final TableCellConverter<E, TO> tableCellConverter )
  {
    //
    final Table<TO> table = new ArrayTable<TO>();
    
    //
    this.processTableCells( new TableCellVisitor<E>()
    {
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell )
      {
        table.setCellElement( rowIndexPosition, columnIndexPosition, tableCellConverter.convert( cell.getElement() ) );
      }
    } );
    
    //
    return table;
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.iteratorRow();
  }
  
  public Iterable<Cell<E>> cells()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        return ArrayTable.this.iteratorCell();
      }
    };
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
    //TODO can this be optimized by indexes?
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      if ( cell.hasElement( element ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public String toString()
  {
    //TODO take the implementation of the tablehelper to enhance this
    //
    StringBuilder sb = new StringBuilder();
    
    //
    String rowDelimiter = "";
    for ( Row<E> row : this )
    {
      //
      sb.append( rowDelimiter + "[" );
      
      //
      String elementDelimiter = "";
      for ( Cell<E> cell : row )
      {
        //
        E element = cell.getElement();
        
        //
        String elementValue = String.valueOf( element );
        sb.append( elementDelimiter + elementValue );
        elementDelimiter = ",";
      }
      
      //
      sb.append( "]" );
      rowDelimiter = "\n";
    }
    
    return sb.toString();
  }
  
  @Override
  public StripeListContainer<E> getStripeListContainer()
  {
    return this.stripeListContainer;
  }
  
  @Override
  public CellAndStripeResolver<E> getCellAndStripeResolver()
  {
    return this.cellAndStripeResolver;
  }
  
  @Override
  public Table<E> insertArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> insertTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> putTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public org.omnaest.utils.structure.table.Table.Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public org.omnaest.utils.structure.table.Table.Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public org.omnaest.utils.structure.table.Table.Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public List<E> getCellList()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Iterator<Row<E>> iteratorRow()
  {
    return new Iterator<Row<E>>()
    {
      protected int rowIndexPosition = -1;
      
      @Override
      public boolean hasNext()
      {
        return this.rowIndexPosition + 1 < ArrayTable.this.getTableSize().getRowSize();
      }
      
      @Override
      public Row<E> next()
      {
        return ArrayTable.this.getRow( ++this.rowIndexPosition );
      }
      
      @Override
      public void remove()
      {
        ArrayTable.this.removeRow( this.rowIndexPosition-- );
      }
    };
  }
  
  @Override
  public Table<E> innerJoinByEqualColumn( Table<E> joinTable, int[][] columnIndexPositionPairs )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> innerJoinByEqualColumn( Table<E> joinTable, List<IndexPositionPair> columnIndexPositionPairList )
  {
    // TODO Auto-generated method stub
    return null;
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
  
}
