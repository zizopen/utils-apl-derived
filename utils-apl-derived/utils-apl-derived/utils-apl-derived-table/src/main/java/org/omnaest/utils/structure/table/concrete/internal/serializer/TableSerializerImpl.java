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
package org.omnaest.utils.structure.table.concrete.internal.serializer;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.internal.serializer.executor.TableMarshallerExecutorImpl;
import org.omnaest.utils.structure.table.concrete.internal.serializer.executor.TableUnmarshallerExecutorImpl;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see Table
 * @see TableSerializer
 * @see TableSerializable
 * @author Omnaest
 * @param <E>
 */
public class TableSerializerImpl<E> implements TableSerializer<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 5510796761896337708L;
  
  /* ********************************************** Variables ********************************************** */
  protected Table<E>        table            = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param table
   */
  public TableSerializerImpl( Table<E> table )
  {
    super();
    this.table = table;
  }
  
  @Override
  public TableMarshallerExecutor<E> marshal( TableMarshaller<E> tableMarshaller )
  {
    return new TableMarshallerExecutorImpl<E>( tableMarshaller, this.table );
  }
  
  @Override
  public TableUnmarshallerExecutor<E> unmarshal( TableUnmarshaller<E> tableUnmarshaller )
  {
    return new TableUnmarshallerExecutorImpl<E>( tableUnmarshaller, this.table );
  }
  
}
