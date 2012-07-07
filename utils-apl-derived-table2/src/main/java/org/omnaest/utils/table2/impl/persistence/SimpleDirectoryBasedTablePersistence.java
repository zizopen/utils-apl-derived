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

import java.io.File;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.store.DirectoryBasedObjectStore;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table2.TablePersistence;
import org.omnaest.utils.tuple.KeyValue;

/**
 * Simple {@link TablePersistence} which writes the complete data to {@link File}s using a {@link DirectoryBasedObjectStore}. The
 * performance of this solution is very limited.
 * 
 * @author Omnaest
 * @param <E>
 */
public class SimpleDirectoryBasedTablePersistence<E> implements TablePersistence<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 1166090223383046706L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final List<E[]>   elementsList;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SimpleDirectoryBasedTablePersistence
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   */
  public SimpleDirectoryBasedTablePersistence( File baseDirectory, ExceptionHandlerSerializable exceptionHandler )
  {
    super();
    
    this.elementsList = new DirectoryBasedObjectStore<E[]>( baseDirectory, exceptionHandler );
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
