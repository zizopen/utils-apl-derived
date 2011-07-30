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
package org.omnaest.utils.structure.table.serializer.marshaller;

import java.io.OutputStream;

import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.helper.TableHelper;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerPlainText<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 729579410301748875L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          encoding         = TableSerializer.DEFAULT_ENCODING_UTF8;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableMarshallerPlainText()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableMarshallerPlainText( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  @Override
  public void marshal( Table<E> table, OutputStream outputStream )
  {
    //
    if ( table != null && outputStream != null )
    {
      //
      StringBuffer stringBuffer = new StringBuffer();
      
      //
      this.marshal( table, stringBuffer );
      
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( stringBuffer );
      
      //
      byteArrayContainer.writeTo( outputStream );
    }
  }
  
  @Override
  public void marshal( Table<E> table, Appendable appendable )
  {
    //
    if ( table != null && appendable != null )
    {
      //
      String renderToString = TableHelper.renderToString( table );
      
      //
      try
      {
        //
        appendable.append( renderToString );
      }
      catch ( Exception e )
      {
      }
    }
    
  }
  
}
