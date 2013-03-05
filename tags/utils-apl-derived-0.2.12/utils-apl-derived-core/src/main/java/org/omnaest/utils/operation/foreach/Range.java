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
package org.omnaest.utils.operation.foreach;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.strings.StringReplacer;
import org.omnaest.utils.strings.StringReplacer.ReplacementResult;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.iterator.ElementStreamToIteratorAdapter;

/**
 * Represents a {@link Range} for {@link Long} values which can be used as {@link Iterable}. The number limits are always
 * included.<br>
 * <br>
 * Example:
 * 
 * <pre>
 * for ( long counter : new Range( 1, 1000 ) )
 * {
 *   //do something
 * }
 * </pre>
 * 
 * @author Omnaest
 */
public class Range implements Iterable<Long>
{
  private static final long DEFAULT_STEP = 1l;
  
  private Long              numberFrom   = null;
  private Long              numberTo     = null;
  private Long              step         = Range.DEFAULT_STEP;
  
  /**
   * @see Range
   * @param numberFrom
   * @param numberTo
   * @param step
   */
  public Range( Long numberFrom, Long numberTo, Long step )
  {
    super();
    this.numberFrom = numberFrom;
    this.numberTo = numberTo;
    this.step = step;
  }
  
  /**
   * @see Range
   * @param numberFrom
   * @param numberTo
   */
  public Range( long numberFrom, long numberTo )
  {
    super();
    this.numberFrom = numberFrom;
    this.numberTo = numberTo;
    this.step = determineDefaultStep( this.numberFrom, this.numberTo );
  }
  
  private static long determineDefaultStep( Long numberFrom, Long numberTo )
  {
    return numberTo >= numberFrom ? DEFAULT_STEP : -DEFAULT_STEP;
  }
  
  /**
   * @see Range
   * @param numberFrom
   * @param numberTo
   */
  public Range( Long numberFrom, Long numberTo )
  {
    super();
    this.numberFrom = numberFrom;
    this.numberTo = numberTo;
    this.step = determineDefaultStep( this.numberFrom, this.numberTo );
  }
  
  /**
   * @see Range
   * @param numberFrom
   * @param numberTo
   */
  public Range( int numberFrom, int numberTo )
  {
    super();
    this.numberFrom = (long) numberFrom;
    this.numberTo = (long) numberTo;
    this.step = determineDefaultStep( this.numberFrom, this.numberTo );
  }
  
  /**
   * Allows to specify a {@link Range} with a given {@link String} expression.<br>
   * <br>
   * The expression format is:<br>
   * 
   * <pre>
   * new Range( &quot;1-5&quot; ); //results in 1,2,3,4,5
   * new Range( &quot;1-5:2&quot; ); //results in 1,3,5
   * new Range( &quot;123&quot; ); //result in only a single value
   * 
   * </pre>
   * 
   * @see Range
   * @param rangeExpression
   */
  public Range( String rangeExpression )
  {
    super();
    
    Assert.isNotNull( rangeExpression );
    
    ReplacementResult replacementResult = new StringReplacer( "\\:([0-9]*)$" ).setGroup( 1 ).findAndRemoveFirst( rangeExpression );
    final boolean hasDeclaredStep = replacementResult.hasMatchingTokens();
    if ( hasDeclaredStep )
    {
      String[] matchingTokens = replacementResult.getMatchingTokens();
      if ( matchingTokens != null && matchingTokens.length == 1 )
      {
        final String stepString = matchingTokens[0];
        Assert.isTrue( StringUtils.isNumeric( stepString ), "Step must be numerical but was " + stepString );
        this.step = NumberUtils.toLong( stepString, DEFAULT_STEP );
      }
    }
    rangeExpression = replacementResult.getOutput();
    
    String[] tokens = rangeExpression.split( "-" );
    Assert.isTrue( tokens.length == 2 || tokens.length == 1 );
    if ( tokens.length == 2 )
    {
      Assert.isTrue( StringUtils.isNumeric( tokens[0] ), "Range start must be numerical but was " + tokens[0] );
      Assert.isTrue( StringUtils.isNumeric( tokens[1] ), "Range start must be numerical but was " + tokens[1] );
      
      this.numberFrom = Long.valueOf( tokens[0] );
      this.numberTo = Long.valueOf( tokens[1] );
    }
    else if ( tokens.length == 1 )
    {
      Assert.isTrue( StringUtils.isNumeric( tokens[0] ), "Range start and end must be numerical but was " + tokens[0] );
      
      this.numberFrom = this.numberTo = Long.valueOf( tokens[0] );
    }
    
    if ( !hasDeclaredStep )
    {
      this.step = determineDefaultStep( this.numberFrom, this.numberTo );
    }
    Assert.isTrue( this.numberTo == this.numberFrom || Math.signum( this.numberTo - this.numberFrom ) == Math.signum( this.step ),
                   "The given end number cannot be reached by the given start number and step " + this );
  }
  
