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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.omnaest.utils.proxy.MethodCallCapturerTest.TestInterface;

/**
 * @see MethodName
 * @author Omnaest
 */
@RunWith(MockitoJUnitRunner.class)
public class MethodNameTest
{
  /* ********************************************** Variables ********************************************** */
  private MethodName methodName = new MethodName();
  
  /* ********************************************** Methods ********************************************** */

  @Before
  public void createMethodName() throws Exception
  {
    this.methodName = new MethodName();
  }
  
  @Test
  public void testOfObject()
  {
    //
    TestInterface testInterface = this.methodName.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    String methodName;
    {
      //
      methodName = this.methodName.of( testInterface.doSomething( "text value" ) );
      assertEquals( "doSomething", methodName );
      
      //
      methodName = this.methodName.of( testInterface.doSomethingPrimitive( "more text" ) );
      assertEquals( "doSomethingPrimitive", methodName );
      
      //
      methodName = this.methodName.of( testInterface.doTestSubInterface().doCalculateSomething() );
      assertEquals( "doTestSubInterface.doCalculateSomething", methodName );
      
      //
      methodName = this.methodName.of( testInterface.doTestSubInterface().doTestSubInterface().doCalculateSomething() );
      assertEquals( "doTestSubInterface.doTestSubInterface.doCalculateSomething", methodName );
    }
  }
  
  @Test
  public void testOfObjectArray()
  {
    //
    TestInterface testInterface = this.methodName.newInstanceOfTransitivlyCapturedType( TestInterface.class );
    
    //
    String[] methodNames = this.methodName.of( testInterface.doSomething( "text value" ),
                                               testInterface.doSomethingPrimitive( "primitive text" ),
                                               testInterface.doTestSubInterface().doCalculateSomething() );
    Assert.assertArrayEquals( new String[] { "doSomething", "doSomethingPrimitive", "doTestSubInterface.doCalculateSomething" },
                              methodNames );
  }
  
}
