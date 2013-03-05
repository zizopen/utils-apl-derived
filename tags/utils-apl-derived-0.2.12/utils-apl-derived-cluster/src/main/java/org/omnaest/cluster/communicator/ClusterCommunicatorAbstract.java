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

import org.omnaest.cluster.Server;
import org.omnaest.utils.events.exception.ExceptionHandler;

public abstract class ClusterCommunicatorAbstract implements ClusterCommunicator
{
  private static final long  serialVersionUID = 8981989878022657124L;
  private Receiver           receiver;
  protected ExceptionHandler exceptionHandler = null;
  
  protected void handlePut( Object object )
  {
    if ( this.receiver != null )
    {
      this.receiver.handlePut( object );
    }
  }
  
  protected Object handleGet( String identifier )
  {
    Object retval = null;
    if ( this.receiver != null )
    {
      retval = this.receiver.handleGet( identifier );
    }
    return retval;
  }
  
  @Override
  public void enableReceiver( Server localServer, Receiver receiver )
  {
    this.receiver = receiver;
  }
  
  protected void handleException( Exception e )
  {
    if ( this.exceptionHandler != null )
    {
      this.exceptionHandler.handleException( e );
    }
  }
  
  public ClusterCommunicator setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }
}
