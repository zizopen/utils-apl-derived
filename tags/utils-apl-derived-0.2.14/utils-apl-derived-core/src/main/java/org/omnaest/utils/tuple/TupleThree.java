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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Container {@link Tuple} holding three data instances.
 * 
 * @see Tuple
 * @see TupleTwo
 * @author Omnaest
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @deprecated use {@link Tuple3} instead
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class TupleThree<T1, T2, T3> implements Tuple
{
  private static final long serialVersionUID = -5561006980478608055L;
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected T1              valueFirst       = null;
  @XmlElement
  protected T2              valueSecond      = null;
  @XmlElement
  protected T3              valueThird       = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public TupleThree()
  {
  }
  
  public TupleThree( T1 valueFirst, T2 valueSecond, T3 valueThird )
  {
    super();
    this.valueFirst = valueFirst;
    this.valueSecond = valueSecond;
    this.valueThird = valueThird;
  }
  
  public T1 getValueFirst()
  {
    return this.valueFirst;
  }
  
  public void setValueFirst( T1 valueFirst )
  {
    this.valueFirst = valueFirst;
  }
  
  public T2 getValueSecond()
  {
    return this.valueSecond;
  }
  
  public void setValueSecond( T2 valueSecond )
  {
    this.valueSecond = valueSecond;
  }
  
  public T3 getValueThird()
  {
    return this.valueThird;
  }
  
  public void setValueThird( T3 valueThird )
  {
    this.valueThird = valueThird;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "TupleThree [valueFirst=" );
    builder.append( this.valueFirst );
    builder.append( ", valueSecond=" );
    builder.append( this.valueSecond );
    builder.append( ", valueThird=" );
    builder.append( this.valueThird );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.valueFirst == null ) ? 0 : this.valueFirst.hashCode() );
    result = prime * result + ( ( this.valueSecond == null ) ? 0 : this.valueSecond.hashCode() );
    result = prime * result + ( ( this.valueThird == null ) ? 0 : this.valueThird.hashCode() );
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
    if ( !( obj instanceof TupleThree ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    TupleThree other = (TupleThree) obj;
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
    if ( this.valueThird == null )
    {
      if ( other.valueThird != null )
      {
        return false;
      }
    }
    else if ( !this.valueThird.equals( other.valueThird ) )
    {
      return false;
    }
    return true;
  }
  
}
