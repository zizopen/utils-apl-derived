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

import java.util.ArrayList;
import java.util.List;

/**
 * @see MethodCallCapturer
 */
public class MethodName
{
  /* ********************************************** Variables ********************************************** */
  protected MethodCallCapturer methodCallCapturer = null;
  
  /* ********************************************** Variables ********************************************** */
  /**
   * @see MethodName
   * @param methodCallCapturer
   */
  protected MethodName( MethodCallCapturer methodCallCapturer )
  {
    super();
    this.methodCallCapturer = methodCallCapturer;
  }
  
  /**
   * @see MethodName
   */
  public MethodName()
  {
    super();
    this.methodCallCapturer = new MethodCallCapturer();
  }
  
  /**
   * Returns the canonical method name of the last method call done from the stub created by the
   * {@link MethodName#newInstanceOfCapturedType(Class)} method.<br>
   * <br>
   * This should be used like <br>
   * <br>
   * 
   * <pre>
   * {
   *   TestInterface testInterface = this.methodName.newInstanceOfTransitivlyCapturedType( TestInterface.class );
   *   String methodName = this.methodName.of( testInterface.doSomething( &quot;text value&quot; ) );
   * }
   * </pre>
   * 
   * <br>
   * <br>
   * where the <code>stub</code> is a previously created stub by this {@link MethodCallCapturer} instance.
   * 
   * @see #newInstanceOfCapturedType(Class)
   * @param object
   * @return
   */
  public String of( Object methodCall )
  {
    //
    List<String> canonicalMethodNameList = this.methodCallCapturer.getCapturedCanonicalMethodNameList();
    int canonicalMethodNameListSize = canonicalMethodNameList.size();
    return canonicalMethodNameListSize > 0 ? canonicalMethodNameList.get( canonicalMethodNameListSize - 1 ) : null;
  }
  
  /**
   * <code>
    TestInterface testInterface = this.methodName.newInstanceOfTransitivlyCapturedType( TestInterface.class );<br><br>
    
    String[] methodNames = this.methodName.of( testInterface.doSomething( "text value" ),
                                               testInterface.doSomethingPrimitive( "primitive text" ),
                                               testInterface.doTestSubInterface().doCalculateSomething() );
   * </code>
   * 
   * @see #of(Object)
   * @param methodCalls
   * @return
   */
  public String[] of( Object... methodCalls )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    int methodCallsLength = methodCalls.length;
    if ( methodCalls != null && methodCalls.length > 0 )
    {
      //
      List<String> canonicalMethodNameList = this.methodCallCapturer.getCapturedCanonicalMethodNameListWithMergedHierarchyCalls();
      int canonicalMethodNameListSize = canonicalMethodNameList.size();
      int indexLimitUpper = canonicalMethodNameListSize - 1;
      int indexLimitLower = canonicalMethodNameListSize - methodCallsLength;
      for ( int ii = indexLimitLower; ii <= indexLimitUpper; ii++ )
      {
        retlist.add( canonicalMethodNameList.get( ii ) );
      }
    }
    
    //
    return retlist.toArray( new String[0] );
  }
  
  /**
   * Creates a new stub instance for the given class or interface type for which the method calls will be captured. The capturing
   * is not transitive which means that method calls of nested objects / fields are not captured.
   * 
   * @see #newInstanceOfTransitivlyCapturedType(Class)
   * @see #of(Object)
   * @param <E>
   * @param clazz
   * @return
   */
  public <E> E newInstanceOfCapturedType( Class<? extends E> clazz )
  {
    return this.methodCallCapturer.newInstanceOfCapturedType( clazz );
  }
  
  /**
   * Creates a new stub instance for the given class or interface type for which the method calls will be captured. This stub
   * captures transitive method calls for field objects and further child nodes within the object hierarchy as well.
   * 
   * @see #newInstanceOfCapturedType(Class)
   * @param <E>
   * @param clazz
   * @return
   */
  public <E> E newInstanceOfTransitivlyCapturedType( Class<? extends E> clazz )
  {
    return this.methodCallCapturer.newInstanceOfTransitivlyCapturedType( clazz );
  }
  
}
