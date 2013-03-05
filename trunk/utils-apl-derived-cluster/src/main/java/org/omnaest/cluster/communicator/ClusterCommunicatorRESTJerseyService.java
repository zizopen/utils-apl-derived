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
import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("cluster")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public interface ClusterCommunicatorRESTJerseyService extends Serializable
{
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Data implements Serializable
  {
    private static final long serialVersionUID = 1616017012768765044L;
    
    @XmlElement
    private byte[]            data;
    
    @SuppressWarnings("rawtypes")
    @XmlElement
    private Class             type;
    
    public byte[] getData()
    {
      return this.data;
    }
    
    @SuppressWarnings("rawtypes")
    public Data( byte[] data, Class type )
    {
      this();
      this.data = data;
    }
    
    Data()
    {
      super();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Data [data=" );
      builder.append( Arrays.toString( this.data ) );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( "]" );
      return builder.toString();
    }
    
    @SuppressWarnings("rawtypes")
    public Class getType()
    {
      return this.type;
    }
    
  }
  
  @Path("data/{identifier}")
  @GET
  public Data get( @PathParam(value = "identifier") String identifier );
  
  @Path("data")
  @PUT
  public void put( Data data );
  
  @Path("ping")
  @GET
  public Data ping();
}
