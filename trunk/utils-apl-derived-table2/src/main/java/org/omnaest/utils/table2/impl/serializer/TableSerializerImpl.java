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

import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer;

/**
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public class TableSerializerImpl<E> implements TableSerializer<E>
{
  
  private final Table<E> table;
  
  public TableSerializerImpl( Table<E> table )
  {
    this.table = table;
  }
  
  @Override
  public UnmarshallerDeclarer<E> unmarshal()
  {
    final Table<E> table = this.table;
    return new UnmarshallerDeclarer<E>()
    {
      @Override
      public Unmarshaller<E> asCsv()
      {
        return new CsvUnmarshallerImpl<E>( table );
      }
      
      @Override
      public Unmarshaller<E> asXml()
      {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Unmarshaller<E> asJson()
      {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Unmarshaller<E> asPlainText()
      {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
  
  @Override
  public MarshallerDeclarer<E> marshal()
  {
    final Table<E> table = this.table;
    return new MarshallerDeclarer<E>()
    {
      @Override
      public MarshallerCsv<E> asCsv()
      {
        return new CsvMarshallerImpl<E>( table );
      }
      
      @Override
      public Marshaller<E> asXml()
      {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Marshaller<E> asJson()
      {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Marshaller<E> asPlainText()
      {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
  
}
