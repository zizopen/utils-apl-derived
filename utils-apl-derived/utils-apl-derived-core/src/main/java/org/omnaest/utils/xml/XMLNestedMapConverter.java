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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.iterator.IterableUtils;
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
   * Similar to {@link #newNamespaceAwareMapFromXML(CharSequence)} but with non namespace aware {@link String} values as keys.
   * Those keys will only contain the tag name without any namespace information.
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
