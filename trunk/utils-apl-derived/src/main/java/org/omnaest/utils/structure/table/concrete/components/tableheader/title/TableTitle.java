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

import org.omnaest.utils.structure.CloneableDeep;
import org.omnaest.utils.structure.table.concrete.components.tableheader.TableHeader;

/**
 * @see TableHeader
 * @see TableTitleList
 * @author Omnaest
 */
public class TableTitle implements CloneableDeep<TableTitle>
{
  /* ********************************************** Variables ********************************************** */
  protected Object         identifier     = null;
  protected TableTitleList tableTitleList = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new {@link TableTitle} instance
   */
  public TableTitle()
  {
    super();
  }
  
  /**
   * Determines the index position of this {@link TableTitleList} within its {@link TableTitleList}.
   * 
   * @return
   */
  public int determineIndexPosition()
  {
    //
    int retval = -1;
    
    //
    if ( this.tableTitleList != null )
    {
      retval = this.tableTitleList.indexOf( this );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the identifiier of the {@link TableTitle} as {@link String}
   * 
   * @return
   */
  public String getIdentifierAsString()
  {
    //
    String retval = null;
    
    //
    try
    {
      retval = String.valueOf( this.getIdentifier() );
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
  
  public Object getIdentifier()
  {
    return this.identifier;
  }
  
  public void setIdentifier( Object identifier )
  {
    this.identifier = identifier;
  }
  
  protected void setTableTitleList( TableTitleList tableTitleList )
  {
    this.tableTitleList = tableTitleList;
  }
  
  @Override
  public TableTitle cloneDeep()
  {
    //
    TableTitle clone = new TableTitle();
    clone.identifier = this.identifier;
    
    //
    return clone;
  }
  
}
