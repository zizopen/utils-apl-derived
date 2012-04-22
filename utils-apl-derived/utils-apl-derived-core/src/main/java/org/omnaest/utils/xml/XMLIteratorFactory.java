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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;

/**
 * The {@link XMLIteratorFactory} is a wrapper around StAX and JAXB which allows to split a given xml {@link InputStream} content
 * into object or string content chunks.
 * 
 * @author Omnaest
 */
public class XMLIteratorFactory
{
  /* ********************************************** Variables ********************************************** */
  private final XMLEventReader      xmlEventReader;
  private List<XMLEventTransformer> xmlEventTransformerList = new ArrayList<XMLEventTransformer>();
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private final ExceptionHandler    exceptionHandler;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   */
  public static interface XMLElementSelector
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @author Omnaest
     */
    public static interface SelectionContext
    {
      /**
       * @see QName
       * @return
       */
      public QName getQName();
      
      /**
       * Returns all {@link QName}s of the currents element location. The current element is the last entry of the returned
       * {@link List}
       * 
       * @return
       */
      public List<QName> getQNameHierarchy();
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param selectionContext
     *          {@link SelectionContext}
     * @return
     */
    public boolean selectElement( SelectionContext selectionContext );
  }
  
  /**
   * {@link XMLElementSelector} which matches a given {@link QName}
   * 
   * @see XMLElementSelector
   * @author Omnaest
   */
  public static class XMLElementSelectorQNameBased implements XMLElementSelector
  {
    /* ********************************************** Variables ********************************************** */
    private final String selectingNamespace;
    private final String selectingTagName;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see XMLElementSelectorQNameBased
     * @param qName
     */
    public XMLElementSelectorQNameBased( QName qName )
    {
      super();
      this.selectingNamespace = qName != null ? qName.getNamespaceURI() : null;
      this.selectingTagName = qName != null ? qName.getLocalPart() : null;
    }
    
    @Override
    public boolean selectElement( SelectionContext selectionContext )
    {
      //
      boolean retval = false;
      
      //
      final QName currentQName = selectionContext.getQName();
      
      //
      final boolean matchesNamespace = StringUtils.isBlank( this.selectingNamespace )
                                       || ( currentQName != null && StringUtils.equalsIgnoreCase( this.selectingNamespace,
                                                                                                  currentQName.getNamespaceURI() ) );
      
      final boolean matchesTagName = currentQName != null
                                     && StringUtils.equals( this.selectingTagName, currentQName.getLocalPart() );
      retval = matchesNamespace && matchesTagName;
      
      //
      return retval;
    }
    
  }
  
  /**
   * @see XMLIteratorFactory
   * @see #transform(XMLEvent, XMLEventFactory)
   * @author Omnaest
   */
  public static interface XMLEventTransformer
  {
    /**
     * This methods returns a transformed {@link XMLEvent} based on the given {@link XMLEvent}. This can be e.g. lower casing the
     * local name. The {@link XMLEventFactory} supports the creation of new {@link XMLEvent}s.
     * 
     * @param xmlEvent
     * @param xmlEventFactory
     * @return transformed {@link XMLEvent}
     */
    public XMLEvent transform( XMLEvent xmlEvent, XMLEventFactory xmlEventFactory );
  }
  
  /**
   * {@link XMLEventTransformer} which allows to transform the tag and attribute names.<br>
   * <br>
   * 
   * @author Omnaest
   */
  public static class XMLEventTransformerForTagAndAttributeName implements XMLEventTransformer
  {
    /* ********************************************** Variables ********************************************** */
    private final XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * @author Omnaest
     */
    public static interface XMLTagAndAttributeNameTransformer
    {
      /**
       * @param tagName
       * @return
       */
      public String transformTagName( String tagName );
      
      /**
       * @param attributeName
       * @return
       */
      public String transformAttributeName( String attributeName );
    }
    
    /**
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerUpperCase implements XMLTagAndAttributeNameTransformer
    {
      @Override
      public String transformTagName( String tagName )
      {
        return StringUtils.upperCase( tagName );
      }
      
      @Override
      public String transformAttributeName( String attributeName )
      {
        return StringUtils.upperCase( attributeName );
      }
    }
    
    /**
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerLowerCase implements XMLTagAndAttributeNameTransformer
    {
      @Override
      public String transformTagName( String tagName )
      {
        return StringUtils.lowerCase( tagName );
      }
      
      @Override
      public String transformAttributeName( String attributeName )
      {
        return StringUtils.lowerCase( attributeName );
      }
    }
    
    /**
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerCapitalized implements XMLTagAndAttributeNameTransformer
    {
      @Override
      public String transformTagName( String tagName )
      {
        return StringUtils.capitalize( StringUtils.lowerCase( tagName ) );
      }
      
      @Override
      public String transformAttributeName( String attributeName )
      {
        return StringUtils.capitalize( StringUtils.lowerCase( attributeName ) );
      }
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see XMLEventTransformerForTagAndAttributeName
     * @param xmlTagAndAttributeNameTransformer
     */
    public XMLEventTransformerForTagAndAttributeName( XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer )
    {
      super();
      this.xmlTagAndAttributeNameTransformer = xmlTagAndAttributeNameTransformer;
    }
    
