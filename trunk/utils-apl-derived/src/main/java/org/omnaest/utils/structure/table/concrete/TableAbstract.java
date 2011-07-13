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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.TableXMLSerializable;
import org.omnaest.utils.structure.table.helper.TableHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements TableInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long         serialVersionUID     = 413819072598819746L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableXMLSerializable<E> tableXMLSerializable = new TableXMLSerializableImpl<E>( this );
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public abstract Table<E> clone();
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals( Object object )
  {
    return object instanceof Table && this.equals( (Table<E>) object );
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
  public Table<E> putTable( Table<E> table, int rowIndexPosition, int columnIndexPosition )
  {
    //
    if ( table != null )
    {
      //
      for ( int ii = 0; ii < table.getTableSize().getRowSize(); ii++ )
      {
        for ( int jj = 0; jj < table.getTableSize().getColumnSize(); jj++ )
        {
          this.setCellElement( ii + rowIndexPosition, jj + columnIndexPosition, table.getCellElement( ii, jj ) );
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
  public Iterable<Row<E>> rows()
  {
    return new Iterable<Row<E>>()
    {
      @Override
      public Iterator<Row<E>> iterator()
      {
        return TableAbstract.this.iteratorRow();
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
  public Iterator<Row<E>> iteratorRow()
  {
    return new Iterator<Row<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int rowIndexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return this.rowIndexPosition + 1 < TableAbstract.this.getTableSize().getRowSize();
      }
      
      @Override
      public Row<E> next()
      {
        return TableAbstract.this.getRow( ++this.rowIndexPosition );
      }
      
      @Override
      public void remove()
      {
        TableAbstract.this.removeRow( this.rowIndexPosition-- );
      }
    };
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    //
    boolean retval = this.getTableSize().equals( table.getTableSize() );
    
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
  public String toString()
  {
    return TableHelper.renderToString( this );
  }
  
  @Override
  public Table<E> writeAsXMLTo( Appendable appendable )
  {
    return this.tableXMLSerializable.writeAsXMLTo( appendable );
  }
  
  @Override
  public Table<E> writeAsXMLTo( OutputStream outputStream )
  {
    return this.tableXMLSerializable.writeAsXMLTo( outputStream );
  }
  
  @Override
  public Table<E> parseXMLFrom( CharSequence charSequence )
  {
    return this.tableXMLSerializable.parseXMLFrom( charSequence );
  }
  
  @Override
  public Table<E> parseXMLFrom( String xmlContent )
  {
    return this.tableXMLSerializable.parseXMLFrom( xmlContent );
  }
  
  @Override
  public Table<E> parseXMLFrom( InputStream inputStream )
  {
    return this.tableXMLSerializable.parseXMLFrom( inputStream );
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return ListUtils.iteratorAsList( this.iteratorRow() );
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
  
}
