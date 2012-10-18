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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEach;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.threads.submit.SubmitGroup;
import org.omnaest.utils.threads.submit.SubmitGroupFactory;
import org.omnaest.utils.xml.XMLIteratorFactory.JAXBTypeContentConverterFactory;
import org.omnaest.utils.xml.context.XMLInstanceContextFactory;
import org.omnaest.utils.xml.context.XMLInstanceContextFactoryStAXONImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @see XMLIteratorFactory
 * @author Omnaest
 */
public class XMLIteratorFactoryTest
{
  //@Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  @XmlRootElement(name = "book", namespace = "http://www.example.org")
  @XmlType(name = "book")
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class Book
  {
    @XmlElement(name = "title", namespace = "http://www.example.org")
    private String title;
    
    @XmlElement(name = "author", namespace = "http://www.other.example.org")
    private String author;
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Book [title=" );
      builder.append( this.title );
      builder.append( ", author=" );
      builder.append( this.author );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @return the title
     */
    public String getTitle()
    {
      return this.title;
    }
    
    /**
     * @return the author
     */
    public String getAuthor()
    {
      return this.author;
    }
    
    /**
     * @param title
     *          the title to set
     */
    public void setTitle( String title )
    {
      this.title = title;
    }
    
    /**
     * @param author
     *          the author to set
     */
    public void setAuthor( String author )
    {
      this.author = author;
    }
    
  }
  
  @XmlRootElement(name = "book")
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class SimpleBook
  {
    @XmlElement(name = "title")
    private String title;
    
    @XmlElement(name = "author")
    private String author;
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "SimpleBook [title=" );
      builder.append( this.title );
      builder.append( ", author=" );
      builder.append( this.author );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @return the title
     */
    public String getTitle()
    {
      return this.title;
    }
    
    /**
     * @return the author
     */
    public String getAuthor()
    {
      return this.author;
    }
    
    /**
     * @param title
     *          the title to set
     */
    public void setTitle( String title )
    {
      this.title = title;
    }
    
    /**
     * @param author
     *          the author to set
     */
    public void setAuthor( String author )
    {
      this.author = author;
    }
    
  }
  
