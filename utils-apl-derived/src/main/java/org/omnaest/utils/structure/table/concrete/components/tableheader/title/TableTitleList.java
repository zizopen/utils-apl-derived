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
package org.omnaest.utils.structure.table.concrete.components.tableheader.title;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.CloneableDeep;

public class TableTitleList implements Iterable<TableTitle>, CloneableDeep<TableTitleList>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 4214421880642173588L;
  
  /* ********************************************** Variables ********************************************** */

  protected List<TableTitle> tableTitleList   = new ArrayList<TableTitle>();
  
  /* ********************************************** Methods ********************************************** */
  public boolean isEmpty()
  {
    return this.tableTitleList.isEmpty();
  }
  
  public boolean contains( Object o )
  {
    return this.tableTitleList.contains( o );
  }
  
  public Iterator<TableTitle> iterator()
  {
    return this.tableTitleList.iterator();
  }
  
  public boolean add( TableTitle tableTitle )
  {
    //
    boolean retval = false;
    
    //
    if ( tableTitle != null )
    {
      //
      retval = this.tableTitleList.add( tableTitle );
      
      //
      tableTitle.setTableTitleList( this );
    }
    
    //
    return retval;
  }
  
  public boolean remove( Object o )
  {
    return this.tableTitleList.remove( o );
  }
  
  public void clear()
  {
    this.tableTitleList.clear();
  }
  
  public TableTitle get( int index )
  {
    return this.tableTitleList.get( index );
  }
  
  public void add( int index, TableTitle element )
  {
    this.tableTitleList.add( index, element );
  }
  
  public TableTitle remove( int index )
  {
    return this.tableTitleList.remove( index );
  }
  
  public int indexOf( Object o )
  {
    return this.tableTitleList.indexOf( o );
  }
  
  @Override
  public TableTitleList cloneDeep()
  {
    //
    TableTitleList clone = new TableTitleList();
    
    //
    for ( TableTitle tableTitle : this )
    {
      clone.add( tableTitle.cloneDeep() );
    }
    
    //
    return clone;
  }
  
}
