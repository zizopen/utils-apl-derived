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
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

import javax.sql.rowset.spi.XmlReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.accessor.Accessor;
import org.omnaest.utils.structure.element.accessor.adapter.ThreadLocalToAccessorAdapter;
import org.omnaest.utils.structure.element.cached.CachedElement;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;
import org.omnaest.utils.structure.element.cached.ThreadLocalCachedElement;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.xml.JAXBXMLHelper.JAXBContextBasedUnmarshaller;
import org.omnaest.utils.xml.JAXBXMLHelper.UnmarshallingConfiguration;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLElementSelector.SelectionContext;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformer;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerLowerCase;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerRemoveNamespace;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerUpperCase;
import org.omnaest.utils.xml.context.XMLInstanceContextFactory;
import org.omnaest.utils.xml.context.XMLInstanceContextFactoryJavaStaxDefaultImpl;
import org.omnaest.utils.xml.exception.MissingXMLRootElementAnnotationException;

/**
 * The {@link XMLIteratorFactory} is a wrapper around StAX and JAXB which allows to split a given xml {@link InputStream} content
 * into {@link Object}, {@link Map} or {@link String} content chunks. <br>
 * <br>
 * <h2>Example:</h2><br>
 * Code using the {@link XMLIteratorFactory} to create an {@link Iterator} instance for all book elements:
 * 
 * <pre>
 * Iterator&lt;Book&gt; iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames().newIterator( Book.class );
 * </pre>
 * 
 * <br>
 * XML snippet:
 * 
 * <pre>
 *  &lt;Books&gt;
 *     &lt;Book&gt;
 *         &lt;Title&gt;Simple title&lt;/Title&gt;
 *         &lt;author&gt;an author&lt;/author&gt;
 *     &lt;/Book&gt;
 *     &lt;Book&gt;
 *         &lt;Title&gt;Second simple title&lt;/Title&gt;
 *         &lt;Author&gt;Second author&lt;/Author&gt;
 *     &lt;/Book&gt;
 *  &lt;/Books&gt;
 * </pre>
 * 
 * <br>
 * JAXB annotated class:
 * 
 * <pre>
 * &#064;XmlRootElement(name = &quot;book&quot;)
 * &#064;XmlType(name = &quot;book&quot;)
 * &#064;XmlAccessorType(XmlAccessType.FIELD)
 * protected static class Book
 * {
 *   &#064;XmlElement(name = &quot;title&quot;)
 *   private String title;
 *   
 *   &#064;XmlElement(name = &quot;author&quot;)
 *   private String author;
 * }
 * </pre>
 * 
 * <br>
 * There are several {@link Iterator} types offered:<br>
 * <ul>
 * <li> {@link String} based: {@link #newIterator(QName)}</li>
 * <li> {@link Map} based: {@link #newIteratorMapBased(QName)}</li>
 * <li> {@link Class} type based: {@link #newIterator(Class)}</li>
 * </ul>
 * Those types are faster in traversal of the original stream from top to bottom, whereby the slower ones can get some performance
 * improvement by using parallel processing. The {@link Iterator} instances are thread safe by default and the
 * {@link Iterator#next()} function can be called until an {@link NoSuchElementException} is thrown. <br>
 * In normal circumstances an {@link Iterator} is not usable in multithreaded environments, since {@link Iterator#hasNext()} and
 * {@link Iterator#next()} produce imminent gaps within the {@link Lock} of an element. This gap can be circumvented by calling
 * <ul>
 * <li>{@link #doCreateThreadsafeIterators(boolean)}</li>
 * </ul>
 * which will force {@link Iterator} instances to use {@link ThreadLocal}s internally. Otherwise do not use the
 * {@link Iterator#hasNext()} method, since any other {@link Thread} can clear the {@link Iterator} before the call to
 * {@link Iterator#next()} occurs. <br>
 * <br>
 * The {@link XMLIteratorFactory} allows to modify the underlying event stream using e.g.:<br>
 * <ul>
 * <li> {@link #doLowerCaseXMLTagAndAttributeNames()}</li>
 * <li> {@link #doUpperCaseXMLTagAndAttributeNames()}</li>
 * <li> {@link #doRemoveNamespacesForXMLTagAndAttributeNames()}</li>
 * <li> {@link #doAddXMLEventTransformer(XMLEventTransformer)}</li>
 * </ul>
 * <br>
 * <br>
 * If the {@link XMLIteratorFactory} should only operate on a subset of xml tags within a larger stream the concept of sopes is
 * available, which can be instrumented by calling {@link #doAddXMLTagScope(QName)}.<br>
 * If no scope's start tag is passed no reading of events will occur and the reading into a single {@link Iterator} will stop
 * immediately when an end tag of a scope is matched.
 * 
 * @author Omnaest
 */
public class XMLIteratorFactory
{
  
  /* ************************************************** Constants *************************************************** */
  public static final String                          DEFAULT_ENCODING                               = "UTF-8";
  
  private final Factory<Accessor<String>>             SIMPLE_ACCESSOR_FACTORY                        = new Factory<Accessor<String>>()
                                                                                                     {
                                                                                                       @Override
                                                                                                       public Accessor<String> newInstance()
                                                                                                       {
                                                                                                         return new ElementHolder<String>();
                                                                                                       }
                                                                                                     };
  private final Factory<Accessor<String>>             THREADLOCAL_BASED_ACCESSOR_FACTORY             = new Factory<Accessor<String>>()
                                                                                                     {
                                                                                                       @Override
                                                                                                       public Accessor<String> newInstance()
                                                                                                       {
                                                                                                         return new ThreadLocalToAccessorAdapter<String>();
                                                                                                       }
                                                                                                     };
  public static final XMLInstanceContextFactory       XML_INSTANCE_CONTEXT_FACTORY_JAVA_STAX_DEFAULT = new XMLInstanceContextFactoryJavaStaxDefaultImpl();
  
