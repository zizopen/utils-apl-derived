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
package org.omnaest.cluster.communicator.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.omnaest.utils.events.exception.ExceptionHandler;

public class Server
{
  private final ServerSocket serverSocket;
  private ExceptionHandler   exceptionHandler = null;
  
  public class ConnectedClient extends Communicator
  {
    protected final Socket clientSocket;
    
    private ConnectedClient( InputStream inputStream, OutputStream outputStream, Socket clientSocket,
                             ExceptionHandler exceptionHandler )
    {
      super( inputStream, outputStream, exceptionHandler );
      this.clientSocket = clientSocket;
    }
    
    @Override
    public void close()
    {
      super.close();
      if ( this.clientSocket != null )
      {
        try
        {
          this.clientSocket.close();
        }
        catch ( IOException e )
        {
          this.handleExcpetion( e );
        }
      }
    }
    
    public boolean isPing( Object object )
    {
      return object instanceof Ping;
    }
    
    public boolean isAck( Object object )
    {
      return object instanceof Ack;
    }
    
  }
  
  public Server( int port )
                           throws Exception
  {
    super();
    
    try
    {
      this.serverSocket = new ServerSocket( port );
    }
    catch ( Exception e )
    {
      throw new Exception( e );
    }
  }
  
  public ConnectedClient acceptNextClient()
  {
    ConnectedClient retval = null;
    {
      Socket clientSocket = null;
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try
      {
        clientSocket = Server.this.serverSocket.accept();
        outputStream = new BufferedOutputStream( clientSocket.getOutputStream() );
        inputStream = new BufferedInputStream( clientSocket.getInputStream() );
        retval = new ConnectedClient( inputStream, outputStream, clientSocket, this.exceptionHandler );
      }
      catch ( Exception e )
      {
        if ( outputStream != null )
        {
          try
          {
            outputStream.close();
          }
          catch ( IOException e1 )
          {
            this.handleException( e1 );
          }
        }
        if ( inputStream != null )
        {
          try
          {
            inputStream.close();
          }
          catch ( IOException e1 )
          {
            this.handleException( e1 );
          }
        }
        if ( clientSocket != null )
        {
          try
          {
            clientSocket.close();
          }
          catch ( IOException e1 )
          {
            this.handleException( e1 );
          }
        }
        this.handleException( e );
      }
    }
    return retval;
  }
  
  private void handleException( Exception e )
  {
    if ( this.exceptionHandler != null )
    {
      this.exceptionHandler.handleException( e );
    }
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public Server setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }
  
  public void close()
  {
    try
    {
      this.serverSocket.close();
    }
    catch ( IOException e )
    {
      this.handleException( e );
    }
  }
}
