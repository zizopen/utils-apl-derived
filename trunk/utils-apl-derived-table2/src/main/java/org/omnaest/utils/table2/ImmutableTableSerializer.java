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

import java.io.OutputStream;
import java.io.Writer;

/**
 * Immutable {@link TableSerializer}
 * 
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableTableSerializer<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see ImmutableTableSerializer
   * @see ImmutableTableSerializer.Marshaller
   * @author Omnaest
   * @param <E>
   */
  public static interface MarshallerDeclarer<E>
  {
    /**
     * @return {@link ImmutableTableSerializer.Marshaller}
     */
    public Marshaller<E> asCsv();
    
    /**
     * @return {@link ImmutableTableSerializer.Marshaller}
     */
    public Marshaller<E> asXml();
    
    /**
     * @return {@link ImmutableTableSerializer.Marshaller}
     */
    public Marshaller<E> asJson();
    
    /**
     * @return {@link ImmutableTableSerializer.Marshaller}
     */
    public Marshaller<E> asPlainText();
  }
  
  /**
   * @see ImmutableTableSerializer
   * @see ImmutableTableSerializer.MarshallerDeclarer
   * @author Omnaest
   * @param <E>
   */
  public static interface Marshaller<E>
  {
    
    /**
     * @param writer
     *          {@link Writer}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( Writer writer );
    
    /**
     * @param outputStream
     *          {@link OutputStream}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( OutputStream outputStream );
    
    /**
     * @param appendable
     *          {@link Appendable}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( Appendable appendable );
    
  }
  
  public static interface MarshallerCsv<E> extends Marshaller<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * {@link Configuration} for an {@link ImmutableTableSerializer.MarshallerCsv}
     * 
     * @author Omnaest
     */
    public static class Configuration
    {
      //TODO
    }
    
    /* *************************************************** Methods **************************************************** */
    /**
     * Makes the {@link ImmutableTableSerializer.Marshaller} using the given {@link Configuration}
     * 
     * @param configuration
     *          {@link Configuration}
     * @return this
     */
    public MarshallerCsv<E> using( Configuration configuration );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Allows to marshal the executing {@link Table} into another format like Xml, Json or Csv
   * 
   * @return {@link ImmutableTableSerializer.MarshallerDeclarer}
   */
  public MarshallerDeclarer<E> marshal();
}
