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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for {@link Listener} instances which implements the {@link Listener} interface, too. The
 * {@link Listener#handleEvent(Object, ListenerRegistration)} is executed for all of the {@link Listener} instances which are not
 * null. <br>
 * <br>
 * To add new {@link Listener} instances use the {@link ListenerRegistration} instance which can be retrieved via
 * {@link #getListenerRegistration()}. Its best practice to make this method available to clients.
 * 
 * @param <PARAMETER>
 * @param <RETURN_INFO>
 * @author Omnaest
 */
public class ListenerManager<PARAMETER, RETURN_INFO> implements Listener<PARAMETER, RETURN_INFO>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                                         serialVersionUID                           = 185487616795626165L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<Listener<PARAMETER, RETURN_INFO>>                  listenerList                               = new ArrayList<Listener<PARAMETER, RETURN_INFO>>();
  protected ListenerRegistrationControl<PARAMETER, RETURN_INFO> listenerRegistration                       = null;
  protected Map<ListenerRegistration<?, ?>, Listener<?, ?>>         connectedListenerRegistrationToListenerMap = new HashMap<ListenerRegistration<?, ?>, Listener<?, ?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * @see ListenerManager
   */
  public ListenerManager()
  {
    this.listenerRegistration = new ListenerRegistrationControl<PARAMETER, RETURN_INFO>( this.listenerList );
  }
  
  /**
   * Simple method for handling events.
   * 
   * @see #handleEvent(Object, ListenerRegistration)
   * @param parameter
   * @return
   */
  public List<RETURN_INFO> handleEvent( PARAMETER parameter )
  {
    return this.handleEvent( parameter, this.getListenerRegistration() );
  }
  
  @Override
  public List<RETURN_INFO> handleEvent( PARAMETER parameter, ListenerRegistration<PARAMETER, RETURN_INFO> listenerRegistration )
  {
    //
    List<RETURN_INFO> retlist = new ArrayList<RETURN_INFO>();
    
    //
    for ( Listener<PARAMETER, RETURN_INFO> listener : new ArrayList<Listener<PARAMETER, RETURN_INFO>>( this.listenerList ) )
    {
      if ( listener != null )
      {
        //
        List<RETURN_INFO> singleReturnList = listener.handleEvent( parameter, this.getListenerRegistration() );
        
        //
        if ( singleReturnList != null )
        {
          retlist.addAll( singleReturnList );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * @see ListenerRegistration
   * @return
   */
  public ListenerRegistration<PARAMETER, RETURN_INFO> getListenerRegistration()
  {
    return this.listenerRegistration;
  }
  
  public ListenerRegistrationControl<PARAMETER, RETURN_INFO> getListenerRegistrationFullControl()
  {
    return this.listenerRegistration;
  }
  
  /**
   * Connects the current {@link ListenerManager} to the {@link ListenerRegistration} from another {@link ListenerManager}
   * instance. This allows to chain {@link ListenerManager} instances.
   * 
   * @see #listenTo(ListenerManager, ListenerAdapter)
   * @see #listenTo(ListenerRegistration, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistration)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  @SuppressWarnings("rawtypes")
  public ListenerManager<PARAMETER, RETURN_INFO> disconnectFrom( final ListenerManager listenerManager )
  {
    //
    if ( listenerManager != null )
    {
      this.disconnectFrom( listenerManager.getListenerRegistration() );
    }
    
    //
    return this;
  }
  
  /**
   * @see #disconnectFrom(ListenerManager)
   * @see #listenTo(ListenerRegistration, ListenerAdapter)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ListenerManager<PARAMETER, RETURN_INFO> disconnectFrom( final ListenerRegistration listenerRegistration )
  {
    //
    if ( listenerRegistration != null && this.connectedListenerRegistrationToListenerMap.containsKey( listenerRegistration ) )
    {
      //      
      Listener listener = this.connectedListenerRegistrationToListenerMap.get( listenerRegistration );
      listenerRegistration.removeListener( listener );
      
      //
      this.connectedListenerRegistrationToListenerMap.remove( listenerRegistration );
    }
    
    //
    return this;
  }
  
  /**
   * @see #listenTo(ListenerRegistration, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerRegistration
   * @return this
   */
  public ListenerManager<PARAMETER, RETURN_INFO> listenTo( final ListenerRegistration<PARAMETER, RETURN_INFO> listenerRegistration )
  {
    ListenerAdapter<PARAMETER, RETURN_INFO, PARAMETER, RETURN_INFO> listenerAdapter = new ListenerAdapter<PARAMETER, RETURN_INFO, PARAMETER, RETURN_INFO>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public List<PARAMETER> adaptParameter( PARAMETER otherParameter )
      {
        //
        return Arrays.asList( otherParameter );
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public List<RETURN_INFO> adaptReturnInfo( RETURN_INFO returninfo )
      {
        // 
        return Arrays.asList( returninfo );
      }
    };
    return this.listenTo( listenerRegistration, listenerAdapter );
  }
  
  /**
   * Connects the current {@link ListenerManager} to the {@link ListenerRegistration} from another {@link ListenerManager}
   * instance. This allows to chain {@link ListenerManager} instances.
   * 
   * @see ListenerAdapter
   * @see #listenTo(ListenerManager, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistration)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerRegistration
   * @param listenerAdapter
   * @return this
   */
  public <OTHER_PARAMETER, OTHER_RETURN_INFO> ListenerManager<PARAMETER, RETURN_INFO> listenTo( final ListenerRegistration<OTHER_PARAMETER, OTHER_RETURN_INFO> listenerRegistration,
                                                                                                final ListenerAdapter<OTHER_PARAMETER, OTHER_RETURN_INFO, PARAMETER, RETURN_INFO> listenerAdapter )
  {
    //
    if ( listenerRegistration != null && listenerAdapter != null
         && !this.connectedListenerRegistrationToListenerMap.containsKey( listenerRegistration ) )
    {
      //
      Listener<OTHER_PARAMETER, OTHER_RETURN_INFO> listener = new Listener<OTHER_PARAMETER, OTHER_RETURN_INFO>()
      {
        @SuppressWarnings("rawtypes")
        @Override
        public List<OTHER_RETURN_INFO> handleEvent( OTHER_PARAMETER otherParameter, ListenerRegistration listenerRegistration )
        {
          //
          List<OTHER_RETURN_INFO> otherReturnInfoList = new ArrayList<OTHER_RETURN_INFO>();
          
          //
          List<PARAMETER> parameterList = listenerAdapter.adaptParameter( otherParameter );
          
          //
          List<RETURN_INFO> returnInfoList = new ArrayList<RETURN_INFO>();
          if ( parameterList != null )
          {
            for ( PARAMETER parameter : parameterList )
            {
              //              
              List<RETURN_INFO> singleReturnInfoList = ListenerManager.this.handleEvent( parameter,
                                                                                         ListenerManager.this.getListenerRegistration() );
              
              //
              if ( singleReturnInfoList != null )
              {
                returnInfoList.addAll( singleReturnInfoList );
              }
            }
          }
          
          //
          if ( returnInfoList != null )
          {
            for ( RETURN_INFO returnInfo : returnInfoList )
            {
              //
              List<OTHER_RETURN_INFO> singleOtherReturnInfoList = listenerAdapter.adaptReturnInfo( returnInfo );
              
              //
              if ( singleOtherReturnInfoList != null )
              {
                otherReturnInfoList.addAll( singleOtherReturnInfoList );
              }
            }
          }
          
          // 
          return otherReturnInfoList;
        }
      };
      
      //
      listenerRegistration.addListener( listener );
      
      //
      this.connectedListenerRegistrationToListenerMap.put( listenerRegistration, listener );
    }
    
    //
    return this;
  }
  
  /**
   * @see #listenTo(ListenerRegistration)
   * @param listenerManager
   * @return
   */
  public ListenerManager<PARAMETER, RETURN_INFO> listenTo( ListenerManager<PARAMETER, RETURN_INFO> listenerManager )
  {
    return this.listenTo( listenerManager.getListenerRegistration() );
  }
  
  /**
   * @see #disconnectFrom(ListenerManager)
   * @see #listenTo(ListenerRegistration, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerManager
   * @param listenerAdapter
   */
  public <OTHER_PARAMETER, OTHER_RETURN_INFO> ListenerManager<PARAMETER, RETURN_INFO> listenTo( ListenerManager<OTHER_PARAMETER, OTHER_RETURN_INFO> listenerManager,
                                                                                                ListenerAdapter<OTHER_PARAMETER, OTHER_RETURN_INFO, PARAMETER, RETURN_INFO> listenerAdapter )
  {
    //
    if ( listenerManager != null )
    {
      this.listenTo( listenerManager.getListenerRegistration(), listenerAdapter );
    }
    
    //
    return this;
  }
}
