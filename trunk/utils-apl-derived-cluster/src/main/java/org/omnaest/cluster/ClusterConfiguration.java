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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ClusterConfiguration implements Serializable
{
  private static final long serialVersionUID       = 3467444603535051856L;
  @XmlElementWrapper(name = "servers")
  @XmlElementRef
  private List<Server>      serverList             = new ArrayList<Server>();
  
  @XmlElement
  private double            clusterAvailableFactor = 0.66;
  
  public List<Server> getServerList()
  {
    return this.serverList;
  }
  
  public ClusterConfiguration setServerList( List<Server> serverList )
  {
    this.serverList.clear();
    this.serverList.addAll( serverList );
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ClusterConfiguration [serverList=" );
    builder.append( this.serverList );
    builder.append( ", clusterAvailableFactor=" );
    builder.append( this.clusterAvailableFactor );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits( this.clusterAvailableFactor );
    result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
    result = prime * result + ( ( this.serverList == null ) ? 0 : this.serverList.hashCode() );
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
    if ( !( obj instanceof ClusterConfiguration ) )
    {
      return false;
    }
    ClusterConfiguration other = (ClusterConfiguration) obj;
    if ( Double.doubleToLongBits( this.clusterAvailableFactor ) != Double.doubleToLongBits( other.clusterAvailableFactor ) )
    {
      return false;
    }
    if ( this.serverList == null )
    {
      if ( other.serverList != null )
      {
        return false;
      }
    }
    else if ( !this.serverList.equals( other.serverList ) )
    {
      return false;
    }
    return true;
  }
  
  public double getClusterAvailableFactor()
  {
    return this.clusterAvailableFactor;
  }
  
  /**
   * Factor of how many slave nodes have to be available related to the number of all slaves (=clustersize-1), so that a master
   * becomes active<br>
   * <br>
   * Default is 66%
   * 
   * @param clusterAvailableFactor
   * @return
   */
  public ClusterConfiguration setClusterAvailableFactor( double clusterAvailableFactor )
  {
    this.clusterAvailableFactor = clusterAvailableFactor;
    return this;
  }
  
}
