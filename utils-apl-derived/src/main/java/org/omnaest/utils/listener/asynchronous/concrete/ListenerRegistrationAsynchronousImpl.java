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

import org.omnaest.utils.listener.Listener;
import org.omnaest.utils.listener.ListenerRegistration;
import org.omnaest.utils.listener.asynchronous.ListenerAsynchronous;
import org.omnaest.utils.listener.asynchronous.ListenerRegistrationAsynchronous;

/**
 * Registration for {@link Listener} instances. Allows to add and remove {@link Listener} instances. Intended for client use.
 * 
 * @see ListenerManagerAsynchronousImpl
 * @param <EVENT>
 * @param <RESULT>
 */
public class ListenerRegistrationAsynchronousImpl<EVENT, RESULT> implements ListenerRegistrationAsynchronous<EVENT, RESULT>
{
  /* ********************************************** Variables ********************************************** */
  protected ListenerRegistration<Future<EVENT>, Future<RESULT>> listenerRegistration = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param listenerRegistration
   */
  public ListenerRegistrationAsynchronousImpl( ListenerRegistration<Future<EVENT>, Future<RESULT>> listenerRegistration )
  {
    super();
    this.listenerRegistration = listenerRegistration;
  }
  
  @Override
  public ListenerRegistrationAsynchronous<EVENT, RESULT> addListener( ListenerAsynchronous<EVENT, RESULT> listener )
  {
    return this.addListener( (Listener<Future<EVENT>, Future<RESULT>>) listener );
  }
  
  @Override
  public ListenerRegistrationAsynchronous<EVENT, RESULT> removeListener( ListenerAsynchronous<EVENT, RESULT> listener )
  {
    return this.removeListener( (Listener<Future<EVENT>, Future<RESULT>>) listener );
  }
  
  @Override
  public ListenerRegistrationAsynchronous<EVENT, RESULT> addListener( Listener<Future<EVENT>, Future<RESULT>> listener )
  {
    //
    this.listenerRegistration.addListener( listener );
    
    //
    return this;
  }
  
  @Override
  public ListenerRegistrationAsynchronous<EVENT, RESULT> removeListener( Listener<Future<EVENT>, Future<RESULT>> listener )
  {
    //
    this.listenerRegistration.removeListener( listener );
    
    //
    return this;
  }
  
}
