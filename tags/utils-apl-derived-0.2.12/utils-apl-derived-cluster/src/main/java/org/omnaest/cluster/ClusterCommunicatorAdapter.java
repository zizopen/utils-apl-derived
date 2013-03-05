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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.cluster.communicator.ClusterCommunicator;
import org.omnaest.cluster.communicator.ClusterCommunicator.Receiver;
import org.omnaest.cluster.store.ClusterStoreProvider.ClusterStoreIdentifier;

class ClusterCommunicatorAdapter implements Serializable
{
  private static final long   serialVersionUID = -2836346829131470267L;
  private ClusterCommunicator clusterCommunicator;
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterStoreRemove implements Serializable
  {
    private static final long serialVersionUID = -667732075097431286L;
    private Class<?>          type;
    private String[]          qualifiers;
    
    public ClusterStoreRemove( Class<?> type, String[] qualifiers )
    {
      this();
      this.type = type;
      this.qualifiers = qualifiers;
    }
    
    private ClusterStoreRemove()
    {
      super();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreRemove [type=" );
      builder.append( this.type );
      builder.append( ", qualifiers=" );
      builder.append( Arrays.toString( this.qualifiers ) );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode( this.qualifiers );
      result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
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
      if ( !( obj instanceof ClusterStoreRemove ) )
      {
        return false;
      }
      ClusterStoreRemove other = (ClusterStoreRemove) obj;
      if ( !Arrays.equals( this.qualifiers, other.qualifiers ) )
      {
        return false;
      }
      if ( this.type == null )
      {
        if ( other.type != null )
        {
          return false;
        }
      }
      else if ( !this.type.equals( other.type ) )
      {
        return false;
      }
      return true;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
    public String[] getQualifiers()
    {
      return this.qualifiers;
    }
    
  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterStoreElementSet implements Serializable
  {
    private static final long serialVersionUID = -3200720388035698697L;
    private Class<?>          type;
    private String[]          qualifiers;
    private Object            instance;
    
    public ClusterStoreElementSet( Class<?> type, String[] qualifiers, Object instance )
    {
      this();
      this.type = type;
      this.qualifiers = qualifiers;
      this.instance = instance;
    }
    
    private ClusterStoreElementSet()
    {
      super();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreElementSet [type=" );
      builder.append( this.type );
      builder.append( ", qualifiers=" );
      builder.append( Arrays.toString( this.qualifiers ) );
      builder.append( ", instance=" );
      builder.append( this.instance );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.instance == null ) ? 0 : this.instance.hashCode() );
      result = prime * result + Arrays.hashCode( this.qualifiers );
      result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
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
      if ( !( obj instanceof ClusterStoreElementSet ) )
      {
        return false;
      }
      ClusterStoreElementSet other = (ClusterStoreElementSet) obj;
      if ( this.instance == null )
      {
        if ( other.instance != null )
        {
          return false;
        }
      }
      else if ( !this.instance.equals( other.instance ) )
      {
        return false;
      }
      if ( !Arrays.equals( this.qualifiers, other.qualifiers ) )
      {
        return false;
      }
      if ( this.type == null )
      {
        if ( other.type != null )
        {
          return false;
        }
      }
      else if ( !this.type.equals( other.type ) )
      {
        return false;
      }
      return true;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
    public String[] getQualifiers()
    {
      return this.qualifiers;
    }
    
    public Object getInstance()
    {
      return this.instance;
    }
    
  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterStoreDatas implements Serializable
  {
    private static final long      serialVersionUID     = 8652191944245354948L;
    
    private List<ClusterStoreData> clusterStoreDataList = new ArrayList<ClusterCommunicatorAdapter.ClusterStoreData>();
    
    public ClusterStoreDatas( List<ClusterStoreData> clusterStoreDataList )
    {
      this();
      this.clusterStoreDataList.addAll( clusterStoreDataList );
    }
    
    private ClusterStoreDatas()
    {
      super();
    }
    
    public List<ClusterStoreData> getClusterStoreDataList()
    {
      return this.clusterStoreDataList;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreDatas [clusterStoreDataList=" );
      builder.append( this.clusterStoreDataList );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterStoreData implements Serializable
  {
    private static final long         serialVersionUID = -1751749442124968268L;
    
    private ClusterStoreIdentifier<?> clusterStoreIdentifier;
    private Object                    data;
    
    public ClusterStoreData( ClusterStoreIdentifier<?> clusterStoreIdentifier, Object data )
    {
      this();
      this.clusterStoreIdentifier = clusterStoreIdentifier;
      this.data = data;
    }
    
    private ClusterStoreData()
    {
      super();
    }
    
    public ClusterStoreIdentifier<?> getClusterStoreIdentifier()
    {
      return this.clusterStoreIdentifier;
    }
    
    public Object getData()
    {
      return this.data;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreData [clusterStoreIdentifier=" );
      builder.append( this.clusterStoreIdentifier );
      builder.append( ", data=" );
      builder.append( this.data );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  public ClusterCommunicatorAdapter( ClusterCommunicator clusterCommunicator )
  {
    super();
    this.clusterCommunicator = clusterCommunicator;
  }
  
  public ClusterState getClusterState( Server master )
  {
    Object object = this.clusterCommunicator.getSender( master ).get( "CLUSTERSTATE" );
    return (ClusterState) object;
  }
  
  public void putClusterState( Server client, ClusterState clusterState )
  {
    this.clusterCommunicator.getSender( client ).put( clusterState );
  }
  
  public int ping( Server server )
  {
    return this.clusterCommunicator.getSender( server ).ping();
  }
  
  public boolean isAvailable( Server server )
  {
    return this.ping( server ) >= 0;
  }
  
  public static interface ClusterStateHandler extends Serializable
  {
    public void setClusterState( ClusterState clusterState );
  }
  
  public static interface ClusterStoreHandler extends Serializable
  {
    public void set( Class<?> type, String[] qualifiers, Object instance );
    
    public void remove( Class<?> type, String[] qualifiers );
    
    public void setStoreData( ClusterStoreDatas clusterStoreDatas );
  }
  
  public void enableReceiver( Server localServer,
                              final ClusterStateHandler clusterStateHandler,
                              final ClusterStoreHandler clusterStoreHandler )
  {
    final Receiver receiver = new Receiver()
    {
      private static final long serialVersionUID = 7333066216291367374L;
      
      @Override
      public void handlePut( Object object )
      {
        if ( object instanceof ClusterState )
        {
          clusterStateHandler.setClusterState( (ClusterState) object );
        }
        else if ( object instanceof ClusterStoreRemove )
        {
          ClusterStoreRemove clusterStoreRemove = (ClusterStoreRemove) object;
          Class<?> type = clusterStoreRemove.getType();
          String[] qualifiers = clusterStoreRemove.getQualifiers();
          clusterStoreHandler.remove( type, qualifiers );
        }
        else if ( object instanceof ClusterStoreElementSet )
        {
          ClusterStoreElementSet clusterStoreElementSet = (ClusterStoreElementSet) object;
          Class<?> type = clusterStoreElementSet.getType();
          String[] qualifiers = clusterStoreElementSet.getQualifiers();
          Object instance = clusterStoreElementSet.getInstance();
          clusterStoreHandler.set( type, qualifiers, instance );
        }
        else if ( object instanceof ClusterStoreDatas )
        {
          ClusterStoreDatas clusterStoreDatas = (ClusterStoreDatas) object;
          clusterStoreHandler.setStoreData( clusterStoreDatas );
        }
      }
      
      @Override
      public Object handleGet( String identifier )
      {
        throw new UnsupportedOperationException();
      }
      
    };
    this.clusterCommunicator.enableReceiver( localServer, receiver );
  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class MasterAcceptRequest implements Serializable
  {
    private static final long serialVersionUID = 3374355264374789384L;
    private Server            master;
    
    public MasterAcceptRequest( Server master )
    {
      this();
      this.master = master;
    }
    
    private MasterAcceptRequest()
    {
      super();
    }
    
    public Server getMaster()
    {
      return this.master;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "MasterAcceptRequest [master=" );
      builder.append( this.master );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  public void disbaleReceiver( Server localServer )
  {
    this.clusterCommunicator.disableReceiver( localServer );
  }
  
  public <T> void clusterStoreSetElement( Class<T> type, String[] qualifiers, T instance, Server destination )
  {
    this.clusterCommunicator.getSender( destination ).put( new ClusterStoreElementSet( type, qualifiers, instance ) );
  }
  
  public <T> void clusterStoreRemoveElement( Class<T> type, String[] qualifiers, Server destination )
  {
    this.clusterCommunicator.getSender( destination ).put( new ClusterStoreRemove( type, qualifiers ) );
  }
  
  public void sendClusterStoreData( ClusterStoreDatas clusterStoreDatas, Server destination )
  {
    this.clusterCommunicator.getSender( destination ).put( clusterStoreDatas );
  }
  
}
