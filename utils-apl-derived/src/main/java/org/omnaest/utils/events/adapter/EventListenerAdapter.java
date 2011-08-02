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
package org.omnaest.utils.events.adapter;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.events.EventListener;
import org.omnaest.utils.events.EventListenerRegistration;
import org.omnaest.utils.events.EventManager;
import org.omnaest.utils.events.concrete.EventManagerImpl;
import org.omnaest.utils.tuple.Tuple;
import org.omnaest.utils.tuple.TupleDuad;
import org.omnaest.utils.tuple.TupleTriple;

/**
 * Adapter interface used to connect two {@link EventManager} instances with
 * {@link EventManagerImpl#listenTo(EventListenerRegistration, EventListenerAdapter)}. Converts the source and event from another
 * {@link EventListener} to a current source and event. And the result and client coming from a current {@link EventListener} back
 * to the other {@link EventListener}s client and result.
 * 
 * @see EventListener
 * @see EventManager
 * @see EventListenerRegistration
 * @author Omnaest
 * @param <OTHER_EVENT>
 *          parameter coming from the source listener
 * @param <OTHER_RESULT>
 *          return tuple coming from the source listener
 * @param <EVENT>
 *          paramter returned by the adapter
 * @param <RESULT>
 *          return tuple returned by the adapter
 */
public interface EventListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT>
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Container which allows storage of <source,event,data> tuples.
   * 
   * @see EventListenerAdapter
   */
  public static class SourceEventDataContainer<SOURCE, EVENT, DATA>
  {
    /* ********************************************** Variables ********************************************** */
    protected List<TupleTriple<SOURCE, EVENT, DATA>> sourceEventDataList = new ArrayList<TupleTriple<SOURCE, EVENT, DATA>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Adds a new {@link Tuple} to the {@link SourceEventDataContainer}.
     * 
     * @param source
     * @param event
     * @param data
     */
    public void addSourceEventData( SOURCE source, EVENT event, DATA data )
    {
      this.sourceEventDataList.add( new TupleTriple<SOURCE, EVENT, DATA>( source, event, data ) );
    }
    
    public List<TupleTriple<SOURCE, EVENT, DATA>> getSourceEventDataList()
    {
      return this.sourceEventDataList;
    }
    
  }
  
  /**
   * @see EventListenerAdapter
   * @author Omnaest
   * @param <CLIENT>
   * @param <RESULT>
   */
  public static class ClientResultContainer<CLIENT, RESULT>
  {
    /* ********************************************** Variables ********************************************** */
    protected List<TupleDuad<CLIENT, RESULT>> clientResultList = new ArrayList<TupleDuad<CLIENT, RESULT>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Adds a new {@link Tuple} to the {@link ClientResultContainer}.
     * 
     * @param client
     * @param result
     */
    public void addClientResult( CLIENT client, RESULT result )
    {
      this.clientResultList.add( new TupleDuad<CLIENT, RESULT>( client, result ) );
    }
    
    public List<TupleDuad<CLIENT, RESULT>> getClientResultList()
    {
      return this.clientResultList;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Adapts a given source and its event listener. Result is a parameter tuple, this allows to generate multiple results and
   * multiple clients.
   * 
   * @param otherParameter
   * @return
   */
  public List<EVENT> adaptEvent( OTHER_EVENT otherParameter );
  
  /**
   * Adapts the given result from a given client. Result is a tuple of return information, this allows to generate multiple
   * results and multiple clients.
   * 
   * @param returninfo
   * @return
   */
  public List<OTHER_RESULT> adaptResult( RESULT returninfo );
  
}
