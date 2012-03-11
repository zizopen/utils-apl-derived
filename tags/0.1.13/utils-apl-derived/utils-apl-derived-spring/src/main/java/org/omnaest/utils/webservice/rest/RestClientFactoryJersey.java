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

import java.net.URI;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * Implementation for a {@link RestClientFactory} for Jersey.<br>
 * <br>
 * This implementation does currently <b>not support matrix parameters</b>
 * 
 * @author Omnaest
 */
public class RestClientFactoryJersey extends RestClientFactory
{
  
  /**
   * @param baseAddress
   */
  public RestClientFactoryJersey( String baseAddress )
  {
    this( baseAddress, null );
  }
  
  /**
   * @see RestClientFactory.Authentification
   * @param baseAddress
   * @param authentification
   */
  public RestClientFactoryJersey( String baseAddress, final Authentification authentification )
  {
    super( baseAddress, new RestInterfaceMethodInvocationHandler()
    {
      
      @SuppressWarnings("cast")
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
        T retval = null;
        
        //
        try
        {
          //
          WebResource webResource = null;
          
          //see http://java.net/projects/jersey/sources/svn/content/trunk/jersey/samples/https-clientserver-grizzly/src/test/java/com/sun/jersey/samples/https_grizzly/MainTest.java?rev=5453
          ClientConfig clientConfig = new DefaultClientConfig();
          
          //
          if ( authentification instanceof HTTPSAuthentification )
          {
            //
            HTTPSAuthentification httpsAuthentification = (HTTPSAuthentification) authentification;
            HostnameVerifier hostnameVerifier = httpsAuthentification.getHostnameVerifier();
            SSLContext sslContext = httpsAuthentification.getSslContext();
            
            //
            clientConfig.getProperties().put( HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                                              new HTTPSProperties( hostnameVerifier, sslContext ) );
            
          }
          
          //
          Client client = Client.create( clientConfig );
          
          //
          if ( authentification instanceof BasicAuthentification )
          {
            //
            BasicAuthentification basicAuthentification = (BasicAuthentification) authentification;
            String username = basicAuthentification.getUsername();
            String password = basicAuthentification.getPassword();
            
            //
            client.addFilter( new HTTPBasicAuthFilter( username, password ) );
          }
          
          //
          webResource = client.resource( baseAddress );
          webResource = webResource.path( pathRelative );
          
          //
          webResource.accept( producesMediaTypes );
          if ( consumesMediaTypes.length > 0 )
          {
            webResource.type( consumesMediaTypes[0] );
          }
          
          //
          Object entity = null;
          if ( parameterList != null )
          {
            for ( Parameter parameter : parameterList )
            {
              if ( parameter instanceof QueryParameter )
              {
                //
                QueryParameter queryParameter = (QueryParameter) parameter;
                
                //
                String key = queryParameter.getKey();
                String value = queryParameter.getValue();
                
                //
                webResource = webResource.queryParam( key, value );
              }
              else if ( parameter instanceof MatrixParameter )
              {
                //
                //MatrixParameter matrixParameter = (MatrixParameter) parameter;
                
                //
                //String key = matrixParameter.getKey();
                //Collection<Object> valueCollection = matrixParameter.getValueCollection();
                
                //FIXME missing matrix parameter
              }
              else if ( parameter instanceof BodyParameter )
              {
                //
                BodyParameter bodyParameter = (BodyParameter) parameter;
                entity = bodyParameter.getValue();
              }
            }
          }
          
          //
          if ( HttpMethod.GET.equals( httpMethod ) )
          {
            retval = (T) webResource.get( returnType );
          }
          else if ( HttpMethod.PUT.equals( httpMethod ) )
          {
            if ( entity == null )
            {
              retval = (T) webResource.put( returnType );
            }
            else
            {
              retval = (T) webResource.put( returnType, entity );
            }
          }
          else if ( HttpMethod.POST.equals( httpMethod ) )
          {
            if ( entity == null )
            {
              retval = (T) webResource.post( returnType );
            }
            else
            {
              retval = (T) webResource.post( returnType, entity );
            }
          }
          else if ( HttpMethod.DELETE.equals( httpMethod ) )
          {
            retval = (T) webResource.delete( returnType );
          }
          
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
        
        // 
        return retval;
      }
      
    } );
  }
}