  public static final JAXBTypeContentConverterFactory DEFAULT_JAXB_TYPE_CONTENT_CONVERTER_FACTORY    = new JAXBTypeContentConverterFactory()
                                                                                                     {
                                                                                                       @Override
                                                                                                       public <E> ElementConverter<String, E> newElementConverter( Class<? extends E> type,
                                                                                                                                                                   ExceptionHandler exceptionHandler )
                                                                                                       {
                                                                                                         return new JAXBTypeContentConverter<E>(
                                                                                                                                                 type,
                                                                                                                                                 exceptionHandler );
                                                                                                       }
                                                                                                     };
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final CachedElement<XMLEventReader>         xmlEventReaderCache;
  private final TraversalContextControl               traversalContextControl;
  private final List<XMLEventTransformer>             xmlEventTransformerList;
  private final List<Scope>                           scopeList;
  private final List<TouchBarrier>                    touchBarrierList;
  private XMLInstanceContextFactory                   xmlInstanceContextFactory;
  private Factory<Accessor<String>>                   accessorFactory                                = null;
  private String                                      encoding                                       = XMLIteratorFactory.DEFAULT_ENCODING;
  private JAXBTypeContentConverterFactory             jaxbTypeContentConverterFactory                = DEFAULT_JAXB_TYPE_CONTENT_CONVERTER_FACTORY;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final ExceptionHandler                      exceptionHandler;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see #newElementConverter(Class, ExceptionHandler)
   * @author Omnaest
   */
  public static interface JAXBTypeContentConverterFactory
  {
    /**
     * Returns an {@link ElementConverter} which converts from a given xml content {@link String} to an {@link Object} of the
     * given type
     * 
     * @param type
     *          {@link Class}
     * @param exceptionHandler
     *          {@link ExceptionHandler}
     * @return new {@link ElementConverter}
     */
    public <E> ElementConverter<String, E> newElementConverter( Class<? extends E> type, ExceptionHandler exceptionHandler );
  }
  
  /**
   * @author Omnaest
   * @param <E>
   */
  public static class JAXBTypeContentConverter<E> implements ElementConverter<String, E>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    protected final ThreadLocalCachedElement<JAXBContextBasedUnmarshaller> cachedElement;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see JAXBTypeContentConverter
     * @param type
     * @param exceptionHandler
     */
    public JAXBTypeContentConverter( final Class<? extends E> type, final ExceptionHandler exceptionHandler )
    {
      this.cachedElement = new ThreadLocalCachedElement<JAXBXMLHelper.JAXBContextBasedUnmarshaller>(
                                                                                                     new ValueResolver<JAXBContextBasedUnmarshaller>()
                                                                                                     {
                                                                                                       @Override
                                                                                                       public JAXBContextBasedUnmarshaller resolveValue()
                                                                                                       {
                                                                                                         return JAXBXMLHelper.newJAXBContextBasedUnmarshaller( type,
                                                                                                                                                               new UnmarshallingConfiguration().setExceptionHandler( exceptionHandler ) );
                                                                                                       }
                                                                                                     } );
    }
    
