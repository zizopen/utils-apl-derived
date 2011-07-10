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

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.table.IndexTable.IndexPositionPair;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.components.CellAndStripeResolverImpl;
import org.omnaest.utils.structure.table.concrete.components.StripeListContainerImpl;

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
  protected TableSize                tableSize             = new TableSizeImpl();
  protected Object                   tableName             = null;
  
  /* ********************************************** Methods ********************************************** */

  public ArrayTable()
  {
    super();
  }
  
  /**
   * Removes the subtractIndexPositions from the baseIndexPositions.
   * 
   * @param baseIndexPositions
   * @param subtractIndexPositions
   * @return
   */
  protected int[] determineSubtractedIndexPositions( int[] baseIndexPositions, int[] subtractIndexPositions )
  {
    List<Integer> subtractedRowIndexPositionList = new ArrayList<Integer>( 0 );
    CollectionUtils.addAll( subtractedRowIndexPositionList, baseIndexPositions );
    
    for ( int iRowIndexPosition : subtractIndexPositions )
    {
      subtractedRowIndexPositionList.remove( Integer.valueOf( iRowIndexPosition ) );
    }
    
    return CollectionUtils.toArrayInt( subtractedRowIndexPositionList );
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
  
  /**
   * Returns true if two cells are equal.
   * 
   * @see #equals(Table)
   * @param cell1
   * @param cell2
   * @return
   */
  protected boolean areCellsEqual( E cell1, E cell2 )
  {
    return cell1 == cell2 || cell1 != null && cell2 != null && cell1.equals( cell2 );
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    //TODO
    
    //
    return false;
  }
  
  @Override
  public Table<E> setRowTitle( Object titleValue, int rowIndexPosition )
  {
    //
    RowInternal<E> row = this.cellAndStripeResolver.resolveOrCreateRow( rowIndexPosition );
    row.getTitle().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowTitles( List<String> titleList )
  {
    //
    this.setStripeTitleValueList( titleList, StripeType.ROW );
    
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
  public Table<E> setColumnTitle( Object titleValue, int columnIndexPosition )
  {
    //
    ColumnInternal<E> column = this.cellAndStripeResolver.resolveOrCreateColumn( columnIndexPosition );
    column.getTitle().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnTitles( List<String> titleList )
  {
    //
    this.setStripeTitleValueList( titleList, StripeType.COLUMN );
    
    //
    return this;
  }
  
  @Override
  public List<Object> getRowTitleList()
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
  public Object getRowTitle( int rowIndexPosition )
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
  public List<Object> getColumnTitleList()
  {
    return this.getStripeTitleValueList( StripeType.COLUMN );
  }
  
  @Override
  public String[] getColumnTitles()
  {
    return this.getColumnTitleList().toArray( new String[0] );
  }
  
  @Override
  public Object getColumnTitle( int columnIndexPosition )
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
  
  public Table<E> setRow( int rowIndexPosition, List<E> row )
  {
    //
    for ( int ii = 0; ii < row.size(); ii++ )
    {
      this.setCellElement( rowIndexPosition, ii, row.get( ii ) );
    }
    
    //
    return this;
  }
  
  public Table<E> setRow( int rowIndexPosition, Object beanObject )
  {
    //
    //TODO
    
    //
    return this;
  }
  
  public Table<E> setColumn( int columnIndexPosition, List<E> column )
  {
    //
    for ( int ii = 0; ii < column.size(); ii++ )
    {
      this.setCellElement( ii, columnIndexPosition, column.get( ii ) );
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
    return this.getCell( rowIndexPosition, columnIndexPosition ).getElement();
  }
  
  public Table<E> addColumn( List<E> columnValues )
  {
    //TODO
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRow( List<E> row )
  {
    //TODO
    //
    return this;
  }
  
  @Override
  public Table<E> addRow( int rowIndexPosition, List<E> row )
  {
    //TODO
    
    //
    return this;
  }
  
  @Override
  public List<E> removeRow( int rowIndexPosition )
  {
    //TODO
    
    //
    return null;
  }
  
  public List<E> removeColumn( int columnIndexPosition )
  {
    //TODO
    
    //
    return null;
  }
  
  public List<E>[] removeRows( int[] rowIndexPositions )
  {
    //TODO
    
    //
    return null;
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    return this.cellAndStripeResolver.resolveRow( rowIndexPosition );
  }
  
  @Override
  public Row<E> getRow( String rowTitle )
  {
    return this.cellAndStripeResolver.resolveRow( rowTitle );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveColumn( columnIndexPosition );
  }
  
  @Override
  public Column<E> getColumn( String columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveColumn( columnTitleValue );
  }
  
  public Table<E> clear()
  {
    //TODO
    
    //
    return this;
  }
  
  public Table<E> cloneTableStructure()
  {
    //TODO
    
    //
    return null;
  }
  
  public Table<E> clone()
  {
    //TODO
    //
    return null;
  }
  
  @Override
  public String getTableName()
  {
    //TODO
    return null;
  }
  
  @Override
  public Table<E> setTableName( String tableName )
  {
    //
    this.tableName = tableName;
    
    //
    return this;
  }
  
  int[] generateIndexArrayForBetween( int indexPositionFrom, int indexPositionTo )
  {
    int[] indexes = new int[indexPositionTo - indexPositionFrom + 1];
    for ( int ii = indexPositionFrom; ii <= indexPositionTo; ii++ )
    {
      indexes[ii - indexPositionFrom] = ii;
    }
    return indexes;
  }
  
  /* ********************************************** Classes ********************************************** */

  /**
   * @see TableSize
   * @author Omnaest
   */
  protected class TableSizeImpl implements TableSize
  {
    @Override
    public int getCellSize()
    {
      return this.getRowSize() * this.getColumnSize();
    }
    
    @Override
    public int getRowSize()
    {
      return ArrayTable.this.stripeListContainer.getRowList().size();
    }
    
    @Override
    public int getColumnSize()
    {
      return ArrayTable.this.stripeListContainer.getColumnList().size();
    }
  }
  
  @Override
  public Iterator<Cell<E>> cellIterator()
  {
    //TODO
    return null;
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( cellIndexPosition );
  }
  
  @Override
  public Table<E> setCell( int cellIndexPosition, E element )
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
          tableCellVisitor.inspect( rowIndexPosition, columnIndexPosition, this.getCell( rowIndexPosition, columnIndexPosition ) );
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
      public void inspect( int rowIndexPosition, int columnIndexPosition, Cell<E> cell )
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
    return this.rowIterator();
  }
  
  public Iterable<Cell<E>> cellIterable()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        return ArrayTable.this.cellIterator();
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
    for ( Cell<E> cell : this.cellIterable() )
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
    StringBuilder sb = new StringBuilder();
    sb.append( "[" );
    Iterator<Row<E>> rowIterator = this.rowIterator();
    String rowDelimiter = "";
    while ( rowIterator.hasNext() )
    {
      //
      sb.append( rowDelimiter + "[" );
      
      //
      Row<E> row = rowIterator.next();
      
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
      rowDelimiter = ",";
    }
    sb.append( "]" );
    return sb.toString();
  }
  
  @Override
  public StripeListContainer<E> getStripeListContainer()
  {
    return this.stripeListContainer;
  }
  
  @Override
  public CellAndStripeResolver<E> getCellResolver()
  {
    return this.cellAndStripeResolver;
  }
  
  @Override
  public Table<E> getSubTableByRows( int rowIndexPositionFrom, int rowIndexPositionTo )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTableByRows( int[] rowIndexPositions )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTableByRows( List<Integer> rowIndexPositionList )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTableByColumns( int[] columnIndexPositions )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTableByColumns( List<Integer> columnIndexPositionList )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTable( int[] rowIndexPositions, int[] columnIndexPositions )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTable( int rowIndexPositionFrom,
                               int rowIndexPositionTo,
                               int columnIndexPositionFrom,
                               int columnIndexPositionTo )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> getSubTableByColumns( int colunmIndexPositionFrom, int colunmIndexPositionTo )
  {
    // TODO Auto-generated method stub
    return null;
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
  public Table<E> setRowTitles( Enum<?>[] rowTitleEnums )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> setRowTitles( String[] titles )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> setColumnTitles( Enum<?>[] titleEnumerations )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> setColumnTitles( String... titles )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> setColumnTitles( Class<?> beanClass )
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
  public Table<E> addRow( Object beanObject )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public <B> B getRowAsBean( B beanObject, int rowIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public <C> C getRow( int rowIndexPosition, C emptyBeanObject )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public boolean containsRow( List<E> row )
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override
  public boolean containsRow( Object beanObject )
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override
  public int indexOfRow( List<E> row )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int lastIndexOfRow( List<E> row )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int indexOfFirstColumnWithElementEquals( int rowIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int indexOfFirstRowWithElementEquals( int columnIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int[] indexesOfRowsWithElementsEquals( int columnIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int lastIndexOfElementWithinRow( int rowIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int lastIndexOfElementWithinColumn( int columnIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Iterator<org.omnaest.utils.structure.table.Table.Row<E>> rowIterator()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> convertFirstRowToTitle()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> convertFirstColumnToTitle()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Table<E> distinct()
  {
    // TODO Auto-generated method stub
    return null;
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
  
}
