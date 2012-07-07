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
package org.omnaest.utils.table2.impl.persistence;

import java.util.List;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table2.TablePersistence;
import org.omnaest.utils.tuple.KeyValue;

/**
 * Abstract {@link TablePersistence}
 * 
 * @author Omnaest
 * @param <E>
 */
abstract class SimpleDirectoryBasedTablePersistenceAbstract<E> implements TablePersistence<E>
{
  
  private static final long serialVersionUID = 3838780633578772811L;
  protected final List<E[]> elementsList;
  
  public SimpleDirectoryBasedTablePersistenceAbstract( List<E[]> elementsList )
  {
    super();
    this.elementsList = elementsList;
  }
  
  @Override
  public void add( int id, E[] elements )
  {
    this.elementsList.add( id, elements );
  }
  
  @Override
  public Iterable<KeyValue<Integer, E[]>> allElements()
  {
    return ListUtils.convert( this.elementsList, new ElementConverter<E[], KeyValue<Integer, E[]>>()
    {
      private int index = 0;
      
      @Override
      public KeyValue<Integer, E[]> convert( E[] elements )
      {
        final Integer key = this.index++;
        final E[] value = elements;
        return new KeyValue<Integer, E[]>( key, value );
      }
    } );
  }
  
  @Override
  public void remove( int id )
  {
    this.elementsList.remove( id );
  }
  
  @Override
  public void removeAll()
  {
    this.elementsList.clear();
  }
  
  @Override
  public void update( int id, E[] elements )
  {
    this.elementsList.set( id, elements );
  }
  
}
