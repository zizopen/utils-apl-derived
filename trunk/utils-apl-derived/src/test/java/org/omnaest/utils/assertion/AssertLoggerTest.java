package org.omnaest.utils.assertion;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.slf4j.Logger;

/**
 * @see AssertLogger
 * @author Omnaest
 */
public class AssertLoggerTest
{
  
  /* ********************************************** Variables ********************************************** */
  private Logger         logger       = Mockito.mock( Logger.class );
  protected AssertLogger assertLogger = new AssertLogger( this.logger );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testAssertLoggerLogger()
  {
    //
    this.assertLogger.debug.assertThis.isTrue( true );
    Mockito.verify( this.logger, new Times( 0 ) ).debug( Matchers.anyString() );
    
    //
    this.assertLogger.debug.assertThis.isTrue( false );
    Mockito.verify( this.logger, new Times( 1 ) ).debug( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.trace.assertThis.isNotNull( new Object() );
    Mockito.verify( this.logger, new Times( 0 ) ).trace( Matchers.anyString() );
    
    //
    this.assertLogger.trace.assertThis.isNotNull( null );
    Mockito.verify( this.logger, new Times( 1 ) ).trace( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.info.assertThis.fails();
    Mockito.verify( this.logger, new Times( 0 ) ).info( Matchers.anyString() );
    Mockito.verify( this.logger, new Times( 1 ) ).info( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.info.assertThis.fails( "Additional message" );
    Mockito.verify( this.logger, new Times( 2 ) ).info( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.warn.assertThis.isNotNull( new Object(), "Additional message" );
    Mockito.verify( this.logger, new Times( 0 ) ).warn( Matchers.anyString() );
    
    //
    this.assertLogger.warn.assertThis.isNotNull( null, "Additional message" );
    Mockito.verify( this.logger, new Times( 1 ) ).warn( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.error.assertThis.isTrue( true, "Additional message" );
    Mockito.verify( this.logger, new Times( 0 ) ).error( Matchers.anyString() );
    
    //
    this.assertLogger.error.assertThis.isTrue( false, "Additional message" );
    Mockito.verify( this.logger, new Times( 1 ) ).error( Matchers.anyString(), Matchers.any( Exception.class ) );
  }
  
}
