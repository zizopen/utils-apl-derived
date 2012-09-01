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

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * Implementation for a {@link RestClientFactory} for Jersey.<br>
 * <br>
 * This is an experimental implementation, please test the functionality carefully if used in production.
 * 
 * @see RestClientFactory
 * @author Omnaest
 */
public class RestClientFactoryJersey extends RestClientFactory
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class InvocationHandlerArbitraryClient implements RestInterfaceMethodInvocationHandler
  {
    private final BasicConfiguration configuration;
    private final Client             client;
    
    public InvocationHandlerArbitraryClient( BasicConfiguration configuration, Client client )
    {
      super();
      this.configuration = configuration;
      this.client = client;
    }
    
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
      final ExceptionHandler exceptionHandler = this.configuration != null ? this.configuration.getExceptionHandler() : null;
      try
      {
        //
        WebResource webResource = this.client.resource( baseAddress ).path( pathRelative );
        Object entity = null;
        {
          //
          if ( this.configuration != null )
          {
            final WebResourceModificator webResourceModificator = this.configuration.getWebResourceModificator();
            if ( webResourceModificator != null )
            {
              WebResource modifiedWebResource = webResourceModificator.modifyWebResource( webResource );
              if ( modifiedWebResource != null )
              {
                webResource = modifiedWebResource;
              }
            }
          }
          
          //
          if ( parameterList != null )
          {
            for ( Parameter parameter : parameterList )
            {
              if ( parameter instanceof QueryParameter )
              {
                //
                QueryParameter queryParameter = (QueryParameter) parameter;
                final String key = queryParameter.getKey();
                final String value = queryParameter.getValue();
                webResource = webResource.queryParam( key, value );
              }
              else if ( parameter instanceof BodyParameter )
              {
                //
                final BodyParameter bodyParameter = (BodyParameter) parameter;
                entity = bodyParameter.getValue();
              }
            }
          }
        }
        
        //
        Builder webRequestBuilder = webResource.accept( producesMediaTypes );
        if ( consumesMediaTypes.length > 0 )
        {
          webRequestBuilder = webRequestBuilder.type( consumesMediaTypes[0] );
        }
        
        if ( parameterList != null )
        {
          for ( Parameter parameter : parameterList )
          {
            if ( parameter instanceof CookieParameter )
            {
              //
              final CookieParameter cookieParameter = (CookieParameter) parameter;
              webRequestBuilder = webRequestBuilder.cookie( cookieParameter.getCookie() );
            }
            else if ( parameter instanceof HeaderParameter )
            {
              //
              final HeaderParameter headerParameter = (HeaderParameter) parameter;
              final String key = headerParameter.getKey();
              final String value = headerParameter.getValue();
              webRequestBuilder = webRequestBuilder.header( key, value );
            }
          }
        }
        
        //
        if ( HttpMethod.GET.equals( httpMethod ) )
        {
          retval = webRequestBuilder.get( returnType );
        }
        else if ( HttpMethod.PUT.equals( httpMethod ) )
        {
          if ( entity == null )
          {
            retval = webRequestBuilder.put( returnType );
          }
          else
          {
            retval = webRequestBuilder.put( returnType, entity );
          }
        }
        else if ( HttpMethod.POST.equals( httpMethod ) )
        {
          if ( entity == null )
          {
            retval = webRequestBuilder.post( returnType );
          }
          else
          {
            retval = webRequestBuilder.post( returnType, entity );
          }
        }
        else if ( HttpMethod.DELETE.equals( httpMethod ) )
        {
          retval = webRequestBuilder.delete( returnType );
        }
      }
      catch ( Exception e )
      {
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
      }
      
      // 
      return retval;
    }
    
  }
  
  private static class InvocationHandlerApache4Client extends InvocationHandlerWithClientFactory
  {
    public InvocationHandlerApache4Client( final Apache4ClientConfiguration configuration )
    {
      super( configuration, new FactoryParameterized<Client, ClientConfig>()
      {
        @Override
        public Client newInstance( ClientConfig clientConfig )
        {
          HttpClient httpClient = new DefaultHttpClient();
          
          if ( configuration != null )
          {
            HttpHost proxy = configuration.getProxy();
            if ( proxy != null )
            {
              httpClient.getParams().setParameter( ConnRoutePNames.DEFAULT_PROXY, proxy );
            }
            
            HttpClientModificator httpClientModificator = configuration.getHttpClientModificator();
            if ( httpClientModificator != null )
            {
              httpClientModificator.modify( httpClient );
            }
          }
          
          final boolean preemtiveBasicAuth = false;
          final CookieStore cookieStore = new BasicCookieStore();
          return new ApacheHttpClient4( new ApacheHttpClient4Handler( httpClient, cookieStore, preemtiveBasicAuth ), clientConfig );
        }
      } );
      
    }
    
  }
  
  private static class InvocationHandlerJerseyClient extends InvocationHandlerWithClientFactory
  {
    InvocationHandlerJerseyClient( Configuration configuration )
    {
      super( configuration, new FactoryParameterized<Client, ClientConfig>()
      {
        @Override
        public Client newInstance( ClientConfig clientConfig )
        {
          return Client.create( clientConfig );
        }
      } );
    }
    
  }
  
  private static class InvocationHandlerWithClientFactory extends InvocationHandlerArbitraryClient
  {
    
    InvocationHandlerWithClientFactory( Configuration configuration, FactoryParameterized<Client, ClientConfig> clientFactory )
    {
      super( configuration, newClient( configuration, clientFactory ) );
    }
    
    private static Client newClient( Configuration configuration, FactoryParameterized<Client, ClientConfig> clientFactory )
    {
      //
      final Authentification authentification = configuration != null ? configuration.getAuthentification() : null;
      
      //see http://java.net/projects/jersey/sources/svn/content/trunk/jersey/samples/https-clientserver-grizzly/src/test/java/com/sun/jersey/samples/https_grizzly/MainTest.java?rev=5453
      final ClientConfig clientConfig = new DefaultClientConfig();
      {
        //
        if ( authentification != null )
        {
          final HTTPSContext httpsContext = authentification.getHttpsContext();
          if ( httpsContext != null )
          {
            final HostnameVerifier hostnameVerifier = httpsContext.getHostnameVerifier();
            final SSLContext sslContext = httpsContext.getSslContext();
            
            clientConfig.getProperties().put( HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                                              new HTTPSProperties( hostnameVerifier, sslContext ) );
          }
        }
        
        //
        if ( configuration != null )
        {
          //
          final boolean activateJSONPojoMapping = configuration.isActivateJSONPojoMapping();
          if ( activateJSONPojoMapping )
          {
            clientConfig.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
          }
          
          //
          final ClientConfigModificator clientConfigModificator = configuration.getClientConfigModificator();
          if ( clientConfigModificator != null )
          {
            clientConfigModificator.modifyClientConfig( clientConfig );
          }
        }
      }
      
      final Client client = clientFactory.newInstance( clientConfig );
      {
        //
        if ( authentification != null )
        {
          final BasicAuthentification basicAuthentification = authentification.getBasicAuthentification();
          if ( basicAuthentification != null )
          {
            String username = basicAuthentification.getUsername();
            String password = basicAuthentification.getPassword();
            
            client.addFilter( new HTTPBasicAuthFilter( username, password ) );
          }
        }
        
        //
        if ( configuration != null )
        {
          final ClientModificator clientModificator = configuration.getClientModificator();
          if ( clientModificator != null )
          {
            clientModificator.modifyClient( client );
          }
        }
      }
      
      return client;
    }
    
  }
  
  public static class BasicConfiguration
  {
    private WebResourceModificator webResourceModificator = null;
    private ExceptionHandler       exceptionHandler       = null;
    
    WebResourceModificator getWebResourceModificator()
    {
      return this.webResourceModificator;
    }
    
    public BasicConfiguration setWebResourceModificator( WebResourceModificator webResourceModificator )
    {
      this.webResourceModificator = webResourceModificator;
      return this;
    }
    
    ExceptionHandler getExceptionHandler()
    {
      return this.exceptionHandler;
    }
    
    public BasicConfiguration setExceptionHandler( ExceptionHandler exceptionHandler )
    {
      this.exceptionHandler = exceptionHandler;
      return this;
    }
    
  }
  
  /**
   * {@link Configuration} for the {@link RestClientFactoryJersey}
   * 
   * @see Authentification
   * @author Omnaest
   */
  public static class Configuration extends BasicConfiguration
  {
    private Authentification        authentification        = null;
    private ClientConfigModificator clientConfigModificator = null;
    private ClientModificator       clientModificator       = null;
    private boolean                 activateJSONPojoMapping = false;
    
    ClientConfigModificator getClientConfigModificator()
    {
      return this.clientConfigModificator;
    }
    
    public Configuration setClientConfigModificator( ClientConfigModificator clientConfigModificator )
    {
      this.clientConfigModificator = clientConfigModificator;
      return this;
    }
    
    @Override
    public Configuration setWebResourceModificator( WebResourceModificator webResourceModificator )
    {
      super.setWebResourceModificator( webResourceModificator );
      return this;
    }
    
    ClientModificator getClientModificator()
    {
      return this.clientModificator;
    }
    
    public Configuration setClientModificator( ClientModificator clientModificator )
    {
      this.clientModificator = clientModificator;
      return this;
    }
    
    boolean isActivateJSONPojoMapping()
    {
      return this.activateJSONPojoMapping;
    }
    
    public Configuration setActivateJSONPojoMapping( boolean activateJSONPojoMapping )
    {
      this.activateJSONPojoMapping = activateJSONPojoMapping;
      return this;
    }
    
    Authentification getAuthentification()
    {
      return this.authentification;
    }
    
    public Configuration setAuthentification( Authentification authentification )
    {
      this.authentification = authentification;
      return this;
    }
    
    @Override
    public Configuration setExceptionHandler( ExceptionHandler exceptionHandler )
    {
      super.setExceptionHandler( exceptionHandler );
      return this;
    }
    
  }
  
  public static class Apache4ClientConfiguration extends Configuration
  {
    private HttpHost              proxy                 = null;
    private HttpClientModificator httpClientModificator = null;
    
    HttpHost getProxy()
    {
      return this.proxy;
    }
    
    public Apache4ClientConfiguration setProxy( HttpHost proxy )
    {
      this.proxy = proxy;
      return this;
    }
    
    HttpClientModificator getHttpClientModificator()
    {
      return this.httpClientModificator;
    }
    
    public Apache4ClientConfiguration setHttpClientModificator( HttpClientModificator httpClientModificator )
    {
      this.httpClientModificator = httpClientModificator;
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setClientConfigModificator( ClientConfigModificator clientConfigModificator )
    {
      
      super.setClientConfigModificator( clientConfigModificator );
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setWebResourceModificator( WebResourceModificator webResourceModificator )
    {
      super.setWebResourceModificator( webResourceModificator );
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setClientModificator( ClientModificator clientModificator )
    {
      super.setClientModificator( clientModificator );
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setActivateJSONPojoMapping( boolean activateJSONPojoMapping )
    {
      super.setActivateJSONPojoMapping( activateJSONPojoMapping );
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setAuthentification( Authentification authentification )
    {
      super.setAuthentification( authentification );
      return this;
    }
    
    @Override
    public Apache4ClientConfiguration setExceptionHandler( ExceptionHandler exceptionHandler )
    {
      super.setExceptionHandler( exceptionHandler );
      return this;
    }
    
  }
  
  public static interface HttpClientModificator
  {
    public void modify( HttpClient httpClient );
  }
  
  public static interface ClientConfigModificator
  {
    public void modifyClientConfig( ClientConfig clientConfig );
  }
  
  public static interface WebResourceModificator
  {
    public WebResource modifyWebResource( WebResource webResource );
  }
  
  public static interface ClientModificator
  {
    public void modifyClient( Client client );
  }
  
  /**
   * {@link Authentification} context used by the {@link RestClientFactoryJersey}
   * 
   * @author Omnaest
   */
  public static class Authentification
  {
    private BasicAuthentification basicAuthentification = null;
    private HTTPSContext          httpsContext          = null;
    
    public Authentification setBasicAuthentification( BasicAuthentification basicAuthentification )
    {
      this.basicAuthentification = basicAuthentification;
      return this;
    }
    
    public Authentification setBasicAuthentification( String username, String password )
    {
      this.basicAuthentification = new BasicAuthentification( username, password );
      return this;
    }
    
    public Authentification setHttpsContext( HTTPSContext httpsContext )
    {
      this.httpsContext = httpsContext;
      return this;
    }
    
    BasicAuthentification getBasicAuthentification()
    {
      return this.basicAuthentification;
    }
    
    HTTPSContext getHttpsContext()
    {
      return this.httpsContext;
    }
  }
  
  /**
   * @see Authentification
   * @author Omnaest
   */
  public static class BasicAuthentification
  {
    /* ********************************************** Variables ********************************************** */
    protected String username = null;
    protected String password = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param username
     * @param password
     */
    public BasicAuthentification( String username, String password )
    {
      super();
      this.username = username;
      this.password = password;
    }
    
    public String getUsername()
    {
      return this.username;
    }
    
    public String getPassword()
    {
      return this.password;
    }
  }
  
  /**
   * @see Authentification
   * @author Omnaest
   */
  public static class HTTPSContext
  {
    /* ********************************************** Variables ********************************************** */
    protected SSLContext       sslContext       = null;
    protected HostnameVerifier hostnameVerifier = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public HTTPSContext( SSLContext sslContext, HostnameVerifier hostnameVerifier )
    {
      super();
      this.sslContext = sslContext;
      this.hostnameVerifier = hostnameVerifier;
    }
    
    public HostnameVerifier getHostnameVerifier()
    {
      return this.hostnameVerifier;
    }
    
    public SSLContext getSslContext()
    {
      return this.sslContext;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  /**
   * @param baseAddress
   */
  public RestClientFactoryJersey( String baseAddress )
  {
    this( baseAddress, null );
  }
  
  /**
   * @see RestClientFactoryJersey.Configuration
   * @param baseAddress
   * @param configuration
   *          {@link Configuration}
   */
  public RestClientFactoryJersey( String baseAddress, final Configuration configuration )
  {
    super( baseAddress, new InvocationHandlerJerseyClient( configuration ) );
  }
  
  /**
   * @see RestClientFactoryJersey.Configuration
   * @param baseAddress
   * @param client
   *          {@link Client}
   * @param configuration
   *          {@link Configuration}
   */
  public RestClientFactoryJersey( String baseAddress, Client client, BasicConfiguration configuration )
  {
    super( baseAddress, new InvocationHandlerArbitraryClient( configuration, client ) );
  }
  
  public RestClientFactoryJersey( String baseAddress, Apache4ClientConfiguration configuration )
  {
    super( baseAddress, new InvocationHandlerApache4Client( configuration ) );
  }
  
}
