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

import static org.junit.Assert.assertEquals;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.OperationFactory;

/**
 * @see OperationBatteryRoundRobin
 * @author Omnaest
 */
public class OperationBatteryRoundRobinTest
{
  @Rule
  public ContiPerfRule                     contiPerfRule          = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  private OperationFactory<String, String> operationFactory       = new OperationFactory<String, String>()
                                                                  {
                                                                    @Override
                                                                    public Operation<String, String> newOperation()
                                                                    {
                                                                      return new Operation<String, String>()
                                                                      {
                                                                        @Override
                                                                        public String execute( String parameter )
                                                                        {
                                                                          try
                                                                          {
                                                                            Thread.sleep( 10 );
                                                                          }
                                                                          catch ( InterruptedException e )
                                                                          {
                                                                          }
                                                                          return parameter;
                                                                        }
                                                                      };
                                                                    }
                                                                  };
  private boolean                          usingReentrantLock     = true;
  private int                              initialBatteryCapacity = 64;
  private OperationBattery<String, String> operationBattery       = new OperationBatteryRoundRobin<String, String>(
                                                                                                                    this.operationFactory,
                                                                                                                    this.usingReentrantLock,
                                                                                                                    this.initialBatteryCapacity );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  @PerfTest(invocations = 100, threads = 64)
  @Required(average = 400)
  public void testExecute()
  {
    for ( int ii = 0; ii < 10; ii++ )
    {
      //
      String parameter = "test" + ii;
      String result = this.operationBattery.execute( parameter );
      assertEquals( parameter, result );
    }
  }
  
}
