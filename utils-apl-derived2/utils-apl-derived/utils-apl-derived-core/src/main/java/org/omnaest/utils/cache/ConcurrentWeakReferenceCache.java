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
package org.omnaest.utils.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.structure.element.ElementHolder;

/**
 * A {@link ConcurrentWeakReferenceCache} holds a given number of separated {@link SynchronizedWeakReferenceCache} instances,
 * called segments. <br>
 * Based on the number of segments the retrieval operation will block less long for a multithreaded call to {@link #get(Object)},
 * {@link #containsKey(Object)}, {@link #containsValue(Object)}, {@link #size()} and {@link #isEmpty()}, but any additional
 * segment will increase the blocking time for any {@link #put(Object, Object)}, {@link #putAll(Map)}, {@link #remove(Object)} and
 * {@link #clear()} call, as well as it is increasing the memory footprint since every segment will hold all the information
 * within the {@link Cache}.
 * 
 * @see Cache
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class ConcurrentWeakReferenceCache<K, V> implements Cache<K, V>
{
  /* ********************************************** Variables ********************************************** */
  private final List<Cache<K, V>> cacheList;
  private final int               numberOfSegments;
  private final AtomicLong        currentSegmentCounter = new AtomicLong();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * This does create a {@link ConcurrentWeakReferenceCache} which uses as many segments as the
   * {@link Runtime#availableProcessors()} returns
   * 
   * @see ConcurrentWeakReferenceCache
   */
  public ConcurrentWeakReferenceCache()
  {
    this( Runtime.getRuntime().availableProcessors() );
  }
  
  /**
   * @see ConcurrentWeakReferenceCache
   * @param numberOfSegments
   */
  public ConcurrentWeakReferenceCache( int numberOfSegments )
  {
    //
    super();
    this.numberOfSegments = numberOfSegments;
    
    //
    this.cacheList = newCacheList( numberOfSegments );
  }
  
  /**
   * @param numberOfSegments
   * @return
   */
  private static <K, V> List<Cache<K, V>> newCacheList( int numberOfSegments )
  {
    //
    final List<Cache<K, V>> retlist = new CopyOnWriteArrayList<Cache<K, V>>();
    
    //
    for ( int ii = 0; ii < numberOfSegments; ii++ )
    {
      retlist.add( new SynchronizedWeakReferenceCache<K, V>() );
    }
    
    //
    return retlist;
  }
  
  @Override
  public int size()
  {
    return this.getCache().size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.getCache().isEmpty();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.getCache().containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.getCache().containsValue( value );
  }
  
  @Override
  public V get( Object key )
  {
    return this.getCache().get( key );
  }
  
  @Override
  public V put( final K key, final V value )
  {
    //
    final ElementHolder<V> retval = new ElementHolder<V>();
    
    //
    final Operation<Void, Cache<K, V>> operation = new Operation<Void, Cache<K, V>>()
    {
      @Override
      public Void execute( Cache<K, V> parameter )
      {
        retval.setElement( parameter.put( key, value ) );
        return null;
      }
    };
    this.executeOnAllCacheSegments( operation );
    
    //
    return retval.getElement();
  }
  
  @Override
  public V remove( final Object key )
  {
    //
    final ElementHolder<V> retval = new ElementHolder<V>();
    
    //
    final Operation<Void, Cache<K, V>> operation = new Operation<Void, Cache<K, V>>()
    {
      @Override
      public Void execute( Cache<K, V> parameter )
      {
        retval.setElement( parameter.remove( key ) );
        return null;
      }
    };
    this.executeOnAllCacheSegments( operation );
    
    //
    return retval.getElement();
  }
  
  @Override
  public void putAll( final Map<? extends K, ? extends V> m )
  {
    Operation<Void, Cache<K, V>> operation = new Operation<Void, Cache<K, V>>()
    {
      @Override
      public Void execute( Cache<K, V> parameter )
      {
        parameter.putAll( m );
        return null;
      }
    };
    this.executeOnAllCacheSegments( operation );
  }
  
  @Override
  public void clear()
  {
    Operation<Void, Cache<K, V>> operation = new Operation<Void, Cache<K, V>>()
    {
      @Override
      public Void execute( Cache<K, V> parameter )
      {
        parameter.clear();
        return null;
      }
    };
    this.executeOnAllCacheSegments( operation );
  }
  
  @Override
  public Set<K> keySet()
  {
    return this.getCache().keySet();
  }
  
  @Override
  public Collection<V> values()
  {
    return this.getCache().values();
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return this.getCache().entrySet();
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.getCache().equals( o );
  }
  
  @Override
  public int hashCode()
  {
    return this.getCache().hashCode();
  }
  
  /**
   * Executes the given {@link Operation} on all {@link Cache} segments
   * 
   * @param operation
   */
  private void executeOnAllCacheSegments( Operation<Void, Cache<K, V>> operation )
  {
    for ( Cache<K, V> cache : this.cacheList )
    {
      operation.execute( cache );
    }
  }
  
  /**
   * Returns every time a new one of the segment caches. This does move the internal pointer the opposite ways the
   * {@link #executeOnAllCacheSegments(Operation)} will move through all the cache segments. This ensures only blocking once and
   * not getting a race condition between the read and write iteraton.
   * 
   * @return
   */
  private Cache<K, V> getCache()
  {
    //
    final int numberOfSegments = this.numberOfSegments;
    final int index = ( numberOfSegments - 1 )
                      - ( Math.abs( (int) this.currentSegmentCounter.getAndIncrement() ) % numberOfSegments );
    return this.cacheList.get( index );
  }
  
}
