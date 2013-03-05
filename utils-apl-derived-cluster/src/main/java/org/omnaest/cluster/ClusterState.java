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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ClusterState implements Serializable
{
  private static final long             serialVersionUID = -7639020502783737759L;
  
  @XmlElementWrapper(name = "nodes")
  private Map<Server, ClusterNodeState> serverToStateMap = new HashMap<Server, ClusterNodeState>();
  
  @XmlType
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterNodeState implements Serializable
  {
    private static final long serialVersionUID = -7221342630208276170L;
    
    private int               ping;
    private boolean           isMaster;
    private Server            server;
    
    public ClusterNodeState( Server server, boolean isMaster, int ping )
    {
      this();
      this.server = server;
      this.isMaster = isMaster;
      this.ping = ping;
    }
    
    private ClusterNodeState()
    {
      super();
    }
    
    public int getPing()
    {
      return this.ping;
    }
    
    public boolean isAvailable()
    {
      return this.ping >= 0;
    }
    
    public boolean isMaster()
    {
      return this.isMaster;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterNodeState [ping=" );
      builder.append( this.ping );
      builder.append( ", isMaster=" );
      builder.append( this.isMaster );
      builder.append( ", server=" );
      builder.append( this.server );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( this.isMaster ? 1231 : 1237 );
      result = prime * result + this.ping;
      result = prime * result + ( ( this.server == null ) ? 0 : this.server.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof ClusterNodeState ) )
      {
        return false;
      }
      ClusterNodeState other = (ClusterNodeState) obj;
      if ( this.isMaster != other.isMaster )
      {
        return false;
      }
      if ( this.ping != other.ping )
      {
        return false;
      }
      if ( this.server == null )
      {
        if ( other.server != null )
        {
          return false;
        }
      }
      else if ( !this.server.equals( other.server ) )
      {
        return false;
      }
      return true;
    }
    
    public Server getServer()
    {
      return this.server;
    }
    
  }
  
  public ClusterState( Map<Server, ClusterNodeState> serverToStateMap )
  {
    this();
    this.serverToStateMap = serverToStateMap;
  }
  
  private ClusterState()
  {
    super();
  }
  
  public ClusterNodeState getClusterNodeState( Server server )
  {
    return this.serverToStateMap.get( server );
  }
  
  public Set<Server> getServerSet()
  {
    return Collections.unmodifiableSet( this.serverToStateMap.keySet() );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ClusterState [serverToStateMap=" );
    builder.append( this.serverToStateMap );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.serverToStateMap == null ) ? 0 : this.serverToStateMap.hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof ClusterState ) )
    {
      return false;
    }
    ClusterState other = (ClusterState) obj;
    if ( this.serverToStateMap == null )
    {
      if ( other.serverToStateMap != null )
      {
        return false;
      }
    }
    else if ( !this.serverToStateMap.equals( other.serverToStateMap ) )
    {
      return false;
    }
    return true;
  }
  
  public ClusterNodeState getMasterServerNodeState()
  {
    ClusterNodeState retval = null;
    if ( this.serverToStateMap != null )
    {
      for ( Server server : this.serverToStateMap.keySet() )
      {
        ClusterNodeState clusterNodeState = this.serverToStateMap.get( server );
        if ( clusterNodeState != null )
        {
          boolean master = clusterNodeState.isMaster();
          if ( master )
          {
            retval = clusterNodeState;
          }
        }
      }
    }
    return retval;
  }
  
  public int size()
  {
    return this.serverToStateMap.size();
  }
}
