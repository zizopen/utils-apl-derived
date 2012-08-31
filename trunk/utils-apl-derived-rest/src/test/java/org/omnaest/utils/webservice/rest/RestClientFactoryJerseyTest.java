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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
  
  @Path("service")
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  private static interface RestService
  {
    @POST
    @Path("container")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public void container( Entity entity );
  }
  
  @Test
  @Ignore
  public void testRestClientFactoryJersey() throws Exception
  {
    final Configuration configuration = new Configuration().setActivateJSONPojoMapping( true );
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
}
