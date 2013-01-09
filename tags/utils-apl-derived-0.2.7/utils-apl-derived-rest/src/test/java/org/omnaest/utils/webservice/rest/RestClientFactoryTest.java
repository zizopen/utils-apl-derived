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
package org.omnaest.utils.webservice.rest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.webservice.rest.RestClientFactory.BodyParameter;
import org.omnaest.utils.webservice.rest.RestClientFactory.HttpMethod;
import org.omnaest.utils.webservice.rest.RestClientFactory.Parameter;
import org.omnaest.utils.webservice.rest.RestClientFactory.QueryParameter;
import org.omnaest.utils.webservice.rest.RestClientFactoryTest.MockJSR311Interface.SubResource;

/**
 * @see RestClientFactory
 * @author Omnaest
 */
public class RestClientFactoryTest
{
  /* ********************************************** Variables ********************************************** */
  private String            baseAddress       = "http://localhost:8080/root/rest";
  private RestClientFactory restClientFactory = new RestClientFactoryTestImpl( this.baseAddress );
  
  private DataRecord        dataRecord        = new DataRecord();
  private Object            returnValue       = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class DataRecord
  {
    public URI             baseAddress        = null;
    public String          pathRelative       = null;
    public HttpMethod      httpMethod         = null;
    public List<Parameter> parameterList      = null;
    public Class<?>        returnType         = null;
    public String[]        consumesMediaTypes = null;
    public String[]        producesMediaTypes = null;
  }
  
  protected class RestClientFactoryTestImpl extends RestClientFactory
  {
    
    public RestClientFactoryTestImpl( String baseAddress )
    
    {
      super( baseAddress, new RestInterfaceMethodInvocationHandler()
      {
        
        @SuppressWarnings("unchecked")
        @Override
        public <T> T handleMethodInvocation( URI baseAddress,
                                             String pathRelative,
                                             HttpMethod httpMethod,
                                             List<Parameter> parameterList,
                                             Class<T> returnType,
                                             String[] consumesMediaTypes,
                                             String[] producesMediaTypes )
        {
          //
          RestClientFactoryTest.this.dataRecord.baseAddress = baseAddress;
          RestClientFactoryTest.this.dataRecord.httpMethod = httpMethod;
          RestClientFactoryTest.this.dataRecord.parameterList = parameterList;
          RestClientFactoryTest.this.dataRecord.pathRelative = pathRelative;
          RestClientFactoryTest.this.dataRecord.returnType = returnType;
          RestClientFactoryTest.this.dataRecord.producesMediaTypes = producesMediaTypes;
          RestClientFactoryTest.this.dataRecord.consumesMediaTypes = consumesMediaTypes;
          
          //
          return (T) RestClientFactoryTest.this.returnValue;
        }
      } );
    }
    
  }
  
