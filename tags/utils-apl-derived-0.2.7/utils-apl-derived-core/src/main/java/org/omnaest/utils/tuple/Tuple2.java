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
 * @see Tuple3
 * @author Omnaest
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Tuple2<T1, T2> implements Tuple
{
  private static final long serialVersionUID = 7098064708499668698L;
  
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected T1              valueFirst       = null;
  
  @XmlElement
  protected T2              valueSecond      = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see Tuple2
   */
  public Tuple2()
  {
  }
  
  /**
   * @see Tuple2
   * @param valueFirst
   * @param valueSecond
   */
  public Tuple2( T1 valueFirst, T2 valueSecond )
  {
    this.valueFirst = valueFirst;
    this.valueSecond = valueSecond;
  }
  
  /**
   * Creates a new {@link Tuple2} instance based on the values of an already existing instance
   * 
   * @see Tuple2
   * @param tuple2
   */
  public Tuple2( Tuple2<T1, T2> tuple2 )
  {
    if ( tuple2 != null )
    {
      this.valueFirst = tuple2.valueFirst;
      this.valueSecond = tuple2.valueSecond;
    }
  }
  
  /**
   * Returns a new {@link Tuple2} instance with inverted first and second value
   * 
   * @return {@link Tuple2}
   */
  public Tuple2<T2, T1> newInvertedInstance()
  {
    return new Tuple2<T2, T1>( this.valueSecond, this.valueFirst );
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
    retmap.put( this.valueFirst, this.valueSecond );
    return retmap;
  }
  
  @Override
  public String toString()
  {
    return "[" + this.valueFirst + "," + this.valueSecond + "]";
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
    if ( !( obj instanceof Tuple2 ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    Tuple2 other = (Tuple2) obj;
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
