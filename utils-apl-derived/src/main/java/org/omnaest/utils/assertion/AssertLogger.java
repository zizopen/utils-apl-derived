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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AssertLogger} provides methods of a {@link Logger} as well as methods of an {@link Assert} helper. This allows to
 * easily log assertions.
 * 
 * @see Assert
 * @see Logger
 * @author Omnaest
 */
public class AssertLogger
{
  /* ********************************************** Variables ********************************************** */
  protected final Logger logger;
  
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
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates an {@link AssertLogger} using the {@link LoggerFactory} to create an {@link Logger} instance.
   * 
   * @see AssertLogger
   * @param type
   */
  public AssertLogger( Class<?> type )
  {
    super();
    this.logger = LoggerFactory.getLogger( type );
  }
  
  /**
   * @see AssertLogger
   * @param logger
   */
  public AssertLogger( Logger logger )
  {
    super();
    this.logger = logger;
  }
  
  /**
   * @see MessageFactory
   * @param messageFactory
   */
  public void debug( MessageFactory messageFactory )
  {
    if ( messageFactory != null && this.isDebugEnabled() )
    {
      this.debug( messageFactory.message() );
    }
  }
  
  /**
   * @param msg
   * @see org.slf4j.Logger#debug(java.lang.String)
   */
  public void debug( String msg )
  {
    this.logger.debug( msg );
  }
  
  /**
   * @param msg
   * @param e
   * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
   */
  public void debug( String msg, Throwable e )
  {
    this.logger.debug( msg, e );
  }
  
