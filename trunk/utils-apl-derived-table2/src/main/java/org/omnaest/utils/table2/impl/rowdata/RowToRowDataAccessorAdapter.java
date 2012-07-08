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
package org.omnaest.utils.table2.impl.rowdata;

import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.RowDataAccessor;

/**
 * @author Omnaest
 * @param <E>
 */
public final class RowToRowDataAccessorAdapter<E> implements RowDataAccessor<E>
{
  private final Row<E> row;
  
  public RowToRowDataAccessorAdapter( Row<E> row )
  {
    this.row = row;
  }
  
  @Override
  public void setElement( String columnTitle, E element )
  {
    this.row.setElement( columnTitle, element );
  }
  
  @Override
  public E getElement( String columnTitle )
  {
    return this.row.getElement( columnTitle );
  }
  
  @Override
  public E getElement( int columnIndex )
  {
    return this.row.getElement( columnIndex );
  }
  
  @Override
  public E[] getElements()
  {
    return this.row.getElements();
  }
  
  @Override
  public void setElement( int columnIndex, E element )
  {
    this.row.setElement( columnIndex, element );
  }
}
