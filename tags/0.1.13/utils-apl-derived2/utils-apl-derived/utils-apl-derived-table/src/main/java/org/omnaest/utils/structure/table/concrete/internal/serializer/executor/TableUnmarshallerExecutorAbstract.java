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
package org.omnaest.utils.structure.table.concrete.internal.serializer.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer.TableUnmarshallerExecutor;

/**
 * @see TableUnmarshallerExecutor
 * @author Omnaest
 * @param <E>
 */
public abstract class TableUnmarshallerExecutorAbstract<E> implements TableUnmarshallerExecutor<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long      serialVersionUID  = -7174138083451290080L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableUnmarshaller<E> tableUnmarshaller = null;
  protected Table<E>             table             = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableUnmarshaller
   * @param table
   */
  public TableUnmarshallerExecutorAbstract( TableUnmarshaller<E> tableUnmarshaller, Table<E> table )
  {
    super();
    this.tableUnmarshaller = tableUnmarshaller;
    this.table = table;
  }
  
  @Override
  public Table<E> from( String string )
  {
    //
    if ( string != null )
    {
      //
      CharSequence charSequence = new StringBuffer( string );
      this.from( charSequence );
    }
    
    //
    return this.table;
  }
  
  @Override
  public Table<E> from( File file )
  {
    //
    if ( file != null && file.exists() )
    {
      //
      try
      {
        //
        InputStream inputStream = new FileInputStream( file );
        this.from( inputStream );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return this.table;
  }
  
  @Override
  public Table<E> from( URL url )
  {
    //
    if ( url != null )
    {
      try
      {
        //
        InputStream inputStream = url.openStream();
        this.from( inputStream );
      }
      catch ( IOException e )
      {
      }
    }
    
    // 
    return this.table;
  }
  
}
