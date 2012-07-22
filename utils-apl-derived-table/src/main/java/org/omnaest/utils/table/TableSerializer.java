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
package org.omnaest.utils.table;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;

/**
 * A {@link TableSerializer} is used to marshal and unmarshal a {@link Table} instance into other forms like csv, xml or json
 * 
 * @see ImmutableTableSerializer
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface TableSerializer<E> extends ImmutableTableSerializer<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see TableSerializer
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerDeclarer<E>
  {
    /**
     * Returns a {@link TableSerializer.UnmarshallerCsv} instance
     * 
     * @return
     */
    public UnmarshallerCsv<E> asCsv();
    
    /**
     * Returns an {@link TableSerializer.UnmarshallerXml} instance
     * 
     * @return
     */
    public UnmarshallerXml<E> asXml();
    
    /**
     * Returns an {@link TableSerializer.UnmarshallerXml} instance
     * 
     * @return
     */
    public UnmarshallerXHtml<E> asXHtml();
    
    /**
     * Returns an {@link TableSerializer.UnmarshallerJson} instance
     * 
     * @return
     */
    public UnmarshallerJson<E> asJson();
    
    /**
     * Returns a {@link TableSerializer.UnmarshallerPlainText} instance
     * 
     * @return
     */
    public UnmarshallerPlainText<E> asPlainText();
  }
  
  /**
   * @see TableSerializer
   * @author Omnaest
   * @param <E>
   */
  public static interface Unmarshaller<E>
  {
    
    /**
     * @param reader
     *          {@link Reader}
     * @return executing {@link Table} instance
     */
    public Table<E> from( Reader reader );
    
    /**
     * @param inputStream
     *          {@link InputStream}
     * @return executing {@link Table} instance
     */
    public Table<E> from( InputStream inputStream );
    
    /**
     * @param charSequence
     *          {@link CharSequence}
     * @return executing {@link Table} instance
     */
    public Table<E> from( CharSequence charSequence );
    
    /**
     * @param file
     *          {@link File}
     * @return executing {@link Table} instance
     */
    public Table<E> from( File file );
    
    /**
     * @param url
     *          {@link URL}
     * @return executing {@link Table} instance
     */
    public Table<E> from( URL url );
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} for plain text
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerPlainText<E> extends Unmarshaller<E>
  {
    
    /**
     * Makes the {@link TableSerializer.UnmarshallerPlainText} using the given {@link MarshallingConfiguration}
     * 
     * @param configuration
     * @return this
     */
    public UnmarshallerPlainText<E> using( MarshallingConfiguration configuration );
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} for xml
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerXml<E> extends Unmarshaller<E>
  {
    
    /**
     * Makes the {@link TableSerializer.UnmarshallerPlainText} using the given {@link MarshallingConfiguration}
     * 
     * @param configuration
     * @return this
     */
    public UnmarshallerXml<E> using( MarshallingConfiguration configuration );
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} for XHTML
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerXHtml<E> extends UnmarshallerXml<E>
  {
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} for json
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerJson<E> extends Unmarshaller<E>
  {
    
    /**
     * Makes the {@link TableSerializer.UnmarshallerPlainText} using the given {@link MarshallingConfiguration}
     * 
     * @param configuration
     * @return this
     */
    public UnmarshallerJson<E> using( MarshallingConfiguration configuration );
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} interface specialized for csv operations
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerCsv<E> extends Unmarshaller<E>
  {
    
    /**
     * Makes the {@link TableSerializer.UnmarshallerCsv} using the given {@link CSVMarshallingConfiguration}
     * 
     * @param configuration
     * @return this
     */
    public UnmarshallerCsv<E> using( CSVMarshallingConfiguration configuration );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Returns a {@link UnmarshallerDeclarer} instance
   * 
   * @return
   */
  public UnmarshallerDeclarer<E> unmarshal();
}
