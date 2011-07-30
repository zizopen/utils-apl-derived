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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.listener.ListenerRegistration;
import org.omnaest.utils.listener.Listenable;
import org.omnaest.utils.listener.Listener;
import org.omnaest.utils.listener.ListenerAdapter;

/**
 * Manager for {@link Listener} instances which implements the {@link Listener} interface, too. The
 * {@link Listener#handleEvent(Object, ListenerRegistrationImpl)} is executed for all of the {@link Listener} instances which are not
 * null. <br>
 * <br>
 * To add new {@link Listener} instances use the {@link ListenerRegistrationImpl} instance which can be retrieved via
 * {@link #getListenerRegistration()}. Its best practice to make this method available to clients by a delegate method.
 * 
 * @see Listenable
 * @param <EVENT>
 * @param <RESULT>
 * @author Omnaest
 */
public class ListenerManager<EVENT, RESULT> implements Listener<EVENT, RESULT>, Listenable<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                                  serialVersionUID                           = 185487616795626165L;
  
  /* ********************************************** Variables ********************************************** */
  protected List<Listener<EVENT, RESULT>>                    listenerList                               = new ArrayList<Listener<EVENT, RESULT>>();
  protected ListenerRegistration<EVENT, RESULT>             listenerRegistration                       = new ListenerRegistrationImpl<EVENT, RESULT>(
                                                                                                                                                   this.listenerList );
  protected Map<ListenerRegistration<?, ?>, Listener<?, ?>> connectedListenerRegistrationToListenerMap = new HashMap<ListenerRegistration<?, ?>, Listener<?, ?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see ListenerManager
   */
  public ListenerManager()
  {
    super();
  }
  
  /**
   * Removes all listeners from the {@link ListenerManager} instance.
   * 
   * @return this
   */
  public ListenerManager<EVENT, RESULT> clearListeners()
  {
    //
    this.listenerList.clear();
    
    //
    return this;
  }
  
  /**
   * Simple method for handling events.
   * 
   * @see #handleEvent(Object, ListenerRegistrationImpl)
   * @param parameter
   * @return
   */
  public List<RESULT> handleEvent( EVENT parameter )
  {
    return this.handleEvent( parameter, this.getListenerRegistration() );
  }
  
  @Override
  public List<RESULT> handleEvent( EVENT parameter, ListenerRegistration<EVENT, RESULT> listenerRegistration )
  {
    //
    List<RESULT> retlist = new ArrayList<RESULT>();
    
    //
    for ( Listener<EVENT, RESULT> listener : new ArrayList<Listener<EVENT, RESULT>>( this.listenerList ) )
    {
      if ( listener != null )
      {
        //
        List<RESULT> singleReturnList = listener.handleEvent( parameter, this.getListenerRegistration() );
        
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
  
  @Override
  public ListenerRegistration<EVENT, RESULT> getListenerRegistration()
  {
    return this.listenerRegistration;
  }
  
  /**
   * Connects the current {@link ListenerManager} to the {@link ListenerRegistrationImpl} from another {@link ListenerManager}
   * instance. This allows to chain {@link ListenerManager} instances.
   * 
   * @see #listenTo(ListenerManager, ListenerAdapter)
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistrationImpl)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  @SuppressWarnings("rawtypes")
  public ListenerManager<EVENT, RESULT> disconnectFrom( final ListenerManager listenerManager )
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
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ListenerManager<EVENT, RESULT> disconnectFrom( final ListenerRegistration listenerRegistration )
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
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerRegistration
   * @return this
   */
  public ListenerManager<EVENT, RESULT> listenTo( final ListenerRegistration<EVENT, RESULT> listenerRegistration )
  {
    ListenerAdapter<EVENT, RESULT, EVENT, RESULT> listenerAdapter = new ListenerAdapter<EVENT, RESULT, EVENT, RESULT>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public List<EVENT> adaptParameter( EVENT otherParameter )
      {
        //
        return Arrays.asList( otherParameter );
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public List<RESULT> adaptReturnInfo( RESULT returninfo )
      {
        // 
        return Arrays.asList( returninfo );
      }
    };
    return this.listenTo( listenerRegistration, listenerAdapter );
  }
  
  /**
   * Connects the current {@link ListenerManager} to the {@link ListenerRegistrationImpl} from another {@link ListenerManager}
   * instance. This allows to chain {@link ListenerManager} instances.
   * 
   * @see ListenerAdapter
   * @see #listenTo(ListenerManager, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistrationImpl)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerRegistration
   * @param listenerAdapter
   * @return this
   */
  public <OTHER_EVENT, OTHER_RESULT> ListenerManager<EVENT, RESULT> listenTo( final ListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration,
                                                                              final ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter )
  {
    //
    if ( listenerRegistration != null && listenerAdapter != null
         && !this.connectedListenerRegistrationToListenerMap.containsKey( listenerRegistration ) )
    {
      //
      Listener<OTHER_EVENT, OTHER_RESULT> listener = new Listener<OTHER_EVENT, OTHER_RESULT>()
      {
        
        /* ********************************************** Constants ********************************************** */
        private static final long serialVersionUID = -100666254531161206L;
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public List<OTHER_RESULT> handleEvent( OTHER_EVENT otherParameter,
                                               ListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration )
        {
          //
          List<OTHER_RESULT> otherReturnInfoList = new ArrayList<OTHER_RESULT>();
          
          //
          List<EVENT> parameterList = listenerAdapter.adaptParameter( otherParameter );
          
          //
          List<RESULT> returnInfoList = new ArrayList<RESULT>();
          if ( parameterList != null )
          {
            for ( EVENT parameter : parameterList )
            {
              //              
              List<RESULT> singleReturnInfoList = ListenerManager.this.handleEvent( parameter,
                                                                                    ListenerManager.this.getListenerRegistration() );
              
              //
              if ( singleReturnInfoList != null )
              {
                returnInfoList.addAll( singleReturnInfoList );
              }
            }
          }
          
          //          
          for ( RESULT returnInfo : returnInfoList )
          {
            //
            List<OTHER_RESULT> singleOtherReturnInfoList = listenerAdapter.adaptReturnInfo( returnInfo );
            
            //
            if ( singleOtherReturnInfoList != null )
            {
              otherReturnInfoList.addAll( singleOtherReturnInfoList );
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
   * @see #listenTo(ListenerRegistrationImpl)
   * @param listenerManager
   * @return
   */
  public ListenerManager<EVENT, RESULT> listenTo( ListenerManager<EVENT, RESULT> listenerManager )
  {
    return this.listenTo( listenerManager.getListenerRegistration() );
  }
  
  /**
   * @see #disconnectFrom(ListenerManager)
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerManager
   * @param listenerAdapter
   */
  public <OTHER_EVENT, OTHER_RESULT> ListenerManager<EVENT, RESULT> listenTo( ListenerManager<OTHER_EVENT, OTHER_RESULT> listenerManager,
                                                                              ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter )
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
