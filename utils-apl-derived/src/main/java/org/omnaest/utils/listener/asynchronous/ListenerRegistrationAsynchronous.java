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
package org.omnaest.utils.listener.asynchronous;

import java.util.concurrent.Future;

import org.omnaest.utils.listener.ListenerRegistration;
import org.omnaest.utils.listener.Listener;

/**
 * Registration for {@link Listener} instances. Allows to add and remove {@link Listener} instances. Intended for client use.
 * 
 * @see ListenerManagerAsynchronousImpl
 * @param <EVENT>
 * @param <RESULT>
 */
public class ListenerRegistrationAsynchronous<EVENT, RESULT> implements ListenerRegistration<Future<EVENT>, Future<RESULT>>
{
  /* ********************************************** Variables ********************************************** */
  protected ListenerRegistration<Future<EVENT>, Future<RESULT>> listenerRegistration = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param listenerRegistration
   */
  public ListenerRegistrationAsynchronous( ListenerRegistration<Future<EVENT>, Future<RESULT>> listenerRegistration )
  {
    super();
    this.listenerRegistration = listenerRegistration;
  }
  
  @Override
  public ListenerRegistration<Future<EVENT>, Future<RESULT>> addListener( Listener<Future<EVENT>, Future<RESULT>> listener )
  {
    return this.listenerRegistration.addListener( listener );
  }
  
  @Override
  public ListenerRegistration<Future<EVENT>, Future<RESULT>> removeListener( Listener<Future<EVENT>, Future<RESULT>> listener )
  {
    return this.listenerRegistration.removeListener( listener );
  }
  
}
