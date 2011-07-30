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
package org.omnaest.utils.listener.synchronous;

import java.util.List;

import org.omnaest.utils.listener.ListenerRegistration;
import org.omnaest.utils.listener.Listener;

/**
 * Registration for {@link Listener} instances. Allows to add and remove {@link Listener} instances. Intended for client use.
 * 
 * @see ListenerManager
 * @param <EVENT>
 * @param <RESULT>
 */
public class ListenerRegistrationImpl<EVENT, RESULT> implements ListenerRegistration<EVENT, RESULT>
{
  /* ********************************************** Variables ********************************************** */
  protected List<Listener<EVENT, RESULT>> listenerList = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param listenerList
   */
  @SuppressWarnings("unchecked")
  protected ListenerRegistrationImpl( List<? extends Listener<EVENT, RESULT>> listenerList )
  {
    this.listenerList = (List<Listener<EVENT, RESULT>>) listenerList;
  }
  
  /**
   * Adds a new {@link Listener} to the handler.
   * 
   * @param listener
   * @return this
   */
  @Override
  public ListenerRegistration<EVENT, RESULT> addListener( Listener<EVENT, RESULT> listener )
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
   * Removes a given {@link Listener} instance from the handler.
   * 
   * @param listener
   * @return this
   */
  @Override
  public ListenerRegistration<EVENT, RESULT> removeListener( Listener<EVENT, RESULT> listener )
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
