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
package org.omnaest.utils.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link Average} calculates average values based on a given {@link Collection} of {@link Number}s
 * 
 * @author Omnaest
 * @param <N>
 */
public class Average<N extends Number>
{
  /* ********************************************** Variables ********************************************** */
  private Collection<N> numberCollection = new ArrayList<N>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public Average()
  {
    super();
  }
  
  /**
   * @param numberCollection
   */
  public Average( Collection<N> numberCollection )
  {
    super();
    
    //
    if ( numberCollection != null )
    {
      //
      this.numberCollection.addAll( numberCollection );
    }
  }
  
  /**
   * Calculates the {@link Average} of the stored values
   * 
   * @return
   */
  public BigDecimal calculate()
  {
    //
    BigDecimal retval = null;
    
    //
    BigDecimal sum = BigDecimal.ZERO;
    for ( N number : this.numberCollection )
    {
      BigDecimal currentValue = new BigDecimal( number.toString() );
      sum = sum.add( currentValue );
    }
    
    //
    if ( this.size() > 0 )
    {
      retval = sum.divide( BigDecimal.valueOf( this.size() ), BigDecimal.ROUND_HALF_UP );
    }
    else
    {
      retval = BigDecimal.ZERO;
    }
    
    //
    return retval;
  }
  
  public int size()
  {
    return this.numberCollection.size();
  }
  
  public boolean isEmpty()
  {
    return this.numberCollection.isEmpty();
  }
  
  public boolean add( N e )
  {
    return this.numberCollection.add( e );
  }
  
  public boolean addAll( Collection<? extends N> c )
  {
    return this.numberCollection.addAll( c );
  }
  
  public void clear()
  {
    this.numberCollection.clear();
  }
  
}
