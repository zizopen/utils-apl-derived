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
package org.omnaest.utils.assertion;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.omnaest.utils.assertion.AssertLogger.LoglevelImpl.LoglevelSupport;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.time.DurationCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AssertLogger} provides methods of a {@link Logger} as well as methods of an {@link Assert} helper. This allows to
 * easily log assertion based {@link Exception}s and their stack trace information without having them to be raised into the
 * callers logic. The callers logic will only have to deal with an easy boolean return value.<br>
 * <br>
 * <h1>Usage examples</h1>
 * 
 * <pre>
 * AssertLogger assertLogger = new AssertLogger( SampleClass.class );
 * </pre>
 * 
 * Simple logging:
 * 
 * <pre>
 * this.assertLogger.info.message( &quot;Simple message logged with the INFO log level&quot; );
 * 
 * //generate and log a message only if the loglevel of DEBUG is enabled
 * this.assertLogger.debug.message( new MessageFactory()
 * {
 *   &#064;Override
 *   public String message()
 *   {
 *     return &quot;Message which is costly to generate&quot;;
 *   }
 * } );
 * </pre>
 * 
 * Logging catched exceptions:
 * 
 * <pre>
 * try
 * {
 *   throw new Exception( &quot;Some exception&quot; );
 * }
 * catch ( Exception e )
 * {
 *   this.assertLogger.warn.message( e );
 * }
 * </pre>
 * 
 * Assertions:
 * 
 * <pre>
 * assertLogger.debug.assertThat().isTrue( expression );
 * assertLogger.warn.assertThat().isNotNull( object, &quot;Additional message&quot; );
 * assertLogger.info.assertThat().fails( &quot;Additional message&quot; );
 * </pre>
 * 
 * Assertions in combination with control flow:
 * 
 * <pre>
 * final Collection&lt;String&gt; collection = ...
 * if ( this.assertLogger.error.assertThat().isNotEmpty( collection, &quot;This collection must not be empty!&quot; ) )
 * {
 *   // this block is only executed if the collection is not empty 
 * }
 * </pre>
 * 
 * which replaces code like:
 * 
 * <pre>
 * try
 * {
 *   //
 *   Assert.isNotEmpty( collection );
 *   
 *   //...      
 * }
 * catch ( Exception e )
 * {
 *   this.logger.error( &quot;Assertion failed&quot;, e );
 * }
 * </pre>
 * 
 * @see Loglevel
 * @see LoglevelAssert
 * @see Assert
 * @see Logger
 * @author Omnaest
 */
