/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * @see HttpSessionToMapAdapter
 * @author Omnaest
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpSessionToMapAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  @Mock
  private HttpSession         httpSession;
  private Map<String, Object> httpSessionMapAdapter;
  private Map<String, Object> underlyingMap;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    this.httpSessionMapAdapter = HttpSessionToMapAdapter.newInstance( this.httpSession );
    
    //
    final Map<String, Object> underlyingMap = new LinkedHashMap<String, Object>();
    this.underlyingMap = underlyingMap;
    
    underlyingMap.put( "attribute1", "value1" );
    underlyingMap.put( "attribute2", 123.5d );
    underlyingMap.put( "attribute3", 1234l );
    
    //
    Mockito.when( this.httpSession.getAttribute( Matchers.anyString() ) ).thenAnswer( new Answer<Object>()
    {
      
      @Override
      public Object answer( InvocationOnMock invocation ) throws Throwable
      {
        //
        String key = (String) invocation.getArguments()[0];
        
        // 
        return underlyingMap.get( key );
      }
    } );
    Mockito.doAnswer( new Answer<Void>()
    {
      
      @Override
      public Void answer( InvocationOnMock invocation ) throws Throwable
      {
        //
        String key = (String) invocation.getArguments()[0];
        String value = (String) invocation.getArguments()[1];
        
        //
        underlyingMap.put( key, value );
        
        // 
        return null;
      }
    } ).when( this.httpSession ).setAttribute( Matchers.anyString(), Matchers.anyObject() );
    Mockito.when( this.httpSession.getAttributeNames() ).thenAnswer( new Answer<Enumeration<String>>()
    {
      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public Enumeration<String> answer( InvocationOnMock invocation ) throws Throwable
      {
        return new Vector( underlyingMap.keySet() ).elements();
      }
    } );
    Mockito.doAnswer( new Answer<Void>()
    {
      @Override
      public Void answer( InvocationOnMock invocation ) throws Throwable
      {
        //
        String key = (String) invocation.getArguments()[0];
        
        //
        underlyingMap.remove( key );
        
        //
        return null;
      }
    } ).when( this.httpSession ).removeAttribute( Matchers.anyString() );
  }
  
  @Test
  public void testEquals()
  {
    //
    assertEquals( this.underlyingMap, this.httpSessionMapAdapter );
  }
  
  @Test
  public void testGet()
  {
    //
    for ( String key : this.underlyingMap.keySet() )
    {
      assertEquals( this.underlyingMap.get( key ), this.httpSessionMapAdapter.get( key ) );
    }
  }
  
  @Test
  public void testPut()
  {
    //
    this.httpSessionMapAdapter.put( "otherKey", "otherValue" );
    
    //
    assertTrue( this.underlyingMap.containsKey( "otherKey" ) );
    assertEquals( "otherValue", this.underlyingMap.get( "otherKey" ) );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.httpSessionMapAdapter.remove( "attribute2" );
    
    //
    assertFalse( this.underlyingMap.containsKey( "attribute2" ) );
  }
  
  @Test
  public void testKeySet()
  {
    assertEquals( this.underlyingMap.keySet(), this.httpSessionMapAdapter.keySet() );
  }
  
  @Test
  public void testValues()
  {
    assertEquals( new ArrayList<Object>( this.underlyingMap.values() ), this.httpSessionMapAdapter.values() );
  }
  
}
