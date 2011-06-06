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

/**
 * Two arguments {@link Tuple}
 * 
 * @see Tuple
 * @author Omnaest
 */
public class TupleDuad<T1, T2> implements Tuple
{
  /* ********************************************** Variables ********************************************** */

  protected T1 valueFirst  = null;
  protected T2 valueSecond = null;
  
  /* ********************************************** Methods ********************************************** */

  public TupleDuad()
  {
  }
  
  public TupleDuad( T1 valueFirst, T2 valueSecond )
  {
    this.valueFirst = valueFirst;
    this.valueSecond = valueSecond;
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
}
