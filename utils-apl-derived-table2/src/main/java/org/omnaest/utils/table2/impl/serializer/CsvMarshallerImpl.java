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

import java.io.OutputStream;
import java.io.Writer;

import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerCsv;
import org.omnaest.utils.table2.Table;

/**
 * {@link Marshaller} for csv
 * 
 * @author Omnaest
 * @param <E>
 */
class CsvMarshallerImpl<E> implements MarshallerCsv<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Table<E> table;
  
  /* *************************************************** Methods **************************************************** */
  
  public CsvMarshallerImpl( Table<E> table )
  {
    this.table = table;
  }
  
  @Override
  public Table<E> to( Writer writer )
  {
    // TODO Auto-generated method stub
    return this.table;
  }
  
  @Override
  public Table<E> to( OutputStream outputStream )
  {
    // TODO Auto-generated method stub
    return this.table;
  }
  
  @Override
  public Table<E> to( Appendable appendable )
  {
    // 
    return this.table;
  }
  
  @Override
  public MarshallerCsv<E> using( MarshallerCsv.Configuration configuration )
  {
    // TODO Auto-generated method stub
    return this;
  }
  
}
