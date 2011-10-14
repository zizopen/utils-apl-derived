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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.EnumerationUtils;
import org.junit.Test;
import org.omnaest.utils.web.HttpSessionFacadeFactory.AttributeName;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Omnaest
 */
public class HttpSessionFacadeFactoryTest
{
  /* ********************************************** Variables ********************************************** */
  private HttpSession           httpSession           = new MockHttpSession();
  private HttpSessionResolver   httpSessionResolver   = new HttpSessionResolver()
                                                      {
                                                        
                                                        @Override
                                                        public HttpSession resolveHttpSession()
                                                        {
                                                          //
                                                          return HttpSessionFacadeFactoryTest.this.httpSession;
                                                        }
                                                      };
  private TestHttpSessionFacade testHttpSessionFacade = new HttpSessionFacadeFactory( this.httpSessionResolver ).newSessionFacade( TestHttpSessionFacade.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  public static interface TestHttpSessionFacade
  {
    public void setFieldString( String field );
    
    public String getFieldString();
    
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    @AttributeName("OTHERFIELD")
    public String getOtherField();
    
    public void setOtherField( String value );
  }
  
  /* ********************************************** Methods ********************************************** */
  @SuppressWarnings("unchecked")
  @Test
  public void testNewSessionFacade()
  {
    //    
    assertNotNull( this.testHttpSessionFacade );
    assertNull( this.testHttpSessionFacade.getFieldDouble() );
    assertNull( this.testHttpSessionFacade.getFieldString() );
    
    //
    this.testHttpSessionFacade.setFieldDouble( 1.345d );
    this.testHttpSessionFacade.setFieldString( "testValue" );
    this.testHttpSessionFacade.setOtherField( "a value" );
    
    //    
    assertNotNull( this.testHttpSessionFacade.getFieldDouble() );
    assertNotNull( this.testHttpSessionFacade.getFieldString() );
    assertNotNull( this.testHttpSessionFacade.getOtherField() );
    assertEquals( 1.345d, this.testHttpSessionFacade.getFieldDouble(), 0.01 );
    assertEquals( "testValue", this.testHttpSessionFacade.getFieldString() );
    assertEquals( "a value", this.testHttpSessionFacade.getOtherField() );
    
    //
    assertEquals( new ArrayList<String>( Arrays.asList( "fieldDouble", "fieldString", "OTHERFIELD" ) ),
                  new ArrayList<String>( EnumerationUtils.toList( this.httpSession.getAttributeNames() ) ) );
  }
}
