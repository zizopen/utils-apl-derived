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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCaptureContext;

/**
 * @see MethodCallCapturer
 */
public class BeanProperty
{
  /* ********************************************** Constants ********************************************** */
  public final Accessor        accessor           = new Accessor();
  public final Name            name               = new Name();
  
  /* ********************************************** Variables ********************************************** */
  protected MethodCallCapturer methodCallCapturer = new MethodCallCapturer();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * {@link Accessor} resolver for a Java {@link BeanProperty}
   */
  public class Accessor
  {
    
    @SuppressWarnings("unchecked")
    public <B> BeanPropertyAccessor<B> of( Object methodCall )
    {
      //
      BeanPropertyAccessor<B> retval = null;
      
      //
      MethodCallCaptureContext lastMethodCallContext = BeanProperty.this.methodCallCapturer.getLastMethodCallContext();
      if ( lastMethodCallContext != null )
      {
        //
        MethodCallCapture methodCallCapture = lastMethodCallContext.getMethodCallCapture();
        if ( methodCallCapture != null )
        {
          //
          Object object = methodCallCapture.getObj();
          if ( object != null )
          {
            //
            Class<? extends B> beanClass = (Class<? extends B>) object.getClass();
            
            //
            Method method = methodCallCapture.getMethod();
            
            //
            retval = (BeanPropertyAccessor<B>) BeanUtils.determineBeanPropertyAccessor( beanClass, method );
          }
        }
        
      }
      
      return retval;
    }
  }
  
  /**
   * {@link Name} resolver for a Java {@link BeanProperty}
   * 
   * @see BeanProperty
   * @author Omnaest
   */
  public class Name
  {
    
    /**
     * Returns the canonical property name related to the last method call done from the stub created by the
     * {@link BeanProperty#newInstanceOfCapturedType(Class)} method.<br>
     * <br>
     * This should be used like <br>
     * <br>
     * 
     * <pre>
     * {
     *   TestInterface testInterface = this.methodName.newInstanceOfTransitivlyCapturedType( TestInterface.class );
     *   String propertyName = this.property.name.of( testInterface.getSomething() );
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
      List<String> canonicalPropertyNameList = BeanProperty.this.methodCallCapturer.getCapturedCanonicalPropertyNameList();
      int canonicalPropertyNameListSize = canonicalPropertyNameList.size();
      return canonicalPropertyNameListSize > 0 ? canonicalPropertyNameList.get( canonicalPropertyNameListSize - 1 ) : null;
    }
    
    /**
     * <pre>
     * {
     *   TestInterface testInterface = this.property.newInstanceOfTransitivlyCapturedType( TestInterface.class );
     *   
     *   String[] propertyNames = this.property.name.of( testInterface.getSomething(), testInterface.getSomethingPrimitive(),
     *                                                   testInterface.getTestSubInterface().getSomething() );
     * }
     * </pre>
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
        List<String> canonicalPropertyNameList = BeanProperty.this.methodCallCapturer.getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls();
        int canonicalPropertyNameListSize = canonicalPropertyNameList.size();
        int indexLimitUpper = canonicalPropertyNameListSize - 1;
        int indexLimitLower = canonicalPropertyNameListSize - methodCallsLength;
        for ( int ii = indexLimitLower; ii <= indexLimitUpper; ii++ )
        {
          retlist.add( canonicalPropertyNameList.get( ii ) );
        }
      }
      
      //
      return retlist.toArray( new String[0] );
    }
  }
  
  /* ********************************************** Variables ********************************************** */

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
