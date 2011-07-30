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

import java.io.InputStream;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer.TableUnmarshallerExecutor;

/**
 * @see TableUnmarshallerExecutor
 * @see TableUnmarshallerExecutorAbstract
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerExecutorImpl<E> extends TableUnmarshallerExecutorAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -7174138083451290080L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableUnmarshaller
   * @param table
   */
  public TableUnmarshallerExecutorImpl( TableUnmarshaller<E> tableUnmarshaller, Table<E> table )
  {
    super( tableUnmarshaller, table );
  }
  
  @Override
  public void from( InputStream inputStream )
  {
    //
    if ( this.tableUnmarshaller != null && this.table != null && inputStream != null )
    {
      //
      this.tableUnmarshaller.unmarshal( this.table, inputStream );
    }
  }
  
  @Override
  public void from( CharSequence charSequence )
  {
    //
    if ( this.tableUnmarshaller != null && this.table != null && charSequence != null )
    {
      //
      this.tableUnmarshaller.unmarshal( this.table, charSequence );
    }
  }
  
}
