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
package org.omnaest.utils.table.impl.adapter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;

/**
 * Adapter to make a {@link Table} usable as {@link ResultSet}
 * 
 * @see Table
 * @see ResultSet
 * @author Omnaest
 */
class TableToResultSetAdapter implements ResultSet
{
  /* ********************************************** Variables ********************************************** */
  protected Table<?>             table            = null;
  protected ListIterator<Row<?>> rowIterator      = null;
  protected Row<?>               row              = null;
  protected boolean              directionForward = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new initialized adapter. For use with {@link TableAdapterProvider#adapter(TableAdapter)} use
   * {@link #TableToResultSetAdapter()} instead
   * 
   * @see TableToResultSetAdapter
   * @param table
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  TableToResultSetAdapter( Table table )
  {
    super();
    this.table = table;
    this.rowIterator = IteratorUtils.toListIterator( table.rows().iterator() );
  }
  
  @Override
  public <T> T unwrap( Class<T> iface ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean isWrapperFor( Class<?> iface ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean next() throws SQLException
  {
    //
    this.directionForward = true;
    
    //
    boolean retval = this.rowIterator != null && this.rowIterator.hasNext();
    
    //
    this.row = this.rowIterator != null ? this.rowIterator.next() : null;
    
    //
    return retval;
  }
  
  @Override
  public void close() throws SQLException
  {
    this.rowIterator = null;
  }
  
  @Override
  public boolean wasNull() throws SQLException
  {
    return this.row == null;
  }
  
  @Override
  public String getString( int columnIndex ) throws SQLException
  {
    return String.valueOf( this.row.getElement( columnIndex - 1 ) );
  }
  
  @Override
  public boolean getBoolean( int columnIndex ) throws SQLException
  {
    return Boolean.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public byte getByte( int columnIndex ) throws SQLException
  {
    return Byte.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public short getShort( int columnIndex ) throws SQLException
  {
    return Short.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public int getInt( int columnIndex ) throws SQLException
  {
    return Integer.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public long getLong( int columnIndex ) throws SQLException
  {
    return Long.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public float getFloat( int columnIndex ) throws SQLException
  {
    return Float.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public double getDouble( int columnIndex ) throws SQLException
  {
    return Double.valueOf( this.getString( columnIndex ) );
  }
  
  @Override
  public BigDecimal getBigDecimal( int columnIndex, int scale ) throws SQLException
  {
    return new BigDecimal( this.getString( columnIndex ) );
  }
  
  @Override
  public byte[] getBytes( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Date getDate( int columnIndex ) throws SQLException
  {
    return new Date( this.getLong( columnIndex ) );
  }
  
  @Override
  public Time getTime( int columnIndex ) throws SQLException
  {
    return new Time( this.getLong( columnIndex ) );
  }
  
  @Override
  public Timestamp getTimestamp( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public InputStream getAsciiStream( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public InputStream getUnicodeStream( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public InputStream getBinaryStream( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public String getString( String columnLabel ) throws SQLException
  {
    return String.valueOf( this.row.getElement( columnLabel ) );
  }
  
  @Override
  public boolean getBoolean( String columnLabel ) throws SQLException
  {
    return Boolean.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public byte getByte( String columnLabel ) throws SQLException
  {
    return Byte.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public short getShort( String columnLabel ) throws SQLException
  {
    return Short.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public int getInt( String columnLabel ) throws SQLException
  {
    return Integer.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public long getLong( String columnLabel ) throws SQLException
  {
    return Long.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public float getFloat( String columnLabel ) throws SQLException
  {
    return Float.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public double getDouble( String columnLabel ) throws SQLException
  {
    return Double.valueOf( this.getString( columnLabel ) );
  }
  
  @Override
  public BigDecimal getBigDecimal( String columnLabel, int scale ) throws SQLException
  {
    return new BigDecimal( this.getString( columnLabel ) );
  }
  
  @Override
  public byte[] getBytes( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Date getDate( String columnLabel ) throws SQLException
  {
    return new Date( this.getLong( columnLabel ) );
  }
  
  @Override
  public Time getTime( String columnLabel ) throws SQLException
  {
    return new Time( this.getLong( columnLabel ) );
  }
  
  @Override
  public Timestamp getTimestamp( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public InputStream getAsciiStream( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public InputStream getUnicodeStream( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public InputStream getBinaryStream( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public SQLWarning getWarnings() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void clearWarnings() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public String getCursorName() throws SQLException
  {
    Object rowTitleValue = this.table.getRowTitle( this.getRow() );
    return rowTitleValue != null ? String.valueOf( rowTitleValue ) : null;
  }
  
  @Override
  public ResultSetMetaData getMetaData() throws SQLException
  {
    return new ResultSetMetaData()
    {
      
      @Override
      public <T> T unwrap( Class<T> iface ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isWrapperFor( Class<?> iface ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isWritable( int column ) throws SQLException
      {
        return true;
      }
      
      @Override
      public boolean isSigned( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isSearchable( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isReadOnly( int column ) throws SQLException
      {
        return false;
      }
      
      @Override
      public int isNullable( int column ) throws SQLException
      {
        return ResultSetMetaData.columnNullable;
      }
      
      @Override
      public boolean isDefinitelyWritable( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isCurrency( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public boolean isCaseSensitive( int column ) throws SQLException
      {
        return true;
      }
      
      @Override
      public boolean isAutoIncrement( int column ) throws SQLException
      {
        return false;
      }
      
      @Override
      public String getTableName( int column ) throws SQLException
      {
        Object tableName = TableToResultSetAdapter.this.table.getTableName();
        return tableName != null ? String.valueOf( tableName ) : null;
      }
      
      @Override
      public String getSchemaName( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public int getScale( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public int getPrecision( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public String getColumnTypeName( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public int getColumnType( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public String getColumnName( int column ) throws SQLException
      {
        //
        String retval = null;
        
        //
        Object columnTitleValue = null;
        try
        {
          columnTitleValue = TableToResultSetAdapter.this.table.getColumnTitle( column - 1 );
          retval = columnTitleValue != null ? String.valueOf( columnTitleValue ) : null;
        }
        catch ( Exception e )
        {
        }
        
        //
        return retval;
      }
      
      @Override
      public String getColumnLabel( int column ) throws SQLException
      {
        return this.getColumnName( column );
      }
      
      @Override
      public int getColumnDisplaySize( int column ) throws SQLException
      {
        return this.getColumnName( column ).length();
      }
      
      @Override
      public int getColumnCount() throws SQLException
      {
        return TableToResultSetAdapter.this.table.columnSize();
      }
      
      @Override
      public String getColumnClassName( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public String getCatalogName( int column ) throws SQLException
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  @Override
  public Object getObject( int columnIndex ) throws SQLException
  {
    return this.row.getElement( columnIndex - 1 );
  }
  
  @Override
  public Object getObject( String columnLabel ) throws SQLException
  {
    return this.row.getElement( columnLabel );
  }
  
  @Override
  public int findColumn( String columnLabel ) throws SQLException
  {
    return this.table.getColumnTitleList().indexOf( columnLabel ) + 1;
  }
  
  @Override
  public Reader getCharacterStream( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Reader getCharacterStream( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public BigDecimal getBigDecimal( int columnIndex ) throws SQLException
  {
    return new BigDecimal( this.getString( columnIndex ) );
  }
  
  @Override
  public BigDecimal getBigDecimal( String columnLabel ) throws SQLException
  {
    return new BigDecimal( this.getString( columnLabel ) );
  }
  
  @Override
  public boolean isBeforeFirst() throws SQLException
  {
    //
    boolean retval = false;
    
    //
    boolean directionForward = this.directionForward;
    if ( !directionForward )
    {
      this.next();
    }
    
    //
    retval = this.row == null && this.rowIterator.hasNext() && !this.rowIterator.hasPrevious();
    
    //
    if ( !directionForward )
    {
      this.previous();
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean isAfterLast() throws SQLException
  {
    //
    boolean retval = false;
    
    //
    boolean directionForward = this.directionForward;
    if ( directionForward )
    {
      this.previous();
    }
    
    //
    retval = this.row == null && this.rowIterator.hasPrevious() && !this.rowIterator.hasNext();
    
    //
    if ( directionForward )
    {
      this.next();
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean isFirst() throws SQLException
  {
    //
    boolean retval = false;
    
    //
    boolean directionForward = this.directionForward;
    if ( directionForward )
    {
      this.previous();
    }
    
    //
    retval = this.row != null && !this.rowIterator.hasPrevious();
    
    //
    if ( directionForward )
    {
      this.next();
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean isLast() throws SQLException
  {
    //
    boolean retval = false;
    
    //
    boolean directionForward = this.directionForward;
    if ( !directionForward )
    {
      this.next();
    }
    
    //
    retval = this.row != null && !this.rowIterator.hasNext() && this.rowIterator.hasPrevious();
    
    //
    if ( !directionForward )
    {
      this.previous();
    }
    
    //
    return retval;
  }
  
  @Override
  public void beforeFirst() throws SQLException
  {
    //
    this.first();
    
    //
    while ( this.rowIterator.hasPrevious() )
    {
      this.rowIterator.previous();
    }
    this.rowIterator.previous();
    this.rowIterator.next();
  }
  
  @Override
  public void afterLast() throws SQLException
  {
    while ( this.rowIterator.hasNext() )
    {
      this.rowIterator.next();
    }
    this.rowIterator.next();
  }
  
  @Override
  public boolean first() throws SQLException
  {
    //
    boolean directionForward = this.directionForward;
    
    //
    while ( this.rowIterator.nextIndex() <= 0 )
    {
      this.rowIterator.next();
    }
    
    //
    while ( this.rowIterator.previousIndex() >= 0 )
    {
      this.rowIterator.previous();
    }
    
    if ( directionForward )
    {
      this.rowIterator.next();
    }
    else
    {
      this.rowIterator.previous();
    }
    
    //
    return this.row != null;
  }
  
  @Override
  public boolean last() throws SQLException
  {
    //
    boolean directionForward = this.directionForward;
    
    //
    while ( this.rowIterator.previousIndex() >= 0 && !this.rowIterator.hasPrevious() )
    {
      this.rowIterator.previous();
    }
    this.rowIterator.previous();
    
    //
    while ( this.rowIterator.nextIndex() <= 0 && !this.rowIterator.hasNext() )
    {
      this.rowIterator.next();
    }
    
    //
    while ( this.rowIterator.hasPrevious() && !this.rowIterator.hasNext() )
    {
      this.rowIterator.previous();
    }
    
    //
    while ( this.rowIterator.hasNext() )
    {
      this.rowIterator.next();
    }
    
    //
    if ( !directionForward )
    {
      this.rowIterator.previous();
    }
    
    //
    return this.row != null;
  }
  
  @Override
  public int getRow() throws SQLException
  {
    return this.directionForward ? this.rowIterator.previousIndex() : this.rowIterator.nextIndex();
  }
  
  @Override
  public boolean absolute( int row ) throws SQLException
  {
    this.first();
    return this.relative( row );
  }
  
  @Override
  public boolean relative( int rows ) throws SQLException
  {
    //
    boolean retval = true;
    
    //
    for ( int ii = 0; ii < rows; ii++ )
    {
      retval = this.next();
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean previous() throws SQLException
  {
    //
    this.directionForward = false;
    
    //
    boolean retval = this.rowIterator != null && this.rowIterator.hasPrevious();
    
    //
    this.row = this.rowIterator != null ? this.rowIterator.previous() : null;
    
    //
    return retval;
  }
  
  @Override
  public void setFetchDirection( int direction ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int getFetchDirection() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void setFetchSize( int rows ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int getFetchSize() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int getType() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int getConcurrency() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public boolean rowUpdated() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public boolean rowInserted() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public boolean rowDeleted() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateNull( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBoolean( int columnIndex, boolean x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateByte( int columnIndex, byte x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateShort( int columnIndex, short x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateInt( int columnIndex, int x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateLong( int columnIndex, long x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateFloat( int columnIndex, float x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateDouble( int columnIndex, double x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBigDecimal( int columnIndex, BigDecimal x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateString( int columnIndex, String x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBytes( int columnIndex, byte[] x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateDate( int columnIndex, Date x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateTime( int columnIndex, Time x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateTimestamp( int columnIndex, Timestamp x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( int columnIndex, InputStream x, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( int columnIndex, InputStream x, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( int columnIndex, Reader x, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateObject( int columnIndex, Object x, int scaleOrLength ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateObject( int columnIndex, Object x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNull( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBoolean( String columnLabel, boolean x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateByte( String columnLabel, byte x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateShort( String columnLabel, short x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateInt( String columnLabel, int x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateLong( String columnLabel, long x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateFloat( String columnLabel, float x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateDouble( String columnLabel, double x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBigDecimal( String columnLabel, BigDecimal x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateString( String columnLabel, String x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBytes( String columnLabel, byte[] x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateDate( String columnLabel, Date x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateTime( String columnLabel, Time x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateTimestamp( String columnLabel, Timestamp x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( String columnLabel, InputStream x, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( String columnLabel, InputStream x, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( String columnLabel, Reader reader, int length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateObject( String columnLabel, Object x, int scaleOrLength ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateObject( String columnLabel, Object x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void insertRow() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateRow() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void deleteRow() throws SQLException
  {
    this.rowIterator.remove();
  }
  
  @Override
  public void refreshRow() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void cancelRowUpdates() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void moveToInsertRow() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void moveToCurrentRow() throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Statement getStatement() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Object getObject( int columnIndex, Map<String, Class<?>> map ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Ref getRef( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Blob getBlob( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Clob getClob( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Array getArray( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Object getObject( String columnLabel, Map<String, Class<?>> map ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Ref getRef( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Blob getBlob( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Clob getClob( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Array getArray( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Date getDate( int columnIndex, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Date getDate( String columnLabel, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Time getTime( int columnIndex, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Time getTime( String columnLabel, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Timestamp getTimestamp( int columnIndex, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Timestamp getTimestamp( String columnLabel, Calendar cal ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public URL getURL( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public URL getURL( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateRef( int columnIndex, Ref x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateRef( String columnLabel, Ref x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBlob( int columnIndex, Blob x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBlob( String columnLabel, Blob x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateClob( int columnIndex, Clob x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateClob( String columnLabel, Clob x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateArray( int columnIndex, Array x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateArray( String columnLabel, Array x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public RowId getRowId( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public RowId getRowId( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateRowId( int columnIndex, RowId x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateRowId( String columnLabel, RowId x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public int getHoldability() throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean isClosed() throws SQLException
  {
    return this.rowIterator == null;
  }
  
  @Override
  public void updateNString( int columnIndex, String nString ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateNString( String columnLabel, String nString ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNClob( int columnIndex, NClob nClob ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNClob( String columnLabel, NClob nClob ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public NClob getNClob( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public NClob getNClob( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public SQLXML getSQLXML( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public SQLXML getSQLXML( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateSQLXML( int columnIndex, SQLXML xmlObject ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateSQLXML( String columnLabel, SQLXML xmlObject ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public String getNString( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public String getNString( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Reader getNCharacterStream( int columnIndex ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public Reader getNCharacterStream( String columnLabel ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNCharacterStream( int columnIndex, Reader x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNCharacterStream( String columnLabel, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( int columnIndex, InputStream x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( int columnIndex, InputStream x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( int columnIndex, Reader x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( String columnLabel, InputStream x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( String columnLabel, InputStream x, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( String columnLabel, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBlob( int columnIndex, InputStream inputStream, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBlob( String columnLabel, InputStream inputStream, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateClob( int columnIndex, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateClob( String columnLabel, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNClob( int columnIndex, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNClob( String columnLabel, Reader reader, long length ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNCharacterStream( int columnIndex, Reader x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateNCharacterStream( String columnLabel, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( int columnIndex, InputStream x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( int columnIndex, InputStream x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( int columnIndex, Reader x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateAsciiStream( String columnLabel, InputStream x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateBinaryStream( String columnLabel, InputStream x ) throws SQLException
  {
    throw new UnsupportedOperationException();
    
  }
  
  @Override
  public void updateCharacterStream( String columnLabel, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateBlob( int columnIndex, InputStream inputStream ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateBlob( String columnLabel, InputStream inputStream ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateClob( int columnIndex, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateClob( String columnLabel, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateNClob( int columnIndex, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void updateNClob( String columnLabel, Reader reader ) throws SQLException
  {
    throw new UnsupportedOperationException();
  }
  
}
