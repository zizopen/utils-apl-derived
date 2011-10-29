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
package org.omnaest.utils.tuple;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Two arguments {@link Tuple} supporting {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} using the values of
 * the given elements.
 * 
 * @see Tuple
 * @see TupleThree
 * @author Omnaest
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class TupleTwo<T1, T2> implements Tuple
{
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected T1 valueFirst  = null;
  
  @XmlElement
  protected T2 valueSecond = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public TupleTwo()
  {
  }
  
  public TupleTwo( T1 valueFirst, T2 valueSecond )
  {
    this.valueFirst = valueFirst;
    this.valueSecond = valueSecond;
  }
  
  /**
   * Returns the first value of the {@link Tuple}.
   * 
   * @return
   */
  public T1 getValueFirst()
  {
    return this.valueFirst;
  }
  
  /**
   * Sets the first value of the {@link Tuple}.
   * 
   * @param valueFirst
   */
  public void setValueFirst( T1 valueFirst )
  {
    this.valueFirst = valueFirst;
  }
  
  /**
   * Returns the second value of the {@link Tuple}.
   * 
   * @return
   */
  public T2 getValueSecond()
  {
    return this.valueSecond;
  }
  
  /**
   * Sets the second value of the {@link Tuple}.
   * 
   * @param valueSecond
   */
  public void setValueSecond( T2 valueSecond )
  {
    this.valueSecond = valueSecond;
  }
  
  /**
   * Returns a {@link Map} containing an entry based on this {@link Tuple}.
   * 
   * @return
   */
  public Map<T1, T2> asMap()
  {
    //
    Map<T1, T2> retmap = new HashMap<T1, T2>();
    
    //
    retmap.put( this.valueFirst, this.valueSecond );
    
    //
    return retmap;
  }
  
  @Override
  public String toString()
  {
    return "TupleDuad [valueFirst=" + this.valueFirst + ", valueSecond=" + this.valueSecond + "]";
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.valueFirst == null ) ? 0 : this.valueFirst.hashCode() );
    result = prime * result + ( ( this.valueSecond == null ) ? 0 : this.valueSecond.hashCode() );
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
    if ( !( obj instanceof TupleTwo ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    TupleTwo other = (TupleTwo) obj;
    if ( this.valueFirst == null )
    {
      if ( other.valueFirst != null )
      {
        return false;
      }
    }
    else if ( !this.valueFirst.equals( other.valueFirst ) )
    {
      return false;
    }
    if ( this.valueSecond == null )
    {
      if ( other.valueSecond != null )
      {
        return false;
      }
    }
    else if ( !this.valueSecond.equals( other.valueSecond ) )
    {
      return false;
    }
    return true;
  }
  
}