    @Override
    public E convert( String element )
    {
      return this.cachedElement.getValue().unmarshal( new ByteArrayContainer( element ).getInputStream() );
    }
  }
  
  /**
   * @see XMLIteratorFactory
   * @author Omnaest
   */
  protected static final class XMLIterator implements Iterator<String>
  {
    
    /* ********************************************** Variables ********************************************** */
    private final Accessor<String>          nextElementAccessor;
    private final NamespaceStack            namespaceStack = new NamespaceStack();
    
    /* ********************************************** Beans / Services / References / Delegation ********************************************** */
    private final Accessor<String>          accessor;
    private final ScopeControl              scopeControl;
    private final ExceptionHandler          exceptionHandler;
    private final XMLEventReader            xmlEventReader;
    private final XMLEventFactory           xmlEventFactory;
    private final String                    encoding;
    private final TraversalContextControl   traversalContextControl;
    private final XMLElementSelector        xmlElementSelector;
    private final XMLOutputFactory          xmlOutputFactory;
    private final TouchBarrierControl       touchBarrierControl;
    private final List<XMLEventTransformer> xmlEventTransformerList;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see XMLIterator
     * @param accessor
     * @param scopeControl
     * @param exceptionHandler
     * @param xmlEventReader
     * @param xmlEventFactory
     * @param encoding
     * @param traversalContextControl
     * @param xmlElementSelector
     * @param xmlOutputFactory
     * @param touchBarrierControl
     * @param xmlEventTransformerList
     */
    protected XMLIterator( Accessor<String> accessor, ScopeControl scopeControl, ExceptionHandler exceptionHandler,
                           XMLEventReader xmlEventReader, XMLEventFactory xmlEventFactory, String encoding,
                           TraversalContextControl traversalContextControl, XMLElementSelector xmlElementSelector,
                           XMLOutputFactory xmlOutputFactory, TouchBarrierControl touchBarrierControl,
                           List<XMLEventTransformer> xmlEventTransformerList )
    {
      this.accessor = accessor;
      this.scopeControl = scopeControl;
      this.exceptionHandler = exceptionHandler;
      this.xmlEventReader = xmlEventReader;
      this.xmlEventFactory = xmlEventFactory;
      this.encoding = encoding;
      this.traversalContextControl = traversalContextControl;
      this.xmlElementSelector = xmlElementSelector;
      this.xmlOutputFactory = xmlOutputFactory;
      this.touchBarrierControl = touchBarrierControl;
      this.xmlEventTransformerList = xmlEventTransformerList;
      this.nextElementAccessor = this.accessor;
    }
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public synchronized boolean hasNext()
    {
      //
      this.resolveNextElementIfUnresolved();
      
      // 
      return this.nextElementAccessor.getElement() != null;
    }
    
    @Override
    public synchronized String next()
    {
      //
      String retval = null;
      
      //
      this.resolveNextElementIfUnresolved();
      
      //  
      retval = this.nextElementAccessor.getElement();
      this.nextElementAccessor.setElement( null );
      
      //
      if ( retval == null )
      {
        throw new NoSuchElementException();
      }
      
      //
      return retval;
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    public void resolveNextElementIfUnresolved()
    {
      //
      if ( this.nextElementAccessor.getElement() == null )
      {
        this.nextElementAccessor.setElement( this.resolveNextElement() );
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
        final ByteArrayContainer byteArrayContainerOut = new ByteArrayContainer();
        final OutputStream outputStream = byteArrayContainerOut.getOutputStream();
        final XMLEventWriter xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter( outputStream, this.encoding );
        final XMLEventConsumer xmlEventConsumer = xmlEventWriter;
        
        //
        boolean read = false;
        boolean done = false;
        boolean touchedBarrier = false;
        boolean hasWrittenAtLeastOneElement = false;
        while ( !done
                && !this.scopeControl.hasTraversedAnyScope()
                && this.xmlEventReader.hasNext()
                && ( read || ( !touchedBarrier && !( touchedBarrier = this.touchBarrierControl.isAnyBarrierTouched( this.transformXMLElement( this.xmlEventReader.peek() ),
                                                                                                                    this.traversalContextControl.getCurrentSelectionContext()
                                                                                                                                                .getQNameHierarchy() ) ) ) ) )
        {
          //
          final XMLEvent currentEvent = this.transformXMLElement( this.xmlEventReader.nextEvent() );
          XMLEvent writableEvent = currentEvent;
          
          //
          if ( currentEvent.isStartElement() )
          {
            //
            final StartElement startElement = currentEvent.asStartElement();
            final QName name = startElement.getName();
            this.traversalContextControl.addQName( name );
            
            //
            final SelectionContext selectionContext = this.traversalContextControl.getCurrentSelectionContext();
            this.scopeControl.visitStartElement( selectionContext );
            
            //
            if ( this.xmlElementSelector.selectElement( selectionContext )
                 && ( !this.scopeControl.hasScopes() || this.scopeControl.hasEnteredAnyScope() ) )
            {
              //
              read = true;
            }
            
            //
            if ( read )
            {
              //
              if ( !hasWrittenAtLeastOneElement )
              {
                xmlEventWriter.add( this.xmlEventFactory.createStartDocument() );
              }
              
              //
              this.namespaceStack.addStack( startElement.getNamespaces() );
              writableEvent = transformEventIncludingCurrentNamespaceIfNonDefault( this.xmlEventFactory, startElement, name );
            }
          }
          
          //
          if ( read )
          {
            //
            xmlEventConsumer.add( writableEvent );
            hasWrittenAtLeastOneElement = true;
          }
          
          if ( currentEvent.isEndElement() )
          {
            //
            if ( read )
            {
              this.namespaceStack.removeStack();
            }
            
            //                  
            final SelectionContext selectionContext = this.traversalContextControl.getCurrentSelectionContext();
            if ( this.xmlElementSelector.selectElement( selectionContext ) )
            {
              //
              read = false;
              done = true;
              
              //
              xmlEventWriter.add( this.xmlEventFactory.createEndDocument() );
            }
            
            //
            this.traversalContextControl.reduceLastQName();
            
            //
            this.scopeControl.visitEndElement( selectionContext );
          }
        }
        
        //
        if ( hasWrittenAtLeastOneElement )
        {
          //
          xmlEventWriter.close();
          outputStream.close();
          
          //
          if ( byteArrayContainerOut.isNotEmpty() )
          {
            retval = byteArrayContainerOut.toString( ByteArrayContainer.ENCODING_UTF8 );
          }
        }
        
        //
        if ( !this.xmlEventReader.hasNext() )
        {
          this.xmlEventReader.close();
        }
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleException( e );
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
      for ( XMLEventTransformer xmlEventTransformer : this.xmlEventTransformerList )
      {
        retval = xmlEventTransformer.transform( retval, this.xmlEventFactory );
      }
      
      //
      return retval;
    }
  }
  
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
                                       || ( currentQName != null && StringUtils.equals( this.selectingNamespace,
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
       *          {@link QName}
       * @return {@link QName}
       */
      public QName transformTagName( QName tagName );
      
      /**
       * @param attributeName
       *          {@link QName}
       * @return {@link QName}
       */
      public QName transformAttributeName( QName attributeName );
    }
    
    /**
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerUpperCase implements XMLTagAndAttributeNameTransformer
    {
      @Override
      public QName transformTagName( QName tagName )
      {
        return new QName( tagName.getNamespaceURI(), StringUtils.upperCase( tagName.getLocalPart() ) );
      }
      
      @Override
      public QName transformAttributeName( QName attributeName )
      {
        return new QName( attributeName.getNamespaceURI(), StringUtils.upperCase( attributeName.getLocalPart() ) );
      }
    }
    
    /**
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerLowerCase implements XMLTagAndAttributeNameTransformer
    {
      @Override
      public QName transformTagName( QName tagName )
      {
        return new QName( tagName.getNamespaceURI(), StringUtils.lowerCase( tagName.getLocalPart() ) );
      }
      
      @Override
      public QName transformAttributeName( QName attributeName )
      {
        return new QName( attributeName.getNamespaceURI(), StringUtils.lowerCase( attributeName.getLocalPart() ) );
      }
    }
    
    /**
     * {@link XMLTagAndAttributeNameTransformer} which removes any {@link Namespace} from xml tag and attributes
     * 
     * @author Omnaest
     */
    public static class XMLTagAndAttributeNameTransformerRemoveNamespace implements XMLTagAndAttributeNameTransformer
    {
      
      @Override
      public QName transformTagName( QName tagName )
      {
        // 
        return new QName( null, tagName.getLocalPart() );
      }
      
      @Override
      public QName transformAttributeName( QName attributeName )
      {
        return new QName( null, attributeName.getLocalPart() );
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
        final QName qname = this.xmlTagAndAttributeNameTransformer.transformTagName( name );
        final Iterator<?> attributes = startElement.getAttributes();
        final Iterator<?> namespaces = startElement.getNamespaces();
        
        //
        retval = xmlEventFactory.createStartElement( qname, attributes, namespaces );
      }
      else if ( xmlEvent.isEndElement() )
      {
        //
        final EndElement endElement = xmlEvent.asEndElement();
        final QName name = endElement.getName();
        
        //        
        final QName qname = this.xmlTagAndAttributeNameTransformer.transformTagName( name );
        final Iterator<?> namespaces = endElement.getNamespaces();
        
        //
        retval = xmlEventFactory.createEndElement( qname, namespaces );
      }
      else if ( xmlEvent.isAttribute() )
      {
        //
        final Attribute attribute = (Attribute) xmlEvent;
        final QName name = attribute.getName();
        
        //
        final QName qname = this.xmlTagAndAttributeNameTransformer.transformAttributeName( name );
        final String value = attribute.getValue();
        
        //
        retval = xmlEventFactory.createAttribute( qname, value );
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
  
  /**
   * Controls the traversal context over the {@link XMLStreamReader}
   * 
   * @author Omnaest
   */
  private static class TraversalContextControl
  {
    /* ********************************************** Variables ********************************************** */
    private final List<QName> qNameList = new ArrayList<QName>();
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see SelectionContext
     * @author Omnaest
     */
    private static class SelectionContextImpl implements SelectionContext
    {
      /* ********************************************** Variables ********************************************** */
      private final List<QName> qNameList;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @see SelectionContextImpl
       * @param qNameList
       */
      public SelectionContextImpl( List<QName> qNameList )
      {
        super();
        this.qNameList = new ArrayList<QName>( qNameList );
      }
      
      @Override
      public List<QName> getQNameHierarchy()
      {
        return this.qNameList;
      }
      
      @Override
      public QName getQName()
      {
        return ListUtils.lastElement( this.qNameList );
      }
      
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns the current {@link SelectionContext}
     * 
     * @return
     */
    public SelectionContext getCurrentSelectionContext()
    {
      return new SelectionContextImpl( this.qNameList );
    }
    
    /**
     * Adds a new {@link QName} to the internal {@link List}
     * 
     * @param qName
     */
    public void addQName( QName qName )
    {
      this.qNameList.add( qName );
    }
    
    /**
     * Deletes the last given {@link QName}
     */
    public void reduceLastQName()
    {
      ListUtils.removeLast( this.qNameList );
    }
  }
  
  /**
   * Internal representation of an {@link Scope} based on a given {@link QName}
   * 
   * @author Omnaest
   */
  private static class Scope
  {
    /* ********************************************** Variables ********************************************** */
    private final XMLElementSelector elementSelector;
    
    private boolean                  isTraversed    = false;
    private int                      enclosureCount = 0;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param selectionContext
     */
    public void visitStartElement( SelectionContext selectionContext )
    {
      if ( this.elementSelector.selectElement( selectionContext ) )
      {
        this.enclosureCount++;
      }
    }
    
    /**
     * @see Scope
     * @param qName
     */
    public Scope( QName qName )
    {
      super();
      this.elementSelector = new XMLIteratorFactory.XMLElementSelectorQNameBased( qName );
    }
    
    /**
     * @param selectionContext
     */
    public void visitEndElement( SelectionContext selectionContext )
    {
      if ( this.elementSelector.selectElement( selectionContext ) )
      {
        this.enclosureCount--;
        if ( this.enclosureCount == 0 )
        {
          this.isTraversed = true;
        }
      }
    }
    
    /**
     * @return the isTraversed
     */
    public boolean isTraversed()
    {
      return this.isTraversed;
    }
    
    /**
     * Resets the traversed state to false
     */
    public void resetTraversedState()
    {
      this.isTraversed = false;
    }
    
    /**
     * Returns true if the current {@link Scope} has actually been entered
     * 
     * @return
     */
    public boolean hasBeenEntered()
    {
      return this.enclosureCount > 0;
    }
  }
  
  /**
   * Control structure for a given {@link List} of {@link Scope}s.
   * 
   * @author Omnaest
   */
  private static class ScopeControl
  {
    /* ********************************************** Variables ********************************************** */
    private final List<Scope> scopeList;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * Resets any given {@link Scope} initially and begins to manage it
     * 
     * @see ScopeControl
     * @param scopeList
     */
    public ScopeControl( List<Scope> scopeList )
    {
      super();
      this.scopeList = scopeList;
      
      //
      if ( scopeList != null )
      {
        for ( Scope scope : scopeList )
        {
          scope.resetTraversedState();
        }
      }
    }
    
    /**
     * Returns true if the {@link ScopeControl} manages at least {@link Scope} instance
     * 
     * @return
     */
    public boolean hasScopes()
    {
      return this.scopeList != null && !this.scopeList.isEmpty();
    }
    
    /**
     * Returns true if any internal available {@link Scope} has been traversed
     * 
     * @return
     */
    public boolean hasTraversedAnyScope()
    {
      //
      boolean retval = false;
      
      //
      if ( this.scopeList != null )
      {
        for ( Scope scope : this.scopeList )
        {
          if ( scope.isTraversed() )
          {
            retval = true;
            break;
          }
        }
      }
      
      //
      return retval;
    }
    
    /**
     * @param selectionContext
     *          {@link SelectionContext}
     */
    public void visitStartElement( SelectionContext selectionContext )
    {
      if ( this.scopeList != null )
      {
        for ( Scope scope : this.scopeList )
        {
          scope.visitStartElement( selectionContext );
        }
      }
    }
    
    /**
     * @param selectionContext
     *          {@link SelectionContext}
     */
    public void visitEndElement( SelectionContext selectionContext )
    {
      if ( this.scopeList != null )
      {
        for ( Scope scope : this.scopeList )
        {
          scope.visitEndElement( selectionContext );
        }
      }
    }
    
    /**
     * Returns true if at least one {@link Scope} has been entered currently
     * 
     * @return
     */
    public boolean hasEnteredAnyScope()
    {
      //
      boolean retval = false;
      
      //
      if ( this.scopeList != null )
      {
        for ( Scope scope : this.scopeList )
        {
          if ( scope.hasBeenEntered() )
          {
            //
            retval = true;
            break;
          }
        }
      }
      
      // 
      return retval;
    }
  }
  
  /**
   * Barrier which
   * 
   * @author Omnaest
   */
  private static class TouchBarrier
  {
    /* ********************************************** Variables / State ********************************************** */
    private final XMLElementSelector xmlElementSelector;
    
    /* ********************************************** Methods ********************************************** */
    @SuppressWarnings("unused")
    public TouchBarrier( XMLElementSelector xmlElementSelector )
    {
      super();
      this.xmlElementSelector = xmlElementSelector;
    }
    
    public TouchBarrier( QName qName )
    {
      super();
      this.xmlElementSelector = new XMLElementSelectorQNameBased( qName );
    }
    
    /**
     * Returns true if the current {@link TouchBarrier} matches a given {@link SelectionContext}
     * 
     * @param selectionContext
     * @return
     */
    protected boolean matches( SelectionContext selectionContext )
    {
      return this.xmlElementSelector.selectElement( selectionContext );
    }
    
  }
  
  /**
   * Controls structure for any given touch barrier {@link QName}
   * 
   * @author Omnaest
   */
  private static class TouchBarrierControl
  {
    /* ********************************************** Variables / State ********************************************** */
    private final List<TouchBarrier> touchBarrierList;
    private final ExceptionHandler   exceptionHandler;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see TouchBarrierControl
     * @param touchBarrierList
     *          {@link List} of {@link TouchBarrier}s
     * @param exceptionHandler
     *          {@link ExceptionHandler}
     */
    public TouchBarrierControl( List<TouchBarrier> touchBarrierList, ExceptionHandler exceptionHandler )
    {
      super();
      this.touchBarrierList = touchBarrierList;
      this.exceptionHandler = exceptionHandler;
    }
    
    /**
     * Checks if any of the internal {@link TouchBarrier}s are matching the next {@link XMLEvent} of any {@link XMLEventReader}.
     * To retrieve the next element use {@link XMLEventReader#peek()}, which does not remove the {@link XMLEvent} from the
     * {@link XmlReader}s stream.
     * 
     * @param xmlEventPeek
     *          {@link XMLEvent}
     * @param qNameHierarchy
     * @return true if the next element will touch any barrier
     */
    public boolean isAnyBarrierTouched( XMLEvent xmlEventPeek, final List<QName> qNameHierarchy )
    {
      //
      boolean retval = false;
      
      //
      if ( xmlEventPeek != null && xmlEventPeek.isStartElement() && this.touchBarrierList != null
           && !this.touchBarrierList.isEmpty() )
      {
        try
        {
          //
          final QName qName = xmlEventPeek.asStartElement().getName();
          final SelectionContext selectionContext = new SelectionContext()
          {
            @Override
            public List<QName> getQNameHierarchy()
            {
              return qNameHierarchy;
            }
            
            @Override
            public QName getQName()
            {
              return qName;
            }
          };
          for ( TouchBarrier touchBarrier : this.touchBarrierList )
          {
            if ( touchBarrier != null )
            {
              //
              final boolean matches = touchBarrier.matches( selectionContext );
              if ( matches )
              {
                retval = true;
                break;
              }
            }
          }
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
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Note: the {@link XMLIteratorFactory} does not close the underlying {@link InputStream}
   * 
   * @see XMLIteratorFactory
   * @param inputStream
   *          {@link InputStream}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public XMLIteratorFactory( final InputStream inputStream, final ExceptionHandler exceptionHandler )
  {
    //
    super();
    
    //
    this.xmlEventTransformerList = new ArrayList<XMLEventTransformer>();
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() );
    this.xmlInstanceContextFactory = XMLIteratorFactory.XML_INSTANCE_CONTEXT_FACTORY_JAVA_STAX_DEFAULT;
    this.xmlEventReaderCache = this.newXmlEventReaderCache( inputStream, exceptionHandler );
    this.scopeList = new ArrayList<Scope>();
    this.touchBarrierList = new ArrayList<TouchBarrier>();
    this.traversalContextControl = new TraversalContextControl();
  }
  
  private CachedElement<XMLEventReader> newXmlEventReaderCache( final InputStream inputStream,
                                                                final ExceptionHandler exceptionHandler )
  {
    return new CachedElement<XMLEventReader>( new ValueResolver<XMLEventReader>()
    {
      @Override
      public XMLEventReader resolveValue()
      {
        //
        XMLEventReader retval = null;
        
        // 
        try
        {
          //
          final XMLInputFactory xmlInputFactory = XMLIteratorFactory.this.xmlInstanceContextFactory.newXmlInputFactory();
          Assert.isNotNull( xmlInputFactory, "xmlInputFactory must not be null" );
          
          //
          retval = xmlInputFactory.createXMLEventReader( inputStream );
        }
        catch ( Exception e )
        {
          exceptionHandler.handleException( e );
        }
        
        //
        return retval;
      }
    } );
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
   * @param xmlEventReaderCache
   * @param xmlInstanceContextFactory
   * @param xmlTransformerList
   * @param exceptionHandler
   * @param scopeList
   * @param touchBarrierList
   * @param jaxbTypeContentConverterFactory
   * @param traversalControl
   */
  private XMLIteratorFactory( CachedElement<XMLEventReader> xmlEventReaderCache,
                              XMLInstanceContextFactory xmlInstanceContextFactory, List<XMLEventTransformer> xmlTransformerList,
                              ExceptionHandler exceptionHandler, List<Scope> scopeList, List<TouchBarrier> touchBarrierList,
                              TraversalContextControl traversalContextControl,
                              JAXBTypeContentConverterFactory jaxbTypeContentConverterFactory )
  {
    super();
    this.xmlEventReaderCache = xmlEventReaderCache;
    this.xmlEventTransformerList = xmlTransformerList;
    this.exceptionHandler = exceptionHandler;
    this.scopeList = scopeList;
    this.touchBarrierList = touchBarrierList;
    this.traversalContextControl = traversalContextControl;
    this.xmlInstanceContextFactory = xmlInstanceContextFactory;
    this.jaxbTypeContentConverterFactory = jaxbTypeContentConverterFactory;
  }
  
  /**
   * This adds an {@link XMLEventTransformer} which does lower case the xml tag and attribute names
   * 
   * @return new {@link XMLIteratorFactory} instance
   */
  public XMLIteratorFactory doLowerCaseXMLTagAndAttributeNames()
  {
    //
    final XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer = new XMLTagAndAttributeNameTransformerLowerCase();
    final XMLEventTransformer xmlEventTransformer = new XMLEventTransformerForTagAndAttributeName(
                                                                                                   xmlTagAndAttributeNameTransformer );
    return this.doAddXMLEventTransformer( xmlEventTransformer );
  }
  
  /**
   * This adds an {@link XMLEventTransformer} which does remove all {@link Namespace} declarations on any xml tag and attribute
   * 
   * @return new {@link XMLIteratorFactory} instance
   */
  public XMLIteratorFactory doRemoveNamespacesForXMLTagAndAttributeNames()
  {
    //
    final XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer = new XMLTagAndAttributeNameTransformerRemoveNamespace();
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
    final XMLTagAndAttributeNameTransformer xmlTagAndAttributeNameTransformer = new XMLTagAndAttributeNameTransformerUpperCase();
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
      retval = new XMLIteratorFactory( this.xmlEventReaderCache, this.xmlInstanceContextFactory,
                                       ListUtils.addToNewList( this.xmlEventTransformerList, xmlEventTransformer ),
                                       this.exceptionHandler, this.scopeList, this.touchBarrierList,
                                       this.traversalContextControl, this.jaxbTypeContentConverterFactory );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a new {@link XMLIteratorFactory} instance with the configuration of this one but holding an additional xml tag scope
   * restriction. A scope restriction means that the internal stream is forwarded until it finds the beginning of a xml tag and
   * which is stopped when the end of the same xml tag is reached. <br>
   * <br>
   * Be aware of the fact that scopes can be nested. To begin reading elements only one of all the scopes have to be entered. To
   * stop an {@link Iterator} only one of the scopes has to be left. <br>
   * So it is quite possible that even if one scope is left an enclosing scope is still valid, which means the selection matching
   * is immediately active again until the enclosing scope is now left. <br>
   * <br>
   * After a scope has been passed it is possible to iterate further by creating a new {@link Iterator}.
   * 
   * @param tagName
   *          {@link QName}
   * @return
   */
  public XMLIteratorFactory doAddXMLTagScope( QName tagName )
  {
    //
    XMLIteratorFactory retval = this;
    
    //
    if ( tagName != null )
    {
      //
      final Scope scope = new Scope( tagName );
      retval = new XMLIteratorFactory( this.xmlEventReaderCache, this.xmlInstanceContextFactory, this.xmlEventTransformerList,
                                       this.exceptionHandler, ListUtils.addToNewList( this.scopeList, scope ),
                                       this.touchBarrierList, this.traversalContextControl, this.jaxbTypeContentConverterFactory );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a new {@link XMLIteratorFactory} instance with the configuration of this one but holding an additional xml tag touch
   * barrier restriction. A touch barrier restriction means that the internal stream is validated in advance if the next start
   * element will match the given xml tag. If this is the case, the traversal is stopped and the next element keeps unread, so
   * that any further attempt to create a new {@link Iterator} of any kind will use the still remaining element of the touch
   * barrier. <br>
   * <br>
   * 
   * @param tagName
   *          {@link QName}
   * @return new {@link XMLIteratorFactory} instance
   */
  public XMLIteratorFactory doAddXMLTagTouchBarrier( QName tagName )
  {
    //
    XMLIteratorFactory retval = this;
    
    //
    if ( tagName != null )
    {
      //
      final TouchBarrier touchBarrier = new TouchBarrier( tagName );
      retval = new XMLIteratorFactory( this.xmlEventReaderCache, this.xmlInstanceContextFactory, this.xmlEventTransformerList,
                                       this.exceptionHandler, this.scopeList, ListUtils.addToNewList( this.touchBarrierList,
                                                                                                      touchBarrier ),
                                       this.traversalContextControl, this.jaxbTypeContentConverterFactory );
    }
    
    //
    return retval;
  }
  
  /**
   * If given true as parameter the returned {@link Iterator} instances will use {@link ThreadLocal} states. This results in the
   * case that if one {@link Thread} resolves true for the {@link Iterator#hasNext()} function, the respective value will be
   * locked to this {@link Thread}. Another {@link Thread} would e.g. then get false for the {@link Iterator#hasNext()} function
   * even if the first {@link Thread} did not yet pulled the explicit value by invoking the {@link Iterator#next()} method.<br>
   * <br>
   * Even if this circumstance allows to share any created {@link Iterator} instance between threads without loosing the contract
   * of the {@link Iterator}, it must be ensured that any {@link Thread} which requests {@link Iterator#hasNext()} do actually
   * pull the value. Otherwise the internally backed value gets lost with the dereferencing of the {@link ThreadLocal}.
   * 
   * @param threadsafe
   * @return this
   */
  public XMLIteratorFactory doCreateThreadsafeIterators( boolean threadsafe )
  {
    //
    if ( threadsafe )
    {
      this.accessorFactory = this.THREADLOCAL_BASED_ACCESSOR_FACTORY;
    }
    else
    {
      this.accessorFactory = this.SIMPLE_ACCESSOR_FACTORY;
    }
    
    //
    return this;
  }
  
  /**
   * New {@link Iterator} which returns xml content chunks for all xml tags matching the given {@link QName} <br>
   * <br>
   * Performance is fast with about <b>10000 elements per second</b> beeing processed
   * 
   * @see #newIterator(QName, ElementConverter)
   * @param qName
   *          {@link QName}
   * @return
   */
  public Iterator<String> newIterator( final QName qName )
  {
    //
    return newIterator( qName, new ElementConverterIdentitiyCast<String, String>() );
  }
  
  /**
   * New {@link Iterator} which returns {@link Map} entities each based on a single content chunk which are produced for all xml
   * tags matching the given {@link QName} <br>
   * <br>
   * Performance is medium to slow with about <b>1000 elements per second</b> beeing processed. <br>
   * <br>
   * For details how xml content is transformed to a {@link Map} instance see {@link XMLNestedMapConverter}
   * 
   * @see XMLNestedMapConverter
   * @see #newIterator(QName, ElementConverter)
   * @param qName
   *          {@link QName}
   * @return
   */
  public Iterator<Map<String, Object>> newIteratorMapBased( final QName qName )
  {
    //
    final ElementConverter<String, Map<String, Object>> elementConverter = new ElementConverter<String, Map<String, Object>>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public Map<String, Object> convert( String element )
      {
        //
        final Map<String, Object> mapFromXML = new XMLNestedMapConverter().setExceptionHandler( XMLIteratorFactory.this.exceptionHandler )
                                                                          .setXmlInstanceContextFactory( XMLIteratorFactory.this.xmlInstanceContextFactory )
                                                                          .newMapFromXML( element );
        final Entry<String, Object> firstEntry = MapUtils.firstEntry( mapFromXML );
        final Object value = firstEntry != null ? firstEntry.getValue() : null;
        return (Map<String, Object>) ( value instanceof Map ? value : null );
      }
    };
    return newIterator( qName, elementConverter );
  }
  
  /**
   * Similar to {@link #newIterator(QName)} but allows to specify an additional {@link ElementConverter} which post processes the
   * extracted xml chunks
   * 
   * @param qName
   *          {@link QName}
   * @param elementConverter
   *          {@link ElementConverter}
   * @return
   */
  public <E> Iterator<E> newIterator( final QName qName, ElementConverter<String, E> elementConverter )
  {
    //    
    final XMLElementSelector xmlElementSelector = new XMLElementSelectorQNameBased( qName );
    return newIterator( xmlElementSelector, elementConverter );
  }
  
  /**
   * Selects xml parts based on {@link Class}es annotated with JAXB compliant annotations and uses JAXB to create instances of the
   * given type based on the data of the extracted xml chunks. <br>
   * <br>
   * Performance is slow with about <b>500 elements per second</b> beeing processed
   * 
   * @param type
   * @return
   * @throws MissingXMLRootElementAnnotationException
   */
  public <E> Iterator<E> newIterator( final Class<? extends E> type )
  {
    //
    final QName qName = JAXBXMLHelper.determineRootName( type );
    XMLElementSelector xmlElementSelector = new XMLElementSelectorQNameBased( qName );
    
    //
    return this.newIterator( xmlElementSelector, type );
  }
  
  /**
   * Similar to {@link #newIterator(Class)} but allows to specify a {@link XMLElementSelector} to select tags from the xml stream.
   * 
   * @param xmlElementSelector
   *          {@link XMLElementSelector}
   * @param type
   * @return
   */
  public <E> Iterator<E> newIterator( final XMLElementSelector xmlElementSelector, final Class<? extends E> type )
  {
    //
    final ElementConverter<String, E> elementConverter = this.jaxbTypeContentConverterFactory.newElementConverter( type,
                                                                                                                   this.exceptionHandler );
    return newIterator( xmlElementSelector, elementConverter );
  }
  
  /**
   * Similar to {@link #newIterator(QName, ElementConverter)} but allows to specify a more general {@link XMLElementSelector}
   * instead of a {@link QName}
   * 
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
   * Similar to {@link #newIterator(QName)} but allows to specify a more general {@link XMLElementSelector} instead of a
   * {@link QName}
   * 
   * @param xmlElementSelector
   * @return
   */
  public Iterator<String> newIterator( final XMLElementSelector xmlElementSelector )
  {
    //
    Iterator<String> retval = null;
    
    //
    final XMLEventReader xmlEventReader = this.getXmlEventReader();
    if ( xmlEventReader != null && xmlElementSelector != null )
    {
      try
      {
        //
        final XMLOutputFactory xmlOutputFactory = this.xmlInstanceContextFactory.newXmlOutputFactory();
        final XMLEventFactory xmlEventFactory = this.xmlInstanceContextFactory.newXmlEventFactory();
        Assert.isNotNull( xmlOutputFactory, "xmlOutputFactory must not be null" );
        Assert.isNotNull( xmlEventFactory, "xmlEventFactory must not be null" );
        
        //
        final ScopeControl scopeControl = new ScopeControl( this.scopeList );
        final TouchBarrierControl touchBarrierControl = new TouchBarrierControl( this.touchBarrierList, this.exceptionHandler );
        final Accessor<String> accessor = this.newAccessor();
        
        //
        retval = new XMLIterator( accessor, scopeControl, this.exceptionHandler, xmlEventReader, xmlEventFactory, this.encoding,
                                  this.traversalContextControl, xmlElementSelector, xmlOutputFactory, touchBarrierControl,
                                  this.xmlEventTransformerList );
        
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a new instance of an {@link Accessor} using the internal {@link #accessorFactory}. If the {@link #accessorFactory} is
   * null it will be set to the {@link #SIMPLE_ACCESSOR_FACTORY}.
   * 
   * @return
   */
  private Accessor<String> newAccessor()
  {
    //
    if ( this.accessorFactory == null )
    {
      this.accessorFactory = this.SIMPLE_ACCESSOR_FACTORY;
    }
    
    //
    return this.accessorFactory.newInstance();
  }
  
  /**
   * Sets the encoding. Default is {@value #DEFAULT_ENCODING}
   * 
   * @param encoding
   *          the encoding to set
   * @return this
   */
  public XMLIteratorFactory setEncoding( String encoding )
  {
    this.encoding = encoding;
    return this;
  }
  
  /**
   * Closes the internal {@link XMLEventReader} which closes all iterators immediately
   */
  public XMLIteratorFactory close()
  {
    //
    try
    {
      this.getXmlEventReader().close();
    }
    catch ( XMLStreamException e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Returns the {@link XMLEventReader} from the {@link #xmlEventReaderCache}
   * 
   * @return
   */
  private XMLEventReader getXmlEventReader()
  {
    return this.xmlEventReaderCache.getValue();
  }
  
  /**
   * Allows to set an alternative {@link XMLInstanceContextFactory}, e.g. to replace the current java default stax implementation
   * by another one like Staxon or Jettison for JSON
   * 
   * @see #XML_INSTANCE_CONTEXT_FACTORY_JAVA_STAX_DEFAULT
   * @param xmlInstanceContextFactory
   *          {@link XMLInstanceContextFactory}
   * @return this
   */
  public XMLIteratorFactory setXmlInstanceContextFactory( XMLInstanceContextFactory xmlInstanceContextFactory )
  {
    this.xmlInstanceContextFactory = xmlInstanceContextFactory;
    return this;
  }
  
  /**
   * Allows to set another {@link JAXBTypeContentConverterFactory} which is used to convert xml content to instances of JAXB based
   * types. See {@link #newIterator(Class)}.
   * 
   * @see #DEFAULT_JAXB_TYPE_CONTENT_CONVERTER_FACTORY
   * @param jaxbTypeContentConverterFactory
   *          {@link JAXBTypeContentConverterFactory}
   * @return this
   */
  public XMLIteratorFactory setJAXBTypeContentConverterFactory( JAXBTypeContentConverterFactory jaxbTypeContentConverterFactory )
  {
    this.jaxbTypeContentConverterFactory = jaxbTypeContentConverterFactory;
    return this;
  }
  
}