  /**
   * Returns true if the given number is within the {@link Range}
   * 
   * @param number
   * @return
   */
  public boolean isWithinRange( long number )
  {
    return number >= this.numberFrom && number <= this.numberTo;
  }
  
  @Override
  public Iterator<Long> iterator()
  {
    return new ElementStreamToIteratorAdapter<Long>( new ElementStream<Long>()
    {
      /* ********************************************** Variables ********************************************** */
      private Long currentValue = Range.this.numberFrom;
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public Long next()
      {
        Long retval = this.currentValue;
        this.currentValue += Range.this.step;
        return retval <= Range.this.numberTo ? retval : null;
      }
    } );
  }
  
  public Long getNumberFrom()
  {
    return this.numberFrom;
  }
  
  public Long getNumberTo()
  {
    return this.numberTo;
  }
  
  public Long getStep()
  {
    return this.step;
  }
  
  /**
   * Returns an int array with all number between the given ranges
   * 
   * @return
   */
  public int[] toIntArray()
  {
    int[] retvals = null;
    
    int numberFrom = 0;
    int numberTo = 0;
    
    if ( this.numberFrom != null )
    {
      numberFrom = this.numberFrom.intValue();
    }
    if ( this.numberTo != null )
    {
      numberTo = this.numberTo.intValue();
    }
    
    final int step = this.step.intValue();
    final int delta = ( numberTo - numberFrom ) / step;
    retvals = new int[delta + 1];
    for ( int ii = 0; ii <= delta; ii++ )
    {
      retvals[ii] = numberFrom + ii * step;
    }
    return retvals;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Range [numberFrom=" );
    builder.append( this.numberFrom );
    builder.append( ", numberTo=" );
    builder.append( this.numberTo );
    builder.append( ", step=" );
    builder.append( this.step );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.numberFrom == null ) ? 0 : this.numberFrom.hashCode() );
    result = prime * result + ( ( this.numberTo == null ) ? 0 : this.numberTo.hashCode() );
    result = prime * result + ( ( this.step == null ) ? 0 : this.step.hashCode() );
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
    if ( !( obj instanceof Range ) )
    {
      return false;
    }
    Range other = (Range) obj;
    if ( this.numberFrom == null )
    {
      if ( other.numberFrom != null )
      {
        return false;
      }
    }
    else if ( !this.numberFrom.equals( other.numberFrom ) )
    {
      return false;
    }
    if ( this.numberTo == null )
    {
      if ( other.numberTo != null )
      {
        return false;
      }
    }
    else if ( !this.numberTo.equals( other.numberTo ) )
    {
      return false;
    }
    if ( this.step == null )
    {
      if ( other.step != null )
      {
        return false;
      }
    }
    else if ( !this.step.equals( other.step ) )
    {
      return false;
    }
    return true;
  }
  
}
