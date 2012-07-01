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
package org.omnaest.utils.table2;

import java.io.InputStream;
import java.io.Reader;

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
    public Unmarshaller<E> asCsv();
    
    public Unmarshaller<E> asXml();
    
    public Unmarshaller<E> asJson();
    
    public Unmarshaller<E> asPlainText();
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
     * @param appendable
     *          {@link Appendable}
     * @return executing {@link Table} instance
     */
    public Table<E> from( Appendable appendable );
  }
  
  /**
   * {@link TableSerializer.Unmarshaller} interface specialized for csv operations
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface UnmarshallerCsv<E> extends Unmarshaller<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * {@link Configuration} for an {@link TableSerializer.UnmarshallerCsv}
     * 
     * @author Omnaest
     */
    public static class Configuration extends ImmutableTableSerializer.MarshallerCsv.Configuration
    {
      //TODO
    }
    
    /* *************************************************** Methods **************************************************** */
    /**
     * Makes the {@link TableSerializer.UnmarshallerCsv} using the given {@link Configuration}
     * 
     * @param configuration
     * @return this
     */
    public UnmarshallerCsv<E> using( UnmarshallerCsv.Configuration configuration );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public UnmarshallerDeclarer<E> unmarshal();
}
