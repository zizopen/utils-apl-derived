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

import java.io.InputStream;
import java.io.Reader;

import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer.Unmarshaller;
import org.omnaest.utils.table2.TableSerializer.UnmarshallerCsv;

/**
 * @see Unmarshaller
 * @author Omnaest
 * @param <E>
 */
class CsvUnmarshallerImpl<E> implements UnmarshallerCsv<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Table<E> table;
  
  /* *************************************************** Methods **************************************************** */
  
  public CsvUnmarshallerImpl( Table<E> table )
  {
    this.table = table;
  }
  
  @Override
  public Table<E> from( Reader reader )
  {
    // 
    return this.table;
  }
  
  @Override
  public Table<E> from( InputStream inputStream )
  {
    // 
    return this.table;
  }
  
  @Override
  public Table<E> from( Appendable appendable )
  {
    // 
    return this.table;
  }
  
  @Override
  public UnmarshallerCsv<E> using( UnmarshallerCsv.Configuration configuration )
  {
    // TODO Auto-generated method stub
    return this;
  }
  
}
