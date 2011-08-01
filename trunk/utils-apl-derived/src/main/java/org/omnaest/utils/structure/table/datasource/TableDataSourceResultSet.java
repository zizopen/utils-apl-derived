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
package org.omnaest.utils.structure.table.datasource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.table.Table.CellImmutable;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * {@link TableDataSource} for a given {@link ResultSet}
 * 
 * @param <E>
 * @author Omnaest
 */
public class TableDataSourceResultSet<E> implements TableDataSource<E>
{
  /* ********************************************** Variables ********************************************** */
  protected ResultSet resultSet = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param resultSet
   */
  public TableDataSourceResultSet( ResultSet resultSet )
  {
    super();
    this.resultSet = resultSet;
  }
  
  @Override
  public Iterable<? extends Iterable<? extends CellImmutable<E>>> rows()
  {
    //
    final ResultSet resultSet = this.resultSet;
    
    //
    return new Iterable<Iterable<? extends CellImmutable<E>>>()
    {
      @Override
      public Iterator<Iterable<? extends CellImmutable<E>>> iterator()
      {
        return new Iterator<Iterable<? extends CellImmutable<E>>>()
        {
          @Override
          public boolean hasNext()
          {
            try
            {
              //
              return resultSet.next();
            }
            catch ( SQLException e )
            {
              return false;
            }
          }
          
          @Override
          public Iterable<? extends CellImmutable<E>> next()
          {
            return new Iterable<CellImmutable<E>>()
            {
              @Override
              public Iterator<CellImmutable<E>> iterator()
              {
                return new Iterator<CellImmutable<E>>()
                {
                  /* ********************************************** Variables ********************************************** */
                  protected int columnIndexPosition = 0;
                  
                  /* ********************************************** Methods ********************************************** */
                  
                  @Override
                  public boolean hasNext()
                  {
                    try
                    {
                      return this.columnIndexPosition < resultSet.getMetaData().getColumnCount();
                    }
                    catch ( SQLException e )
                    {
                      return false;
                    }
                  }
                  
                  @Override
                  public CellImmutable<E> next()
                  {
                    //
                    final int columnIndexPosition = this.columnIndexPosition++;
                    
                    //
                    return new CellImmutable<E>()
                    {
                      @SuppressWarnings("unchecked")
                      @Override
                      public E getElement()
                      {
                        //
                        try
                        {
                          return (E) resultSet.getObject( columnIndexPosition );
                        }
                        catch ( SQLException e )
                        {
                          return null;
                        }
                      }
                      
                      @Override
                      public boolean hasElement( E element )
                      {
                        //
                        E elementThis = this.getElement();
                        return element == elementThis || ( elementThis != null && elementThis.equals( element ) );
                      }
                    };
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
          
          @Override
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    try
    {
      //
      ResultSetMetaData metaData = this.resultSet.getMetaData();
      if ( metaData != null )
      {
        for ( int columnIndexPosition = 0; columnIndexPosition < metaData.getColumnCount(); columnIndexPosition++ )
        {
          //
          String columnName = metaData.getColumnName( columnIndexPosition );
          retlist.add( columnName );
        }
      }
    }
    catch ( Exception e )
    {
    }
    
    // 
    return retlist;
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    try
    {
      //
      int rowIndexPositionSaved = this.resultSet.getRow();
      this.resultSet.beforeFirst();
      
      //
      while ( this.resultSet.next() )
      {
        //
        String cursorName = this.resultSet.getCursorName();
        retlist.add( cursorName );
      }
      
      //
      this.resultSet.absolute( rowIndexPositionSaved );
    }
    catch ( Exception e )
    {
    }
    
    //
    return retlist;
  }
  
  @Override
  public Object getTableName()
  {
    //
    final int column = 0;
    try
    {
      return this.resultSet.getMetaData().getTableName( column );
    }
    catch ( SQLException e )
    {
      return null;
    }
  }
  
}
