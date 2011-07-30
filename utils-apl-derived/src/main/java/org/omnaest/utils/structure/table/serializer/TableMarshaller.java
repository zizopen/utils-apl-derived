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

import java.io.OutputStream;
import java.io.Serializable;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshallerFactory
 * @see TableUnmarshaller
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public interface TableMarshaller<E> extends Serializable
{
  
  /**
   * Marshals the given {@link Table} into an {@link OutputStream}
   * 
   * @param table
   * @param outputStream
   */
  public void marshal( Table<E> table, OutputStream outputStream );
  
  /**
   * Marshals and appends the given {@link Table} to the given {@link Appendable}
   * 
   * @param table
   * @param appendable
   */
  public void marshal( Table<E> table, Appendable appendable );
}
