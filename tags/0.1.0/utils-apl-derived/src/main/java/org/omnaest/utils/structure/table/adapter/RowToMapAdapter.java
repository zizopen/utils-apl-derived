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
package org.omnaest.utils.structure.table.adapter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.map.MapAbstract;
import org.omnaest.utils.structure.table.Table.Row;

/**
 * Adapter to use a given {@link Row}s as {@link Map}
 * 
 * @author Omnaest
 * @param <E>
 */
public class RowToMapAdapter<E> extends MapAbstract<Object, E>
{
  /* ********************************************** Variables ********************************************** */
  protected Row<E> row = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param row
   */
  public RowToMapAdapter( Row<E> row )
  {
    super();
    this.row = row;
  }
  
  @Override
  public E get( Object key )
  {
    // 
    return this.row.getCellElement( key );
  }
  
  @Override
  public E put( Object key, E value )
  {
    //
    E retval = this.get( key );
    
    //
    this.row.setCellElement( key, value );
    
    return retval;
  }
  
  @Override
  public E remove( Object key )
  {
    return this.put( key, null );
  }
  
  @Override
  public Set<Object> keySet()
  {
    return new LinkedHashSet<Object>( this.row.getColumnTitleValueList() );
  }
  
  @Override
  public Collection<E> values()
  {
    return this.row.getCellElementList();
  }
  
}
