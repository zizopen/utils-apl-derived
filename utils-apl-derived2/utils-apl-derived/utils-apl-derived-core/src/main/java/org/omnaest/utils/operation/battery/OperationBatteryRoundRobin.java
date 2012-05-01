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
package org.omnaest.utils.operation.battery;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.OperationFactory;
import org.omnaest.utils.structure.iterator.QueueToCircularIteratorAdapter;

/**
 * This is an {@link OperationBattery} implementation using a {@link ConcurrentLinkedQueue} in combination with a simple round
 * robin algorithm.
 * 
 * @see OperationBattery
 * @see Operation
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationBatteryRoundRobin<RESULT, PARAMETER> extends OperationBattery<RESULT, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected Queue<Operation<RESULT, PARAMETER>>  operationQueue;
  private Iterator<Operation<RESULT, PARAMETER>> operationQueueIterator;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see OperationBatteryRoundRobin
   * @param operationFactory
   * @param usingReentrantLock
   */
  public OperationBatteryRoundRobin( OperationFactory<RESULT, PARAMETER> operationFactory, boolean usingReentrantLock )
  {
    super( operationFactory, usingReentrantLock );
  }
  
  /**
   * @see OperationBatteryRoundRobin
   * @param operationFactory
   * @param usingReentrantLock
   * @param initialBatteryCapacity
   */
  public OperationBatteryRoundRobin( OperationFactory<RESULT, PARAMETER> operationFactory, boolean usingReentrantLock,
                                     int initialBatteryCapacity )
  {
    super( operationFactory, usingReentrantLock, initialBatteryCapacity );
  }
  
  /**
   * Resolves an active {@link Iterator} instance from the current {@link #operationQueue}
   * 
   * @return
   */
  protected Iterator<Operation<RESULT, PARAMETER>> resolveActiveOperationQueueIterator()
  {
    //
    return this.operationQueueIterator;
  }
  
  @Override
  public RESULT execute( PARAMETER parameter )
  {
    //
    RESULT retval = null;
    
    //
    Iterator<Operation<RESULT, PARAMETER>> operationQueueIterator = this.resolveActiveOperationQueueIterator();
    if ( operationQueueIterator.hasNext() )
    {
      //
      Operation<RESULT, PARAMETER> operation = operationQueueIterator.next();
      
      //
      retval = operation.execute( parameter );
    }
    
    //
    return retval;
  }
  
  @Override
  protected void initializeOperationBattery( int initialBatteryCapacity )
  {
    //
    this.operationQueue = new ConcurrentLinkedQueue<Operation<RESULT, PARAMETER>>();
    this.operationQueueIterator = new QueueToCircularIteratorAdapter<Operation<RESULT, PARAMETER>>( this.operationQueue );
    for ( int ii = 0; ii < initialBatteryCapacity; ii++ )
    {
      //
      Operation<RESULT, PARAMETER> operation = this.resolveNewOperationInstanceFromOperationFactory();
      this.operationQueue.offer( operation );
    }
  }
  
}