  @XmlRootElement(name = "books", namespace = "http://www.example.org")
  protected static class AnyEntity
  {
    @XmlAnyElement
    private List<Element> elements;
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "AnyEntity [elements=" );
      builder.append( this.elements );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @return the elements
     */
    public List<Element> getElements()
    {
      return this.elements;
    }
    
  }
  
  @XmlRootElement(namespace = "http://www.example.org")
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class Books
  {
    @XmlElement(name = "book", namespace = "http://www.example.org")
    private List<Book> bookList = new ArrayList<Book>();
    
    /**
     * @return the bookList
     */
    public List<Book> getBookList()
    {
      return this.bookList;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Books [bookList=" );
      builder.append( this.bookList );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNewIteratorClassOfQextendsE()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames().newIterator( Book.class );
    
    //
    final List<Book> bookList = ListUtils.valueOf( iterator );
    assertEquals( 2, bookList.size() );
    
    //
    for ( Book book : bookList )
    {
      //System.out.println( book );
      
      //
      assertNotNull( book.getAuthor() );
      assertNotNull( book.getTitle() );
    }
    
  }
  
  @Test
  public void testNewIteratorClassOfQextendsEWithAnyElement() throws XPathExpressionException
  {
    //    
    final int numberOfObjects = 2;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<AnyEntity> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames()
                                                                        .newIterator( AnyEntity.class );
    
    //
    final List<AnyEntity> anyEntityList = ListUtils.valueOf( iterator );
    assertEquals( 1, anyEntityList.size() );
    
    AnyEntity anyEntity = ListUtils.firstElement( anyEntityList );
    assertNotNull( anyEntity );
    {
      //System.out.println( anyEntity );
      
      //
      final List<Element> elements = anyEntity.getElements();
      assertEquals( numberOfObjects, elements.size() );
      
      //
      for ( Element element : elements )
      {
        //
        final String expression = "//*[local-name()='book']";
        Node node = XMLHelper.select( expression, element );
        
        //
        Book book = JAXBXMLHelper.loadObjectFromNode( node, Book.class );
        assertNotNull( book.getAuthor() );
        assertNotNull( book.getTitle() );
        
      }
    }
  }
  
  @Test
  public void testNewIteratorMapBased()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<Map<String, Object>> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames()
                                                                                  .newIteratorMapBased( new QName(
                                                                                                                   "http://www.example.org",
                                                                                                                   "book" ) );
    
    //
    final List<Map<String, Object>> bookList = ListUtils.valueOf( iterator );
    assertEquals( 2, bookList.size() );
    
    //
    for ( Map<String, Object> book : bookList )
    {
      //      
      assertNotNull( book.get( "author" ) );
      assertNotNull( book.get( "title" ) );
    }
  }
  
  @Test
  public void testNewIteratorMapBasedStaxonJSON()
  {
    //    
    final int numberOfObjects = 2;
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer(
                                                                          "{\"books\":[ {\"book\": {\"author\" : \"John Doe1\", \"title\":\"Another world1\"}},{\"book\": {\"author\" : \"John Doe2\", \"title\":\"Another world2\"}}]}" );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    XMLInstanceContextFactory xmlInstanceContextFactory = new XMLInstanceContextFactoryStAXONImpl();
    Iterator<Map<String, Object>> iterator = new XMLIteratorFactory( inputStream, new ExceptionHandlerEPrintStackTrace() ).setXmlInstanceContextFactory( xmlInstanceContextFactory )
                                                                                                                          .doLowerCaseXMLTagAndAttributeNames()
                                                                                                                          .newIteratorMapBased( new QName(
                                                                                                                                                           "book" ) );
    
    //
    final List<Map<String, Object>> bookList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    int counter = 1;
    for ( Map<String, Object> book : bookList )
    {
      //      
      assertEquals( "John Doe" + counter, book.get( "author" ) );
      assertEquals( "Another world" + counter, book.get( "title" ) );
      counter++;
    }
  }
  
  @Test
  public void testNewIteratorStringContent()
  {
    //    
    final int numberOfObjects = 2;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<String> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames()
                                                                     .newIterator( new QName( "http://www.example.org", "book" ) );
    
    //
    final List<String> valueList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, valueList.size() );
    
    for ( String value : valueList )
    {
      // System.out.println( anyEntity );
      assertNotNull( value );
    }
    
    //
    try
    {
      iterator.next();
      fail();
    }
    catch ( NoSuchElementException e )
    {
    }
  }
  
  @Test
  public void testNewIteratorStringContentJSONUsingStaxonAndJAXB()
  {
    //    
    final int numberOfObjects = 2;
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer(
                                                                          "{\"books\":[ {\"book\": {\"author\" : \"John Doe1\", \"title\":\"Another world1\"}},{\"book\": {\"author\" : \"John Doe2\", \"title\":\"Another world2\"}}]}" );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    JAXBTypeContentConverterFactory elementConverterForJAXBTypeFactory = new JAXBTypeContentConverterFactoryStAXONImpl();
    Iterator<SimpleBook> iterator = new XMLIteratorFactory( inputStream, new ExceptionHandlerEPrintStackTrace() ).setXmlInstanceContextFactory( new XMLInstanceContextFactoryStAXONImpl() )
                                                                                                                 .setJAXBTypeContentConverterFactory( elementConverterForJAXBTypeFactory )
                                                                                                                 .doLowerCaseXMLTagAndAttributeNames()
                                                                                                                 .newIterator( SimpleBook.class );
    
    //
    final List<SimpleBook> bookList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    int counter = 1;
    for ( SimpleBook book : bookList )
    {
      //System.out.println( book );
      
      //
      assertEquals( "John Doe" + counter, book.getAuthor() );
      assertEquals( "Another world" + counter, book.getTitle() );
      counter++;
    }
    
  }
  
  @Test
  public void testNewIteratorStringContentWithTouchBarrier()
  {
    //    
    final int numberOfObjects = 2;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    final XMLIteratorFactory xmlIteratorFactory = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames();
    
    //
    {
      final Iterator<String> iterator = xmlIteratorFactory.doAddXMLTagTouchBarrier( new QName( "http://www.other.example.org",
                                                                                               "author" ) )
                                                          .newIterator( new QName( "http://www.example.org", "book" ) );
      final List<String> valueList = ListUtils.valueOf( iterator );
      assertEquals( 2, valueList.size() );
    }
    
  }
  
  @Test
  public void testNewIteratorScopedWithStringContent()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    final XMLIteratorFactory xmlIteratorFactory = new XMLIteratorFactory( inputStream ).doRemoveNamespacesForXMLTagAndAttributeNames()
                                                                                       .doLowerCaseXMLTagAndAttributeNames()
                                                                                       .doAddXMLTagScope( new QName( "books" ) );
    
    //
    {
      //
      Iterator<Map<String, Object>> iterator = xmlIteratorFactory.doAddXMLTagScope( new QName( "header" ) )
                                                                 .newIteratorMapBased( new QName( "header" ) );
      
      //
      final List<Map<String, Object>> valueList = ListUtils.valueOf( iterator );
      assertEquals( 1, valueList.size() );
      Map<String, Object> firstElement = ListUtils.firstElement( valueList );
      assertNotNull( firstElement );
      assertEquals( "Some meta information", firstElement.get( "metainfo" ) );
    }
    
    //
    for ( int ii = 1; ii <= 2; ii++ )
    {
      //
      Iterator<String> iterator = xmlIteratorFactory.doAddXMLTagScope( new QName( "book" ) ).newIterator( new QName( "title" ) );
      
      //
      final List<String> valueList = ListUtils.valueOf( iterator );
      assertEquals( 1, valueList.size() );
      assertNotNull( ListUtils.firstElement( valueList ) );
    }
    
    //
    {
      //
      Iterator<String> iterator = xmlIteratorFactory.doAddXMLTagScope( new QName( "book" ) ).newIterator( new QName( "title" ) );
      
      //
      final List<String> valueList = ListUtils.valueOf( iterator );
      assertEquals( 0, valueList.size() );
    }
  }
  
  @Test
  @PerfTest(invocations = 1)
  @Ignore("Performance test")
  public void testNewIteratorClassOfQextendsEPerformance()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doCreateThreadsafeIterators( true )
                                                                   .doLowerCaseXMLTagAndAttributeNames()
                                                                   .newIterator( Book.class );
    
    //    
    final ExecutorService executorService = Executors.newFixedThreadPool( 2 );
    final List<Book> bookList = new ForEach<Book, Book>( IterableUtils.valueOf( iterator ) ).doIterateInParallelUsing( executorService,
                                                                                                                       2 )
                                                                                            .execute( new Operation<Book, Book>()
                                                                                            {
                                                                                              @Override
                                                                                              public Book execute( Book book )
                                                                                              {
                                                                                                return book;
                                                                                              }
                                                                                            } );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    for ( Book book : bookList )
    {
      //System.out.println( book );
      
      //
      assertNotNull( book.getAuthor() );
      assertNotNull( book.getTitle() );
    }
    
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewIteratorStringContentPerformance()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<String> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames()
                                                                     .newIterator( new QName( "http://www.example.org", "book" ) );
    
    //
    final List<String> valueList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, valueList.size() );
    
    for ( String value : valueList )
    {
      // System.out.println( anyEntity );
      assertNotNull( value );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewIteratorJAXBPerformance()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames().newIterator( Book.class );
    
    //
    final List<Book> bookList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    for ( Book book : bookList )
    {
      //System.out.println( book );
      
      //
      assertNotNull( book.getAuthor() );
      assertNotNull( book.getTitle() );
    }
    
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewIteratorJAXBPerformanceMultithreaded()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    final Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doCreateThreadsafeIterators( true )
                                                                         .doLowerCaseXMLTagAndAttributeNames()
                                                                         .newIterator( Book.class );
    
    //
    final int numberOfThreads = 10;
    ExecutorService executorService = Executors.newFixedThreadPool( numberOfThreads );
    SubmitGroupFactory submitGroupFactory = new SubmitGroupFactory( executorService );
    SubmitGroup<List<Book>> submitGroup = submitGroupFactory.newSubmitGroup( new ArrayList<List<Book>>() );
    Callable<List<Book>> callable = new Callable<List<Book>>()
    {
      @Override
      public List<Book> call() throws Exception
      {
        final List<Book> bookList = new ArrayList<Book>();
        ListUtils.addAll( bookList, IterableUtils.valueOf( iterator ) );
        return bookList;
      }
    };
    submitGroup.submit( callable, numberOfThreads );
    
    final List<Book> bookList = ListUtils.mergeAll( submitGroup.doWait().untilAllTasksAreDone().reduceToList() );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    for ( Book book : bookList )
    {
      //System.out.println( book );
      
      //
      assertNotNull( book.getAuthor() );
      assertNotNull( book.getTitle() );
    }
    
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewIteratorMapBasedPerformance()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    Iterator<Map<String, Object>> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames()
                                                                                  .newIteratorMapBased( new QName(
                                                                                                                   "http://www.example.org",
                                                                                                                   "book" ) );
    
    //
    final List<Map<String, Object>> bookList = ListUtils.valueOf( iterator );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    for ( Map<String, Object> book : bookList )
    {
      //      
      assertNotNull( book.get( "author" ) );
      assertNotNull( book.get( "title" ) );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewIteratorMapBasedMultithreadedPerformance()
  {
    //    
    final int numberOfObjects = 10000;
    final ByteArrayContainer byteArrayContainer = generateTestObjects( numberOfObjects );
    
    //
    final InputStream inputStream = byteArrayContainer.getInputStream();
    final Iterator<Map<String, Object>> iterator = new XMLIteratorFactory( inputStream ).doCreateThreadsafeIterators( true )
                                                                                        .doLowerCaseXMLTagAndAttributeNames()
                                                                                        .newIteratorMapBased( new QName(
                                                                                                                         "http://www.example.org",
                                                                                                                         "book" ) );
    
    //    
    final int numberOfThreads = 10;
    ExecutorService executorService = Executors.newFixedThreadPool( numberOfThreads );
    SubmitGroupFactory submitGroupFactory = new SubmitGroupFactory( executorService );
    SubmitGroup<List<Map<String, Object>>> submitGroup = submitGroupFactory.newSubmitGroup( new ArrayList<List<Map<String, Object>>>() );
    Callable<List<Map<String, Object>>> callable = new Callable<List<Map<String, Object>>>()
    {
      @Override
      public List<Map<String, Object>> call() throws Exception
      {
        final List<Map<String, Object>> bookList = new ArrayList<Map<String, Object>>();
        ListUtils.addAll( bookList, IterableUtils.valueOf( iterator ) );
        return bookList;
      }
    };
    submitGroup.submit( callable, numberOfThreads );
    
    final List<Map<String, Object>> bookList = ListUtils.mergeAll( submitGroup.doWait().untilAllTasksAreDone().reduceToList() );
    assertEquals( numberOfObjects, bookList.size() );
    
    //
    for ( Map<String, Object> book : bookList )
    {
      //      
      assertNotNull( book.get( "author" ) );
      assertNotNull( book.get( "title" ) );
    }
  }
  
  private static ByteArrayContainer generateTestObjects( int count )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    Books books = new Books();
    List<Book> bookList = books.getBookList();
    for ( int ii = 1; ii <= count; ii++ )
    {
      //
      Book book = new Book();
      book.setAuthor( "author" + ii );
      book.setTitle( "title" + ii );
      
      //
      bookList.add( book );
    }
    
    //
    JAXBXMLHelper.storeObjectAsXML( books, byteArrayContainer.getOutputStream() );
    
    //
    return byteArrayContainer;
  }
  
}
