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
package org.omnaest.utils.spring.session.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.spring.session.HttpSessionService;
import org.omnaest.utils.web.HttpSessionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @see HttpSessionService
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "HttpSessionServiceTestAC.xml" })
public class HttpSessionServiceImplTest
{
  /* ********************************************** Variables ********************************************** */
  @Autowired
  protected HttpSessionService httpSessionService = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see HttpSessionFacade
   * @author Omnaest
   */
  public static interface ExampleHttpSessionFacade extends HttpSessionFacade
  {
    public String getFieldString();
    
    public void setFieldString( String value );
    
    public Double getFieldDouble();
    
    public void setFieldDouble( Double value );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    MockHttpServletRequest request = new MockHttpServletRequest();
    {
      //
      MockHttpSession session = new MockHttpSession();
      {
        //
        session.setAttribute( "fieldString", "value" );
        session.setAttribute( "fieldDouble", 1.45 );
      }
      request.setSession( session );
    }
    boolean inheritable = true;
    RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( request ), inheritable );
  }
  
  @Test
  public void testNewHttpSessionFacade()
  {
    //
    ExampleHttpSessionFacade exampleHttpSessionFacade = this.httpSessionService.newHttpSessionFacade( ExampleHttpSessionFacade.class );
    
    //
    {
      //
      Double fieldDouble = exampleHttpSessionFacade.getFieldDouble();
      String fieldString = exampleHttpSessionFacade.getFieldString();
      
      assertNotNull( fieldDouble );
      assertNotNull( fieldString );
      
      assertEquals( 1.45, fieldDouble.doubleValue(), 0.01 );
      assertEquals( "value", fieldString );
    }
    
    //
    {
      //
      exampleHttpSessionFacade.setFieldDouble( 5.67 );
      exampleHttpSessionFacade.setFieldString( "another value" );
      
      //
      Double fieldDouble = exampleHttpSessionFacade.getFieldDouble();
      String fieldString = exampleHttpSessionFacade.getFieldString();
      
      assertNotNull( fieldDouble );
      assertNotNull( fieldString );
      
      assertEquals( 5.67, fieldDouble.doubleValue(), 0.01 );
      assertEquals( "another value", fieldString );
    }
    
  }
  
  @Test
  public void testResolveHttpSession()
  {
    HttpSession httpSession = this.httpSessionService.resolveHttpSession();
    assertNotNull( httpSession );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testResolveHttpSessionAndReturnAsMapView()
  {
    //
    Map<String, Object> map = this.httpSessionService.resolveHttpSessionAndReturnAsMapView();
    assertNotNull( map );
    assertEquals( 2, map.size() );
    assertEquals( Arrays.asList( "fieldString", "fieldDouble" ), new ArrayList<String>( map.keySet() ) );
    assertEquals( Arrays.asList( "value", 1.45 ), map.values() );
    
    //
    map.put( "newAttribute", "new value" );
    assertEquals( 3, map.size() );
    assertEquals( Arrays.asList( "fieldString", "fieldDouble", "newAttribute" ), new ArrayList<String>( map.keySet() ) );
    assertEquals( Arrays.asList( "value", 1.45, "new value" ), map.values() );
    
    //
    map.remove( "fieldString" );
    assertEquals( 2, map.size() );
    assertEquals( Arrays.asList( "fieldDouble", "newAttribute" ), new ArrayList<String>( map.keySet() ) );
    assertEquals( Arrays.asList( 1.45, "new value" ), map.values() );
  }
  
  @Test
  public void testGetAndSetHttpSessionAttribute()
  {
    //
    assertEquals( "value", this.httpSessionService.getHttpSessionAttribute( "fieldString" ) );
    assertEquals( 1.45, this.httpSessionService.getHttpSessionAttribute( "fieldDouble" ) );
    
    //
    this.httpSessionService.setHttpSessionAttribute( "fieldString", "another value" );
    this.httpSessionService.setHttpSessionAttribute( "fieldDouble", 5.67 );
    
    //
    assertEquals( "another value", this.httpSessionService.getHttpSessionAttribute( "fieldString" ) );
    assertEquals( 5.67, this.httpSessionService.getHttpSessionAttribute( "fieldDouble" ) );
    
  }
  
}
