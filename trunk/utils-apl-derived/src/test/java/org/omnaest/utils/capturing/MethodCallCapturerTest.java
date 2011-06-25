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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.proxy.MethodCallCapturer;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCaptureContext;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCapturerAware;
import org.omnaest.utils.proxy.MethodCallCapturer.ReplayResult;

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
    
    public TestSubInterface doTestSubInterface();
  }
  
  protected static interface TestSubInterface
  {
    public Double doCalculateSomething();
    
    public TestSubInterface doTestSubInterface();
  }
  
  protected static class TestInterfaceObject implements TestInterface
  {
    public String                       doSomethingText            = null;
    public String                       doSomethingPrimitiveText   = null;
    public List<TestSubInterfaceObject> testSubInterfaceObjectList = new ArrayList<MethodCallCapturerTest.TestSubInterfaceObject>();
    
    @Override
    public String doSomething( String text )
    {
      this.doSomethingText = text;
      return text;
    }
    
    @Override
    public boolean doSomethingPrimitive( String text )
    {
      this.doSomethingPrimitiveText = text;
      return false;
    }
    
    @Override
    public TestSubInterface doTestSubInterface()
    {
      TestSubInterfaceObject testSubInterfaceObject = new TestSubInterfaceObject();
      this.testSubInterfaceObjectList.add( testSubInterfaceObject );
      return testSubInterfaceObject;
    }
    
  }
  
  protected static class TestSubInterfaceObject implements TestSubInterface
  {
    public boolean                doCalcualteSomething   = false;
    public TestSubInterfaceObject testSubInterfaceObject = null;
    
    @Override
    public Double doCalculateSomething()
    {
      this.doCalcualteSomething = true;
      return Double.valueOf( 345 );
    }
    
    @Override
    public TestSubInterface doTestSubInterface()
    {
      this.testSubInterfaceObject = new TestSubInterfaceObject();
      return this.testSubInterfaceObject;
    }
    
  }
  
  @Test
  public void testNewCaptureTypeInstance()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfCapturedType( TestInterface.class );
    
    //
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    
    //
    List<MethodCallCaptureContext> methodCallCaptureContextList = methodCallCapturer.getMethodCallCaptureContextList();
    
    assertEquals( 2, methodCallCaptureContextList.size() );
    assertEquals( "text value", methodCallCaptureContextList.get( 0 ).getMethodCallCapture().getArgs()[0] );
    assertEquals( "more text", methodCallCaptureContextList.get( 1 ).getMethodCallCapture().getArgs()[0] );
    
    assertEquals( methodCallCaptureContextList.get( 1 ), methodCallCapturer.getLastMethodCallContext() );
  }
  
  @Test
  public void testNewCapturedTypeInstanceMethodNameCapturerAware()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware( TestInterface.class );
    
    //
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    
    //
    assertTrue( testInterface instanceof MethodCallCapturerAware );
    
    //
    MethodCallCapturerAware methodCallCapturerAware = (MethodCallCapturerAware) testInterface;
    assertNotNull( methodCallCapturerAware.getMethodCallCapturer() );
  }
  
  @Test
  public void testNewInstanceOfCapturedTypeNotBeingTransitiv()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfCapturedType( TestInterface.class );
    
    //
    TestSubInterface doTestSubInterface = testInterface.doTestSubInterface();
    assertNull( doTestSubInterface );
  }
  
  @Test
  public void testNewInstanceOfTransitivlyCapturedType()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    TestSubInterface doTestSubInterface = testInterface.doTestSubInterface();
    assertNotNull( doTestSubInterface );
    
    //
    doTestSubInterface.doCalculateSomething();
    
    //
    List<String> capturedCanonicalMethodNameList = methodCallCapturer.getCapturedCanonicalMethodNameList();
    assertNotNull( capturedCanonicalMethodNameList );
    assertEquals( 2, capturedCanonicalMethodNameList.size() );
    assertTrue( capturedCanonicalMethodNameList.contains( "doTestSubInterface" ) );
    assertTrue( capturedCanonicalMethodNameList.contains( "doTestSubInterface.doCalculateSomething" ) );
  }
  
  @Test
  public void testMethodNameOf()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    String methodName;
    {
      //
      methodName = methodCallCapturer.methodNameOf( testInterface.doSomething( "text value" ) );
      assertEquals( "doSomething", methodName );
      
      //
      methodName = methodCallCapturer.methodNameOf( testInterface.doSomethingPrimitive( "more text" ) );
      assertEquals( "doSomethingPrimitive", methodName );
      
      //
      methodName = methodCallCapturer.methodNameOf( testInterface.doTestSubInterface().doCalculateSomething() );
      assertEquals( "doTestSubInterface.doCalculateSomething", methodName );
      
      //
      methodName = methodCallCapturer.methodNameOf( testInterface.doTestSubInterface()
                                                                 .doTestSubInterface()
                                                                 .doCalculateSomething() );
      assertEquals( "doTestSubInterface.doTestSubInterface.doCalculateSomething", methodName );
    }
  }
  
  @Test
  public void testReplay()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    testInterface.doTestSubInterface().doCalculateSomething();
    testInterface.doTestSubInterface().doTestSubInterface().doCalculateSomething();
    
    //
    TestInterfaceObject testInterfaceObject = new TestInterfaceObject();
    
    //
    ReplayResult replayResult = methodCallCapturer.replay( testInterfaceObject );
    assertTrue( replayResult.isReplaySuccessful() );
    
    //
    assertEquals( "text value", testInterfaceObject.doSomethingText );
    assertEquals( "more text", testInterfaceObject.doSomethingPrimitiveText );
    assertEquals( 2, testInterfaceObject.testSubInterfaceObjectList.size() );
    
    //
    {
      TestSubInterfaceObject testSubInterfaceObject = testInterfaceObject.testSubInterfaceObjectList.get( 0 );
      assertEquals( true, testSubInterfaceObject.doCalcualteSomething );
      assertNull( testSubInterfaceObject.testSubInterfaceObject );
    }
    
    //
    {
      TestSubInterfaceObject testSubInterfaceObject = testInterfaceObject.testSubInterfaceObjectList.get( 1 );
      assertEquals( false, testSubInterfaceObject.doCalcualteSomething );
      assertNotNull( testSubInterfaceObject.testSubInterfaceObject );
      
      TestSubInterfaceObject subTestSubInterfaceObjectSub = testSubInterfaceObject.testSubInterfaceObject;
      assertEquals( true, subTestSubInterfaceObjectSub.doCalcualteSomething );
      assertNull( subTestSubInterfaceObjectSub.testSubInterfaceObject );
    }
  }
  
}
