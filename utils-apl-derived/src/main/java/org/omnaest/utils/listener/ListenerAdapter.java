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

import org.omnaest.utils.tuple.Tuple;
import org.omnaest.utils.tuple.TupleDuad;
import org.omnaest.utils.tuple.TupleTriple;


/**
 * Adapter interface used to connect two {@link ListenerManager} instances with
 * {@link ListenerManager#listenTo(ListenerRegistration, ListenerAdapter)}. Converts the source and event from another
 * {@link Listener} to a current source and event. And the result and client coming from a current {@link Listener} back to the
 * other {@link Listener}s client and result.
 * 
 * @see Listener
 * @see ListenerManager
 * @see ListenerRegistration
 * @author Omnaest
 * @param <OTHER_PARAMETER>
 *          parameter coming from the source listener
 * @param <OTHER_RETURN>
 *          return tuple coming from the source listener
 * @param <PARAMETER>
 *          paramter returned by the adapter
 * @param <RETURNINFO>
 *          return tuple returned by the adapter
 */
public interface ListenerAdapter<OTHER_PARAMETER, OTHER_RETURN, PARAMETER, RETURNINFO>
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * Container which allows storage of <source,event,data> tuples.
   * 
   * @see ListenerAdapter
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
   * @see ListenerAdapter
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
  public List<PARAMETER> adaptParameter( OTHER_PARAMETER otherParameter );
  
  /**
   * Adapts the given result from a given client. Result is a tuple of return information, this allows to generate multiple
   * results and multiple clients.
   * 
   * @param returninfo
   * @return
   */
  public List<OTHER_RETURN> adaptReturnInfo( RETURNINFO returninfo );
  
}
