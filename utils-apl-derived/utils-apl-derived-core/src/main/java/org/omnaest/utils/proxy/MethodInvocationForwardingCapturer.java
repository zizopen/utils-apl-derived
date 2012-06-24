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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.omnaest.utils.operation.foreach.Range;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.ElementStreamToIteratorAdapter;
import org.omnaest.utils.structure.iterator.RangedIterable;
import org.omnaest.utils.structure.map.MapUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The {@link MethodInvocationForwardingCapturer} allows to create proxies which are put on top of existing object instances. The
 * created proxy will then capture all method invocations and write them as xml format to a given {@link OutputStream}. The method
 * invocation will also be forwared to the original object method. <br>
 * <br>
 * To capture
 * 
 * @see #newProxyInstanceCapturing(Object, OutputStream)
 * @author Omnaest
 */
public class MethodInvocationForwardingCapturer
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @XStreamAlias("methodInvocation")
  public static class MethodInvocationAndResult
  {
    private Method   method    = null;
    private Object[] arguments = null;
    private Object   result    = null;
    
    public MethodInvocationAndResult( Method method, Object[] arguments, Object result )
    {
      super();
      this.method = method;
      this.arguments = arguments;
      this.result = result;
    }
    
    public Method getMethod()
    {
      return this.method;
    }
    
    public Object[] getArguments()
    {
      return this.arguments;
    }
    
    public Object getResult()
    {
      return this.result;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "MethodInvocationAndResult [method=" );
      builder.append( this.method );
      builder.append( ", arguments=" );
      builder.append( Arrays.toString( this.arguments ) );
      builder.append( ", result=" );
      builder.append( this.result );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  protected static interface MethodInvocationComparison
  {
    public boolean equals( Object obj );
    
    public int hashCode();
  }
  
  protected static class MethodOnly implements MethodInvocationComparison
  {
    /* ********************************************** Variables ********************************************** */
    private Method method = null;
    
    /* ********************************************** Methods ********************************************** */
    public MethodOnly( Method method )
    {
      super();
      this.method = method;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.method == null ) ? 0 : this.method.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof MethodOnly ) )
      {
        return false;
      }
      MethodOnly other = (MethodOnly) obj;
      if ( this.method == null )
      {
        if ( other.method != null )
        {
          return false;
        }
      }
      else if ( !this.method.equals( other.method ) )
      {
        return false;
      }
      return true;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "MethodOnly [method=" );
      builder.append( this.method );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * Wrapper for {@link Method} and arguments which implements {@link #hashCode()} and {@link #equals(Object)}
   * 
   * @author Omnaest
   */
  protected static class MethodAndArguments implements MethodInvocationComparison
  {
    /* ********************************************** Variables ********************************************** */
    private Method   method    = null;
    private Object[] arguments = null;
    
    /* ********************************************** Methods ********************************************** */
    public MethodAndArguments( Method method, Object[] arguments )
    {
      super();
      this.method = method;
      this.arguments = arguments;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode( this.arguments );
      result = prime * result + ( ( this.method == null ) ? 0 : this.method.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof MethodAndArguments ) )
      {
        return false;
      }
      MethodAndArguments other = (MethodAndArguments) obj;
      if ( !Arrays.equals( this.arguments, other.arguments ) )
      {
        return false;
      }
      if ( this.method == null )
      {
        if ( other.method != null )
        {
          return false;
        }
      }
      else if ( !this.method.equals( other.method ) )
      {
        return false;
      }
      return true;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "\nMethodAndArguments [method=" );
      builder.append( this.method );
      builder.append( ", arguments=" );
      builder.append( Arrays.toString( this.arguments ) );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  protected static class ReplayingMethodInvocationHandler implements MethodInvocationHandler
  {
    /* ********************************************** Variables ********************************************** */
    private Map<MethodInvocationComparison, Object> methodInvocationComparisonToResultMap = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public ReplayingMethodInvocationHandler( Map<MethodInvocationComparison, Object> methodInvocationComparisonToResultMap )
    {
      super();
      this.methodInvocationComparisonToResultMap = methodInvocationComparisonToResultMap;
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      Method method = methodCallCapture.getMethod();
      Object[] arguments = methodCallCapture.getArguments();
      
      //
      MethodAndArguments methodAndArguments = new MethodAndArguments( method, arguments );
      MethodOnly methodOnly = new MethodOnly( method );
      
      //
      if ( this.methodInvocationComparisonToResultMap.containsKey( methodAndArguments ) )
      {
        retval = this.methodInvocationComparisonToResultMap.get( methodAndArguments );
      }
      else if ( this.methodInvocationComparisonToResultMap.containsKey( methodOnly ) )
      {
        retval = this.methodInvocationComparisonToResultMap.get( methodOnly );
      }
      
      //
      return retval;
    }
    
  }
  
  protected static class ForwardingMethodInvocationHandler implements MethodInvocationHandler
  {
    /* ********************************************** Variables ********************************************** */
    private Object             object             = null;
    private XStream            xStream            = new XStream();
    private ObjectOutputStream objectOutputStream = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public ForwardingMethodInvocationHandler( Object object, OutputStream outputStream )
    {
      //
      super();
      
      //
      this.object = object;
      
      //
      try
      {
        this.objectOutputStream = this.xStream.createObjectOutputStream( outputStream );
      }
      catch ( IOException e )
      {
        e.printStackTrace();
      }
      
      //
      this.xStream.processAnnotations( MethodInvocationAndResult.class );
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      Object[] arguments = methodCallCapture.getArguments();
      retval = methodCallCapture.getProxy().invoke( this.object, arguments );
      
      //
      try
      {
        //
        Method method = methodCallCapture.getMethod();
        MethodInvocationAndResult methodInvocationAndResult = new MethodInvocationAndResult( method, arguments, retval );
        
        //
        
        //this.xStream.toXML( methodInvocationAndResult, this.outputStream );
        
        this.objectOutputStream.writeObject( methodInvocationAndResult );
        
        //
        this.objectOutputStream.flush();
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
      
      //
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns a new proxy instance which will forward all method invocations to the given object but also captures the invocations
   * to put them into the {@link OutputStream} as xml.
   * 
   * @param object
   * @param outputStream
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T newProxyInstanceCapturing( T object, OutputStream outputStream )
  {
    //
    T retval = null;
    
    //
    if ( object != null )
    {
      //
      MethodInvocationHandler methodInvocationHandler = new ForwardingMethodInvocationHandler( object, outputStream );
      
      //
      Class<? extends Object> type = object.getClass();
      Class<?>[] interfaces = type.getInterfaces();
      
      //
      retval = (T) StubCreator.newStubInstance( type, interfaces, methodInvocationHandler );
    }
    
    //
    return retval;
  }
  
  /**
   * Closes a given {@link OutputStream} which is used by a created proxy.
   * 
   * @param outputStream
   */
  public static void closeCapturingOutputStream( OutputStream outputStream )
  {
    if ( outputStream != null )
    {
      try
      {
        //
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream );
        outputStreamWriter.append( "</object-stream>" );
        outputStreamWriter.close();
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Returns a new proxy instance which replays a previously recorded behavior when a method of it is invoked. <br>
   * <br>
   * If argument values are ignored a recored method behavior is triggered even if the invoked method is invoked with different
   * argument values as before. Otherwise the method replay will only occur for the same argument values given during invocation. <br>
   * <br>
   * To record a behavior see the sibling proxy factory method {@link #newProxyInstanceCapturing(Object, OutputStream)}
   * 
   * @param type
   * @param inputStream
   * @param ignoreArgumentValues
   */
  public static <T> T newProxyInstanceReplaying( Class<T> type, InputStream inputStream, final boolean ignoreArgumentValues )
  {
    Range range = null;
    return newProxyInstanceReplaying( type, inputStream, ignoreArgumentValues, range );
  }
  
  /**
   * Returns a new proxy instance which replays a previously recorded behavior when a method of it is invoked. <br>
   * <br>
   * If argument values are ignored a recored method behavior is triggered even if the invoked method is invoked with different
   * argument values as before. Otherwise the method replay will only occur for the same argument values given during invocation. <br>
   * <br>
   * To record a behavior see the sibling proxy factory method {@link #newProxyInstanceCapturing(Object, OutputStream)}
   * 
   * @param type
   * @param inputStream
   * @param ignoreArgumentValues
   * @param range
   */
  @SuppressWarnings("cast")
  public static <T> T newProxyInstanceReplaying( Class<T> type,
                                                 InputStream inputStream,
                                                 final boolean ignoreArgumentValues,
                                                 Range range )
  {
    //
    T retval = null;
    
    //
    if ( type != null )
    {
      //
      final KeyExtractor<MethodInvocationComparison, MethodInvocationAndResult> keyExtractor = new KeyExtractor<MethodInvocationForwardingCapturer.MethodInvocationComparison, MethodInvocationForwardingCapturer.MethodInvocationAndResult>()
      {
        @Override
        public MethodInvocationComparison extractKey( MethodInvocationAndResult methodInvocationAndResult )
        {
          //
          MethodInvocationComparison key = null;
          
          //
          if ( methodInvocationAndResult != null )
          {
            //
            Method method = methodInvocationAndResult.getMethod();
            Object[] arguments = methodInvocationAndResult.getArguments();
            
            //
            key = ignoreArgumentValues ? new MethodOnly( method ) : new MethodAndArguments( method, arguments );
          }
          
          //
          return key;
        }
      };
      final ElementConverter<MethodInvocationAndResult, Object> valueElementConverter = new ElementConverter<MethodInvocationAndResult, Object>()
      {
        @Override
        public Object convert( MethodInvocationAndResult methodInvocationAndResult )
        {
          return methodInvocationAndResult.getResult();
        }
      };
      final Iterable<MethodInvocationAndResult> iterable = newMethodInvocationAndResultIterable( inputStream, range );
      Map<MethodInvocationComparison, Object> methodInvocationComparisonToResultMap = MapUtils.convertMapValue( ListUtils.toMap( keyExtractor,
                                                                                                                                 iterable ),
                                                                                                                valueElementConverter );
      
      //
      MethodInvocationHandler methodInvocationHandler = new ReplayingMethodInvocationHandler(
                                                                                              methodInvocationComparisonToResultMap );
      
      //
      Class<?>[] interfaces = type.getInterfaces();
      
      //
      retval = (T) StubCreator.newStubInstance( type, interfaces, methodInvocationHandler );
    }
    
    //
    return retval;
  }
  
  /**
   * Replays previously recorded method invocations on a real {@link Object} with the same type. <br>
   * <br>
   * Optional it is possible to declare {@link Range}s which will result in only these method invocations being repeated, where
   * the index position is within at least one given {@link Range}. <br>
   * If no {@link Range} parameters are given all method invocations are applied to the given object.
   * 
   * @param inputStream
   * @param object
   * @param ranges
   */
  public static void replayOn( InputStream inputStream, Object object, Range... ranges )
  {
    //
    if ( inputStream != null && object != null )
    {
      //
      final Class<? extends Object> type = object.getClass();
      
      //
      Iterable<MethodInvocationAndResult> methodInvocationAndResultIterable = newMethodInvocationAndResultIterable( inputStream );
      if ( methodInvocationAndResultIterable != null )
      {
        //
        long indexPosition = 0;
        for ( MethodInvocationAndResult methodInvocationAndResult : methodInvocationAndResultIterable )
        {
          //
          boolean isWithinRange = ranges.length == 0;
          if ( !isWithinRange )
          {
            for ( Range range : ranges )
            {
              if ( range.isWithinRange( indexPosition ) )
              {
                isWithinRange = true;
                break;
              }
            }
          }
          
          //
          if ( isWithinRange )
          {
            //
            try
            {
              //
              Method method = methodInvocationAndResult.getMethod();
              Object[] args = methodInvocationAndResult.getArguments();
              
              Method declaredMethod = type.getDeclaredMethod( method.getName(), method.getParameterTypes() );
              
              declaredMethod.invoke( object, args );
            }
            catch ( Exception e )
            {
              e.printStackTrace();
            }
          }
          
          //
          indexPosition++;
        }
      }
    }
  }
  
  protected static XStream newXStream()
  {
    XStream xStream = new XStream();
    xStream.processAnnotations( MethodInvocationAndResult.class );
    return xStream;
  }
  
  /**
   * Creates an {@link ElementStream} of {@link MethodInvocationAndResult} instances from a given {@link InputStream} which was
   * produced by the {@link #newProxyInstanceCapturing(Object, OutputStream)} before.
   * 
   * @param inputStream
   * @return
   */
  public static ElementStream<MethodInvocationAndResult> newMethodInvocationAndResultElementStream( InputStream inputStream )
  {
    //
    ElementStream<MethodInvocationAndResult> retval = null;
    
    //
    final XStream xStream = newXStream();
    try
    {
      //
      final ObjectInputStream objectInputStream = xStream.createObjectInputStream( inputStream );
      
      //
      retval = new ElementStream<MethodInvocationAndResult>()
      {
        @Override
        public MethodInvocationAndResult next()
        {
          //
          MethodInvocationAndResult retval = null;
          
          //
          try
          {
            retval = (MethodInvocationAndResult) objectInputStream.readObject();
          }
          catch ( Exception e )
          {
          }
          
          //
          return retval;
        }
        
      };
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    
    //
    return retval;
  }
  
  /**
   * @see Iterator
   * @see #newMethodInvocationAndResultElementStream(InputStream)
   * @param inputStream
   * @return
   */
  protected static Iterator<MethodInvocationAndResult> newMethodInvocationAndResultIterator( InputStream inputStream )
  {
    return new ElementStreamToIteratorAdapter<MethodInvocationAndResult>( newMethodInvocationAndResultElementStream( inputStream ) );
  }
  
  /**
   * @see #newMethodInvocationAndResultElementStream(InputStream)
   * @see Iterable
   * @param inputStream
   * @return
   */
  public static Iterable<MethodInvocationAndResult> newMethodInvocationAndResultIterable( final InputStream inputStream )
  {
    return new Iterable<MethodInvocationForwardingCapturer.MethodInvocationAndResult>()
    {
      @Override
      public Iterator<MethodInvocationAndResult> iterator()
      {
        return newMethodInvocationAndResultIterator( inputStream );
      }
    };
  }
  
  /**
   * Returns only the {@link MethodInvocationAndResult} instances within the given {@link Range} of the index positions of the
   * order of invocation.<br>
   * <br>
   * If null is given as {@link Range} the original {@link Iterable} is returned.
   * 
   * @see #newMethodInvocationAndResultElementStream(InputStream)
   * @see Range
   * @see Iterable
   * @param inputStream
   * @param range
   * @return
   */
  public static Iterable<MethodInvocationAndResult> newMethodInvocationAndResultIterable( final InputStream inputStream,
                                                                                          Range range )
  {
    Iterable<MethodInvocationAndResult> iterable = newMethodInvocationAndResultIterable( inputStream );
    return range != null ? new RangedIterable<MethodInvocationAndResult>( range, iterable ) : iterable;
  }
  
  private MethodInvocationForwardingCapturer( OutputStream outputStream )
  {
    super();
  }
}
