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
package org.omnaest.utils.structure.table.subspecification;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.TableComponent;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;

/**
 * This interface adds methods which allows a {@link Table} to serialize into XML format and restore from there.
 * 
 * @author Omnaest
 * @param
 */
public interface TableSerializable<E>
{
  
  /**
   * A {@link TableSerializer} offers methods to {@link #marshal(TableMarshaller)} and {@link #unmarshal(TableUnmarshaller)} an
   * underlying {@link Table} instance into serializable format.
   * 
   * @see TableSerializable
   * @see TableMarshaller
   * @see TableUnmarshaller
   * @author Omnaest
   * @param <E>
   */
  public static interface TableSerializer<E> extends TableComponent
  {
    /* ********************************************** Constants ********************************************** */
    public static final String DEFAULT_ENCODING_UTF8 = "UTF-8";
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see TableSerializer
     * @author Omnaest
     * @param <E>
     */
    public static interface TableMarshallerExecutor<E> extends Serializable
    {
      /**
       * Returns the marshalled {@link Table} as {@link String}
       * 
       * @return
       */
      public String toString();
      
      /**
       * Appends the marshalled {@link Table} to an {@link Appendable}
       * 
       * @param appendable
       */
      public void appendTo( Appendable appendable );
      
      /**
       * Writes the marshalled {@link Table} content to the given {@link OutputStream}
       * 
       * @param outputStream
       */
      public void writeTo( OutputStream outputStream );
      
      /**
       * Writes the marshalled {@link Table} content to the given {@link File}
       * 
       * @param file
       */
      public void writeTo( File file );
      
      /**
       * Updates the given {@link InputStream} with an existing {@link Table} content and writes it to the given
       * {@link OutputStream}
       * 
       * @param inputStream
       * @param outputStream
       */
      public void updateTo( InputStream inputStream, OutputStream outputStream );
      
      /**
       * Updates the given {@link File} with an existing {@link Table} content and writes it back to the given {@link File}
       * 
       * @param file
       */
      public void updateTo( File file );
    }
    
    /**
     * @see TableSerializer
     * @author Omnaest
     * @param <E>
     */
    public static interface TableUnmarshallerExecutor<E> extends Serializable
    {
      
      /**
       * Reads a {@link CharSequence} and unmarshalls it into the underlying {@link Table}
       * 
       * @param charSequence
       * @return {@link Table}
       */
      public Table<E> from( CharSequence charSequence );
      
      /**
       * Reads a {@link String} and unmarshalls it into the underlying {@link Table}
       * 
       * @param string
       * @return {@link Table}
       */
      public Table<E> from( String string );
      
      /**
       * Reads a {@link InputStream} and unmarshalls it into the underlying {@link Table}
       * 
       * @param inputStream
       * @return {@link Table}
       */
      public Table<E> from( InputStream inputStream );
      
      /**
       * Reads the given {@link File} and unmarshalls it into the underlying {@link Table}
       * 
       * @param file
       * @return {@link Table}
       */
      public Table<E> from( File file );
      
      /**
       * Reads from the given {@link URL} and unmarshalls the content coming in.
       * 
       * @param url
       * @return {@link Table}
       */
      public Table<E> from( URL url );
      
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * Marshals the underlying {@link Table} into a serialized format
     * 
     * @param tableMarshaller
     * @return {@link TableMarshallerExecutor}
     */
    public TableMarshallerExecutor<E> marshal( TableMarshaller<E> tableMarshaller );
    
    /**
     * Clears the current {@link Table} content and fills it with unmarshalled content from a serialized format
     * 
     * @param tableUnmarshaller
     * @return {@link TableUnmarshallerExecutor}
     */
    public TableUnmarshallerExecutor<E> unmarshal( TableUnmarshaller<E> tableUnmarshaller );
    
  }
  
  /**
   * @see TableSerializer
   * @return
   */
  public TableSerializer<E> serializer();
  
}
