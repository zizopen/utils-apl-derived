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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.omnaest.utils.proxy.MethodInvocationForwardingCapturer.MethodInvocationAndResult;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.Range;

/**
 * @see MethodInvocationForwardingCapturer
 * @author Omnaest
 */
public class MethodInvocationForwardingCapturerTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static class TestClass
  {
    private String fieldString = null;
    
    public void arbitraryMethod()
    {
    }
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNewProxyInstanceCapturingReplaying() throws IOException,
                                                      ClassNotFoundException,
                                                      SecurityException,
                                                      NoSuchMethodException,
                                                      IllegalArgumentException,
                                                      IllegalAccessException,
                                                      InvocationTargetException
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    TestClass object = new TestClass();
    OutputStream outputStream = byteArrayContainer.getOutputStream();
    
    TestClass proxyInstance = MethodInvocationForwardingCapturer.newProxyInstanceCapturing( object, outputStream );
    
    //
    final String fieldString = "lalaa";
    
    //
    proxyInstance.arbitraryMethod();
    proxyInstance.setFieldString( fieldString );
    
    assertEquals( fieldString, proxyInstance.getFieldString() );
    
    //
    MethodInvocationForwardingCapturer.closeCapturingOutputStream( outputStream );
    //System.out.println( byteArrayContainer.toString() );
    
    //
    Iterable<MethodInvocationAndResult> methodInvocationAndResultIterable = MethodInvocationForwardingCapturer.newMethodInvocationAndResultIterable( byteArrayContainer.getInputStream(),
                                                                                                                                                     new Range(
                                                                                                                                                                0,
                                                                                                                                                                1 ) );
    
    for ( MethodInvocationAndResult methodInvocationAndResult : methodInvocationAndResultIterable )
    {
      Method method = methodInvocationAndResult.getMethod();
      Object[] args = methodInvocationAndResult.getArguments();
      Object retval = methodInvocationAndResult.getResult();
      
      Method declaredMethod = TestClass.class.getDeclaredMethod( method.getName(), method.getParameterTypes() );
      
      Object result = declaredMethod.invoke( object, args );
      assertEquals( retval, result );
    }
  }
  
  @Test
  public void testReplayOn()
  {
    //
    TestClass testClass = Mockito.mock( TestClass.class );
    
    //
    InputStream preparedCapturingMethodInvocationInputStream = MethodInvocationForwardingCapturerTest.prepareCapturingMethodInvocationInputStream();
    
    //
    MethodInvocationForwardingCapturer.replayOn( preparedCapturingMethodInvocationInputStream, testClass );
    
    //
    Mockito.verify( testClass, new Times( 1 ) ).arbitraryMethod();
    Mockito.verify( testClass, new Times( 1 ) ).setFieldString( "value1" );
    Mockito.verify( testClass, new Times( 1 ) ).setFieldString( "value2" );
    Mockito.verify( testClass, new Times( 2 ) ).getFieldString();
  }
  
  private static InputStream prepareCapturingMethodInvocationInputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    TestClass object = new TestClass();
    OutputStream outputStream = byteArrayContainer.getOutputStream();
    
    TestClass proxyInstance = MethodInvocationForwardingCapturer.newProxyInstanceCapturing( object, outputStream );
    
    //
    proxyInstance.arbitraryMethod();
    proxyInstance.setFieldString( "value1" );
    proxyInstance.getFieldString();
    proxyInstance.setFieldString( "value2" );
    proxyInstance.getFieldString();
    
    //
    MethodInvocationForwardingCapturer.closeCapturingOutputStream( outputStream );
    
    //
    return byteArrayContainer.getInputStream();
  }
  
  @Test
  public void testNewProxyInstanceReplaying()
  {
    //
    {
      //
      InputStream preparedCapturingMethodInvocationInputStream = MethodInvocationForwardingCapturerTest.prepareCapturingMethodInvocationInputStream();
      
      //
      boolean ignoreArgumentValues = false;
      TestClass proxyInstanceReplaying = MethodInvocationForwardingCapturer.newProxyInstanceReplaying( TestClass.class,
                                                                                                       preparedCapturingMethodInvocationInputStream,
                                                                                                       ignoreArgumentValues );
      
      //
      assertEquals( "value2", proxyInstanceReplaying.getFieldString() );
    }
    {
      //
      InputStream preparedCapturingMethodInvocationInputStream = MethodInvocationForwardingCapturerTest.prepareCapturingMethodInvocationInputStream();
      
      //
      boolean ignoreArgumentValues = false;
      TestClass proxyInstanceReplaying = MethodInvocationForwardingCapturer.newProxyInstanceReplaying( TestClass.class,
                                                                                                       preparedCapturingMethodInvocationInputStream,
                                                                                                       ignoreArgumentValues,
                                                                                                       new Range( 0, 2 ) );
      
      //
      assertEquals( "value1", proxyInstanceReplaying.getFieldString() );
    }
  }
}
