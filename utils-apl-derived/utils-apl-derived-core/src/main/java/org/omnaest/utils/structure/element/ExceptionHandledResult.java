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
package org.omnaest.utils.structure.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.ListUtils;

/**
 * An {@link ExceptionHandledResult} is the result of an operation which catches {@link Exception}s and does not throw them.<br>
 * The {@link #getResult()} will return potential value.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ExceptionHandledResult<E>
{
  /* ********************************************** Variables ********************************************** */
  protected E               result        = null;
  protected List<Exception> exceptionList = new ArrayList<Exception>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param result
   * @param exceptionCollection
   */
  public ExceptionHandledResult( E result, Collection<Exception> exceptionCollection )
  {
    super();
    this.result = result;
    this.exceptionList.addAll( exceptionCollection );
  }
  
  /**
   * Returns the resulting element
   * 
   * @return
   */
  public E getResult()
  {
    return this.result;
  }
  
  /**
   * Returns true if {@link #getResult()} is not null
   * 
   * @return
   */
  public boolean hasResult()
  {
    return this.result != null;
  }
  
  /**
   * Returns the {@link List} of all catched {@link Exception}s
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Exception> getExceptionList()
  {
    return ListUtils.unmodifiableList( this.exceptionList );
  }
  
  /**
   * Returns true, if no {@link Exception}s have been catched
   * 
   * @return
   */
  public boolean hasNoExceptions()
  {
    return this.exceptionList == null || this.exceptionList.isEmpty();
  }
  
  /**
   * Returns true if any {@link Exception}s have been catched
   * 
   * @return
   */
  public boolean hasExceptions()
  {
    return !this.hasNoExceptions();
  }
  
  /**
   * Returns true if the {@link ExceptionHandledResult} contains any exception which could be assigned to the given {@link Class}
   * of {@link Exception}
   * 
   * @param exceptionType
   * @return
   */
  public boolean containsAssignableException( Class<? extends Exception> exceptionType )
  {
    return this.resolveAssignableException( exceptionType ) != null;
  }
  
  /**
   * Resolves the first occurring stored {@link Exception} which can be assigned to the given {@link Class} type. This includes
   * the causes of stored {@link Exception}s.
   * 
   * @see #containsAssignableException(Class)
   * @param exceptionType
   * @return
   */
  public Exception resolveAssignableException( Class<? extends Exception> exceptionType )
  {
    //    
    Exception retval = null;
    
    //
    if ( exceptionType != null && this.hasExceptions() )
    {
      exceptionListLoop: for ( Exception exception : this.exceptionList )
      {
        Throwable cause = exception;
        while ( cause != null )
        {
          //
          if ( exceptionType.isAssignableFrom( cause.getClass() ) )
          {
            if ( cause instanceof Exception )
            {
              retval = (Exception) cause;
              break exceptionListLoop;
            }
          }
          
          //
          cause = cause.getCause();
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Throws the first {@link Exception} again if {@link #hasExceptions()} is true which shows that at least one {@link Exception}
   * has occurred.
   * 
   * @throws Exception
   */
  public void rethrowFirstExceptionIfAnyExceptionHasOccurred() throws Exception
  {
    if ( this.hasExceptions() )
    {
      throw this.exceptionList.get( 0 );
    }
  }
  
  /**
   * Throws the first occurring {@link Exception} which is assignable to the given {@link Class} again. If no {@link Exception}
   * are present or match nothing will happen.
   * 
   * @see #resolveAssignableException(Class)
   * @throws Exception
   */
  public void rethrowFirstExceptionAssignableToTypeIfAnyExceptionHasOccurred( Class<? extends Exception> exceptionType ) throws Exception
  {
    if ( this.hasExceptions() )
    {
      //
      Exception exception = this.resolveAssignableException( exceptionType );
      if ( exception != null )
      {
        throw exception;
      }
    }
  }
  
  /**
   * Returns the first {@link Exception} if any {@link Exception} is present
   * 
   * @return
   */
  public Exception getFirstException()
  {
    //    
    Exception retval = null;
    
    //
    if ( this.hasExceptions() )
    {
      retval = this.exceptionList.get( 0 );
    }
    
    //
    return retval;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ExceptionHandledResult [result=" );
    builder.append( this.result );
    builder.append( ", exceptionList=" );
    builder.append( this.exceptionList );
    builder.append( "]" );
    return builder.toString();
  }
}
