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
package org.omnaest.utils.structure.map.decorator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link MapDecorator} which uses a {@link Lock} instance to synchronize every method invocation. This allows to make not thread
 * safe {@link Map}s thread safe.<br>
 * <br>
 * Child {@link Collection}s like {@link #keySet()} as well as {@link #values()} and {@link #entrySet()} are using this
 * {@link Lock}, too.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class LockingMapDecorator<K, V> extends MapDecorator<K, V>
{
  /* ********************************************** Variables ********************************************** */
  protected final Lock lock;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see LockingMapDecorator
   * @param map
   * @param lock
   */
  public LockingMapDecorator( Map<K, V> map, Lock lock )
  {
    super( map );
    this.lock = lock;
  }
  
  /**
   * As default a new {@link ReentrantLock} instance is created
   * 
   * @see LockingMapDecorator
   * @param map
   */
  public LockingMapDecorator( Map<K, V> map )
  {
    super( map );
    this.lock = new ReentrantLock();
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#size()
   */
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.size();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.isEmpty();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey( Object key )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.containsKey( key );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue( Object value )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.containsValue( value );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#get(java.lang.Object)
   */
  @Override
  public V get( Object key )
  {
    //
    V retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.get( key );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public V put( K key, V value )
  {
    //
    V retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.put( key, value );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#remove(java.lang.Object)
   */
  @Override
  public V remove( Object key )
  {
    //
    V retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.remove( key );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#putAll(java.util.Map)
   */
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    //
    this.lock.lock();
    try
    {
      super.putAll( m );
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#clear()
   */
  @Override
  public void clear()
  {
    //
    this.lock.lock();
    try
    {
      super.clear();
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#keySet()
   */
  @Override
  public Set<K> keySet()
  {
    return SetUtils.locked( super.keySet(), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#values()
   */
  @Override
  public Collection<V> values()
  {
    return CollectionUtils.locked( super.values(), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#entrySet()
   */
  @Override
  public Set<Map.Entry<K, V>> entrySet()
  {
    return SetUtils.locked( super.entrySet(), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.equals( obj );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#hashCode()
   */
  @Override
  public int hashCode()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.hashCode();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.map.decorator.MapDecorator#toString()
   */
  @Override
  public String toString()
  {
    //
    String retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.toString();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
}
