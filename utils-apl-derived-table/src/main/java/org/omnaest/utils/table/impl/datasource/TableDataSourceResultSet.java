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
package org.omnaest.utils.table.impl.datasource;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.table.TableDataSource;

/**
 * {@link TableDataSource} for a given {@link ResultSet}
 * 
 * @param <E>
 * @author Omnaest
 */
public class TableDataSourceResultSet<E> implements TableDataSource<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long              serialVersionUID         = -9106875893967218350L;
  /* ********************************************** Variables ********************************************** */
  private final ResultSet                resultSet;
  private final Class<E>                 elementType;
  private final ExceptionHandlerDelegate exceptionHandlerDelegate = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TableDataSourceResultSet
   * @param resultSet
   * @param elementType
   */
  public TableDataSourceResultSet( ResultSet resultSet, Class<E> elementType )
  {
    super();
    this.resultSet = resultSet;
    this.elementType = elementType;
  }
  
  @Override
  public String getTableName()
  {
    //
    final int column = 1;
    try
    {
      return this.resultSet.getMetaData().getTableName( column );
    }
    catch ( SQLException e )
    {
      return null;
    }
  }
  
  @Override
  public String[] getColumnTitles()
  {
    final List<Object> retlist = new ArrayList<Object>();
    try
    {
      ResultSetMetaData metaData = this.resultSet.getMetaData();
      if ( metaData != null )
      {
        for ( int columnIndexPosition = 1; columnIndexPosition <= metaData.getColumnCount(); columnIndexPosition++ )
        {
          String columnName = metaData.getColumnName( columnIndexPosition );
          retlist.add( columnName );
        }
      }
    }
    catch ( Exception e )
    {
    }
    return retlist.toArray( new String[0] );
  }
  
  @Override
  public Iterable<E[]> rowElements()
  {
    final ResultSet resultSet = this.resultSet;
    final Class<E> elementType = this.elementType;
    final ExceptionHandler exceptionHandler = this.exceptionHandlerDelegate;
    return new Iterable<E[]>()
    {
      @Override
      public Iterator<E[]> iterator()
      {
        return new Iterator<E[]>()
        {
          @Override
          public boolean hasNext()
          {
            try
            {
              return resultSet.next();
            }
            catch ( SQLException e )
            {
              return false;
            }
          }
          
          @SuppressWarnings("unchecked")
          @Override
          public E[] next()
          {
            E[] retvals = null;
            try
            {
              final int columnCount = resultSet.getMetaData().getColumnCount();
              retvals = (E[]) Array.newInstance( elementType, columnCount );
              for ( int columnIndexPosition = 0; columnIndexPosition < columnCount; columnIndexPosition++ )
              {
                retvals[columnIndexPosition] = (E) resultSet.getObject( columnIndexPosition + 1 );
              }
            }
            catch ( SQLException e )
            {
              exceptionHandler.handleException( e );
            }
            return retvals;
          }
          
          @Override
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public void setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandlerDelegate.setExceptionHandler( exceptionHandler );
  }
}
