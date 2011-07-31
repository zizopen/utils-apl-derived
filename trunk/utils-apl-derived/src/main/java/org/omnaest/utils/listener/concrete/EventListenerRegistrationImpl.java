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

import java.util.List;

import org.omnaest.utils.listener.EventListenerRegistration;
import org.omnaest.utils.listener.EventListener;

/**
 * Registration for {@link EventListener} instances. Allows to add and remove {@link EventListener} instances. Intended for client use.
 * 
 * @see EventManagerImpl
 * @param <EVENT>
 * @param <RESULT>
 */
public class EventListenerRegistrationImpl<EVENT, RESULT> implements EventListenerRegistration<EVENT, RESULT>
{
  /* ********************************************** Variables ********************************************** */
  protected List<EventListener<EVENT, RESULT>> listenerList = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param listenerList
   */
  @SuppressWarnings("unchecked")
  protected EventListenerRegistrationImpl( List<? extends EventListener<EVENT, RESULT>> listenerList )
  {
    this.listenerList = (List<EventListener<EVENT, RESULT>>) listenerList;
  }
  
  /**
   * Adds a new {@link EventListener} to the handler.
   * 
   * @param listener
   * @return this
   */
  @Override
  public EventListenerRegistration<EVENT, RESULT> addEventListener( EventListener<EVENT, RESULT> listener )
  {
    //
    if ( listener != null && !this.listenerList.contains( listener ) )
    {
      this.listenerList.add( listener );
    }
    
    //
    return this;
  }
  
  /**
   * Removes a given {@link EventListener} instance from the handler.
   * 
   * @param listener
   * @return this
   */
  @Override
  public EventListenerRegistration<EVENT, RESULT> removeEventListener( EventListener<EVENT, RESULT> listener )
  {
    //
    if ( listener != null )
    {
      this.listenerList.remove( listener );
    }
    
    //
    return this;
  }
}
