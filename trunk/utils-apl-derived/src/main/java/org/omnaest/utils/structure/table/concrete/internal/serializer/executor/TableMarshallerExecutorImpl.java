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

import java.io.OutputStream;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer.TableMarshallerExecutor;

/**
 * @see TableMarshallerExecutor
 * @see TableMarshallerExecutorAbstract
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerExecutorImpl<E> extends TableMarshallerExecutorAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 1032297869326007539L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param tableMarshaller
   * @param table
   */
  public TableMarshallerExecutorImpl( TableMarshaller<E> tableMarshaller, Table<E> table )
  {
    super( tableMarshaller, table );
  }
  
  @Override
  public void appendTo( Appendable appendable )
  {
    //
    if ( this.tableMarshaller != null && appendable != null )
    {
      //
      this.tableMarshaller.marshal( this.table, appendable );
    }
  }
  
  @Override
  public void writeTo( OutputStream outputStream )
  {
    //
    if ( this.tableMarshaller != null && outputStream != null )
    {
      //
      this.tableMarshaller.marshal( this.table, outputStream );
    }
  }
  
}
