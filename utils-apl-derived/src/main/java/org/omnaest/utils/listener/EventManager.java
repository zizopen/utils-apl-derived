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

import java.util.List;

import org.omnaest.utils.listener.adapter.ListenerAdapter;
import org.omnaest.utils.listener.concrete.EventManagerImpl;
import org.omnaest.utils.listener.concrete.EventListenerRegistrationImpl;

/**
 * 
 * @author Omnaest
 *
 * @param <EVENT>
 * @param <RESULT>
 */
public interface EventManager<EVENT, RESULT> extends EventProducer<EVENT, RESULT>
{
  
  /**
   * Removes all listeners from the {@link EventManager} instance.
   * 
   * @return this
   */
  public EventManager<EVENT, RESULT> clearListeners();
  
  /**
   * Simple method for handling events.
   * 
   * @see #handleEvent(Object, EventListenerRegistration)
   * @param parameter
   * @return
   */
  public List<RESULT> handleEvent( EVENT parameter );
  
  public List<RESULT> handleEvent( EVENT parameter, EventListenerRegistration<EVENT, RESULT> listenerRegistration );
  
  /**
   * Connects the current {@link EventManager} to the {@link EventListenerRegistration} from another {@link EventManager}
   * instance. This allows to chain {@link EventManager} instances.
   * 
   * @see #listenTo(EventManagerImpl, ListenerAdapter)
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistration)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  public EventManager<EVENT, RESULT> disconnectFrom( final EventManager<EVENT, RESULT> listenerManager );
  
  /**
   * @see #disconnectFrom(EventManagerImpl)
   * @see #listenTo(EventListenerRegistrationImpl, ListenerAdapter)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  public EventManager<EVENT, RESULT> disconnectFrom( final EventListenerRegistration<EVENT, RESULT> listenerRegistration );
  
  /**
   * @see #listenTo(EventListenerRegistration, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerRegistration
   * @return this
   */
  public EventManager<EVENT, RESULT> listenTo( final EventListenerRegistration<EVENT, RESULT> listenerRegistration );
  
  /**
   * Connects the current {@link EventManager} to the {@link EventListenerRegistration} from another {@link EventManager}
   * instance. This allows to chain {@link EventManager} instances.
   * 
   * @see ListenerAdapter
   * @see #listenTo(EventManager, ListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistration)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerRegistration
   * @param listenerAdapter
   * @return this
   */
  public <OTHER_EVENT, OTHER_RESULT> EventManager<EVENT, RESULT> listenTo( final EventListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration,
                                                                              final ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter );
  
  /**
   * @see #listenTo(EventListenerRegistration)
   * @param listenerManager
   * @return
   */
  public EventManager<EVENT, RESULT> listenTo( EventManagerImpl<EVENT, RESULT> listenerManager );
  
  /**
   * @see #disconnectFrom(EventManager)
   * @see #listenTo(EventListenerRegistration, ListenerAdapter)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerManager
   * @param listenerAdapter
   */
  public <OTHER_EVENT, OTHER_RESULT> EventManager<EVENT, RESULT> listenTo( EventManager<OTHER_EVENT, OTHER_RESULT> listenerManager,
                                                                              ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter );
  
}
