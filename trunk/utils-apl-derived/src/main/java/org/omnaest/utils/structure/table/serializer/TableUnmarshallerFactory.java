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

import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXML;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableSerializer
 * @see TableUnmarshaller
 * @see TableMarshallerFactory
 * @author Omnaest
 */
public abstract class TableUnmarshallerFactory
{
  /**
   * Creates new {@link TableUnmarshallerXML} instance using the {@link TableSerializer#DEFAULT_ENCODING_UTF8}
   * 
   * @see #XML(String)
   * @return
   */
  public static <E> TableUnmarshaller<E> XML()
  {
    return new TableUnmarshallerXML<E>();
  }
  
  /**
   * Creates new {@link TableUnmarshallerXML} instance using the given encoding
   * 
   * @see #XML()
   * @param encoding
   * @return
   */
  public static <E> TableUnmarshaller<E> XML( String encoding )
  {
    return new TableUnmarshallerXML<E>( encoding );
  }
}
