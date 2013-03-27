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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.omnaest.cluster.Server;
import org.omnaest.cluster.communicator.ClusterCommunicator.Receiver;
import org.omnaest.cluster.store.MarshallingStrategyJavaSerialization;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.structure.element.ElementHolder;

/**
 * @see ClusterCommunicatorTCP
 * @author Omnaest
 */
public class ClusterCommunicatorTCPTest
{
  @Test
  public void test()
  {
    final Server localServer1 = new Server( "localhost", 47001 );
    ClusterCommunicator clusterCommunicator1 = new ClusterCommunicatorTCP().setMarshallingStrategy( new MarshallingStrategyJavaSerialization() )
                                                                           .setTimeout( 90000 )
                                                                           .setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    
    final ElementHolder<Object> putElementHolder = new ElementHolder<Object>();
    final ElementHolder<String> getElementHolder = new ElementHolder<String>();
    final ElementHolder<Object> getResponseElementHolder = new ElementHolder<Object>();
    final Receiver receiver = new Receiver()
    {
      private static final long serialVersionUID = -8345599134076633490L;
      
      @Override
      public void handlePut( Object object )
      {
        putElementHolder.setElement( object );
      }
      
      @Override
      public Object handleGet( String identifier )
      {
        getElementHolder.setElement( identifier );
        return getResponseElementHolder.getElement();
      }
    };
    clusterCommunicator1.enableReceiver( localServer1, receiver );
    
    final Server localServer2 = new Server( "localhost", 47002 );
    ClusterCommunicator clusterCommunicator2 = new ClusterCommunicatorTCP().setMarshallingStrategy( new MarshallingStrategyJavaSerialization() )
                                                                           .setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    clusterCommunicator2.enableReceiver( localServer2, receiver );
    
    for ( int ii = 0; ii < 20; ii++ )
    {
      //ping
      assertTrue( clusterCommunicator1.getSender( localServer2 ).ping() >= 0 );
      
      //put
      clusterCommunicator1.getSender( localServer2 ).put( "test" );
      assertEquals( "test", putElementHolder.getElement() );
      
      //get
      getResponseElementHolder.setElement( "response" );
      Object object = clusterCommunicator1.getSender( localServer2 ).get( "requestkey1" );
      assertEquals( "response", object );
      assertEquals( "requestkey1", getElementHolder.getElement() );
    }
    
    //
    clusterCommunicator1.disableReceiver( localServer1 );
    clusterCommunicator2.disableReceiver( localServer2 );
    
  }
}
