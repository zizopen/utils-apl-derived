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
package org.omnaest.utils.xml;

import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * The {@link XMLNestedMapConverter} allows to convert xml content into a nested {@link Map} hierarchy. <br>
 * <br>
 * <h1>Example:</h1><br>
 * The following xml snippet:
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;Books &gt;
 *     &lt;header&gt;
 *         &lt;metainfo&gt;Some meta information&lt;/metainfo&gt;
 *     &lt;/header&gt;
 *     &lt;Book&gt;
 *         &lt;Title&gt;Simple title&lt;/Title&gt;
 *         &lt;author &gt;
 *             an author
 *         &lt;/author&gt;
 *     &lt;/Book&gt;
 *     &lt;Book&gt;
 *         &lt;Title&gt;Second&lt;![CDATA[ simple]]&gt;
 *             title
 *         &lt;/Title&gt;
 *         &lt;Author &gt;Second author&lt;/Author&gt;
 *     &lt;/Book&gt;
 * &lt;/Books&gt;
 * </pre>
 * 
 * will be converted into following nested map hierarchy:
 * 
 * <pre>
 * -+
 *  |-+ Books
 *  | |-+ header
 *  | | |-- metainfo=Some meta information
 *  | |
 *  | |-- Book=[{Title=Simple title, author=
 *             an author
 *         }, {Title=Second simple
 *             title
 *         , Author=Second author}]
 *  |
 * 
 * </pre>
 * 
 * (All '+' nodes represents a new {@link Map} object)<br>
 * <br>
 * Note: the two similar named 'book' xml tags are <b>merged</b> into their parental {@link Map} using a single shared key but
 * instead of simple primitive values the key points to a {@link List} of {@link Object}s.
 * 
 * @see #newMapFromXML(CharSequence)
 * @see #newNamespaceAwareMapFromXML(CharSequence)
 * @see #setExceptionHandler(ExceptionHandler)
 * @author Omnaest
 */
public class XMLNestedMapConverter
{
  /* ********************************************** Variables ********************************************** */
  private ExceptionHandler exceptionHandler = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates nested {@link Map} instances which contains all information from the given xml content as key value pairs.<br>
   * <br>
   * {@link QName}s of the tag elements do represent the keys of the returned {@link Map}. <br>
   * Values are {@link String}s, {@link List}s or {@link Map}s. <br>
   * <br>
   * Attributes are mapped to key value pairs as they are. Sub tags containing only text data will be converted to key value pairs
   * using their tag name as key and the textual information as value.
   * 
   * @see XMLNestedMapConverter
   * @see #newMapFromXML(CharSequence)
   * @param xmlContent
   * @return new (nested) {@link Map} instance
   */
  public Map<QName, Object> newNamespaceAwareMapFromXML( CharSequence xmlContent )
  {
    //
    final ElementConverter<QName, QName> keyElementConverter = new ElementConverterIdentitiyCast<QName, QName>();
    return this.newMapFromXML( xmlContent, keyElementConverter );
  }
  
  /**
   * Similar to {@link #newNamespaceAwareMapFromXML(CharSequence)} but with non {@link Namespace} aware {@link String} values as
   * keys. Those keys will only contain the tag name without any {@link Namespace} information.
   * 
   * @see #newNamespaceAwareMapFromXML(CharSequence)
   * @param xmlContent
   * @return new (nested) {@link Map} instance
   */
  public Map<String, Object> newMapFromXML( CharSequence xmlContent )
  {
    //
    final ElementConverter<QName, String> keyElementConverter = new ElementConverter<QName, String>()
    {
      @Override
      public String convert( QName element )
      {
        // 
        return element.getLocalPart();
      }
    };
    return this.newMapFromXML( xmlContent, keyElementConverter );
  }
  
