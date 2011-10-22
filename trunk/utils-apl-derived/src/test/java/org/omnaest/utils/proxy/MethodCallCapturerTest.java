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
package org.omnaest.utils.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCaptureContext;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCapturerAware;
import org.omnaest.utils.proxy.MethodCallCapturer.ReplayResult;

/**
 * {@link MethodCallCapturer}
 * 
 * @author Omnaest
 */
public class MethodCallCapturerTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */

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
  
  protected static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    protected String    fieldString = null;
    protected Double    fieldDouble = null;
    protected TestClass testClass   = null;
    
    /* ********************************************** Methods ********************************************** */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    public TestClass getTestClass()
    {
      return new TestClass();
    }
    
    public void setTestClass( TestClass testClass )
    {
      this.testClass = testClass;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  @Before
  public void setUp() throws Exception
  {
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
    assertEquals( "text value", methodCallCaptureContextList.get( 0 ).getMethodCallCapture().getArguments()[0] );
    assertEquals( "more text", methodCallCaptureContextList.get( 1 ).getMethodCallCapture().getArguments()[0] );
    
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
  
  @Test
  public void testGetCapturedCanonicalMethodNameListWithMergedHierarchyCalls()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestInterface testInterface = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    TestInterface testInterface2 = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    testInterface2.doSomething( "text value" );
    testInterface2.doSomethingPrimitive( "more text" );
    
    testInterface.doSomething( "text value" );
    testInterface.doSomethingPrimitive( "more text" );
    testInterface.doTestSubInterface().doCalculateSomething();
    testInterface.doTestSubInterface().doTestSubInterface().doCalculateSomething();
    
    testInterface2.doTestSubInterface().doCalculateSomething();
    testInterface2.doTestSubInterface().doTestSubInterface().doCalculateSomething();
    
    //
    {
      List<String> capturedCanonicalMethodNameListWithMergedHierarchyCalls = methodCallCapturer.getCapturedCanonicalMethodNameListWithMergedHierarchyCalls( testInterface );
      assertEquals( Arrays.asList( "doSomething", "doSomethingPrimitive", "doTestSubInterface.doCalculateSomething",
                                   "doTestSubInterface.doTestSubInterface.doCalculateSomething" ),
                    capturedCanonicalMethodNameListWithMergedHierarchyCalls );
    }
    
    //
    {
      List<String> capturedCanonicalMethodNameListWithMergedHierarchyCalls = methodCallCapturer.getCapturedCanonicalMethodNameListWithMergedHierarchyCalls( testInterface2 );
      assertEquals( Arrays.asList( "doSomething", "doSomethingPrimitive", "doTestSubInterface.doCalculateSomething",
                                   "doTestSubInterface.doTestSubInterface.doCalculateSomething" ),
                    capturedCanonicalMethodNameListWithMergedHierarchyCalls );
    }
    
  }
  
  @Test
  public void testGetCapturedCanonicalPropertyNameListWithMergedHierarchyCalls()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestClass testClass = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestClass.class );
    
    //    
    testClass.getFieldString();
    testClass.getFieldDouble();
    testClass.setFieldDouble( 1.67 );
    testClass.setFieldString( "test value" );
    testClass.getTestClass().setFieldString( "other value" );
    
    //    
    List<String> capturedCanonicalPropertyNameListWithMergedHierarchyCalls = methodCallCapturer.getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls( testClass );
    assertEquals( Arrays.asList( "fieldString", "fieldDouble", "fieldDouble", "fieldString", "testClass.fieldString" ),
                  capturedCanonicalPropertyNameListWithMergedHierarchyCalls );
  }
  
  @Test
  public void testGetMethodCallCaptureContextWithMergedHierarchyList()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestClass testClass = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestClass.class );
    
    //    
    testClass.getFieldString();
    testClass.getTestClass().setFieldString( "other value" );
    
    //    
    List<MethodCallCaptureContext> methodCallCaptureContextWithMergedHierarchyList = methodCallCapturer.getMethodCallCaptureContextWithMergedHierarchyList( testClass );
    assertNotNull( methodCallCaptureContextWithMergedHierarchyList );
    assertEquals( 2, methodCallCaptureContextWithMergedHierarchyList.size() );
    assertEquals( "getFieldString", methodCallCaptureContextWithMergedHierarchyList.get( 0 )
                                                                                   .getMethodCallCapture()
                                                                                   .getMethodName() );
    assertEquals( "setFieldString", methodCallCaptureContextWithMergedHierarchyList.get( 1 )
                                                                                   .getMethodCallCapture()
                                                                                   .getMethodName() );
    
  }
  
  @Test
  public void testMethodNameOf()
  {
    //
    MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
    TestClass testClass = methodCallCapturer.newInstanceOfTransitivlyCapturedType( TestClass.class );
    
    //
    String methodName = methodCallCapturer.methodName.of( testClass.getTestClass().getFieldString() );
    assertEquals( "getTestClass.getFieldString", methodName );
    
    //
    String propertyName = methodCallCapturer.beanProperty.name.of( testClass.getTestClass().getFieldString() );
    assertEquals( "testClass.fieldString", propertyName );
    
    //
    BeanPropertyAccessor<Object> beanPropertyAccessor = methodCallCapturer.beanProperty.accessor.of( testClass.getTestClass()
                                                                                                              .getFieldString() );
    assertNotNull( beanPropertyAccessor );
    assertEquals( "fieldString", beanPropertyAccessor.getPropertyName() );
    
  }
  
}
