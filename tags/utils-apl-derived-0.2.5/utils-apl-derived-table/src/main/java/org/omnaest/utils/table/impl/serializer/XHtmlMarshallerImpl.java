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

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerXHtml;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.xml.XMLHelper;
import org.omnaest.utils.xml.XMLHelper.XSLTransformerConfiguration;

/**
 * {@link MarshallerXHtml} implementation
 * 
 * @see XmlUnmarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class XHtmlMarshallerImpl<E> extends XmlMarshallerImpl<E> implements MarshallerXHtml<E>
{
  
  XHtmlMarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @Override
  public Table<E> to( Appendable appendable )
  {
    try
    {
      final StringBuffer stringBuffer = new StringBuffer();
      super.to( stringBuffer );
      
      final Reader reader = new StringReader( stringBuffer.toString() );
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final Writer writer = byteArrayContainer.getOutputStreamWriter();
      final InputStream inputStreamXSLT = XHtmlMarshallerImpl.class.getResourceAsStream( "/XmlToXHtml.xsl" );
      
      final StreamSource xslt = new StreamSource( inputStreamXSLT );
      final StreamSource xml = new StreamSource( reader );
      final StreamResult result = new StreamResult( writer );
      XMLHelper.transform( xslt, xml, result, this.exceptionHandler,
                           new XSLTransformerConfiguration().addOutputProperty( "omit-xml-declaration", "yes" ) );
      writer.close();
      
      appendable.append( byteArrayContainer.toString( this.getEncoding() ) );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return this.table;
  }
  
}
