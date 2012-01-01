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
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.iterator.ElementStreamToIteratorAdapter;

/**
 * Represents a {@link Range} for {@link Long} values which can be used as {@link Iterable}. The number limits are always
 * included.
 * 
 * @author Omnaest
 */
public class Range implements Iterable<Long>
{
  /* ********************************************** Variables ********************************************** */
  private Long numberFrom = null;
  private Long numberTo   = null;
  private Long step       = 1l;
  
  /* ********************************************** Methods ********************************************** */
  
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
  }
  
  /**
   * Allows to specify a {@link Range} with a given {@link String} expression.<br>
   * <br>
   * The expression format is:<br>
   * 
   * <pre>
   * new Range( &quot;1-5&quot; );
   * </pre>
   * 
   * @see Range
   * @param rangeExpression
   */
  public Range( String rangeExpression )
  {
    super();
    
    Assert.isNotNull( rangeExpression );
    String[] tokens = rangeExpression.split( "-" );
    Assert.isTrue( tokens.length == 2 );
    StringUtils.isNumeric( tokens[0] );
    StringUtils.isNumeric( tokens[1] );
    
    this.numberFrom = Long.valueOf( tokens[0] );
    this.numberTo = Long.valueOf( tokens[1] );
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
  
}
