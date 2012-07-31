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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.omnaest.utils.structure.collection.list.BooleanList;

/**
 * @see Reducer
 * @author Omnaest
 * @param <T>
 */
class ReducerImpl<T> implements Reducer<T>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Collection<T> resultCollection;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see ReducerImpl
   * @param resultCollection
   */
  ReducerImpl( Collection<T> resultCollection )
  {
    this.resultCollection = resultCollection;
  }
  
  @Override
  public List<T> reduceToList()
  {
    return Collections.unmodifiableList( new ArrayList<T>( this.resultCollection ) );
  }
  
  @Override
  public Set<T> reduceToSet()
  {
    return Collections.unmodifiableSet( new LinkedHashSet<T>( this.resultCollection ) );
  }
  
  @Override
  public <R> List<R> reduceToList( Reducer.ValueHandler<T, R> valueHandler )
  {
    final List<R> retlist = new ArrayList<R>();
    if ( valueHandler != null )
    {
      for ( T value : this.resultCollection )
      {
        final R result = valueHandler.reduce( value );
        retlist.add( result );
      }
    }
    return retlist;
  }
  
  @Override
  public <R> Set<R> reduceToSet( Reducer.ValueHandler<T, R> valueHandler )
  {
    final Set<R> retset = new LinkedHashSet<R>();
    if ( valueHandler != null )
    {
      for ( T value : this.resultCollection )
      {
        final R result = valueHandler.reduce( value );
        retset.add( result );
      }
    }
    return retset;
  }
  
  @Override
  public <R> R reduceToValue( Reducer.ValuesHandler<T, R> valuesHandler )
  {
    R retval = null;
    if ( valuesHandler != null )
    {
      retval = valuesHandler.reduce( this.resultCollection );
    }
    return retval;
  }
  
  @Override
  public BooleanList reduceToBooleanValueList( Reducer.BooleanHandler<T> booleanHandler )
  {
    final BooleanList retlist = new BooleanList( this.reduceToList( booleanHandler ) );
    return retlist;
  }
  
  @Override
  public Set<Boolean> reduceToBooleanValueSet( Reducer.BooleanHandler<T> booleanHandler )
  {
    return this.reduceToSet( booleanHandler );
  }
  
  @Override
  public boolean reduceToBooleanValue( Reducer.BooleansHandler<T> booleansHandler )
  {
    return booleansHandler != null && BooleanUtils.isTrue( booleansHandler.reduce( this.resultCollection ) );
  }
  
  @Override
  public boolean reduceToBooleanValue()
  {
    final BooleanHandler<T> booleanHandler = new BooleanHandler<T>()
    {
      @Override
      public Boolean reduce( T value )
      {
        return value instanceof Boolean && ( (Boolean) value ).booleanValue();
      }
    };
    return this.reduceToBooleanValueList( booleanHandler ).containsOnlyTrueValues();
  }
}
