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

import org.omnaest.cluster.Cluster;
import org.omnaest.cluster.Server;

/**
 * Abstraction of the {@link Cluster} communication
 * 
 * @author Omnaest
 */
public interface ClusterCommunicator extends Serializable
{
  public static interface Receiver extends Serializable
  {
    public void handlePut( Object object );
    
    public Object handleGet( String identifier );
  }
  
  public static interface Sender extends Serializable
  {
    public void put( Object object );
    
    public Object get( String identifier );
    
    /**
     * Returns the delay to the destination {@link Server} or -1 if the destination {@link Server} is not reachable
     * 
     * @return
     */
    public int ping();
  }
  
  public Sender getSender( Server destination );
  
  public void enableReceiver( Server localServer, Receiver receiver );
  
  public void disableReceiver( Server localServer );
}
