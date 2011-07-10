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
package org.omnaest.utils.structure.table;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface adds methods which allows a {@link Table} to serialize into XML format and restore from there.
 * 
 * @author Omnaest
 * @param
 */
public interface TableXMLSerializable<E>
{
  /**
   * Returns the {@link Table} data as XML String content
   * 
   * @return this
   */
  public Table<E> writeAsXMLTo( Appendable appendable );
  
  /**
   * Writes the {@link Table} data as XML content to the given {@link OutputStream}
   * 
   * @param outputStream
   * @return this
   */
  public Table<E> writeAsXMLTo( OutputStream outputStream );
  
  /**
   * Parses the {@link Table} data from an XML {@link CharSequence}
   * 
   * @param charSequence
   * @return this
   */
  public Table<E> parseXMLFrom( CharSequence charSequence );
  
  /**
   * Parses the {@link Table} data from an XML String
   * 
   * @param xmlContent
   * @return this
   */
  public Table<E> parseXMLFrom( String xmlContent );
  
  /**
   * Parses the {@link Table} data from an XML {@link InputStream}
   * 
   * @param inputStream
   * @return this
   */
  public Table<E> parseXMLFrom( InputStream inputStream );
}
