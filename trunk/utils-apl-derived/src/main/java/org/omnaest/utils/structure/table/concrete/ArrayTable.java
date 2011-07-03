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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.omnaest.utils.beans.BeanUtil;
import org.omnaest.utils.beans.BeanUtil.BeanProperty;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.IndexArrayList;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.collection.list.ListToListIteratorAdapter;
import org.omnaest.utils.structure.table.IndexTable.IndexPositionPair;
import org.omnaest.utils.structure.table.IndexTable.TableSize;
import org.omnaest.utils.structure.table.Table;

/**
 * Implementation of {@link Table} that uses two array lists as row and column data structure.
 * 
 * @see Table
 * @author Omnaest
 */
public class ArrayTable<E> implements Table<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 1763808639838518679L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<List<E>>   rowList          = new ArrayList<List<E>>( 0 );
  protected List<List<E>>   columnList       = new ArrayList<List<E>>( 0 );
  /** Defines the row and column titles */
  private TableHeader       tableHeader      = new TableHeader();
  
  /* ********************************************** Methods ********************************************** */
  public ArrayTable()
  {
    super();
  }
  
  public Table<E> getSubTableByRows( int rowIndexPositionFrom, int rowIndexPositionTo )
  {
    return this.getSubTableByRows( this.generateIndexArrayForBetween( rowIndexPositionFrom, rowIndexPositionTo ) );
  }
  
  public Table<E> getSubTableByRows( int[] rowIndexPositions )
  {
    return this.getSubTable( rowIndexPositions, this.generateIndexArrayForBetween( 0, this.columnList.size() - 1 ) );
  }
  
  public Table<E> getSubTableByRows( List<Integer> rowIndexPositionList )
  {
    int[] rowIndexPositions = new int[rowIndexPositionList.size()];
    for ( int ii = 0; ii < rowIndexPositions.length; ii++ )
    {
      rowIndexPositions[ii] = rowIndexPositionList.get( ii );
    }
    
    return this.getSubTableByRows( rowIndexPositions );
  }
  
  public Table<E> getSubTableByColumns( int[] columnIndexPositions )
  {
    return this.getSubTable( this.generateIndexArrayForBetween( 0, this.rowList.size() - 1 ), columnIndexPositions );
  }
  
  public Table<E> getSubTableByColumns( List<Integer> columnIndexPositionList )
  {
    int[] columnIndexPositions = new int[columnIndexPositionList.size()];
    for ( int ii = 0; ii < columnIndexPositions.length; ii++ )
    {
      columnIndexPositions[ii] = columnIndexPositionList.get( ii );
    }
    
    return this.getSubTableByColumns( columnIndexPositions );
  }
  
  /**
   * Converts a array of given index positions into a sorted list.
   * 
   * @param indexPositions
   * @return
   */
  protected List<Integer> determineIndexPositionList( int[] indexPositions )
  {
    //
    List<Integer> indexPositionList = new ArrayList<Integer>( indexPositions.length );
    
    //
    for ( int iRowIndexPosition : indexPositions )
    {
      indexPositionList.add( iRowIndexPosition );
    }
    
    //
    Collections.sort( indexPositionList );
    
    //
    return indexPositionList;
  }
  
  public Table<E> getSubTable( int[] rowIndexPositions, int[] columnIndexPositions )
  {
    //
    ArrayTable<E> rettable = new ArrayTable<E>();
    
    //generate a sorted list 
    List<Integer> rowIndexPositionList = this.determineIndexPositionList( rowIndexPositions );
    List<Integer> columnIndexPositionList = this.determineIndexPositionList( columnIndexPositions );
    
    //add row and column titles
    rettable.tableHeader = new TableHeader();
    for ( int iColumnIndexPosition : columnIndexPositionList )
    {
      if ( iColumnIndexPosition < this.tableHeader.getColumnTitles().size() )
      {
        rettable.tableHeader.columnTitles.add( this.tableHeader.getColumnTitles().get( iColumnIndexPosition ) );
      }
    }
    for ( int iRowIndexPosition : rowIndexPositionList )
    {
      if ( iRowIndexPosition < this.tableHeader.getRowTitles().size() )
      {
        rettable.tableHeader.rowTitles.add( this.tableHeader.getRowTitles().get( iRowIndexPosition ) );
      }
    }
    
    //add columns
    {
      int columnIndexPosition = 0;
      for ( int iColumnIndexPosition : columnIndexPositionList )
      {
        int rowIndexPosition = 0;
        for ( int iRowIndexPosition : rowIndexPositionList )
        {
          rettable.setCell( rowIndexPosition, columnIndexPosition, this.getCell( iRowIndexPosition, iColumnIndexPosition ) );
          rowIndexPosition++;
        }
        columnIndexPosition++;
      }
    }
    
    //
    return rettable;
  }
  
  public Table<E> getSubTable( int rowIndexPositionFrom,
                               int rowIndexPositionTo,
                               int columnIndexPositionFrom,
                               int columnIndexPositionTo )
  {
    return this.getSubTable( this.generateIndexArrayForBetween( rowIndexPositionFrom, rowIndexPositionTo ),
                             this.generateIndexArrayForBetween( columnIndexPositionFrom, columnIndexPositionTo ) );
  }
  
  public Table<E> getSubTableByColumns( int colunmIndexPositionFrom, int colunmIndexPositionTo )
  {
    return this.getSubTableByColumns( this.generateIndexArrayForBetween( colunmIndexPositionFrom, colunmIndexPositionTo ) );
  }
  
  public Table<E> innerJoinByEqualColumn( Table<E> joinTable, int[][] columnIndexPositionPairs )
  {
    //create column index position pairs
    List<IndexPositionPair> columnIndexPositionPairList = new ArrayList<IndexPositionPair>( 0 );
    
    for ( int ii = 0; ii < columnIndexPositionPairs.length; ii++ )
    {
      int[] columnIndexPositionPair = columnIndexPositionPairs[ii];
      int currentTableColumnIndexPosition = columnIndexPositionPair[0];
      int joinTableColumnIndexPosition = columnIndexPositionPair[1];
      
      IndexPositionPair indexPositionPair = new IndexPositionPairImpl();
      indexPositionPair.setCurrentTableIndexPosition( currentTableColumnIndexPosition );
      indexPositionPair.setJoinTableIndexPosition( joinTableColumnIndexPosition );
      
      columnIndexPositionPairList.add( indexPositionPair );
    }
    
    return this.innerJoinByEqualColumn( joinTable, columnIndexPositionPairList );
  }
  
  public Table<E> innerJoinByEqualColumn( Table<E> joinTable, List<IndexPositionPair> columnIndexPositionPairList )
  {
    //
    Table<E> rettable = new ArrayTable<E>();
    
    /*
     * First make sure the smaller table is executing the job. This should reduce the costs, because
     * the lookup will be done by the joinTable, and the joinTable can do that with log(n2) cost, if it is an indexed table,
     * while the current table has to loop through the whole rows, which is a cost of (n1). As long
     * as n1 * log(n2) < n2 * log(n1) is true, the join operation should be done by this table.
     */
    int currentTableRowSize = this.getTableSize().getRowSize();
    int joinTableRowSize = joinTable.getTableSize().getRowSize();
    boolean thisTableShouldBeTheJoinTable = currentTableRowSize > 10
                                            && joinTableRowSize > 10
                                            && currentTableRowSize * Math.log10( joinTableRowSize ) > joinTableRowSize
                                                                                                      * Math.log10( currentTableRowSize );
    if ( thisTableShouldBeTheJoinTable )
    {
      rettable = joinTable.innerJoinByEqualColumn( joinTable, columnIndexPositionPairList );
    }
    else
    {
      rettable = this.doInnerJoinByEqualColumn( joinTable, columnIndexPositionPairList );
    }
    
    //
    return rettable;
  }
  
  protected Table<E> doInnerJoinByEqualColumn( Table<E> joinTable, List<IndexPositionPair> columnIndexPositionPairList )
  {
    /*
     * Step through each row.
     *  1. Go column for column pair 
     *    - Try to resolve all rowIndexPositions
     *      from the other table 
     *    - break the cycle if the number of indexes resolved from the other
     *      table are smaller than log(tablesize other table) 
     * 2. merge the current available indexes
     *    beginning with the colunms which have the smallest numbers of results 
     * 3. if the columns are
     *    not completely walked through, test the remaining row indexes to have equal values for all
     *    columns 
     * 4. merge subparts of both tables together to a new table
     */

    //
    Table<E> rettable = new ArrayTable<E>();
    for ( int iRowIndexPosition = 0; iRowIndexPosition < this.rowList.size(); iRowIndexPosition++ )
    {
      //
      List<MatchingIndexes> matchingIndexesList = new ArrayList<MatchingIndexes>( columnIndexPositionPairList.size() );
      
      //
      for ( IndexPositionPair iColumnIndexPair : columnIndexPositionPairList )
      {
        //
        MatchingIndexes matchingIndexes = new MatchingIndexes();
        
        //
        int currentTableColumnIndexPosition = iColumnIndexPair.getCurrentTableIndexPosition();
        int joinTableColumnIndexPosition = iColumnIndexPair.getJoinTableIndexPosition();
        
        //
        E element = this.getCell( iRowIndexPosition, currentTableColumnIndexPosition );
        
        //
        List<Integer> destinationIndexPositionList = matchingIndexes.getDestinationIndexPositionList();
        int[] matchingJoinTableRowIndexPositions = joinTable.indexesOfRowsWithElementsEquals( joinTableColumnIndexPosition,
                                                                                              element );
        
        if ( matchingJoinTableRowIndexPositions != null )
        {
          for ( int iMatchingJoinTableRowIndexPosition : matchingJoinTableRowIndexPositions )
          {
            destinationIndexPositionList.add( iMatchingJoinTableRowIndexPosition );
          }
        }
        
        //
        matchingIndexes.setIndexPositionPair( iColumnIndexPair );
        matchingIndexes.setSourceIndexPosition( iRowIndexPosition );
        
        //
        matchingIndexesList.add( matchingIndexes );
        
      }
      
      //merge the indexlists
      MatchingIndexes mergedMatchingIndexes = null;
      for ( MatchingIndexes iMatchingIndexes : matchingIndexesList )
      {
        if ( mergedMatchingIndexes != null )
        {
          mergedMatchingIndexes = mergedMatchingIndexes.disjunction( iMatchingIndexes );
        }
        else
        {
          mergedMatchingIndexes = iMatchingIndexes;
        }
      }
      
      if ( mergedMatchingIndexes.getDestinationIndexPositionList().size() > 0 )
      {
        //take the subtables of both tables which are defined by the rows 
        //and merge them with the source table on the left
        Table<E> leftTable = this.getSubTableByRows( mergedMatchingIndexes.getSourceIndexPosition(),
                                                     mergedMatchingIndexes.getSourceIndexPosition() );
        Table<E> rightTable = joinTable.getSubTableByRows( mergedMatchingIndexes.getDestinationIndexPositionList() );
        while ( leftTable.getTableSize().getRowSize() < rightTable.getTableSize().getRowSize() )
        {
          leftTable.addRow( leftTable.getRow( 0 ) );
        }
        
        //
        int rowInsertIndexPosition = rettable.getTableSize().getRowSize();
        rettable.putTable( leftTable, rowInsertIndexPosition, 0 );
        rettable.putTable( rightTable, rowInsertIndexPosition, leftTable.getTableSize().getColumnSize() );
      }
    }
    
    //
    return rettable;
  }
  
  /**
   * Returns an array of all index positions from the table available reduced by the given index positions.
   * 
   * @param rowIndexPositions
   * @return
   */
  protected int[] determineInvertedRowIndexPositions( int[] rowIndexPositions )
  {
    //
    int[] retvals = null;
    
    //
    retvals = this.determineAllRowIndexPositions();
    retvals = this.determineSubtractedIndexPositions( retvals, rowIndexPositions );
    //
    return retvals;
  }
  
  /**
   * Returns all row index positions as array.
   * 
   * @return
   */
  protected int[] determineAllRowIndexPositions()
  {
    //
    int[] retvals = null;
    
    //
    if ( this.rowList.size() > 0 )
    {
      retvals = new int[this.rowList.size()];
      for ( int ii = 0; ii < this.rowList.size(); ii++ )
      {
        retvals[ii] = ii;
      }
    }
    //
    return retvals;
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
  
  /**
   * Inserts empty rows at the given position and moves existing rows behind the new created rows.
   * 
   * @param rowIndexPosition
   * @param numberOfRows
   */
  private void insertEmptyRows( int rowIndexPosition, int numberOfRows )
  {
    for ( int ii = this.rowList.size() - 1 + numberOfRows; ii >= rowIndexPosition + numberOfRows; ii-- )
    {
      this.setRow( ii, this.rowList.get( ii - numberOfRows ) );
    }
    for ( int ii = 0; ii < numberOfRows; ii++ )
    {
      this.setRow( ii + rowIndexPosition, this.createNewEmptyRow() );
    }
  }
  
  public Table<E> insertArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    //
    this.insertEmptyRows( rowIndexPosition, elementArray.length );
    //
    this.putArray( elementArray, rowIndexPosition, columnIndexPosition );
    
    //
    return this;
  }
  
  public Table<E> insertTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition )
  {
    //
    this.insertEmptyRows( rowIndexPosition, insertIndexedTable.getTableSize().getRowSize() );
    
    //
    this.putTable( insertIndexedTable, rowIndexPosition, columnIndexPosition );
    
    //
    return this;
  }
  
  public Table<E> putTable( Table<E> insertTable, int rowIndexPosition, int columnIndexPosition )
  {
    if ( insertTable != null )
    {
      //copy the foreign table to the current table cell for cell
      for ( int ii = 0; ii < insertTable.getTableSize().getRowSize(); ii++ )
      {
        for ( int jj = 0; jj < insertTable.getTableSize().getColumnSize(); jj++ )
        {
          this.setCell( ii + rowIndexPosition, jj + columnIndexPosition, insertTable.getCell( ii, jj ) );
        }
      }
      
      //copy titles for rows and columns if necessary
      for ( int ii = 0; ii < insertTable.getTableSize().getRowSize(); ii++ )
      {
        if ( this.getRowTitle( ii + rowIndexPosition ) == null && insertTable.getRowTitle( ii ) != null )
        {
          this.setRowTitle( insertTable.getRowTitle( ii ), ii + rowIndexPosition );
        }
      }
      for ( int jj = 0; jj < insertTable.getTableSize().getColumnSize(); jj++ )
      {
        if ( this.getColumnTitle( jj + columnIndexPosition ) == null && insertTable.getColumnTitle( jj ) != null )
        {
          this.setColumnTitle( insertTable.getColumnTitle( jj ), jj + columnIndexPosition );
        }
      }
    }
    //
    return this;
  }
  
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    //
    for ( int ii = 0; ii < elementArray.length; ii++ )
    {
      for ( int jj = 0; jj < elementArray[ii].length; jj++ )
      {
        this.setCell( rowIndexPosition + ii, columnIndexPosition + jj, elementArray[ii][jj] );
      }
    }
    
    //
    return this;
  }
  
  public Table<E> transpose()
  {
    //
    this.tableHeader.swapColumnAndRowTitles();
    
    //
    List<List<E>> tempList = this.columnList;
    this.columnList = this.rowList;
    this.rowList = tempList;
    
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
  
  public boolean equals( Table<E> table )
  {
    //
    boolean retval = table != null;
    
    //
    if ( retval )
    {
      //size compare
      retval &= this.getTableSize().getColumnSize() == table.getTableSize().getColumnSize();
      retval &= this.getTableSize().getRowSize() == table.getTableSize().getRowSize();
    }
    
    //
    if ( retval )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        for ( int columnIndexPosition = 0; columnIndexPosition < this.getTableSize().getColumnSize(); columnIndexPosition++ )
        {
          E cell1 = this.getCell( rowIndexPosition, columnIndexPosition );
          E cell2 = table.getCell( rowIndexPosition, columnIndexPosition );
          retval &= this.areCellsEqual( cell1, cell2 );
          
          //
          if ( !retval )
          {
            break;
          }
        }
        
        //
        if ( !retval )
        {
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  public Table<E> setRowTitles( Enum<?>[] rowTitleEnums )
  {
    //
    List<String> titleList = new ArrayList<String>( 0 );
    if ( rowTitleEnums != null )
    {
      for ( Enum<?> iTitleEnum : rowTitleEnums )
      {
        String iTitle = iTitleEnum.toString();
        titleList.add( iTitle );
      }
      this.setRowTitles( titleList );
    }
    //
    return this;
  }
  
  public Table<E> setRowTitles( String[] titles )
  {
    //
    List<String> titleList = new ArrayList<String>( 0 );
    Collections.addAll( titleList, titles );
    
    //
    this.setRowTitles( titleList );
    
    //
    return this;
  }
  
  public Table<E> setRowTitle( String title, int rowIndexPosition )
  {
    //
    List<String> rowTitleList = this.getRowTitleList();
    
    while ( rowIndexPosition >= rowTitleList.size() )
    {
      rowTitleList.add( null );
    }
    
    rowTitleList.set( rowIndexPosition, title );
    
    //
    return this;
  }
  
  public Table<E> setRowTitles( List<String> titleList )
  {
    //create a new list, its not defined which kind of list is passed as parameter
    List<String> rowTitleList = new ArrayList<String>( 0 );
    rowTitleList.addAll( titleList );
    
    //
    this.tableHeader.setRowTitles( rowTitleList );
    
    //
    return this;
  }
  
  public Table<E> setColumnTitles( Enum<?>[] titleEnumerations )
  {
    //
    List<String> titleList = new ArrayList<String>( 0 );
    if ( titleEnumerations != null )
    {
      for ( Enum<?> iTitleEnum : titleEnumerations )
      {
        String iTitle = iTitleEnum.toString();
        titleList.add( iTitle );
      }
      this.setColumnTitles( titleList );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnTitles( String... titles )
  {
    //
    List<String> columnTitleList = new ArrayList<String>( 0 );
    Collections.addAll( columnTitleList, titles );
    
    //
    this.setColumnTitles( columnTitleList );
    
    //
    return this;
  }
  
  public Table<E> setColumnTitles( Class<?> beanClass )
  {
    //
    BeanProperty[] beanProperties = BeanUtil.determineBeanProperties( beanClass );
    
    List<String> columnNameList = new ArrayList<String>( 0 );
    for ( BeanProperty iBeanProperty : beanProperties )
    {
      if ( iBeanProperty.isReadable() )
      {
        columnNameList.add( iBeanProperty.getPropertyName() );
      }
    }
    
    //
    this.setColumnTitles( columnNameList );
    
    //
    return this;
  }
  
  public Table<E> setColumnTitle( String title, int columnIndexPosition )
  {
    //
    List<String> columnTitleList = this.getColumnTitleList();
    while ( columnIndexPosition >= columnTitleList.size() )
    {
      columnTitleList.add( null );
    }
    columnTitleList.set( columnIndexPosition, title );
    
    //
    return this;
  }
  
  public Table<E> setColumnTitles( List<String> titleList )
  {
    //create a new list, its not defined which kind of list is passed as parameter
    List<String> columnTitleList = new ArrayList<String>( 0 );
    columnTitleList.addAll( titleList );
    
    //
    this.tableHeader.setColumnTitles( columnTitleList );
    
    //
    return this;
  }
  
  public List<String> getRowTitleList()
  {
    return this.tableHeader.getRowTitles();
  }
  
  public String getRowTitle( int rowIndexPosition )
  {
    if ( rowIndexPosition < this.getRowTitleList().size() && rowIndexPosition >= 0 )
    {
      return this.getRowTitleList().get( rowIndexPosition );
    }
    else
    {
      return null;
    }
  }
  
  @Override
  public List<String> getColumnTitleList()
  {
    return this.tableHeader.getColumnTitles();
  }
  
  @Override
  public String[] getColumnTitles()
  {
    return this.getColumnTitleList().toArray( new String[0] );
  }
  
  @Override
  public String getColumnTitle( int columnIndexPosition )
  {
    if ( columnIndexPosition < this.getColumnTitleList().size() && columnIndexPosition >= 0 )
    {
      return this.getColumnTitleList().get( columnIndexPosition );
    }
    else
    {
      return null;
    }
  }
  
  @Override
  public TableSize getTableSize()
  {
    return new TableSizeImpl();
  }
  
  public Table<E> setCell( int rowIndexPosition, int columnIndexPosition, E element )
  {
    if ( rowIndexPosition >= 0 && columnIndexPosition >= 0 )
    {
      //adapt table dimension if necessary
      this.expandTableBoundariesToGivenIndexPositionsIfNecessary( rowIndexPosition, columnIndexPosition );
      
      //
      List<E> column = this.columnList.get( columnIndexPosition );
      List<E> row = this.rowList.get( rowIndexPosition );
      
      column.set( rowIndexPosition, element );
      row.set( columnIndexPosition, element );
    }
    
    //
    return this;
  }
  
  public Table<E> setRow( int rowIndexPosition, List<E> row )
  {
    //
    for ( int ii = 0; ii < row.size(); ii++ )
    {
      this.setCell( rowIndexPosition, ii, row.get( ii ) );
    }
    
    //
    return this;
  }
  
  public Table<E> setRow( int rowIndexPosition, Object beanObject )
  {
    //
    List<E> row = this.determineRowFromJavaBean( beanObject );
    
    //
    this.setRow( rowIndexPosition, row );
    
    //
    return this;
  }
  
  public Table<E> setColumn( int columnIndexPosition, List<E> column )
  {
    //
    for ( int ii = 0; ii < column.size(); ii++ )
    {
      this.setCell( ii, columnIndexPosition, column.get( ii ) );
    }
    
    //
    return this;
  }
  
  public E getCell( int rowIndexPosition, int columnIndexPosition )
  {
    //
    E retval = null;
    
    //
    if ( this.isRowAndColumnInTableBoundary( rowIndexPosition, columnIndexPosition ) )
    {
      List<E> row = this.rowList.get( rowIndexPosition );
      retval = row.get( columnIndexPosition );
    }
    
    //
    return retval;
  }
  
  public E getCell( String rowTitle, String columnTitle )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitle ),
                         this.tableHeader.getColumnTitles().indexOf( columnTitle ) );
  }
  
  public E getCell( String rowTitle, int columnIndexPosition )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitle ), columnIndexPosition );
  }
  
  public E getCell( int rowIndexPosition, String columnTitle )
  {
    return this.getCell( rowIndexPosition, this.tableHeader.getColumnTitles().indexOf( columnTitle ) );
  }
  
  public E getCell( Enum<?> rowTitleEnum, Enum<?> columnTitleEnum )
  {
    return this.getCell( rowTitleEnum.name(), columnTitleEnum.name() );
  }
  
  public E getCell( Enum<?> rowTitleEnum, int columnIndexPosition )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitleEnum.name() ), columnIndexPosition );
  }
  
  public E getCell( int rowIndexPosition, Enum<?> columnTitleEnumeration )
  {
    return this.getCell( rowIndexPosition, this.tableHeader.getColumnTitles().indexOf( columnTitleEnumeration.name() ) );
  }
  
  /**
   * Looks, if the given index positions are within the table row and column boundaries.
   * 
   * @see #isColumnInTableBoundary(int)
   * @see #isRowInTableBoundary(int)
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  protected boolean isRowAndColumnInTableBoundary( int rowIndexPosition, int columnIndexPosition )
  {
    return this.isRowInTableBoundary( rowIndexPosition ) && this.isColumnInTableBoundary( columnIndexPosition );
  }
  
  /**
   * @see #isRowAndColumnInTableBoundary(int, int)
   * @see #isColumnInTableBoundary(int)
   * @param rowIndexPosition
   * @return
   */
  protected boolean isRowInTableBoundary( int rowIndexPosition )
  {
    return rowIndexPosition >= 0 && rowIndexPosition < this.rowList.size();
  }
  
  /**
   * @see #isRowAndColumnInTableBoundary(int, int)
   * @see #isRowInTableBoundary(int)
   * @param columnIndexPosition
   * @return
   */
  protected boolean isColumnInTableBoundary( int columnIndexPosition )
  {
    return columnIndexPosition >= 0 && columnIndexPosition < this.columnList.size();
  }
  
  /**
   * Expands the table, until it has enough rows and columns to have a table cell with the given index positions.
   * 
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  protected boolean expandTableBoundariesToGivenIndexPositionsIfNecessary( int rowIndexPosition, int columnIndexPosition )
  {
    //
    boolean retval = false;
    
    //
    while ( this.columnList.size() <= columnIndexPosition )
    {
      this.columnList.add( this.createNewEmptyColumn() );
      for ( List<E> iRow : this.rowList )
      {
        iRow.add( null );
      }
      retval = true;
    }
    while ( this.rowList.size() <= rowIndexPosition )
    {
      this.rowList.add( this.createNewEmptyRow() );
      for ( List<E> iColumn : this.columnList )
      {
        iColumn.add( null );
      }
      retval = true;
    }
    
    //
    return retval;
  }
  
  private List<E> createNewEmptyColumn()
  {
    return this.createNewEmptyRowOrColumn( this.rowList.size() );
  }
  
  private List<E> createNewEmptyRow()
  {
    return this.createNewEmptyRowOrColumn( this.columnList.size() );
  }
  
  private List<E> createNewEmptyRowOrColumn( int listSize )
  {
    List<E> retval = new ArrayList<E>( listSize );
    for ( int ii = 0; ii < listSize; ii++ )
    {
      retval.add( null );
    }
    return retval;
  }
  
  public Table<E> addColumn( List<E> column )
  {
    //adapt table dimension
    this.expandTableBoundariesToGivenIndexPositionsIfNecessary( column.size() - 1, this.columnList.size() );
    
    //put the elements of the new column to the table
    int rowIndexPosition = 0;
    int columnIndexPosition = this.columnList.size() - 1;
    for ( E iElement : column )
    {
      this.setCell( rowIndexPosition++, columnIndexPosition, iElement );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRow( List<E> row )
  {
    //adapt table dimension
    this.expandTableBoundariesToGivenIndexPositionsIfNecessary( this.rowList.size(), row.size() - 1 );
    
    //put the elements of the new row to the table
    int rowIndexPosition = this.rowList.size() - 1;
    int columnIndexPosition = 0;
    for ( E iElement : row )
    {
      this.setCell( rowIndexPosition, columnIndexPosition++, iElement );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRow( int rowIndexPosition, List<E> row )
  {
    if ( ( this.isRowInTableBoundary( rowIndexPosition ) || rowIndexPosition == this.getTableSize().getRowSize() ) && row != null
         && row.size() > 0 )
    {
      //
      row = new ArrayList<E>( row );
      
      //adapt column width
      this.expandTableBoundariesToGivenIndexPositionsIfNecessary( -1, row.size() - 1 );
      while ( row.size() < this.getTableSize().getColumnSize() )
      {
        row.add( null );
      }
      
      //title
      if ( rowIndexPosition <= this.getRowTitleList().size() )
      {
        this.tableHeader.addRowTitle( rowIndexPosition, "" );
      }
      
      //push down the rows
      this.rowList.add( rowIndexPosition, row );
      
      //push down the elements in the column lists
      int columnIndexPosition = 0;
      for ( List<E> iColumnList : this.columnList )
      {
        iColumnList.add( rowIndexPosition, row.get( columnIndexPosition++ ) );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Adds the content of a java bean object into the table. Important is that the table column titles have the same name as the
   * properties of the java bean. Properties with different names will be ignored.
   */
  public Table<E> addRow( Object beanObject )
  {
    //titles
    if ( !this.tableHeader.hasColumnTitles() )
    {
      this.setColumnTitles( beanObject.getClass() );
    }
    
    //
    List<E> row = this.determineRowFromJavaBean( beanObject );
    
    //
    this.addRow( row );
    
    //
    return this;
  }
  
  @SuppressWarnings("unchecked")
  protected List<E> determineRowFromJavaBean( Object beanObject )
  {
    //
    List<E> row = new ArrayList<E>( 0 );
    
    //
    BeanProperty[] beanObjectProperties = BeanUtil.determineBeanProperties( beanObject );
    if ( this.tableHeader.hasColumnTitles() )
    {
      for ( int ii = 0; ii < this.tableHeader.columnTitles.size(); ii++ )
      {
        row.add( null );
      }
      
      for ( BeanProperty iBeanProperty : beanObjectProperties )
      {
        if ( iBeanProperty.isReadable() && this.tableHeader.getColumnTitles().contains( iBeanProperty.getPropertyName() ) )
        {
          int columnIndexPosition = this.tableHeader.indexOfColumnTitle( iBeanProperty.getPropertyName() );
          if ( columnIndexPosition >= 0 )
          {
            row.set( columnIndexPosition,
                     (E) BeanUtil.invokeJavaBeanPropertyMethod( beanObject, iBeanProperty.getGetterMethodName(), null ) );
          }
        }
      }
    }
    
    //
    return row;
  }
  
  public List<E> removeRow( int rowIndexPosition )
  {
    //
    List<E> retval = null;
    
    if ( this.isRowAndColumnInTableBoundary( rowIndexPosition, 0 ) )
    {
      //data
      retval = this.rowList.remove( rowIndexPosition );
      
      for ( List<E> iColumn : this.columnList )
      {
        iColumn.remove( rowIndexPosition );
      }
      
      //title of the row
      this.tableHeader.removeRowTitle( rowIndexPosition );
    }
    
    //
    return retval;
  }
  
  public List<E> removeColumn( int columnIndexPosition )
  {
    //
    List<E> retval = null;
    
    if ( this.isRowAndColumnInTableBoundary( columnIndexPosition, 0 ) )
    {
      //data
      retval = this.columnList.remove( columnIndexPosition );
      
      for ( List<E> iRow : this.rowList )
      {
        iRow.remove( columnIndexPosition );
      }
      
      //title of the row
      this.tableHeader.removeColumnTitle( columnIndexPosition );
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  public List<E>[] removeRows( int[] rowIndexPositions )
  {
    //
    List<E>[] retvals = null;
    
    //we need a descending ordered list, to remove the elements from back to beginning.
    List<Integer> rowIndexPositionList = new ArrayList<Integer>( rowIndexPositions.length );
    CollectionUtils.addAll( rowIndexPositionList, rowIndexPositions );
    Collections.sort( rowIndexPositionList, new Comparator<Integer>()
    {
      @Override
      public int compare( Integer o1, Integer o2 )
      {
        return o2.compareTo( o1 );
      }
    } );
    
    //
    List<List<E>> removedRowList = new ArrayList<List<E>>( 0 );
    for ( Integer iRowIndexPosition : rowIndexPositionList )
    {
      List<E> removedRow = this.removeRow( iRowIndexPosition );
      if ( removedRow != null )
      {
        removedRowList.add( removedRow );
      }
    }
    
    if ( removedRowList.size() > 0 )
    {
      retvals = new List[removedRowList.size()];
      retvals = removedRowList.toArray( retvals );
    }
    
    //
    return retvals;
  }
  
  public Row<E> getRow( int rowIndexPosition )
  {
    //
    Row<E> retlist = new ArrayTableRow<E>( this.columnList.size() );
    
    //
    for ( E iElement : this.rowList.get( rowIndexPosition ) )
    {
      retlist.add( iElement );
    }
    
    //
    return retlist;
  }
  
  public <C> C getRow( int rowIndexPosition, C beanObject )
  {
    //
    C retval = beanObject;
    
    //
    BeanProperty[] beanObjectProperties = BeanUtil.determineBeanProperties( beanObject );
    for ( BeanProperty iBeanProperty : beanObjectProperties )
    {
      if ( iBeanProperty.isWritable() )
      {
        String propertyName = iBeanProperty.getPropertyName();
        if ( this.tableHeader.columnTitles.contains( propertyName ) )
        {
          //
          int columnIndexPosition = this.tableHeader.columnTitles.indexOf( propertyName );
          E element = this.getCell( rowIndexPosition, columnIndexPosition );
          
          //
          BeanUtil.invokeJavaBeanPropertyMethod( beanObject, iBeanProperty.getSetterMethodName(), element );
          
        }
      }
    }
    
    //
    return retval;
  }
  
  public Row<E> getRow( String rowTitle )
  {
    //
    Row<E> retlist = null;
    
    //
    int rowIndexPosition = this.tableHeader.getRowTitles().indexOf( rowTitle );
    
    if ( rowIndexPosition >= 0 )
    {
      retlist = this.getRow( rowIndexPosition );
    }
    
    //
    return retlist;
  }
  
  public Row<E> getRow( Enum<?> rowTitleEnum )
  {
    //
    Row<E> retlist = null;
    
    //
    String rowTitle = rowTitleEnum.name();
    retlist = this.getRow( rowTitle );
    
    //
    return retlist;
  }
  
  public List<E> getColumn( int columnIndexPosition )
  {
    //
    List<E> retlist = new ArrayList<E>( this.rowList.size() );
    
    //
    for ( E iElement : this.columnList.get( columnIndexPosition ) )
    {
      retlist.add( iElement );
    }
    
    //
    return retlist;
  }
  
  @Override
  public boolean containsRow( List<E> row )
  {
    return this.indexOfRow( row ) >= 0;
  }
  
  @Override
  public int indexOfRow( List<E> row )
  {
    //
    int retval = -1;
    
    //
    if ( row != null )
    {
      Integer[] rowIndexPositions = this.indexesOfRowsWhereRowsEquals( row, true );
      if ( rowIndexPositions.length > 0 )
      {
        retval = rowIndexPositions[0];
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public int lastIndexOfRow( List<E> row )
  {
    //
    int retval = -1;
    
    //
    if ( row != null )
    {
      Integer[] rowIndexPositions = this.indexesOfRowsWhereRowsEquals( row, false );
      if ( rowIndexPositions.length > 0 )
      {
        retval = rowIndexPositions[rowIndexPositions.length - 1];
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public RowList<E> getRowList()
  {
    return new TableRowList<E>( this );
  }
  
  @Override
  public List<E> getCellList()
  {
    return new TableCellList<E>( this );
  }
  
  /**
   * Implementation of a list operating on the cells of a table not considering columns and rows.
   * 
   * @author Omnaest
   */
  private static class TableCellList<E> extends ListAbstract<E> implements List<E>, Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 8081399996606545974L;
    
    /* ********************************************** Variables ********************************************** */
    private Table<E>          table            = null;
    
    /* ********************************************** Methods ********************************************** */
    public TableCellList( Table<E> table )
    {
      this.table = table;
    }
    
    private boolean isTableNotNull()
    {
      if ( this.table == null )
      {
        throw new NullPointerException( "Table row has no underlying table." );
      }
      return true;
    }
    
    @Override
    public boolean add( E e )
    {
      throw new UnsupportedOperationException( "This list cannot add new elements." );
    }
    
    @Override
    public void add( int index, E element )
    {
      throw new UnsupportedOperationException( "This list cannot add new elements." );
    }
    
    @Override
    public void clear()
    {
      if ( this.isTableNotNull() )
      {
        this.table.clear();
      }
    }
    
    @Override
    public E get( int index )
    {
      //
      E retval = null;
      
      //
      if ( this.isTableNotNull() && this.isValidIndex( index ) )
      {
        retval = this.table.getCell( index );
      }
      
      //
      return retval;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int indexOf( Object o )
    {
      //
      int retval = -1;
      
      //
      if ( this.isTableNotNull() )
      {
        try
        {
          E element = (E) o;
          retval = this.table.indexOf( element ).getCellIndexPosition();
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      return retval;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int lastIndexOf( Object o )
    {
      //
      int retval = -1;
      
      //
      if ( this.isTableNotNull() )
      {
        try
        {
          E element = (E) o;
          retval = this.table.lastIndexOf( element ).getCellIndexPosition();
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public E remove( int index )
    {
      return this.set( index, null );
    }
    
    @Override
    public E set( int index, E element )
    {
      //
      E retval = null;
      
      //
      if ( this.isTableNotNull() && this.isValidIndex( index ) )
      {
        this.table.setCell( index, element );
      }
      
      //
      return retval;
    }
    
    @Override
    public int size()
    {
      return this.isTableNotNull() ? this.table.getTableSize().getCellSize() : 0;
    }
    
  }
  
  /**
   * Adpater class to provide add,addAll,set for simple List<E> objects, too.
   * 
   * @author Omnaest
   * @param <E>
   */
  @SuppressWarnings("hiding")
  private abstract class ListToRowListAdapter<E> implements RowList<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 3847114548939619054L;
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public void add( int index, List<E> element )
    {
      this.add( new ArrayTableRow<E>( element ) );
    }
    
    @Override
    public boolean add( List<E> e )
    {
      return this.add( new ArrayTableRow<E>( e ) );
    }
    
    @Override
    public List<E> set( int index, List<E> element )
    {
      return this.set( index, new ArrayTableRow<E>( element ) );
    }
    
    @Override
    public boolean addAll( int index, List<? extends List<E>> listList )
    {
      return this.addAll( index, this.convertListListToRowCollection( listList ) );
    }
    
    private Collection<Row<E>> convertListListToRowCollection( List<? extends List<E>> listList )
    {
      List<Row<E>> rowList = new ArrayList<Row<E>>();
      for ( List<E> row : listList )
      {
        rowList.add( new ArrayTableRow<E>( row ) );
      }
      return rowList;
    }
    
    @Override
    public boolean addAll( List<? extends List<E>> listList )
    {
      return this.addAll( this.convertListListToRowCollection( listList ) );
    }
    
  }
  
  /**
   * Implementation of a table row list
   * 
   * @author Omnaest
   */
  @SuppressWarnings("hiding")
  private class TableRowList<E> extends ListToRowListAdapter<E> implements RowList<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -7391298856803654643L;
    
    /* ********************************************** Variables ********************************************** */
    private Table<E>          table            = null;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * Creates a new instance with the given table as the underlying table of the list.
     */
    public TableRowList( Table<E> table )
    {
      this.table = table;
    }
    
    private boolean isTableNotNull()
    {
      if ( this.table == null )
      {
        throw new NullPointerException( "Table row has no underlying table." );
      }
      return true;
    }
    
    @Override
    public boolean add( Row<E> row )
    {
      //
      if ( this.isTableNotNull() && row != null )
      {
        this.table.addRow( row );
      }
      
      //
      return true;
    }
    
    @Override
    public void add( int index, Row<E> row )
    {
      //
      if ( row != null && index >= 0 && this.isTableNotNull() && this.table.getTableSize().getRowSize() >= index )
      {
        this.table.addRow( index, row );
      }
    }
    
    @Override
    public boolean addAll( Collection<? extends Row<E>> rowCollection )
    {
      //
      boolean retval = true;
      
      //
      if ( rowCollection != null )
      {
        for ( Row<E> iRow : rowCollection )
        {
          if ( iRow != null )
          {
            retval &= this.add( iRow );
          }
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public boolean addAll( int index, Collection<? extends Row<E>> rowCollection )
    {
      //
      boolean retval = false;
      
      //
      if ( rowCollection != null )
      {
        for ( Row<E> iRow : rowCollection )
        {
          if ( iRow != null )
          {
            this.add( index++, iRow );
            retval = true;
          }
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public void clear()
    {
      if ( this.isTableNotNull() )
      {
        this.table.clear();
      }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains( Object row )
    {
      if ( row != null && row instanceof List && ( (List<E>) row ).size() > 0 )
      {
        return this.table.containsRow( (List<E>) row );
      }
      else
      {
        return false;
      }
    }
    
    @Override
    public boolean containsAll( Collection<?> rowCollection )
    {
      //
      boolean retval = true;
      
      //
      if ( rowCollection != null )
      {
        for ( Object iObject : rowCollection )
        {
          retval &= this.contains( iObject );
          
          if ( !retval )
          {
            break;
          }
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public Row<E> get( int index )
    {
      //
      Row<E> row = null;
      
      //
      if ( this.isTableNotNull() && this.isValidIndex( index ) )
      {
        row = this.table.getRow( index );
      }
      
      //
      return row;
    }
    
    private boolean isValidIndex( int index )
    {
      return index >= 0 && index < this.size();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int indexOf( Object row )
    {
      //
      int retval = -1;
      
      //
      if ( row != null && ( row instanceof List ) && this.isTableNotNull() )
      {
        retval = this.table.indexOfRow( (List<E>) row );
      }
      
      //
      return retval;
    }
    
    @Override
    public boolean isEmpty()
    {
      return this.size() == 0;
    }
    
    @Override
    public Iterator<Row<E>> iterator()
    {
      return this.listIterator();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int lastIndexOf( Object row )
    {
      //
      int retval = -1;
      
      //
      if ( row != null && row instanceof List && this.isTableNotNull() )
      {
        retval = this.table.lastIndexOfRow( (List<E>) row );
      }
      //
      return retval;
    }
    
    @Override
    public ListIterator<Row<E>> listIterator()
    {
      return new ListToListIteratorAdapter<Row<E>>( this );
    }
    
    @Override
    public ListIterator<Row<E>> listIterator( int index )
    {
      return new ListToListIteratorAdapter<Row<E>>( this, index );
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove( Object row )
    {
      //
      boolean retval = false;
      
      //
      if ( row != null && row instanceof List && this.isTableNotNull() )
      {
        int rowIndexPosition = this.table.indexOfRow( (List<E>) row );
        if ( rowIndexPosition >= 0 )
        {
          retval = this.table.removeRow( rowIndexPosition ) != null;
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public Row<E> remove( int index )
    {
      //
      Row<E> row = null;
      
      //
      if ( this.isValidIndex( index ) && this.isTableNotNull() )
      {
        this.table.removeRow( index );
      }
      
      //
      return row;
    }
    
    @Override
    public boolean removeAll( Collection<?> rowCollection )
    {
      //
      boolean retval = true;
      
      //
      if ( rowCollection != null && this.isTableNotNull() )
      {
        for ( Object iRow : rowCollection )
        {
          if ( iRow != null && iRow instanceof List )
          {
            retval &= this.remove( iRow );
          }
        }
      }
      
      //
      return retval;
    }
    
    @Override
    public boolean retainAll( Collection<?> rowCollection )
    {
      //
      boolean retval = false;
      
      //
      if ( rowCollection != null && this.isTableNotNull() )
      {
        //
        List<List<E>> removeRowList = new ArrayList<List<E>>();
        for ( List<E> iRow : this )
        {
          if ( !rowCollection.contains( iRow ) )
          {
            //
            removeRowList.add( iRow );
            
            //
            retval = true;
          }
        }
        
        //
        this.removeAll( removeRowList );
      }
      
      //
      return retval;
    }
    
    @Override
    public Row<E> set( int index, Row<E> row )
    {
      //
      Row<E> retlist = null;
      
      //
      if ( this.isValidIndex( index ) && row != null )
      {
        retlist = this.table.getRow( index );
        this.table.setRow( index, row );
      }
      
      //
      return retlist;
    }
    
    @Override
    public int size()
    {
      //
      int retval = 0;
      
      //
      if ( this.isTableNotNull() )
      {
        retval = this.table.getTableSize().getRowSize();
      }
      
      //
      return retval;
    }
    
    @Override
    public List<Row<E>> subList( int fromIndex, int toIndex )
    {
      throw new UnsupportedOperationException( "Subtables are not allowed." );
    }
    
    @Override
    public Object[] toArray()
    {
      //
      Object[] retvals = null;
      
      //
      if ( this.isTableNotNull() )
      {
        retvals = new Object[this.size()];
        
        for ( int rowIndexPosition = 0; rowIndexPosition < this.size(); rowIndexPosition++ )
        {
          List<E> iRow = this.get( rowIndexPosition );
          retvals[rowIndexPosition] = iRow;
        }
      }
      
      //
      return retvals;
    }
    
    @Override
    public <T> T[] toArray( T[] a )
    {
      throw new UnsupportedOperationException( "This list can not be converted to an array." );
    }
    
  }
  
  public List<E> getColumn( Enum<?> columnTitleEnum )
  {
    //
    List<E> retlist = null;
    
    //
    String columnTitle = columnTitleEnum.name();
    retlist = this.getColumn( columnTitle );
    
    //
    return retlist;
  }
  
  public List<E> getColumn( String columnTitle )
  {
    //
    List<E> retlist = null;
    
    //
    int columnIndexPosition = this.tableHeader.getColumnTitles().indexOf( columnTitle );
    if ( columnIndexPosition >= 0 )
    {
      retlist = this.getColumn( columnIndexPosition );
    }
    
    //
    return retlist;
  }
  
  public int determineColumnIndexPosition( Enum<?> columnTitleEnum )
  {
    return this.tableHeader.getColumnTitles().indexOf( columnTitleEnum.name() );
  }
  
  public int determineColumnIndexPosition( String columnTitle )
  {
    return this.tableHeader.getColumnTitles().indexOf( columnTitle );
  }
  
  public int determineRowIndexPosition( Enum<?> rowTitleEnum )
  {
    return this.tableHeader.getRowTitles().indexOf( rowTitleEnum.name() );
  }
  
  public int determineRowIndexPosition( String rowTitle )
  {
    return this.tableHeader.getRowTitles().indexOf( rowTitle );
  }
  
  /**
   * Determines the cell index position of a given row and column index position. Cells are counted from left to right of a row
   * and down all rows. Cell positions are beginning with index = 0, but if no valid row and column possition is given, -1 is
   * returned.
   * 
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  protected CellIndexPosition determineCellIndexPosition( int rowIndexPosition, int columnIndexPosition )
  {
    //
    CellIndexPosition retval = new CellIndexPositionImpl();
    
    //
    int cellIndexPosition = -1;
    if ( rowIndexPosition >= 0 && columnIndexPosition >= 0 )
    {
      cellIndexPosition = rowIndexPosition * this.getTableSize().getColumnSize() + columnIndexPosition;
    }
    
    //
    retval.setCellIndexPosition( cellIndexPosition );
    retval.setColumnIndexPosition( columnIndexPosition );
    retval.setRowIndexPosition( rowIndexPosition );
    
    //
    return retval;
  }
  
  @Override
  public CellIndexPosition lastIndexOf( E element )
  {
    //
    CellIndexPosition retval = new CellIndexPositionImpl();
    
    //    
    for ( int ii = this.rowList.size() - 1; ii >= 0; ii-- )
    {
      int indexOf = this.rowList.get( ii ).lastIndexOf( element );
      if ( indexOf >= 0 )
      {
        retval = this.determineCellIndexPosition( ii, indexOf );
        break;
      }
    }
    
    //
    return retval;
  }
  
  public CellIndexPosition indexOf( E element )
  {
    //
    CellIndexPosition retval = new CellIndexPositionImpl();
    
    //    
    for ( int ii = 0; ii < this.rowList.size(); ii++ )
    {
      int indexOf = this.rowList.get( ii ).indexOf( element );
      if ( indexOf >= 0 )
      {
        retval = this.determineCellIndexPosition( ii, indexOf );
        break;
      }
    }
    
    //
    return retval;
  }
  
  public int indexOfFirstColumnWithElementEquals( int rowIndexPosition, E element )
  {
    List<E> row = this.rowList.get( rowIndexPosition );
    return row.indexOf( element );
  }
  
  public int indexOfFirstRowWithElementEquals( int columnIndexPosition, E element )
  {
    List<E> column = this.columnList.get( columnIndexPosition );
    return column.indexOf( element );
  }
  
  public int[] indexesOfRowsWithElementsEquals( int columnIndexPosition, E element )
  {
    //
    int[] retvals = null;
    
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      //make a full table scan
      List<Integer> indexPositionList = new ArrayList<Integer>( 0 );
      List<E> column = this.columnList.get( columnIndexPosition );
      
      for ( int ii = 0; ii < column.size(); ii++ )
      {
        E columnElement = column.get( ii );
        try
        {
          if ( this.areCellsEqual( element, columnElement ) )
          {
            indexPositionList.add( ii );
          }
        }
        catch ( NullPointerException e )
        {
          if ( element == null && columnElement == null )
          {
            indexPositionList.add( ii );
          }
        }
      }
      
      //
      retvals = new int[indexPositionList.size()];
      {
        int ii = 0;
        for ( Integer iIndexPosition : indexPositionList )
        {
          retvals[ii++] = iIndexPosition;
        }
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Determines all row index positions for the rows of the table matching the given row. The rows have to match in the order of
   * the elements, as well as in the elements.
   * 
   * @param row
   * @param breakAtFirstMatching
   *          : true->only the first row index position is determined. Faster algorithm.
   * @return
   */
  private Integer[] indexesOfRowsWhereRowsEquals( List<E> row, boolean breakAtFirstMatching )
  {
    //
    List<Integer> rowIndexPositionList = new ArrayList<Integer>();
    
    //
    if ( row != null && row.size() > 0 && row.size() == this.getTableSize().getColumnSize() )
    {
      
      for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        List<E> iTableRow = this.rowList.get( rowIndexPosition );
        if ( iTableRow != null && iTableRow.equals( row ) )
        {
          rowIndexPositionList.add( rowIndexPosition );
          if ( breakAtFirstMatching )
          {
            break;
          }
        }
      }
    }
    
    //
    return rowIndexPositionList.toArray( new Integer[rowIndexPositionList.size()] );
  }
  
  public int lastIndexOfElementWithinRow( int rowIndexPosition, E element )
  {
    List<E> row = this.rowList.get( rowIndexPosition );
    return row.lastIndexOf( element );
  }
  
  public Table<E> clear()
  {
    //
    this.columnList.clear();
    this.rowList.clear();
    
    //
    return this;
  }
  
  public int lastIndexOfElementWithinColumn( int columnIndexPosition, E element )
  {
    List<E> column = this.columnList.get( columnIndexPosition );
    return column.lastIndexOf( element );
  }
  
  public Table<E> cloneTableStructure()
  {
    //
    Table<E> rettable = new ArrayTable<E>();
    
    //copy the table name
    rettable.setTableName( this.getTableName() );
    
    //copy the titles
    for ( int ii = 0; ii < this.rowList.size(); ii++ )
    {
      if ( this.getRowTitle( ii ) != null )
      {
        rettable.setRowTitle( this.getRowTitle( ii ), ii );
      }
    }
    for ( int jj = 0; jj < this.columnList.size(); jj++ )
    {
      if ( this.getColumnTitle( jj ) != null )
      {
        rettable.setColumnTitle( this.getColumnTitle( jj ), jj );
      }
    }
    
    //
    return rettable;
  }
  
  public Table<E> clone()
  {
    //
    Table<E> rettable = this.cloneTableStructure();
    
    //copy the data
    for ( int ii = 0; ii < this.rowList.size(); ii++ )
    {
      for ( int jj = 0; jj < this.columnList.size(); jj++ )
      {
        rettable.setCell( ii, jj, this.getCell( ii, jj ) );
      }
    }
    
    //
    return rettable;
  }
  
  @Override
  public String getTableName()
  {
    return this.tableHeader.getTableName();
  }
  
  @Override
  public Table<E> setTableName( String tableName )
  {
    //
    this.tableHeader.setTableName( tableName );
    
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
   * Used within {@link IndexArrayTable#innerJoinByEqualColumn(IndexArrayTable, int[], int[])}
   */
  protected class IndexPositionPairImpl implements IndexPositionPair
  {
    private int currentTableIndexPosition = -1;
    private int joinTableIndexPosition    = -1;
    
    public int getCurrentTableIndexPosition()
    {
      return currentTableIndexPosition;
    }
    
    public void setCurrentTableIndexPosition( int currentTableIndexPosition )
    {
      this.currentTableIndexPosition = currentTableIndexPosition;
    }
    
    public int getJoinTableIndexPosition()
    {
      return joinTableIndexPosition;
    }
    
    public void setJoinTableIndexPosition( int joinTableIndexPosition )
    {
      this.joinTableIndexPosition = joinTableIndexPosition;
    }
    
  }
  
  /**
   * This class holds informations
   * 
   * @author Omnaest
   */
  protected class MatchingIndexes
  {
    private IndexPositionPair indexPositionPair            = null;
    private int               sourceIndexPosition          = -1;
    private List<Integer>     destinationIndexPositionList = new IndexArrayList<Integer>();
    
    public MatchingIndexes disjunction( MatchingIndexes compareMatchingIndexes )
    {
      MatchingIndexes matchingIndexes = new MatchingIndexes();
      matchingIndexes.setSourceIndexPosition( this.getSourceIndexPosition() );
      
      List<Integer> indexPositionList1 = this.getDestinationIndexPositionList();
      List<Integer> indexPositionList2 = compareMatchingIndexes.getDestinationIndexPositionList();
      
      List<Integer> inBothExistingIndexPositionList = matchingIndexes.getDestinationIndexPositionList();
      
      for ( Integer iIndexPosition : indexPositionList1 )
      {
        if ( indexPositionList2.contains( iIndexPosition ) )
        {
          inBothExistingIndexPositionList.add( iIndexPosition );
        }
      }
      
      //
      return matchingIndexes;
    }
    
    public int getSourceIndexPosition()
    {
      return sourceIndexPosition;
    }
    
    public void setSourceIndexPosition( int sourceIndexPosition )
    {
      this.sourceIndexPosition = sourceIndexPosition;
    }
    
    public List<Integer> getDestinationIndexPositionList()
    {
      return destinationIndexPositionList;
    }
    
    public void setDestinationIndexPositionList( List<Integer> destinationIndexPositionList )
    {
      this.destinationIndexPositionList = destinationIndexPositionList;
    }
    
    public IndexPositionPair getIndexPositionPair()
    {
      return this.indexPositionPair;
    }
    
    public void setIndexPositionPair( IndexPositionPair indexPositionPair )
    {
      this.indexPositionPair = indexPositionPair;
    }
  }
  
  /**
   * Holds informations for the table header lines.
   * 
   * @author Omnaest
   */
  protected class TableHeader
  {
    private String tableName    = null;
    List<String>   columnTitles = new ArrayList<String>( 0 );
    List<String>   rowTitles    = new ArrayList<String>( 0 );
    
    /**
     * Adds a new title at the given row index position.
     * 
     * @param rowIndexPosition
     * @param title
     */
    public void addRowTitle( int rowIndexPosition, String title )
    {
      this.rowTitles.add( rowIndexPosition, title );
    }
    
    public void swapColumnAndRowTitles()
    {
      List<String> tempList = this.columnTitles;
      this.columnTitles = this.rowTitles;
      this.rowTitles = tempList;
    }
    
    public int indexOfColumnTitle( String columnTitle )
    {
      return this.columnTitles.indexOf( columnTitle );
    }
    
    public boolean hasColumnTitles()
    {
      return this.columnTitles.size() > 0;
    }
    
    public List<String> getColumnTitles()
    {
      return columnTitles;
    }
    
    public void setColumnTitles( List<String> columnTitles )
    {
      this.columnTitles = columnTitles;
    }
    
    public List<String> getRowTitles()
    {
      return rowTitles;
    }
    
    public void setRowTitles( List<String> rowTitles )
    {
      this.rowTitles = rowTitles;
    }
    
    public void removeRowTitle( int rowIndexPosition )
    {
      if ( rowIndexPosition >= 0 && rowIndexPosition < this.rowTitles.size() )
      {
        this.rowTitles.remove( rowIndexPosition );
      }
    }
    
    public void removeColumnTitle( int columnIndexPosition )
    {
      if ( columnIndexPosition >= 0 && columnIndexPosition < this.columnTitles.size() )
      {
        this.columnTitles.remove( columnIndexPosition );
      }
    }
    
    public TableHeader clone()
    {
      //
      TableHeader retval = new TableHeader();
      
      //
      retval.columnTitles.addAll( this.columnTitles );
      retval.rowTitles.addAll( this.rowTitles );
      
      //
      retval.setTableName( this.tableName );
      
      //
      return retval;
    }
    
    public String getTableName()
    {
      return tableName;
    }
    
    public void setTableName( String tableName )
    {
      this.tableName = tableName;
    }
  }
  
  /**
   * Holds methods to return the current table size for rows and columns. If the data of the underlying table changes, the methods
   * will return the new actual results then, too!
   */
  public class TableSizeImpl implements TableSize
  {
    public int getCellSize()
    {
      return ArrayTable.this.columnList.size() * ArrayTable.this.rowList.size();
    }
    
    public int getRowSize()
    {
      return ArrayTable.this.rowList.size();
    }
    
    public int getColumnSize()
    {
      return ArrayTable.this.columnList.size();
    }
  }
  
  /**
   * Holds the position indexes for a cell.
   * 
   * @author Omnaest
   */
  public class CellIndexPositionImpl implements CellIndexPosition
  {
    /* ********************************************** Variables ********************************************** */
    private int rowIndexPosition    = -1;
    private int columnIndexPosition = -1;
    private int cellIndexPosition   = -1;
    
    /* ********************************************** Methods ********************************************** */

    public int getRowIndexPosition()
    {
      return rowIndexPosition;
    }
    
    public void setRowIndexPosition( int rowIndexPosition )
    {
      this.rowIndexPosition = rowIndexPosition;
    }
    
    public int getColumnIndexPosition()
    {
      return columnIndexPosition;
    }
    
    public void setColumnIndexPosition( int columnIndexPosition )
    {
      this.columnIndexPosition = columnIndexPosition;
    }
    
    public int getCellIndexPosition()
    {
      return this.cellIndexPosition;
    }
    
    public void setCellIndexPosition( int cellIndexPosition )
    {
      this.cellIndexPosition = cellIndexPosition;
    }
  }
  
  @Override
  public Iterator<E> cellIterator()
  {
    return this.cellListIterator();
  }
  
  @Override
  public ListIterator<E> cellListIterator()
  {
    return this.getCellList().listIterator();
  }
  
  @Override
  public E getCell( int cellIndexPosition )
  {
    //
    E cell = null;
    
    //
    if ( cellIndexPosition >= 0 && cellIndexPosition < this.getTableSize().getCellSize() )
    {
      int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
      int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
      
      cell = this.getCell( rowIndexPosition, columnIndexPosition );
    }
    
    //
    return cell;
  }
  
  private int determineRowIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    return cellIndexPosition / this.getTableSize().getColumnSize();
  }
  
  private int determineColumnIndexPositionForCellIndexPosition( int cellIndexPosition )
  {
    return cellIndexPosition % this.getTableSize().getColumnSize();
  }
  
  @Override
  public Table<E> setCell( int cellIndexPosition, E element )
  {
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    return this.setCell( rowIndexPosition, columnIndexPosition, element );
  }
  
  @Override
  public ListIterator<Row<E>> listIterator()
  {
    return this.rowIterator();
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
      public void inspect( int rowIndexPosition, int columnIndexPosition, E cell )
      {
        table.setCell( rowIndexPosition, columnIndexPosition, tableCellConverter.convert( cell ) );
      }
    } );
    
    //
    return table;
  }
  
  @Override
  public Table<E> convertFirstRowToTitle()
  {
    //
    if ( this.getTableSize().getRowSize() > 0 )
    {
      //
      try
      {
        //
        List<String> titleList = new ArrayList<String>();
        for ( E cell : this.getRow( 0 ) )
        {
          titleList.add( String.valueOf( cell ) );
        }
        
        //
        this.setColumnTitles( titleList );
        
        //
        this.removeRow( 0 );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> convertFirstColumnToTitle()
  {
    //
    if ( this.getTableSize().getRowSize() > 0 )
    {
      //
      try
      {
        //
        List<String> titleList = new ArrayList<String>();
        for ( E cell : this.getColumn( 0 ) )
        {
          titleList.add( String.valueOf( cell ) );
        }
        
        //
        this.setRowTitles( titleList );
        
        //
        this.removeColumn( 0 );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return this;
  }
  
  @Override
  public ListIterator<Row<E>> rowIterator()
  {
    return this.rowListIterator();
  }
  
  @Override
  public ListIterator<Row<E>> rowListIterator()
  {
    return new ListToListIteratorAdapter<Row<E>>( this.getRowList() );
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.rowIterator();
  }
  
  @Override
  public boolean contains( E element )
  {
    return element != null && this.indexOf( element ).getCellIndexPosition() >= 0;
  }
  
  /**
   * Implementation of the {@link Row} interface for the array table.
   * 
   * @see Row
   * @author Omnaest
   */
  @SuppressWarnings("hiding")
  public class ArrayTableRow<E> extends ArrayList<E> implements Table.Row<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -9058077903036410564L;
    
    /* ********************************************** Methods ********************************************** */

    public ArrayTableRow()
    {
      super();
    }
    
    public ArrayTableRow( Collection<? extends E> c )
    {
      super( c );
    }
    
    public ArrayTableRow( int initialCapacity )
    {
      super( initialCapacity );
    }
    
    @Override
    public E get( Enum<?> columnTitleEnum )
    {
      int columnIndexPosition = ArrayTable.this.determineColumnIndexPosition( columnTitleEnum );
      return this.get( columnIndexPosition );
    }
    
    @Override
    public E get( String columnTitle )
    {
      int columnIndexPosition = ArrayTable.this.determineColumnIndexPosition( columnTitle );
      return this.get( columnIndexPosition );
    }
    
    @Override
    public <B> B asBean( B beanObject )
    {
      B retval = ArrayTable.this.determineJavaBeanFromRow( beanObject, this );
      return retval;
    }
  }
  
  @Override
  public boolean containsRow( Object beanObject )
  {
    return this.containsRow( this.determineRowFromJavaBean( beanObject ) );
  }
  
  @Override
  public <B> B getRowAsBean( B beanObject, int rowIndexPosition )
  {
    return this.determineJavaBeanFromRow( beanObject, this.getRow( rowIndexPosition ) );
  }
  
  /**
   * Injects the values of the row for the properties of the given java bean, which have the same names as the column titles of a
   * given row.
   * 
   * @param beanObject
   * @param row
   * @return
   */
  protected <B> B determineJavaBeanFromRow( B beanObject, Row<?> row )
  {
    //
    for ( String columnTitle : this.getColumnTitleList() )
    {
      beanObject = BeanUtil.injectValueOfPropertyIntoBean( beanObject, columnTitle, row.get( columnTitle ) );
    }
    
    //
    return beanObject;
  }
  
  @Override
  public Table<E> distinct()
  {
    //
    Table<E> table = new ArrayTable<E>();
    
    //
    for ( Row<E> row : this )
    {
      if ( !table.containsRow( row ) )
      {
        table.addRow( row );
      }
    }
    
    //
    return table;
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
      for ( E element : row )
      {
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
  
}
