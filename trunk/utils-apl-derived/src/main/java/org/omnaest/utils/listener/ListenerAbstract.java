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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple abstract implementation of a {@link Listener} which offers several overwritable methods to handle the event.
 * 
 * @author Omnaest
 */
public abstract class ListenerAbstract<PARAMETER, RETURN_INFO> implements Listener<PARAMETER, RETURN_INFO>
{
  /* ********************************************** Variables ********************************************** */
  private final List<RETURN_INFO> EMPTY_RETURN_INFO_LIST = new ArrayList<RETURN_INFO>();
  
  /* ********************************************** Methods ********************************************** */

  public void handleEventSilently( PARAMETER parameter )
  {
  }
  
  public List<RETURN_INFO> handleEvent( PARAMETER parameter )
  {
    return EMPTY_RETURN_INFO_LIST;
  }
  
  public void handleEventSilently( PARAMETER parameter, ListenerRegistration<PARAMETER, RETURN_INFO> listenerRegistration )
  {
  }
  
  @Override
  public List<RETURN_INFO> handleEvent( PARAMETER parameter, ListenerRegistration<PARAMETER, RETURN_INFO> listenerRegistration )
  {
    //
    List<RETURN_INFO> retlist = new ArrayList<RETURN_INFO>();
    
    //
    retlist.addAll( this.handleEvent( parameter ) );
    
    //
    this.handleEventSilently( parameter, listenerRegistration );
    this.handleEventSilently( parameter );
    
    // 
    return retlist;
  }
  
}
