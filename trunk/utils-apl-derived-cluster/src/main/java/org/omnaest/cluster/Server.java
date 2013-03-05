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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "server")
@XmlAccessorType(XmlAccessType.FIELD)
public class Server implements Serializable
{
  private static final long serialVersionUID = 4877703417931337737L;
  private String            host;
  private int               port;
  private String            context          = null;
  
  public Server( String host, int port )
  {
    this();
    this.host = host;
    this.port = port;
  }
  
  private Server()
  {
    super();
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Server [host=" );
    builder.append( this.host );
    builder.append( ", port=" );
    builder.append( this.port );
    builder.append( ", context=" );
    builder.append( this.context );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.context == null ) ? 0 : this.context.hashCode() );
    result = prime * result + ( ( this.host == null ) ? 0 : this.host.hashCode() );
    result = prime * result + this.port;
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
    if ( !( obj instanceof Server ) )
    {
      return false;
    }
    Server other = (Server) obj;
    if ( this.context == null )
    {
      if ( other.context != null )
      {
        return false;
      }
    }
    else if ( !this.context.equals( other.context ) )
    {
      return false;
    }
    if ( this.host == null )
    {
      if ( other.host != null )
      {
        return false;
      }
    }
    else if ( !this.host.equals( other.host ) )
    {
      return false;
    }
    if ( this.port != other.port )
    {
      return false;
    }
    return true;
  }
  
  public String getHost()
  {
    return this.host;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public String getContext()
  {
    return this.context;
  }
  
  public Server setContext( String context )
  {
    this.context = context;
    return this;
  }
  
}
