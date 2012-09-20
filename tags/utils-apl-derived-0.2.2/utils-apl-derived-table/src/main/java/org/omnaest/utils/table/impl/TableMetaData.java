/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.table.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableEventHandler;

/**
 * Container for the {@link Table} meta data like titles
 * 
 * @author Omnaest
 * @param <E>
 */
public class TableMetaData<E> implements TableEventHandler<E>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -7072099446189045388L;
  private List<String>      columnTitleList  = new ArrayList<String>();
  private List<String>      rowTitleList     = new ArrayList<String>();
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private String            tableName        = null;
  
  /* *************************************************** Methods **************************************************** */
  
  public int getColumnIndex( Pattern columnTitlePattern )
  {
    for ( int ii = 0; ii < this.columnTitleList.size(); ii++ )
    {
      final String columnTitle = this.columnTitleList.get( ii );
      if ( columnTitlePattern.matcher( columnTitle ).matches() )
      {
        return ii;
      }
    }
    return -1;
  }
  
  public int getColumnIndex( String columnTitle )
  {
    return this.columnTitleList.indexOf( columnTitle );
  }
  
  public BitSet getColumnIndexFilter( Pattern columnTitlePattern )
  {
    BitSet retval = new BitSet();
    for ( int ii = 0; ii < this.columnTitleList.size(); ii++ )
    {
      final String columnTitle = this.columnTitleList.get( ii );
      if ( columnTitlePattern.matcher( columnTitle ).matches() )
      {
        retval.set( ii );
      }
    }
    return retval;
  }
  
  public BitSet getColumnIndexFilter( Set<String> columnTitleSet )
  {
    BitSet retval = new BitSet();
    if ( columnTitleSet != null )
    {
      for ( int ii = 0; ii < this.columnTitleList.size(); ii++ )
      {
        final String columnTitle = this.columnTitleList.get( ii );
        if ( columnTitleSet.contains( columnTitle ) )
        {
          retval.set( ii );
        }
      }
    }
    return retval;
  }
  
  public String getColumnTitle( int columnIndex )
  {
    return ListUtils.get( this.columnTitleList, columnIndex );
  }
  
  public List<String> getColumnTitleList()
  {
    return Collections.unmodifiableList( this.columnTitleList );
  }
  
  public int getRowIndex( String rowTitle )
  {
    return this.rowTitleList.indexOf( rowTitle );
  }
  
  public String getRowTitle( int rowIndex )
  {
    return ListUtils.get( this.rowTitleList, rowIndex );
  }
  
  public List<String> getRowTitleList()
  {
    return Collections.unmodifiableList( this.rowTitleList );
  }
  
  public String getTableName()
  {
    return this.tableName;
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    ListUtils.add( this.columnTitleList, columnIndex, (E) null );
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    ListUtils.add( this.rowTitleList, rowIndex, (E) null );
  }
  
  @Override
  public void handleClearTable()
  {
    this.columnTitleList.clear();
    this.rowTitleList.clear();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle )
  {
    ListUtils.remove( this.columnTitleList, columnIndex );
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements, String rowTitle )
  {
    ListUtils.remove( this.rowTitleList, rowIndex );
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
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
  
  /**
   * @param columnIndex
   * @param columnTitle
   * @return previously set element
   */
  public String setColumnTitle( int columnIndex, String columnTitle )
  {
    final String retval = ListUtils.get( this.columnTitleList, columnIndex );
    ListUtils.set( this.columnTitleList, columnIndex, columnTitle );
    return retval;
  }
  
  /**
   * @param columnTitles
   * @return previous column titles
   */
  public String[] setColumnTitles( String[] columnTitles )
  {
    final String[] retvals = this.columnTitleList.toArray( new String[0] );
    this.columnTitleList.clear();
    ListUtils.addAll( this.columnTitleList, columnTitles );
    return retvals;
  }
  
  /**
   * @param rowIndex
   * @param rowTitle
   * @return previously set row title
   */
  public String setRowTitle( int rowIndex, String rowTitle )
  {
    final String retval = ListUtils.get( this.rowTitleList, rowIndex );
    ListUtils.set( this.rowTitleList, rowIndex, rowTitle );
    return retval;
  }
  
  /**
   * @param rowTitles
   * @return previously set elements
   */
  public String[] setRowTitles( String[] rowTitles )
  {
    final String[] retvals = this.rowTitleList.toArray( new String[0] );
    this.rowTitleList.clear();
    ListUtils.addAll( this.rowTitleList, rowTitles );
    return retvals;
  }
  
  /**
   * @param tableName
   * @return the previously set table name
   */
  public String setTableName( String tableName )
  {
    String retval = this.tableName;
    this.tableName = tableName;
    return retval;
  }
  
  @Override
  public void handleModifiedColumnTitle( int columnIndex, String columnTitle, String columnTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitle( int rowIndex, String rowTitle, String rowTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedColumnTitles( String[] columnTitles, String[] columnTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitles( String[] rowTitles, String[] rowTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedTableName( String tableName, String tableNamePrevious )
  {
  }
}