    @Override
    public XMLEvent transform( XMLEvent xmlEvent, XMLEventFactory xmlEventFactory )
    {
      //
      XMLEvent retval = xmlEvent;
      
      //      
      if ( xmlEvent.isStartElement() )
      {
        //
        final StartElement startElement = xmlEvent.asStartElement();
        final QName name = startElement.getName();
        
        //
        final String prefix = name.getPrefix();
        final String namespaceUri = name.getNamespaceURI();
        final String localName = this.xmlTagAndAttributeNameTransformer.transformTagName( name.getLocalPart() );
        final Iterator<?> attributes = startElement.getAttributes();
        final Iterator<?> namespaces = startElement.getNamespaces();
        final NamespaceContext context = startElement.getNamespaceContext();
        
        //
        retval = xmlEventFactory.createStartElement( prefix, namespaceUri, localName, attributes, namespaces, context );
      }
      else if ( xmlEvent.isEndElement() )
      {
        //
        final EndElement endElement = xmlEvent.asEndElement();
        final QName name = endElement.getName();
        
        //
        final String prefix = name.getPrefix();
        final String namespaceUri = name.getNamespaceURI();
        final String localName = this.xmlTagAndAttributeNameTransformer.transformTagName( name.getLocalPart() );
        final Iterator<?> namespaces = endElement.getNamespaces();
        
        //
        retval = xmlEventFactory.createEndElement( prefix, namespaceUri, localName, namespaces );
      }
      else if ( xmlEvent.isAttribute() )
      {
        //
        final Attribute attribute = (Attribute) xmlEvent;
        final QName name = attribute.getName();
        
        //
        final String prefix = name.getPrefix();
        final String namespaceURI = name.getNamespaceURI();
        final String localName = this.xmlTagAndAttributeNameTransformer.transformAttributeName( name.getLocalPart() );
        final String value = attribute.getValue();
        
        //
        retval = xmlEventFactory.createAttribute( prefix, namespaceURI, localName, value );
      }
      
      //
      return retval;
    }
  }
  
  /**
   * Implementation of a {@link Namespace} stack which can be used to distinguish if a single element should explicitly declare a
   * {@link Namespace} or if a parental element did this already.
   * 
   * @author Omnaest
   */
  private static class NamespaceStack
  {
    /* ********************************************** Variables ********************************************** */
    private final List<Map<String, String>> prefixToNamespaceUriMapList = new ArrayList<Map<String, String>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Adds a new stack layer. This should be called for each StartElement with all the explicitly declared {@link Namespace}s by
     * this element.
     * 
     * @see #removeStack()
     * @see Namespace
     * @param namespaceIterator
     */
    public void addStack( Iterator<Namespace> namespaceIterator )
    {
      //
      final Map<String, String> prefixToNamespaceUriMap = new HashMap<String, String>();
      {
        //
        if ( namespaceIterator != null )
        {
          for ( Namespace namespace : IterableUtils.valueOf( namespaceIterator ) )
          {
            if ( namespace != null )
            {
              //
              final String prefix = namespace.getPrefix();
              final String namespaceURI = namespace.getNamespaceURI();
              prefixToNamespaceUriMap.put( prefix, namespaceURI );
            }
          }
        }
      }
      this.prefixToNamespaceUriMapList.add( 0, prefixToNamespaceUriMap );
    }
    
    /**
     * @param namespace
     */
    public void addNamespaceToCurrentNamespaceStack( Namespace namespace )
    {
      //
      final Map<String, String> prefixToNamespaceUriMap = ListUtils.firstElement( this.prefixToNamespaceUriMapList );
      if ( prefixToNamespaceUriMap != null )
      {
        //
        final String prefix = namespace.getPrefix();
        final String namespaceUri = namespace.getNamespaceURI();
        prefixToNamespaceUriMap.put( prefix, namespaceUri );
      }
    }
    
    /**
     * This should be called for each {@link EndElement}
     * 
     * @see #addStack(Iterator)
     */
    public void removeStack()
    {
      ListUtils.removeFirst( this.prefixToNamespaceUriMapList );
    }
    