  /**
   * @see #debug(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param expression
   */
  public void debugAssertIsTrue( boolean expression )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.debug( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #debug(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param object
   */
  public void debugAssertIsTrue( boolean expression, String message )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.debug( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #debug(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void debugAssertNotNull( Object object )
  {
    //
    try
    {
      Assert.notNull( object );
    }
    catch ( Exception e )
    {
      this.debug( "Assert not null failed", e );
    }
  }
  
  /**
   * @see #debug(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void debugAssertNotNull( Object object, String message )
  {
    //
    try
    {
      Assert.notNull( object, message );
    }
    catch ( Exception e )
    {
      this.debug( "Assert not null failed", e );
    }
  }
  
  /**
   * @see MessageFactory
   * @param messageFactory
   */
  public void error( MessageFactory messageFactory )
  {
    if ( messageFactory != null && this.isErrorEnabled() )
    {
      this.error( messageFactory.message() );
    }
  }
  
  /**
   * @param msg
   * @see org.slf4j.Logger#error(java.lang.String)
   */
  public void error( String msg )
  {
    this.logger.error( msg );
  }
  
  /**
   * @param msg
   * @param e
   * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
   */
  public void error( String msg, Throwable e )
  {
    this.logger.error( msg, e );
  }
  
  /**
   * @see #error(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param expression
   */
  public void errorAssertIsTrue( boolean expression )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.error( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #error(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param object
   */
  public void errorAssertIsTrue( boolean expression, String message )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.error( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #error(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void errorAssertNotNull( Object object )
  {
    //
    try
    {
      Assert.notNull( object );
    }
    catch ( Exception e )
    {
      this.error( "Assert not null failed", e );
    }
  }
  
  /**
   * @see #error(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void errorAssertNotNull( Object object, String message )
  {
    //
    try
    {
      Assert.notNull( object, message );
    }
    catch ( Exception e )
    {
      this.error( "Assert not null failed", e );
    }
  }
  
  /**
   * @return the logger
   */
  public Logger getLogger()
  {
    return this.logger;
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#getName()
   */
  public String getName()
  {
    return this.logger.getName();
  }
  
  /**
   * @see MessageFactory
   * @param messageFactory
   */
  public void info( MessageFactory messageFactory )
  {
    if ( messageFactory != null && this.isInfoEnabled() )
    {
      this.info( messageFactory.message() );
    }
  }
  
  /**
   * @param msg
   * @see org.slf4j.Logger#info(java.lang.String)
   */
  public void info( String msg )
  {
    this.logger.info( msg );
  }
  
  /**
   * @param msg
   * @param e
   * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
   */
  public void info( String msg, Throwable e )
  {
    this.logger.info( msg, e );
  }
  
  /**
   * @see #info(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param expression
   */
  public void infoAssertIsTrue( boolean expression )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.info( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #info(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param object
   */
  public void infoAssertIsTrue( boolean expression, String message )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.info( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #info(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void infoAssertNotNull( Object object )
  {
    //
    try
    {
      Assert.notNull( object );
    }
    catch ( Exception e )
    {
      this.info( "Assert not null failed", e );
    }
  }
  
  /**
   * @see #info(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void infoAssertNotNull( Object object, String message )
  {
    //
    try
    {
      Assert.notNull( object, message );
    }
    catch ( Exception e )
    {
      this.info( "Assert not null failed", e );
    }
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#isDebugEnabled()
   */
  public boolean isDebugEnabled()
  {
    return this.logger.isDebugEnabled();
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#isErrorEnabled()
   */
  public boolean isErrorEnabled()
  {
    return this.logger.isErrorEnabled();
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#isInfoEnabled()
   */
  public boolean isInfoEnabled()
  {
    return this.logger.isInfoEnabled();
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#isTraceEnabled()
   */
  public boolean isTraceEnabled()
  {
    return this.logger.isTraceEnabled();
  }
  
  /**
   * @return
   * @see org.slf4j.Logger#isWarnEnabled()
   */
  public boolean isWarnEnabled()
  {
    return this.logger.isWarnEnabled();
  }
  
  /**
   * @see MessageFactory
   * @param messageFactory
   */
  public void trace( MessageFactory messageFactory )
  {
    if ( messageFactory != null && this.isTraceEnabled() )
    {
      this.trace( messageFactory.message() );
    }
  }
  
  /**
   * @param msg
   * @see org.slf4j.Logger#trace(java.lang.String)
   */
  public void trace( String msg )
  {
    this.logger.trace( msg );
  }
  
  /**
   * @param msg
   * @param e
   * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
   */
  public void trace( String msg, Throwable e )
  {
    this.logger.trace( msg, e );
  }
  
  /**
   * @see #trace(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param expression
   */
  public void traceAssertIsTrue( boolean expression )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.trace( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #trace(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param object
   */
  public void traceAssertIsTrue( boolean expression, String message )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.trace( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #trace(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void traceAssertNotNull( Object object )
  {
    //
    try
    {
      Assert.notNull( object );
    }
    catch ( Exception e )
    {
      this.trace( "Assert not null failed", e );
    }
  }
  
  /**
   * @see #trace(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void traceAssertNotNull( Object object, String message )
  {
    //
    try
    {
      Assert.notNull( object, message );
    }
    catch ( Exception e )
    {
      this.trace( "Assert not null failed", e );
    }
  }
  
  /**
   * @see MessageFactory
   * @param messageFactory
   */
  public void warn( MessageFactory messageFactory )
  {
    if ( messageFactory != null && this.isWarnEnabled() )
    {
      this.warn( messageFactory.message() );
    }
  }
  
  /**
   * @param msg
   * @see org.slf4j.Logger#warn(java.lang.String)
   */
  public void warn( String msg )
  {
    this.logger.warn( msg );
  }
  
  /**
   * @param msg
   * @param e
   * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
   */
  public void warn( String msg, Throwable e )
  {
    this.logger.warn( msg, e );
  }
  
  /**
   * @see #warn(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param expression
   */
  public void warnAssertIsTrue( boolean expression )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.warn( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #warn(String, Throwable)
   * @see Assert#isTrue(boolean)
   * @param object
   */
  public void warnAssertIsTrue( boolean expression, String message )
  {
    //
    try
    {
      Assert.isTrue( expression );
    }
    catch ( Exception e )
    {
      this.warn( "Assert is true failed", e );
    }
  }
  
  /**
   * @see #warn(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void warnAssertNotNull( Object object )
  {
    //
    try
    {
      Assert.notNull( object );
    }
    catch ( Exception e )
    {
      this.warn( "Assert not null failed", e );
    }
  }
  
  /**
   * @see #warn(String, Throwable)
   * @see Assert#notNull(Object)
   * @param object
   */
  public void warnAssertNotNull( Object object, String message )
  {
    //
    try
    {
      Assert.notNull( object, message );
    }
    catch ( Exception e )
    {
      this.warn( "Assert not null failed", e );
    }
  }
  
}
