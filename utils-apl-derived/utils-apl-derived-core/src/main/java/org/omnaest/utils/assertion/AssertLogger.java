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

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.omnaest.utils.assertion.AssertLogger.LoglevelImpl.LoglevelSupport;
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
public class AssertLogger
{
  /* ********************************************** Variables ********************************************** */
  public final Logger   logger;
  public final Loglevel trace;
  public final Loglevel debug;
  public final Loglevel info;
  public final Loglevel warn;
  public final Loglevel error;
  
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
  public static interface Loglevel
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
  public static interface LoglevelAssert
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
    /* ********************************************** Variables ********************************************** */
    private final LoglevelSupport loglevelSupport;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * Necessarily supported operations for a {@link Loglevel}
     * 
     * @author Omnaest
     */
    public static interface LoglevelSupport
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
  
}
