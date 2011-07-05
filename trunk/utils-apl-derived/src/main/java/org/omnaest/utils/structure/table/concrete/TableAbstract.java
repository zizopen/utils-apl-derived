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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements TableInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 413819072598819746L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public abstract Table<E> clone();
  
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
  
}