  @Path("mockJSR311Interface")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public static interface MockJSR311Interface
  {
    
    public static interface SubResource
    {
      @GET
      public String getValue();
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Path("determineFieldString")
    public String determineFieldString( @QueryParam("number") Integer number );
    
    @POST
    @Path("{field}/determineFieldDouble")
    public Double determineFieldDouble( @PathParam("field") String field, String value );
    
    @Path("delete")
    @DELETE
    public void delete();
    
    @PUT
    public void newInstance();
    
    @Path("subresource/{identifier}")
    public SubResource getSubResource( @PathParam("identifier") String identifier );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNewRestClient()
  {
    //
    MockJSR311Interface mockJSR311Interface = this.restClientFactory.newRestClient( MockJSR311Interface.class );
    
    //
    {
      //
      this.returnValue = "lala";
      String determineFieldString = mockJSR311Interface.determineFieldString( 123 );
      assertEquals( this.returnValue, determineFieldString );
      assertEquals( this.baseAddress, this.dataRecord.baseAddress.toString() );
      assertEquals( "mockJSR311Interface/determineFieldString", this.dataRecord.pathRelative );
      assertEquals( HttpMethod.GET, this.dataRecord.httpMethod );
      assertEquals( 1, this.dataRecord.parameterList.size() );
      assertArrayEquals( new String[] { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON },
                         this.dataRecord.producesMediaTypes );
      assertArrayEquals( new String[] { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON },
                         this.dataRecord.consumesMediaTypes );
      assertEquals( String.class, this.dataRecord.returnType );
      
      //
      Parameter parameter = this.dataRecord.parameterList.iterator().next();
      assertTrue( parameter instanceof QueryParameter );
      
      QueryParameter queryParameter = (QueryParameter) parameter;
      assertEquals( "number", queryParameter.getKey() );
      assertEquals( "123", queryParameter.getValue() );
    }
    {
      //
      this.returnValue = 123.12;
      Double determineFieldDouble = mockJSR311Interface.determineFieldDouble( "456", "value" );
      assertEquals( this.returnValue, determineFieldDouble );
      assertEquals( this.baseAddress, this.dataRecord.baseAddress.toString() );
      assertEquals( "mockJSR311Interface/456/determineFieldDouble", this.dataRecord.pathRelative );
      assertEquals( HttpMethod.POST, this.dataRecord.httpMethod );
      assertArrayEquals( new String[] { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON },
                         this.dataRecord.producesMediaTypes );
      assertArrayEquals( new String[] { MediaType.APPLICATION_JSON }, this.dataRecord.consumesMediaTypes );
      assertEquals( 1, this.dataRecord.parameterList.size() );
      
      //
      Parameter parameter = this.dataRecord.parameterList.iterator().next();
      assertTrue( parameter instanceof BodyParameter );
      BodyParameter bodyParameter = (BodyParameter) parameter;
      Object value = bodyParameter.getValue();
      assertEquals( "value", value );
    }
    {
      //
      mockJSR311Interface.delete();
      assertEquals( HttpMethod.DELETE, this.dataRecord.httpMethod );
      
      //
      mockJSR311Interface.newInstance();
      assertEquals( HttpMethod.PUT, this.dataRecord.httpMethod );
      assertEquals( this.baseAddress, this.dataRecord.baseAddress.toString() );
      assertEquals( "mockJSR311Interface", this.dataRecord.pathRelative );
    }
    {
      //
      SubResource subResource = mockJSR311Interface.getSubResource( "identifier" );
      assertNotNull( subResource );
      
      //
      this.returnValue = "test";
      String value = subResource.getValue();
      assertEquals( this.returnValue, value );
      
      //      
      assertEquals( HttpMethod.GET, this.dataRecord.httpMethod );
      assertEquals( this.baseAddress + "/mockJSR311Interface/subresource/identifier", this.dataRecord.baseAddress.toString() );
      assertEquals( "", this.dataRecord.pathRelative );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewRestClientPerformance()
  {
    for ( int ii = 0; ii < 100000; ii++ )
    {
      //
      MockJSR311Interface mockJSR311Interface = this.restClientFactory.newRestClient( MockJSR311Interface.class );
      
      //
      {
        //
        this.returnValue = "lala";
        String determineFieldString = mockJSR311Interface.determineFieldString( 123 );
        assertEquals( this.returnValue, determineFieldString );
        assertEquals( this.baseAddress, this.dataRecord.baseAddress.toString() );
        assertEquals( "mockJSR311Interface/determineFieldString", this.dataRecord.pathRelative );
        assertEquals( HttpMethod.GET, this.dataRecord.httpMethod );
        assertEquals( 1, this.dataRecord.parameterList.size() );
        assertArrayEquals( new String[] { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON },
                           this.dataRecord.producesMediaTypes );
        assertArrayEquals( new String[] { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON },
                           this.dataRecord.consumesMediaTypes );
        assertEquals( String.class, this.dataRecord.returnType );
        
        //
        Parameter parameter = this.dataRecord.parameterList.iterator().next();
        assertTrue( parameter instanceof QueryParameter );
        
        QueryParameter queryParameter = (QueryParameter) parameter;
        assertEquals( "number", queryParameter.getKey() );
        assertEquals( "123", queryParameter.getValue() );
      }
    }
  }
}
