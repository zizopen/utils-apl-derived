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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.proxy.StubCreator.MethodInvocationHandler;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * A {@link MethodCallCapturer} allows to create stubs for given java types which capture the calls of methods of this stub.
 * 
 * @see MethodName
 * @see BeanProperty
 * @see #methodName
 * @see #beanProperty
 * @see #newInstanceOfCapturedType(Class)
 * @see #newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware(Class)
 * @see #newInstanceOfTransitivlyCapturedType(Class)
 * @see #newInstanceOfTransitivlyCapturedTypeWhichIsMethodCallCapturerAware(Class)
 * @see #getMethodCallCaptureContextList()
 * @see #getCanonicalMethodNameToMethodCallCaptureMap()
 * @see MethodCallCapture
 * @author Omnaest
 */
public class MethodCallCapturer
{
  /* ********************************************** Constants ********************************************** */
  public final MethodName                               methodName                            = new MethodName( this );
  public final BeanProperty                             beanProperty                          = new BeanProperty( this );
  
  /* ********************************************** Variables ********************************************** */
  protected Map<Object, List<MethodCallCaptureContext>> stubToMethodCallCaptureContextListMap = Collections.synchronizedMap( new IdentityHashMap<Object, List<MethodCallCaptureContext>>() );
  protected Object                                      lastActiveRootStub                    = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Interface stubs are implementing when they are created by
   * {@link MethodCallCapturer#newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware(Class)}.
   * 
   * @see MethodCallCapturer
   */
  public static interface MethodCallCapturerAware
  {
    /**
     * Returns the underlying {@link MethodCallCapturer}
     * 
     * @return
     */
    public MethodCallCapturer getMethodCallCapturer();
  }
  
  /**
   * {@link MethodInterceptor} for the {@link MethodCallCapturer}.
   * 
   * @author Omnaest
   */
  protected class MethodCaptureMethodInvocationHandler implements MethodInvocationHandler
  {
    /* ********************************************** Variables ********************************************** */
    protected CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public MethodCaptureMethodInvocationHandler( CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration )
    {
      this.capturedTypeInstanceCreationConfiguration = capturedTypeInstanceCreationConfiguration;
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture )
    {
      //
      Object retval = null;
      
      //
      if ( this.capturedTypeInstanceCreationConfiguration.isMethodCallCapturerAware()
           && "getMethodCallCapturer".equals( methodCallCapture.getMethod().getName() ) )
      {
        //
        retval = MethodCallCapturer.this;
      }
      else
      {
        Method method = methodCallCapture.getMethod();
        if ( method != null )
        {
          //
          MethodCallCaptureContext methodCallCaptureContext = new MethodCallCaptureContext(
                                                                                            methodCallCapture,
                                                                                            this.capturedTypeInstanceCreationConfiguration.getPreviousMethodCallCaptureContext() );
          if ( this.capturedTypeInstanceCreationConfiguration.isCreatingTransitiveStubs() )
          {
            Class<?> returnType = method.getReturnType();
            if ( returnType != null && Object.class.isAssignableFrom( returnType ) )
            {
              //
              CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration = new CapturedTypeInstanceCreationConfiguration(
                                                                                                                                                   returnType,
                                                                                                                                                   this.capturedTypeInstanceCreationConfiguration.getInterfaces(),
                                                                                                                                                   this.capturedTypeInstanceCreationConfiguration.isCreatingTransitiveStubs() );
              capturedTypeInstanceCreationConfiguration.setPreviousMethodCallCaptureContext( methodCallCaptureContext );
              
              //
              retval = MethodCallCapturer.this.newInstanceOfCapturedType( capturedTypeInstanceCreationConfiguration );
              
              // 
              methodCallCaptureContext.setReturnedStub( retval );
              
            }
          }
          
          //
          MethodCallCapturer.this.addMethodCallCapture( methodCallCaptureContext );
        }
      }
      
      // 
      return retval;
    }
  }
  
  /**
   * Configuration object when a {@link MethodCallCapturer} creates a new stub instance.
   * 
   * @see MethodCallCapturer#newInstanceOfCapturedType(CapturedTypeInstanceCreationConfiguration)
   * @author Omnaest
   */
  protected static class CapturedTypeInstanceCreationConfiguration
  {
    /* ********************************************** Variables ********************************************** */
    protected Class<?>                 clazz                            = null;
    protected Class<?>[]               interfaces                       = null;
    protected boolean                  isCreatingTransitiveStubs        = false;
    protected MethodCallCaptureContext previousMethodCallCaptureContext = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public CapturedTypeInstanceCreationConfiguration( Class<?> clazz, Class<?>[] interfaces, boolean isCreatingTransitiveStubs )
    {
      super();
      this.clazz = clazz;
      this.interfaces = interfaces;
      this.isCreatingTransitiveStubs = isCreatingTransitiveStubs;
    }
    
    /**
     * @return
     */
    public Class<?> getClazz()
    {
      return this.clazz;
    }
    
    /**
     * @param clazz
     */
    public void setClazz( Class<?> clazz )
    {
      this.clazz = clazz;
    }
    
    /**
     * @return
     */
    public Class<?>[] getInterfaces()
    {
      return this.interfaces;
    }
    
    /**
     * @param interfaces
     */
    public void setInterfaces( Class<?>[] interfaces )
    {
      this.interfaces = interfaces;
    }
    
    /**
     * @return
     */
    public boolean isCreatingTransitiveStubs()
    {
      return this.isCreatingTransitiveStubs;
    }
    
    /**
     * @param isCreatingTransitiveStubs
     */
    public void setCreatingTransitiveStubs( boolean isCreatingTransitiveStubs )
    {
      this.isCreatingTransitiveStubs = isCreatingTransitiveStubs;
    }
    
    /**
     * Returns true, if the stub implements the {@link MethodCallCapturerAware} interface.
     * 
     * @return
     */
    protected boolean isMethodCallCapturerAware()
    {
      return this.interfaces != null && ArrayUtils.contains( this.interfaces, MethodCallCapturerAware.class );
    }
    
    protected MethodCallCaptureContext getPreviousMethodCallCaptureContext()
    {
      return this.previousMethodCallCaptureContext;
    }
    
    protected void setPreviousMethodCallCaptureContext( MethodCallCaptureContext previousMethodCallCaptureContext )
    {
      this.previousMethodCallCaptureContext = previousMethodCallCaptureContext;
    }
    
  }
  
  /**
   * Container class for the absolute canonical method name when a {@link MethodCallCapture} instance has been captured.
   * 
   * @see MethodCallCapturer
   * @author Omnaest
   */
  public static class MethodCallCaptureContext
  {
    /* ********************************************** Variables ********************************************** */
    protected MethodCallCapture        methodCallCapture                = null;
    protected MethodCallCaptureContext previousMethodCallCaptureContext = null;
    protected Object                   returnedStub                     = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public MethodCallCaptureContext( MethodCallCapture methodCallCapture,
                                     MethodCallCaptureContext previousMethodCallCaptureContext )
    {
      super();
      this.methodCallCapture = methodCallCapture;
      this.previousMethodCallCaptureContext = previousMethodCallCaptureContext;
    }
    
    public MethodCallCapture getMethodCallCapture()
    {
      return this.methodCallCapture;
    }
    
    /**
     * Returns the canonical list of {@link MethodCallCapture}s from the given root stub as base.
     * 
     * @param rootStub
     * @return
     */
    public List<MethodCallCapture> determineCanonicalMethodCallCaptures( Object rootStub )
    {
      //
      List<MethodCallCapture> retlist = new ArrayList<MethodCallCapture>();
      
      //
      if ( this.previousMethodCallCaptureContext == null )
      {
        //
        retlist.add( this.methodCallCapture );
      }
      else if ( this.getStub() == rootStub )
      {
      }
      else
      {
        //
        retlist.addAll( this.previousMethodCallCaptureContext.determineCanonicalMethodCallCaptures( rootStub ) );
        retlist.add( this.methodCallCapture );
      }
      
      //
      return retlist;
    }
    
    /**
     * Returns the canonical method name relative to the given root stub object
     * 
     * @return
     */
    public String determineCanonicalMethodName( Object rootStub )
    {
      //
      String retval = null;
      
      //
      List<MethodCallCapture> canonicalMethodCallCaptureList = this.determineCanonicalMethodCallCaptures( rootStub );
      for ( MethodCallCapture methodCallCapture : canonicalMethodCallCaptureList )
      {
        //
        String methodName = methodCallCapture.getMethodName();
        
        //
        retval = retval == null ? methodName : retval + "." + methodName;
      }
      
      //
      return retval;
    }
    
    /**
     * Returns the canonical property name relative to the given root stub object
     * 
     * @return
     */
    public String determineCanonicalPropertyName( Object rootStub )
    {
      //
      String retval = null;
      
      //
      List<MethodCallCapture> canonicalMethodCallCaptureList = this.determineCanonicalMethodCallCaptures( rootStub );
      for ( MethodCallCapture methodCallCapture : canonicalMethodCallCaptureList )
      {
        //
        Method method = methodCallCapture.getMethod();
        if ( method != null )
        {
          //        
          BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
          if ( beanMethodInformation != null )
          {
            //
            String referencedFieldName = beanMethodInformation.getReferencedFieldName();
            
            //
            retval = retval == null ? referencedFieldName : retval + "." + referencedFieldName;
          }
        }
      }
      
      //
      return retval;
    }
    
    /**
     * Returns the stub for which this {@link MethodCallCaptureContext} was created for one of its methods called.
     * 
     * @return
     */
    public Object getStub()
    {
      return this.methodCallCapture != null ? this.methodCallCapture.getObject() : null;
    }
    
    /**
     * Returns the stub objects which have been invoked to produce this {@link MethodCallCaptureContext}.
     * 
     * @return
     */
    public List<Object> getStubList()
    {
      //
      List<Object> retlist = new ArrayList<Object>();
      
      //
      if ( this.previousMethodCallCaptureContext != null )
      {
        retlist.addAll( this.previousMethodCallCaptureContext.getStubList() );
      }
      
      //
      retlist.add( this.getStub() );
      
      //
      return retlist;
    }
    
    /**
     * Gets the stub object returned by the captured method call.
     * 
     * @return
     */
    protected Object getReturnedStub()
    {
      return this.returnedStub;
    }
    
    protected void setReturnedStub( Object returnedStub )
    {
      this.returnedStub = returnedStub;
    }
    
    public MethodCallCaptureContext getPreviousMethodCallCaptureContext()
    {
      return this.previousMethodCallCaptureContext;
    }
    
  }
  
  /**
   * Result of a replay action.
   * 
   * @see MethodCallCapturer#replay(Object)
   * @author Omnaest
   */
  public static class ReplayResult
  {
    /* ********************************************** Variables ********************************************** */
    protected Exception exception          = null;
    protected boolean   isReplaySuccessful = false;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns true if the replay action was successful without an exception been thrown.
     * 
     * @return
     */
    public boolean isReplaySuccessful()
    {
      return this.isReplaySuccessful;
    }
    
    /**
     * Returns the {@link Exception} if one was thrown during the replay.
     * 
     * @return
     */
    public Exception getException()
    {
      return this.exception;
    }
    
    protected void setException( Exception exception )
    {
      this.exception = exception;
    }
    
    protected void setReplaySuccessful( boolean isReplaySuccessful )
    {
      this.isReplaySuccessful = isReplaySuccessful;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new stub instance for the given class or interface which records all method invocations to this
   * {@link MethodCallCapturer}.
   * 
   * @see MethodCallCapturer#getMethodCallCaptureContextList()
   * @see MethodCallCapturer#getCanonicalMethodNameToMethodCallCaptureMap()
   * @see #methodNameOf(Object)
   */
  public <E> E newInstanceOfCapturedType( Class<? extends E> clazz )
  {
    //
    return this.<E> newInstanceOfCapturedType( new CapturedTypeInstanceCreationConfiguration( clazz, null, false ) );
  }
  
  /**
   * Creates a new instance like {@link #newInstanceOfCapturedType(Class)} but makes the created stub implementing the
   * {@link MethodCallCapturerAware} interface which allows to get the underlying {@link MethodCallCapturer} through the generated
   * stub.
   * 
   * @see #newInstanceOfCapturedType(Class)
   * @param <E>
   * @param clazz
   * @return
   */
  public <E> E newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware( Class<? extends E> clazz )
  {
    //
    CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration = new CapturedTypeInstanceCreationConfiguration(
                                                                                                                                         clazz,
                                                                                                                                         new Class[] { MethodCallCapturerAware.class },
                                                                                                                                         false );
    return this.<E> newInstanceOfCapturedType( capturedTypeInstanceCreationConfiguration );
  }
  
  /**
   * Does create stubs whose objects returned by method invocations are stubs themselves. This allows to capture canonical method
   * names.
   * 
   * @see #newInstanceOfCapturedType(Class)
   * @see #methodNameOf(Object)
   * @param <E>
   * @param clazz
   * @return
   */
  public <E> E newInstanceOfTransitivlyCapturedType( Class<? extends E> clazz )
  {
    return this.<E> newInstanceOfCapturedType( new CapturedTypeInstanceCreationConfiguration( clazz, null, true ) );
  }
  
  /**
   * Creates a stub which implements the {@link MethodCallCapturerAware} interface and which generates method returned objects
   * which are stubs, too.
   * 
   * @see #newInstanceOfCapturedType(Class)
   * @see #newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware(Class)
   * @see #newInstanceOfTransitivlyCapturedType(Class)
   * @param <E>
   * @param clazz
   * @return
   */
  public <E> E newInstanceOfTransitivlyCapturedTypeWhichIsMethodCallCapturerAware( Class<? extends E> clazz )
  {
    //
    CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration = new CapturedTypeInstanceCreationConfiguration(
                                                                                                                                         clazz,
                                                                                                                                         new Class[] { MethodCallCapturerAware.class },
                                                                                                                                         true );
    return this.<E> newInstanceOfCapturedType( capturedTypeInstanceCreationConfiguration );
  }
  
  /**
   * @see #newInstanceOfCapturedType(Class)
   * @see #newInstanceOfCapturedTypeWhichIsMethodCallCapturerAware(Class)
   * @see #newInstanceOfTransitivlyCapturedType(Class)
   * @see #newInstanceOfTransitivlyCapturedTypeWhichIsMethodCallCapturerAware(Class)
   * @param <E>
   * @param capturedTypeInstanceCreationConfiguration
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <E> E newInstanceOfCapturedType( CapturedTypeInstanceCreationConfiguration capturedTypeInstanceCreationConfiguration )
  {
    //
    MethodCaptureMethodInvocationHandler methodCaptureMethodInvocationHandler = new MethodCaptureMethodInvocationHandler(
                                                                                                                          capturedTypeInstanceCreationConfiguration );
    E stubInstance = StubCreator.<E> newStubInstance( (Class<? extends E>) capturedTypeInstanceCreationConfiguration.getClazz(),
                                                      capturedTypeInstanceCreationConfiguration.getInterfaces(),
                                                      methodCaptureMethodInvocationHandler );
    
    //
    if ( capturedTypeInstanceCreationConfiguration.getPreviousMethodCallCaptureContext() == null )
    {
      this.lastActiveRootStub = stubInstance;
    }
    
    //
    return stubInstance;
  }
  
  /**
   * Gets an available list for the given proxy object or creates a new one.
   * 
   * @param stub
   * @return
   */
  protected List<MethodCallCaptureContext> getOrCreateMethodCallCaptureContextListForStub( Object stub )
  {
    //
    List<MethodCallCaptureContext> retlist = null;
    
    //
    if ( !this.stubToMethodCallCaptureContextListMap.containsKey( stub ) )
    {
      this.stubToMethodCallCaptureContextListMap.put( stub,
                                                      Collections.synchronizedList( new ArrayList<MethodCallCaptureContext>() ) );
    }
    
    //
    retlist = this.stubToMethodCallCaptureContextListMap.get( stub );
    
    //
    return retlist;
  }
  
  /**
   * Returns a new list instance of all {@link MethodCallCaptureContext} instances.
   * 
   * @return
   */
  public List<MethodCallCaptureContext> getMethodCallCaptureContextList()
  {
    return ListUtils.mergeAll( this.stubToMethodCallCaptureContextListMap.values() );
  }
  
  /**
   * Returns a new list instance of all {@link MethodCallCaptureContext} instances for a given stub instance.
   * 
   * @param stub
   * @return
   */
  public List<MethodCallCaptureContext> getMethodCallCaptureContextList( Object stub )
  {
    return new ArrayList<MethodCallCaptureContext>( this.getOrCreateMethodCallCaptureContextListForStub( stub ) );
  }
  
  /**
   * Returns a new list instance of all {@link MethodCallCaptureContext} instances for the last active stub instance. All
   * {@link MethodCallCaptureContext} instances coming before an {@link MethodCallCaptureContext} which is based on a nested call
   * within the previous context will be merged into the nested call {@link MethodCallCaptureContext}.
   * 
   * @return
   */
  public List<MethodCallCaptureContext> getMethodCallCaptureContextWithMergedHierarchyList()
  {
    return this.getMethodCallCaptureContextWithMergedHierarchyList( this.lastActiveRootStub );
  }
  
  /**
   * Returns a new list instance of all {@link MethodCallCaptureContext} instances for a given stub instance. All
   * {@link MethodCallCaptureContext} instances coming before an {@link MethodCallCaptureContext} which is based on a nested call
   * within the previous context will be merged into the nested call {@link MethodCallCaptureContext}.
   * 
   * @param stub
   * @return
   */
  public List<MethodCallCaptureContext> getMethodCallCaptureContextWithMergedHierarchyList( Object stub )
  {
    //
    List<MethodCallCaptureContext> retlist = this.getMethodCallCaptureContextList( stub );
    
    //
    this.mergeHierarchicalMethodCallCaptureContextList( retlist );
    
    //
    return retlist;
  }
  
  /**
   * Returns the last {@link MethodCallCaptureContext} which has been reported to this {@link MethodCallCapturer}.
   * 
   * @return
   */
  public MethodCallCaptureContext getLastMethodCallContext()
  {
    //
    List<MethodCallCaptureContext> methodCallCaptureContextList = this.getMethodCallCaptureContextList();
    int methodCallCaptureListSize = methodCallCaptureContextList.size();
    return methodCallCaptureListSize > 0 ? methodCallCaptureContextList.get( methodCallCaptureListSize - 1 ) : null;
  }
  
  /**
   * Resets the {@link MethodCallCapturer}.
   * 
   * @return this
   */
  public MethodCallCapturer reset()
  {
    //
    this.stubToMethodCallCaptureContextListMap.clear();
    
    //
    return this;
  }
  
  /**
   * Clears the captured method calls for a special stub object.
   * 
   * @param stub
   * @return this
   */
  public MethodCallCapturer reset( Object stub )
  {
    //
    this.getOrCreateMethodCallCaptureContextListForStub( stub ).clear();
    
    //
    return this;
  }
  
  /**
   * Returns a new list of all captured canonical property names for the last active stub object in order of their invocation.
   * (First invocation is the first entry)
   * 
   * @see #getCapturedCanonicalMethodNameList()
   * @return
   */
  public List<String> getCapturedCanonicalPropertyNameList()
  {
    return this.getCapturedCanonicalPropertyNameList( this.lastActiveRootStub );
  }
  
  /**
   * Returns a new list of all captured canonical property names for a given stub object in order of their invocation. (First
   * invocation is the first entry)
   * 
   * @see #getCapturedCanonicalMethodNameList()
   * @param stub
   * @return
   */
  public List<String> getCapturedCanonicalPropertyNameList( final Object stub )
  {
    //
    List<String> canonicalPropertyNameList = null;
    
    //    
    ElementConverter<MethodCallCaptureContext, String> elementTransformer = new ElementConverter<MethodCallCapturer.MethodCallCaptureContext, String>()
    {
      @Override
      public String convert( MethodCallCaptureContext methodCallCaptureContext )
      {
        return methodCallCaptureContext.determineCanonicalPropertyName( stub );
      }
    };
    canonicalPropertyNameList = ListUtils.transform( this.getOrCreateMethodCallCaptureContextListForStub( stub ),
                                                     elementTransformer );
    
    //
    return canonicalPropertyNameList;
  }
  
  /**
   * Returns a new list of all captured canonical method names for a given stub object in order of their invocation. (First
   * invocation is the first entry)
   * 
   * @see #getCapturedCanonicalMethodNameList()
   * @param stub
   * @return
   */
  public List<String> getCapturedCanonicalMethodNameList( final Object stub )
  {
    //
    List<String> canonicalMethodNameList = null;
    
    //    
    ElementConverter<MethodCallCaptureContext, String> elementTransformer = new ElementConverter<MethodCallCapturer.MethodCallCaptureContext, String>()
    {
      @Override
      public String convert( MethodCallCaptureContext methodCallCaptureContext )
      {
        return methodCallCaptureContext.determineCanonicalMethodName( stub );
      }
    };
    canonicalMethodNameList = ListUtils.transform( this.getOrCreateMethodCallCaptureContextListForStub( stub ),
                                                   elementTransformer );
    
    //
    return canonicalMethodNameList;
  }
  
  /**
   * Returns a new list of all captured canonical method names for the last active stub object in order of their invocation.
   * (First invocation is the first entry)
   * 
   * @see #getCapturedCanonicalMethodNameListWithMergedHierarchyCalls(Object)
   * @return
   */
  public List<String> getCapturedCanonicalMethodNameList()
  {
    return this.getCapturedCanonicalMethodNameList( this.lastActiveRootStub );
  }
  
  /**
   * Returns a {@link List} of the canonical method names captured by the last active stub.
   * 
   * @see #getCapturedCanonicalMethodNameListWithMergedHierarchyCalls(Object)
   * @return
   */
  public List<String> getCapturedCanonicalMethodNameListWithMergedHierarchyCalls()
  {
    return this.getCapturedCanonicalMethodNameListWithMergedHierarchyCalls( this.lastActiveRootStub );
  }
  
  /**
   * Returns a {@link List} of the canonical property names captured by the last active stub.
   * 
   * @see #getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls(Object)
   * @return
   */
  public List<String> getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls()
  {
    return this.getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls( this.lastActiveRootStub );
  }
  
  /**
   * Returns a {@link List} of the canonical method names captured by the given stub but with all hierarchical calls like
   * <code>testInterface.doTestSubInterface().doCalculateSomething()</code> which result in
   * <ul>
   * <li>testInterface</li>
   * <li>testInterface.fieldObject</li>
   * <li>testInterface.fieldObject.fieldString</li>
   * </ul>
   * merged into a single representation like:
   * <ul>
   * <li>testInterface.fieldObject.fieldString</li>
   * </ul>
   * 
   * @see #getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls()
   * @see #getCapturedCanonicalMethodNameListWithMergedHierarchyCalls(Object)
   * @param stub
   * @return
   */
  public List<String> getCapturedCanonicalPropertyNameListWithMergedHierarchyCalls( Object stub )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    List<String> capturedCanonicalPropertyNameList = this.getCapturedCanonicalPropertyNameList( stub );
    if ( capturedCanonicalPropertyNameList != null )
    {
      //
      this.mergeHierarchicalNameList( capturedCanonicalPropertyNameList );
      
      //
      retlist.addAll( capturedCanonicalPropertyNameList );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a {@link List} of the canonical method names captured by the given stub but with all hierarchical calls like
   * <code>testInterface.doTestSubInterface().doCalculateSomething()</code> which result in
   * <ul>
   * <li>testInterface</li>
   * <li>testInterface.doTestSubInterface</li>
   * <li>testInterface.doTestSubInterface.doCalculateSomething</li>
   * </ul>
   * merged into a single representation like:
   * <ul>
   * <li>testInterface.doTestSubInterface.doCalculateSomething</li>
   * </ul>
   * 
   * @see #getCapturedCanonicalMethodNameList()
   * @see #getCapturedCanonicalMethodNameListWithMergedHierarchyCalls()
   * @param stub
   * @return
   */
  public List<String> getCapturedCanonicalMethodNameListWithMergedHierarchyCalls( Object stub )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    List<String> capturedCanonicalMethodNameList = this.getCapturedCanonicalMethodNameList( stub );
    if ( capturedCanonicalMethodNameList != null )
    {
      //
      this.mergeHierarchicalNameList( capturedCanonicalMethodNameList );
      
      //
      retlist.addAll( capturedCanonicalMethodNameList );
    }
    
    //
    return retlist;
  }
  
  /**
   * Merges a list of hierarchical names. E.g. <code>testInterface.doTestSubInterface().doCalculateSomething()</code> which result
   * in
   * <ul>
   * <li>testInterface</li>
   * <li>testInterface.doTestSubInterface</li>
   * <li>testInterface.doTestSubInterface.doCalculateSomething</li>
   * </ul>
   * merged into a single representation like:
   * <ul>
   * <li>testInterface.doTestSubInterface.doCalculateSomething</li>
   * </ul>
   * 
   * @param hierarchicalNameList
   */
  protected void mergeHierarchicalNameList( List<String> hierarchicalNameList )
  {
    
    //
    List<String> hierarchicalNameListReversed = new ArrayList<String>( hierarchicalNameList );
    Collections.reverse( hierarchicalNameListReversed );
    
    //
    hierarchicalNameList.clear();
    
    //
    String methodNameLast = null;
    for ( String methodName : hierarchicalNameListReversed )
    {
      //
      if ( methodNameLast == null || !StringUtils.startsWith( methodNameLast, methodName + "." ) )
      {
        hierarchicalNameList.add( methodName );
      }
      
      //
      methodNameLast = methodName;
    }
    
    //
    Collections.reverse( hierarchicalNameList );
  }
  
  /**
   * Merges hierarchies of {@link MethodCallCaptureContext}s into the most nested {@link MethodCallCaptureContext} for each group
   * of {@link MethodCallCaptureContext}s
   * 
   * @param hierarchicalMethodCallCaptureContextList
   */
  protected void mergeHierarchicalMethodCallCaptureContextList( List<MethodCallCaptureContext> hierarchicalMethodCallCaptureContextList )
  {
    
    //
    List<MethodCallCaptureContext> hierarchicalMethodCallCaptureContextReversedList = new ArrayList<MethodCallCaptureContext>(
                                                                                                                               hierarchicalMethodCallCaptureContextList );
    Collections.reverse( hierarchicalMethodCallCaptureContextReversedList );
    
    //
    hierarchicalMethodCallCaptureContextList.clear();
    
    //
    MethodCallCaptureContext methodCallCaptureContextLast = null;
    for ( MethodCallCaptureContext methodCallCaptureContext : hierarchicalMethodCallCaptureContextReversedList )
    {
      //
      MethodCallCaptureContext previousMethodCallCaptureContextFromLast = methodCallCaptureContextLast != null ? methodCallCaptureContextLast.getPreviousMethodCallCaptureContext()
                                                                                                              : null;
      
      //
      if ( methodCallCaptureContextLast == null || !methodCallCaptureContext.equals( previousMethodCallCaptureContextFromLast ) )
      {
        hierarchicalMethodCallCaptureContextList.add( methodCallCaptureContext );
      }
      
      //
      methodCallCaptureContextLast = methodCallCaptureContext;
    }
    
    //
    Collections.reverse( hierarchicalMethodCallCaptureContextList );
  }
  
  /**
   * Replays the method invocations done to the last active stub for the given object like the object would have been invoked in
   * the first place.
   * 
   * @see #replay(Object, Object)
   * @param object
   */
  public <E> ReplayResult replay( E object )
  {
    return this.replay( this.lastActiveRootStub, object );
  }
  
  /**
   * Replays the method invocations done to the given stub for the given object like the object would have been invoked in the
   * first place.
   * 
   * @see #replay(Object)
   * @param stub
   * @param object
   * @return {@link ReplayResult}
   */
  public <E> ReplayResult replay( E stub, E object )
  {
    //
    ReplayResult result = new ReplayResult();
    
    //
    if ( stub != null && object != null )
    {
      //
      Map<Object, Object> stubToObjectMap = new IdentityHashMap<Object, Object>();
      stubToObjectMap.put( stub, object );
      
      //
      try
      {
        List<MethodCallCaptureContext> methodCallCaptureContextList = this.getMethodCallCaptureContextList( stub );
        for ( MethodCallCaptureContext methodCallCaptureContext : methodCallCaptureContextList )
        {
          if ( methodCallCaptureContext != null )
          {
            //
            MethodCallCapture methodCallCapture = methodCallCaptureContext.getMethodCallCapture();
            if ( methodCallCapture != null )
            {
              //
              Object invocationStub = methodCallCapture.getObject();
              Object invocationObject = stubToObjectMap.get( invocationStub );
              
              //
              if ( invocationObject != null )
              {
                Method method = methodCallCapture.getMethod();
                Object[] args = methodCallCapture.getArguments();
                if ( method != null )
                {
                  //
                  Object methodInvocationResult = method.invoke( invocationObject, args );
                  
                  //
                  Object returnedStub = methodCallCaptureContext.getReturnedStub();
                  if ( returnedStub != null && methodInvocationResult != null )
                  {
                    stubToObjectMap.put( returnedStub, methodInvocationResult );
                  }
                }
              }
            }
          }
        }
        
        //
        result.setReplaySuccessful( true );
      }
      catch ( Exception exception )
      {
        result.setException( exception );
      }
    }
    
    //
    return result;
  }
  
  /**
   * Adds a {@link MethodCallCapture} instance to the report.
   * 
   * @param canonicalMethodName
   * @param methodCallCapture
   * @param rootStub
   * @param returnedStub
   */
  private void addMethodCallCapture( MethodCallCaptureContext methodCallCaptureContext )
  {
    //
    List<Object> stubList = methodCallCaptureContext.getStubList();
    for ( Object stub : stubList )
    {
      this.getOrCreateMethodCallCaptureContextListForStub( stub ).add( methodCallCaptureContext );
    }
    
    //
    this.lastActiveRootStub = stubList.get( 0 );
  }
}
