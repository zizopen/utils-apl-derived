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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * @see XMLNestedMapConverter
 * @author Omnaest
 */
public class XMLNestedMapConverterTest
{
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNewMapFromXML()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final String xmlContent = byteArrayContainer.toString();
    Map<String, Object> map = new XMLNestedMapConverter().newMapFromXML( xmlContent );
    
    //
    //    System.out.println( MapUtils.toStringUsingHierarchy( map ) );
    
    //
    assertNotNull( map );
    Map<String, Object> books = (Map<String, Object>) map.get( "Books" );
    List<Map<String, Object>> bookList = (List<Map<String, Object>>) books.get( "Book" );
    assertEquals( 2, bookList.size() );
    
    //
    Iterator<Map<String, Object>> iterator = bookList.iterator();
    {
      Map<String, Object> book = iterator.next();
      assertEquals( "Simple title", book.get( "Title" ) );
    }
    {
      Map<String, Object> book = iterator.next();
      assertEquals( "Second simple\n            title\n        ", book.get( "Title" ) );
    }
    
  }
  
  @Test
  public void testToXML()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final String xmlContent = byteArrayContainer.toString();
    final XMLNestedMapConverter xmlNestedMapConverter = new XMLNestedMapConverter();
    Map<String, Object> nestedMap = xmlNestedMapConverter.newMapFromXML( xmlContent );
    
    //System.out.println( MapUtils.toStringUsingHierarchy( nestedMap ) );
    
    String xmlResult = xmlNestedMapConverter.toXML( nestedMap );
    //System.out.println( xmlResult );
    assertNotNull( xmlResult );
    assertEquals( "<Books><header><metainfo>Some meta information</metainfo></header><Book><Title>Simple title</Title><author>\n            an author\n        </author></Book><Book><Title>Second simple\n            title\n        </Title><Author>Second author</Author></Book></Books>",
                  xmlResult );
    
  }
  
  @Test
  public void testToNamespaceAwareXML()
  {
    //    
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer().copyFrom( this.getClass()
                                                                                         .getResourceAsStream( "books.xml" ) );
    
    //
    final String xmlContent = byteArrayContainer.toString();
    final XMLNestedMapConverter xmlNestedMapConverter = new XMLNestedMapConverter();
    Map<QName, Object> nestedMap = xmlNestedMapConverter.newNamespaceAwareMapFromXML( xmlContent );
    
    //System.out.println( MapUtils.toStringUsingHierarchy( nestedMap ) );
    
    String xmlResult = xmlNestedMapConverter.toNamespaceAwareXML( nestedMap );
    //System.out.println( xmlResult );
    
    assertNotNull( xmlResult );
    assertEquals( "<Books xmlns=\"http://www.example.org\"><header><metainfo>Some meta information</metainfo></header><Book><Title>Simple title</Title><author xmlns=\"http://www.other.example.org\">\n            an author\n        </author></Book><Book><Title>Second simple\n            title\n        </Title><Author xmlns=\"http://www.other.example.org\">Second author</Author></Book></Books>",
                  xmlResult );
    
  }
}
