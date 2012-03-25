/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.MapUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.EventListener;
import org.omnaest.utils.events.EventManager;
import org.omnaest.utils.events.concrete.EventManagerImpl;
import org.omnaest.utils.events.exception.ExceptionHandlerManager;
import org.omnaest.utils.events.exception.ExceptionHandlerManager.ExceptionHandlerRegistration;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.store.ElementStore.AccessEventData.AccessEvent;
import org.omnaest.utils.store.ElementStore.PersistenceExecutionControl.PersistenceOperation;
import org.omnaest.utils.structure.collection.CollectionAbstract;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.map.decorator.SortedMapDecorator;

/**
 * An {@link ElementStore} is a simple {@link Collection} for any elements which ensures simple persistence to any provided
 * {@link PersistenceAccessor}.<br>
 * <br>
 * The {@link Collection} can only contain one instance out of two equal ones. To detect equality it uses the
 * {@link Object#equals(Object)} method. This behavior is similar to a {@link HashSet}.
 * 
 * @see Collection
 * @see PersistenceAccessor
 * @see PersistenceExecutionControl
 * @author Omnaest
 * @param <E>
 */
public class ElementStore<E> extends CollectionAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                        serialVersionUID            = -4214411010069052346L;
  
  /* ********************************************** Variables ********************************************** */
  protected Map<E, Long>                           elementToIdentifierMap      = newElementToIdentifierMap();
  
  /* ********************************************** Beans / Services / References ********************************************** */
  protected final IdentifierFactory                identifierFactory           = new IdentifierFactory();
  protected final PersistenceAccessor<E>           persistenceAccessor;
  protected EventManager<AccessEventData<E>, Void> eventManager                = new EventManagerImpl<AccessEventData<E>, Void>();
  protected PersistenceExecutionControl<E>         persistenceExecutionControl = new PersistenceExecutionControlImmediateExecution<E>(); ;
  protected ExceptionHandlerManager                exceptionHandlerManager     = new ExceptionHandlerManager();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * The {@link PersistenceExecutionControl} controls when a persistence {@link OperationIntrinsic} is executed in time.
   * 
   * @see ElementStore
   * @see PersistenceOperation
   * @author Omnaest
   * @param <E>
   */
  @SuppressWarnings("javadoc")
  public static interface PersistenceExecutionControl<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * {@link OperationIntrinsic} specially for persistence operations
     * 
     * @author Omnaest
     */
    public static interface PersistenceOperation extends OperationIntrinsic
    {
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param persistenceOperation
     */
    public void execute( PersistenceOperation persistenceOperation );
  }
  
  /**
   * Default implementation for a {@link PersistenceExecutionControl} which immediately executes the given
   * {@link PersistenceOperation}s
   * 
   * @see PersistenceExecutionControl
   * @see PersistenceExecutionControlUsingExecutorService
   * @see ElementStore
   * @author Omnaest
   * @param <E>
   */
  @SuppressWarnings("javadoc")
  public static class PersistenceExecutionControlImmediateExecution<E> implements PersistenceExecutionControl<E>
  {
    @Override
    public void execute( PersistenceOperation persistenceOperation )
    {
      if ( persistenceOperation != null )
      {
        persistenceOperation.execute();
      }
    }
  }
  
  /**
   * This {@link PersistenceExecutionControl} uses a given {@link ExecutorService} to execute any {@link PersistenceOperation}
   * 
   * @see PersistenceExecutionControl
   * @see PersistenceExecutionControlImmediateExecution
   * @author Omnaest
   * @param <E>
   */
  @SuppressWarnings("javadoc")
  public static class PersistenceExecutionControlUsingExecutorService<E> implements PersistenceExecutionControl<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final int        NUMBER_OF_THREADS = 5;
    /* ********************************************** Beans / Services / References ********************************************** */
    protected final ExecutorService executorService;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see PersistenceExecutionControlUsingExecutorService
     * @param executorService
     */
    public PersistenceExecutionControlUsingExecutorService( ExecutorService executorService )
    {
      //
      super();
      this.executorService = executorService;
      
      //
      Assert.isNotNull( executorService, "ExecutorService must not be null" );
    }
    
    /**
     * Uses {@link Executors#newFixedThreadPool(int)} with {@value #NUMBER_OF_THREADS} threads as default {@link ExecutorService}
     * 
     * @see PersistenceExecutionControlUsingExecutorService
     */
    public PersistenceExecutionControlUsingExecutorService()
    {
      super();
      this.executorService = Executors.newFixedThreadPool( PersistenceExecutionControlUsingExecutorService.NUMBER_OF_THREADS );
    }
    
    @Override
    public void execute( final PersistenceOperation persistenceOperation )
    {
      if ( persistenceOperation != null )
      {
        this.executorService.submit( new Runnable()
        {
          @Override
          public void run()
          {
            persistenceOperation.execute();
          }
        } );
      }
    }
    
  }
  
  /**
   * @param element
   */
  private void fireEventForRemovedElement( final E element )
  {
    //
    if ( this.persistenceAccessor != null && this.persistenceExecutionControl != null )
    {
      //
      final PersistenceOperation persistenceOperation = new PersistenceOperation()
      {
        @Override
        public void execute()
        {
          //
          try
          {
            //
            final Long identifier = ElementStore.this.elementToIdentifierMap.get( element );
            if ( identifier != null )
            {
              ElementStore.this.persistenceAccessor.remove( identifier );
              ElementStore.this.identifierFactory.release( identifier );
            }
          }
          catch ( Exception e )
          {
            ElementStore.this.exceptionHandlerManager.getExceptionHandler().handleExcpetion( e );
          }
        }
      };
      this.persistenceExecutionControl.execute( persistenceOperation );
      
      //      
      @SuppressWarnings("unchecked")
      final AccessEventData<E> event = new AccessEventData<E>( AccessEvent.REMOVE, Arrays.asList( element ) );
      ElementStore.this.eventManager.fireEvent( event );
    }
  }
  
  /**
   * @param element
   */
  private void fireEventForAddedElement( final E element )
  {
    //
    if ( this.persistenceAccessor != null && this.persistenceExecutionControl != null )
    {
      //
      final PersistenceOperation persistenceOperation = new PersistenceOperation()
      {
        
        @Override
        public void execute()
        {
          //
          try
          {
            final Long identifier = ElementStore.this.elementToIdentifierMap.get( element );
            if ( identifier != null )
            {
              ElementStore.this.persistenceAccessor.add( identifier, element );
            }
          }
          catch ( Exception e )
          {
            ElementStore.this.exceptionHandlerManager.getExceptionHandler().handleExcpetion( e );
          }
        }
      };
      this.persistenceExecutionControl.execute( persistenceOperation );
      
      //
      //
      @SuppressWarnings("unchecked")
      final AccessEventData<E> event = new AccessEventData<E>( AccessEvent.ADD, Arrays.asList( element ) );
      this.eventManager.fireEvent( event );
    }
  }
  
  /**
   * @param newElementToIdentifierMap
   */
  private void fireEventForReload( final Map<E, Long> newElementToIdentifierMap )
  {
    //
    final List<E> elementList = new ArrayList<E>( newElementToIdentifierMap.keySet() );
    final AccessEventData<E> event = new AccessEventData<E>( AccessEvent.RELOAD, elementList );
    this.eventManager.fireEvent( event );
  }
  
  /**
   * Extracts a key value from a given element
   * 
   * @see ElementStore#newIndex(KeyExtractor)
   * @author Omnaest
   * @param <K>
   * @param <E>
   */
  public static interface KeyExtractor<K, E>
  {
    /**
     * Returns a key value for a given element
     * 
     * @param element
     * @return
     */
    public K getKey( E element );
  }
  
  /**
   * Internal event for the {@link ElementStore}
   * 
   * @author Omnaest
   */
  protected static class AccessEventData<E>
  {
    /* ********************************************** Variables ********************************************** */
    private final AccessEvent accessEvent;
    private final List<E>     elementList;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * @see AccessEventData
     * @author Omnaest
     */
    public enum AccessEvent
    {
      ADD,
      REMOVE,
      RELOAD
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see AccessEventData
     * @param accessEvent
     * @param elementList
     */
    public AccessEventData( AccessEvent accessEvent, List<E> elementList )
    {
      super();
      this.accessEvent = accessEvent;
      this.elementList = elementList;
    }
    
    /**
     * @return the accessEvent
     */
    public AccessEvent getAccessEvent()
    {
      return this.accessEvent;
    }
    
    /**
     * @return the elementList
     */
    public List<E> getElementList()
    {
      return this.elementList;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "AccessEventData [accessEvent=" );
      builder.append( this.accessEvent );
      builder.append( ", elementList=" );
      builder.append( this.elementList );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * Generates and manage new {@link Long} identifiers. This is a thread safe implementation.
   * 
   * @author Omnaest
   */
  protected static class IdentifierFactory implements Factory<Long>
  {
    /* ********************************************** Variables ********************************************** */
    private Set<Long>  availableIdentifierSet     = new ConcurrentSkipListSet<Long>();
    private Lock       availableIdentifierSetLock = new ReentrantLock();
    private AtomicLong upperIdentifierBoundary    = new AtomicLong();
    
    /* ********************************************** Methods ********************************************** */
    
    @Override
    public Long newInstance()
    {
      //
      Long retval = null;
      
      //
      if ( !this.availableIdentifierSet.isEmpty() )
      {
        //
        this.availableIdentifierSetLock.lock();
        try
        {
          //
          final Iterator<Long> iterator = this.availableIdentifierSet.iterator();
          if ( iterator.hasNext() )
          {
            //
            retval = iterator.next();
            iterator.remove();
          }
        }
        finally
        {
          this.availableIdentifierSetLock.unlock();
        }
      }
      
      //
      if ( retval == null )
      {
        retval = this.upperIdentifierBoundary.getAndIncrement();
      }
      
      // 
      return retval;
    }
    
    /**
     * Marks the given identifier as available again
     * 
     * @param identifier
     */
    public void release( Long identifier )
    {
      //
      if ( identifier != null )
      {
        //
        this.availableIdentifierSetLock.lock();
        try
        {
          //
          this.availableIdentifierSet.add( identifier );
        }
        finally
        {
          this.availableIdentifierSetLock.unlock();
        }
      }
    }
    
    /**
     * @see #release(Long)
     * @param identifierIterable
     */
    public void releaseAll( Iterable<Long> identifierIterable )
    {
      if ( identifierIterable != null )
      {
        for ( Long identifier : identifierIterable )
        {
          this.release( identifier );
        }
      }
    }
    
    /**
     * Marks the given identifier as used
     * 
     * @param identifier
     */
    public void aquire( Long identifier )
    {
      //
      if ( identifier != null )
      {
        //
        this.availableIdentifierSetLock.lock();
        try
        {
          //
          for ( long ii = this.upperIdentifierBoundary.get(); ii < identifier; ii = this.upperIdentifierBoundary.incrementAndGet() )
          {
            this.availableIdentifierSet.add( ii );
          }
          
          //
          if ( this.upperIdentifierBoundary.get() == identifier )
          {
            this.upperIdentifierBoundary.incrementAndGet();
          }
          
          //
          this.availableIdentifierSet.remove( identifier );
        }
        finally
        {
          this.availableIdentifierSetLock.unlock();
        }
      }
    }
    
    /**
     * @see #aquire(Long)
     * @param identifierIterable
     */
    public void aquireAll( Iterable<Long> identifierIterable )
    {
      if ( identifierIterable != null )
      {
        for ( Long identifier : identifierIterable )
        {
          this.aquire( identifier );
        }
      }
    }
  }
  
  /**
   * Accessor interface for any persistence implementation.
   * 
   * @see ElementStore
   * @author Omnaest
   * @param <E>
   */
  public static interface PersistenceAccessor<E>
  {
    /**
     * Adds a given element with the given identifier to the persistence
     * 
     * @param identifier
     * @param element
     */
    public void add( long identifier, E element );
    
    /**
     * Removes the element with the given identifier from the persistence
     * 
     * @param identifier
     */
    public void remove( long identifier );
    
    /**
     * Returns all available elements as keys and their identifiers as value of a {@link Map}
     * 
     * @return
     */
    public Map<E, Long> getElementToIdentifierMap();
    
    /**
     * @return {@link ExceptionHandlerRegistration}
     * @see ExceptionHandlerManager#getExceptionHandlerRegistration()
     */
    public ExceptionHandlerRegistration getExceptionHandlerRegistration();
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ElementStore
   * @param persistenceAccessor
   *          {@link PersistenceAccessor}
   */
  public ElementStore( PersistenceAccessor<E> persistenceAccessor )
  {
    //
    super();
    this.persistenceAccessor = persistenceAccessor;
    
    //
    Assert.isNotNull( persistenceAccessor, "PersistenceAccessor must not be null" );
    persistenceAccessor.getExceptionHandlerRegistration()
                       .registerExceptionHandler( this.exceptionHandlerManager.getExceptionHandler() );
    
    //
    this.loadAllElementsFromPersistence( persistenceAccessor, this.elementToIdentifierMap );
  }
  
  /**
   * @see ElementStore
   * @param persistenceAccessor
   *          {@link PersistenceAccessor}
   * @param collection
   */
  public ElementStore( PersistenceAccessor<E> persistenceAccessor, Collection<E> collection )
  {
    //
    this( persistenceAccessor );
    
    //
    this.addAll( collection );
  }
  
  /**
   * @param persistenceAccessor
   * @param elementToIdentifierMap
   */
  protected void loadAllElementsFromPersistence( PersistenceAccessor<E> persistenceAccessor, Map<E, Long> elementToIdentifierMap )
  {
    //
    if ( persistenceAccessor != null )
    {
      //
      try
      {
        //
        final Map<E, Long> newElementToIdentifierMap = persistenceAccessor.getElementToIdentifierMap();
        elementToIdentifierMap.putAll( newElementToIdentifierMap );
        this.identifierFactory.aquireAll( newElementToIdentifierMap.values() );
      }
      catch ( Exception e )
      {
        this.exceptionHandlerManager.getExceptionHandler().handleExcpetion( e );
      }
    }
  }
  
  @Override
  public int size()
  {
    return this.elementToIdentifierMap.size();
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.elementToIdentifierMap.keySet().contains( o );
  }
  
  @Override
  public Iterator<E> iterator()
  {
    //
    final Iterator<E> iterator = this.elementToIdentifierMap.keySet().iterator();
    return new Iterator<E>()
    {
      /* ********************************************** Variables ********************************************** */
      private E currentElement = null;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return iterator.hasNext();
      }
      
      @Override
      public E next()
      {
        return this.currentElement = iterator.next();
      }
      
      @Override
      public void remove()
      {
        if ( this.currentElement != null )
        {
          ElementStore.this.fireEventForRemovedElement( this.currentElement );
          this.currentElement = null;
          iterator.remove();
        }
      }
    };
  }
  
  @Override
  public boolean add( E element )
  {
    //
    boolean retval = false;
    
    //
    if ( element != null )
    {
      //
      this.elementToIdentifierMap.put( element, this.identifierFactory.newInstance() );
      this.fireEventForAddedElement( element );
      retval = true;
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean remove( Object object )
  {
    //
    boolean retval = false;
    
    //
    final Set<E> elementSet = this.elementToIdentifierMap.keySet();
    if ( object != null && elementSet.contains( object ) )
    {
      //
      this.fireEventForRemovedElement( (E) object );
      retval = elementSet.remove( object );
    }
    
    // 
    return retval;
  }
  
  /**
   * @return
   */
  private static <E> Map<E, Long> newElementToIdentifierMap()
  {
    return new ConcurrentHashMap<E, Long>();
  }
  
  /**
   * Reloads the elements from the persistence and switches nearly atomically to the new set of elements.
   * 
   * @return
   */
  public ElementStore<E> reloadFromPersistence()
  {
    //
    final Map<E, Long> newElementToIdentifierMap = newElementToIdentifierMap();
    final Map<E, Long> oldElementToIdentifierMap = this.elementToIdentifierMap;
    
    //
    this.loadAllElementsFromPersistence( this.persistenceAccessor, newElementToIdentifierMap );
    this.elementToIdentifierMap = newElementToIdentifierMap;
    
    //
    this.identifierFactory.releaseAll( oldElementToIdentifierMap.values() );
    this.fireEventForReload( newElementToIdentifierMap );
    
    //
    return this;
  }
  
  /**
   * @see #newIndex(KeyExtractor, Comparator)
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @return
   */
  public <K extends Comparable<K>> SortedMap<K, E> newIndex( final KeyExtractor<K, E> keyExtractor )
  {
    //
    final Comparator<? super K> comparator = null;
    return this.newIndex( keyExtractor, comparator );
  }
  
  /**
   * @see #newIndex(KeyExtractor)
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param comparator
   *          {@link Comparator}
   * @return
   */
  @SuppressWarnings("unchecked")
  public <K> SortedMap<K, E> newIndex( final KeyExtractor<K, E> keyExtractor, final Comparator<? super K> comparator )
  {
    //
    final SortedMap<K, E> sortedMap = newInitializedSortedMap( keyExtractor, comparator );
    final SortedMapDecorator<K, E> retmap = new SortedMapDecorator<K, E>( sortedMap );
    
    //
    final EventListener<AccessEventData<E>, Void> listener = new EventListener<AccessEventData<E>, Void>()
    {
      /* ********************************************** Constants ********************************************** */
      private static final long serialVersionUID = -1968435453056648391L;
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public List<Void> handleEvent( AccessEventData<E> accessEventData )
      {
        //
        if ( accessEventData != null )
        {
          //
          final AccessEvent accessEvent = accessEventData.getAccessEvent();
          final List<E> elementList = accessEventData.getElementList();
          final E firstElement = ListUtils.firstElement( elementList );
          final K key = keyExtractor.getKey( firstElement );
          
          //
          if ( key != null )
          {
            if ( AccessEvent.ADD.equals( accessEvent ) )
            {
              //             
              sortedMap.put( key, firstElement );
            }
            else if ( AccessEvent.REMOVE.equals( accessEvent ) )
            {
              //
              sortedMap.remove( key );
            }
            else if ( AccessEvent.RELOAD.equals( accessEvent ) )
            {
              //
              final SortedMap<K, E> sortedMap = newInitializedSortedMap( keyExtractor, comparator );
              retmap.setSortedMap( sortedMap );
            }
          }
        }
        
        //
        return null;
      }
    };
    this.eventManager.getEventListenerRegistration().addEventListener( listener );
    
    //
    return (SortedMap<K, E>) MapUtils.unmodifiableSortedMap( retmap );
  }
  
  /**
   * @param keyExtractor
   * @param comparator
   * @return
   */
  private <K> SortedMap<K, E> newInitializedSortedMap( final KeyExtractor<K, E> keyExtractor, Comparator<? super K> comparator )
  {
    //
    Assert.isNotNull( keyExtractor, "keyAccessor must not be null" );
    final SortedMap<K, E> sortedMap = comparator != null ? new ConcurrentSkipListMap<K, E>( comparator )
                                                        : new ConcurrentSkipListMap<K, E>();
    
    //
    for ( E element : this.elementToIdentifierMap.keySet() )
    {
      //
      final K key = keyExtractor.getKey( element );
      if ( key != null )
      {
        sortedMap.put( key, element );
      }
    }
    
    //
    return sortedMap;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ElementStore [elementToIdentifierMap=" );
    builder.append( this.elementToIdentifierMap );
    builder.append( ", persistenceAccessor=" );
    builder.append( this.persistenceAccessor );
    builder.append( "]" );
    return builder.toString();
  }
  
  /**
   * @param persistenceExecutionControl
   *          the {@link PersistenceExecutionControl} to set
   * @return this
   */
  public ElementStore<E> setPersistenceExecutionControl( PersistenceExecutionControl<E> persistenceExecutionControl )
  {
    this.persistenceExecutionControl = persistenceExecutionControl;
    return this;
  }
  
  /**
   * @return
   * @see ExceptionHandlerManager#getExceptionHandlerRegistration()
   */
  public ExceptionHandlerRegistration getExceptionHandlerRegistration()
  {
    return this.exceptionHandlerManager.getExceptionHandlerRegistration();
  }
  
}
