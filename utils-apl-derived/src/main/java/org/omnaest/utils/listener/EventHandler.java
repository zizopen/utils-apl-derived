package org.omnaest.utils.listener;

import java.util.List;

/**
 * Declares the consumer of events. <br>
 * Subclasses will get references to {@link EventProducer} instances and will register their {@link EventListener} to these. This gives
 * the control to the {@link EventHandler} at holding the references to the {@link EventProducer}, too. The {@link EventHandler}
 * can so decide to unregister at any given time.<br>
 * Since the {@link EventHandler} is often used to hold controlling business logic this kind of component referencing will better
 * reflect the logic requirements. <br>
 * 
 * @author Omnaest
 */
public interface EventHandler<EVENT, RESULT>
{
  
  /**
   * Sets a ordered {@link List} of {@link EventProducer} references to the {@link EventHandler}.
   * 
   * @see #registerAtEventProducers()
   * @see #unregisterAtEventProducers()
   * @param eventProducers
   * @return
   */
  @SuppressWarnings("rawtypes")
  public EventHandler<EVENT, RESULT> setEventProducers( Iterable<? extends EventProducer> eventProducers );
  
  /**
   * Registers itself via {@link EventListener} instances at the internally hold references to {@link EventProducer} instances
   * 
   * @see #unregisterAtEventProducers()
   * @return this
   */
  public EventHandler<EVENT, RESULT> registerAtEventProducers();
  
  /**
   * Unregisters all its {@link EventListener} from the {@link EventProducer} instances
   * 
   * @see #registerAtEventProducers()
   * @return
   */
  public EventHandler<EVENT, RESULT> unregisterAtEventProducers();
  
}
