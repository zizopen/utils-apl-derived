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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEach;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @see XMLIteratorFactory
 * @author Omnaest
 */
@Ignore("Problems with maven consoles unit test")
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
  public void testNewIteratorClassOfQextendsEWithStringContent()
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
    assertEquals( 2, valueList.size() );
    
    for ( String value : valueList )
    {
      // System.out.println( anyEntity );
      assertNotNull( value );
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
    Iterator<Book> iterator = new XMLIteratorFactory( inputStream ).doLowerCaseXMLTagAndAttributeNames().newIterator( Book.class );
    
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
  public void testNewIteratorClassOfQextendsEWithStringContentPerformance()
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
