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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer.TableMarshallerExecutor;

/**
 * @see TableMarshallerExecutor
 * @author Omnaest
 * @param <E>
 */
public abstract class TableMarshallerExecutorAbstract<E> implements TableMarshallerExecutor<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long    serialVersionUID = 1032297869326007539L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableMarshaller<E> tableMarshaller  = null;
  protected Table<E>           table            = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableMarshaller
   * @param table
   */
  public TableMarshallerExecutorAbstract( TableMarshaller<E> tableMarshaller, Table<E> table )
  {
    super();
    this.tableMarshaller = tableMarshaller;
    this.table = table;
  }
  
  @Override
  public String toString()
  {
    //
    StringBuilder stringBuilder = new StringBuilder();
    this.appendTo( stringBuilder );
    
    //
    return stringBuilder.toString();
  }
  
}
