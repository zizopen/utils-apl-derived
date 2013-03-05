/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.cluster.communicator;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.http.HttpHost;
import org.omnaest.cluster.Server;
import org.omnaest.cluster.store.MarshallingStrategy;
import org.omnaest.cluster.store.MarshallingStrategy.MarshallingException;
import org.omnaest.cluster.store.MarshallingStrategy.UnmarshallingException;
import org.omnaest.utils.operation.OperationUtils;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.time.DurationCapture;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey.Apache4ClientConfiguration;
import org.omnaest.utils.webservice.rest.RestClientFactoryJersey.Authentification;

/**
 * {@link ClusterCommunicator} which uses a JSR-311 interface to allow e.g. jersey to provide a communication base
 * 
 * @author Omnaest
 */
public class ClusterCommunicatorRESTJersey extends ClusterCommunicatorAbstract implements ClusterCommunicatorRESTJerseyService
{
  private static final long   serialVersionUID = -5774435079187032666L;
  
  private String              protocol         = "http";
  private Authentification    authentification;
  
  private MarshallingStrategy marshallingStrategy;
  
  private Server              proxy;
  private int                 timeout          = 1000;
  
  @Override
  @GET
  public Data get( String identifier )
  {
    Object object = this.handleGet( identifier );
    try
    {
      return marshalData( object );
    }
    catch ( MarshallingException e )
    {
      this.handleException( e );
      return new Data();
    }
  }
  
  private Data marshalData( Object object ) throws MarshallingException
  {
    return new Data( this.marshallingStrategy.marshal( object ), object.getClass() );
  }
  
  @Override
  @PUT
  public void put( Data data )
  {
    try
    {
      this.handlePut( unmarshalData( data ) );
    }
    catch ( UnmarshallingException e )
    {
      this.handleException( e );
    }
  }
  
  @SuppressWarnings("unchecked")
  private Object unmarshalData( Data data ) throws UnmarshallingException
  {
    return this.marshallingStrategy.unmarshal( data.getData(), data.getType() );
  }
  
  @Override
  public Sender getSender( Server destination )
  {
    final String baseAddress = buildClienRestUrl( destination );
    final Apache4ClientConfiguration configuration = new Apache4ClientConfiguration().setActivateJSONPojoMapping( true )
                                                                                     .setAuthentification( this.authentification );
    if ( this.proxy != null )
    {
      configuration.setProxy( new HttpHost( this.proxy.getHost(), this.proxy.getPort() ) );
    }
    final ClusterCommunicatorRESTJerseyService service = new RestClientFactoryJersey( baseAddress, configuration ).newRestClient( ClusterCommunicatorRESTJerseyService.class );
    return new Sender()
    {
      private static final long serialVersionUID = -2882369553906585857L;
      
      @Override
      public void put( final Object object )
      {
        this.executeWithTimeout( new OperationIntrinsic()
        {
          @Override
          public void execute()
          {
            try
            {
              service.put( marshalData( object ) );
            }
            catch ( MarshallingException e )
            {
              handleException( e );
            }
          }
        } );
      }
      
      @Override
      public Object get( final String identifier )
      {
        final ElementHolder<Object> retvalHolder = new ElementHolder<Object>();
        try
        {
          this.executeWithTimeout( new OperationIntrinsic()
          {
            @Override
            public void execute()
            {
              try
              {
                retvalHolder.setElement( unmarshalData( service.get( identifier ) ) );
              }
              catch ( UnmarshallingException e )
              {
                handleException( e );
              }
            }
          } );
        }
        catch ( Exception e )
        {
          handleException( e );
          return null;
        }
        return retvalHolder;
      }
      
      @Override
      public int ping()
      {
        DurationCapture durationCapture = new DurationCapture().startTimeMeasurement();
        final AtomicBoolean ping = new AtomicBoolean( false );
        try
        {
          this.executeWithTimeout( new OperationIntrinsic()
          {
            @Override
            public void execute()
            {
              ping.set( service.ping() != null );
            }
          } );
        }
        catch ( Exception e )
        {
          handleException( e );
        }
        return ping.get() ? (int) durationCapture.stopTimeMeasurement().getDurationInMilliseconds() : -1;
      }
      
      private boolean executeWithTimeout( OperationIntrinsic operation )
      {
        try
        {
          return OperationUtils.executeWithTimeout( operation, Executors.newSingleThreadExecutor(),
                                                    ClusterCommunicatorRESTJersey.this.timeout, TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
          handleException( e );
          return false;
        }
      }
    };
  }
  
  protected String buildClienRestUrl( Server destination )
  {
    return this.protocol + "://" + destination.getHost() + ":" + destination.getPort() + "/" + destination.getContext();
  }
  
  public ClusterCommunicatorRESTJersey setMarshallingStrategy( MarshallingStrategy marshallingStrategy )
  {
    this.marshallingStrategy = marshallingStrategy;
    return this;
  }
  
  public ClusterCommunicatorRESTJersey setAuthentification( Authentification authentification )
  {
    this.authentification = authentification;
    return this;
  }
  
  @Override
  public void disableReceiver( Server localServer )
  {
  }
  
  @Override
  @Path("ping")
  @GET
  public Data ping()
  {
    return new Data();
  }
  
  public ClusterCommunicatorRESTJersey setProtocol( String protocol )
  {
    this.protocol = protocol;
    return this;
  }
  
  public ClusterCommunicatorRESTJersey setProxy( Server proxy )
  {
    this.proxy = proxy;
    return this;
  }
  
  /**
   * Timeout in milliseconds
   * 
   * @param timeout
   * @return
   */
  public ClusterCommunicatorRESTJersey setTimeout( int timeout )
  {
    this.timeout = timeout;
    return this;
  }
  
}
