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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TablePersistence;
import org.omnaest.utils.tuple.KeyValue;

/**
 * Simple {@link TablePersistence} which writes the complete data to a {@link File} every time something changes.<br>
 * <br>
 * This implementation caches the data in memory to speed up the write through, but this is still very unperformant for larger
 * {@link Table}s<br>
 * <br>
 * It takes about <b>5-10 seconds</b> for about <b>1000 rows</b>.
 * 
 * @author Omnaest
 * @param <E>
 */
public class SimpleFileBasedTablePersistence<E> implements TablePersistence<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                  serialVersionUID = 4587018898135825772L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private List<E[]>                          elementsList     = new ArrayList<E[]>();
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final ExceptionHandlerSerializable exceptionHandler;
  private final File                         file;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SimpleFileBasedTablePersistence
   * @param file
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   */
  public SimpleFileBasedTablePersistence( File file, ExceptionHandlerSerializable exceptionHandler )
  {
    super();
    this.file = file;
    this.exceptionHandler = exceptionHandler;
    
    if ( file != null && file.exists() )
    {
      try
      {
        final byte[] byteArray = FileUtils.readFileToByteArray( file );
        @SuppressWarnings("unchecked")
        final List<E[]> deserializedList = (List<E[]>) SerializationUtils.deserialize( byteArray );
        if ( deserializedList != null )
        {
          this.elementsList.addAll( deserializedList );
        }
      }
      catch ( Exception e )
      {
        if ( this.exceptionHandler != null )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
  }
  
  @Override
  public void add( int id, E[] elements )
  {
    this.elementsList.add( id, elements );
    this.writeToFile();
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
    this.writeToFile();
  }
  
  @Override
  public void removeAll()
  {
    this.elementsList.clear();
    this.writeToFile();
  }
  
  @Override
  public void update( int id, E[] elements )
  {
    this.elementsList.set( id, elements );
    this.writeToFile();
  }
  
  private void writeToFile()
  {
    if ( this.file != null )
    {
      try
      {
        byte[] data = SerializationUtils.serialize( (Serializable) this.elementsList );
        FileUtils.writeByteArrayToFile( this.file, data );
      }
      catch ( Exception e )
      {
        if ( this.exceptionHandler != null )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
  }
  
}
