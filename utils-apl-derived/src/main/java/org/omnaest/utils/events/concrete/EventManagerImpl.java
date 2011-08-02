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
package org.omnaest.utils.events.concrete;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.events.EventListener;
import org.omnaest.utils.events.EventListenerRegistration;
import org.omnaest.utils.events.EventManager;
import org.omnaest.utils.events.EventManagerConnector;
import org.omnaest.utils.events.EventProducer;
import org.omnaest.utils.events.event.EventResults;

/**
 * Manager for {@link EventListener} instances which implements the {@link EventListener} interface, too. The
 * {@link EventListener#handleEvent(Object)} is executed for all of the {@link EventListener} instances which are not null. <br>
 * <br>
 * To add new {@link EventListener} instances use the {@link EventListenerRegistrationImpl} instance which can be retrieved via
 * {@link #getEventListenerRegistration()}. Its best practice to make this method available to clients by a delegate method.
 * 
 * @see EventProducer
 * @param <EVENT>
 * @param <RESULT>
 * @author Omnaest
 */
public class EventManagerImpl<EVENT, RESULT> implements EventProducer<EVENT, RESULT>, EventManager<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                          serialVersionUID          = 185487616795626165L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<EventListener<EVENT, RESULT>>       listenerList              = new ArrayList<EventListener<EVENT, RESULT>>();
  protected EventListenerRegistration<EVENT, RESULT> eventListenerRegistration = new EventListenerRegistrationImpl<EVENT, RESULT>(
                                                                                                                                   this.listenerList );
  
  protected EventManagerConnector<EVENT, RESULT>     eventManagerConnector     = new EventManagerConnectorImpl<EVENT, RESULT>(
                                                                                                                               this );
  
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
   * @see #fireEvent(Object, EventListenerRegistrationImpl)
   * @param parameter
   * @return
   */
  @Override
  public EventResults<RESULT> fireEvent( EVENT parameter )
  {
    //
    List<RESULT> retlist = new ArrayList<RESULT>();
    
    //
    for ( EventListener<EVENT, RESULT> listener : new ArrayList<EventListener<EVENT, RESULT>>( this.listenerList ) )
    {
      if ( listener != null )
      {
        //
        List<RESULT> singleReturnList = listener.handleEvent( parameter );
        
        //
        if ( singleReturnList != null )
        {
          retlist.addAll( singleReturnList );
        }
      }
    }
    
    //
    return new EventResults<RESULT>( retlist );
  }
  
  @Override
  public EventListenerRegistration<EVENT, RESULT> getEventListenerRegistration()
  {
    return this.eventListenerRegistration;
  }
  
  @Override
  public EventManagerConnector<EVENT, RESULT> getEventManagerConnector()
  {
    return this.eventManagerConnector;
  }
  
}
