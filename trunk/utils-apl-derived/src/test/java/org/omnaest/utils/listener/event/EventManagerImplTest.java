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
package org.omnaest.utils.listener.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.listener.EventListener;
import org.omnaest.utils.listener.EventListenerBasic;
import org.omnaest.utils.listener.EventManager;
import org.omnaest.utils.listener.adapter.EventListenerAdapter;
import org.omnaest.utils.listener.concrete.EventManagerImpl;

/**
 * @see EventManagerImpl
 * @author Omnaest
 */
public class EventManagerImplTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected class ListenerParameterA extends Event<Object, Object, Object>
  {
    public ListenerParameterA( Object source, Object event, Object data )
    {
      super( source, event, data );
    }
  }
  
  protected class ListenerParameterB extends Event<String, Object, Long>
  {
    public ListenerParameterB( String source, Object event, Long data )
    {
      super( source, event, data );
    }
  }
  
  protected class ListenerReturnInfoA extends Result<Object, Object>
  {
    private static final long serialVersionUID = -2119707514452115820L;
    
    public ListenerReturnInfoA( Object client, Object result )
    {
      super( client, result );
      
    }
  }
  
  protected class ListenerReturnInfoB extends Result<String, Double>
  {
    private static final long serialVersionUID = -2801413542572657004L;
    
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
    EventManager<ListenerParameterA, ListenerReturnInfoA> listenerManagerSource = new EventManagerImpl<ListenerParameterA, ListenerReturnInfoA>();
    EventManager<ListenerParameterB, ListenerReturnInfoB> listenerManagerFacade = new EventManagerImpl<ListenerParameterB, ListenerReturnInfoB>();
    
    //
    EventListenerAdapter<ListenerParameterA, ListenerReturnInfoA, ListenerParameterB, ListenerReturnInfoB> listenerAdapter = new EventListenerAdapter<ListenerParameterA, ListenerReturnInfoA, ListenerParameterB, ListenerReturnInfoB>()
    {
      @Override
      public List<ListenerParameterB> adaptEvent( ListenerParameterA otherParameter )
      {
        //
        List<ListenerParameterB> retlist = new ArrayList<ListenerParameterB>();
        
        //        
        String source = (String) otherParameter.getSource();
        Object event = null;
        Long data = (Long) otherParameter.getData();
        retlist.add( new ListenerParameterB( source, event, data ) );
        
        // 
        return retlist;
      }
      
      @Override
      public List<ListenerReturnInfoA> adaptResult( ListenerReturnInfoB returninfo )
      {
        //
        List<ListenerReturnInfoA> retlist = new ArrayList<ListenerReturnInfoA>();
        
        //
        String client = returninfo.getClient();
        Double result = returninfo.getResult();
        
        retlist.add( new ListenerReturnInfoA( client, result ) );
        
        // 
        return retlist;
      }
      
    };
    listenerManagerFacade.getEventManagerConnector().listenTo( listenerManagerSource, listenerAdapter );
    
    //
    final int resultAddition = 1;
    final String clientAddition = "_to_client";
    
    EventListener<ListenerParameterB, ListenerReturnInfoB> listener = new EventListenerBasic<ListenerParameterB, ListenerReturnInfoB>()
    {
      private static final long serialVersionUID = 1586783633343970943L;
      
      @Override
      public List<ListenerReturnInfoB> handleEvent( ListenerParameterB parameter )
      {
        //
        List<ListenerReturnInfoB> retlist = new ArrayList<ListenerReturnInfoB>();
        
        //
        String source = parameter.getSource();
        Long data = parameter.getData();
        retlist.add( new ListenerReturnInfoB( source + clientAddition, Double.valueOf( data.doubleValue() + resultAddition ) ) );
        
        // 
        return retlist;
      }
    };
    listenerManagerFacade.getEventListenerRegistration().addEventListener( listener );
    
    //
    final String source = "source";
    final Object event = null;
    final Long data = Long.valueOf( 10000 );
    
    //
    EventResults<ListenerReturnInfoA> returnInfoList = listenerManagerSource.fireEvent( new ListenerParameterA( source, event,
                                                                                                                data ) );
    
    assertNotNull( returnInfoList );
    assertEquals( 1, returnInfoList.size() );
    ListenerReturnInfoA returnInfoA = returnInfoList.get( 0 );
    assertEquals( source + clientAddition, returnInfoA.getClient() );
    assertEquals( data.longValue() + resultAddition, ( (Double) returnInfoA.getResult() ).longValue() );
  }
}
