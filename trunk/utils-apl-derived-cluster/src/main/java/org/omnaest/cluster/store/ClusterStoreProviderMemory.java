/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.cluster.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.omnaest.utils.assertion.Assert;

public class ClusterStoreProviderMemory extends ClusterStoreProviderAbstract implements ClusterStoreProvider
{
  private static final long                            serialVersionUID             = 3992344738331974625L;
  private final Map<ClusterStoreIdentifier<?>, Object> qualifiersToObjectMap        = new ConcurrentHashMap<ClusterStoreProvider.ClusterStoreIdentifier<?>, Object>();
  private ReadWriteLock                                readWriteLock                = new ReentrantReadWriteLock();
  private ClusterStoreProvider                         delegateClusterStoreProvider = null;
  
  public ClusterStoreProviderMemory setDelegateClusterStoreProvider( ClusterStoreProvider delegateClusterStoreProvider )
  {
    this.delegateClusterStoreProvider = delegateClusterStoreProvider;
    return this;
  }
  
  @Override
  public <T> ClusterStore<T> getClusterStore( final ClusterStoreIdentifier<T> clusterStoreIdentifier )
  {
    Assert.isNotNull( clusterStoreIdentifier, "clusterStoreIdentifier must not be null" );
    final ClusterStore<T> clusterStoreDelegate;
    if ( this.delegateClusterStoreProvider != null )
    {
      clusterStoreDelegate = this.delegateClusterStoreProvider.getClusterStore( clusterStoreIdentifier );
    }
    else
    {
      clusterStoreDelegate = null;
    }
    final Lock readLock = this.readWriteLock.readLock();
    final Lock writeLock = this.readWriteLock.writeLock();
    return new ClusterStore<T>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public T get()
      {
        readLock.lock();
        try
        {
          return (T) ClusterStoreProviderMemory.this.qualifiersToObjectMap.get( clusterStoreIdentifier );
        }
        finally
        {
          readLock.unlock();
        }
      }
      
      @Override
      public void set( T instance )
      {
        writeLock.lock();
        try
        {
          ClusterStoreProviderMemory.this.qualifiersToObjectMap.put( clusterStoreIdentifier, instance );
          if ( clusterStoreDelegate != null )
          {
            clusterStoreDelegate.set( instance );
          }
        }
        finally
        {
          writeLock.unlock();
        }
      }
      
      @Override
      public void remove()
      {
        writeLock.lock();
        try
        {
          ClusterStoreProviderMemory.this.qualifiersToObjectMap.remove( clusterStoreIdentifier );
          if ( clusterStoreDelegate != null )
          {
            clusterStoreDelegate.remove();
          }
        }
        finally
        {
          writeLock.unlock();
        }
      }
    };
  }
  
  @Override
  public <T> ClusterStoreIdentifier<?>[] getClusterStoreIdentifiers()
  {
    final Lock readLock = this.readWriteLock.readLock();
    readLock.lock();
    try
    {
      return this.qualifiersToObjectMap.keySet().toArray( new ClusterStoreIdentifier[0] );
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  @Override
  public void clear()
  {
    Lock writeLock = this.readWriteLock.writeLock();
    writeLock.lock();
    try
    {
      this.qualifiersToObjectMap.clear();
    }
    finally
    {
      writeLock.unlock();
    }
  }
  
  @Override
  public void executeWriteAtomical( Runnable runnable )
  {
    Lock writeLock = this.readWriteLock.writeLock();
    writeLock.lock();
    try
    {
      runnable.run();
    }
    finally
    {
      writeLock.unlock();
    }
  }
  
  @Override
  public void executeReadAtomical( Runnable runnable )
  {
    Lock readLock = this.readWriteLock.readLock();
    readLock.lock();
    try
    {
      runnable.run();
    }
    finally
    {
      readLock.unlock();
    }
  }
  
}
