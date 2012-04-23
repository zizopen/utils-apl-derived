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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.input.CharSequenceReader;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.tuple.TupleTwo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * {@link XMLHelper} for {@link XPath} or {@link Document} based helper methods.
 * 
 * @see JAXBXMLHelper
 * @author Omnaest
 */
public class XMLHelper
{
  /**
   * Similar to {@link #select(String, Node, ExceptionHandler)}
   * 
   * @param xPathExpression
   * @param node
   * @return
   */
  public static Node select( String xPathExpression, Node node )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return select( xPathExpression, node, exceptionHandler );
  }
  
  /**
   * Allows to select sub {@link Node}s by a given {@link XPath} expression
   * 
   * @param xPathExpression
   * @param node
   *          {@link Node}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public static Node select( String xPathExpression, Node node, ExceptionHandler exceptionHandler )
  {
    //
    Node retval = null;
    
    //
    final XPath xPath = XPathFactory.newInstance().newXPath();
    try
    {
      retval = (Node) xPath.evaluate( xPathExpression, node, XPathConstants.NODE );
    }
    catch ( XPathExpressionException e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Creates nested {@link Map} instances which contains all information from the given xml content as key value pairs.<br>
   * <br>
   * Attributes are mapped to key value pairs as they are. Sub tags containing only text data will be converted to key value pairs
   * using their tag name as key and the textual information as value.
   * 
   * @param xmlContent
   * @return new (nested) {@link Map} instance
   */
  public static Map<String, Object> newMapFromXML( String xmlContent, ExceptionHandler exceptionHandler )
  {
    //
    final Map<String, Object> retmap = new LinkedHashMap<String, Object>();
    
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
        private List<TupleTwo<String, Object>> stackList = new ArrayList<TupleTwo<String, Object>>();
        
        /* ********************************************** Methods ********************************************** */
        
        /**
         * Manifests a single tag node recursively
         * 
         * @return
         * @throws XMLStreamException
         */
        @SuppressWarnings("unchecked")
        public TupleTwo<String, Object> manifest() throws XMLStreamException
        {
          //
          TupleTwo<String, Object> retval = null;
          
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
              this.addNewStackElement().setValueFirst( name.getLocalPart() );
              
              //
              final Iterator<Attribute> attributeIterator = startElement.getAttributes();
              if ( attributeIterator.hasNext() )
              {
                //
                final TupleTwo<String, Object> currentStackValue = this.getCurrentStackValue();
                
                //
                Map<String, Object> map = null;
                {
                  Object valueSecond = currentStackValue.getValueSecond();
                  if ( valueSecond instanceof Map )
                  {
                    map = (Map<String, Object>) valueSecond;
                  }
                  if ( map == null )
                  {
                    map = new LinkedHashMap<String, Object>();
                  }
                }
                
                //
                for ( Attribute attribute : IterableUtils.valueOf( attributeIterator ) )
                {
                  map.put( attribute.getName().getLocalPart(), attribute.getValue() );
                }
                
                //
                currentStackValue.setValueSecond( map );
              }
            }
            else if ( xmlEvent.isEndElement() )
            {
              //
              retval = this.removeStackElement();
              
              //
              final Object manifestation = retval.getValueSecond();
              final String tagname = retval.getValueFirst();
              
              //
              final TupleTwo<String, Object> currentStackValue = this.getCurrentStackValue();
              
              //
              if ( currentStackValue != null )
              {
                Map<String, Object> map = null;
                {
                  Object valueSecond = currentStackValue.getValueSecond();
                  if ( valueSecond instanceof Map )
                  {
                    map = (Map<String, Object>) valueSecond;
                  }
                  if ( map == null )
                  {
                    map = new LinkedHashMap<String, Object>();
                  }
                }
                
                //
                map.put( tagname, manifestation );
                
                //
                currentStackValue.setValueSecond( map );
              }
            }
            else if ( xmlEvent.isCharacters() )
            {
              //
              final Characters characters = xmlEvent.asCharacters();
              if ( !characters.isWhiteSpace() )
              {
                //
                if ( this.getCurrentStackValue().getValueSecond() == null )
                {
                  //
                  this.getCurrentStackValue().setValueSecond( characters.getData() );
                }
              }
            }
          }
          
          //
          return retval;
        }
        
        private TupleTwo<String, Object> getCurrentStackValue()
        {
          return ListUtils.firstElement( this.stackList );
        }
        
        private TupleTwo<String, Object> removeStackElement()
        {
          return ListUtils.removeFirst( this.stackList );
        }
        
        private TupleTwo<String, Object> addNewStackElement()
        {
          //
          final TupleTwo<String, Object> retval = new TupleTwo<String, Object>();
          this.stackList.add( 0, retval );
          return retval;
        }
      }
      
      //  
      try
      {
        final Helper helper = new Helper();
        final TupleTwo<String, Object> result = helper.manifest();
        retmap.put( result.getValueFirst(), result.getValueSecond() );
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
}
