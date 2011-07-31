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
package org.omnaest.utils.listener.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.listener.EventListener;
import org.omnaest.utils.listener.EventListenerRegistration;
import org.omnaest.utils.listener.EventManager;
import org.omnaest.utils.listener.EventManagerConnector;
import org.omnaest.utils.listener.adapter.EventListenerAdapter;
import org.omnaest.utils.listener.event.EventResults;

/**
 * @see EventManagerConnector
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public class EventManagerConnectorImpl<EVENT, RESULT> implements EventManagerConnector<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                                           serialVersionUID                           = 6714239925791970079L;
  
  /* ********************************************** Variables ********************************************** */
  protected EventManager<EVENT, RESULT>                               eventManager                               = null;
  protected Map<EventListenerRegistration<?, ?>, EventListener<?, ?>> connectedListenerRegistrationToListenerMap = new HashMap<EventListenerRegistration<?, ?>, EventListener<?, ?>>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see EventManagerConnector
   * @param eventManager
   */
  public EventManagerConnectorImpl( EventManager<EVENT, RESULT> eventManager )
  {
    super();
    this.eventManager = eventManager;
  }
  
  @Override
  public EventManagerConnector<EVENT, RESULT> disconnectFrom( final EventManager<EVENT, RESULT> eventManager )
  {
    //
    if ( eventManager != null )
    {
      this.disconnectFrom( eventManager.getEventListenerRegistration() );
    }
    
    //
    return this;
  }
  
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public EventManagerConnector<EVENT, RESULT> disconnectFrom( final EventListenerRegistration eventListenerRegistration )
  {
    //
    if ( eventListenerRegistration != null
         && this.connectedListenerRegistrationToListenerMap.containsKey( eventListenerRegistration ) )
    {
      //      
      EventListener eventListener = this.connectedListenerRegistrationToListenerMap.get( eventListenerRegistration );
      eventListenerRegistration.removeEventListener( eventListener );
      
      //
      this.connectedListenerRegistrationToListenerMap.remove( eventListenerRegistration );
    }
    
    //
    return this;
  }
  
  @Override
  public EventManagerConnector<EVENT, RESULT> listenTo( final EventListenerRegistration<EVENT, RESULT> eventListenerRegistration )
  {
    EventListenerAdapter<EVENT, RESULT, EVENT, RESULT> listenerAdapter = new EventListenerAdapter<EVENT, RESULT, EVENT, RESULT>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public List<EVENT> adaptEvent( EVENT otherParameter )
      {
        //
        return Arrays.asList( otherParameter );
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public List<RESULT> adaptResult( RESULT returninfo )
      {
        // 
        return Arrays.asList( returninfo );
      }
    };
    return this.listenTo( eventListenerRegistration, listenerAdapter );
  }
  
  @Override
  public <OTHER_EVENT, OTHER_RESULT> EventManagerConnector<EVENT, RESULT> listenTo( final EventListenerRegistration<OTHER_EVENT, OTHER_RESULT> eventListenerRegistration,
                                                                                    final EventListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> eventListenerAdapter )
  {
    //
    if ( eventListenerRegistration != null && eventListenerAdapter != null
         && !this.connectedListenerRegistrationToListenerMap.containsKey( eventListenerRegistration ) )
    {
      //
      EventListener<OTHER_EVENT, OTHER_RESULT> listener = new EventListener<OTHER_EVENT, OTHER_RESULT>()
      {
        
        /* ********************************************** Constants ********************************************** */
        private static final long serialVersionUID = -100666254531161206L;
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public List<OTHER_RESULT> handleEvent( OTHER_EVENT otherEvent )
        {
          //
          List<OTHER_RESULT> otherResultList = new ArrayList<OTHER_RESULT>();
          
          //
          List<EVENT> eventList = eventListenerAdapter.adaptEvent( otherEvent );
          
          //
          List<RESULT> resultList = new ArrayList<RESULT>();
          if ( eventList != null )
          {
            for ( EVENT event : eventList )
            {
              //              
              EventResults<RESULT> singleResultList = EventManagerConnectorImpl.this.eventManager.fireEvent( event );
              
              //
              if ( singleResultList != null )
              {
                resultList.addAll( singleResultList.getResultList() );
              }
            }
          }
          
          //          
          for ( RESULT result : resultList )
          {
            //
            List<OTHER_RESULT> singleOtherResult = eventListenerAdapter.adaptResult( result );
            
            //
            if ( singleOtherResult != null )
            {
              otherResultList.addAll( singleOtherResult );
            }
          }
          
          // 
          return otherResultList;
        }
      };
      
      //
      eventListenerRegistration.addEventListener( listener );
      
      //
      this.connectedListenerRegistrationToListenerMap.put( eventListenerRegistration, listener );
    }
    
    //
    return this;
  }
  
  @Override
  public EventManagerConnector<EVENT, RESULT> listenTo( EventManager<EVENT, RESULT> listenerManager )
  {
    return this.listenTo( listenerManager.getEventListenerRegistration() );
  }
  
  @Override
  public <OTHER_EVENT, OTHER_RESULT> EventManagerConnector<EVENT, RESULT> listenTo( EventManager<OTHER_EVENT, OTHER_RESULT> listenerManager,
                                                                                    EventListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter )
  {
    //
    if ( listenerManager != null )
    {
      this.listenTo( listenerManager.getEventListenerRegistration(), listenerAdapter );
    }
    
    //
    return this;
  }
}
