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
package org.omnaest.utils.table.impl.rowdata;

import org.omnaest.utils.table.RowDataAccessor;
import org.omnaest.utils.table.RowDataReader;
import org.omnaest.utils.table.Table;

/**
 * @see RowDataReader
 * @author Omnaest
 * @param <E>
 */
public final class ElementsToRowDataReaderAdapter<E> implements RowDataAccessor<E>
{
  private final E[]      elements;
  private final Table<E> table;
  
  public ElementsToRowDataReaderAdapter( E[] elements, Table<E> table )
  {
    this.elements = elements;
    this.table = table;
  }
  
  @Override
  public void setElement( String columnTitle, E element )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public E getElement( String columnTitle )
  {
    int columnIndex = this.table.getColumnIndex( columnTitle );
    return this.elements[columnIndex];
  }
  
  @Override
  public void setElement( int columnIndex, E element )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public E getElement( int columnIndex )
  {
    return this.elements[columnIndex];
  }
  
  @Override
  public E[] getElements()
  {
    return this.elements;
  }
}
