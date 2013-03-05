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
package org.omnaest.cluster;

import java.io.Serializable;

import org.omnaest.cluster.store.ClusterStore;

public interface Cluster extends Serializable
{
  /**
   * {@link Exception} which indicates that the {@link Cluster} is disconnected while another {@link Thread} is waiting on
   * connection.
   * 
   * @author Omnaest
   */
  public static class ClusterDisconnectedException extends Exception
  {
    private static final long serialVersionUID = 2959043098646076987L;
    
    public ClusterDisconnectedException( Server localServer )
    {
      super( localServer + " Cluster is not connected" );
    }
  }
  
  /**
   * @return {@link ClusterState}
   */
  public ClusterState getClusterState();
  
  /**
   * @param type
   * @param qualifiers
   * @return {@link ClusterStore}
   */
  public <T> ClusterStore<T> getClusterStore( Class<T> type, String... qualifiers );
  
  public Cluster disconnect();
  
  public Cluster connect();
  
  public boolean isMaster();
  
  /**
   * @see #isAvailable()
   * @throws InterruptedException
   * @throws ClusterDisconnectedException
   */
  public void awaitUntilClusterIsAvailable() throws InterruptedException,
                                            ClusterDisconnectedException;
  
  /**
   * Returns true if the {@link Cluster} is available
   * 
   * @see #awaitUntilClusterIsAvailable()
   * @return
   */
  public boolean isAvailable();
}
