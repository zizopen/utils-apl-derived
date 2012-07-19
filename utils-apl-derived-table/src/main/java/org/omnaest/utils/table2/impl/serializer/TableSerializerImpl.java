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
package org.omnaest.utils.table2.impl.serializer;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer;

/**
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public class TableSerializerImpl<E> implements TableSerializer<E>
{
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>         table;
  private final ExceptionHandler exceptionHandler;
  
  /* *************************************************** Methods **************************************************** */
  
  public TableSerializerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    this.table = table;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public UnmarshallerDeclarer<E> unmarshal()
  {
    final Table<E> table = this.table;
    final ExceptionHandler exceptionHandler = this.exceptionHandler;
    return new UnmarshallerDeclarer<E>()
    {
      @Override
      public UnmarshallerCsv<E> asCsv()
      {
        return new CsvUnmarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public UnmarshallerXml<E> asXml()
      {
        return new XmlUnmarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public UnmarshallerJson<E> asJson()
      {
        return new JsonUnmarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public UnmarshallerPlainText<E> asPlainText()
      {
        return new PlainTextUnmarshaller<E>( table, exceptionHandler );
      }
    };
  }
  
  @Override
  public MarshallerDeclarer<E> marshal()
  {
    final Table<E> table = this.table;
    final ExceptionHandler exceptionHandler = this.exceptionHandler;
    return new MarshallerDeclarer<E>()
    {
      @Override
      public MarshallerCsv<E> asCsv()
      {
        return new CsvMarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public MarshallerXml<E> asXml()
      {
        return new XmlMarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public MarshallerJson<E> asJson()
      {
        return new JsonMarshallerImpl<E>( table, exceptionHandler );
      }
      
      @Override
      public MarshallerPlainText<E> asPlainText()
      {
        return new PlainTextMarshaller<E>( table, exceptionHandler );
      }
    };
  }
  
}
