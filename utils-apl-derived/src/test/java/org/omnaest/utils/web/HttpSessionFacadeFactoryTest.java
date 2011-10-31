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
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.structure.element.converter.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Omnaest
 */
public class HttpSessionFacadeFactoryTest
{
  @Rule
  public ContiPerfRule             contiPerfRule            = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  private HttpSession              httpSession              = new MockHttpSession();
  private HttpSessionResolver      httpSessionResolver      = new HttpSessionResolver()
                                                            {
                                                              @Override
                                                              public HttpSession resolveHttpSession()
                                                              {
                                                                //
                                                                return HttpSessionFacadeFactoryTest.this.httpSession;
                                                              }
                                                            };
  private HttpSessionFacadeExample httpSessionFacadeExample = new HttpSessionFacadeFactory( this.httpSessionResolver ).newSessionFacade( HttpSessionFacadeExample.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  public static interface HttpSessionFacadeExample
  {
    public void setFieldString( String field );
    
    public String getFieldString();
    
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    @PropertyNameTemplate("OTHERFIELD")
    public String getOtherField();
    
    @Adapter(type = ElementConverterIdentity.class)
    public void setOtherField( String value );
  }
  
  /* ********************************************** Methods ********************************************** */
  @SuppressWarnings("unchecked")
  @Test
  public void testNewSessionFacade()
  {
    //    
    assertNotNull( this.httpSessionFacadeExample );
    assertNull( this.httpSessionFacadeExample.getFieldDouble() );
    assertNull( this.httpSessionFacadeExample.getFieldString() );
    
    //
    this.httpSessionFacadeExample.setFieldDouble( 1.345d );
    this.httpSessionFacadeExample.setFieldString( "testValue" );
    this.httpSessionFacadeExample.setOtherField( "a value" );
    
    //    
    assertNotNull( this.httpSessionFacadeExample.getFieldDouble() );
    assertNotNull( this.httpSessionFacadeExample.getFieldString() );
    assertNotNull( this.httpSessionFacadeExample.getOtherField() );
    assertEquals( 1.345d, this.httpSessionFacadeExample.getFieldDouble(), 0.01 );
    assertEquals( "testValue", this.httpSessionFacadeExample.getFieldString() );
    assertEquals( "a value", this.httpSessionFacadeExample.getOtherField() );
    
    //
    assertEquals( new ArrayList<String>( Arrays.asList( "fieldDouble", "fieldString", "OTHERFIELD" ) ),
                  new ArrayList<String>( EnumerationUtils.toList( this.httpSession.getAttributeNames() ) ) );
  }
  
  @Test
  @PerfTest(invocations = 100)
  @Required(average = 100)
  public void testNewSessionFacadePerformancePerHundredInstantiations()
  {
    //
    for ( int ii = 0; ii < 100; ii++ )
    {
      new HttpSessionFacadeFactory( this.httpSessionResolver ).newSessionFacade( HttpSessionFacadeExample.class );
    }
  }
  
  @Test
  @PerfTest(invocations = 100)
  @Required(average = 100)
  public void testSessionFacadeReadAndWritePerformancePerHundredInvocations()
  {
    //    
    for ( int ii = 0; ii < 100; ii++ )
    {
      //
      this.httpSessionFacadeExample.setFieldDouble( 1.345d );
      this.httpSessionFacadeExample.setFieldString( "testValue" );
      this.httpSessionFacadeExample.setOtherField( "a value" );
      
      //
      this.httpSessionFacadeExample.getFieldDouble();
      this.httpSessionFacadeExample.getFieldString();
      this.httpSessionFacadeExample.getOtherField();
    }
  }
  
}
