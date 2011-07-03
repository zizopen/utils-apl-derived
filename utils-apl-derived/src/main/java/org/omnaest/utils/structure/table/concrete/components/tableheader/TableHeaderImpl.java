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
package org.omnaest.utils.structure.table.concrete.components.tableheader;

import org.omnaest.utils.structure.table.concrete.components.tableheader.title.TableTitle;
import org.omnaest.utils.structure.table.concrete.components.tableheader.title.TableTitleList;

/**
 * @see TableHeader
 * @see TableHeaderInternal
 * @see TableTitle
 * @see TableTitleList
 * @author Omnaest
 */
public class TableHeaderImpl implements TableHeaderInternal
{
  /* ********************************************** Variables ********************************************** */
  protected TableTitleList rowTitleList    = new TableTitleList();
  protected TableTitleList columnTitleList = new TableTitleList();
  
  /* ********************************************** Methods ********************************************** */

  @Override
  public void switchRowAndColumnTitles()
  {
    TableTitleList tableTitleList = this.rowTitleList;
    this.rowTitleList = this.columnTitleList;
    this.columnTitleList = tableTitleList;
  }
  
  @Override
  public TableTitleList getColumnTitleList()
  {
    return this.columnTitleList;
  }
  
  @Override
  public TableTitleList getRowTitleList()
  {
    return this.rowTitleList;
  }
  
  @Override
  public void setColumnTitleList( TableTitleList columnTitleList )
  {
    this.columnTitleList = columnTitleList;
  }
  
  @Override
  public void setRowTitleList( TableTitleList rowTitleList )
  {
    this.rowTitleList = rowTitleList;
  }
  
  @Override
  public TableHeaderInternal cloneDeep()
  {
    //
    TableHeaderImpl clone = new TableHeaderImpl();
    
    //
    clone.columnTitleList = this.columnTitleList.cloneDeep();
    clone.rowTitleList = this.rowTitleList.cloneDeep();
    
    //
    return clone;
  }
  
}
