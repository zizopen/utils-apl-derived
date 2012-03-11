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

import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.OperationFactory;
import org.omnaest.utils.operation.decorator.OperationDecoratorReentrantLock;

/**
 * An {@link OperationBattery} allows to use a single operation facade to access a pool of object instances which offers one and
 * the same method in a multithreaded environment.<br>
 * <br>
 * The underlying operation instances will be resolved by a single {@link OperationFactory} initially.<br>
 * These instances do not have to be thread safe, if {@link #setUsingReentrantLock(boolean)} is set to true. In that case the
 * {@link OperationBattery} will decorate calls to the instances with a {@link ReentrantLock}, so that in the case there are more
 * invocations than unused instances available, that the incoming invocations will be blocked until an instance of an underlying
 * {@link Operation} gets unlocked. This ensures the thread safetyness of the {@link OperationBattery}.<br>
 * <br>
 * An {@link OperationBattery} should be given an initial capacity of instances in that way, that the required throughput can be
 * achieved by the throughput of a single {@link Operation} instance multiplied with the capacity number. E.g. if a single
 * {@link Operation} can handle 100 requests per second and 1000 requests per seconds is the requirement, an
 * {@link OperationBattery} with a capacity of 10 instances should narrow down the required throughput very well.<br>
 * <br>
 * But be aware that the scaling behavior is drained rapidly, if a single {@link Operation#execute(Object)} invocation is much
 * faster, than the internal distribution algorithm. So the scaling will be much better for {@link Operation}s with an invocation
 * duration of at least 1 millisecond or more.
 * 
 * @see #DEFAULT_INITIAL_BATTERY_CAPACITY
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public abstract class OperationBattery<RESULT, PARAMETER> implements Operation<RESULT, PARAMETER>
{
  /* ********************************************** Constants ********************************************** */
  public final static int                     DEFAULT_INITIAL_BATTERY_CAPACITY = 32;
  /* ********************************************** Variables ********************************************** */
  private OperationFactory<RESULT, PARAMETER> operationFactory                 = null;
  private boolean                             usingReentrantLock               = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see OperationBattery
   * @param operationFactory
   * @param usingReentrantLock
   */
  public OperationBattery( OperationFactory<RESULT, PARAMETER> operationFactory, boolean usingReentrantLock,
                           int initialBatteryCapacity )
  {
    this.operationFactory = operationFactory;
    this.usingReentrantLock = usingReentrantLock;
    
    //
    this.initializeOperationBattery( initialBatteryCapacity );
  }
  
  /**
   * @see OperationBattery
   * @param operationFactory
   * @param usingReentrantLock
   */
  public OperationBattery( OperationFactory<RESULT, PARAMETER> operationFactory, boolean usingReentrantLock )
  {
    this( operationFactory, usingReentrantLock, DEFAULT_INITIAL_BATTERY_CAPACITY );
  }
  
  /**
   * Initialize the {@link OperationBattery} before the constructor call ends. This should be used to for example fill a container
   * with a given amount of {@link Operation} instances resolved by the {@link #resolveNewOperationInstanceFromOperationFactory()}
   * method.
   * 
   * @param initialBatteryCapacity
   */
  protected abstract void initializeOperationBattery( int initialBatteryCapacity );
  
  /**
   * @return
   */
  protected Operation<RESULT, PARAMETER> resolveNewOperationInstanceFromOperationFactory()
  {
    //
    Operation<RESULT, PARAMETER> retval = null;
    
    //
    Assert.isNotNull( this.operationFactory,
                      "OperationFactory reference is null, but is needed to create a new Operation instance." );
    retval = this.operationFactory.newOperation();
    
    //
    Assert.isNotNull( retval, "OperationFactory created a null reference, but it should provide new instances." );
    if ( this.usingReentrantLock )
    {
      //
      boolean fair = true;
      ReentrantLock reentrantLock = new ReentrantLock( fair );
      retval = new OperationDecoratorReentrantLock<RESULT, PARAMETER>( retval, reentrantLock );
    }
    
    //
    return retval;
  }
  
  /**
   * @see OperationFactory
   * @param operationFactory
   */
  public void setOperationFactory( OperationFactory<RESULT, PARAMETER> operationFactory )
  {
    this.operationFactory = operationFactory;
  }
  
  /**
   * If this returns true, the {@link OperationBattery} will ensure thread safetyness using a {@link ReentrantLock} on
   * {@link Operation} instances.
   * 
   * @see ReentrantLock
   * @return
   */
  public boolean isUsingReentrantLock()
  {
    return this.usingReentrantLock;
  }
  
  /**
   * @see #isUsingReentrantLock()
   * @param usingReentrantLock
   */
  public void setUsingReentrantLock( boolean usingReentrantLock )
  {
    this.usingReentrantLock = usingReentrantLock;
  }
  
}
