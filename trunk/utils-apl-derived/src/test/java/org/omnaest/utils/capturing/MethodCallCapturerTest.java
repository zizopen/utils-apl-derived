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
package org.omnaest.utils.capturing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.proxy.MethodCallCapture;
import org.omnaest.utils.proxy.MethodCallCapturer;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCapturerAware;

public class MethodCallCapturerTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static interface TestInterface
  {
    public String doSomething( String text );
    
    public boolean doSomethingPrimitive( String text );
  }
  
  @Test
  public void testNewCaptureTypeInstance()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newCapturedTypeInstance( TestInterface.class );
    
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    
    //
    List<MethodCallCapture> methodCallCaptureList = methodCallCapturer.getMethodCallCaptureList();
    
    assertEquals( 2, methodCallCaptureList.size() );
    assertEquals( "text value", methodCallCaptureList.get( 0 ).getArgs()[0] );
    assertEquals( "more text", methodCallCaptureList.get( 1 ).getArgs()[0] );
    
    assertEquals( methodCallCaptureList.get( 1 ), methodCallCapturer.getLastMethodCallCapture() );
  }
  
  @Test
  public void testNewCapturedTypeInstanceMethodNameCapturerAware()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newCapturedTypeInstanceMethodCallCapturerAware( TestInterface.class );
    
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    
    //
    assertTrue( testInterface instanceof MethodCallCapturerAware );
    
    //
    MethodCallCapturerAware methodCallCapturerAware = (MethodCallCapturerAware) testInterface;
    assertNotNull( methodCallCapturerAware.getMethodCallCapturer() );
  }
  
}
