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
package org.omnaest.utils.table.impl.serializer;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableSerializer.UnmarshallerXHtml;
import org.omnaest.utils.xml.XMLHelper;
import org.omnaest.utils.xml.XMLHelper.XSLTransformerConfiguration;

/**
 * {@link UnmarshallerXHtml} implementation
 * 
 * @see XmlMarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class XHtmlUnmarshallerImpl<E> extends XmlUnmarshallerImpl<E> implements UnmarshallerXHtml<E>
{
  
  XHtmlUnmarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @Override
  public Table<E> from( Reader reader )
  {
    try
    {
      final ByteArrayContainer byteArrayContainerSource = new ByteArrayContainer().copyFrom( reader );
      final String xhtmlContent = StringEscapeUtils.unescapeHtml4( byteArrayContainerSource.toString() );
      final Reader readerOfConvertedContent = new StringReader( xhtmlContent );
      
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final Writer writer = byteArrayContainer.getOutputStreamWriter();
      final InputStream inputStreamXSLT = XHtmlMarshallerImpl.class.getResourceAsStream( "/XHtmlToXml.xsl" );
      
      final StreamSource xslt = new StreamSource( inputStreamXSLT );
      final StreamSource xml = new StreamSource( readerOfConvertedContent );
      final StreamResult result = new StreamResult( writer );
      XMLHelper.transform( xslt, xml, result, this.exceptionHandler, new XSLTransformerConfiguration() );
      writer.close();
      
      super.from( byteArrayContainer.getReader( this.getEncoding() ) );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return this.table;
  }
}