    /**
     * Returns true if the given {@link Namespace} is already declared
     * 
     * @param prefix
     * @param namespaceUri
     * @return
     */
    public boolean hasDeclaredNamespace( String prefix, String namespaceUri )
    {
      //
      boolean retval = false;
      
      //
      for ( Map<String, String> prefixToNamespaceUriMap : this.prefixToNamespaceUriMapList )
      {
        //
        if ( StringUtils.equalsIgnoreCase( namespaceUri, prefixToNamespaceUriMap.get( prefix ) ) )
        {
          retval = true;
          break;
        }
      }
      
      //
      return retval;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "NamespaceStack [prefixToNamespaceUriMapList=" );
      builder.append( this.prefixToNamespaceUriMapList );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see XMLIteratorFactory
   * @param inputStream
   *          {@link InputStream}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public XMLIteratorFactory( InputStream inputStream, ExceptionHandler exceptionHandler )
  {
    //
    super();
    
    //
    this.exceptionHandler = exceptionHandler;
    this.xmlEventReader = createXmlEventReader( inputStream, exceptionHandler );
  }
  
  /**
   * Similar to {@link #XMLIteratorFactory(InputStream, ExceptionHandler)} using an {@link ExceptionHandlerIgnoring}
   * 
   * @see XMLIteratorFactory
   * @param inputStream
   *          {@link InputStream}
   */
  public XMLIteratorFactory( InputStream inputStream )
  {
    //
    this( inputStream, new ExceptionHandlerIgnoring() );
  }
  
  /**
   * @see XMLIteratorFactory
   * @param xmlEventReader
   * @param xmlTransformerList
   * @param exceptionHandler
   */
  private XMLIteratorFactory( XMLEventReader xmlEventReader, List<XMLEventTransformer> xmlTransformerList,
                              ExceptionHandler exceptionHandler )
  {
    super();
    this.xmlEventReader = xmlEventReader;
    this.xmlEventTransformerList = xmlTransformerList;
    this.exceptionHandler = exceptionHandler;
  }
  
  /**
   * This adds an {@link XMLEventTransformer} which does lower case the xml tag and attribute names
   * 
   * @return new {@link XMLIteratorFactory} instance
   */
  public XMLIteratorFactory doLowerCaseXMLTagAndAttributeNames()
  {
    //
    final XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer = new XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerLowerCase();
    final XMLEventTransformer xmlEventTransformer = new XMLEventTransformerForTagAndAttributeName(
                                                                                                   xmlTagAndAttributeNameTransformer );
    return this.doAddXMLEventTransformer( xmlEventTransformer );
  }
  
  /**
   * This adds an {@link XMLEventTransformer} which does upper case the xml tag and attribute names
   * 
   * @return new {@link XMLIteratorFactory} instance
   */
  public XMLIteratorFactory doUpperCaseXMLTagAndAttributeNames()
  {
    //
    final XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer = new XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerUpperCase();
    final XMLEventTransformer xmlEventTransformer = new XMLEventTransformerForTagAndAttributeName(
                                                                                                   xmlTagAndAttributeNameTransformer );
    return this.doAddXMLEventTransformer( xmlEventTransformer );
  }
  
  /**
   * @param xmlEventTransformer
   * @return new {@link XMLIteratorFactory} instance if the given {@link XMLEventTransformer} is not null otherwise this instance
   */
  public XMLIteratorFactory doAddXMLEventTransformer( XMLEventTransformer xmlEventTransformer )
  {
    //
    XMLIteratorFactory retval = this;
    
    //
    if ( xmlEventTransformer != null )
    {
      retval = new XMLIteratorFactory( this.xmlEventReader, ListUtils.addToNewList( this.xmlEventTransformerList,
                                                                                    xmlEventTransformer ), this.exceptionHandler );
    }
    
    //
    return retval;
  }
  
  /**
   * @param inputStream
   * @param exceptionHandler
   * @return
   */
  private static XMLEventReader createXmlEventReader( InputStream inputStream, ExceptionHandler exceptionHandler )
  {
    //
    XMLEventReader retval = null;
    
    //
    try
    {
      //
      retval = XMLInputFactory.newInstance().createXMLEventReader( inputStream );
    }
    catch ( XMLStreamException e )
    {
      exceptionHandler.handleException( e );
    }
    
    //
    return retval;
  }
  
  /**
   * @param qName
   *          {@link QName}
   * @return
   */
  public <E> Iterator<String> newIterator( final QName qName )
  {
    //
    return newIterator( qName, new ElementConverterIdentitiyCast<String, String>() );
  }
  
