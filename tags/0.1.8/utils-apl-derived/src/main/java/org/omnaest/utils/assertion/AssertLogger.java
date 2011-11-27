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

import org.omnaest.utils.assertion.AssertLogger.Loglevel.LoglevelSupport;
import org.omnaest.utils.time.DurationCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AssertLogger} provides methods of a {@link Logger} as well as methods of an {@link Assert} helper. This allows to
 * easily log assertions.<br>
 * <br>
 * Usage example:
 * 
 * <pre>
 * AssertLogger assertLogger = new AssertLogger( this.logger );
 * assertLogger.debug.assertThat.isTrue( expression );
 * assertLogger.warn.assertThat.isNotNull( object, &quot;Additional message&quot; );
 * assertLogger.info.assertThat.fails( &quot;Additional message&quot; );
 * </pre>
 * 
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
   * info, debug or trace.
   * 
   * @author Omnaest
   */
  public static class Loglevel
  {
    /* ********************************************** Variables ********************************************** */
    public final LoglevelAssert     assertThat = new LoglevelAssert();
    
    protected final LoglevelSupport loglevelSupport;
    
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
    
    /**
     * Provider for {@link Assert} based methods which catches all {@link Exception}s and logs it to the underlying {@link Logger}
     * using the selected {@link Loglevel}
     * 
     * @author Omnaest
     */
    public class LoglevelAssert
    {
      /**
       * @see Assert#isTrue(boolean)
       * @param expression
       * @return
       */
      public boolean isTrue( boolean expression )
      {
        //
        try
        {
          return Assert.isTrue( expression );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isTrue(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isTrue(boolean, String)
       * @param expression
       * @param message
       * @return
       */
      public boolean isTrue( boolean expression, String message )
      {
        //
        try
        {
          return Assert.isTrue( expression, message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isTrue(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isFalse(boolean)
       * @param expression
       * @return
       */
      public boolean isFalse( boolean expression )
      {
        //
        try
        {
          return Assert.isFalse( expression );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isFalse(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isFalse(boolean, String)
       * @param expression
       * @param message
       * @return
       */
      public boolean isFalse( boolean expression, String message )
      {
        //
        try
        {
          return Assert.isFalse( expression, message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isFalse(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isEqual(Object, Object)
       * @param object1
       * @param object2
       * @return
       */
      public boolean isEqual( Object object1, Object object2 )
      {
        //
        try
        {
          return Assert.isEqual( object1, object2 );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isEqual(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isEqual(Object, Object, String)
       * @param object1
       * @param object2
       * @param message
       * @return
       */
      public boolean isEqual( Object object1, Object object2, String message )
      {
        //
        try
        {
          return Assert.isEqual( object1, object2, message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isEqual(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isNotEmpty(Collection)
       * @param collection
       * @return
       */
      public boolean isNotEmpty( Collection<?> collection )
      {
        //
        try
        {
          return Assert.isNotEmpty( collection );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isNotEmpty(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isNotEmpty(Collection, String)
       * @param message
       * @param collection
       * @return
       */
      public boolean isNotEmpty( Collection<?> collection, String message )
      {
        //
        try
        {
          return Assert.isNotEmpty( collection, message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isNotEmpty(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isNotNull(Object)
       * @param object
       * @return
       */
      public boolean isNotNull( Object object )
      {
        //
        try
        {
          return Assert.isNotNull( object );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isNotNull(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#isNotNull(Object)
       * @param object
       * @param message
       * @return
       */
      public boolean isNotNull( Object object, String message )
      {
        //
        try
        {
          return Assert.isNotNull( object, message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.isNotNull(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see Assert#fails()
       */
      public void fails()
      {
        //
        try
        {
          Assert.fails();
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.fails() notifies about an operation failure", e );
        }
      }
      
      /**
       * @see Assert#fails()
       * @param message
       */
      public void fails( String message )
      {
        //
        try
        {
          Assert.fails( message );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.fails() notifies about an operation failure", e );
        }
      }
      
      /**
       * @see Assert#fails()
       * @param message
       * @param cause
       */
      public void fails( String message, Exception cause )
      {
        //
        try
        {
          Assert.fails( message, cause );
        }
        catch ( Exception e )
        {
          Loglevel.this.message( "Assert.fails() notifies about an operation failure", e );
        }
      }
      
      /**
       * @see Assert#isInterimTimeLowerThan(int, TimeUnit, DurationCapture, Object[])
       * @param durationLimit
       * @param timeUnit
       * @param durationCapture
       * @param intervalKeys
       * @return
       */
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
          Loglevel.this.message( "Assert.isInterimTimeLowerThan(...) failed", e );
          return false;
        }
      }
      
      /**
       * @see LoglevelAssert
       */
      protected LoglevelAssert()
      {
        super();
      }
      
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Loglevel
     * @param lomessage
     */
    protected Loglevel( LoglevelSupport loglevelSupport )
    {
      super();
      this.loglevelSupport = loglevelSupport;
    }
    
    /**
     * Writes a message to the {@link Logger} using the selected {@link Loglevel}
     * 
     * @param message
     * @return {@link Loglevel}
     */
    public Loglevel message( String message )
    {
      //
      Loglevel.this.loglevelSupport.writeMessage( message );
      return Loglevel.this;
    }
    
    /**
     * Writes a message and a given {@link Throwable} to the {@link Logger} using the selected {@link Loglevel}
     * 
     * @param message
     * @param e
     *          {@link Throwable}
     * @return {@link Loglevel}
     */
    public Loglevel message( String message, Throwable e )
    {
      //
      Loglevel.this.loglevelSupport.writeMessage( message, e );
      return Loglevel.this;
    }
    
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
    public Loglevel message( MessageFactory messageFactory )
    {
      //
      if ( Loglevel.this.loglevelSupport.isLoglevelEnabled() )
      {
        String message = messageFactory != null ? messageFactory.message() : "";
        Loglevel.this.loglevelSupport.writeMessage( message );
      }
      return Loglevel.this;
    }
    
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
    public Loglevel message( MessageFactory messageFactory, Throwable e )
    {
      //
      if ( Loglevel.this.loglevelSupport.isLoglevelEnabled() )
      {
        String message = messageFactory != null ? messageFactory.message() : "";
        Loglevel.this.loglevelSupport.writeMessage( message, e );
      }
      return Loglevel.this;
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
    this.trace = new Loglevel( new LoglevelSupport()
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
    this.debug = new Loglevel( new LoglevelSupport()
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
    this.info = new Loglevel( new LoglevelSupport()
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
    this.warn = new Loglevel( new LoglevelSupport()
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
    this.error = new Loglevel( new LoglevelSupport()
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
