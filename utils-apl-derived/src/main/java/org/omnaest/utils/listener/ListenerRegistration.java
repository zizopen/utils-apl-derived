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

/**
 * Registration for {@link Listener} instances. Allows to add and remove {@link Listener} instances. Intended for client use.
 * 
 * @param <PARAMETER>
 * @param <RETURN_INFO>
 */
public class ListenerRegistration<PARAMETER, RETURN_INFO>
{
  /* ********************************************** Variables ********************************************** */
  protected List<Listener<PARAMETER, RETURN_INFO>> listenerList = null;
  
  /* ********************************************** Methods ********************************************** */
  protected ListenerRegistration( List<Listener<PARAMETER, RETURN_INFO>> listenerList )
  {
    this.listenerList = listenerList;
  }
  
  /**
   * Adds a new {@link Listener} to the handler.
   * 
   * @param listener
   * @return this
   */
  public ListenerRegistration<PARAMETER, RETURN_INFO> addListener( Listener<PARAMETER, RETURN_INFO> listener )
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
  public ListenerRegistration<PARAMETER, RETURN_INFO> removeListener( Listener<PARAMETER, RETURN_INFO> listener )
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
