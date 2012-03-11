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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omnaest.utils.structure.table.Table.CellImmutable;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * {@link TableDataSource} for a given {@link Map}
 * 
 * @param <E>
 * @author Omnaest
 */
public class TableDataSourceMap<E> implements TableDataSource<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<?, ?> map              = null;
  protected Object    columnTitleKey   = null;
  protected Object    columnTitleValue = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param map
   * @param columnTitleKey
   * @param columnTitleValue
   */
  public TableDataSourceMap( Map<?, ?> map, Object columnTitleKey, Object columnTitleValue )
  {
    super();
    this.map = map;
    this.columnTitleKey = columnTitleKey;
    this.columnTitleValue = columnTitleValue;
  }
  
  @Override
  public Iterable<? extends Iterable<? extends CellImmutable<E>>> rows()
  {
    //
    final Iterator<?> iteratorEntrySet = this.map.entrySet().iterator();
    
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
            return iteratorEntrySet.hasNext();
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
                  protected int         columnIndexPosition = 0;
                  protected Entry<?, ?> entry               = (Entry<?, ?>) iteratorEntrySet.next();
                  
                  /* ********************************************** Methods ********************************************** */
                  
                  @Override
                  public boolean hasNext()
                  {
                    return this.columnIndexPosition <= 1;
                  }
                  
                  @Override
                  public CellImmutable<E> next()
                  {
                    //
                    final int columnIndexPosition = this.columnIndexPosition++;
                    final Entry<?, ?> entry = this.entry;
                    
                    //
                    return new CellImmutable<E>()
                    {
                      @SuppressWarnings("unchecked")
                      @Override
                      public E getElement()
                      {
                        //
                        E retval = null;
                        
                        //
                        if ( columnIndexPosition == 0 )
                        {
                          retval = (E) entry.getKey();
                        }
                        else if ( columnIndexPosition == 1 )
                        {
                          retval = (E) entry.getValue();
                        }
                        
                        //
                        return retval;
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
    retlist.add( this.columnTitleKey );
    retlist.add( this.columnTitleValue );
    
    // 
    return retlist;
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return new ArrayList<Object>();
  }
  
  @Override
  public Object getTableName()
  {
    return null;
  }
  
}
