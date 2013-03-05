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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.cluster.ClusterState.ClusterNodeState;

/**
 * Builder for a {@link ClusterState}
 * 
 * @author Omnaest
 */
public class ClusterStateBuilder
{
  
  private Map<Server, ClusterNodeState> serverToStateMap = new LinkedHashMap<Server, ClusterState.ClusterNodeState>();
  
  public ClusterStateBuilder( ClusterState clusterState )
  {
    super();
    if ( clusterState != null )
    {
      Set<Server> serverSet = clusterState.getServerSet();
      for ( Server server : serverSet )
      {
        ClusterNodeState clusterNodeState = clusterState.getClusterNodeState( server );
        this.serverToStateMap.put( server, clusterNodeState );
      }
    }
  }
  
  /**
   * @param master
   *          {@link Server}
   * @return
   */
  public ClusterStateBuilder setMaster( Server master )
  {
    //find old master and disable him
    ClusterNodeState oldMasterNodeState = null;
    for ( Server masterCandidate : this.serverToStateMap.keySet() )
    {
      ClusterNodeState clusterNodeStateMasterCandidate = this.serverToStateMap.get( masterCandidate );
      if ( clusterNodeStateMasterCandidate != null && clusterNodeStateMasterCandidate.isMaster() )
      {
        oldMasterNodeState = clusterNodeStateMasterCandidate;
      }
    }
    {
      //
      final Server oldMaster = oldMasterNodeState != null ? oldMasterNodeState.getServer() : null;
      if ( oldMaster == null || !ObjectUtils.equals( master, oldMaster ) )
      {
        //
        if ( oldMaster != null )
        {
          boolean isMaster = false;
          int ping = oldMasterNodeState.getPing();
          ClusterNodeState newOldMasterNodeState = new ClusterNodeState( oldMaster, isMaster, ping );
          this.serverToStateMap.put( oldMaster, newOldMasterNodeState );
        }
        
        //
        if ( master != null )
        {
          ClusterNodeState clusterNodeState = this.serverToStateMap.get( master );
          final int ping = clusterNodeState.getPing();
          final boolean isMaster = true;
          ClusterNodeState newClusterNodeState = new ClusterNodeState( master, isMaster, ping );
          this.serverToStateMap.put( master, newClusterNodeState );
        }
      }
      
    }
    return this;
  }
  
  /**
   * @return {@link ClusterState}
   */
  public ClusterState build()
  {
    return new ClusterState( this.serverToStateMap );
  }
  
  /**
   * @param server
   *          {@link Server}
   * @param ping
   * @return this
   */
  public ClusterStateBuilder updateServerState( Server server, int ping )
  {
    final ClusterNodeState clusterNodeState = this.serverToStateMap.get( server );
    final boolean isMaster = clusterNodeState.isMaster();
    ClusterNodeState newClusterNodeState = new ClusterNodeState( server, isMaster, ping );
    this.serverToStateMap.put( server, newClusterNodeState );
    return this;
  }
  
  public ClusterStateBuilder( ClusterConfiguration clusterConfiguration )
  {
    if ( clusterConfiguration != null )
    {
      List<Server> serverList = clusterConfiguration.getServerList();
      if ( serverList != null )
      {
        for ( Server server : serverList )
        {
          final int ping = -1;
          final boolean isMaster = false;
          this.serverToStateMap.put( server, new ClusterNodeState( server, isMaster, ping ) );
        }
      }
    }
  }
}
