package org.omnaest.utils.table2.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.table2.Table;

/**
 * Container for the {@link Table} meta data like titles
 * 
 * @author Omnaest
 * @param <E>
 */
public class TableMetaData<E> implements TableEventHandler<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private String       tableName       = null;
  private List<String> columnTitleList = new ArrayList<String>();
  private List<String> rowTitleList    = new ArrayList<String>();
  
  /* *************************************************** Methods **************************************************** */
  
  public String getTableName()
  {
    return this.tableName;
  }
  
  public void setTableName( String tableName )
  {
    this.tableName = tableName;
  }
  
  public List<String> getColumnTitleList()
  {
    return Collections.unmodifiableList( this.columnTitleList );
  }
  
  public String getColumnTitle( int columnIndex )
  {
    return ListUtils.get( this.columnTitleList, columnIndex );
  }
  
  public String getRowTitle( int rowIndex )
  {
    return ListUtils.get( this.rowTitleList, rowIndex );
  }
  
  public void setColumnTitles( Iterable<String> columnTitleIterable )
  {
    this.columnTitleList.clear();
    ListUtils.addAll( this.columnTitleList, columnTitleIterable );
  }
  
  public List<String> getRowTitleList()
  {
    return Collections.unmodifiableList( this.rowTitleList );
  }
  
  public void setRowTitles( Iterable<String> rowTitleIterable )
  {
    this.rowTitleList.clear();
    ListUtils.addAll( this.rowTitleList, rowTitleIterable );
  }
  
  public void setRowTitle( int rowIndex, String rowTitle )
  {
    ListUtils.set( this.rowTitleList, rowIndex, rowTitle );
  }
  
  public void setColumnTitle( int columnIndex, String columnTitle )
  {
    ListUtils.set( this.columnTitleList, columnIndex, columnTitle );
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    this.rowTitleList.add( rowIndex, null );
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
  }
  
  @Override
  public void handleClearTable()
  {
    this.columnTitleList.clear();
    this.rowTitleList.clear();
  }
  
  public boolean hasColumnTitles()
  {
    boolean hasColumnTitles = !ListUtils.filterExcludingNullElements( this.columnTitleList ).isEmpty();
    return hasColumnTitles;
  }
  
  public boolean hasRowTitles()
  {
    boolean hasRowTitles = !ListUtils.filterExcludingNullElements( this.rowTitleList ).isEmpty();
    return hasRowTitles;
  }
  
  public boolean hasTableName()
  {
    return this.tableName != null;
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
  }
}
