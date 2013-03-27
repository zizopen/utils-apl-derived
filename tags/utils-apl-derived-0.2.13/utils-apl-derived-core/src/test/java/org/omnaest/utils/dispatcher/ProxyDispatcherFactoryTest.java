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
package org.omnaest.utils.dispatcher;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @see ProxyDispatcherFactory
 * @author Omnaest
 */
public class ProxyDispatcherFactoryTest
{
  private static final int                      numberOfInvocations = 1000000;
  
  @Rule
  public ContiPerfRule                          contiPerfRule       = new ContiPerfRule();
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private ProxyDispatcherFactory<TestInterface> dispatcherFactory   = new ProxyDispatcherFactory<TestInterface>(
                                                                                                                 TestInterface.class );
  
  private final List<TestImpl>                  instanceList        = Arrays.asList( new TestImpl(), new TestImpl() );
  private TestInterface                         dispatcher          = this.dispatcherFactory.newDispatcher( this.instanceList );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static interface TestInterface
  {
    public void callMe();
    
    public void callMeTimes( int number );
  }
  
  private static class TestImpl implements TestInterface
  {
    private int numberOfCalls = 0;
    
    @Override
    public void callMe()
    {
      this.numberOfCalls++;
    }
    
    @Override
    public void callMeTimes( int number )
    {
      this.numberOfCalls += number;
    }
    
    public int getNumberOfCalls()
    {
      return this.numberOfCalls;
    }
    
    public void reset()
    {
      this.numberOfCalls = 0;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testNewDispatcher()
  {
    final List<TestImpl> instanceList = Arrays.asList( new TestImpl(), new TestImpl() );
    TestInterface dispatcher = this.dispatcherFactory.newDispatcher( instanceList );
    
    dispatcher.callMe();
    assertEquals( 1, instanceList.get( 0 ).getNumberOfCalls() );
    assertEquals( 1, instanceList.get( 1 ).getNumberOfCalls() );
    
    dispatcher.callMeTimes( 2 );
    assertEquals( 3, instanceList.get( 0 ).getNumberOfCalls() );
    assertEquals( 3, instanceList.get( 1 ).getNumberOfCalls() );
  }
  
  @Test
  // @PerfTest(invocations = numberOfInvocations)
  public void testNewDispatcherPerformance()
  {
    final TestImpl testImpl1 = this.instanceList.get( 0 );
    final TestImpl testImpl2 = this.instanceList.get( 1 );
    testImpl1.reset();
    testImpl2.reset();
    
    this.dispatcher.callMe();
    assertEquals( 1, testImpl1.getNumberOfCalls() );
    assertEquals( 1, testImpl2.getNumberOfCalls() );
    
    this.dispatcher.callMeTimes( 2 );
    assertEquals( 3, testImpl1.getNumberOfCalls() );
    assertEquals( 3, testImpl2.getNumberOfCalls() );
  }
  
  @Test
  // @PerfTest(invocations = numberOfInvocations)
  public void testNativeDispatcherPerformance()
  {
    final TestImpl testImpl1 = this.instanceList.get( 0 );
    final TestImpl testImpl2 = this.instanceList.get( 1 );
    testImpl1.reset();
    testImpl2.reset();
    
    for ( TestInterface instance : this.instanceList )
    {
      instance.callMe();
    }
    assertEquals( 1, testImpl1.getNumberOfCalls() );
    assertEquals( 1, testImpl2.getNumberOfCalls() );
    
    for ( TestInterface instance : this.instanceList )
    {
      instance.callMeTimes( 2 );
    }
    assertEquals( 3, testImpl1.getNumberOfCalls() );
    assertEquals( 3, testImpl2.getNumberOfCalls() );
  }
  
}
