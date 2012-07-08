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
package org.omnaest.utils.proxy;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;

/**
 * @see StubCreator
 * @author Omnaest
 */
public class StubCreatorTest
{
  
  /* ********************************************** Variables ********************************************** */
  private final MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler()
                                                                {
                                                                  @Override
                                                                  public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
                                                                  {
                                                                    return methodCallCapture.getArgumentCasted( 0 );
                                                                  }
                                                                };
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static interface TestInterface
  {
    public String echoValue( String value );
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testNewInstance()
  {
    //
    final TestInterface testInterface = StubCreator.newStubInstance( TestInterface.class, this.methodInvocationHandler );
    assertEquals( "test", testInterface.echoValue( "test" ) );
  }
  
  @Test
  public void testBuild()
  {
    //
    final StubCreator<TestInterface> stubCreator = new StubCreator<TestInterface>( TestInterface.class, (Class<?>[]) null );
    final TestInterface testInterface = stubCreator.build( this.methodInvocationHandler );
    assertEquals( "test", testInterface.echoValue( "test" ) );
  }
  
  @Test
  @Ignore("Performancetest")
  public void testNewInstancePerformance()
  {
    //
    final int numberOfInvocations = 1000000;
    for ( int ii = 0; ii < numberOfInvocations; ii++ )
    {
      final TestInterface testInterface = StubCreator.newStubInstance( TestInterface.class, this.methodInvocationHandler );
      assertEquals( "test", testInterface.echoValue( "test" ) );
    }
  }
  
  @Test
  @Ignore("Performancetest")
  public void testBuildPerformance()
  {
    //
    final StubCreator<TestInterface> stubCreator = new StubCreator<TestInterface>( TestInterface.class, (Class<?>[]) null );
    
    //    
    final int numberOfInvocations = 1000000;
    for ( int ii = 0; ii < numberOfInvocations; ii++ )
    {
      //
      final TestInterface testInterface = stubCreator.build( this.methodInvocationHandler );
      assertEquals( "test", testInterface.echoValue( "test" ) );
    }
  }
}
