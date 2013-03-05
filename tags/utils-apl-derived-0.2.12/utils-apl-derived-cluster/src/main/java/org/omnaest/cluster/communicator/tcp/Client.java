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
import java.net.Socket;

import org.omnaest.utils.events.exception.ExceptionHandler;

/**
 * @author Omnaest
 */
public class Client extends Communicator
{
  protected Socket clientSocket;
  
  /**
   * @see Client
   * @param host
   * @param port
   */
  public Client( String host, int port )
  {
    this( host, port, null );
  }
  
  /**
   * @see Client
   * @param host
   * @param port
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public Client( String host, int port, ExceptionHandler exceptionHandler )
  {
    super();
    
    try
    {
      this.exceptionHandler = exceptionHandler;
      this.clientSocket = new Socket( host, port );
      this.outputStream = new BufferedOutputStream( this.clientSocket.getOutputStream() );
      this.inputStream = new BufferedInputStream( this.clientSocket.getInputStream() );
    }
    catch ( Exception e )
    {
      this.close();
      this.handleExcpetion( e );
    }
  }
  
  public void close()
  {
    if ( this.inputStream != null )
    {
      try
      {
        this.inputStream.close();
      }
      catch ( Exception e )
      {
        this.handleExcpetion( e );
      }
    }
    if ( this.outputStream != null )
    {
      try
      {
        this.outputStream.close();
      }
      catch ( IOException e )
      {
        this.handleExcpetion( e );
      }
    }
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
  
}
