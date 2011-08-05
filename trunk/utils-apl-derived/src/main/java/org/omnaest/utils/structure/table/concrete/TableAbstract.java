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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.list.iterator.ListIterable;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.internal.adapterprovider.TableAdapterProviderImpl;
import org.omnaest.utils.structure.table.concrete.internal.iterator.TableRowListIterator;
import org.omnaest.utils.structure.table.concrete.internal.serializer.TableSerializerImpl;
import org.omnaest.utils.structure.table.helper.TableHelper;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * @see Table
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements Table<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long    serialVersionUID = 413819072598819746L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableSerializer<E> tableSerializer  = new TableSerializerImpl<E>( this );
  
  /* ********************************************** Methods ********************************************** */
  
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
  public Table<E> setRowTitleValues( List<?> titleValueList )
  {
    //
    this.setStripeTitleValueList( titleValueList, StripeType.ROW );
    
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
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.getStripeTitleValue( StripeType.ROW, rowIndexPosition );
  }
  
  /**
   * Returns the {@link Title#getValue()} object for the given index position within the respective
   * {@link TableContent#getStripeDataList(StripeType)}
   * 
   * @param stripeType
   * @param indexPosition
   * @return
   */
  protected abstract Object getStripeTitleValue( StripeType stripeType, int indexPosition );
  
  /**
   * Returns the {@link Title#getValue()} elements within a {@link List}
   * 
   * @param stripeType
   * @return
   */
  protected abstract List<Object> getStripeTitleValueList( StripeType stripeType );
  
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
  
  /**
   * Sets the {@link Title#setValue(Object)} of the {@link Row}s or {@link Column}s
   * 
   * @param titleValueList
   * @param stripeType
   */
  protected abstract void setStripeTitleValueList( List<?> titleValueList, StripeType row );
  
  @Override
  public List<List<E>> removeRows( int[] rowIndexPositions )
  {
    //
    List<List<E>> retlist = new ArrayList<List<E>>();
    
    //
    for ( int rowIndexPosition : rowIndexPositions )
    {
      //
      List<E> rowElementList = this.removeRow( rowIndexPosition );
      
      //
      retlist.add( rowElementList );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Table<E> truncateRows( int rowIndexPosition )
  {
    //
    rowIndexPosition = rowIndexPosition >= 0 ? rowIndexPosition : 0;
    
    //
    TableSize tableSize = this.getTableSize();
    while ( tableSize.getRowSize() > rowIndexPosition )
    {
      this.removeRow( rowIndexPosition );
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
        return this.cellIndexPosition + 1 < TableAbstract.this.getTableSize().getCellSize();
      }
      
      @Override
      public Cell<E> next()
      {
        return TableAbstract.this.getCell( ++this.cellIndexPosition );
      }
      
      @Override
      public void remove()
      {
        Cell<E> cell = TableAbstract.this.getCell( this.cellIndexPosition-- );
        if ( cell != null )
        {
          cell.setElement( null );
        }
      }
    };
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
    table.setTableName( this.getTableName() );
    table.setColumnTitleValues( this.getColumnTitleValueList() );
    table.setRowTitleValues( this.getRowTitleValueList() );
    
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
    return this.rows().iterator();
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
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
  public Table<E> putTable( TableDataSource<E> tableDataSource, int rowIndexPosition, int columnIndexPosition )
  {
    //
    if ( tableDataSource != null )
    {
      //
      Iterable<? extends Iterable<? extends CellImmutable<E>>> rows = tableDataSource.rows();
      if ( rows != null )
      {
        //
        Iterator<? extends Iterable<? extends CellImmutable<E>>> iteratorRows = rows.iterator();
        if ( iteratorRows != null )
        {
          for ( int rowIndexPositionCurrent = rowIndexPosition; iteratorRows.hasNext(); rowIndexPositionCurrent++ )
          {
            //
            Iterable<? extends CellImmutable<E>> iterableCells = iteratorRows.next();
            if ( iterableCells != null )
            {
              //
              Iterator<? extends CellImmutable<E>> iteratorCells = iterableCells.iterator();
              if ( iteratorCells != null )
              {
                for ( int columnIndexPositionCurrent = columnIndexPosition; iteratorCells.hasNext(); columnIndexPositionCurrent++ )
                {
                  //
                  CellImmutable<E> cellImmutable = iteratorCells.next();
                  
                  //
                  this.setCellElement( rowIndexPositionCurrent, columnIndexPositionCurrent,
                                       cellImmutable != null ? cellImmutable.getElement() : null );
                }
              }
            }
          }
        }
      }
      
      //
      {
        //
        List<Object> rowTitleValueList = tableDataSource.getRowTitleValueList();
        int rowIndexPositionCurrent = rowIndexPosition;
        for ( Object titleValue : rowTitleValueList )
        {
          this.setRowTitleValue( titleValue, rowIndexPositionCurrent++ );
        }
      }
      
      //
      {
        //
        List<Object> columnTitleValueList = tableDataSource.getColumnTitleValueList();
        int columnIndexPositionCurrent = columnIndexPosition;
        for ( Object titleValue : columnTitleValueList )
        {
          this.setColumnTitleValue( titleValue, columnIndexPositionCurrent++ );
        }
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        return TableAbstract.this.iteratorCell();
      }
    };
  }
  
  @Override
  public ListIterable<Row<E>> rows()
  {
    return new ListIterable<Row<E>>()
    {
      @Override
      public ListIterator<Row<E>> iterator()
      {
        return new TableRowListIterator<E>( TableAbstract.this );
      }
    };
  }
  
  @Override
  public Iterable<Column<E>> columns()
  {
    return new Iterable<Column<E>>()
    {
      @Override
      public Iterator<Column<E>> iterator()
      {
        return TableAbstract.this.iteratorColumn();
      }
    };
  }
  
  @Override
  public Table<E> convertFirstRowToTitle()
  {
    //
    final int rowIndexPosition = 0;
    for ( int columnIndexPosition = 0; columnIndexPosition < this.getTableSize().getColumnSize(); columnIndexPosition++ )
    {
      //
      Object titleValue = this.getCellElement( rowIndexPosition, columnIndexPosition );
      this.setColumnTitleValue( titleValue, columnIndexPosition );
    }
    
    //
    this.removeRow( 0 );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> convertFirstColumnToTitle()
  {
    //
    final int columnIndexPosition = 0;
    for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
    {
      //
      Object titleValue = this.getCellElement( rowIndexPosition, columnIndexPosition );
      this.setRowTitleValue( titleValue, rowIndexPosition );
    }
    
    //
    this.removeColumn( 0 );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    //
    for ( int ii = 0; ii < elementArray.length; ii++ )
    {
      for ( int jj = 0; jj < elementArray[ii].length; jj++ )
      {
        this.setCellElement( rowIndexPosition + ii, columnIndexPosition + jj, elementArray[ii][jj] );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return ListUtils.iteratorAsList( this.iteratorCell() );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //    
    List<E> retlist = new ArrayList<E>();
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      retlist.add( cell != null ? cell.getElement() : null );
    }
    
    // 
    return retlist;
  }
  
  @Override
  public Iterator<Column<E>> iteratorColumn()
  {
    return new Iterator<Column<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int columnIndexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return this.columnIndexPosition + 1 < TableAbstract.this.getTableSize().getColumnSize();
      }
      
      @Override
      public Column<E> next()
      {
        return TableAbstract.this.getColumn( ++this.columnIndexPosition );
      }
      
      @Override
      public void remove()
      {
        TableAbstract.this.removeColumn( this.columnIndexPosition-- );
      }
    };
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    for ( Cell<E> cell : this.cells() )
    {
      result = prime * result + ( ( cell == null ) ? 0 : cell.hashCode() );
    }
    return result;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals( Object object )
  {
    return object instanceof Table && this.equals( (Table<E>) object );
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    //
    boolean retval = table != null;
    
    //
    if ( retval )
    {
      //
      retval = this.getTableSize().equals( table.getTableSize() );
      
      //
      retval &= this.getTableName() == table.getTableName()
                || ( this.getTableName() != null && this.getTableName().equals( table.getTableName() ) );
      
      //
      retval &= this.getColumnTitleValueList().equals( table.getColumnTitleValueList() );
      retval &= this.getRowTitleValueList().equals( table.getRowTitleValueList() );
      
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
    }
    
    //
    return retval;
  }
  
  @Override
  public String toString()
  {
    return TableHelper.renderToString( this );
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return ListUtils.iteratorAsList( this.rows().iterator() );
  }
  
  @Override
  public List<Column<E>> getColumnList()
  {
    return ListUtils.iteratorAsList( this.iteratorColumn() );
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    //
    boolean retval = false;
    
    //
    for ( Column<E> column : this.columns() )
    {
      if ( column.hasTitle() )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.getTableName() != null;
  }
  
  @Override
  public boolean hasRowTitles()
  {
    //
    boolean retval = false;
    
    //
    for ( Row<E> row : this.rows() )
    {
      if ( row.hasTitle() )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> setNumberOfColumns( int numberOfColumns )
  {
    //
    for ( int columnIndexPosition = this.getTableSize().getColumnSize() - 1; columnIndexPosition >= numberOfColumns; columnIndexPosition-- )
    {
      this.removeColumn( columnIndexPosition );
    }
    
    //
    this.ensureNumberOfColumns( numberOfColumns );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> ensureNumberOfColumns( int numberOfColumns )
  {
    //
    if ( numberOfColumns > 0 && this.getTableSize().getColumnSize() < numberOfColumns )
    {
      //
      int rowIndexPosition = 0;
      int columnIndexPosition = numberOfColumns - 1;
      E element = null;
      this.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    // 
    return this;
  }
  
  @Override
  public Table<E> setNumberOfRows( int numberOfRows )
  {
    //
    for ( int rowIndexPosition = this.getTableSize().getColumnSize() - 1; rowIndexPosition >= numberOfRows; rowIndexPosition-- )
    {
      this.removeRow( rowIndexPosition );
    }
    
    //
    this.ensureNumberOfRows( numberOfRows );
    
    //
    return this;
  }
  
  @Override
  public Table<E> ensureNumberOfRows( int numberOfRows )
  {
    //
    if ( numberOfRows > 0 && this.getTableSize().getRowSize() < numberOfRows )
    {
      //
      int rowIndexPosition = numberOfRows - 1;
      int columnIndexPosition = 0;
      E element = null;
      this.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    // 
    return this;
  }
  
  @Override
  public TableSerializer<E> serializer()
  {
    return this.tableSerializer;
  }
  
  @Override
  public Table<E> copyFrom( TableDataSource<E> tableDataSource )
  {
    //
    if ( tableDataSource != null )
    {
      //
      this.clear();
      
      //
      int rowIndexPosition = 0;
      int columnIndexPosition = 0;
      this.putTable( tableDataSource, rowIndexPosition, columnIndexPosition );
      
      //
      this.setRowTitleValues( tableDataSource.getRowTitleValueList() );
      this.setColumnTitleValues( tableDataSource.getColumnTitleValueList() );
      this.setTableName( tableDataSource.getTableName() );
    }
    
    // 
    return this;
  }
  
  @Override
  public Table<E> setRowTitleValues( Object... titleValues )
  {
    //
    this.setRowTitleValues( Arrays.asList( titleValues ) );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> setColumnTitleValues( Object... titleValues )
  {
    //
    this.setColumnTitleValues( Arrays.asList( titleValues ) );
    
    //
    return this;
  }
  
  @Override
  public List<E> removeColumn( Column<E> column )
  {
    //
    List<E> retlist = null;
    
    //
    if ( column != null )
    {
      //
      int columnIndexPosition = column.determineColumnIndexPosition();
      
      //
      retlist = this.removeColumn( columnIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public List<E> removeRow( Row<E> row )
  {
    //
    List<E> retlist = null;
    
    //
    if ( row != null )
    {
      //
      int rowIndexPosition = row.determineRowIndexPosition();
      
      //
      retlist = this.removeRow( rowIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Row<E> getLastRow()
  {
    return this.getRow( this.getTableSize().getRowSize() - 1 );
  }
  
  @Override
  public Column<E> getLastColumn()
  {
    return this.getColumn( this.getTableSize().getColumnSize() - 1 );
  }
  
  @Override
  public Row<E> getFirstRow()
  {
    return this.getRow( 0 );
  }
  
  @Override
  public Column<E> getFirstColumn()
  {
    return this.getColumn( 0 );
  }
  
  @Override
  public TableAdapterProvider<E> as()
  {
    return new TableAdapterProviderImpl<E>( this );
  }
  
}
