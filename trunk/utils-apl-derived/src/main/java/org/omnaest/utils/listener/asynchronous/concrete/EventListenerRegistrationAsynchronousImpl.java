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
package org.omnaest.utils.listener.asynchronous.concrete;

import java.util.concurrent.Future;

import org.omnaest.utils.listener.EventListener;
import org.omnaest.utils.listener.EventListenerRegistration;
import org.omnaest.utils.listener.asynchronous.EventListenerAsynchronous;
import org.omnaest.utils.listener.asynchronous.EventListenerRegistrationAsynchronous;

/**
 * Registration for {@link EventListener} instances. Allows to add and remove {@link EventListener} instances. Intended for client use.
 * 
 * @see EventManagerAsynchronousImpl
 * @param <EVENT>
 * @param <RESULT>
 */
public class EventListenerRegistrationAsynchronousImpl<EVENT, RESULT> implements EventListenerRegistrationAsynchronous<EVENT, RESULT>
{
  /* ********************************************** Variables ********************************************** */
  protected EventListenerRegistration<Future<EVENT>, Future<RESULT>> eventListenerRegistration = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param listenerRegistration
   */
  public EventListenerRegistrationAsynchronousImpl( EventListenerRegistration<Future<EVENT>, Future<RESULT>> listenerRegistration )
  {
    super();
    this.eventListenerRegistration = listenerRegistration;
  }
  
  @Override
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> addListener( EventListenerAsynchronous<EVENT, RESULT> listener )
  {
    return this.addEventListener( (EventListener<Future<EVENT>, Future<RESULT>>) listener );
  }
  
  @Override
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> removeListener( EventListenerAsynchronous<EVENT, RESULT> listener )
  {
    return this.removeEventListener( (EventListener<Future<EVENT>, Future<RESULT>>) listener );
  }
  
  @Override
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> addEventListener( EventListener<Future<EVENT>, Future<RESULT>> listener )
  {
    //
    this.eventListenerRegistration.addEventListener( listener );
    
    //
    return this;
  }
  
  @Override
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> removeEventListener( EventListener<Future<EVENT>, Future<RESULT>> listener )
  {
    //
    this.eventListenerRegistration.removeEventListener( listener );
    
    //
    return this;
  }
  
}