public class AssertLogger implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 830408918854821772L;
  /* ********************************************** Variables ********************************************** */
  public final Logger       logger;
  public final Loglevel     trace;
  public final Loglevel     debug;
  public final Loglevel     info;
  public final Loglevel     warn;
  public final Loglevel     error;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * A {@link MessageFactory} allows to produce messages
   * 
   * @author Omnaest
   */
  public static interface MessageFactory
  {
    /**
     * This method returns a message text
     * 
     * @return
     */
    public String message();
  }
  
  /**
   * Representation of a selected {@link Loglevel} of the {@link AssertLogger}. This usually maps to log level like error, warn,
   * info, debug or trace and provides message write methods like:<br>
   * {@link #message(String)},<br>
   * {@link #message(Throwable)},<br>
   * {@link #message(MessageFactory)},<br>
   * {@link #message(String, Throwable)}<br>
   * <br>
   * The {@link #assertThat()} method provides further method to do assertions in various forms. See {@link LoglevelAssert} for
   * more information about that.
   * 
   * @author Omnaest
   */
  public static interface Loglevel extends Serializable
  {
    /**
     * Returns a {@link LoglevelAssert} instance
     * 
     * @return
     */
    public LoglevelAssert assertThat();
    
    /**
     * Writes a message and a given {@link Throwable} to the {@link Logger} using the selected {@link Loglevel}.<br>
     * <br>
     * The {@link MessageFactory#message()} method is only invoked if the respective {@link Loglevel} is set to true within the
     * logging configuration.
     * 
     * @param messageFactory
     *          {@link MessageFactory}
     * @param e
     *          {@link Throwable}
     * @return {@link Loglevel}
     */
    public Loglevel message( MessageFactory messageFactory, Throwable e );
    
    /**
     * Writes a message to the {@link Logger} using the selected {@link Loglevel} <br>
     * <br>
     * The {@link MessageFactory#message()} method is only invoked if the respective {@link Loglevel} is set to true within the
     * logging configuration.
     * 
     * @param messageFactory
     *          {@link MessageFactory}
     * @return {@link Loglevel}
     */
    public Loglevel message( MessageFactory messageFactory );
    
    /**
     * Writes the stacktracke of a given {@link Throwable} to the {@link Logger} using the selected {@link Loglevel}
     * 
     * @param e
     *          {@link Throwable}
     * @return {@link Loglevel}
     */
    public Loglevel message( Throwable e );
    
    /**
     * Writes a message and a given {@link Throwable} to the {@link Logger} using the selected {@link Loglevel}
     * 
     * @param message
     * @param e
     *          {@link Throwable}
     * @return {@link Loglevel}
     */
    public Loglevel message( String message, Throwable e );
    
    /**
     * Writes a message to the {@link Logger} using the selected {@link Loglevel}
     * 
     * @param message
     * @return {@link Loglevel}
     */
    public Loglevel message( String message );
    
  }
  
  /**
   * Provider for {@link Assert} based methods which throws appropriate {@link Exception}s which it catches immediately and logs
   * it to the underlying {@link Logger} using the selected {@link Loglevel}. This allows to output {@link Exception} based
   * stacktrace information without raising an {@link Exception} for any calling logic.<br>
   * <br>
   * Therefore all functions of the {@link LoglevelAssert} will <b>return true</b>, if the assertion has <b>not failed</b>. And it
   * returns false if the assertion has failed and an {@link Exception} was logged to the underlying {@link Logger} instance. <br>
   * <br>
   * For usage examples see {@link AssertLogger}...
   * 
   * @author Omnaest
   */
  public static interface LoglevelAssert extends Serializable
  {
    
    /**
     * Returns true if the {@link DurationCapture#getInterimTime(TimeUnit)} is lower than the given duration limit.
     * 
     * @see Assert#isInterimTimeLowerThan(int, TimeUnit, DurationCapture, Object[])
     * @see LoglevelAssert
     * @param durationLimit
     * @param timeUnit
     *          {@link TimeUnit}
     * @param durationCapture
     *          {@link DurationCapture}
     * @param intervalKeys
     * @return
     */
    public boolean isInterimTimeLowerThan( int durationLimit,
                                           TimeUnit timeUnit,
                                           DurationCapture durationCapture,
                                           Object[] intervalKeys );
    
    /**
     * @see Assert#fails()
     * @see LoglevelAssert
     * @param message
     * @param cause
     */
    public void fails( String message, Exception cause );
    
    /**
     * @see Assert#fails()
     * @see LoglevelAssert
     * @param message
     */
    public void fails( String message );
    
    /**
     * @see Assert#fails(Exception)
     * @see LoglevelAssert
     * @param cause
     */
    public void fails( Exception cause );
    
    /**
     * @see Assert#fails()
     * @see LoglevelAssert
     */
    public void fails();
    
    /**
     * Returns true if all the given {@link Object}s are not null.
     * 
     * @see Assert#isNotNull(String, Object, Object, Object...)
     * @see LoglevelAssert
     * @param message
     * @param object
     * @param objects
     * @return
     */
    public boolean isNotNull( String message, Object object, Object... objects );
    
    /**
     * Returns true if all the given {@link Object}s are not null.
     * 
     * @see Assert#isNotNull(Object, String)
     * @see LoglevelAssert
     * @param object
     * @param message
     * @return
     */
    public boolean isNotNull( Object object, String message );
    
    /**
     * Returns true if all the given {@link Object}s are not null.
     * 
     * @see Assert#isNotNull(Object, Object...)
     * @see LoglevelAssert
     * @param object
     * @param objects
     * @return
     */
    public boolean isNotNull( Object object, Object... objects );
    
    /**
     * Returns true if the given {@link Object} is not null.
     * 
     * @see Assert#isNotNull(Object)
     * @see LoglevelAssert
     * @param object
     * @return
     */
    public boolean isNotNull( Object object );
    
    /**
     * Returns true if the given {@link Collection}s is not null and not empty.
     * 
     * @see Assert#isNotEmpty(Collection, String)
     * @see LoglevelAssert
     * @param message
     * @param collection
     * @return
     */
    public boolean isNotEmpty( Collection<?> collection, String message );
    
    /**
     * Returns true if the given {@link Collection}s is not null and not empty.
     * 
     * @see Assert#isNotEmpty(Collection)
     * @see LoglevelAssert
     * @param collection
     * @return
     */
    public boolean isNotEmpty( Collection<?> collection );
    
    /**
     * Returns true if the given {@link Object}s are equal
     * 
     * @see Assert#areEqual(Object, Object, String)
     * @see LoglevelAssert
     * @param object1
     * @param object2
     * @param message
     * @return
     */
    public boolean areEqual( Object object1, Object object2, String message );
    
    /**
     * Returns true if the given {@link Object}s are equal
     * 
     * @see Assert#areEqual(Object, Object)
     * @see LoglevelAssert
     * @param object1
     * @param object2
     * @return
     */
    public boolean areEqual( Object object1, Object object2 );
    
    /**
     * Returns true if the given expression is false
     * 
     * @see Assert#isFalse(boolean, String)
     * @see LoglevelAssert
     * @param expression
     * @param message
     * @return
     */
    public boolean isFalse( boolean expression, String message );
    
    /**
     * Returns true if the given expression is false
     * 
     * @see Assert#isFalse(boolean)
     * @see LoglevelAssert
     * @param expression
     * @return
     */
    public boolean isFalse( boolean expression );
    
    /**
     * Returns true if the given expression is true
     * 
     * @see Assert#isTrue(boolean, String)
     * @see LoglevelAssert
     * @param expression
     * @param message
     * @return
     */
    public boolean isTrue( boolean expression, String message );
    
    /**
     * Returns true if the given expression is true
     * 
     * @see Assert#isTrue(boolean)
     * @see LoglevelAssert
     * @param expression
     * @return
     */
    public boolean isTrue( boolean expression );
  }
  
  /**
   * Implementation for {@link Loglevel} and {@link LoglevelAssert}
   * 
   * @author Omnaest
   */
  protected static class LoglevelImpl implements Loglevel, LoglevelAssert
  {
    /* ************************************************** Constants *************************************************** */
    private static final long     serialVersionUID = -1253381257506882164L;
    /* ********************************************** Variables ********************************************** */
    private final LoglevelSupport loglevelSupport;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * Necessarily supported operations for a {@link Loglevel}
     * 
     * @author Omnaest
     */
    public static interface LoglevelSupport extends Serializable
    {
      /**
       * @return
       */
      public boolean isLoglevelEnabled();
      
      /**
       * @param message
       */
      public void writeMessage( String message );
      
      /**
       * @param message
       * @param e
       */
      public void writeMessage( String message, Throwable e );
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Loglevel
     * @param lomessage
     */
    protected LoglevelImpl( LoglevelSupport loglevelSupport )
    {
      super();
      this.loglevelSupport = loglevelSupport;
    }
    
    @Override
    public Loglevel message( String message )
    {
      //
      this.loglevelSupport.writeMessage( message );
      return this;
    }
    
    @Override
    public Loglevel message( String message, Throwable e )
    {
      //
      this.loglevelSupport.writeMessage( message, e );
      return this;
    }
    
    @Override
    public Loglevel message( Throwable e )
    {
      //
      this.loglevelSupport.writeMessage( "Exception occurred", e );
      return this;
    }
    
    @Override
    public Loglevel message( MessageFactory messageFactory )
    {
      //
      if ( this.loglevelSupport.isLoglevelEnabled() )
      {
        String message = messageFactory != null ? messageFactory.message() : "";
        this.loglevelSupport.writeMessage( message );
      }
      return this;
    }
    
    @Override
    public Loglevel message( MessageFactory messageFactory, Throwable e )
    {
      //
      if ( this.loglevelSupport.isLoglevelEnabled() )
      {
        String message = messageFactory != null ? messageFactory.message() : "";
        this.loglevelSupport.writeMessage( message, e );
      }
      return this;
    }
    
    @Override
    public boolean isTrue( boolean expression )
    {
      //
      try
      {
        return Assert.isTrue( expression );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isTrue(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isTrue( boolean expression, String message )
    {
      //
      try
      {
        return Assert.isTrue( expression, message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isTrue(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isFalse( boolean expression )
    {
      //
      try
      {
        return Assert.isFalse( expression );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isFalse(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isFalse( boolean expression, String message )
    {
      //
      try
      {
        return Assert.isFalse( expression, message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isFalse(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean areEqual( Object object1, Object object2 )
    {
      //
      try
      {
        return Assert.areEqual( object1, object2 );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isEqual(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean areEqual( Object object1, Object object2, String message )
    {
      //
      try
      {
        return Assert.areEqual( object1, object2, message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isEqual(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotEmpty( Collection<?> collection )
    {
      //
      try
      {
        return Assert.isNotEmpty( collection );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotEmpty(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotEmpty( Collection<?> collection, String message )
    {
      //
      try
      {
        return Assert.isNotEmpty( collection, message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotEmpty(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotNull( Object object )
    {
      //
      try
      {
        return Assert.isNotNull( object );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotNull(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotNull( Object object, Object... objects )
    {
      //
      try
      {
        return Assert.isNotNull( object, objects );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotNull(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotNull( Object object, String message )
    {
      //
      try
      {
        return Assert.isNotNull( object, message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotNull(...) failed", e );
        return false;
      }
    }
    
    @Override
    public boolean isNotNull( String message, Object object, Object... objects )
    {
      //
      try
      {
        return Assert.isNotNull( message, object, objects );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isNotNull(...) failed", e );
        return false;
      }
    }
    
    @Override
    public void fails()
    {
      //
      try
      {
        Assert.fails();
      }
      catch ( Exception e )
      {
        this.message( "Assert.fails() notifies about an operation failure", e );
      }
    }
    
    @Override
    public void fails( Exception cause )
    {
      //
      try
      {
        Assert.fails( cause );
      }
      catch ( Exception e )
      {
        this.message( "Assert.fails() notifies about an operation failure", e );
      }
    }
    
    @Override
    public void fails( String message )
    {
      //
      try
      {
        Assert.fails( message );
      }
      catch ( Exception e )
      {
        this.message( "Assert.fails() notifies about an operation failure", e );
      }
    }
    
    @Override
    public void fails( String message, Exception cause )
    {
      //
      try
      {
        Assert.fails( message, cause );
      }
      catch ( Exception e )
      {
        this.message( "Assert.fails() notifies about an operation failure", e );
      }
    }
    
    @Override
    public boolean isInterimTimeLowerThan( int durationLimit,
                                           TimeUnit timeUnit,
                                           DurationCapture durationCapture,
                                           Object[] intervalKeys )
    {
      //
      try
      {
        return Assert.isInterimTimeLowerThan( durationLimit, timeUnit, durationCapture, intervalKeys );
      }
      catch ( Exception e )
      {
        this.message( "Assert.isInterimTimeLowerThan(...) failed", e );
        return false;
      }
    }
    
    @Override
    public LoglevelAssert assertThat()
    {
      return this;
    }
  }
  
  /**
   * @author Omnaest
   */
  public static interface DirectAssertHandlerMessageChoice
  {
    /**
     * @param message
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandlerLogLevelChoice logWithMessage( String message );
    
    /**
     * @param messageFactory
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandlerLogLevelChoice logWithMessage( Factory<String> messageFactory );
  }
  
  /**
   * @author Omnaest
   */
  public static interface DirectAssertHandlerLogLevelChoice
  {
    /**
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandler asError();
    
    /**
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandler asWarn();
    
    /**
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandler asInfo();
    
    /**
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandler asDebug();
    
    /**
     * @return {@link DirectAssertHandler}
     */
    public DirectAssertHandler asTrace();
    
  }
  
  /**
   * @author Omnaest
   */
  public static interface DirectAssertResultValueProvider
  {
    /**
     * Returns the result of the assertion. If the assertion fails this is false otherwise true.
     * 
     * @return
     */
    public boolean getAssertResult();
  }
  
  /**
   * @author Omnaest
   */
  public static interface DirectAssertHandler extends DirectAssertResultValueProvider, Serializable,
                                             DirectAssertHandlerMessageChoice
  {
    /**
     * @throws Exception
     */
    public void throwException() throws Exception;
    
    /**
     * 
     */
    public void throwRuntimeException();
    
    /**
     * @param exception
     * @throws Exception
     */
    public void throwException( Exception exception ) throws Exception;
    
    /**
     * @param throwable
     * @throws Throwable
     */
    public void throwException( Throwable throwable ) throws Throwable;
    
    /**
     * @return {@link DirectAssertHandlerLogLevelChoice}
     */
    public DirectAssertHandlerLogLevelChoice log();
  }
  
  public static interface DirectAssertWithExpression extends DirectAssert, DirectAssertResultValueProvider
  {
    /**
     * Returns the {@link DirectAssert} again to assert further expressions
     * 
     * @return this
     */
    public DirectAssert and();
    
    /**
     * Returns the {@link DirectAssertHandler} which allows to handle the result of the previous assertions
     * 
     * @return
     */
    public DirectAssertHandler onFailure();
  }
  
  /**
   * @author Omnaest
   */
  public static interface DirectAssert extends Serializable
  {
    
    /**
     * Returns true if the {@link DurationCapture#getInterimTime(TimeUnit)} is lower than the given duration limit.
     * 
     * @see Assert#isInterimTimeLowerThan(int, TimeUnit, DurationCapture, Object[])
     * @see LoglevelAssert
     * @param durationLimit
     * @param timeUnit
     *          {@link TimeUnit}
     * @param durationCapture
     *          {@link DurationCapture}
     * @param intervalKeys
     * @return
     */
    public DirectAssertWithExpression isInterimTimeLowerThan( int durationLimit,
                                                              TimeUnit timeUnit,
                                                              DurationCapture durationCapture,
                                                              Object[] intervalKeys );
    
    /**
     * @see Assert#fails(Exception)
     * @see LoglevelAssert
     * @param cause
     */
    public DirectAssertWithExpression fails( Exception cause );
    
    /**
     * @see Assert#fails()
     * @see LoglevelAssert
     */
    public DirectAssertWithExpression fails();
    
    /**
     * Returns true if all the given {@link Object}s are not null.
     * 
     * @see Assert#isNotNull(Object, Object...)
     * @see LoglevelAssert
     * @param object
     * @param objects
     * @return
     */
    public DirectAssertWithExpression isNotNull( Object object, Object... objects );
    
    /**
     * Returns true if the given {@link Object} is not null.
     * 
     * @see Assert#isNotNull(Object)
     * @see LoglevelAssert
     * @param object
     * @return
     */
    public DirectAssertWithExpression isNotNull( Object object );
    
    /**
     * Returns true if the given {@link Collection}s is not null and not empty.
     * 
     * @see Assert#isNotEmpty(Collection)
     * @see LoglevelAssert
     * @param collection
     * @return
     */
    public DirectAssertWithExpression isNotEmpty( Collection<?> collection );
    
    /**
     * Returns true if the given {@link Object}s are equal
     * 
     * @see Assert#areEqual(Object, Object)
     * @see LoglevelAssert
     * @param object1
     * @param object2
     * @return
     */
    public DirectAssertWithExpression areEqual( Object object1, Object object2 );
    
    /**
     * Returns true if the given expression is false
     * 
     * @see Assert#isFalse(boolean)
     * @see LoglevelAssert
     * @param expression
     * @return
     */
    public DirectAssertWithExpression isFalse( boolean expression );
    
    /**
     * Returns true if the given expression is true
     * 
     * @see Assert#isTrue(boolean)
     * @see LoglevelAssert
     * @param expression
     * @return
     */
    public DirectAssertWithExpression isTrue( boolean expression );
  }
  
  private class DirectAssertImpl implements DirectAssert, DirectAssertWithExpression, DirectAssertHandler,
                                DirectAssertHandlerLogLevelChoice, DirectAssertHandlerMessageChoice
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = -885293756532927338L;
    
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private boolean           assertResult     = true;
    private String            message          = null;
    private Exception         catchedException = null;
    
    /* *************************************************** Methods **************************************************** */
    
    @Override
    public DirectAssertWithExpression isInterimTimeLowerThan( int durationLimit,
                                                              TimeUnit timeUnit,
                                                              DurationCapture durationCapture,
                                                              Object[] intervalKeys )
    {
      try
      {
        Assert.isInterimTimeLowerThan( durationLimit, timeUnit, durationCapture, intervalKeys );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression fails( Exception cause )
    {
      try
      {
        Assert.fails( cause );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression fails()
    {
      try
      {
        Assert.fails();
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression isNotNull( Object object, Object... objects )
    {
      try
      {
        Assert.isNotNull( object, objects );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression isNotNull( Object object )
    {
      try
      {
        Assert.isNotNull( object );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression isNotEmpty( Collection<?> collection )
    {
      try
      {
        Assert.isNotEmpty( collection );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression areEqual( Object object1, Object object2 )
    {
      try
      {
        Assert.areEqual( object1, object2 );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression isFalse( boolean expression )
    {
      try
      {
        Assert.isFalse( expression );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public DirectAssertWithExpression isTrue( boolean expression )
    {
      try
      {
        Assert.isTrue( expression );
      }
      catch ( Exception e )
      {
        this.assertResult = false;
        this.catchedException = e;
      }
      return this;
    }
    
    @Override
    public boolean getAssertResult()
    {
      return this.assertResult;
    }
    
    @Override
    public DirectAssert and()
    {
      return this;
    }
    
    @Override
    public DirectAssertHandler onFailure()
    {
      return this;
    }
    
    @Override
    public void throwException() throws Exception
    {
      if ( !this.assertResult )
      {
        if ( this.message != null )
        {
          throw new RuntimeException( this.message, this.catchedException );
        }
        throw new Exception( this.catchedException );
      }
    }
    
    @Override
    public void throwException( Exception exception ) throws Exception
    {
      if ( !this.assertResult && exception != null )
      {
        throw exception;
      }
    }
    
    @Override
    public void throwException( Throwable throwable ) throws Throwable
    {
      if ( !this.assertResult && throwable != null )
      {
        throw throwable;
      }
    }
    
    @Override
    public void throwRuntimeException()
    {
      if ( !this.assertResult )
      {
        if ( this.message != null )
        {
          throw new RuntimeException( this.message, this.catchedException );
        }
        throw new RuntimeException( this.catchedException );
      }
    }
    
    @Override
    public DirectAssertHandler asError()
    {
      this.log( AssertLogger.this.error );
      return this;
    }
    
    @Override
    public DirectAssertHandler asWarn()
    {
      this.log( AssertLogger.this.warn );
      return this;
    }
    
    @Override
    public DirectAssertHandler asInfo()
    {
      this.log( AssertLogger.this.info );
      return this;
    }
    
    @Override
    public DirectAssertHandler asDebug()
    {
      this.log( AssertLogger.this.debug );
      return this;
    }
    
    @Override
    public DirectAssertHandler asTrace()
    {
      this.log( AssertLogger.this.trace );
      return this;
    }
    
    private void log( Loglevel loglevel )
    {
      if ( !this.assertResult )
      {
        if ( this.message != null )
        {
          loglevel.message( this.message, this.catchedException );
        }
        else
        {
          loglevel.message( this.catchedException );
        }
      }
    }
    
    @Override
    public DirectAssertHandlerLogLevelChoice logWithMessage( String message )
    {
      this.message = message;
      return this;
    }
    
    @Override
    public DirectAssertHandlerLogLevelChoice logWithMessage( Factory<String> messageFactory )
    {
      if ( !this.assertResult )
      {
        this.message = messageFactory != null ? messageFactory.newInstance() : null;
      }
      return this;
    }
    
    @Override
    public DirectAssertHandlerLogLevelChoice log()
    {
      return this;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates an {@link AssertLogger} using the {@link LoggerFactory} to create an {@link Logger} instance.
   * 
   * @see AssertLogger
   * @param type
   */
  public AssertLogger( Class<?> type )
  {
    this( LoggerFactory.getLogger( type ) );
  }
  
  /**
   * @see AssertLogger
   * @param logger
   */
  public AssertLogger( final Logger logger )
  {
    super();
    Assert.isNotNull( logger, "Logger reference must not be null, but a null reference has been given" );
    this.logger = logger;
    this.trace = new LoglevelImpl( new LoglevelSupport()
    {
      private static final long serialVersionUID = 7211946266092536103L;
      
      @Override
      public void writeMessage( String message, Throwable e )
      {
        logger.trace( message, e );
      }
      
      @Override
      public void writeMessage( String message )
      {
        logger.trace( message );
      }
      
      @Override
      public boolean isLoglevelEnabled()
      {
        return logger.isTraceEnabled();
      }
    } );
    this.debug = new LoglevelImpl( new LoglevelSupport()
    {
      private static final long serialVersionUID = -8205878887500731466L;
      
      @Override
      public void writeMessage( String message, Throwable e )
      {
        logger.debug( message, e );
      }
      
      @Override
      public void writeMessage( String message )
      {
        logger.debug( message );
      }
      
      @Override
      public boolean isLoglevelEnabled()
      {
        return logger.isDebugEnabled();
      }
    } );
    this.info = new LoglevelImpl( new LoglevelSupport()
    {
      private static final long serialVersionUID = -763948000020211389L;
      
      @Override
      public void writeMessage( String message, Throwable e )
      {
        logger.info( message, e );
      }
      
      @Override
      public void writeMessage( String message )
      {
        logger.info( message );
      }
      
      @Override
      public boolean isLoglevelEnabled()
      {
        return logger.isInfoEnabled();
      }
    } );
    this.warn = new LoglevelImpl( new LoglevelSupport()
    {
      private static final long serialVersionUID = -5760294798817317257L;
      
      @Override
      public void writeMessage( String message, Throwable e )
      {
        logger.warn( message, e );
      }
      
      @Override
      public void writeMessage( String message )
      {
        logger.warn( message );
      }
      
      @Override
      public boolean isLoglevelEnabled()
      {
        return logger.isWarnEnabled();
      }
    } );
    this.error = new LoglevelImpl( new LoglevelSupport()
    {
      private static final long serialVersionUID = 8052613300931257305L;
      
      @Override
      public void writeMessage( String message, Throwable e )
      {
        logger.error( message, e );
      }
      
      @Override
      public void writeMessage( String message )
      {
        logger.error( message );
      }
      
      @Override
      public boolean isLoglevelEnabled()
      {
        return logger.isErrorEnabled();
      }
    } );
  }
  
  /**
   * @return new {@link DirectAssert} instance
   */
  public DirectAssert assertThat()
  {
    return new DirectAssertImpl();
  }
  
}