  /**
   * @param qName
   *          {@link QName}
   * @param elementConverter
   * @return
   */
  public <E> Iterator<E> newIterator( final QName qName, ElementConverter<String, E> elementConverter )
  {
    //    
    final XMLElementSelector xmlElementSelector = new XMLElementSelectorQNameBased( qName );
    return newIterator( xmlElementSelector, elementConverter );
  }
  
  /**
   * @param type
   * @return
   */
  public <E> Iterator<E> newIterator( final Class<? extends E> type )
  {
    //
    final String selectingTagName;
    final String selectingNamespace;
    
    //
    final XmlRootElement xmlRootElement = ReflectionUtils.annotation( type, XmlRootElement.class );
    if ( xmlRootElement != null )
    {
      //      
      String tagName = xmlRootElement.name();
      if ( tagName != null && !StringUtils.equalsIgnoreCase( tagName, "##default" ) )
      {
        selectingTagName = tagName;
      }
      else
      {
        selectingTagName = StringUtils.lowerCase( type.getSimpleName() );
      }
      
      //
      String namespace = xmlRootElement.namespace();
      if ( StringUtils.equalsIgnoreCase( namespace, "##default" ) )
      {
        //
        namespace = null;
        
        //
        final XmlSchema xmlSchema = ReflectionUtils.annotation( type.getPackage(), XmlSchema.class );
        if ( xmlSchema != null )
        {
          namespace = xmlSchema.namespace();
        }
      }
      selectingNamespace = namespace;
      
    }
    else
    {
      selectingNamespace = null;
      selectingTagName = null;
    }
    
    //    
    final QName qName = new QName( selectingNamespace, selectingTagName );
    XMLElementSelector xmlElementSelector = new XMLElementSelectorQNameBased( qName );
    
    //
    return this.newIterator( xmlElementSelector, type );
  }
  
  /**
   * @param xmlElementSelector
   *          {@link XMLElementSelector}
   * @param type
   * @return
   */
  public <E> Iterator<E> newIterator( final XMLElementSelector xmlElementSelector, final Class<? extends E> type )
  {
    //
    final ElementConverter<String, E> elementConverter = new ElementConverter<String, E>()
    {
      @Override
      public E convert( String element )
      {
        return JAXBXMLHelper.loadObjectFromXML( element, type, XMLIteratorFactory.this.exceptionHandler );
      }
    };
    return newIterator( xmlElementSelector, elementConverter );
  }
  
  /**
   * @param xmlElementSelector
   *          {@link XMLElementSelector}
   * @param elementConverter
   *          {@link ElementConverter}
   * @return
   */
  public <E> Iterator<E> newIterator( final XMLElementSelector xmlElementSelector,
                                      final ElementConverter<String, E> elementConverter )
  {
    //
    final Iterator<String> iterator = newIterator( xmlElementSelector );
    return IteratorUtils.convertingIteratorDecorator( iterator, elementConverter );
  }
  
