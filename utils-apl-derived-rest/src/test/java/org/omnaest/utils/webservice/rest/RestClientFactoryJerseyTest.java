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
package org.omnaest.utils.webservice.rest;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.http.HttpHost;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey.Apache4ClientConfiguration;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey.Authentification;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey.Configuration;

/**
 * @see RestClientFactoryJersey
 * @author Omnaest
 */
public class RestClientFactoryJerseyTest
{
  @XmlRootElement
  private static class Entity
  {
    private String fieldString;
    
    @SuppressWarnings("unused")
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Entity [fieldString=" );
      builder.append( this.fieldString );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  static interface SubResource
  {
    @GET
    public String getValue();
    
    @Path("{identifier}")
    public SubResource getSubResource( @PathParam("identifier") String identifier );
  }
  
  @Path("/service/subservice")
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  static interface RestService extends SubResource
  {
    
    @POST
    @Path("container")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public void container( Entity entity );
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void diverseParams( @MatrixParam("matrix") List<String> matrix,
                               @HeaderParam("user-agent") String headerParam,
                               @CookieParam("cookieParam") Cookie cookieParam,
                               @CookieParam("cookieParam2") String cookieValue );
    
    @Path("subresource/{identifier}")
    public SubResource getSubResource( @PathParam("identifier") String identifier );
  }
  
  @Test
  @Ignore
  public void testRestClientFactoryJersey() throws Exception
  {
    final Configuration configuration = new Configuration().setActivateJSONPojoMapping( true )
                                                           .setAuthentification( new Authentification().setBasicAuthentification( "username",
                                                                                                                                  "password" ) );
    final String baseAddress = "http://localhost:8888/webapp";
    RestClientFactoryJersey restClientFactoryJersey = new RestClientFactoryJersey( baseAddress, configuration );
    
    RestService restService = restClientFactoryJersey.newRestClient( RestService.class );
    Entity entity = new Entity();
    entity.setFieldString( "test" );
    restService.container( entity );
  }
  
  @Test
  @Ignore
  public void testRestClientFactoryJerseyAndHttpClient4() throws Exception
  {
    final Apache4ClientConfiguration configuration = new Apache4ClientConfiguration().setActivateJSONPojoMapping( true )
                                                                                     .setProxy( new HttpHost( "localhost", 8888 ) )
                                                                                     .setAuthentification( new Authentification().setBasicAuthentification( "username",
                                                                                                                                                            "password" ) );
    final String baseAddress = "http://localhost:18888/webapp";
    RestClientFactoryJersey restClientFactoryJersey = new RestClientFactoryJersey( baseAddress, configuration );
    
    RestService restService = restClientFactoryJersey.newRestClient( RestService.class );
    Entity entity = new Entity();
    entity.setFieldString( "test" );
    restService.container( entity );
  }
  
  @Test
  @Ignore
  public void testDiverseParams()
  {
    final String baseAddress = "http://localhost:8888/webapp";
    RestClientFactoryJersey restClientFactoryJersey = new RestClientFactoryJersey( baseAddress );
    
    RestService restService = restClientFactoryJersey.newRestClient( RestService.class );
    
    List<String> matrix = Arrays.asList( "value1", "value2" );
    String headerParam = "user-agent value";
    Cookie cookieParam = new Cookie( "cookiekey", "cookievalue" );
    String cookieValue = "other cookie value";
    restService.diverseParams( matrix, headerParam, cookieParam, cookieValue );
  }
  
  @Test
  @Ignore
  public void testSubResource()
  {
    final Apache4ClientConfiguration configuration = new Apache4ClientConfiguration().setActivateJSONPojoMapping( true )
                                                                                     .setProxy( new HttpHost( "localhost", 8888 ) );
    
    final String baseAddress = "http://localhost:8888/webapp/rest2";
    RestClientFactoryJersey restClientFactoryJersey = new RestClientFactoryJersey( baseAddress, configuration );
    
    RestService restService = restClientFactoryJersey.newRestClient( RestService.class );
    
    SubResource subResource = restService.getSubResource( "identifier1" ).getSubResource( "identifier2" );
    String value = subResource.getValue();
    System.out.println();
  }
}
