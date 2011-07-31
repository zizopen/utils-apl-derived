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

import org.omnaest.utils.listener.EventProducer;
import org.omnaest.utils.listener.EventListener;
import org.omnaest.utils.listener.EventManager;
import org.omnaest.utils.listener.EventListenerRegistration;
import org.omnaest.utils.listener.adapter.ListenerAdapter;

/**
 * Manager for {@link EventListener} instances which implements the {@link EventListener} interface, too. The
 * {@link EventListener#handleEvent(Object, EventListenerRegistrationImpl)} is executed for all of the {@link EventListener} instances which are
 * not null. <br>
 * <br>
 * To add new {@link EventListener} instances use the {@link EventListenerRegistrationImpl} instance which can be retrieved via
 * {@link #getEventListenerRegistration()}. Its best practice to make this method available to clients by a delegate method.
 * 
 * @see EventProducer
 * @param <EVENT>
 * @param <RESULT>
 * @author Omnaest
 */
public class EventManagerImpl<EVENT, RESULT> implements EventListener<EVENT, RESULT>, EventProducer<EVENT, RESULT>,
                                                EventManager<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                                 serialVersionUID                           = 185487616795626165L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<EventListener<EVENT, RESULT>>                   listenerList                               = new ArrayList<EventListener<EVENT, RESULT>>();
  protected EventListenerRegistration<EVENT, RESULT>             eventListenerRegistration                       = new EventListenerRegistrationImpl<EVENT, RESULT>(
                                                                                                                                                      this.listenerList );
  protected Map<EventListenerRegistration<?, ?>, EventListener<?, ?>> connectedListenerRegistrationToListenerMap = new HashMap<EventListenerRegistration<?, ?>, EventListener<?, ?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see EventManagerImpl
   */
  public EventManagerImpl()
  {
    super();
  }
  
  /**
   * Removes all listeners from the {@link EventManagerImpl} instance.
   * 
   * @return this
   */
  @Override
  public EventManager<EVENT, RESULT> clearListeners()
  {
    //
    this.listenerList.clear();
    
    //
    return this;
  }
  
  /**
   * Simple method for handling events.
   * 
   * @see #handleEvent(Object, EventListenerRegistrationImpl)
   * @param parameter
   * @return
   */
  @Override
  public List<RESULT> handleEvent( EVENT parameter )
  {
    return this.handleEvent( parameter, this.getEventListenerRegistration() );
  }
  
  @Override
  public List<RESULT> handleEvent( EVENT parameter, EventListenerRegistration<EVENT, RESULT> listenerRegistration )
  {
    //
    List<RESULT> retlist = new ArrayList<RESULT>();
    
    //
    for ( EventListener<EVENT, RESULT> listener : new ArrayList<EventListener<EVENT, RESULT>>( this.listenerList ) )
    {
      if ( listener != null )
      {
        //
        List<RESULT> singleReturnList = listener.handleEvent( parameter, this.getEventListenerRegistration() );
        
        //
        if ( singleReturnList != null )
        {
          retlist.addAll( singleReturnList );
        }
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public EventListenerRegistration<EVENT, RESULT> getEventListenerRegistration()
  {
    return this.eventListenerRegistration;
  }
  
  /**
   * Connects the current {@link EventManagerImpl} to the {@link EventListenerRegistrationImpl} from another
   * {@link EventManagerImpl} instance. This allows to chain {@link EventManagerImpl} instances.
   * 
   * @see #listenTo(EventManagerImpl, ListenerAdapter)
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistrationImpl)
   * @see ListenerAdapter
   * @param eventListenerRegistration
   * @return this
   */
  @Override
  public EventManager<EVENT, RESULT> disconnectFrom( final EventManager<EVENT, RESULT> eventManager )
  {
    //
    if ( eventManager != null )
    {
      this.disconnectFrom( eventManager.getEventListenerRegistration() );
    }
    
    //
    return this;
  }
  
  /**
   * @see #disconnectFrom(EventManagerImpl)
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @see ListenerAdapter
   * @param eventListenerRegistration
   * @return this
   */
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public EventManager<EVENT, RESULT> disconnectFrom( final EventListenerRegistration eventListenerRegistration )
  {
    //
    if ( eventListenerRegistration != null && this.connectedListenerRegistrationToListenerMap.containsKey( eventListenerRegistration ) )
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
  
  /**
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param eventListenerRegistration
   * @return this
   */
  @Override
  public EventManager<EVENT, RESULT> listenTo( final EventListenerRegistration<EVENT, RESULT> eventListenerRegistration )
  {
    ListenerAdapter<EVENT, RESULT, EVENT, RESULT> listenerAdapter = new ListenerAdapter<EVENT, RESULT, EVENT, RESULT>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public List<EVENT> adaptParameter( EVENT otherParameter )
      {
        //
        return Arrays.asList( otherParameter );
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public List<RESULT> adaptReturnInfo( RESULT returninfo )
      {
        // 
        return Arrays.asList( returninfo );
      }
    };
    return this.listenTo( eventListenerRegistration, listenerAdapter );
  }
  
  /**
   * Connects the current {@link EventManagerImpl} to the {@link EventListenerRegistrationImpl} from another
   * {@link EventManagerImpl} instance. This allows to chain {@link EventManagerImpl} instances.
   * 
   * @see ListenerAdapter
   * @see #listenTo(EventManagerImpl, ListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistrationImpl)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerRegistration
   * @param listenerAdapter
   * @return this
   */
  @Override
  public <OTHER_EVENT, OTHER_RESULT> EventManager<EVENT, RESULT> listenTo( final EventListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration,
                                                                              final ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter )
  {
    //
    if ( listenerRegistration != null && listenerAdapter != null
         && !this.connectedListenerRegistrationToListenerMap.containsKey( listenerRegistration ) )
    {
      //
      EventListener<OTHER_EVENT, OTHER_RESULT> listener = new EventListener<OTHER_EVENT, OTHER_RESULT>()
      {
        
        /* ********************************************** Constants ********************************************** */
        private static final long serialVersionUID = -100666254531161206L;
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public List<OTHER_RESULT> handleEvent( OTHER_EVENT otherParameter,
                                               EventListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration )
        {
          //
          List<OTHER_RESULT> otherReturnInfoList = new ArrayList<OTHER_RESULT>();
          
          //
          List<EVENT> parameterList = listenerAdapter.adaptParameter( otherParameter );
          
          //
          List<RESULT> returnInfoList = new ArrayList<RESULT>();
          if ( parameterList != null )
          {
            for ( EVENT parameter : parameterList )
            {
              //              
              List<RESULT> singleReturnInfoList = EventManagerImpl.this.handleEvent( parameter,
                                                                                        EventManagerImpl.this.getEventListenerRegistration() );
              
              //
              if ( singleReturnInfoList != null )
              {
                returnInfoList.addAll( singleReturnInfoList );
              }
            }
          }
          
          //          
          for ( RESULT returnInfo : returnInfoList )
          {
            //
            List<OTHER_RESULT> singleOtherReturnInfoList = listenerAdapter.adaptReturnInfo( returnInfo );
            
            //
            if ( singleOtherReturnInfoList != null )
            {
              otherReturnInfoList.addAll( singleOtherReturnInfoList );
            }
          }
          
          // 
          return otherReturnInfoList;
        }
      };
      
      //
      listenerRegistration.addEventListener( listener );
      
      //
      this.connectedListenerRegistrationToListenerMap.put( listenerRegistration, listener );
    }
    
    //
    return this;
  }
  
  /**
   * @see #listenTo(EventListenerRegistrationImpl)
   * @param listenerManager
   * @return
   */
  @Override
  public EventManager<EVENT, RESULT> listenTo( EventManagerImpl<EVENT, RESULT> listenerManager )
  {
    return this.listenTo( listenerManager.getEventListenerRegistration() );
  }
  
  /**
   * @see #disconnectFrom(EventManagerImpl)
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerManager
   * @param listenerAdapter
   */
  @Override
  public <OTHER_EVENT, OTHER_RESULT> EventManager<EVENT, RESULT> listenTo( EventManager<OTHER_EVENT, OTHER_RESULT> listenerManager,
                                                                              ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter )
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
