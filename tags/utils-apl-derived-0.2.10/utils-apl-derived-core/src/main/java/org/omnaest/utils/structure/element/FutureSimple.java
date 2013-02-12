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
package org.omnaest.utils.structure.element;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple {@link Future} implementation using a {@link CountDownLatch} which unlocks when the value is set.
 * 
 * @author Omnaest
 * @param <V>
 */
public class FutureSimple<V> implements Future<V>
{
  /* ********************************************** Variables ********************************************** */
  private final AtomicBoolean  shouldCancel   = new AtomicBoolean( false );
  private final AtomicBoolean  isCancelled    = new AtomicBoolean( false );
  private final CountDownLatch countDownLatch = new CountDownLatch( 1 );
  private V                    value          = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see Future
   * @see FutureSimple
   */
  public FutureSimple()
  {
    super();
  }
  
  @Override
  public boolean cancel( boolean mayInterruptIfRunning )
  {
    this.shouldCancel.set( true );
    return true;
  }
  
  @Override
  public boolean isCancelled()
  {
    return this.isCancelled.get();
  }
  
  @Override
  public boolean isDone()
  {
    return this.countDownLatch.getCount() == 0;
  }
  
  @Override
  public V get() throws InterruptedException,
                ExecutionException
  {
    //
    V retval = null;
    
    // 
    this.countDownLatch.await();
    retval = this.value;
    
    //
    return retval;
  }
  
  @Override
  public V get( long timeout, TimeUnit unit ) throws InterruptedException,
                                             ExecutionException,
                                             TimeoutException
  {
    //
    V retval = null;
    
    //
    this.countDownLatch.await( timeout, unit );
    retval = this.value;
    
    // 
    return retval;
  }
  
  /**
   * Sets the value to be returned by {@link Future#get()}. After setting any value the {@link Future#isDone()} will return true.
   * 
   * @param value
   */
  public void setValue( V value )
  {
    this.value = value;
    this.countDownLatch.countDown();
  }
  
  /**
   * Returns true if the {@link Future#cancel(boolean)} method has been called at least once
   * 
   * @return
   */
  public boolean getShouldCancel()
  {
    return this.shouldCancel.get();
  }
  
  /**
   * Sets the value for the {@link Future#isCancelled()}
   * 
   * @param isCancelled
   */
  public void setIsCancelled( boolean isCancelled )
  {
    this.isCancelled.set( isCancelled );
  }
  
}
