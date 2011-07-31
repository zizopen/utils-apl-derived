package org.omnaest.utils.listener;

import java.io.Serializable;

import org.omnaest.utils.listener.adapter.EventListenerAdapter;

/**
 * Offers methods to connect an {@link EventManager} to another {@link EventManager} or {@link EventListenerRegistration}.
 * Instances of this type should be used as part of an {@link EventManager}.
 * 
 * @see EventManager
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public interface EventManagerConnector<EVENT, RESULT> extends Serializable
{
  /**
   * Connects the current {@link EventManager} to the {@link EventListenerRegistration} from another {@link EventManager}
   * instance. This allows to chain {@link EventManager} instances.
   * 
   * @see #listenTo(EventManager, EventListenerAdapter)
   * @see #listenTo(EventListenerRegistration, EventListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistration)
   * @see EventListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  public EventManagerConnector<EVENT, RESULT> disconnectFrom( final EventManager<EVENT, RESULT> eventManager );
  
  /**
   * @see #disconnectFrom(EventManager)
   * @see #listenTo(EventListenerRegistration, EventListenerAdapter)
   * @see EventListenerAdapter
   * @param eventListenerRegistration
   * @return this
   */
  public EventManagerConnector<EVENT, RESULT> disconnectFrom( final EventListenerRegistration<EVENT, RESULT> eventListenerRegistration );
  
  /**
   * @see #listenTo(EventListenerRegistration, EventListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param eventListenerRegistration
   * @return this
   */
  public EventManagerConnector<EVENT, RESULT> listenTo( final EventListenerRegistration<EVENT, RESULT> eventListenerRegistration );
  
  /**
   * Connects the current {@link EventManager} to the {@link EventListenerRegistration} from another {@link EventManager}
   * instance. This allows to chain {@link EventManager} instances.
   * 
   * @see EventListenerAdapter
   * @see #listenTo(EventManager, EventListenerAdapter)
   * @see #disconnectFrom(EventListenerRegistration)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param eventListenerRegistration
   * @param listenerAdapter
   * @return this
   */
  public <OTHER_EVENT, OTHER_RESULT> EventManagerConnector<EVENT, RESULT> listenTo( final EventListenerRegistration<OTHER_EVENT, OTHER_RESULT> eventListenerRegistration,
                                                                                    final EventListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter );
  
  /**
   * @see #listenTo(EventListenerRegistration)
   * @param eventManager
   * @return this
   */
  public EventManagerConnector<EVENT, RESULT> listenTo( EventManager<EVENT, RESULT> eventManager );
  
  /**
   * @see #disconnectFrom(EventManager)
   * @see #listenTo(EventListenerRegistration, EventListenerAdapter)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param eventManager
   * @param eventListenerAdapter
   */
  public <OTHER_EVENT, OTHER_RESULT> EventManagerConnector<EVENT, RESULT> listenTo( EventManager<OTHER_EVENT, OTHER_RESULT> eventManager,
                                                                                    EventListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> eventListenerAdapter );
}
