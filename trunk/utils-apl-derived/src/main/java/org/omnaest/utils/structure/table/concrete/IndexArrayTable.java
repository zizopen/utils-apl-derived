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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.sorting.SortUtil;
import org.omnaest.utils.sorting.SortUtil.ArbitraryStructureContext;
import org.omnaest.utils.sorting.SortUtil.ComparableArbitraryStructureIndexPosition;
import org.omnaest.utils.sorting.SortUtil.MergeSortDataModify;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.CollectionUtils.ElementConverter;
import org.omnaest.utils.structure.collection.list.IndexArrayList;
import org.omnaest.utils.structure.collection.list.IndexList;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.table.IndexTable;
import org.omnaest.utils.structure.table.Table;

/**
 * Extension of the {@link ArrayTable} class, which allows to used index lists for faster access. This allows more advanced where
 * clauses. Disadvantage: the element type has to implement {@link Comparable}
 * 
 * @see IndexTable
 * @see Table
 * @author Omnaest
 */
public class IndexArrayTable<E extends Comparable<E>> extends ArrayTable<E> implements IndexTable<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long      serialVersionUID       = 2940698111037523937L;
  
  /* ********************************************** Variables ********************************************** */
  private IndexedListFactory     indexedListFactory     = new IndexedListFactory();
  private IndexElementComparator indexElementComparator = new IndexElementComparator();
  private double                 columnIndexFillFactor  = 0;
  private double                 rowIndexFillFactor     = 0;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new instance.
   */
  public IndexArrayTable()
  {
    super();
  }
  
  /**
   * Creates a new object with a copy of the given table object.
   * 
   * @param table
   */
  public IndexArrayTable( Table<E> table )
  {
    this.putTable( table, 0, 0 );
  }
  
  /**
   * Used to compare two elements.
   * 
   * @see IndexArrayTable#orderRowsBy(int, boolean)
   * @author Omnaest
   */
  private class IndexElementComparator implements Comparator<E>
  {
    @Override
    public int compare( E e1, E e2 )
    {
      //
      int retval;
      
      //
      if ( e1 != null && e2 != null )
      {
        retval = e1.compareTo( e2 );
      }
      else if ( e1 != null && e2 == null )
      {
        try
        {
          retval = e1.compareTo( e2 );
        }
        catch ( NullPointerException e )
        {
          retval = -1;
        }
      }
      else if ( e1 == null && e2 != null )
      {
        try
        {
          retval = -1 * e2.compareTo( e1 );
        }
        catch ( NullPointerException e )
        {
          retval = 1;
        }
      }
      else
      //both are null
      {
        retval = 0;
      }
      
      //
      return retval;
    }
  }
  
  public static class IndexedListFactory
  {
    public <F extends Comparable<? super F>> IndexList<F> create()
    {
      return new IndexArrayList<F>();
    }
  }
  
  /*
   * Methods
   */

  @Override
  public boolean isRowIndexed( int rowIndexPosition )
  {
    return this.isIndexedList( this.rowList.get( rowIndexPosition ) );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#isRowIndexed(java.lang.Enum)
   */
  public boolean isRowIndexed( Enum<?> rowTitleEnum )
  {
    return this.isRowIndexed( this.determineRowIndexPosition( rowTitleEnum ) );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#isRowIndexed(java.lang.String)
   */
  public boolean isRowIndexed( String rowTitle )
  {
    return this.isRowIndexed( this.determineRowIndexPosition( rowTitle ) );
  }
  
  public boolean isColumnIndexed( int columnIndexPosition )
  {
    return this.isColumnInTableBoundary( columnIndexPosition ) && this.isIndexedList( this.columnList.get( columnIndexPosition ) );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#isColumnIndexed(java.lang.Enum)
   */
  public boolean isColumnIndexed( Enum<?> columnTitleEnum )
  {
    return this.isColumnIndexed( this.determineColumnIndexPosition( columnTitleEnum ) );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#isColumnIndexed(java.lang.String)
   */
  public boolean isColumnIndexed( String columnTitle )
  {
    return this.isColumnIndexed( this.determineColumnIndexPosition( columnTitle ) );
  }
  
  boolean isIndexedList( List<E> list )
  {
    return ( list instanceof IndexList );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexRow(int, boolean)
   */
  public Table<E> setIndexRow( int rowIndexPosition, boolean indexed )
  {
    //
    this.expandTableBoundariesToGivenIndexPositionsIfNecessary( rowIndexPosition, -1 );
    
    //
    if ( this.isRowInTableBoundary( rowIndexPosition ) )
    {
      List<E> row = this.rowList.get( rowIndexPosition );
      if ( indexed && !this.isIndexedList( row ) )
      {
        List<E> newRow = this.indexedListFactory.<E> create();
        newRow.addAll( row );
        this.rowList.set( rowIndexPosition, newRow );
      }
      else if ( !indexed && this.isIndexedList( row ) )
      {
        List<E> newRow = new ArrayList<E>( 0 );
        newRow.addAll( row );
        this.rowList.set( rowIndexPosition, newRow );
      }
    }
    
    //
    this.determineIndexFillFactors();
    
    //
    return this;
  }
  
  private void determineIndexFillFactors()
  {
    this.columnIndexFillFactor = this.determineIndexFillFactor( this.columnList, 0 );
    this.rowIndexFillFactor = this.determineIndexFillFactor( this.rowList, 0 );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexRow(java.lang.Enum, boolean)
   */
  public Table<E> setIndexRow( Enum<?> rowTitleEnum, boolean indexed )
  {
    return this.setIndexRow( this.determineRowIndexPosition( rowTitleEnum ), indexed );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexRow(java.lang.String, boolean)
   */
  public Table<E> setIndexRow( String rowTitle, boolean indexed )
  {
    return this.setIndexRow( this.determineRowIndexPosition( rowTitle ), indexed );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexColumn(int, boolean)
   */
  public Table<E> setIndexColumn( int columnIndexPosition, boolean indexed )
  {
    //
    this.expandTableBoundariesToGivenIndexPositionsIfNecessary( -1, columnIndexPosition );
    
    //
    if ( this.isColumnInTableBoundary( columnIndexPosition ) )
    {
      List<E> column = this.columnList.get( columnIndexPosition );
      if ( indexed && !this.isIndexedList( column ) )
      {
        List<E> newColumn = this.indexedListFactory.<E> create();
        newColumn.addAll( column );
        this.columnList.set( columnIndexPosition, newColumn );
      }
      else if ( !indexed && this.isIndexedList( column ) )
      {
        List<E> newColumn = new ArrayList<E>( 0 );
        newColumn.addAll( column );
        this.columnList.set( columnIndexPosition, newColumn );
      }
    }
    
    //
    this.determineIndexFillFactors();
    
    //
    return this;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexColumn(java.lang.Enum, boolean)
   */
  public Table<E> setIndexColumn( Enum<?> columnTitleEnum, boolean indexed )
  {
    return this.setIndexColumn( this.determineColumnIndexPosition( columnTitleEnum ), indexed );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.java.utils.IndexedTable#setIndexColumn(java.lang.String, boolean)
   */
  public Table<E> setIndexColumn( String columnTitle, boolean indexed )
  {
    return this.setIndexColumn( this.determineColumnIndexPosition( columnTitle ), indexed );
  }
  
  public Table<E> setIndexedListFactory( IndexedListFactory indexedListFactory )
  {
    this.indexedListFactory = indexedListFactory;
    return this;
  }
  
  @Override
  public Table<E> getSubTable( int[] rowIndexPositions, int[] columnIndexPositions )
  {
    //
    Table<E> superTable = super.getSubTable( rowIndexPositions, columnIndexPositions );
    IndexTable<E> rettable = new IndexArrayTable<E>( superTable );
    
    //generate a sorted list 
    List<Integer> rowIndexPositionList = this.determineIndexPositionList( rowIndexPositions );
    List<Integer> columnIndexPositionList = this.determineIndexPositionList( columnIndexPositions );
    
    //set indexes
    {
      
      int columnIndexPosition = 0;
      for ( int iColumnIndexPosition : columnIndexPositionList )
      {
        List<E> iColumn = this.columnList.get( iColumnIndexPosition );
        if ( this.isIndexedList( iColumn ) )
        {
          rettable.setIndexColumn( columnIndexPosition++, true );
        }
      }
      int rowIndexPosition = 0;
      for ( int iRowIndexPosition : rowIndexPositionList )
      {
        List<E> iRow = this.rowList.get( iRowIndexPosition );
        if ( this.isIndexedList( iRow ) )
        {
          rettable.setIndexRow( rowIndexPosition++, true );
        }
      }
    }
    
    return rettable;
  }
  
  public IndexTable<E> orderRowsBy( final int columnIndexPosition, final boolean ascending )
  {
    //
    ArbitraryStructureContext arbitraryStructureContext = new ArbitraryStructureContext()
    {
      @Override
      public int getStartIndexPosition()
      {
        return 0;
      }
      
      @Override
      public int getEndIndexPosition()
      {
        return IndexArrayTable.this.getTableSize().getRowSize() - 1;
      }
    };
    
    ComparableArbitraryStructureIndexPosition comparableArbitraryStructureIndexPosition = new ComparableArbitraryStructureIndexPosition()
    {
      @Override
      public int compare( int indexPosition1, int indexPosition2 )
      {
        E e1 = IndexArrayTable.this.getCell( indexPosition1, columnIndexPosition );
        E e2 = IndexArrayTable.this.getCell( indexPosition2, columnIndexPosition );
        return IndexArrayTable.this.indexElementComparator.compare( e1, e2 );
      }
    };
    
    MergeSortDataModify mergeSortDataModify = new MergeSortDataModify()
    {
      private Table<E> stack = new IndexArrayTable<E>();
      
      @Override
      public void pushOnStack( int sourceIndexPosition )
      {
        List<E> row = IndexArrayTable.this.getRow( sourceIndexPosition );
        stack.addRow( row );
      }
      
      @Override
      public void popFromStack( int destinationIndexPosition )
      {
        List<E> row = this.stack.removeRow( this.stack.getTableSize().getRowSize() - 1 );
        IndexArrayTable.this.setRow( destinationIndexPosition, row );
      }
    };
    
    //
    SortUtil.mergeSort( arbitraryStructureContext, comparableArbitraryStructureIndexPosition, mergeSortDataModify, ascending );
    //
    return this;
  }
  
  @Override
  public IndexTable<E> orderRowsAscendingBy( String columnTitle )
  {
    int columnIndexPosition = this.determineColumnIndexPosition( columnTitle );
    return this.orderRowsBy( columnIndexPosition, true );
  }
  
  @Override
  public <B> IndexTable<E> orderRowsAscendingByBeanObjectPropertyNotNull( B beanObject )
  {
    //
    IndexTable<E> rettable = this;
    
    //
    Map<String, E> beanPropertyNameToElementMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanObject );
    for ( String columnTitle : beanPropertyNameToElementMap.keySet() )
    {
      if ( beanPropertyNameToElementMap.get( columnTitle ) != null )
      {
        rettable = this.orderRowsAscendingBy( columnTitle );
        break;
      }
    }
    
    //
    return rettable;
  }
  
  @Override
  public int[] indexesOfRowsWithElementsGreaterThan( int columnIndexPosition, E element )
  {
    //
    int[] retvals = null;
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      if ( this.isColumnIndexed( columnIndexPosition ) )
      {
        int[] largerElementsIndexPositions = ( (IndexList<E>) this.columnList.get( columnIndexPosition ) ).indexesOfElementsGreaterThan( element );
        
        retvals = this.determineAllRowIndexPositions();
        
        retvals = this.determineSubtractedIndexPositions( retvals, largerElementsIndexPositions );
      }
      else
      {
        //full table scan
        List<Integer> largerElementsIndexPositionList = new ArrayList<Integer>( 0 );
        for ( int ii = 0; ii < this.rowList.size(); ii++ )
        {
          E e = this.getCell( ii, columnIndexPosition );
          if ( this.indexElementComparator.compare( element, e ) < 0 )
          {
            largerElementsIndexPositionList.add( ii );
          }
        }
        
        //
        retvals = CollectionUtils.toArrayInt( largerElementsIndexPositionList );
        
      }
    }
    
    //
    return retvals;
  }
  
  @Override
  public int[] indexesOfRowsWithElementsLesserThan( int columnIndexPosition, E element )
  {
    //
    int[] retvals = null;
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      if ( this.isColumnIndexed( columnIndexPosition ) )
      {
        int[] smallerElementsIndexPositions = ( (IndexList<E>) this.columnList.get( columnIndexPosition ) ).indexesOfElementsGreaterThan( element );
        
        retvals = this.determineAllRowIndexPositions();
        
        retvals = this.determineSubtractedIndexPositions( retvals, smallerElementsIndexPositions );
      }
      else
      {
        //full table scan
        List<Integer> smallerElementsIndexPositionList = new ArrayList<Integer>( 0 );
        for ( int ii = 0; ii < this.rowList.size(); ii++ )
        {
          E e = this.getCell( ii, columnIndexPosition );
          if ( this.indexElementComparator.compare( element, e ) > 0 )
          {
            smallerElementsIndexPositionList.add( ii );
          }
        }
        
        //
        retvals = CollectionUtils.toArrayInt( smallerElementsIndexPositionList );
      }
    }
    
    //
    return retvals;
  }
  
  @Override
  public int[] indexesOfRowsWithElementsBetween( int columnIndexPosition, E lowerElement, E upperElement )
  {
    //
    int[] retvals = null;
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      if ( this.isColumnIndexed( columnIndexPosition ) )
      {
        int[] largerElementsIndexPositions = ( (IndexList<E>) this.columnList.get( columnIndexPosition ) ).indexesOfElementsGreaterThan( upperElement );
        int[] smallerElementsIndexPositions = ( (IndexList<E>) this.columnList.get( columnIndexPosition ) ).indexesOfElementsLessThan( lowerElement );
        
        retvals = this.determineAllRowIndexPositions();
        retvals = this.determineSubtractedIndexPositions( retvals, smallerElementsIndexPositions );
        retvals = this.determineSubtractedIndexPositions( retvals, largerElementsIndexPositions );
      }
      else
      {
        //full table scan
        List<Integer> betweenIndexPositionList = new ArrayList<Integer>( 0 );
        for ( int ii = 0; ii < this.rowList.size(); ii++ )
        {
          E e = this.getCell( ii, columnIndexPosition );
          if ( this.indexElementComparator.compare( upperElement, e ) >= 0
               && this.indexElementComparator.compare( lowerElement, e ) <= 0 )
          {
            betweenIndexPositionList.add( ii );
          }
        }
        
        //
        retvals = CollectionUtils.toArrayInt( betweenIndexPositionList );
        
      }
    }
    
    //
    return retvals;
  }
  
  @Override
  public IndexTable<E> whereElementIsGreaterThan( String columnTitle, E element )
  {
    return this.whereElementIsGreaterThan( this.determineColumnIndexPosition( columnTitle ), element );
  }
  
  @Override
  public IndexTable<E> whereElementIsGreaterThan( Enum<?> columnTitleEnum, E element )
  {
    return this.whereElementIsGreaterThan( this.determineColumnIndexPosition( columnTitleEnum ), element );
  }
  
  @Override
  public IndexTable<E> whereElementIsGreaterThan( int columnIndexPosition, E element )
  {
    //
    IndexTable<E> table = this.cloneTableStructure();
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      int[] rowIndexPositionsForElementsGreaterThan = this.indexesOfRowsWithElementsGreaterThan( columnIndexPosition, element );
      
      for ( int rowIndexPosition : rowIndexPositionsForElementsGreaterThan )
      {
        table.addRow( this.getRow( rowIndexPosition ) );
      }
    }
    
    //
    return table;
  }
  
  @Override
  public Table<E> whereElementIsGreaterThanColumnIndexMap( Map<Integer, E> columnIndexToElementMap )
  {
    //
    IndexTable<E> table = this;
    
    //
    if ( columnIndexToElementMap != null )
    {
      for ( Integer columnIndexPosition : columnIndexToElementMap.keySet() )
      {
        if ( this.isColumnInTableBoundary( columnIndexPosition ) )
        {
          E element = columnIndexToElementMap.get( columnIndexPosition );
          table = (IndexTable<E>) table.whereElementIsGreaterThan( columnIndexPosition, element );
        }
      }
    }
    
    //
    return table;
  }
  
  @Override
  public <B> IndexTable<E> whereElementIsGreaterThanBeanObject( B beanObject )
  {
    Map<String, E> columnTitleToElementMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanObject,
                                                                                                    this.getColumnTitles() );
    return this.whereElementIsGreaterThanColumnTitleMap( columnTitleToElementMap );
  }
  
  @Override
  public <B> IndexTable<E> whereElementIsGreaterThanBeanObjectIgnoringNullValues( B beanObject )
  {
    //
    Map<String, E> columnTitleToElementMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanObject,
                                                                                                    this.getColumnTitles() );
    for ( String key : new ArrayList<String>( columnTitleToElementMap.keySet() ) )
    {
      if ( columnTitleToElementMap.get( key ) == null )
      {
        columnTitleToElementMap.remove( key );
      }
    }
    
    //
    return this.whereElementIsGreaterThanColumnTitleMap( columnTitleToElementMap );
  }
  
  @Override
  public IndexTable<E> whereElementIsGreaterThanColumnTitleMap( Map<String, E> columnTitleToElementMap )
  {
    //
    ElementConverter<String, Integer> columnTitleToColumnIndexElementConverter = new ElementConverter<String, Integer>()
    {
      @Override
      public Integer convert( String columnTitle )
      {
        return IndexArrayTable.this.determineColumnIndexPosition( columnTitle );
      }
    };
    
    //
    Map<Integer, E> columnIndexToElementMap = MapUtils.<String, Integer, E> convertMapKey( columnTitleToElementMap,
                                                                                           columnTitleToColumnIndexElementConverter );
    
    //
    return (IndexTable<E>) this.whereElementIsGreaterThanColumnIndexMap( columnIndexToElementMap );
  }
  
  @Override
  public IndexTable<E> whereElementIsLesserThan( String columnTitle, E element )
  {
    return this.whereElementIsLesserThan( this.determineColumnIndexPosition( columnTitle ), element );
  }
  
  @Override
  public IndexTable<E> whereElementIsLesserThan( int columnIndexPosition, E element )
  {
    //
    IndexTable<E> table = this.cloneTableStructure();
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      int[] rowIndexPositionsForElementsLesserThan = this.indexesOfRowsWithElementsLesserThan( columnIndexPosition, element );
      
      for ( int rowIndexPosition : rowIndexPositionsForElementsLesserThan )
      {
        table.addRow( this.getRow( rowIndexPosition ) );
      }
    }
    
    //
    return table;
  }
  
  @Override
  public IndexTable<E> whereElementEquals( String columnTitle, E element )
  {
    return this.whereElementEquals( this.determineColumnIndexPosition( columnTitle ), element );
  }
  
  @Override
  public IndexTable<E> whereElementEquals( Enum<?> columnTitleEnum, E element )
  {
    return this.whereElementEquals( this.determineColumnIndexPosition( columnTitleEnum ), element );
  }
  
  @Override
  public IndexTable<E> whereElementEquals( int columnIndexPosition, E element )
  {
    //
    IndexTable<E> table = this.cloneTableStructure();
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      int[] rowIndexPositionsForEqualElements = this.indexesOfRowsWithElementsEquals( columnIndexPosition, element );
      
      for ( int rowIndexPosition : rowIndexPositionsForEqualElements )
      {
        table.addRow( this.getRow( rowIndexPosition ) );
      }
    }
    
    //
    return table;
  }
  
  @Override
  public IndexTable<E> whereElementEqualsColumnIndexMap( Map<Integer, E> columnIndexToElementMap )
  {
    //
    IndexTable<E> table = this;
    
    //
    if ( columnIndexToElementMap != null )
    {
      for ( Integer columnIndexPosition : columnIndexToElementMap.keySet() )
      {
        if ( this.isColumnInTableBoundary( columnIndexPosition ) )
        {
          E element = columnIndexToElementMap.get( columnIndexPosition );
          table = (IndexTable<E>) table.whereElementEquals( columnIndexPosition, element );
        }
      }
    }
    
    //
    return table;
  }
  
  @Override
  public <B> IndexTable<E> whereElementEqualsBeanObject( B beanObject )
  {
    Map<String, E> columnTitleToElementMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanObject,
                                                                                                    this.getColumnTitles() );
    return this.whereElementEqualsColumnTitleMap( columnTitleToElementMap );
  }
  
  @Override
  public <B> IndexTable<E> whereElementEqualsBeanObjectIgnoringNullValues( B beanObject )
  {
    //
    Map<String, E> columnTitleToElementMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanObject,
                                                                                                    this.getColumnTitles() );
    for ( String key : new ArrayList<String>( columnTitleToElementMap.keySet() ) )
    {
      if ( columnTitleToElementMap.get( key ) == null )
      {
        columnTitleToElementMap.remove( key );
      }
    }
    
    //
    return this.whereElementEqualsColumnTitleMap( columnTitleToElementMap );
  }
  
  @Override
  public IndexTable<E> whereElementEqualsColumnEnumMap( Map<Enum<?>, E> columnEnumToElementMap )
  {
    //    
    ElementConverter<Enum<?>, Integer> columnEnumToColumnIndexElementConverter = new ElementConverter<Enum<?>, Integer>()
    {
      
      @Override
      public Integer convert( Enum<?> columnTitleEnum )
      {
        return IndexArrayTable.this.determineColumnIndexPosition( columnTitleEnum );
      }
    };
    
    //
    Map<Integer, E> columnIndexToElementMap = MapUtils.convertMapKey( columnEnumToElementMap,
                                                                      columnEnumToColumnIndexElementConverter );
    
    //
    return this.whereElementEqualsColumnIndexMap( columnIndexToElementMap );
  }
  
  @Override
  public IndexTable<E> whereElementEqualsColumnTitleMap( Map<String, E> columnTitleToElementMap )
  {
    //
    ElementConverter<String, Integer> columnTitleToColumnIndexElementConverter = new ElementConverter<String, Integer>()
    {
      @Override
      public Integer convert( String columnTitle )
      {
        return IndexArrayTable.this.determineColumnIndexPosition( columnTitle );
      }
    };
    
    //
    Map<Integer, E> columnIndexToElementMap = MapUtils.convertMapKey( columnTitleToElementMap,
                                                                      columnTitleToColumnIndexElementConverter );
    
    //
    return this.whereElementEqualsColumnIndexMap( columnIndexToElementMap );
  }
  
  @Override
  public IndexTable<E> whereElementIsBetween( String columnTitle, E lowerElement, E upperElement )
  {
    return this.whereElementIsBetween( this.determineColumnIndexPosition( columnTitle ), lowerElement, upperElement );
  }
  
  @Override
  public IndexTable<E> whereElementIsBetween( int columnIndexPosition, E lowerElement, E upperElement )
  {
    //
    IndexTable<E> table = this.cloneTableStructure();
    
    //
    if ( this.isRowAndColumnInTableBoundary( 0, columnIndexPosition ) )
    {
      int[] rowIndexPositionsForElementsBetween = this.indexesOfRowsWithElementsBetween( columnIndexPosition, lowerElement,
                                                                                         upperElement );
      
      for ( int rowIndexPosition : rowIndexPositionsForElementsBetween )
      {
        table.addRow( this.getRow( rowIndexPosition ) );
      }
    }
    
    //
    return table;
  }
  
  @Override
  public IndexTable<E> cloneTableStructure()
  {
    //
    IndexTable<E> rettable = new IndexArrayTable<E>();
    
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
    
    //set the same indexes
    for ( int columnIndexPosition = 0; columnIndexPosition < this.columnList.size(); columnIndexPosition++ )
    {
      rettable.setIndexColumn( columnIndexPosition, this.isColumnIndexed( columnIndexPosition ) );
    }
    
    //
    return rettable;
  }
  
  @Override
  protected boolean areCellsEqual( E cell1, E cell2 )
  {
    return this.indexElementComparator.compare( cell1, cell2 ) == 0;
  }
  
  public int[] indexesOfRowsWithElementsEquals( int columnIndexPosition, E element )
  {
    //
    int[] retvals = null;
    
    if ( this.isColumnInTableBoundary( columnIndexPosition ) )
    {
      //
      if ( this.isColumnIndexed( columnIndexPosition ) )
      {
        //use the index to find all the index positions
        IndexList<E> columnIndex = (IndexList<E>) this.columnList.get( columnIndexPosition );
        retvals = columnIndex.indexesOf( element );
      }
      else
      {
        //make a full table scan
        retvals = super.indexesOfRowsWithElementsEquals( columnIndexPosition, element );
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Determines the fill factor of indexes for a given list of row or column lists.
   * 
   * @param listList
   * @param breakLevel
   *          : if a breaklevel >= 0 is set, the determination breaks, if the given level cannot be reached anymore. This results
   *          in a wrong fill factor result in an absolut manner.
   * @return
   */
  protected double determineIndexFillFactor( List<List<E>> listList, double breakLevel )
  {
    //
    double retval = 0;
    
    //
    int size = 0;
    if ( listList != null && ( size = listList.size() ) > 0 )
    {
      //
      int indexCount = 0;
      int minCount = (int) Math.round( size * breakLevel );
      for ( int ii = size - 1; ii >= 0; ii-- )
      {
        //
        List<E> list = listList.get( ii );
        
        //
        if ( this.isIndexedList( list ) )
        {
          indexCount++;
        }
        
        //potential remaining + matched < needed
        if ( ii + indexCount < minCount )
        {
          break;
        }
      }
      
      //
      retval = indexCount / size;
    }
    
    //
    return retval;
  }
  
  @Override
  public CellIndexPosition indexOf( E element )
  {
    //
    CellIndexPosition retval = new CellIndexPositionImpl();
    
    /*
     * If there are indexed lists on the columns they are four times as fast as normal list. 
     * But for columns we have to collect all results, because any column can have a result in a higher row. 
     * Rows can break the search loop after they found a result. In average the need so the half time, because the dont 
     * have to search the whole space.
     * 
     * effort for column search:
     *     1/4 * columnFillFactor * tableSpace + (1-columnFillFactor) * tableSpace
     *     
     * effort for row search:
     *     1/2 (1/4 * rowFillFactor * tableSpace + (1-rowFillFactor) * tableSpace)
     */
    if ( 2 - 1.5 * this.columnIndexFillFactor <= 1 - 0.75 * this.rowIndexFillFactor )
    {
      for ( int ii = 0; ii < this.columnList.size(); ii++ )
      {
        int indexOf = this.columnList.get( ii ).indexOf( element );
        if ( indexOf >= 0 )
        {
          CellIndexPosition cellIndexPosition = this.determineCellIndexPosition( indexOf, ii );
          if ( retval.getCellIndexPosition() < cellIndexPosition.getCellIndexPosition() )
          {
            retval = cellIndexPosition;
          }
        }
      }
    }
    else
    {
      for ( int ii = 0; ii < this.rowList.size(); ii++ )
      {
        int indexOf = this.rowList.get( ii ).indexOf( element );
        if ( indexOf >= 0 )
        {
          retval = this.determineCellIndexPosition( ii, indexOf );
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public IndexTable<E> clone()
  {
    IndexTable<E> rettable = new IndexArrayTable<E>( super.clone() );
    
    //set the indexes
    for ( int ii = 0; ii < this.rowList.size(); ii++ )
    {
      if ( this.isRowIndexed( ii ) )
      {
        rettable.setIndexRow( ii, true );
      }
    }
    for ( int jj = 0; jj < this.columnList.size(); jj++ )
    {
      if ( this.isColumnIndexed( jj ) )
      {
        rettable.setIndexColumn( jj, true );
      }
    }
    return rettable;
  }
  
}
