/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.listener.Listener.ListenerExtendedEvent;
import org.omnaest.utils.listener.Listener.ListenerExtendedResult;


/**
 * @see ListenerManager
 * @author Omnaest
 */
public class ListenerManagerTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  protected class ListenerParameterA extends ListenerExtendedEvent<Object, Object, Object>
  {
    public ListenerParameterA( Object source, Object event, Object data )
    {
      super( source, event, data );
    }
  }
  
  protected class ListenerParameterB extends ListenerExtendedEvent<String, Object, Long>
  {
    public ListenerParameterB( String source, Object event, Long data )
    {
      super( source, event, data );
    }
  }
  
  protected class ListenerReturnInfoA extends ListenerExtendedResult<Object, Object>
  {
    public ListenerReturnInfoA( Object client, Object result )
    {
      super( client, result );
      
    }
  }
  
  protected class ListenerReturnInfoB extends ListenerExtendedResult<String, Double>
  {
    public ListenerReturnInfoB( String client, Double result )
    {
      super( client, result );
    }
  }
  
  /* ********************************************** Methods ********************************************** */

  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testConnectToListenerManager()
  {
    //
    ListenerManager<ListenerParameterA, ListenerReturnInfoA> listenerManagerSource = new ListenerManager<ListenerManagerTest.ListenerParameterA, ListenerManagerTest.ListenerReturnInfoA>();
    ListenerManager<ListenerParameterB, ListenerReturnInfoB> listenerManagerFacade = new ListenerManager<ListenerManagerTest.ListenerParameterB, ListenerManagerTest.ListenerReturnInfoB>();
    
    //
    ListenerAdapter<ListenerParameterA, ListenerReturnInfoA, ListenerParameterB, ListenerReturnInfoB> listenerAdapter = new ListenerAdapter<ListenerManagerTest.ListenerParameterA, ListenerManagerTest.ListenerReturnInfoA, ListenerManagerTest.ListenerParameterB, ListenerManagerTest.ListenerReturnInfoB>()
    {
      @Override
      public List<ListenerParameterB> adaptParameter( ListenerParameterA otherParameter )
      {
        //
        List<ListenerParameterB> retlist = new ArrayList<ListenerManagerTest.ListenerParameterB>();
        
        //        
        String source = (String) otherParameter.getSource();
        Object event = null;
        Long data = (Long) otherParameter.getData();
        retlist.add( new ListenerParameterB( source, event, data ) );
        
        // 
        return retlist;
      }
      
      @Override
      public List<ListenerReturnInfoA> adaptReturnInfo( ListenerReturnInfoB returninfo )
      {
        //
        List<ListenerReturnInfoA> retlist = new ArrayList<ListenerManagerTest.ListenerReturnInfoA>();
        
        //
        String client = returninfo.getClient();
        Double result = returninfo.getResult();
        
        retlist.add( new ListenerReturnInfoA( client, result ) );
        
        // 
        return retlist;
      }
      
    };
    listenerManagerFacade.listenTo( listenerManagerSource, listenerAdapter );
    
    //
    final int resultAddition = 1;
    final String clientAddition = "_to_client";
    
    Listener<ListenerParameterB, ListenerReturnInfoB> listener = new ListenerAbstract<ListenerParameterB, ListenerReturnInfoB>()
    {
      @Override
      public List<ListenerReturnInfoB> handleEvent( ListenerParameterB parameter )
      {
        //
        List<ListenerReturnInfoB> retlist = new ArrayList<ListenerManagerTest.ListenerReturnInfoB>();
        
        //
        String source = parameter.getSource();
        Long data = parameter.getData();
        retlist.add( new ListenerReturnInfoB( source + clientAddition, Double.valueOf( data.doubleValue() + resultAddition ) ) );
        
        // 
        return retlist;
      }
    };
    listenerManagerFacade.getListenerRegistration().addListener( listener );
    
    //
    final String source = "source";
    final Object event = null;
    final Long data = Long.valueOf( 10000 );
    
    //
    List<ListenerReturnInfoA> returnInfoList = listenerManagerSource.handleEvent( new ListenerParameterA( source, event, data ) );
    assertNotNull( returnInfoList );
    assertEquals( 1, returnInfoList.size() );
    ListenerReturnInfoA returnInfoA = returnInfoList.get( 0 );
    assertEquals( source + clientAddition, returnInfoA.getClient() );
    assertEquals( data.longValue() + resultAddition, ( (Double) returnInfoA.getResult() ).longValue() );
  }
  
}