  /**
   * Template method for {@link #newNamespaceAwareMapFromXML(CharSequence)} and {@link #newMapFromXML(CharSequence)} which allows
   * to convert the {@link QName} based key values to other representations.
   * 
   * @param xmlContent
   * @return new (nested) {@link Map} instance
   */
  protected <K> Map<K, Object> newMapFromXML( CharSequence xmlContent, final ElementConverter<QName, K> keyElementConverter )
  {
    //
    final Map<K, Object> retmap = new LinkedHashMap<K, Object>();
    
    //
    Assert.isNotNull( keyElementConverter, "keyElementConverter must not be null" );
    
    //
    final ExceptionHandler exceptionHandler = this.exceptionHandler;
    
    //    
    try
    {
      //
      final Reader reader = new CharSequenceReader( xmlContent );
      final XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader( reader );
      
      //
      final class Helper
      {
        /* ********************************************** Variables ********************************************** */
        private List<TupleTwo<QName, Object>> stackList = new ArrayList<TupleTwo<QName, Object>>();
        
        /* ********************************************** Methods ********************************************** */
        
        /**
         * Manifests a single tag node recursively
         * 
         * @return
         * @throws XMLStreamException
         */
        @SuppressWarnings("unchecked")
        public TupleTwo<QName, Object> manifest() throws XMLStreamException
        {
          //
          TupleTwo<QName, Object> retval = null;
          
          //          
          while ( xmlEventReader.hasNext() )
          {
            //
            final XMLEvent xmlEvent = xmlEventReader.nextEvent();
            
            //
            if ( xmlEvent.isStartElement() )
            {
              //
              final StartElement startElement = xmlEvent.asStartElement();
              final QName name = startElement.getName();
              
              //
              this.addNewStackElement().setValueFirst( name );
              
              //
              final Iterator<Attribute> attributeIterator = startElement.getAttributes();
              if ( attributeIterator.hasNext() )
              {
                //
                final Map<QName, Object> map = new LinkedHashMap<QName, Object>();
                for ( Attribute attribute : IterableUtils.valueOf( attributeIterator ) )
                {
                  map.put( attribute.getName(), attribute.getValue() );
                }
                
                //
                this.updateCurrentStackValue( map );
              }
            }
            else if ( xmlEvent.isEndElement() )
            {
              //
              retval = this.removeStackElement();
              
              //
              final Object manifestation = retval.getValueSecond();
              final QName tagname = retval.getValueFirst();
              
              //
              updateCurrentStackValue( manifestation, tagname );
            }
            else if ( xmlEvent.isCharacters() )
            {
              //
              final Characters characters = xmlEvent.asCharacters();
              if ( !characters.isWhiteSpace() )
              {
                //
                final TupleTwo<QName, Object> currentStackValue = this.getCurrentStackValue();
                currentStackValue.setValueSecond( ObjectUtils.defaultIfNull( currentStackValue.getValueSecond(), "" )
                                                  + characters.getData() );
                
              }
            }
            
          }
          
          //
          return retval;
        }
        
        /**
         * Updates the current stack value
         * 
         * @param manifestation
         * @param tagname
         */
        private void updateCurrentStackValue( Object manifestation, QName tagname )
        {
          //
          final Map<QName, Object> tagNameToManifestationMap = new LinkedHashMap<QName, Object>();
          tagNameToManifestationMap.put( tagname, manifestation );
          this.updateCurrentStackValue( tagNameToManifestationMap );
        }
        
        @SuppressWarnings("unchecked")
        private void updateCurrentStackValue( Map<QName, Object> tagNameToManifestationMap )
        {
          //
          final TupleTwo<QName, Object> currentStackValue = this.getCurrentStackValue();
          
          //
          if ( currentStackValue != null )
          {
            //
            Map<K, Object> map = null;
            {
              //
              final Object valueSecond = currentStackValue.getValueSecond();
              if ( valueSecond instanceof Map )
              {
                map = (Map<K, Object>) valueSecond;
              }
              else
              {
                //
                map = new LinkedHashMap<K, Object>();
                if ( valueSecond instanceof String )
                {
                  map.put( keyElementConverter.convert( new QName( "" ) ), valueSecond );
                }
              }
            }
            
            //
            for ( Entry<QName, Object> tagNameToManifestationEntry : tagNameToManifestationMap.entrySet() )
            {
              //
              final K tagname = keyElementConverter.convert( tagNameToManifestationEntry.getKey() );
              final Object manifestation = tagNameToManifestationEntry.getValue();
              
              //
              if ( !map.containsKey( tagname ) )
              {
                map.put( tagname, manifestation );
              }
              else
              {
                //
                final Object object = map.get( tagname );
                if ( object instanceof List )
                {
                  //
                  final List<Object> list = (List<Object>) object;
                  list.add( manifestation );
                }
                else
                {
                  //
                  final List<Object> list = new ArrayList<Object>();
                  list.add( object );
                  list.add( manifestation );
                  map.put( tagname, list );
                }
              }
            }
            
            //
            currentStackValue.setValueSecond( map );
          }
        }
        
        private TupleTwo<QName, Object> getCurrentStackValue()
        {
          return ListUtils.firstElement( this.stackList );
        }
        
        private TupleTwo<QName, Object> removeStackElement()
        {
          return ListUtils.removeFirst( this.stackList );
        }
        
        private TupleTwo<QName, Object> addNewStackElement()
        {
          //
          final TupleTwo<QName, Object> retval = new TupleTwo<QName, Object>();
          this.stackList.add( 0, retval );
          return retval;
        }
      }
      
      //  
      try
      {
        final Helper helper = new Helper();
        final TupleTwo<QName, Object> result = helper.manifest();
        retmap.put( keyElementConverter.convert( result.getValueFirst() ), result.getValueSecond() );
      }
      catch ( Exception e )
      {
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
      }
      
      //
      xmlEventReader.close();
      reader.close();
      
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns the xml content equivalent to the given nested {@link Map} structure
   * 
   * @see XMLNestedMapConverter
   * @see #toNamespaceAwareXML(Map)
   * @param nestedMap
   *          {@link Map}
   * @return xml content
   */
  public String toXML( Map<String, Object> nestedMap )
  {
    //
    String retval = null;
    
    //
    try
    {
      //    
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final OutputStream outputStream = byteArrayContainer.getOutputStream();
      this.toXML( nestedMap, outputStream );
      retval = byteArrayContainer.toString();
    }
    catch ( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Similar to {@link #toXML(Map)} but for {@link Map} instances having a {@link QName} based key type
   * 
   * @see XMLNestedMapConverter
   * @see #toXML(Map)
   * @param nestedMap
   *          {@link Map}
   * @return xml content
   */
  public String toNamespaceAwareXML( Map<QName, Object> nestedMap )
  {
    //
    String retval = null;
    
    //
    try
    {
      //
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final OutputStream outputStream = byteArrayContainer.getOutputStream();
      this.toNamespaceAwareXML( nestedMap, outputStream );
      retval = byteArrayContainer.toString();
    }
    catch ( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Similar to {@link #toXMLDocument(Map, OutputStream)} but returns a {@link String} instance instead of writing into an
   * {@link OutputStream}
   * 
   * @param nestedMap
   *          {@link Map}
   * @see XMLNestedMapConverter
   * @see #toNamespaceAwareXML(Map)
   * @see #toXML(Map)
   * @return xml content
   */
  public String toXMLDocument( Map<String, Object> nestedMap )
  {
    //
    String retval = null;
    
    //
    try
    {
      //
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final OutputStream outputStream = byteArrayContainer.getOutputStream();
      this.toXMLDocument( nestedMap, outputStream );
      retval = byteArrayContainer.toString();
    }
    catch ( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Similar to {@link #toXML(Map, OutputStream)} but encloses the given nested {@link Map} into a xml document with a single root
   * tag
   * 
   * @param nestedMap
   * @param outputStream
   * @see #toXML(Map, OutputStream)
   * @see #toNamespaceAwareXMLDocument(Map, OutputStream)
   */
  public void toXMLDocument( Map<String, Object> nestedMap, OutputStream outputStream )
  {
    //      
    final boolean includeDocumentHeader = true;
    this.toXML( nestedMap, outputStream, includeDocumentHeader );
  }
  
  /**
   * Similar to {@link #toNamespaceAwareXML(Map, OutputStream)} but returns a {@link String} instance instead of writing into an
   * {@link OutputStream}
   * 
   * @param nestedMap
   *          {@link Map}
   * @see XMLNestedMapConverter
   * @see #toNamespaceAwareXML(Map, OutputStream)
   * @see #toNamespaceAwareXMLDocument(Map, OutputStream)
   * @return xml content
   */
  public String toNamespaceAwareXMLDocument( Map<QName, Object> nestedMap )
  {
    //
    String retval = null;
    
    //
    try
    {
      //
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final OutputStream outputStream = byteArrayContainer.getOutputStream();
      this.toNamespaceAwareXMLDocument( nestedMap, outputStream );
      outputStream.close();
      retval = byteArrayContainer.toString();
    }
    catch ( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Similar to {@link #toXMLDocument(Map, OutputStream)} but has full qualified {@link QName}s
   * 
   * @param nestedMap
   * @param outputStream
   * @see #toXML(Map, OutputStream)
   * @see #toNamespaceAwareXMLDocument(Map)
   */
  public void toNamespaceAwareXMLDocument( Map<QName, Object> nestedMap, OutputStream outputStream )
  {
    //
    final boolean includeDocumentHeader = true;
    this.toNamespaceAwareXML( nestedMap, outputStream, includeDocumentHeader );
  }
  
  /**
   * Similar to {@link #toXML(Map)} but writes the result to a given {@link OutputStream} directly instead of creating a
   * {@link String}. <br>
   * <br>
   * The {@link OutputStream} will not be closed by this method call.
   * 
   * @see #toNamespaceAwareXML(Map, OutputStream)
   * @see #toXMLDocument(Map)
   * @see #toNamespaceAwareXMLDocument(Map)
   * @param nestedMap
   *          {@link Map}
   * @param outputStream
   *          {@link OutputStream}
   */
  public void toXML( Map<String, Object> nestedMap, OutputStream outputStream )
  {
    final boolean includeDocumentHeader = false;
    this.toXML( nestedMap, outputStream, includeDocumentHeader );
  }
  
  /**
   * Similar to {@link #toXML(Map)} but writes the result to a given {@link OutputStream} directly instead of creating a
   * {@link String}. <br>
   * <br>
   * The {@link OutputStream} will not be closed by this method call.
   * 
   * @see #toNamespaceAwareXML(Map, OutputStream)
   * @see #toXMLDocument(Map)
   * @see #toNamespaceAwareXMLDocument(Map)
   * @param nestedMap
   *          {@link Map}
   * @param outputStream
   *          {@link OutputStream}
   * @param includeDocumentHeader
   */
  private void toXML( Map<String, Object> nestedMap, OutputStream outputStream, boolean includeDocumentHeader )
  {
    //
    final ElementConverter<String, QName> keyElementConverter = new ElementConverter<String, QName>()
    {
      @Override
      public QName convert( String element )
      {
        return new QName( element );
      }
    };
    this.toXML( nestedMap, outputStream, keyElementConverter, includeDocumentHeader );
  }
  
  /**
   * Similar to {@link #toXML(Map, OutputStream)} but for {@link Map}s having {@link QName}s as key type
   * 
   * @see #toXML(Map, OutputStream)
   * @see #toNamespaceAwareXMLDocument(Map, OutputStream)
   * @param nestedMap
   *          {@link Map}
   * @param outputStream
   *          {@link OutputStream}
   */
  public void toNamespaceAwareXML( Map<QName, Object> nestedMap, OutputStream outputStream )
  {
    final boolean includeDocumentHeader = false;
    this.toNamespaceAwareXML( nestedMap, outputStream, includeDocumentHeader );
  }
  
  /**
   * Similar to {@link #toXML(Map, OutputStream)} but for {@link Map}s having {@link QName}s as key type
   * 
   * @see #toXML(Map, OutputStream)
   * @see #toNamespaceAwareXMLDocument(Map, OutputStream)
   * @param nestedMap
   *          {@link Map}
   * @param outputStream
   *          {@link OutputStream}
   * @param includeDocumentHeader
   */
  private void toNamespaceAwareXML( Map<QName, Object> nestedMap, OutputStream outputStream, boolean includeDocumentHeader )
  {
    //
    final ElementConverter<QName, QName> keyElementConverter = new ElementConverterIdentitiyCast<QName, QName>();
    this.toXML( nestedMap, outputStream, keyElementConverter, includeDocumentHeader );
  }
  
  /**
   * @param nestedMap
   *          {@link Map}
   * @param outputStream
   *          {@link OutputStream}
   * @param includeDocumentHeader
   *          if true a xml document header is written out additionally
   */
  private <K> void toXML( Map<K, Object> nestedMap,
                          OutputStream outputStream,
                          final ElementConverter<K, QName> keyElementConverter,
                          boolean includeDocumentHeader )
  {
    //
    if ( nestedMap != null && keyElementConverter != null && outputStream != null )
    {
      //
      try
      {
        //
        final XMLEventWriter xmlEventWriter = XMLOutputFactory.newInstance().createXMLEventWriter( outputStream );
        final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        final ExceptionHandler exceptionHandler = this.exceptionHandler;
        
        //
        try
        {
          //
          class Helper
          {
            /* ********************************************** Variables ********************************************** */
            private List<String> namespaceStack = new ArrayList<String>();
            
            /* ********************************************** Methods ********************************************** */
            
            @SuppressWarnings("unchecked")
            public void write( Map<K, Object> map )
            {
              if ( map != null )
              {
                for ( K key : map.keySet() )
                {
                  //
                  final QName tagName = keyElementConverter.convert( key );
                  final Object value = map.get( key );
                  
                  //
                  if ( value instanceof String )
                  {
                    //
                    this.writeStartTag( tagName );
                    
                    //
                    final String text = (String) value;
                    this.writeText( text );
                    
                    //
                    this.writeEndTag( tagName );
                  }
                  else if ( value instanceof Map )
                  {
                    //
                    this.writeStartTag( tagName );
                    
                    //
                    final Map<K, Object> subMap = (Map<K, Object>) value;
                    this.write( subMap );
                    
                    //
                    this.writeEndTag( tagName );
                  }
                  else if ( value instanceof List )
                  {
                    //
                    final List<Object> valueList = (List<Object>) value;
                    this.write( tagName, valueList );
                  }
                }
              }
            }
            
            /**
             * @param tagName
             */
            private void writeStartTag( QName tagName )
            {
              //
              try
              {
                //
                final String namespaceURI = tagName.getNamespaceURI();
                
                //            
                final Iterator<?> attributes = null;
                final Iterator<?> namespaces = StringUtils.isNotBlank( namespaceURI )
                                               && !StringUtils.equals( namespaceURI, ListUtils.lastElement( this.namespaceStack ) ) ? IteratorUtils.valueOf( xmlEventFactory.createNamespace( namespaceURI ) )
                                                                                                                                   : null;
                StartElement startElement = xmlEventFactory.createStartElement( tagName, attributes, namespaces );
                xmlEventWriter.add( startElement );
                
                //
                this.namespaceStack.add( namespaceURI );
              }
              catch ( Exception e )
              {
                exceptionHandler.handleException( e );
              }
            }
            
            /**
             * @param tagName
             */
            private void writeEndTag( QName tagName )
            {
              //
              try
              {
                //            
                final Iterator<?> namespaces = null;
                EndElement endElement = xmlEventFactory.createEndElement( tagName, namespaces );
                xmlEventWriter.add( endElement );
                
                //
                ListUtils.removeLast( this.namespaceStack );
              }
              catch ( Exception e )
              {
                exceptionHandler.handleException( e );
              }
            }
            
            /**
             * @param text
             */
            private void writeText( String text )
            {
              //
              try
              {
                //            
                final Characters characters = xmlEventFactory.createCharacters( text );
                xmlEventWriter.add( characters );
              }
              catch ( Exception e )
              {
                exceptionHandler.handleException( e );
              }
            }
            
            /**
             * @param tagName
             * @param valueList
             */
            @SuppressWarnings("unchecked")
            private void write( QName tagName, List<Object> valueList )
            {
              if ( valueList != null )
              {
                for ( Object value : valueList )
                {
                  //
                  if ( value != null )
                  {
                    //
                    this.writeStartTag( tagName );
                    
                    //
                    if ( value instanceof Map )
                    {
                      //
                      final Map<K, Object> map = (Map<K, Object>) value;
                      this.write( map );
                    }
                    else if ( value instanceof String )
                    {
                      //
                      final String text = (String) value;
                      this.writeText( text );
                    }
                    
                    //
                    this.writeEndTag( tagName );
                  }
                }
              }
            }
          }
          
          //
          if ( includeDocumentHeader )
          {
            xmlEventWriter.add( xmlEventFactory.createStartDocument() );
          }
          
          //
          new Helper().write( nestedMap );
        }
        finally
        {
          xmlEventWriter.close();
          outputStream.flush();
        }
      }
      catch ( Exception e )
      {
        if ( this.exceptionHandler != null )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public XMLNestedMapConverter setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }
}
