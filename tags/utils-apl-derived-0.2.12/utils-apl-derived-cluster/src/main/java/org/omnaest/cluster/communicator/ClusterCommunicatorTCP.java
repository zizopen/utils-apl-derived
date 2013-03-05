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

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.omnaest.cluster.Server;
import org.omnaest.cluster.communicator.ClusterCommunicatorRESTJerseyService.Data;
import org.omnaest.cluster.communicator.tcp.Client;
import org.omnaest.cluster.communicator.tcp.Server.ConnectedClient;
import org.omnaest.cluster.store.MarshallingStrategy;
import org.omnaest.cluster.store.MarshallingStrategy.MarshallingException;
import org.omnaest.cluster.store.MarshallingStrategy.UnmarshallingException;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.operation.OperationUtils;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.time.DurationCapture;

/**
 * {@link ClusterCommunicator} which uses the TCP/IP protocol to communicate
 * 
 * @author Omnaest
 */
public class ClusterCommunicatorTCP extends ClusterCommunicatorAbstract
{
  private int                       timeout          = 1000;
  
  private static final long         serialVersionUID = -5774323479187032666L;
  
  private MarshallingStrategy       marshallingStrategy;
  private transient ExecutorService executorService  = null;
  private int                       threads          = 10;
  
  public static class DataGetRequest implements Serializable
  {
    private static final long serialVersionUID = -9160566266567235397L;
    
    private String            identifier;
    
    public DataGetRequest( String identifier )
    {
      this();
      this.identifier = identifier;
    }
    
    private DataGetRequest()
    {
      super();
    }
    
    public String getIdentifier()
    {
      return this.identifier;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "DataGetRequest [identifier=" );
      builder.append( this.identifier );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
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
    final String host = destination.getHost();
    final int port = destination.getPort();
    final ExceptionHandler exceptionHandler = this.exceptionHandler;
    return new Sender()
    {
      private static final long serialVersionUID = -228236955390658857L;
      
      @Override
      public void put( final Object object )
      {
        executeWithTimeout( host, port, exceptionHandler, new OperationIntrinsic()
        {
          @Override
          public void execute()
          {
            try
            {
              final Client client = new Client( host, port, exceptionHandler );
              try
              {
                client.send( marshalData( object ) );
                client.receiveAck();
              }
              finally
              {
                client.close();
              }
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
        executeWithTimeout( host, port, exceptionHandler, new OperationIntrinsic()
        {
          @Override
          public void execute()
          {
            try
            {
              final Client client = new Client( host, port, exceptionHandler );
              try
              {
                client.send( new DataGetRequest( identifier ) );
                final Object data = unmarshalData( (Data) client.receive() );
                retvalHolder.setElement( data );
              }
              finally
              {
                client.close();
              }
            }
            catch ( UnmarshallingException e )
            {
              handleException( e );
            }
          }
        } );
        return retvalHolder.getElement();
      }
      
      @Override
      public int ping()
      {
        DurationCapture durationCapture = new DurationCapture().startTimeMeasurement();
        boolean success = executeWithTimeout( host, port, exceptionHandler, new OperationIntrinsic()
        {
          @Override
          public void execute()
          {
            try
            {
              final Client client = new Client( host, port, exceptionHandler );
              try
              {
                client.sendPing();
                client.receiveAck();
              }
              finally
              {
                client.close();
              }
            }
            catch ( Exception e )
            {
              handleException( e );
            }
          }
        } );
        durationCapture.stopTimeMeasurement();
        return success ? (int) durationCapture.getDurationInMilliseconds() : -1;
      }
      
      private boolean executeWithTimeout( final String host,
                                          final int port,
                                          final ExceptionHandler exceptionHandler,
                                          OperationIntrinsic operation )
      {
        try
        {
          return OperationUtils.executeWithTimeout( operation, ClusterCommunicatorTCP.this.executorService,
                                                    ClusterCommunicatorTCP.this.timeout, TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
          handleException( e );
          return false;
        }
      }
    };
  }
  
  public ClusterCommunicatorTCP setMarshallingStrategy( MarshallingStrategy marshallingStrategy )
  {
    this.marshallingStrategy = marshallingStrategy;
    return this;
  }
  
  @Override
  public void disableReceiver( Server localServer )
  {
    if ( this.executorService != null )
    {
      try
      {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination( this.timeout, TimeUnit.MILLISECONDS );
      }
      catch ( Exception e )
      {
        handleException( e );
      }
    }
  }
  
  @Override
  public void enableReceiver( Server localServer, final Receiver receiver )
  {
    if ( this.executorService != null )
    {
      this.executorService.shutdownNow();
    }
    this.executorService = Executors.newFixedThreadPool( this.threads );
    try
    {
      final int port = localServer.getPort();
      final org.omnaest.cluster.communicator.tcp.Server server = new org.omnaest.cluster.communicator.tcp.Server( port );
      this.executorService.submit( new Runnable()
      {
        @Override
        public void run()
        {
          while ( !Thread.interrupted() )
          {
            ConnectedClient connectedClient = server.acceptNextClient();
            {
              Object object = connectedClient.receive();
              if ( connectedClient.isPing( object ) )
              {
                connectedClient.sendAck();
              }
              else if ( object instanceof Data )
              {
                Data data = (Data) object;
                try
                {
                  final Object unmarshalData = unmarshalData( data );
                  receiver.handlePut( unmarshalData );
                }
                catch ( Exception e )
                {
                  handleException( e );
                }
                connectedClient.sendAck();
              }
              else if ( object instanceof DataGetRequest )
              {
                DataGetRequest dataGetRequest = (DataGetRequest) object;
                Object responeObject = receiver.handleGet( dataGetRequest.getIdentifier() );
                try
                {
                  connectedClient.send( marshalData( responeObject ) );
                }
                catch ( MarshallingException e )
                {
                  handleException( e );
                }
              }
            }
            connectedClient.close();
          }
        }
      } );
    }
    catch ( Exception e )
    {
    }
    super.enableReceiver( localServer, receiver );
  }
  
  public ClusterCommunicatorTCP setThreads( int threads )
  {
    this.threads = threads;
    return this;
  }
  
  /**
   * Sets the timeout for communication in milliseconds
   * 
   * @param timeout
   * @return
   */
  public ClusterCommunicatorTCP setTimeout( int timeout )
  {
    this.timeout = timeout;
    return this;
  }
  
}