  /**
   * @param xmlElementSelector
   * @return
   */
  public Iterator<String> newIterator( final XMLElementSelector xmlElementSelector )
  {
    //
    Iterator<String> retval = null;
    
    //
    if ( this.xmlEventReader != null && xmlElementSelector != null )
    {
      try
      {
        //
        final XMLEventReader xmlEventReader = this.xmlEventReader;
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        final ExceptionHandler exceptionHandler = this.exceptionHandler;
        
        //
        retval = new Iterator<String>()
        {
          /* ********************************************** Variables ********************************************** */
          private String               nextElement         = null;
          private boolean              resolvedNextElement = false;
          
          private final List<QName>    qNameList           = new ArrayList<QName>();
          private final NamespaceStack namespaceStack      = new NamespaceStack();
          
          /* ********************************************** Methods ********************************************** */
          @Override
          public synchronized boolean hasNext()
          {
            //
            this.resolveNextElementIfUnresolved();
            
            // 
            return this.nextElement != null;
          }
          
          @Override
          public synchronized String next()
          {
            //
            this.resolveNextElementIfUnresolved();
            
            // 
            this.resolvedNextElement = false;
            return this.nextElement;
          }
          
          @Override
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
          
          public void resolveNextElementIfUnresolved()
          {
            //
            if ( !this.resolvedNextElement )
            {
              this.nextElement = this.resolveNextElement();
              this.resolvedNextElement = true;
            }
          }
          
          @SuppressWarnings("unchecked")
          public String resolveNextElement()
          {
            //
            String retval = null;
            
            //
            try
            {
              //
              ByteArrayContainer byteArrayContainerOut = new ByteArrayContainer();
              final OutputStream outputStream = byteArrayContainerOut.getOutputStream();
              final XMLEventConsumer xmlEventConsumer = xmlOutputFactory.createXMLEventWriter( outputStream );
              final List<QName> qNameList = this.qNameList;
              
              //
              final class SelectionContextImpl implements XMLIteratorFactory.XMLElementSelector.SelectionContext
              {
                /* ********************************************** Variables ********************************************** */
                private final QName name;
                
                /* ********************************************** Methods ********************************************** */
                /**
                 * @see SelectionContextImpl
                 * @param name
                 */
                public SelectionContextImpl( QName name )
                {
                  super();
                  this.name = name;
                }
                
                @Override
                public List<QName> getQNameHierarchy()
                {
                  return qNameList;
                }
                
                @Override
                public QName getQName()
                {
                  return this.name;
                }
                
              }
              
              //
              boolean read = false;
              boolean done = false;
              while ( xmlEventReader.hasNext() && !done )
              {
                //
                final XMLEvent currentEvent = transformXMLElement( xmlEventReader.nextEvent() );
                XMLEvent writableEvent = currentEvent;
                
                //
                if ( currentEvent.isStartElement() )
                {
                  //
                  final StartElement startElement = currentEvent.asStartElement();
                  final QName name = startElement.getName();
                  qNameList.add( name );
                  
                  XMLIteratorFactory.XMLElementSelector.SelectionContext selectionContext = new SelectionContextImpl( name );
                  if ( xmlElementSelector.selectElement( selectionContext ) )
                  {
                    //
                    read = true;
                  }
                  
                  //
                  if ( read )
                  {
                    //
                    this.namespaceStack.addStack( startElement.getNamespaces() );
                    writableEvent = transformEventIncludingCurrentNamespaceIfNonDefault( xmlEventFactory, startElement, name );
                    
                  }
                }
                
                //
                if ( read )
                {
                  //
                  xmlEventConsumer.add( writableEvent );
                }
                
                if ( currentEvent.isEndElement() )
                {
                  //
                  final QName name = currentEvent.asEndElement().getName();
                  if ( name.equals( ListUtils.lastElement( qNameList ) ) )
                  {
                    ListUtils.removeLast( qNameList );
                  }
                  
                  //
                  if ( read )
                  {
                    this.namespaceStack.removeStack();
                  }
                  
                  //
                  XMLIteratorFactory.XMLElementSelector.SelectionContext selectionContext = new SelectionContextImpl( name );
                  if ( xmlElementSelector.selectElement( selectionContext ) )
                  {
                    read = false;
                    done = true;
                  }
                  
                }
              }
              outputStream.close();
              
              //
              if ( byteArrayContainerOut.isNotEmpty() )
              {
                retval = byteArrayContainerOut.toString( ByteArrayContainer.ENCODING_UTF8 );
              }
            }
            catch ( Exception e )
            {
              exceptionHandler.handleException( e );
            }
            
            //
            return retval;
          }
          
          @SuppressWarnings("unchecked")
          private StartElement transformEventIncludingCurrentNamespaceIfNonDefault( final XMLEventFactory xmlEventFactory,
                                                                                    final StartElement startElement,
                                                                                    final QName name )
          {
            //
            StartElement retval = startElement;
            
            //
            final String prefix = name.getPrefix();
            final String namespaceUri = name.getNamespaceURI();
            final String localName = name.getLocalPart();
            if ( StringUtils.isNotBlank( namespaceUri ) && !StringUtils.equalsIgnoreCase( namespaceUri, "##default" )
                 && !this.namespaceStack.hasDeclaredNamespace( prefix, namespaceUri ) )
            {
              //
              final Namespace namespace = xmlEventFactory.createNamespace( prefix, namespaceUri );
              final Iterator<?> namespaces = IteratorUtils.addToNewIterator( startElement.getNamespaces(), namespace );
              final Iterator<?> attributes = startElement.getAttributes();
              
              retval = xmlEventFactory.createStartElement( prefix, namespaceUri, localName, attributes, namespaces );
              this.namespaceStack.addNamespaceToCurrentNamespaceStack( namespace );
            }
            
            //
            return retval;
          }
          
          /**
           * @param xmlEvent
           * @return
           */
          private XMLEvent transformXMLElement( final XMLEvent xmlEvent )
          {
            //
            XMLEvent retval = xmlEvent;
            
            //
            for ( XMLEventTransformer xmlEventTransformer : XMLIteratorFactory.this.xmlEventTransformerList )
            {
              retval = xmlEventTransformer.transform( retval, xmlEventFactory );
            }
            
            //
            return retval;
          }
          
        };
        
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
}
