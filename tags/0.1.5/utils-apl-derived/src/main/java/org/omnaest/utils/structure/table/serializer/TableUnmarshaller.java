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
package org.omnaest.utils.structure.table.serializer;

import java.io.InputStream;
import java.io.Serializable;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshaller
 * @see TableMarshallerFactory
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public interface TableUnmarshaller<E> extends Serializable
{
  
  /**
   * Unmarshals the given {@link Table} from an {@link InputStream}. The given {@link Table} instance is cleared before the
   * unmarshal process.
   * 
   * @param table
   * @param inputStream
   */
  public void unmarshal( Table<E> table, InputStream inputStream );
  
  /**
   * Unmarshalls the given {@link Table} instance from a given {@link CharSequence}. This will clean the {@link Table} instance
   * before.
   * 
   * @see #unmarshal(Table, CharSequence, String)
   * @param table
   * @param charSequence
   */
  public void unmarshal( Table<E> table, CharSequence charSequence );
  
}
