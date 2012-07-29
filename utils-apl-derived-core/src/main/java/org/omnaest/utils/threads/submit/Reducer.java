/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.threads.submit;

import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.BooleanList;

public interface Reducer<T>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  public interface ValueHandler<T, R>
  {
    public R reduce( T value );
  }
  
  public interface ValuesHandler<T, R>
  {
    public R reduce( Iterable<T> values );
  }
  
  public interface BooleansHandler<T> extends ValuesHandler<T, Boolean>
  {
  }
  
  public interface BooleanHandler<T> extends ValueHandler<T, Boolean>
  {
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public List<T> reduceToList();
  
  public Set<T> reduceToSet();
  
  public <R> List<R> reduceToList( ValueHandler<T, R> valueHandler );
  
  public <R> Set<R> reduceToSet( ValueHandler<T, R> valueHandler );
  
  public <R> R reduceToValue( ValuesHandler<T, R> valuesHandler );
  
  public BooleanList reduceToBooleanValueList( BooleanHandler<T> booleanHandler );
  
  public Set<Boolean> reduceToBooleanValueSet( BooleanHandler<T> booleanHandler );
  
  public boolean reduceToBooleanValue( BooleansHandler<T> booleansHandler );
  
  /**
   * Returns true, if all result values are {@link Boolean#TRUE}
   * 
   * @return
   */
  public boolean reduceToBooleanValue();
}
