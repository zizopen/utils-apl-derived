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

import org.omnaest.utils.time.DurationCapture;

/**
 * The {@link Assert} class offers assert methods which throw {@link RuntimeException}s if constraints are not fulfilled.
 * 
 * @author Omnaest
 */
public class Assert
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link RuntimeException} that indicates a failed operation.
   * 
   * @author Omnaest
   */
  public static class FailedOperationException extends RuntimeException
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 8884612146655100366L;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see FailedOperationException
     * @param message
     */
    public FailedOperationException( String message )
    {
      super( message );
    }
    
    /**
     * @see FailedOperationException
     * @param message
     * @param cause
     */
    public FailedOperationException( String message, Throwable cause )
    {
      super( message, cause );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FailedOperationException
   */
  public static void fails()
  {
    fails( "Operation failed" );
  }
  
  /**
   * @see FailedOperationException
   * @param message
   */
  public static void fails( String message )
  {
    throw new FailedOperationException( message );
  }
  
  /**
   * @see FailedOperationException
   * @param message
   * @param cause
   */
  public static void fails( String message, Exception cause )
  {
    throw new FailedOperationException( message, cause );
  }
  
  /**
   * @param object
   */
  public static boolean isNotNull( Object object )
  {
    return isNotNull( object, "Object was null, but must be not null" );
  }
  
  /**
   * @param object
   * @param message
   */
  public static boolean isNotNull( Object object, String message )
  {
    if ( object == null )
    {
      throw new IllegalArgumentException( message );
    }
    return true;
  }
  
  /**
   * @param expression
   */
  public static boolean isTrue( boolean expression )
  {
    return isTrue( expression, "Expression must be true, but was false" );
  }
  
  /**
   * @param expression
   * @param message
   */
  public static boolean isTrue( boolean expression, String message )
  {
    if ( !expression )
    {
      throw new IllegalArgumentException( message );
    }
    return true;
  }
  
  /**
   * @param expression
   */
  public static boolean isFalse( boolean expression )
  {
    return isFalse( expression, "Expression must be false, but was true" );
  }
  
  /**
   * @param expression
   * @param message
   */
  public static boolean isFalse( boolean expression, String message )
  {
    return isTrue( !expression, message );
  }
  
  /**
   * @param collection
   */
  public static boolean isNotEmpty( Collection<?> collection )
  {
    return isNotEmpty( collection, "Collection must be not empty" );
  }
  
  /**
   * @param collection
   * @param message
   */
  public static boolean isNotEmpty( Collection<?> collection, String message )
  {
    if ( collection == null || collection.isEmpty() )
    {
      throw new IllegalArgumentException( message );
    }
    return true;
  }
  
  /**
   * @param object1
   * @param object2
   */
  public static boolean isEqual( Object object1, Object object2 )
  {
    return isEqual( object1, object2, "The two given objects must be equal" );
  }
  
  /**
   * @param object1
   * @param object2
   * @param message
   */
  public static boolean isEqual( Object object1, Object object2, String message )
  {
    if ( object1 != object2 && !( object1 != null && object1.equals( object2 ) )
         && !( object2 != null && object2.equals( object1 ) ) )
    {
      throw new IllegalArgumentException( message );
    }
    return true;
  }
  
  /**
   * Asserts that the {@link DurationCapture#getInterimTimeInMilliseconds(Object...)} is lower than the given duration limit for
   * the given {@link TimeUnit} and interval keys.
   * 
   * @param durationLimit
   * @param timeUnit
   * @param durationCapture
   * @param intervalKeys
   * @return true if the assertion fulfilled the requirement
   */
  public static boolean isInterimTimeLowerThan( int durationLimit,
                                                TimeUnit timeUnit,
                                                DurationCapture durationCapture,
                                                Object[] intervalKeys )
  {
    //
    boolean retval = false;
    
    //
    if ( timeUnit != null && durationCapture != null )
    {
      //
      long interimTimeInMilliseconds = durationCapture.getInterimTimeInMilliseconds( intervalKeys );
      long durationLimitInMilliseconds = timeUnit.toMillis( durationLimit );
      retval = isTrue( interimTimeInMilliseconds <= durationLimitInMilliseconds,
                       "Interim time for the interval keys " + String.valueOf( intervalKeys ) + " should be lower than "
                           + durationLimitInMilliseconds + " ms but actually was " + interimTimeInMilliseconds + " ms" );
    }
    
    //
    return retval;
  }
}