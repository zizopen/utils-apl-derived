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
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.proxy.MethodCallCapturer.MethodCallCaptureContext;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;
import org.omnaest.utils.structure.element.ThreadLocalCachedElement;

/**
 * A {@link BeanProperty} allows to capture method calls for getter and setter methods on a Java Bean. The captured method calls
 * can then be transformed to {@link BeanPropertyAccessor} instances or the property names.
 * 
 * @see #newInstanceOfCapturedType(Class)
 * @see #newInstanceOfTransitivlyCapturedType(Class)
 * @see #accessor
 * @see #name
 * @see MethodName
 * @see MethodCallCapturer
 */
public class BeanProperty
{
  /* ********************************************** Constants ********************************************** */
  public final Accessor                                         accessor                               = new Accessor();
  public final Name                                             name                                   = new Name();
  
  protected final static ThreadLocalCachedElement<BeanProperty> threadLocalCachedElementOfBeanProperty = new ThreadLocalCachedElement<BeanProperty>(
                                                                                                                                                     new ValueResolver<BeanProperty>()
                                                                                                                                                     {
                                                                                                                                                       
                                                                                                                                                       @Override
                                                                                                                                                       public BeanProperty resolveValue()
                                                                                                                                                       {
                                                                                                                                                         return new BeanProperty();
                                                                                                                                                       }
                                                                                                                                                     } );
  
  /* ********************************************** Variables ********************************************** */
  protected MethodCallCapturer                                  methodCallCapturer                     = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * {@link BeanPropertyAccessor}resolver for a Java {@link BeanProperty}
   * 
   * @see #of(Object)
   * @see #of(Object...)
   */
  public class Accessor
  {
    
    /**
     * Returns the {@link BeanPropertyAccessor} related to the last method call done from the stub created by the
     * {@link BeanProperty#newInstanceOfCapturedType(Class)} method.<br>
     * <br>
     * This should be used like <br>
     * <br>
     * 
     * <pre>
     * {
     *   TestInterface testInterface = this.beanProperty.newInstanceOfTransitivlyCapturedType( TestInterface.class );
     *   String propertyName = this.beanProperty.accessor.of( testInterface.getSomething() );
     * }
     * </pre>
     * 
     * <br>
     * <br>
     * where the stub is a previously created stub by this {@link BeanProperty} instance.
     * 
     * @see #of(Object...)
     * @see BeanProperty#newInstanceOfCapturedType(Class)
     * @param methodCall
     * @return
     */
    public <B> BeanPropertyAccessor<B> of( Object methodCall )
    {
      return this.<B> of( new Object[1] ).iterator().next();
    }
    
    /**
     * <pre>
     * {
     *   TestClass testClass = this.beanProperty.newInstanceOfTransitivlyCapturedType( TestClass.class );
     *   
     *   BeanPropertyAccessors&lt;TestClass&gt; beanPropertyAccessors = this.beanProperty.accessor.of( testClass.getFieldString(),
     *                                                                                           testClass.getTestClass()
     *                                                                                                    .getFieldDouble() );
     * }
     * </pre>
     * 
     * @see #of(Object)
     * @see BeanProperty#newInstanceOfCapturedType(Class)
     * @param methodCalls
     * @return
     */
    @SuppressWarnings("unchecked")
    public <B> BeanPropertyAccessors<B> of( Object... methodCalls )
    {
      //
      List<BeanPropertyAccessor<B>> retlist = new ArrayList<BeanPropertyAccessor<B>>();
      
      //
      int methodCallsLength = methodCalls.length;
      if ( methodCalls.length > 0 )
      {
        //
        List<MethodCallCaptureContext> methodCallCaptureContextWithMergedHierarchyList = BeanProperty.this.methodCallCapturer.getMethodCallCaptureContextWithMergedHierarchyList();
        
        //
        int canonicalPropertyNameListSize = methodCallCaptureContextWithMergedHierarchyList.size();
        int indexLimitUpper = canonicalPropertyNameListSize - 1;
        int indexLimitLower = canonicalPropertyNameListSize - methodCallsLength;
        for ( int ii = indexLimitLower; ii <= indexLimitUpper; ii++ )
        {
          //
          MethodCallCaptureContext methodCallCaptureContext = methodCallCaptureContextWithMergedHierarchyList.get( ii );
          if ( methodCallCaptureContext != null )
          {
            //
            MethodCallCapture methodCallCapture = methodCallCaptureContext.getMethodCallCapture();
            if ( methodCallCapture != null )
            {
              //
              Object object = methodCallCapture.getObject();
              if ( object != null )
              {
                //
                Class<? extends B> beanClass = (Class<? extends B>) object.getClass();
                
                //
                Method method = methodCallCapture.getMethod();
                
                //
                BeanPropertyAccessor<B> beanPropertyAccessor = (BeanPropertyAccessor<B>) BeanUtils.beanPropertyAccessor( beanClass,
                                                                                                                         method );
                
                //
                retlist.add( beanPropertyAccessor );
              }
            }
          }
        }
      }
      
      //
      return new BeanPropertyAccessors<B>( retlist );
    }
  }
  
  /**
   * Property {@link Name} resolver for a Java {@link BeanProperty}
   * 
   * @see #of(Object)
   * @see #of(Object...)
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
     *   TestInterface testInterface = this.property.newInstanceOfTransitivlyCapturedType( TestInterface.class );
     *   String propertyName = this.property.name.of( testInterface.getSomething() );
     * }
     * </pre>
     * 
     * <br>
     * <br>
     * where the stub is a previously created stub by this {@link BeanProperty} instance.
     * 
     * @see #of(Object...)
     * @see BeanProperty#newInstanceOfCapturedType(Class)
     * @param methodCall
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
     * @see BeanProperty#newInstanceOfCapturedType(Class)
     * @param methodCalls
     * @return
     */
    public String[] of( Object... methodCalls )
    {
      //
      List<String> retlist = new ArrayList<String>();
      
      //
      int methodCallsLength = methodCalls.length;
      if ( methodCalls.length > 0 )
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
   * @see BeanProperty
   * @param methodCallCapturer
   */
  protected BeanProperty( MethodCallCapturer methodCallCapturer )
  {
    super();
    this.methodCallCapturer = methodCallCapturer;
  }
  
  /**
   * @see BeanProperty
   */
  public BeanProperty()
  {
    super();
    this.methodCallCapturer = new MethodCallCapturer();
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
  
  /**
   * Returns a {@link ThreadLocal} specific instance which will be created at the first time of use with a given {@link Thread}.
   * 
   * @return {@link BeanProperty}
   */
  public static BeanProperty beanProperty()
  {
    return BeanProperty.threadLocalCachedElementOfBeanProperty.getValue();
  }
}
