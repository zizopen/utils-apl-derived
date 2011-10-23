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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Implementation for a {@link RestClientFactory} for Jersey
 * 
 * @author Omnaest
 */
public class RestClientFactoryJersey extends RestClientFactory
{
  
  public RestClientFactoryJersey( String baseAddress )
                                                      throws URISyntaxException
  
  {
    super( baseAddress, new RestInterfaceMethodInvocationHandler()
    {
      
      @SuppressWarnings("cast")
      @Override
      public <T> T handleMethodInvocation( URI baseAddress,
                                           String pathRelative,
                                           HttpMethod httpMethod,
                                           List<Parameter> parameterList,
                                           Class<T> returnType )
      {
        //
        T retval = null;
        
        //
        try
        {
          //
          WebResource webResource = null;
          
          //
          ClientConfig clientConfig = new DefaultClientConfig();
          Client client = Client.create( clientConfig );
          
          //
          webResource = client.resource( baseAddress );
          webResource.path( pathRelative );
          
          //
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
                webResource.queryParam( key, value );
              }
              else if ( parameter instanceof MatrixParameter )
              {
                //
                MatrixParameter matrixParameter = (MatrixParameter) parameter;
                
                //
                String key = matrixParameter.getKey();
                Collection<Object> valueCollection = matrixParameter.getValueCollection();
                
                //FIXME missing matrix parameter
              }
              else if ( parameter instanceof BodyParameter )
              {
                //
                BodyParameter bodyParameter = (BodyParameter) parameter;
                webResource.entity( bodyParameter.getValue() );
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
            retval = (T) webResource.put( returnType );
          }
          else if ( HttpMethod.POST.equals( httpMethod ) )
          {
            retval = (T) webResource.post( returnType );
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
