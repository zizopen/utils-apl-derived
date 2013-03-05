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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omnaest.cluster.Server;

/**
 * {@link ClusterCommunicator} which uses a static {@link Map} to allow interaction between different instances
 * 
 * @see ClusterCommunicator
 * @author Omnaest
 */
public class ClusterCommunicatorInJVM extends ClusterCommunicatorAbstract
{
  private static final long            serialVersionUID   = -424949993788814708L;
  private static Map<Server, Receiver> serverToHandlerMap = new ConcurrentHashMap<Server, ClusterCommunicator.Receiver>();
  
  @Override
  public void enableReceiver( Server localServer, Receiver receiver )
  {
    if ( !serverToHandlerMap.containsKey( localServer ) )
    {
      serverToHandlerMap.put( localServer, receiver );
    }
    super.enableReceiver( localServer, receiver );
  }
  
  @Override
  public Sender getSender( final Server destination )
  {
    return new Sender()
    {
      private static final long serialVersionUID = 949409851713820659L;
      
      @Override
      public void put( Object object )
      {
        final Receiver receiver = getReceiver( destination );
        if ( receiver != null )
        {
          receiver.handlePut( object );
        }
      }
      
      private Receiver getReceiver( final Server destination )
      {
        return destination == null ? null : serverToHandlerMap.get( destination );
      }
      
      @Override
      public int ping()
      {
        final Receiver receiver = getReceiver( destination );
        if ( receiver != null )
        {
          return 0;
        }
        return -1;
      }
      
      @Override
      public Object get( String identifier )
      {
        final Receiver receiver = getReceiver( destination );
        if ( receiver != null )
        {
          return receiver.handleGet( identifier );
        }
        return null;
      }
    };
  }
  
  @Override
  public void disableReceiver( Server localServer )
  {
    serverToHandlerMap.remove( localServer );
  }
  
}
