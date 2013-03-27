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
package org.omnaest.cluster.store;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public interface ClusterStoreProvider extends Serializable
{
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ClusterStoreIdentifier<T> implements Serializable
  {
    private static final long serialVersionUID = 4773971151091603502L;
    
    private Class<T>          type;
    private String[]          qualifiers;
    
    public ClusterStoreIdentifier( Class<T> type, String... qualifiers )
    {
      this();
      this.type = type;
      this.qualifiers = qualifiers;
    }
    
    private ClusterStoreIdentifier()
    {
      super();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreIdentifier [type=" );
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
      if ( !( obj instanceof ClusterStoreIdentifier ) )
      {
        return false;
      }
      @SuppressWarnings("rawtypes")
      ClusterStoreIdentifier other = (ClusterStoreIdentifier) obj;
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
    
    public Class<T> getType()
    {
      return this.type;
    }
    
    public String[] getQualifiers()
    {
      return this.qualifiers;
    }
    
  }
  
  public <T> ClusterStore<T> getClusterStore( ClusterStoreIdentifier<T> clusterStoreIdentifier );
  
  public <T> ClusterStore<T> getClusterStore( Class<T> type, String... qualifiers );
  
  public <T> ClusterStoreIdentifier<?>[] getClusterStoreIdentifiers();
  
  public void clear();
  
  public void executeWriteAtomical( Runnable runnable );
  
  public void executeReadAtomical( Runnable runnable );
}
