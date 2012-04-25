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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
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

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.accessor.Accessor;
import org.omnaest.utils.structure.element.accessor.adapter.ThreadLocalToAccessorAdapter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLElementSelector.SelectionContext;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformer;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerLowerCase;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerRemoveNamespace;
import org.omnaest.utils.xml.XMLIteratorFactory.XMLEventTransformerForTagAndAttributeName.XMLTagAndAttributeNameTransformerUpperCase;

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
  /* ********************************************** Constants ********************************************** */
  private final Factory<Accessor<String>> SIMPLE_ACCESSOR_FACTORY            = new Factory<Accessor<String>>()
                                                                             {
                                                                               @Override
                                                                               public Accessor<String> newInstance()
                                                                               {
                                                                                 return new ElementHolder<String>();
                                                                               }
                                                                             };
  private final Factory<Accessor<String>> THREADLOCAL_BASED_ACCESSOR_FACTORY = new Factory<Accessor<String>>()
                                                                             {
                                                                               @Override
                                                                               public Accessor<String> newInstance()
                                                                               {
                                                                                 return new ThreadLocalToAccessorAdapter<String>();
                                                                               }
                                                                             };
  
  /* ********************************************** Variables ********************************************** */
  private final XMLEventReader            xmlEventReader;
  private final TraversalContextControl   traversalContextControl;
  private final List<XMLEventTransformer> xmlEventTransformerList;
  private final List<Scope>               scopeList;
  private Factory<Accessor<String>>       accessorFactory                    = null;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private final ExceptionHandler          exceptionHandler;
  
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
  public XMLIteratorFactory( InputStream inputStream, ExceptionHandler exceptionHandler )
  {
    //
    super();
    
    //
    this.xmlEventTransformerList = new ArrayList<XMLEventTransformer>();
    this.exceptionHandler = exceptionHandler;
    this.xmlEventReader = createXmlEventReader( inputStream, exceptionHandler );
    this.scopeList = new ArrayList<Scope>();
    this.traversalContextControl = new TraversalContextControl();
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
   * @param scopeList
   * @param traversalControl
   */
  private XMLIteratorFactory( XMLEventReader xmlEventReader, List<XMLEventTransformer> xmlTransformerList,
                              ExceptionHandler exceptionHandler, List<Scope> scopeList,
                              TraversalContextControl traversalContextControl )
  {
    super();
    this.xmlEventReader = xmlEventReader;
    this.xmlEventTransformerList = xmlTransformerList;
    this.exceptionHandler = exceptionHandler;
    this.scopeList = scopeList;
    this.traversalContextControl = traversalContextControl;
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
      retval = new XMLIteratorFactory( this.xmlEventReader, ListUtils.addToNewList( this.xmlEventTransformerList,
                                                                                    xmlEventTransformer ), this.exceptionHandler,
                                       this.scopeList, this.traversalContextControl );
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
      retval = new XMLIteratorFactory( this.xmlEventReader, this.xmlEventTransformerList, this.exceptionHandler,
                                       ListUtils.addToNewList( this.scopeList, scope ), this.traversalContextControl );
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
        final Entry<String, Object> firstEntry = MapUtils.firstEntry( new XMLNestedMapConverter().setExceptionHandler( XMLIteratorFactory.this.exceptionHandler )
                                                                                                 .newMapFromXML( element ) );
        Object value = firstEntry != null ? firstEntry.getValue() : null;
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
    if ( this.xmlEventReader != null && xmlElementSelector != null )
    {
      try
      {
        //
        final XMLEventReader xmlEventReader = this.xmlEventReader;
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        final ExceptionHandler exceptionHandler = this.exceptionHandler;
        final TraversalContextControl traversalContextControl = this.traversalContextControl;
        final ScopeControl scopeControl = new ScopeControl( this.scopeList );
        final Accessor<String> accessor = this.newAccessor();
        
        //
        retval = new Iterator<String>()
        {
          /* ********************************************** Variables ********************************************** */
          private final Accessor<String> nextElementAccessor = accessor;
          private final NamespaceStack   namespaceStack      = new NamespaceStack();
          
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
              ByteArrayContainer byteArrayContainerOut = new ByteArrayContainer();
              final OutputStream outputStream = byteArrayContainerOut.getOutputStream();
              final XMLEventConsumer xmlEventConsumer = xmlOutputFactory.createXMLEventWriter( outputStream );
              
              //
              boolean read = false;
              boolean done = false;
              while ( !done && !scopeControl.hasTraversedAnyScope() && xmlEventReader.hasNext() )
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
                  traversalContextControl.addQName( name );
                  
                  //
                  final SelectionContext selectionContext = traversalContextControl.getCurrentSelectionContext();
                  scopeControl.visitStartElement( selectionContext );
                  
                  //
                  if ( xmlElementSelector.selectElement( selectionContext )
                       && ( !scopeControl.hasScopes() || scopeControl.hasEnteredAnyScope() ) )
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
                  if ( read )
                  {
                    this.namespaceStack.removeStack();
                  }
                  
                  //                  
                  final SelectionContext selectionContext = traversalContextControl.getCurrentSelectionContext();
                  if ( xmlElementSelector.selectElement( selectionContext ) )
                  {
                    read = false;
                    done = true;
                  }
                  
                  //
                  traversalContextControl.reduceLastQName();
                  
                  //
                  scopeControl.visitEndElement( selectionContext );
                }
              }
              outputStream.close();
              
              //
              if ( byteArrayContainerOut.isNotEmpty() )
              {
                retval = byteArrayContainerOut.toString( ByteArrayContainer.ENCODING_UTF8 );
              }
              
              //
              if ( !xmlEventReader.hasNext() )
              {
                xmlEventReader.close();
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
}
